package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by Alexey on 29.04.2017.
 */
@Getter
@Setter
public class PriceListDTO {
    private DateTime endTime;
    private BigDecimal dailyPrice;
    private Long idStorageSpaceType;
    private String comment;
}
