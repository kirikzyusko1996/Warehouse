package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.sql.Timestamp;
import java.util.List;

/**
 * Data transfer object for act entity.
 */
@Setter
@Getter
@ToString(exclude = "goodsList")
@EqualsAndHashCode(exclude = "goodsList")
public class ActDTO {
    private List<GoodsDTO> goodsList;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank (message = "Act type is blank")
    private String type;
    private UserDTO user;
    private Long id;
    private Timestamp date;
    @NotBlank (message = "Note is blank")
    private String note;
    private Long warehouseId;
    private String warehouseName;
}
