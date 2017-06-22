package com.itechart.warehouse.query;

import com.itechart.warehouse.entity.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Criteria object for searching goods in the database.
 */
@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class GoodsSearchCriteria {
    private String name;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private StorageSpaceType storageType;
    private QuantityUnit quantityUnit;
    private WeightUnit weightUnit;
    private PriceUnit priceUnit;
    private List<GoodsStatusSearchCriteria> statuses;
    private GoodsStatusName currentStatus;
    private Long incomingInvoiceId;
    private Long outgoingInvoiceId;
    private Boolean actApplicable;
    private String actType;
}
