package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Data transfer object containing criteria for searching goodsList.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsSearchDTO {
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String name;
    private BigDecimal fromQuantity;
    private BigDecimal toQuantity;
    private BigDecimal fromWeight;
    private BigDecimal toWeight;
    private BigDecimal fromPrice;
    private BigDecimal toPrice;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String storageTypeName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String quantityUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String weightUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String priceUnitName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp fromRegistrationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp toRegistrationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp fromMoveOutDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp toMoveOutDate;


    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String managerName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String controllerName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String dispatcherName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String currentStatus;

    //todo status, registration date, manager etc..
}
