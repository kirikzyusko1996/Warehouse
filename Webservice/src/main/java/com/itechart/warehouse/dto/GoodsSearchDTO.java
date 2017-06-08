package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object containing criteria for searching goods.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsSearchDTO {
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String name;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String storageType;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String quantityUnit;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String weightUnit;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String priceUnit;
    private List<GoodsStatusSearchDTO> statuses;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String currentStatus;
    private Long incomingInvoiceId;
    private Long outgoingInvoiceId;
    private Boolean actApplicable;
    private String actType;
}
