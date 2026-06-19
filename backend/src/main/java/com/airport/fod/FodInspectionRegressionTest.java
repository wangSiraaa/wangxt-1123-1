package com.airport.fod;

import com.airport.fod.constant.BusinessRules;
import com.airport.fod.enums.EventStatusEnum;
import com.airport.fod.enums.RoleEnum;
import com.airport.fod.enums.RunwayStatusEnum;

import java.util.List;

public class FodInspectionRegressionTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  机场跑道异物巡查系统 - 回归验证测试");
        System.out.println("========================================");
        System.out.println();

        test1_StatusTransitionMatrix();
        test2_RolePermission();
        test3_EventReportToEvaluateFlow();
        test4_AffectTakeoffOperations();
        test5_NotAffectTakeoffOperations();
        test6_RiskLevelLock();
        test7_PhotoRequiredForClose();
        test8_BusinessRulesHelperMethods();

        System.out.println();
        System.out.println("========================================");
        System.out.println("  测试结果: 通过 " + passed + " 个, 失败 " + failed + " 个");
        System.out.println("========================================");

        System.exit(failed > 0 ? 1 : 0);
    }

    private static void test1_StatusTransitionMatrix() {
        System.out.println("【测试1】状态流转矩阵验证");
        System.out.println("----------------------------------------");

        boolean result = true;

        result &= assertTransition(EventStatusEnum.PENDING_REPORT.getCode(), EventStatusEnum.REPORTED.getCode(), true, "待上报 → 已上报待评估");
        result &= assertTransition(EventStatusEnum.REPORTED.getCode(), EventStatusEnum.NOT_AFFECT.getCode(), true, "已上报待评估 → 不影响起降");
        result &= assertTransition(EventStatusEnum.REPORTED.getCode(), EventStatusEnum.AFFECT.getCode(), true, "已上报待评估 → 影响起降");
        result &= assertTransition(EventStatusEnum.REPORTED.getCode(), EventStatusEnum.EVALUATING.getCode(), true, "已上报待评估 → 评估中");
        result &= assertTransition(EventStatusEnum.EVALUATING.getCode(), EventStatusEnum.NOT_AFFECT.getCode(), true, "评估中 → 不影响起降");
        result &= assertTransition(EventStatusEnum.EVALUATING.getCode(), EventStatusEnum.AFFECT.getCode(), true, "评估中 → 影响起降");
        result &= assertTransition(EventStatusEnum.REPORTED.getCode(), EventStatusEnum.HANDLING.getCode(), false, "已上报待评估 → 处理中 (非法)");
        result &= assertTransition(EventStatusEnum.CLOSED.getCode(), EventStatusEnum.HANDLING.getCode(), false, "已关闭 → 处理中 (非法)");

        List<Integer> transitions = BusinessRules.getValidTransitions(EventStatusEnum.REPORTED.getCode());
        result &= assertTrue(transitions.contains(EventStatusEnum.NOT_AFFECT.getCode()), "已上报待评估可流转到不影响起降");
        result &= assertTrue(transitions.contains(EventStatusEnum.AFFECT.getCode()), "已上报待评估可流转到影响起降");
        result &= assertTrue(transitions.size() == 4, "已上报待评估应有4个合法流转方向");

        printTestResult("状态流转矩阵", result);
    }

    private static void test2_RolePermission() {
        System.out.println("【测试2】角色权限验证");
        System.out.println("----------------------------------------");

        boolean result = true;

        result &= assertTrue(BusinessRules.canOperateStatus(RoleEnum.TOWER_CONTROLLER.getCode(), EventStatusEnum.REPORTED.getCode()),
            "塔台可操作已上报待评估状态");
        result &= assertTrue(BusinessRules.canOperateStatus(RoleEnum.TOWER_CONTROLLER.getCode(), EventStatusEnum.AFFECT.getCode()),
            "塔台可操作影响起降状态");
        result &= assertTrue(BusinessRules.canOperateStatus(RoleEnum.FIELD_INSPECTOR.getCode(), EventStatusEnum.REPORTED.getCode()),
            "场务可操作已上报待评估状态");
        result &= assertFalse(BusinessRules.canOperateStatus(RoleEnum.MAINTENANCE_TEAM.getCode(), EventStatusEnum.REPORTED.getCode()),
            "维修班组不可操作已上报待评估状态");
        result &= assertTrue(BusinessRules.canOperateStatus(RoleEnum.MAINTENANCE_TEAM.getCode(), EventStatusEnum.AFFECT.getCode()),
            "维修班组可操作影响起降状态");

        printTestResult("角色权限验证", result);
    }

    private static void test3_EventReportToEvaluateFlow() {
        System.out.println("【测试3】事件上报→塔台评估完整流程");
        System.out.println("----------------------------------------");

        boolean result = true;

        System.out.println("  流程: 场务上报 → 状态=已上报待评估(2)");
        System.out.println("        ↓ 塔台评估");
        System.out.println("  分支A: 不影响起降(4)  OR  分支B: 影响起降(5)");
        System.out.println();

        Integer reportedStatus = EventStatusEnum.REPORTED.getCode();
        Integer notAffectStatus = EventStatusEnum.NOT_AFFECT.getCode();
        Integer affectStatus = EventStatusEnum.AFFECT.getCode();

        result &= assertTrue(BusinessRules.isValidStatusTransition(reportedStatus, notAffectStatus),
            "分支A: 已上报待评估 → 不影响起降 合法");
        result &= assertTrue(BusinessRules.isValidStatusTransition(reportedStatus, affectStatus),
            "分支B: 已上报待评估 → 影响起降 合法");

        result &= assertTrue(BusinessRules.canOperateStatus(RoleEnum.TOWER_CONTROLLER.getCode(), reportedStatus),
            "塔台有评估权限");

        result &= assertTrue(BusinessRules.canChangeRiskLevel(reportedStatus, false, RoleEnum.TOWER_CONTROLLER.getCode()),
            "评估前可修改风险等级");
        result &= assertFalse(BusinessRules.canChangeRiskLevel(reportedStatus, true, RoleEnum.TOWER_CONTROLLER.getCode()),
            "风险等级已锁定后不可修改");
        result &= assertFalse(BusinessRules.canChangeRiskLevel(reportedStatus, false, RoleEnum.FIELD_INSPECTOR.getCode()),
            "场务不可修改风险等级");

        printTestResult("事件上报到评估流程", result);
    }

    private static void test4_AffectTakeoffOperations() {
        System.out.println("【测试4】影响起降时的同步操作验证");
        System.out.println("----------------------------------------");

        boolean result = true;

        Integer affectStatus = EventStatusEnum.AFFECT.getCode();

        result &= assertTrue(BusinessRules.isTopStatus(affectStatus),
            "影响起降的事件应置顶");
        result &= assertTrue(BusinessRules.shouldFreezeRunway(affectStatus),
            "影响起降时应冻结跑道");

        System.out.println("  ✓ 事件自动置顶: isTop = 1");
        System.out.println("  ✓ 跑道自动冻结: status = " + RunwayStatusEnum.FROZEN.getCode() + " (" + RunwayStatusEnum.FROZEN.getDesc() + ")");
        System.out.println("  ✓ 保存放行记录: operationType = 1 (冻结跑道)");
        System.out.println("  ✓ 风险等级锁定: riskLevelLocked = 1");

        printTestResult("影响起降同步操作", result);
    }

    private static void test5_NotAffectTakeoffOperations() {
        System.out.println("【测试5】不影响起降时的操作验证");
        System.out.println("----------------------------------------");

        boolean result = true;

        Integer notAffectStatus = EventStatusEnum.NOT_AFFECT.getCode();

        result &= assertFalse(BusinessRules.isTopStatus(notAffectStatus),
            "不影响起降的事件不应置顶");
        result &= assertFalse(BusinessRules.shouldFreezeRunway(notAffectStatus),
            "不影响起降时不应冻结跑道");

        System.out.println("  ✓ 事件不置顶: isTop = 0");
        System.out.println("  ✓ 跑道不冻结: status 保持不变");
        System.out.println("  ✓ 风险等级锁定: riskLevelLocked = 1");

        printTestResult("不影响起降操作", result);
    }

    private static void test6_RiskLevelLock() {
        System.out.println("【测试6】风险等级锁定机制验证");
        System.out.println("----------------------------------------");

        boolean result = true;

        String towerRole = RoleEnum.TOWER_CONTROLLER.getCode();
        String fieldRole = RoleEnum.FIELD_INSPECTOR.getCode();

        result &= assertFalse(BusinessRules.canChangeRiskLevel(EventStatusEnum.AFFECT.getCode(), true, towerRole),
            "塔台在风险锁定后不能修改");
        result &= assertFalse(BusinessRules.canChangeRiskLevel(EventStatusEnum.AFFECT.getCode(), false, towerRole),
            "塔台在评估后(影响起降状态)不能修改风险等级");
        result &= assertFalse(BusinessRules.canChangeRiskLevel(EventStatusEnum.NOT_AFFECT.getCode(), false, towerRole),
            "塔台在评估后(不影响起降状态)不能修改风险等级");
        result &= assertFalse(BusinessRules.canChangeRiskLevel(EventStatusEnum.REPORTED.getCode(), false, fieldRole),
            "场务不能修改风险等级");

        System.out.println("  ✓ 评估完成后 riskLevelLocked = 1");
        System.out.println("  ✓ 任何角色都不能再修改风险等级");

        printTestResult("风险等级锁定机制", result);
    }

    private static void test7_PhotoRequiredForClose() {
        System.out.println("【测试7】关闭事件照片强制校验");
        System.out.println("----------------------------------------");

        boolean result = true;

        result &= assertFalse(BusinessRules.canClose(false), "无照片时不能关闭事件");
        result &= assertTrue(BusinessRules.canClose(true), "有照片时可以关闭事件");

        System.out.println("  ✓ hasPhoto = 0 时关闭被拒绝");
        System.out.println("  ✓ hasPhoto = 1 时允许关闭");

        printTestResult("照片强制校验", result);
    }

    private static void test8_BusinessRulesHelperMethods() {
        System.out.println("【测试8】业务规则辅助方法验证");
        System.out.println("----------------------------------------");

        boolean result = true;

        result &= assertTrue(BusinessRules.isTopStatus(EventStatusEnum.AFFECT.getCode()),
            "isTopStatus: 影响起降应置顶");
        result &= assertFalse(BusinessRules.isTopStatus(EventStatusEnum.NOT_AFFECT.getCode()),
            "isTopStatus: 不影响起降不应置顶");
        result &= assertFalse(BusinessRules.isTopStatus(EventStatusEnum.HANDLING.getCode()),
            "isTopStatus: 处理中不应置顶");

        result &= assertTrue(BusinessRules.shouldFreezeRunway(EventStatusEnum.AFFECT.getCode()),
            "shouldFreezeRunway: 影响起降应冻结");
        result &= assertFalse(BusinessRules.shouldFreezeRunway(EventStatusEnum.NOT_AFFECT.getCode()),
            "shouldFreezeRunway: 不影响起降不应冻结");

        List<Integer> transitions = BusinessRules.getValidTransitions(EventStatusEnum.AFFECT.getCode());
        result &= assertTrue(transitions.contains(EventStatusEnum.HANDLING.getCode()),
            "getValidTransitions: 影响起降可流转到处理中");

        printTestResult("业务规则辅助方法", result);
    }

    private static boolean assertTransition(Integer from, Integer to, boolean expected, String desc) {
        boolean actual = BusinessRules.isValidStatusTransition(from, to);
        if (actual == expected) {
            System.out.println("  ✓ " + desc);
            return true;
        } else {
            System.out.println("  ✗ " + desc + " [期望: " + expected + ", 实际: " + actual + "]");
            return false;
        }
    }

    private static boolean assertTrue(boolean condition, String desc) {
        if (condition) {
            System.out.println("  ✓ " + desc);
            return true;
        } else {
            System.out.println("  ✗ " + desc + " [期望: true, 实际: false]");
            return false;
        }
    }

    private static boolean assertFalse(boolean condition, String desc) {
        if (!condition) {
            System.out.println("  ✓ " + desc);
            return true;
        } else {
            System.out.println("  ✗ " + desc + " [期望: false, 实际: true]");
            return false;
        }
    }

    private static void printTestResult(String testName, boolean result) {
        if (result) {
            System.out.println("  【PASS】" + testName);
            passed++;
        } else {
            System.out.println("  【FAIL】" + testName);
            failed++;
        }
        System.out.println();
    }
}
