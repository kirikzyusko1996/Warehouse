package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lenovo on 07.05.2017.
 */
@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class StorageCellDTO {
    private Long idStorageCell;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String number;
    private Long idStorageSpace;
    private Long idGoods;
}
