package com.itechart.warehouse.query;
import com.itechart.warehouse.entity.GoodsStatusName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Criteria object for searching goods by status.
 */
@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class GoodsStatusSearchCriteria {
    private GoodsStatusName name;
    private String userLastName;
    private String userFirstName;
    private String userPatronymic;
    private Timestamp fromDate;
    private Timestamp toDate;
}
