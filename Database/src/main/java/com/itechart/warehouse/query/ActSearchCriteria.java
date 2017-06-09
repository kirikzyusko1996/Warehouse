package com.itechart.warehouse.query;

import com.itechart.warehouse.entity.ActType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Criteria object for searching acts in the database.
 */
@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class ActSearchCriteria {
    private ActType type;
    private Timestamp fromDate;
    private Timestamp toDate;
    private String creatorLastName;
    private String creatorFirstName;
    private String creatorPatronymic;
}
