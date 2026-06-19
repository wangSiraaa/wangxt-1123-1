package com.airport.fod.enums;

public enum RunwayStatusEnum {

    NORMAL(1, "正常"),
    FROZEN(2, "冻结"),
    MAINTENANCE(3, "维修中"),
    RESTRICTED(4, "受限");

    private final Integer code;
    private final String desc;

    RunwayStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (RunwayStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知状态";
    }
}
