package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data transfer object for goods entity.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsDTO {
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String name;
    private BigDecimal quantity;
    private BigDecimal weight;
    private BigDecimal price;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String storageTypeName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String quantityUnitName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String weightUnitName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String priceUnitName;

}
