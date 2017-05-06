package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.Goods;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object for goods entity.
 */
@Setter
@Getter
@lombok.ToString

public class GoodsDTO {
    private Long id;
    @NotBlank
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String name;
    @DecimalMin(value = "0.0", message = "Quantity can not be less than 0.0")
    @NotNull
    private BigDecimal quantity;
    @DecimalMin(value = "0.0", message = "Weight can not be less than 0.0")
    @NotNull
    private BigDecimal weight;
    @DecimalMin(value = "0.0", message = "Price can not be less than 0.0")
    @NotNull
    private BigDecimal price;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank
    private String storageTypeName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank
    private String quantityUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank
    private String weightUnitName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank
    private String priceUnitName;

    private List<Long> cells;

    public void addCell(Long cell) {
        cells.add(cell);
    }

    public void removeCell(Long cell) {
        cells.remove(cell);
    }


    public Goods buildGoodsEntity() {
        Goods goods = new Goods();
        goods.setName(name);
        goods.setWeight(weight);
        goods.setPrice(price);
        goods.setQuantity(quantity);
        return goods;
    }


}
