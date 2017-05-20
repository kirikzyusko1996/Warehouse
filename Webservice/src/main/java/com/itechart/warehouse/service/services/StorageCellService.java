package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.StorageCellDTO;
import com.itechart.warehouse.entity.StorageCell;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

/**
 * Created by Lenovo on 07.05.2017.
 */
public interface StorageCellService {
    StorageCell createStorageCell(StorageCellDTO storageCellDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
    StorageCell updateStorageCell(StorageCellDTO storageCellDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
    void deleteStorageCell(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;
    StorageCell findStorageCellById(String id) throws DataAccessException, IllegalParametersException;
}
