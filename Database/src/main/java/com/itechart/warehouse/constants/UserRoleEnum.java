package com.itechart.warehouse.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * User's roles.
 */
public enum UserRoleEnum {
    ROLE_ADMIN("Администратор системы"),
    ROLE_SUPERVISOR("Администратор склада"),
    ROLE_DISPATCHER("Диспетчер склада"),
    ROLE_MANAGER("Менеджер по складу"),
    ROLE_CONTROLLER("Контролёр"),
    ROLE_OWNER("Владелец склада");

    private static Map<String, UserRoleEnum> roles = new HashMap();
    private String name;

    static {
        for (UserRoleEnum instance : UserRoleEnum.values()) {
            roles.put(instance.getName(), instance);
        }
    }

    UserRoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UserRoleEnum getRole(String str) {
        return roles.get(str);
    }
}
