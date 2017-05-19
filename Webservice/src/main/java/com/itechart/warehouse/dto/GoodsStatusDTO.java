package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import com.itechart.warehouse.entity.GoodsStatus;
import com.itechart.warehouse.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

import java.sql.Timestamp;

/**
 * Data transfer object for goods status entity.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsStatusDTO {
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    @NotEmpty
    private String name;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String note;
    private Long id;
    private Timestamp date;
    private User user;


    public static GoodsStatusDTO buildStatusDTO(GoodsStatus status) {
        Assert.notNull(status, "Status is null");
        GoodsStatusDTO statusDTO = new GoodsStatusDTO();
        statusDTO.setId(status.getId());
        if (status.getGoodsStatusName() != null)
            statusDTO.setName(status.getGoodsStatusName().getName());
        statusDTO.setDate(status.getDate());
        statusDTO.setNote(status.getNote());
        return statusDTO;
    }
}
