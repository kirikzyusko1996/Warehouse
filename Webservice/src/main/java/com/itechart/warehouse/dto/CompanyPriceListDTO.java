package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Alexey on 15.06.2017.
 */
@Getter
@Setter
public class CompanyPriceListDTO {
    private BigDecimal pricePerMonth;
    private Long idWarehouseCompany;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String comment;
}
