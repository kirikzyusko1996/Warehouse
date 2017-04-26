package com.itechart.warehouse.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Statuses of invoices.
 */
public enum InvoiceStatusEnum {
    REGISTERED("Зарегистрирован"),
    CHECKED("Проверка завершена"),
    COMPLETED("Оформление завершено"),
    RELEASE_ALLOWED("Выпуск разрешен"),
    MOVED_OUT("Вывезен со склада");


    private static Map<String, InvoiceStatusEnum> statuses = new HashMap();
    private String name;

    static {
        for (InvoiceStatusEnum instance : InvoiceStatusEnum.values()) {
            statuses.put(instance.getName(),instance);
        }
    }

    InvoiceStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InvoiceStatusEnum getStatus(String str) {
        return statuses.get(str);
    }
}
