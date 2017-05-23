package com.itechart.warehouse.service.elasticsearch;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.itechart.warehouse.util.Host.host;
import static com.itechart.warehouse.util.Host.port;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by Lenovo on 19.05.2017.
 * Class, which represent myself DAO-layer
 * for working with elastic search framework
 * to TransportCompany entity
 */
public class ElasticSearchTransportCompany {
    private static TransportClient client;
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchTransportCompany.class);
    private static final String TYPE_COMPANY = "company";
    private static final String TYPE_DRIVER = "driver";
    private static final String FIELD = "name";

    static {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            logger.error("Message: " + e.getMessage() + "\nCause: " + e.getCause());
        }
    }

    /**
     * @return String - id of inserted record (transport company)
     */
    public String save(TransportCompany transportCompany) {
        logger.info("Saving transport company with id: {}", transportCompany.getId());
        String index = transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString();//todo or id, if nounique
        IndexResponse response = client.prepareIndex(index, TYPE_COMPANY)
                .setSource(ToJSON.toJSON(transportCompany))
                .get();
        return response.getId();
    }

    /**
     * @return String - status of commited operation
     **/
    public String delete(TransportCompany transportCompany) {
        String index = transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString();
        String id_elastic = findForCRUD(transportCompany);
        if(id_elastic.equals("")) {
            return "NOT_FOUND";//todo remake with exception
        }
        DeleteResponse response = client.prepareDelete(
                index, TYPE_COMPANY, id_elastic).get();
        return response.status().toString();
    }

    /**
     * @return String - id of inserted record (transport company)
     */
    public String edit(TransportCompany _old, TransportCompany _new) {
        delete(_old);
        return save(_new);
    }

    public List<SimilarityWrapper<TransportCompany>> search(TransportCompany transportCompany) {
        List<SimilarityWrapper<TransportCompany>> similarityWrapperList = fuzzyMatchSearch(transportCompany);
        if(similarityWrapperList.size()!=0) {
            logger.info("fuzzy match result");
            return similarityWrapperList;
        }
        else {
            logger.info("sub match result");
            return subMatchSearch(transportCompany);
        }
    }

    private List<SimilarityWrapper<TransportCompany>> fuzzyMatchSearch(TransportCompany transportCompany){
        String index = transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString();
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"query\": {\n" +
                        "    \"multi_match\": {\n" +
                        "      \"fields\":  [ \""+FIELD+"\"],\n" +
                        "      \"query\":     \""
                                +transportCompany.getName()+//todo injection?
                        "\",\n" +
                        "      \"fuzziness\": \"AUTO\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setRequest(new SearchRequest(index).types(TYPE_COMPANY))
                //because name not unique-field between company and driver
                .get()
                .getResponse();

        return parseTransportCompanyData(sr.getHits().getHits());
    }

    private List<SimilarityWrapper<TransportCompany>> subMatchSearch(TransportCompany transportCompany){
        String index = transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString();
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"query\": {\n" +
                        "    \"query_string\": {\n" +
                        "      \"fields\":  [ \""+FIELD+"\"],\n" +
                        "      \"query\":     \"*"
                        +transportCompany.getName().toLowerCase()+//todo injection?
                        "*\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setRequest(new SearchRequest(index).types(TYPE_COMPANY))
                //because name not unique-field between company and driver
                .get()
                .getResponse();
        System.out.println("{\n" +
                "  \"query\": {\n" +
                "    \"query_string\": {\n" +
                "      \"fields\":  [ \""+FIELD+"\"],\n" +
                "      \"query\":     \"*"
                +transportCompany.getName().toLowerCase()+//todo injection?
                "*\"\n" +
                "    }\n" +
                "  }\n" +
                "}");

        return parseTransportCompanyData(sr.getHits().getHits());
    }

    private List<SimilarityWrapper<TransportCompany>> parseTransportCompanyData(SearchHit searchHits[]){
        List<SimilarityWrapper<TransportCompany>> list = new ArrayList<>();
        for(SearchHit searchHit : searchHits){
            SimilarityWrapper<TransportCompany> similarityWrapper = new SimilarityWrapper<>();
            Map map = searchHit.getSource();
            TransportCompany tr = new TransportCompany();//it's parse data, but worst
            tr.setId(Long.valueOf((Integer)map.get("id")));
            tr.setName((String) map.get("name"));
            tr.setTrusted((Boolean) map.get("trusted"));
            similarityWrapper.setOjbect(tr);
            similarityWrapper.setSimilarity(searchHit.getScore());
            list.add(similarityWrapper);
        }
        return list;
    }

    private String findForCRUD(TransportCompany transportCompany){
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(
                FIELD, transportCompany.getName()
        );
        String index = transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString();
        SearchResponse response = client.prepareSearch(index)
                .setTypes(TYPE_COMPANY)
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