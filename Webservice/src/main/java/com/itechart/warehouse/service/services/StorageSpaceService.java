package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.StorageSpaceDTO;
import com.itechart.warehouse.entity.StorageSpace;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Created by Lenovo on 07.05.2017.
 */
public interface StorageSpaceService {
    StorageSpace createStorageSpace(StorageSpaceDTO storageSpaceDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
    StorageSpace updateStorageSpace(StorageSpaceDTO storageSpaceDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
    void deleteStorageSpace(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
    List<StorageSpace> findStorageByWarehouseId(String id) throws DataAccessException, IllegalParametersException;
}
