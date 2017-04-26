package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object for act entity.
 */
@Setter
@Getter
@lombok.ToString(exclude = "goods")
public class ActDTO {
    private List<Long> goods;
    private String actType;
}
