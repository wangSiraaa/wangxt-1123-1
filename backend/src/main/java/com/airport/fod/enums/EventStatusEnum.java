package com.airport.fod.enums;

public enum EventStatusEnum {

    PENDING_REPORT(1, "待上报"),
    REPORTED(2, "已上报待评估"),
    EVALUATING(3, "评估中"),
    NOT_AFFECT(4, "不影响起降"),
    AFFECT(5, "影响起降"),
    HANDLING(6, "处理中"),
    PENDING_CLOSE(7, "待关闭"),
    CLOSED(8, "已关闭"),
    CANCELLED(9, "已取消");

    private final Integer code;
    private final String desc;

    EventStatusEnum(Integer code, String desc) {
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
        for (EventStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知状态";
    }

    public static EventStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EventStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
