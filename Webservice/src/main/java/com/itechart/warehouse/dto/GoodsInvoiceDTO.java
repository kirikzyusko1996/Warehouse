package com.itechart.warehouse.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GoodsInvoiceDTO {
    private String name;
    private BigDecimal quantity;
    private BigDecimal weight;
    private BigDecimal price;
    private String storageTypeName;
}
