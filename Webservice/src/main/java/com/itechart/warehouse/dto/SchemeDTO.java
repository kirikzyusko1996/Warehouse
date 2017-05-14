package com.itechart.warehouse.dto;

import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.entity.StorageSpace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Lenovo on 14.05.2017.
 */
@Setter
@Getter
@lombok.ToString
@EqualsAndHashCode
public class SchemeDTO {
    private StorageSpace storageSpace;
    private List<StorageCell> storageCells;
}
