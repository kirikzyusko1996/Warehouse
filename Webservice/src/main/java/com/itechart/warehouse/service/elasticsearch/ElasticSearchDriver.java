package com.itechart.warehouse.service.elasticsearch;

import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.itechart.warehouse.util.Host.host;
import static com.itechart.warehouse.util.Host.port;

/**
 * Created by Lenovo on 23.05.2017.
 * Class, which represent myself DAO-layer
 * for working with elastic search framework to Driver-entity
 */
public class ElasticSearchDriver {
    private static TransportClient client;
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchDriver.class);
    private static final String TYPE = "driver";
    private static final String FIELD = "fullName";

    static {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            logger.error("Message: " + e.getMessage() + "\nCause: " + e.getCause());
        }
    }

    /**
     * @return String - id of inserted record (driver)
     **/
    public String save(Driver driver, Long id_company) {
        logger.info("Saving driver with id: {}, for company with id: {}", driver.getId(), id_company);
        String index = id_company.toString();
        String json = ToJSON.toJSON(driver);
        logger.info("JSON object of driver: {}", json);
        IndexResponse response = client.prepareIndex(index, TYPE)
                .setSource(json)
                .get();
        return response.getId();
    }

    /**
     * @return String - status of commited operation
     **/
    public String delete(Driver driver, Long id_company) {
        String index = id_company.toString();
        String id_elastic = findForCRUD(driver, id_company);
        if(id_elastic.equals("")) {
            return "NOT_FOUND";//todo remake with exception
        }
        DeleteResponse response = client.prepareDelete(
                index, TYPE, id_elastic).get();
        return response.status().toString();
    }

    /**
     * @return String - id of inserted record (transport company)
     */
    public String edit(Driver _old, Driver _new, Long id_company) {
        delete(_old, id_company);
        return save(_new, id_company);
    }

    /**
     * Dispatcher method
     * */
    public List<SimilarityWrapper<Driver>> search(Driver driver, Long id_company) {
        List<SimilarityWrapper<Driver>> similarityWrapperList = fuzzyMatchSearch(driver, id_company);
        if(similarityWrapperList.size()!=0) {
            logger.info("fuzzy match result");
            return similarityWrapperList;
        }
        else {
            logger.info("sub match result");
            return subMatchSearch(driver, id_company);
        }
    }

    private List<SimilarityWrapper<Driver>> fuzzyMatchSearch(Driver driver, Long id_company){
        String index = id_company.toString();
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"query\": {\n" +
                        "    \"multi_match\": {\n" +
                        "      \"fields\":  [ \""+FIELD+"\"],\n" +
                        "      \"query\":     \""
                        +driver.getFullName()+//todo injection?
                        "\",\n" +
                        "      \"fuzziness\": \"AUTO\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setRequest(new SearchRequest(index).types(TYPE))
                //because name not unique-field between company and driver
                .get()
                .getResponse();

        return parseDriverData(sr.getHits().getHits());
    }

    private List<SimilarityWrapper<Driver>> subMatchSearch(Driver driver, Long id_company){
        String index = id_company.toString();
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"query\": {\n" +
                        "    \"query_string\": {\n" +
                        "      \"fields\":  [ \""+FIELD+"\"],\n" +
                        "      \"query\":     \"*"
                        +driver.getFullName()+//todo injection?
                        "*\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setRequest(new SearchRequest(index).types(TYPE))
                //because name not unique-field between company and driver
                .get()
                .getResponse();

        return parseDriverData(sr.getHits().getHits());
    }

    private List<SimilarityWrapper<Driver>> parseDriverData(SearchHit[] searchHits){
        List<SimilarityWrapper<Driver>> list = new ArrayList<>();
        for(SearchHit searchHit : searchHits){
            SimilarityWrapper<Driver> similarityWrapper = new SimilarityWrapper<>();
            Map map = searchHit.getSource();
            Driver driver = new Driver();//it's parse data, but worst
            driver.setId(Long.valueOf((Integer)map.get("id")));
            driver.setFullName((String) map.get("fullName"));
            driver.setPassportNumber((String) map.get("passportNumber"));
            driver.setIssuedBy((String) map.get("issuedBy"));
            driver.setCountryCode((String) map.get("countryCode"));
            driver.setIssueDate((Date) map.get("issueDate"));
            similarityWrapper.setOjbect(driver);
            similarityWrapper.setSimilarity(searchHit.getScore());
            list.add(similarityWrapper);
        }
        return list;
    }

    private String findForCRUD(Driver driver, Long id_company){
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(
                FIELD, driver.getFullName()
        );
        String index = id_company.toString();
        SearchResponse response = client.prepareSearch(index)
                .setTypes(TYPE)
                .setQuery(queryBuilder)// Query
                .setFrom(0).setSize(5)
                .get();
        SearchHit hits[] = response.getHits().getHits();
        if(hits.length!=0) {
            return hits[0].getId();
        }
        else {
            return "";
        }
    }
}
