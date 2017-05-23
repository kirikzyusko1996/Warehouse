package com.itechart.warehouse.service.elasticsearch;

import com.itechart.warehouse.entity.Driver;
import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.WarehouseCompany;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * Created by Lenovo on 19.05.2017.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        ElasticSearchTransportCompany elasticSearch = new ElasticSearchTransportCompany();

        WarehouseCompany warehouseCompany = new WarehouseCompany();
        warehouseCompany.setName("warehouseCompany");
        warehouseCompany.setIdWarehouseCompany(1l);

        TransportCompany transportCompany = new TransportCompany();
        transportCompany.setId(1l);
        transportCompany.setLogin("login");
        transportCompany.setName("бики");
        transportCompany.setPassword("password");
        transportCompany.setTrusted(true);
        transportCompany.setWarehouseCompany(warehouseCompany);

        System.out.println(elasticSearch.search(transportCompany));

        Driver driver = new Driver();
        driver.setCountryCode("3752");
        driver.setFullName("lex");
        driver.setPassportNumber("22432132432543t546y6460560");
        driver.setId(5l);
        driver.setIssuedBy("19.09.2017");

        ElasticSearchDriver elasticSearchDriver = new ElasticSearchDriver();
        //elasticSearchDriver.save(driver, transportCompany.getWarehouseCompany().getIdWarehouseCompany());
        System.out.println(elasticSearchDriver.search(driver, transportCompany.getWarehouseCompany().getIdWarehouseCompany()));
    }
}
