package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for storage Cell
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
    private Boolean status;
}
