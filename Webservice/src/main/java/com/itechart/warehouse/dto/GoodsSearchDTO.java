package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object containing criteria for searching goodsList.
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
    private String storageTypeName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String quantityUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String weightUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String priceUnitName;
    private List<GoodsStatusSearchDTO> statuses;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String currentStatus;

    public void addStatusSearchDTO(GoodsStatusSearchDTO statusSearchDTO) {
        if (statuses != null)
            statuses.add(statusSearchDTO);
        else throw new AssertionError();
    }

    public void removeStatusSearchDTO(GoodsStatusSearchDTO statusSearchDTO) {
        if (statuses != null)
            statuses.remove(statusSearchDTO);
        else throw new AssertionError();

    }


}
