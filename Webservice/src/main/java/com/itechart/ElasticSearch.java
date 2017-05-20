package com.itechart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.service.impl.StorageCellServiceImpl;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.ingest.TemplateService;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.MustacheScriptEngineService;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.itechart.warehouse.util.Host.host;
import static com.itechart.warehouse.util.Host.port;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by Lenovo on 19.05.2017.
 */
public class ElasticSearch {
    private static TransportClient client;
    private static Logger logger = LoggerFactory.getLogger(ElasticSearch.class);

    static {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            logger.error("Message: " + e.getMessage() + "\nCause: " + e.getCause());
        }
    }

    public String edit() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("blog");
        updateRequest.type("post");
        updateRequest.id("1");
        try {
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field("title", "Веселые котята2")
                    .endObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return "";
    }

    /**
     * @return String - id of inserted record (transport company)
     */
    public String save(TransportCompany transportCompany) {
        System.out.println("ID: "+transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString());
        String index = transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString();//todo or id, if nounique
        String type = "company";
        String json = toJSON(transportCompany);
        System.out.println(json);
        IndexResponse response = client.prepareIndex(index, type)
                .setSource(toJSON(transportCompany))
                .get();
        return response.getId();
    }

    /**
     * @return String - id of inserted record (driver)
     **/
    public String save(Driver driver, String companyName) {
        String index = companyName.toLowerCase();//todo or id, if nounique
        String type = "driver";
        System.out.println(toJSON(driver));
        IndexResponse response = client.prepareIndex(index, type)
                .setSource(toJSON(driver))
                .get();
        return response.getId();
    }

    public String delete(TransportCompany transportCompany, String id_elastic) {
        DeleteResponse response = client.prepareDelete(transportCompany.getWarehouseCompany().getName().toLowerCase(), "company", id_elastic).get();
        return response.status().toString();
    }

    public String get(String id) {
        GetResponse responseGet = client.prepareGet("blog", "post", "2").get();
        System.out.println(responseGet);
        return "";
    }

    /*QueryBuilder qb = QueryBuilders.fuzzyQuery("name", "Скварец");

        client.prepareSearch(transportCompany.getWarehouseCompany().getName().toLowerCase())
            .suggest(new SuggestBuilder().addSuggestion("company", SuggestBuilders.completionSuggestion("name").text("Скварец"))).get();

*/
    public String find(TransportCompany transportCompany) {

        CompletionSuggestionBuilder compBuilder = new
                CompletionSuggestionBuilder("name");
        compBuilder.text("Скорость");

        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"suggest\": {\n" +
                        "    \"my-suggest-1\" : {\n" +
                        "    \"text\" : \"блик\",\n" +
                        "      \"term\" : {\n" +
                        "        \"field\" : \"name\"\n" +
                        "      }\n" +
                        "    }"+
                        "  }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                //.setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();

        System.out.println(sr.getSuggest().getSuggestion("my-suggest-1").getEntries().get(0).getOptions().get(0).getText());
        System.out.println(sr.getSuggest());

        QueryBuilder qb = termQuery("name", "истории");
        //System.out.println(qb=QueryBuilders.queryStringQuery("кот"));
        SearchResponse response = client.prepareSearch("blog4")
                .setTypes("post")

                //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)// Query

                .setFrom(0).setSize(5)
                //.setExplain(true)
                //.addSort("name", SortOrder.ASC)
                .get();
        System.out.println(response);
        //System.out.println(qb.toString());

        /*client.admin().indices()
                .prepareCreate("blog4").setSettings(
                "{\n" +
                        "    \"analysis\": {\n" +
                        "      \"filter\": {\n" +
                        "        \"ru_stop\": {\n" +
                        "          \"type\": \"stop\",\n" +
                        "          \"stopwords\": \"_russian_\"\n" +
                        "        },\n" +
                        "        \"ru_stemmer\": {\n" +
                        "          \"type\": \"stemmer\",\n" +
                        "          \"language\": \"russian\"\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"analyzer\": {\n" +
                        "        \"default\": {\n" +
                        "          \"char_filter\": [\n" +
                        "            \"html_strip\"\n" +
                        "          ],\n" +
                        "          \"tokenizer\": \"standard\",\n" +
                        "          \"filter\": [\n" +
                        "            \"lowercase\",\n" +
                        "            \"ru_stop\",\n" +
                        "            \"ru_stemmer\"\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "}"
        ).get();*/

        return "";
    }

    public List<SimilarityWrapper<TransportCompany>> fuzzyMatchFindTransportCompany(TransportCompany transportCompany){
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"query\": {\n" +
                        "    \"multi_match\": {\n" +
                        "      \"fields\":  [ \"name\"],\n" +
                        "      \"query\":     \""
                                +transportCompany.getName()+//todo injection?
                        "\",\n" +
                        "      \"fuzziness\": \"AUTO\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setRequest(new SearchRequest(transportCompany.getWarehouseCompany().getIdWarehouseCompany().toString())
                        .types("company"))
                //because name not unique-field between company and driver
                .get()
                .getResponse();

        System.out.println(sr);

        List <SimilarityWrapper<TransportCompany>> list = new ArrayList<>();

        for(SearchHit searchHits : sr.getHits().getHits()){
            SimilarityWrapper<TransportCompany> similarityWrapper = new SimilarityWrapper<>();
            Map map = searchHits.getSource();
            TransportCompany tr = new TransportCompany();//it's parse data, but worst
            tr.setId(Long.valueOf((Integer)map.get("id")));
            tr.setName((String) map.get("name"));
            tr.setTrusted((Boolean) map.get("trusted"));
            similarityWrapper.setOjbect(tr);
            similarityWrapper.setSimilarity(searchHits.getScore());
            list.add(similarityWrapper);
        }

        return list;
    }

    public String find(Driver driver) {
        return "";
    }

    public String toJSON(Object object) {
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        byte[] json = new byte[0];//so will right syntax
        try {
            json = mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return new String(json);
    }
}
