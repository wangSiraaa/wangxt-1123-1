package com.airport.fod.enums;

public enum ClearanceOperationEnum {

    FREEZE_RUNWAY(1, "冻结跑道"),
    UNFREEZE_RUNWAY(2, "解除冻结"),
    ALLOW_CLEARANCE(3, "允许放行"),
    DENY_CLEARANCE(4, "禁止放行"),
    RESTRICT_RUNWAY(5, "限制跑道"),
    UNRESTRICT_RUNWAY(6, "解除限制");

    private final Integer code;
    private final String desc;

    ClearanceOperationEnum(Integer code, String desc) {
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
        for (ClearanceOperationEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知操作";
    }
}
