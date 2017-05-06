package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Data transfer object for goods status entity.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsStatusDTO {
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    @NotEmpty
    private String statusName;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String statusNote;
}
