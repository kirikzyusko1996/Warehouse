package com.itechart.warehouse.security;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Service for resolving permission for user.
 */

@Service
public class PermissionResolverService {
    Logger logger = LoggerFactory.getLogger(PermissionResolverService.class);

    private GoodsService goodsService;
    private ActService actService;
    private UserService userService;
    private InvoiceService invoiceService;
    private WarehouseCustomerCompanyService customerService;

    @Autowired

    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Autowired
    public void setActService(ActService actService) {
        this.actService = actService;
    }

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setInvoiceService(InvoiceService service){
        this.invoiceService = service;
    }

    @Autowired
    public void setCustomerService(WarehouseCustomerCompanyService service) {
        this.customerService = service;
    }

    public boolean resolvePermissionToAccessGoods(WarehouseCompanyUserDetails userDetails, Long goodsId) {
        logger.info("Evaluating access permission to goods with id {} for user {}", goodsId, userDetails);
        if (userDetails == null || goodsId == null) return false;
        try {
            if (userDetails.getUser().getWarehouseCompany() != null) {
                WarehouseCompany company = goodsService.findWarehouseCompanyOwner(goodsId);
                if (company == null) return false;
                if (userDetails.getCompany() != null)
                    if (userDetails.getCompany().getIdWarehouseCompany() != null)
                        return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
            } else {
                Warehouse warehouse = goodsService.findWarehouseOwner(goodsId);
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


    public boolean resolvePermissionToAccessAct(WarehouseCompanyUserDetails userDetails, Long actId) {
        logger.info("Evaluating access permission to act with id {} for user {}", actId, userDetails);
        if (userDetails == null || actId == null) return false;
        try {
            if (userDetails.getUser().getWarehouseCompany() != null) {
                WarehouseCompany company = actService.findWarehouseCompanyOwner(actId);
                if (company == null) return false;
                if (userDetails.getCompany() != null)
                    if (userDetails.getCompany().getIdWarehouseCompany() != null)
                        return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
            } else {
                Warehouse warehouse = actService.findWarehouseOwner(actId);
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


    public boolean resolvePermissionToAccessUser(WarehouseCompanyUserDetails userDetails, Long userId) {
        logger.info("Evaluating access permission to user with id {} for user {}", userId, userDetails);
        if (userDetails == null || userId == null) return false;
        try {
            if (userDetails.getUser() != null) {
                User user = userService.findUserById(userId);
                if (user != null) {
                    if (userDetails.getUser().getWarehouseCompany() != null) {
                        if (user.getWarehouseCompany() != null)
                            return userDetails.getCompany().getIdWarehouseCompany().equals(user.getWarehouseCompany().getIdWarehouseCompany());
                        else {
                            if (user.getWarehouse() != null)
                                return userDetails.getWarehouse().getIdWarehouse().equals(user.getWarehouse().getIdWarehouse());
                        }
                    }
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

    public boolean resolvePermissionToAccessInvoice(WarehouseCompanyUserDetails userDetails, Long invoiceId){
        logger.info("Evaluating access permission to invoice with id {} for user {}", invoiceId, userDetails);

        if (userDetails == null || invoiceId == null) {
            return false;
        }

        try {
            if (userDetails.getUser().getWarehouse() != null) {
                Warehouse warehouse = invoiceService.findWarehouseByInvoiceId(invoiceId);
                if (warehouse != null && userDetails.getWarehouse() != null) {
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

    public boolean resolvePermissionToAccessTransportCompany(WarehouseCompanyUserDetails userDetails, Long transportId) {
        logger.info("Evaluating access permission to customer with id {} for user {}", transportId, userDetails);
        if (userDetails == null || transportId == null) {
            return false;
        }

        // todo evaluate

        return true;
    }

    public boolean resolvePermissionToAccessWarehouseCustomerCompany(WarehouseCompanyUserDetails userDetails, Long customerId) {
        logger.info("Evaluating access permission to customer with id {} for user {}", customerId, userDetails);
        if (userDetails == null || customerId == null) {
            return false;
        }

        // todo evaluate

        return true;
    }
}
