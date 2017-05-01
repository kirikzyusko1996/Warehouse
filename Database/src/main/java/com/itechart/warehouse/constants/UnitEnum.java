package com.itechart.warehouse.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Units.
 */
public enum UnitEnum {
    UNIT_KILOGRAM("кг"),
    UNIT_LITER("л"),
    UNIT_METER("м"),
    UNIT_SQUARE_METER("м2"),
    UNIT_CUBIC_METER("м3"),
    UNIT_BARREL("бочка"),
    UNIT_PACK("уп"),
    UNIT_PIECE("шт"),
    UNIT_BYN("руб"),
    UNIT_USD("долл"),
    UNIT_TON("т");

    private static Map<String, UnitEnum> units = new HashMap();
    private String name;

    static {
        for (UnitEnum instance : UnitEnum.values()) {
            units.put(instance.getName(), instance);
        }
    }

    UnitEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UnitEnum getUnit(String str) {
        return units.get(str);
    }
}
