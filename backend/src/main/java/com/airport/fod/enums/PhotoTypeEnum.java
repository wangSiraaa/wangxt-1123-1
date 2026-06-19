package com.airport.fod.enums;

public enum PhotoTypeEnum {

    REPORT_PHOTO(1, "上报照片"),
    HANDLING_PHOTO(2, "处理中照片"),
    COMPLETED_PHOTO(3, "处理后照片");

    private final Integer code;
    private final String desc;

    PhotoTypeEnum(Integer code, String desc) {
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
        for (PhotoTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知类型";
    }
}
