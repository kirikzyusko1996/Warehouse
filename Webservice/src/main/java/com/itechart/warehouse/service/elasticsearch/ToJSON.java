package com.itechart.warehouse.service.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Lenovo on 23.05.2017.
 */

public class ToJSON {
    private static Logger logger = LoggerFactory.getLogger(ToJSON.class);

    public static String toJSON(Object object) {
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
