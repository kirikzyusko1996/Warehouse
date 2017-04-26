package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for goodsList status entity.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsStatusDTO {
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String statusName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String statusNote;
}
