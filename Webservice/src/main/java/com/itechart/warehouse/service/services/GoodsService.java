package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dao.exception.GenericDAOException;
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

    List<GoodsDTO> findStoredGoodsForWarehouse(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<GoodsDTO> findActApplicableGoods(Long warehouseId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    long getGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException;

    long getStoredGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException;

    long getActApplicableGoodsCount(Long warehouseId) throws DataAccessException, IllegalParametersException;

    long getGoodsSearchResultCount(Long warehouseId, GoodsSearchDTO searchDTO) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsDTO> findGoodsDTOsForInvoice(Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsDTO> findGoodsForWarehouseByCriteria(Long warehouseId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForWarehouseByStatus(Long warehouseId, String statusName, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<GoodsStatusDTO> findStatusesOfGoods(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    GoodsStatus findGoodsCurrentStatus(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Warehouse findWarehouseOwnedBy(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    WarehouseCompany findWarehouseCompanyOwnedBy(Long goodsId) throws IllegalParametersException, ResourceNotFoundException, DataAccessException;

    Goods updateGoods(Long id, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    Goods createGoods(Long invoiceId, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsDTO> splitGoodsForAct(String actType, List<GoodsDTO> goodsList) throws IllegalParametersException, DataAccessException, ResourceNotFoundException;

    boolean isActApplicable(List<GoodsDTO> goodsDTOList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<Goods> createGoodsBatch(Long invoiceId, List<GoodsDTO> goodsDtoList) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteGoods(Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    boolean isGoodsExists(Long id) throws DataAccessException, IllegalParametersException;

    GoodsStatus setGoodsStatus(Long goodsId, GoodsStatusDTO goodsStatusDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void putGoodsInCells(Long goodsId, List<StorageCellDTO> storageCells) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void setOutgoingInvoice(List<Long> goodsIds, Long invoiceId) throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    List<GoodsStatusName> getStatusNames() throws DataAccessException;

    List<QuantityUnit> getQuantityUnits() throws DataAccessException;

    List<WeightUnit> getWeightUnits() throws DataAccessException;

    List<PriceUnit> getPriceUnits() throws DataAccessException;

    List<StorageSpaceType> getStorageSpaceTypes() throws DataAccessException;

    GoodsDTO mapToDto(Goods goods);

    Goods saveGoodsForOutgoingInvoice(Goods goodsForInvoice) throws GenericDAOException;
}
