package com.itechart.warehouse.constants;

/**
 * Settings presets.
 */
public enum PresetEnum {
    DARK(1),
    LIGHT(2);

    private long id;

    PresetEnum(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static long DEFAULT(){
        return PresetEnum.DARK.getId();
    }
}
