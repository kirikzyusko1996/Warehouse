package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.Goods;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Data transfer object for goods entity.
 */
@Setter
@Getter
@lombok.ToString

public class GoodsDTO {
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String name;
    @DecimalMin(value = "0.0", message = "Quantity can not be less than 0.0")
    private BigDecimal quantity;
    @DecimalMin(value = "0.0", message = "Weight can not be less than 0.0")
    private BigDecimal weight;
    @DecimalMin(value = "0.0", message = "Price can not be less than 0.0")
    private BigDecimal price;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String storageTypeName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String quantityUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String weightUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String priceUnitName;


    public Goods buildGoodsEntity() {
        Goods goods = new Goods();
        goods.setName(name);
        goods.setWeight(weight);
        goods.setPrice(price);
        goods.setQuantity(quantity);
        return goods;
    }


}
