package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.PriceUnit;
import com.itechart.warehouse.entity.QuantityUnit;
import com.itechart.warehouse.entity.StorageSpaceType;
import com.itechart.warehouse.entity.WeightUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString(exclude = {"cells", "currentStatus", "registeredStatus", "movedOutStatus"})
@EqualsAndHashCode(exclude = {"cells", "currentStatus", "registeredStatus", "movedOutStatus"})
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
    private QuantityUnit quantityUnit;
    @NotNull(message = "weight unit can not be empty")
    private WeightUnit weightUnit;
    @NotNull(message = "Price unit can not be empty")
    private PriceUnit priceUnit;
    private Long warehouseId;
    private List<StorageCellDTO> cells;
    private GoodsStatusDTO currentStatus;
    private GoodsStatusDTO registeredStatus;
    private GoodsStatusDTO movedOutStatus;
}
