package com.itechart.warehouse.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Act's types.
 */
public enum ActTypeEnum {
    MISMATCH_ACT("Акт несоответствия"),
    ACT_OF_LOSS("Акт утери"),
    ACT_OF_THEFT("Акт кражи"),
    WRITE_OFF_ACT("Акт списания");


    private static Map<String, ActTypeEnum> statuses = new HashMap();
    private String name;

    static {
        for (ActTypeEnum instance : ActTypeEnum.values()) {
            statuses.put(instance.getName(),instance);
        }
    }

    ActTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ActTypeEnum getStatus(String str) {
        return statuses.get(str);
    }
}
