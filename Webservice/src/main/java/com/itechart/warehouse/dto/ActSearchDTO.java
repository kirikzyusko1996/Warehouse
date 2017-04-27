package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Data transfer object containing criteria for searching acts.
 */
@Setter
@Getter
@lombok.ToString(exclude = "goodsList")
public class ActSearchDTO {
    private String type;
    private Timestamp date;
}
