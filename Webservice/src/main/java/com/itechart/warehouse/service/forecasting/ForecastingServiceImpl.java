package com.itechart.warehouse.service.forecasting;

import com.itechart.warehouse.dao.GoodsDAO;
import com.itechart.warehouse.dao.InvoiceDAO;
import com.itechart.warehouse.dao.StrategyDAO;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.Strategy;
import com.itechart.warehouse.service.services.StrategyService;
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
import org.hibernate.criterion.Restrictions;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String PATH_TO_TEST_DATA = "/DataExamplesWarehouse/warehouse_test2.csv";

    private static Logger log = LoggerFactory.getLogger(ForecastingServiceImpl.class);
    private StrategyService strategyService;
    private GoodsDAO goodsDAO;
    private StrategyDAO strategyDAO;
    //private static Map<Integer,String> category = readEnumCSV("/DataExamplesWarehouse/category.csv");
    private static Map<Integer, String> classifiers = readEnumCSV("/DataExamplesWarehouse/classifiers.csv");

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

    public Strategy getStrategyByGoods() {
        return new Strategy(1l, "Скидка", "Не более 5%");
    }

    @Transactional(readOnly = true)
    public void initClassifiers() throws GenericDAOException {
        DetachedCriteria criteria = DetachedCriteria.forClass(Strategy.class);
        List<Strategy> strategies = strategyDAO.findAll(criteria, -1, -1);
        classifiers.clear();
        for(Strategy strategy: strategies) {
            classifiers.put(Math.toIntExact(strategy.getIdStrategy()), strategy.getName());
        }
    }

    @Transactional(readOnly = true)
    private long getCountKeepingDays(Goods goods) throws GenericDAOException {
        Goods goodsWithInvoices = goodsDAO.getGoodsByIdWithInvoices(goods.getId());
        long diff = goodsWithInvoices.getOutgoingInvoice().getIssueDate().getTime() - goodsWithInvoices.getIncomingInvoice().getIssueDate().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private int listGoodsToCSV() throws GenericDAOException, IOException {
        List<Goods> goodsList = strategyService.getListForLearning();
        initClassifiers();//todo: be here or replace to train method?
        File f = new ClassPathResource(PATH_TO_TRAIN_DATA).getFile();

        FileWriter writer = new FileWriter(f);
        for (Goods goods : goodsList) {
            if (goods.getOutgoingInvoice() == null) {
                continue;
            }
            List<String> data = new ArrayList<>();
            data.add(String.valueOf(goods.getPrice()));
            data.add(String.valueOf(getCountKeepingDays(goods)));
            //data.add(goods.)todo: category of goods
            data.add(String.valueOf(goods.getQuantity().intValue()));
            data.add(String.valueOf(goods.getStrategy().getIdStrategy()));

            String collect = data.stream().collect(Collectors.joining(","));

            writer.write(collect);
            writer.write("\n");
        }
        writer.close();
        return goodsList == null ? 0 : goodsList.size();
    }

    @Override
    public void train() throws GenericDAOException, IOException, InterruptedException {
        int batchSizeTraining = listGoodsToCSV();

//Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        int labelIndex = 3;     //5 values in each row of the animals.csv CSV: 4 input features followed by an integer label (class) index. Labels are the 5th value (index 4) in each row
        //todo: +1, т.к. 0-ая стратегия нигде не исполльзуется (или id в БД нумеровать с нуля по дефолту)
        int numClasses = strategyService.getQuantityStrategies()+1;     //3 classes (types of animals) in the animals data set. Classes have integer values 0, 1 or 2

        final int numInputs = labelIndex;
        int outputNum = numClasses;
        int iterations = 1000;
        int epochs = 15;
        long seed = 6;

        //test data
        int batchSizeTest = 10;
        DataSet testData = readCSVDataset("/DataExamplesWarehouse/warehouse.csv",
                batchSizeTest, labelIndex, numClasses);
        //int batchSizeTraining = 17;    //Iris data set: 150 examples total. We are loading all of them into one DataSet (not recommended for large data sets)
        DataSet trainingData = readCSVDataset(PATH_TO_TRAIN_DATA,
                batchSizeTraining, labelIndex, numClasses);

        // make the data model for records prior to normalization, because it
        // changes the data.
        Map<Integer, Map<String, Object>> animals = makeAnimalsForTesting(testData);

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
                    .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(4).build())
                    .layer(1, new DenseLayer.Builder().nIn(4).nOut(4).build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX).nIn(4).nOut(outputNum).build())
                    .backprop(true).pretrain(false)
                    .build();

            //run the model
            MultiLayerNetwork model = new MultiLayerNetwork(conf);
            model.init();
            model.setListeners(new ScoreIterationListener(100));

            for (int i = 0; i < epochs; i++) {
                model.fit(trainingData);
            }

            INDArray output = model.output(testData.getFeatureMatrix());

            //evaluate the model on the test set
            Evaluation eval = new Evaluation(numClasses);
            eval.eval(testData.getLabels(), output);
            log.info(eval.stats());

            setFittedClassifiers(output, animals);
            logAnimals(animals);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save(MultiLayerNetwork model) throws IOException {
        log.info("Model was saved...");
        File locationToSave = new File(PATH_TO_MODEL);
        boolean saveUpdater = true;
        // ModelSerializer needs modelname, Location, saveUpdater
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
    }

    private MultiLayerNetwork load() throws IOException {
        log.info("Model was loaded...");
        File locationToSave = new File(PATH_TO_MODEL);
        return ModelSerializer.restoreMultiLayerNetwork(locationToSave);
    }

    private void reload_restart() {
    }

    /*public static void main(String[] args) throws Exception {
        //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        int labelIndex = 3;     //5 values in each row of the animals.csv CSV: 4 input features followed by an integer label (class) index. Labels are the 5th value (index 4) in each row
        int numClasses = 7;     //3 classes (types of animals) in the animals data set. Classes have integer values 0, 1 or 2

        final int numInputs = labelIndex;
        int outputNum = numClasses;
        int iterations = 1000;
        int epochs = 15;
        long seed = 6;

        //test data
        int batchSizeTest = 10;
        DataSet testData = readCSVDataset("/DataExamplesWarehouse/warehouse.csv",
            batchSizeTest, labelIndex, numClasses);
        int batchSizeTraining = 17;    //Iris data set: 150 examples total. We are loading all of them into one DataSet (not recommended for large data sets)
        DataSet trainingData = readCSVDataset(
            "/DataExamplesWarehouse/warehouse_train.csv",
            batchSizeTraining, labelIndex, numClasses);

        // make the data model for records prior to normalization, because it
        // changes the data.
        Map<Integer, Map<String, Object>> animals = makeAnimalsForTesting(testData);

        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set

        try {
            File locationToSave = new File("warehouse_neural_network.zip");
            if(!locationToSave.exists()) {
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
                    .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(4).build())
                    .layer(1, new DenseLayer.Builder().nIn(4).nOut(4).build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX).nIn(4).nOut(outputNum).build())
                    .backprop(true).pretrain(false)
                    .build();

                //run the model
                MultiLayerNetwork model = new MultiLayerNetwork(conf);
                model.init();
                model.setListeners(new ScoreIterationListener(100));

                for (int i = 0; i < epochs; i++) {
                    model.fit(trainingData);
                }


                *//*SAVE MODEL*//*
                // boolean save Updater
                boolean saveUpdater = true;
                // ModelSerializer needs modelname, Location, saveUpdater
                ModelSerializer.writeModel(model,locationToSave,saveUpdater);


                //evaluate the model on the test set
                Evaluation eval = new Evaluation(numClasses);
                INDArray output = model.output(testData.getFeatureMatrix());

                eval.eval(testData.getLabels(), output);
                log.info(eval.stats());

                setFittedClassifiers(output, animals);
                logAnimals(animals);




                log.info("Model was loaded...");
                MultiLayerNetwork restored = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
                model.getLabels();

                output = restored.output(testData.getFeatureMatrix());

                //evaluate the model on the test set
                eval = new Evaluation(numClasses);
                eval.eval(testData.getLabels(), output);
                log.info(eval.stats());

                setFittedClassifiers(output, animals);
                logAnimals(animals);

                System.out.println("Saved and loaded parameters are equal:      " + model.params().equals(restored.params()));
                System.out.println("Saved and loaded configurations are equal:  " + model.getLayerWiseConfigurations().equals(restored.getLayerWiseConfigurations()));

            } else { //load neural network
                log.info("Model was loaded...");
                MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
                model.getLabels();

                INDArray output = model.output(testData.getFeatureMatrix());

                //evaluate the model on the test set
                Evaluation eval = new Evaluation(numClasses);
                eval.eval(testData.getLabels(), output);
                log.info(eval.stats());

                setFittedClassifiers(output, animals);
                logAnimals(animals);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }
*/

    public static void logAnimals(Map<Integer, Map<String, Object>> animals) {
        for (Map<String, Object> a : animals.values())
            log.info(a.toString());
    }

    public static void setFittedClassifiers(INDArray output, Map<Integer, Map<String, Object>> animals) {
        for (int i = 0; i < output.rows(); i++) {
            System.out.println(output.slice(i) + "| Max value is: " + maxIndex(getFloatArrayFromSlice(output.slice(i))));
            // set the classification from the fitted results
            animals.get(i).put("classifier",
                    classifiers.get(maxIndex(getFloatArrayFromSlice(output.slice(i)))));
        }
    }

    /**
     * This method is to show how to convert the INDArray to a float array. This is to
     * provide some more examples on how to convert INDArray to types that are more java
     * centric.
     *
     * @param rowSlice
     * @return
     */
    public static float[] getFloatArrayFromSlice(INDArray rowSlice) {
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
     * @param values
     * @return
     */
    public static int maxIndex(float[] values) {
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
     * @param testData
     * @return
     */
    public static Map<Integer, Map<String, Object>> makeAnimalsForTesting(DataSet testData) {
        Map<Integer, Map<String, Object>> animals = new HashMap<>();

        INDArray features = testData.getFeatureMatrix();
        for (int i = 0; i < features.rows(); i++) {
            INDArray slice = features.slice(i);
            Map<String, Object> animal = new HashMap();

            //set the attributes
            animal.put("price", slice.getFloat(0));
            animal.put("releaseDays", slice.getInt(1));
            //animal.put("category", category.get(slice.getInt(2)));
            animal.put("quantity", slice.getInt(2));

            animals.put(i, animal);
        }
        return animals;

    }

    public static Map<Integer, String> readEnumCSV(String csvFileClasspath) {
        try {
            List<String> lines = IOUtils.readLines(new ClassPathResource(csvFileClasspath).getInputStream());
            Map<Integer, String> enums = new HashMap<>();
            for (String line : lines) {
                String[] parts = line.split(",");
                enums.put(Integer.parseInt(parts[0]), parts[1]);
            }
            return enums;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * used for testing and training
     *
     * @param csvFileClasspath
     * @param batchSize
     * @param labelIndex
     * @param numClasses
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static DataSet readCSVDataset(
            String csvFileClasspath, int batchSize, int labelIndex, int numClasses)
            throws IOException, InterruptedException {

        RecordReader rr = new CSVRecordReader();
        System.out.println(new ClassPathResource(csvFileClasspath).getFile());
        rr.initialize(new FileSplit(new ClassPathResource(csvFileClasspath).getFile()));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr, batchSize, labelIndex, numClasses);
        return iterator.next();
    }
}
