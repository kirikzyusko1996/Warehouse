package com.itechart.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.List;

/**
 * Data transfer object for act entity.
 */
@Setter
@Getter
@lombok.ToString(exclude = "goodsList")
public class ActDTO {
    @NotEmpty
    private List<Long> goodsList;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotBlank
    private String type;
    private User user;
    private Long id;
    private Timestamp date;
    private String note;
    private long totalCount;

    public static ActDTO buildStatusDTO(Act act) {
        Assert.notNull(act, "Act is null");
        ActDTO actDTO = new ActDTO();
        actDTO.setId(act.getId());
        if (act.getActType() != null)
            actDTO.setType(act.getActType().getName());
        actDTO.setDate(act.getDate());
        return actDTO;
    }
}
