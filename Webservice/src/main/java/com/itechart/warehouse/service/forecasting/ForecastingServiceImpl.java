package com.itechart.warehouse.service.forecasting;

import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.StrategyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.Strategy;
import com.itechart.warehouse.service.services.StrategyService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.hibernate.criterion.DetachedCriteria;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This example is intended to be a simple CSV classifier that separates the training data
 * from the test data for the classification of situation in marketing sphere. It would be suitable as a beginner's
 * example because not only does it load CSV data into the network, it also shows how to extract the
 * data and display the results of the classification, as well as a simple method to map the labels
 * from the testing data into the results.
 *
 * @author Kiryl Ziusko
 */
//todo: добавить storage_type
@Service
public class ForecastingServiceImpl implements ForecastingService {
    private static final String PATH_TO_MODEL = "warehouse_neural_network.zip";
    private static final String PATH_TO_TRAIN_DATA = "/DataExamplesWarehouse/data.csv";
    private static final String PATH_TO_TEST_DATA = "/DataExamplesWarehouse/warehouse.csv";

    private static Logger log = LoggerFactory.getLogger(ForecastingServiceImpl.class);
    private StrategyService strategyService;
    private GoodsDAO goodsDAO;
    private StrategyDAO strategyDAO;
    //private static Map<Integer,String> category = readEnumCSV("/DataExamplesWarehouse/category.csv");
    private Map<Integer, String> classifiers = readEnumCSV("/DataExamplesWarehouse/classifiers.csv");

    private MultiLayerNetwork network;

    @PostConstruct
    public void init() {
        try {
            network = load();
        } catch (IOException e) { // file not exist -> train model
            log.error(e.getMessage());
            try {
                train();
                load();
            } catch (GenericDAOException | IOException | InterruptedException e1) {
                log.error(e1.getMessage());
            }
        }
    }

    @Autowired
    public void setStrategyDAO(StrategyDAO strategyDAO) {
        this.strategyDAO = strategyDAO;
    }

    @Autowired
    public void setGoodsDAO(GoodsDAO goodsDAO) {
        this.goodsDAO = goodsDAO;
    }

