package com.itechart.warehouse.security;

import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.ActService;
import com.itechart.warehouse.service.services.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for resolving permission for user.
 */

@Service
public class PermissionResolverService {
    Logger logger = LoggerFactory.getLogger(PermissionResolverService.class);

    private GoodsService goodsService;
    private ActService actService;

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Autowired
    public void setActService(ActService actService) {
        this.actService = actService;
    }

    public boolean resolvePermissionToAccessGoods(WarehouseCompanyUserDetails userDetails, Long goodsId) {
        logger.info("Evaluating access permission to goods with id {} for user {}", goodsId, userDetails);
        if (userDetails == null || goodsId == null) return false;

        try {
            if (userDetails.getUser().getWarehouseCompany() != null) {
                WarehouseCompany company = goodsService.findWarehouseCompanyOfGoods(goodsId);
                if (company == null) return false;
                if (userDetails.getCompany() != null)
                    if (userDetails.getCompany().getIdWarehouseCompany() != null)
                        return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
            } else {
                Warehouse warehouse = goodsService.findWarehouseOfGoods(goodsId);
                if (warehouse != null) {
                    if (userDetails.getWarehouse() != null)
                        if (userDetails.getWarehouse().getIdWarehouse() != null)
                            return userDetails.getWarehouse().getIdWarehouse().equals(warehouse.getIdWarehouse());
                }
            }
            return false;
        } catch (DataAccessException e) {
            logger.error("Exception during evaluation: {}", e.getMessage());
            return false;
        } catch (IllegalParametersException e) {
            logger.error("Exception during evaluation: {}", e.getMessage());
            return false;
        } catch (ResourceNotFoundException e) {
            logger.error("Exception during evaluation: {}", e.getMessage());
            return false;
        }
    }


    public boolean resolvePermissionToAccessAct(WarehouseCompanyUserDetails userDetails,  Long actId) {
         logger.info("Evaluating access permission to act with id {} for user {}", actId, userDetails);
        if (userDetails == null || actId == null) return false;
        try {
            if (userDetails.getUser().getWarehouseCompany() != null) {
                WarehouseCompany company = actService.findWarehouseCompanyOfAct(actId);
                if (company == null) return false;
                if (userDetails.getCompany() != null)
                    if (userDetails.getCompany().getIdWarehouseCompany() != null)
                        return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
            } else {
                Warehouse warehouse = actService.findWarehouseOfAct(actId);
                if (warehouse != null) {
                    if (userDetails.getWarehouse() != null)
                        if (userDetails.getWarehouse().getIdWarehouse() != null)
                            return userDetails.getWarehouse().getIdWarehouse().equals(warehouse.getIdWarehouse());
                }
            }
            return false;
        } catch (DataAccessException e) {
            logger.error("Exception during evaluation: {}", e.getMessage());
            return false;
        } catch (IllegalParametersException e) {
            logger.error("Exception during evaluation: {}", e.getMessage());
            return false;
        } catch (ResourceNotFoundException e) {
            logger.error("Exception during evaluation: {}", e.getMessage());
            return false;
        }
    }


}
