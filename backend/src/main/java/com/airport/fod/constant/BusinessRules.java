package com.airport.fod.constant;

import com.airport.fod.enums.EventStatusEnum;
import com.airport.fod.enums.RoleEnum;

import java.util.*;

public class BusinessRules {

    private BusinessRules() {}

    private static final Map<Integer, List<Integer>> STATUS_TRANSITION_MAP = new HashMap<>();

    static {
        STATUS_TRANSITION_MAP.put(EventStatusEnum.PENDING_REPORT.getCode(),
            Arrays.asList(EventStatusEnum.REPORTED.getCode(), EventStatusEnum.CANCELLED.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.REPORTED.getCode(),
            Arrays.asList(EventStatusEnum.EVALUATING.getCode(), EventStatusEnum.CANCELLED.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.EVALUATING.getCode(),
            Arrays.asList(EventStatusEnum.NOT_AFFECT.getCode(), EventStatusEnum.AFFECT.getCode(),
                EventStatusEnum.CANCELLED.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.NOT_AFFECT.getCode(),
            Arrays.asList(EventStatusEnum.HANDLING.getCode(), EventStatusEnum.PENDING_CLOSE.getCode(),
                EventStatusEnum.CANCELLED.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.AFFECT.getCode(),
            Arrays.asList(EventStatusEnum.HANDLING.getCode(), EventStatusEnum.CANCELLED.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.HANDLING.getCode(),
            Arrays.asList(EventStatusEnum.PENDING_CLOSE.getCode(), EventStatusEnum.NOT_AFFECT.getCode(),
                EventStatusEnum.AFFECT.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.PENDING_CLOSE.getCode(),
            Arrays.asList(EventStatusEnum.CLOSED.getCode(), EventStatusEnum.HANDLING.getCode()));

        STATUS_TRANSITION_MAP.put(EventStatusEnum.CLOSED.getCode(), Collections.emptyList());
        STATUS_TRANSITION_MAP.put(EventStatusEnum.CANCELLED.getCode(), Collections.emptyList());
    }

    private static final Map<String, List<Integer>> ROLE_ALLOWED_STATUSES = new HashMap<>();

    static {
        ROLE_ALLOWED_STATUSES.put(RoleEnum.FIELD_INSPECTOR.getCode(),
            Arrays.asList(
                EventStatusEnum.PENDING_REPORT.getCode(),
                EventStatusEnum.REPORTED.getCode(),
                EventStatusEnum.HANDLING.getCode()
            ));

        ROLE_ALLOWED_STATUSES.put(RoleEnum.TOWER_CONTROLLER.getCode(),
            Arrays.asList(
                EventStatusEnum.REPORTED.getCode(),
                EventStatusEnum.EVALUATING.getCode(),
                EventStatusEnum.NOT_AFFECT.getCode(),
                EventStatusEnum.AFFECT.getCode(),
                EventStatusEnum.HANDLING.getCode(),
                EventStatusEnum.PENDING_CLOSE.getCode(),
                EventStatusEnum.CLOSED.getCode()
            ));

        ROLE_ALLOWED_STATUSES.put(RoleEnum.MAINTENANCE_TEAM.getCode(),
            Arrays.asList(
                EventStatusEnum.NOT_AFFECT.getCode(),
                EventStatusEnum.AFFECT.getCode(),
                EventStatusEnum.HANDLING.getCode(),
                EventStatusEnum.PENDING_CLOSE.getCode()
            ));
    }

    public static boolean isValidStatusTransition(Integer fromStatus, Integer toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }
        List<Integer> allowedTransitions = STATUS_TRANSITION_MAP.get(fromStatus);
        return allowedTransitions != null && allowedTransitions.contains(toStatus);
    }

    public static boolean canOperateStatus(String role, Integer status) {
        if (role == null || status == null) {
            return false;
        }
        List<Integer> allowedStatuses = ROLE_ALLOWED_STATUSES.get(role);
        return allowedStatuses != null && allowedStatuses.contains(status);
    }

    public static boolean canChangeRiskLevel(Integer eventStatus, boolean riskLevelLocked, String role) {
        if (!RoleEnum.TOWER_CONTROLLER.getCode().equals(role)) {
            return false;
        }
        if (riskLevelLocked) {
            return false;
        }
        return EventStatusEnum.REPORTED.getCode().equals(eventStatus)
            || EventStatusEnum.EVALUATING.getCode().equals(eventStatus);
    }

    public static boolean canClose(boolean hasPhoto) {
        return hasPhoto;
    }

    public static boolean isTopStatus(Integer status) {
        return EventStatusEnum.AFFECT.getCode().equals(status);
    }

    public static boolean shouldFreezeRunway(Integer status) {
        return EventStatusEnum.AFFECT.getCode().equals(status);
    }

    public static List<Integer> getValidTransitions(Integer fromStatus) {
        List<Integer> transitions = STATUS_TRANSITION_MAP.get(fromStatus);
        return transitions != null ? transitions : Collections.emptyList();
    }
}
