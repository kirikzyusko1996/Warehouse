package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.dto.StorageCellDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service for managing goods.
 * Provides basic operations with goods such as searching, creation, updating, deleting,
 * and setting status.
 */
public interface GoodsService {
    List<Goods> findAllGoods(int firstResult, int maxResults) throws DataAccessException;

    Goods findGoodsById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    GoodsDTO findGoodsDTOById(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsDTO> findGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    long getGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsDTO> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForWarehouseByStatus(Long warehouseId, String statusName, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<GoodsStatusDTO> findStatusesOfGoods(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    GoodsStatus findGoodsCurrentStatus(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Warehouse findWarehouseOwner(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    WarehouseCompany findWarehouseCompanyOwner(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Goods updateGoods(Long id, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    Goods createGoods(Long invoiceId, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<Goods> createGoodsBatch(Long invoiceId, List<GoodsDTO> goodsDtoList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteGoods(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isGoodsExists(Long id) throws DataAccessException, IllegalParametersException;

    void setGoodsStatus(Long goodsId, GoodsStatusDTO goodsStatusDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void putGoodsInCells(Long goodsId, List<StorageCellDTO> storageCells) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void setOutgoingInvoice(List<Long> goodsIds, Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsStatusName> getStatusNames() throws DataAccessException;

    List<Unit> getUnits() throws DataAccessException;

    List<StorageSpaceType> getStorageSpaceTypes() throws DataAccessException;

}
