package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Data transfer object for searching goods status entity.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsStatusSearchDTO {
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String statusName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String userLastName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String userFirstName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String userPatronymic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp toDate;
}
