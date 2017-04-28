package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Alexey on 28.04.2017.
 */
@Getter
@Setter
public class LossReportItem {
    private String responsiblePersonName;
    private String actCreationDate;
    private String actType;
    private String goodsName;
    private String quantity;
    private String goodsCost;
}
