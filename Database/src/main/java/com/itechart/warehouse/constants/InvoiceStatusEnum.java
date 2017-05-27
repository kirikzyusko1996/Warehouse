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

    private String name;

    InvoiceStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InvoiceStatusEnum getStatus(String str) {
        return InvoiceStatusEnum.valueOf(str);
    }
}
