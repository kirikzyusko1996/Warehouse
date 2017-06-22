package com.itechart.warehouse.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of storage places.
 */
public enum StorageTypeEnum {
    HEATED_PLACE("Отапливаемое помещение"),
    UNHEATED_PLACE("Неотапливаемое помещение"),
    COOLING_CHAMBER("Холодильная камера"),
    OPEN_SPACE("Открытая площадка"),
    FREEZING_CHAMBER("Камера глубокой заморозки");

    private static Map<String, StorageTypeEnum> types = new HashMap();
    private String name;

    static {
        for (StorageTypeEnum instance : StorageTypeEnum.values()) {
            types.put(instance.getName(),instance);
        }
    }

    StorageTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static StorageTypeEnum getStorageType(String str) {
        return types.get(str);
    }
}
