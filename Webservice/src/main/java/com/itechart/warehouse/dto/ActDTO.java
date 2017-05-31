package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.List;

/**
 * Data transfer object for act entity.
 */
@Setter
@Getter
@lombok.ToString(exclude = "goodsIdList")
public class ActDTO {
    private List<Goods> goodsList;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank (message = "Act type is blank")
    private String type;
    private User user;
    private Long id;
    private Timestamp date;
    private String note;
    private long totalCount;
    private Long warehouseId;

    public static ActDTO buildActDTO(Act act) {
        Assert.notNull(act, "Act is null");
        ActDTO actDTO = new ActDTO();
        actDTO.setId(act.getId());
        if (act.getActType() != null)
            actDTO.setType(act.getActType().getName());
        actDTO.setDate(act.getDate());
        actDTO.setNote(act.getNote());
        return actDTO;
    }
}
