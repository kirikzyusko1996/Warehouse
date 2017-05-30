package com.itechart.warehouse.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for storage_space table, which contains only id .
 */
@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class StorageSpaceDTO {
    private Long idStorageSpace;
    private Long idWarehouse;
    private Short idStorageSpaceType;
    private Boolean status;
}
