package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

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
    @NotNull
    private StorageSpaceType storageType;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotNull
    private Unit quantityUnit;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotNull
    private Unit weightUnit;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotNull
    private Unit priceUnit;

    private List<StorageCellDTO> cells;

    private GoodsStatus status;


    public void addCell(StorageCellDTO cell) {
        cells.add(cell);
    }

    public void removeCell(StorageCellDTO cell) {
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

    public static GoodsDTO buildGoodsDTO(Goods goods) {
        Assert.notNull(goods, "Goods is null");
        GoodsDTO dto = new GoodsDTO();
        dto.setId(goods.getId());
        dto.setName(goods.getName());
        dto.setQuantity(goods.getQuantity());
        dto.setWeight(goods.getWeight());
        dto.setPrice(goods.getPrice());
        dto.setStorageType(goods.getStorageType());
        dto.setWeightUnit(goods.getWeightUnit());
        dto.setQuantityUnit(goods.getQuantityUnit());
        dto.setPriceUnit(goods.getPriceUnit());
        return dto;

    }


}
