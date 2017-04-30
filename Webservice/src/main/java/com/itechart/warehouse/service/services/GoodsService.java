package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.GoodsDTO;
import com.itechart.warehouse.dto.GoodsSearchDTO;
import com.itechart.warehouse.dto.GoodsStatusDTO;
import com.itechart.warehouse.entity.Goods;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;

import java.util.List;

/**
 * Service for managing goodsList.
 * Provides basic operations with goodsList such as searching, creation, updating, deleting,
 * and setting status.
 */
public interface GoodsService {
    List<Goods> findAllGoods(int firstResult, int maxResults) throws DataAccessException;

    Goods findGoodsById(Long id) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForInvoice(Long invoiceId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Goods> findGoodsForCompanyByCriteria(Long companyId, GoodsSearchDTO goodsSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    Goods updateGoods(Long id, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException;

    Goods createGoods(Long invoiceId, GoodsDTO goodsDTO) throws DataAccessException, IllegalParametersException;

    void deleteGoods(Long id) throws DataAccessException, IllegalParametersException;

    boolean isGoodsExists(Long id) throws DataAccessException, IllegalParametersException;

    void setGoodsStatus(Long goodsId, GoodsStatusDTO goodsStatusDTO) throws DataAccessException, IllegalParametersException;

    void putGoodsInCells(Long goodsId, List<Long> storageCellIds) throws DataAccessException, IllegalParametersException;

    void removeGoodsFromStorage(Long goodsId) throws DataAccessException, IllegalParametersException;


}
