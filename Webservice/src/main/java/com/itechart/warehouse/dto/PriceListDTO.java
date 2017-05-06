package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by Alexey on 29.04.2017.
 */
@Getter
@Setter
public class PriceListDTO {
    private BigDecimal dailyPrice;
    private Short idStorageSpaceType;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String comment;
}
