package com.itechart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * Created by Lenovo on 19.05.2017.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        ElasticSearch elasticSearch = new ElasticSearch();
        TransportCompany transportCompany = new TransportCompany();
        transportCompany.setId(1l);
        transportCompany.setLogin("login");
        transportCompany.setName("Блики");
        transportCompany.setPassword("password");
        transportCompany.setTrusted(true);

        WarehouseCompany warehouseCompany = new WarehouseCompany();
        warehouseCompany.setName("warehouseCompany");
        warehouseCompany.setIdWarehouseCompany(1l);

        transportCompany.setWarehouseCompany(warehouseCompany);

        //System.out.println(elasticSearch.save(transportCompany));
        System.out.println(elasticSearch.fuzzyMatchFindTransportCompany(transportCompany));

        /*System.out.println(elasticSearch.save(transportCompany));

        Driver driver = new Driver();
        driver.setCountryCode("3752");
        driver.setFullName("Leonardo Di Caprio");
        driver.setPassportNumber("22432132432543t546y6460560");
        driver.setId(1l);
        driver.setIssuedBy("19.09.2017");

        System.out.println(elasticSearch.save(driver, "warehouseCompany"));*/
        //System.out.println(elasticSearch.delete(transportCompany, "AVwhIlQkNwHXiCv_f8wx"));
        //System.out.println(elasticSearch.find(transportCompany));
    }
}
