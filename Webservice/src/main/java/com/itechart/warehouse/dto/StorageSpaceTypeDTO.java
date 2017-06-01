package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString
public class StorageSpaceTypeDTO {
    private Short idStorageSpaceType;
    private String name;

}
