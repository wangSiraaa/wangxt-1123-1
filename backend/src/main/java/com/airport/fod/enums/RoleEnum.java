package com.airport.fod.enums;

public enum RoleEnum {

    FIELD_INSPECTOR("FIELD_INSPECTOR", "场务巡查"),
    TOWER_CONTROLLER("TOWER_CONTROLLER", "塔台协调"),
    MAINTENANCE_TEAM("MAINTENANCE_TEAM", "维修班组");

    private final String code;
    private final String desc;

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(String code) {
        if (code == null) {
            return "";
        }
        for (RoleEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知角色";
    }
}