    @Autowired
    public void setStrategyService(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    public Strategy getStrategyByGoods(Long idGoods) {
        try {
            Goods goods = goodsDAO.getById(idGoods);
            long strategyId = getStrategyId(goods);
            Optional<Strategy> strategy = strategyDAO.findById(strategyId);
            return strategy.orElse(null);
        } catch (GenericDAOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public void initClassifiers() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Strategy.class);
        List<Strategy> strategies = strategyDAO.findAll(criteria, -1, -1);
        classifiers.clear();
        for(Strategy strategy : strategies) {
            classifiers.put(Math.toIntExact(strategy.getIdStrategy()), strategy.getName());
        }
    }

    @Transactional(readOnly = true)
    public long getCountKeepingDays(Goods goods) {
        Goods goodsWithInvoices = goodsDAO.getGoodsByIdWithInvoices(goods.getId());
        long diff = goodsWithInvoices.getOutgoingInvoice().getIssueDate().getTime() - goodsWithInvoices.getIncomingInvoice().getIssueDate().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private int listGoodsToCSV() throws GenericDAOException, IOException {
        List<Goods> goodsList = strategyService.getListForLearning();
        initClassifiers();
        File f = new ClassPathResource(PATH_TO_TRAIN_DATA).getFile();

        try (FileWriter writer = new FileWriter(f)) {
            for (Goods goods : goodsList) {
                if (goods.getOutgoingInvoice() == null) {
                    continue;
                }
                String recordRow = new LearnUnit(goods).toLearnRecord();
                writer.write(recordRow);
                writer.write("\n");
            }
        }

        return goodsList.size();
    }

    @Override
    public void train() throws GenericDAOException, IOException, InterruptedException {
        int batchSizeTraining = listGoodsToCSV();
        //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        int labelIndex = 3;     //5 values in each row of the animals.csv CSV: 4 input features followed by an integer label (class) index. Labels are the 5th value (index 4) in each row
        // +1, т.к. 0-ая стратегия нигде не исполльзуется (или id в БД нумеровать с нуля по дефолту)
        int numClasses = strategyService.getQuantityStrategies() + 1;     //3 classes (types of animals) in the animals data set. Classes have integer values 0, 1 or 2
        int iterations = 1000;
        int epochs = 15;
        long seed = 6;
        //test data
        int batchSizeTest = 10;

        DataSet testData = readCSVDataset(PATH_TO_TEST_DATA,
                batchSizeTest, labelIndex, numClasses);
        //int batchSizeTraining = 17;    //Iris data set: 150 examples total. We are loading all of them into one DataSet (not recommended for large data sets)
        DataSet trainingData = readCSVDataset(PATH_TO_TRAIN_DATA,
                batchSizeTraining, labelIndex, numClasses);

        // make the data model for records prior to normalization, because it
        // changes the data.
        Map<Integer, Map<String, Object>> animals = makeStrategyForTesting(testData);

        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set

        try {
            log.info("Build model....");
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(seed)
                    .iterations(iterations)
                    .activation(Activation.TANH)
                    .weightInit(WeightInit.XAVIER)
                    //.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                    //.updater(Updater.NESTEROVS)
                    .learningRate(0.1)
                    .regularization(true).l2(1e-4)
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(labelIndex).nOut(4).build())
                    .layer(1, new DenseLayer.Builder().nIn(4).nOut(4).build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX).nIn(4).nOut(numClasses).build())
                    .backprop(true).pretrain(false)
                    .build();

            //run the model
            MultiLayerNetwork model = new MultiLayerNetwork(conf);
            model.init();
            model.setListeners(new ScoreIterationListener(100));

            for (int i = 0; i < epochs; i++) {
                model.fit(trainingData);
            }

            reload(model);

            INDArray output = model.output(testData.getFeatureMatrix());

            //evaluate the model on the test set
            Evaluation eval = new Evaluation(numClasses);
            eval.eval(testData.getLabels(), output);
            log.info(eval.stats());

            setFittedClassifiers(output, animals);
            logAnimals(animals);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void save(MultiLayerNetwork model) throws IOException {
        log.info("Model was saved...");
        File locationToSave = new File(PATH_TO_MODEL);
        // ModelSerializer needs modelname, Location, saveUpdater
        ModelSerializer.writeModel(model, locationToSave, true);
    }

    private MultiLayerNetwork load() throws IOException {
        log.info("Model was loaded...");
        File locationToSave = new File(PATH_TO_MODEL);
        return ModelSerializer.restoreMultiLayerNetwork(locationToSave);
    }

    private void reload(MultiLayerNetwork model) throws IOException {
        save(model);
        load();
    }

    private static void logAnimals(Map<Integer, Map<String, Object>> animals) {
        for (Map<String, Object> a : animals.values()) {
            String view = a.toString();
            log.info(view);
        }
    }

    private void setFittedClassifiers(INDArray output, Map<Integer, Map<String, Object>> animals) {
        for (int i = 0; i < output.rows(); i++) {
            log.info(output.slice(i) + "| Max value is: " + maxIndex(getFloatArrayFromSlice(output.slice(i))));
            // set the classification from the fitted results
            animals.get(i).put("classifier",
                    classifiers.get(maxIndex(getFloatArrayFromSlice(output.slice(i)))));
        }
    }

    private long getStrategyId(Goods goods) {
        DataNormalization normalizer = new NormalizerStandardize();
        INDArray data = new LearnUnit(goods).toPredictionRecord();
        DataSet testDataFile = null;
        try {
            testDataFile = readCSVDataset(PATH_TO_TRAIN_DATA,
                    10, 3, 8);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        } 
        DataSet testData = new DataSet(data, data);
        
        normalizer.fit(testDataFile);
        normalizer.transform(testData);
        INDArray output = network.output(testData.getFeatureMatrix());

        return maxIndex(getFloatArrayFromSlice(output.slice(0)));
    }

    /**
     * This method is to show how to convert the INDArray to a float array. This is to
     * provide some more examples on how to convert INDArray to types that are more java
     * centric.
     *
     * @param rowSlice INDArray - contains slice of learn record
     * @return Float array - converted INDArray to float[]
     */
    private float[] getFloatArrayFromSlice(INDArray rowSlice) {
        float[] result = new float[rowSlice.columns()];
        for (int i = 0; i < rowSlice.columns(); i++) {
            result[i] = rowSlice.getFloat(i);
        }
        return result;
    }

    /**
     * find the maximum item index. This is used when the data is fitted and we
     * want to determine which class to assign the test row to
     *
     * @param values float[] - sample of data
     * @return Integer - position of maximal value
     */
    private int maxIndex(float[] values) {
        int maxIndex = 0;
        for (int i = 1; i < values.length; i++) {
            float newNumber = values[i];
            if ((newNumber > values[maxIndex])) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * take the dataset loaded for the matric and make the record model out of it so
     * we can correlate the fitted classifier to the record.
     *
     * @param testData DataSet source of initial test data
     * @return JSON string views of data
     */
    private Map<Integer, Map<String, Object>> makeStrategyForTesting(DataSet testData) {
        Map<Integer, Map<String, Object>> strategies = new HashMap<>();

        INDArray features = testData.getFeatureMatrix();
        LearnUnit learnUnit = new LearnUnit();
        for (int i = 0; i < features.rows(); i++) {
            INDArray slice = features.slice(i);
            Map<String, Object> strategy = learnUnit.toView(slice);

            strategies.put(i, strategy);
        }
        return strategies;

    }

    private Map<Integer, String> readEnumCSV(String csvFileClasspath) {
        try {
            List<String> lines = IOUtils.readLines(new ClassPathResource(csvFileClasspath).getInputStream());
            Map<Integer, String> enums = new HashMap<>();
            for (String line : lines) {
                String[] parts = line.split(",");
                enums.put(Integer.parseInt(parts[0]), parts[1]);
            }
            return enums;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    /**
     * used for testing and training
     *
     * @param csvFileClasspath String - path in filesystem to csv file with data
     * @param batchSize Integer
     * @param labelIndex Integer
     * @param numClasses Integer - count of decision, that neural network can predict
     * @return DataSet data from csv file
     * @throws IOException when file not found
     * @throws InterruptedException when file cannot read
     */
    private DataSet readCSVDataset(String csvFileClasspath, int batchSize, int labelIndex, int numClasses)
            throws IOException, InterruptedException {
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new ClassPathResource(csvFileClasspath).getFile()));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr, batchSize, labelIndex, numClasses);
        return iterator.next();
    }

    @Data
    @NoArgsConstructor
    private class LearnUnit {
        private Float price;
        private Long countKeepingDays;
        private Long quantity;
        private Long strategy;

        LearnUnit(Goods goods) {
            this.price = goods.getPrice().floatValue();
            this.countKeepingDays = ForecastingServiceImpl.this.getCountKeepingDays(goods);
            this.quantity = goods.getQuantity().longValue();
            Optional<Strategy> optionalStrategy = Optional.ofNullable(goods.getStrategy());
            this.strategy = optionalStrategy.isPresent() ? optionalStrategy.get().getIdStrategy() : null;
        }

        String toLearnRecord() {
            List<String> data = new ArrayList<>();
            data.add(String.valueOf(price));
            data.add(String.valueOf(countKeepingDays));
            //data.add(goods.)todo: category of goods
            data.add(String.valueOf(quantity));
            data.add(String.valueOf(strategy));

            return data.stream().collect(Collectors.joining(","));
        }

        INDArray toPredictionRecord() {
            INDArray data = Nd4j.zeros(2, 3);
            data.putScalar(0, 0, price);           //Set value at row i, column i1 to value x
            data.putScalar(0, 1, countKeepingDays);
            data.putScalar(0, 1, quantity);

            return data;
        }

        Map<String, Object> toView(INDArray slice) {
            Map<String, Object> strategyView = new HashMap<>();

            //set the attributes
            strategyView.put("price", slice.getFloat(0));
            strategyView.put("releaseDays", slice.getInt(1));
            strategyView.put("quantity", slice.getInt(2));

            return strategyView;
        }
    }
}
