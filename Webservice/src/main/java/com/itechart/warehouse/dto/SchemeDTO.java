package com.itechart.warehouse.dto;

import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.entity.StorageSpace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object which union space and their cells
 */

@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class SchemeDTO {
    private StorageSpace storageSpace;
    private List<StorageCell> storageCells;
}
