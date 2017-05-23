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
    @NotBlank(message = "Name can not be empty")
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String name;
    @DecimalMin(value = "0.0", message = "Quantity can not be less than 0.0")
    @NotNull(message = "Quantity can not be empty")
    private BigDecimal quantity;
    @DecimalMin(value = "0.0", message = "Weight can not be less than 0.0")
    @NotNull(message = "Weight can not be empty")
    private BigDecimal weight;
    @DecimalMin(value = "0.0", message = "Price can not be less than 0.0")
    @NotNull(message = "Price can not be empty")
    private BigDecimal price;
    @NotNull(message = "Storage type can not be empty")
    private StorageSpaceType storageType;
    @NotNull(message = "Quantity unit can not be empty")
    private Unit quantityUnit;
    @NotNull(message = "weight unit can not be empty")
    private Unit weightUnit;
    @NotNull(message = "Price unit can not be empty")
    private Unit priceUnit;

    private List<StorageCellDTO> cells;

    private GoodsStatusDTO status;

    private int totalCount;


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
