package com.itechart.warehouse.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Evaluator of the permissions to access specific entities by authenticated user.
 */
public class SecurityPermissionEvaluator implements PermissionEvaluator {
    private PermissionResolverService resolver;

    @Autowired
    public void setResolver(PermissionResolverService resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        Assert.notNull(targetType, "Type of resource is null");
        switch (targetType) {
            case "Goods":
                return evaluateGoodsPermission(authentication, targetId, permission);
            case "Act":
                return evaluateActPermission(authentication, targetId, permission);
            case "User":
                return evaluateUserPermission(authentication, targetId, permission);
            case "Warehouse":
                return evaluateWarehousePermission(authentication, targetId, permission);
            case "WarehouseCompany":
                return evaluateWarehouseCompanyPermission(authentication, targetId, permission);
            case "Invoice":
                return evaluateInvoicePermission(authentication, targetId, permission);
            case "TransportCompany":
                return evaluateTransportCompanyPermission(authentication, targetId, permission);
            case "ClientCompany":
                return evaluateClientCompanyPermission(authentication, targetId, permission);
            // TODO: 05.05.2017 add more

            default:
                throw new IllegalArgumentException("No such target");
        }
    }

    private boolean evaluateGoodsPermission(Authentication authentication, Serializable targetId, Object permission) {
        WarehouseCompanyUserDetails userDetails = (WarehouseCompanyUserDetails) authentication.getPrincipal();
        return resolver.resolvePermissionToAccessGoods(userDetails, (Long) targetId);
    }

    private boolean evaluateActPermission(Authentication authentication, Serializable targetId, Object permission) {
        WarehouseCompanyUserDetails userDetails = (WarehouseCompanyUserDetails) authentication.getPrincipal();
        return resolver.resolvePermissionToAccessAct(userDetails, (Long) targetId);
    }

    private boolean evaluateUserPermission(Authentication authentication, Serializable targetId, Object permission) {
        WarehouseCompanyUserDetails userDetails = (WarehouseCompanyUserDetails) authentication.getPrincipal();
        return resolver.resolvePermissionToAccessUser(userDetails, (Long) targetId);
    }

    private boolean evaluateWarehousePermission(Authentication authentication, Serializable targetId, Object permission) {
        return true;
    }

    private boolean evaluateWarehouseCompanyPermission(Authentication authentication, Serializable targetId, Object permission) {
        return true;
    }

    private boolean evaluateInvoicePermission(Authentication authentication, Serializable targetId, Object permission) {
        return true;
    }

    private boolean evaluateTransportCompanyPermission(Authentication authentication, Serializable targetId, Object permission) {
        return true;
    }

    private boolean evaluateClientCompanyPermission(Authentication authentication, Serializable targetId, Object permission) {
        return true;
    }

}
