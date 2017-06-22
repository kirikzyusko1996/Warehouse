package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Timestamp;

/**
 * Data transfer object for goods status entity.
 */
@Setter
@Getter
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class GoodsStatusDTO {
    private Long id;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotEmpty
    private String name;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String note;
    private Timestamp date;
    private User user;
}
