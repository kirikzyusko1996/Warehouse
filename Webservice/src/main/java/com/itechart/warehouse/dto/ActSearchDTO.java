package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
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
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp toDate;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String creatorLastName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String creatorFirstName;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String creatorPatronymic;

}
