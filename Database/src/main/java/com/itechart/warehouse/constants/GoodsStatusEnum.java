package com.itechart.warehouse.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Statuses of goodsList.
 */
public enum GoodsStatusEnum {
    REGISTERED("Зарегистрирован"),
    CHECKED("Проверка завершена"),
    STORED("Принят на хранение"),
    LOST_BY_TRANSPORT_COMPANY("Утерян перевозчиком"),
    LOST_BY_WAREHOUSE_COMPANY("Утерян со склада"),
    STOLEN("Кража со склада"),
    TRANSPORT_COMPANY_MISMATCH("Недостача перевозчика"),
    SEIZED("Конфискован"),
    RECYCLED("Утилизирован"),
    WITHDRAWN("Снят с хранения"),
    RELEASE_ALLOWED("Выпуск разрешен"),
    MOVED_OUT("Вывезен со склада");

    private static Map<String, GoodsStatusEnum> statuses = new HashMap();
    private String name;

    static {
        for (GoodsStatusEnum instance : GoodsStatusEnum.values()) {
            statuses.put(instance.getName(),instance);
        }
    }

    GoodsStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GoodsStatusEnum getStatus(String str) {
        return statuses.get(str);
    }
}
