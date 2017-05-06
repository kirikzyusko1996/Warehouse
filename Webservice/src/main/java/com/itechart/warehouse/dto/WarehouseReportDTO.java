package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.DateJsonDeserializer;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;

/**
 * Created by Alexey on 30.04.2017.
 */
@Getter
@Setter
@ToString
public class WarehouseReportDTO {
    private Long idWarehouse;
    @JsonDeserialize(using = DateJsonDeserializer.class)
    private LocalDate startDate;
    @JsonDeserialize(using = DateJsonDeserializer.class)
    private LocalDate endDate;
}
