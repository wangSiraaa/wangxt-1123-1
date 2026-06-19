package com.airport.fod.enums;

public enum RiskLevelEnum {

    LOW(1, "低"),
    MEDIUM(2, "中"),
    HIGH(3, "高"),
    EXTREME(4, "极高");

    private final Integer code;
    private final String desc;

    RiskLevelEnum(Integer code, String desc) {
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
        for (RiskLevelEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知等级";
    }

    public static String getColorByCode(Integer code) {
        if (code == null) {
            return "#999999";
        }
        switch (code) {
            case 1:
                return "#52c41a";
            case 2:
                return "#faad14";
            case 3:
                return "#fa8c16";
            case 4:
                return "#f5222d";
            default:
                return "#999999";
        }
    }
}
