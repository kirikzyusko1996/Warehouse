package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Data transfer object for act entity.
 */
@Setter
@Getter
@lombok.ToString(exclude = "goodsList")
public class ActDTO {
    private List<Long> goodsList;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String type;

}
