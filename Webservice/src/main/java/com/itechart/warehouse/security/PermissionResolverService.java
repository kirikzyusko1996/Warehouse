package com.itechart.warehouse.security;

import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.Warehouse;
import com.itechart.warehouse.entity.WarehouseCompany;
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

    private static final String ERROR_EXCEPTION_DURING_EVALUATION = "Exception during evaluation: {}";

    private Logger logger = LoggerFactory.getLogger(PermissionResolverService.class);

    private GoodsService goodsService;
    private ActService actService;
    private UserService userService;
    private InvoiceService invoiceService;
    private TransportCompanyService transportService;
    private WarehouseCustomerCompanyService customerService;
    private WarehouseCompanyService warehouseCompanyService;
    private WarehouseService warehouseService;
    private StorageCellService storageCellService;
    private StorageSpaceService storageSpaceService;

    @Autowired
    public void setStorageSpaceService(StorageSpaceService storageSpaceService) {
        this.storageSpaceService = storageSpaceService;
    }

    @Autowired
    public void setStorageCellService(StorageCellService storageCellService) {
        this.storageCellService = storageCellService;
    }

    @Autowired
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

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
    public void setInvoiceService(InvoiceService service) {
        this.invoiceService = service;
    }

    @Autowired
    @Lazy
    public void setTransportService(TransportCompanyService service) {
        this.transportService = service;
    }

    @Autowired
    public void setCustomerService(WarehouseCustomerCompanyService service) {
        this.customerService = service;
    }

    @Autowired
    public void setWarehouseCompanyService(WarehouseCompanyService warehouseCompanyService) {
        this.warehouseCompanyService = warehouseCompanyService;
    }

    public boolean resolvePermissionToAccessGoods(WarehouseCompanyUserDetails userDetails, Long goodsId) {
        logger.info("Evaluating access permission to goods with id {} for user {}", goodsId, userDetails);
        if (userDetails == null || goodsId == null) return false;
        try {
//            if (userDetails.getUser().getWarehouseCompany() != null) {
//                WarehouseCompany company = goodsService.findWarehouseCompanyOwnedBy(goodsId);
//                if (company == null) return false;
//                if (userDetails.getCompany() != null)
//                    if (userDetails.getCompany().getIdWarehouseCompany() != null)
//                        return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
//            } else {
            Warehouse warehouse = goodsService.findWarehouseOwnedBy(goodsId);
            if (warehouse != null && userDetails.getWarehouse() != null && userDetails.getWarehouse().getIdWarehouse() != null) {
                return userDetails.getWarehouse().getIdWarehouse().equals(warehouse.getIdWarehouse());
            }
//            }
            return false;
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }


    public boolean resolvePermissionToAccessAct(WarehouseCompanyUserDetails userDetails, Long actId) {
        logger.info("Evaluating access permission to act with id {} for user {}", actId, userDetails);
        if (userDetails == null || actId == null) return false;
        try {
            if (userDetails.getUser().getWarehouseCompany() != null) {
                WarehouseCompany company = actService.findWarehouseCompanyOwnedBy(actId);
                if (company == null) {
                    return false;
                }
                if (userDetails.getCompany() != null && userDetails.getCompany().getIdWarehouseCompany() != null)
                    return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
            } else {
                Warehouse warehouse = actService.findWarehouseOwnedBy(actId);
                if (warehouse != null && userDetails.getWarehouse() != null && userDetails.getWarehouse().getIdWarehouse() != null) {
                    return userDetails.getWarehouse().getIdWarehouse().equals(warehouse.getIdWarehouse());
                }
            }
            return false;
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }


    public boolean resolvePermissionToAccessUser(WarehouseCompanyUserDetails userDetails, Long userId) {
        logger.info("Evaluating access permission to user with id {} for user {}", userId, userDetails);
        if (userDetails == null || userId == null) return false;
        try {
            if (userDetails.getUser() != null) {
                User user = userService.findUserById(userId);
                if (user != null && userDetails.getUser().getWarehouseCompany() != null) {
                    if (user.getWarehouseCompany() != null) {
                        return userDetails.getCompany().getIdWarehouseCompany().equals(user.getWarehouseCompany().getIdWarehouseCompany());
                    } else {
                        if (user.getWarehouse() != null) {
                            return userDetails.getWarehouse().getIdWarehouse().equals(user.getWarehouse().getIdWarehouse());
                        }
                    }
                }
            }
            return false;
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessInvoice(WarehouseCompanyUserDetails userDetails, Long invoiceId) {
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
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessTransportCompany(WarehouseCompanyUserDetails userDetails, Long transportId) {
        logger.info("Evaluating access permission to customer with id {} for user {}", transportId, userDetails);
        if (userDetails == null || transportId == null) {
            return false;
        }

        try {
            if (userDetails.getCompany() != null) {
                WarehouseCompany company = transportService.findWarehouseCompanyByTransportId(transportId);
                if (company != null && userDetails.getCompany() != null) {
                    return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
                }
            }

            return false;
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessWarehouseCustomerCompany(WarehouseCompanyUserDetails userDetails, Long customerId) {
        logger.info("Evaluating access permission to customer with id {} for user {}", customerId, userDetails);
        if (userDetails == null || customerId == null) {
            return false;
        }

        try {
            if (userDetails.getCompany() != null) {
                WarehouseCompany company = customerService.findWarehouseCompanyByCustomerId(customerId);
                if (company != null && userDetails.getCompany() != null) {
                    return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
                }
            }

            return false;
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessWarehouseCompany(WarehouseCompanyUserDetails userDetails, Long warehouseCompanyId) {
        logger.info("Evaluating access permission to company with id {} for user {}", warehouseCompanyId, userDetails);
        if (userDetails == null || warehouseCompanyId == null) {
            return false;
        }
        if(userDetails.getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString())){
            return true;
        }
        try {
            if (userDetails.getCompany() != null) {
                WarehouseCompany company = warehouseCompanyService.findWarehouseCompanyById(warehouseCompanyId);
                if (company != null) {
                    return userDetails.getCompany().getIdWarehouseCompany().equals(company.getIdWarehouseCompany());
                }
            }
            return false;
        } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessWarehouse(WarehouseCompanyUserDetails userDetails, Long id_warehouse) {
        logger.info("Evaluating access permission to warehouse with id {} for user {}", id_warehouse, userDetails);
        if (userDetails == null || id_warehouse == null) {
            return false;
        }
        /*if(userDetails.getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString())){
            return true;
        }*/
        try {
            if (userDetails.getCompany() != null) {
                Warehouse warehouse = warehouseService.findWarehouseById(id_warehouse);
                if (warehouse != null) {
                    return userDetails.getCompany().getIdWarehouseCompany().equals(warehouse.getWarehouseCompany().getIdWarehouseCompany());
                }
            }
            return false;
        } catch (DataAccessException | IllegalParametersException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessSpace(WarehouseCompanyUserDetails userDetails, Long id_space) {
        logger.info("Evaluating access permission to space with id {} for user {}", id_space, userDetails);
        if (userDetails == null || id_space == null) {
            return false;
        }
        /*if(userDetails.getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString())){
            return true;
        }*/
        try {
            if (userDetails.getCompany() != null) {
                WarehouseCompany warehouseCompany = storageSpaceService.findWarehouseCompanyBySpace(id_space);
                if (warehouseCompany != null) {
                    return userDetails.getCompany().getIdWarehouseCompany().equals(warehouseCompany.getIdWarehouseCompany());
                }
            }
            return false;
        } catch (DataAccessException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }

    public boolean resolvePermissionToAccessCell(WarehouseCompanyUserDetails userDetails, Long id_cell) {
        logger.info("Evaluating access permission to cell with id {} for user {}", id_cell, userDetails);
        if (userDetails == null || id_cell == null) {
            return false;
        }
        /*if(userDetails.getUser().hasRole(UserRoleEnum.ROLE_ADMIN.toString())){
            return true;
        }*/
        try {
            if (userDetails.getCompany() != null) {
                WarehouseCompany warehouseCompany = storageCellService.findWarehouseCompanyByCell(id_cell);
                if (warehouseCompany != null) {
                    return userDetails.getCompany().getIdWarehouseCompany().equals(warehouseCompany.getIdWarehouseCompany());
                }
            }
            return false;
        } catch (DataAccessException e) {
            logger.error(ERROR_EXCEPTION_DURING_EVALUATION, e.getMessage());
            return false;
        }
    }
}
