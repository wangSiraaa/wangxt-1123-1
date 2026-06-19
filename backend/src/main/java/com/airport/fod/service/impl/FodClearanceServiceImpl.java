package com.airport.fod.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.airport.fod.common.Result;
import com.airport.fod.constant.BusinessRules;
import com.airport.fod.constant.FodConstants;
import com.airport.fod.dto.ClearanceOperationDTO;
import com.airport.fod.entity.FodClearance;
import com.airport.fod.entity.FodEvent;
import com.airport.fod.entity.Runway;
import com.airport.fod.enums.ClearanceOperationEnum;
import com.airport.fod.enums.RoleEnum;
import com.airport.fod.enums.RunwayStatusEnum;
import com.airport.fod.mapper.FodClearanceMapper;
import com.airport.fod.service.FodClearanceService;
import com.airport.fod.service.FodEventService;
import com.airport.fod.service.RunwayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FodClearanceServiceImpl extends ServiceImpl<FodClearanceMapper, FodClearance> implements FodClearanceService {

    @Autowired
    private RunwayService runwayService;

    @Autowired
    private FodEventService eventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodClearance> operateClearance(ClearanceOperationDTO dto, String operatorRole) {
        if (!RoleEnum.TOWER_CONTROLLER.getCode().equals(operatorRole)) {
            return Result.error("只有塔台协调员可以执行放行操作");
        }

        FodEvent event = eventService.getById(dto.getEventId());
        if (event == null) {
            return Result.error("事件不存在");
        }

        Runway runway = runwayService.getById(event.getRunwayId());
        if (runway == null) {
            return Result.error("跑道不存在");
        }

        Integer beforeStatus = runway.getStatus();
        FodClearance clearance = createClearance(dto, event, runway, beforeStatus, operatorRole);

        if (ClearanceOperationEnum.FREEZE_RUNWAY.getCode().equals(dto.getOperationType())) {
            updateRunwayStatus(runway, RunwayStatusEnum.FROZEN.getCode(), dto.getReason(),
                dto.getOperatorId(), dto.getOperatorName());
            event.setRiskLevelLocked(1);
            eventService.updateById(event);
        } else if (ClearanceOperationEnum.UNFREEZE_RUNWAY.getCode().equals(dto.getOperationType())) {
            updateRunwayStatus(runway, RunwayStatusEnum.NORMAL.getCode(), null, null, null);
        }

        clearance.setAfterStatus(runway.getStatus());
        save(clearance);

        return Result.success("操作成功", clearance);
    }

    @Override
    public Result<List<FodClearance>> getClearanceByEventId(Long eventId) {
        LambdaQueryWrapper<FodClearance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodClearance::getEventId, eventId)
            .orderByDesc(FodClearance::getOperateTime);
        return Result.success(list(wrapper));
    }

    @Override
    public Result<List<FodClearance>> getClearanceByRunwayId(Long runwayId) {
        LambdaQueryWrapper<FodClearance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodClearance::getRunwayId, runwayId)
            .orderByDesc(FodClearance::getOperateTime);
        return Result.success(list(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeRunway(Long runwayId, Long eventId, String eventNo, String operatorId, String operatorName, String reason) {
        Runway runway = runwayService.getById(runwayId);
        if (runway == null) {
            return;
        }

        Integer beforeStatus = runway.getStatus();

        FodClearance clearance = new FodClearance();
        clearance.setEventId(eventId);
        clearance.setEventNo(eventNo);
        clearance.setRunwayId(runwayId);
        clearance.setRunwayCode(runway.getRunwayCode());
        clearance.setClearanceNo(FodConstants.CLEARANCE_NO_PREFIX + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
            + StrUtil.fillBefore(String.valueOf((int) (Math.random() * 10000)), '0', 4));
        clearance.setOperationType(ClearanceOperationEnum.FREEZE_RUNWAY.getCode());
        clearance.setOperatorId(operatorId);
        clearance.setOperatorName(operatorName);
        clearance.setOperateTime(LocalDateTime.now());
        clearance.setReason(reason);
        clearance.setBeforeStatus(beforeStatus);
        clearance.setAfterStatus(RunwayStatusEnum.FROZEN.getCode());

        updateRunwayStatus(runway, RunwayStatusEnum.FROZEN.getCode(), reason, operatorId, operatorName);
        clearance.setAfterStatus(RunwayStatusEnum.FROZEN.getCode());

        save(clearance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfreezeRunway(Long runwayId, Long eventId, String eventNo, String operatorId, String operatorName, String reason) {
        Runway runway = runwayService.getById(runwayId);
        if (runway == null) {
            return;
        }

        LambdaQueryWrapper<FodClearance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodClearance::getRunwayId, runwayId)
            .eq(FodClearance::getOperationType, ClearanceOperationEnum.FREEZE_RUNWAY.getCode())
            .orderByDesc(FodClearance::getOperateTime)
            .last("LIMIT 1");
        FodClearance lastFreeze = getOne(wrapper);
        if (lastFreeze != null && !lastFreeze.getEventId().equals(eventId)) {
            return;
        }

        Integer beforeStatus = runway.getStatus();

        FodClearance clearance = new FodClearance();
        clearance.setEventId(eventId);
        clearance.setEventNo(eventNo);
        clearance.setRunwayId(runwayId);
        clearance.setRunwayCode(runway.getRunwayCode());
        clearance.setClearanceNo(FodConstants.CLEARANCE_NO_PREFIX + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
            + StrUtil.fillBefore(String.valueOf((int) (Math.random() * 10000)), '0', 4));
        clearance.setOperationType(ClearanceOperationEnum.UNFREEZE_RUNWAY.getCode());
        clearance.setOperatorId(operatorId);
        clearance.setOperatorName(operatorName);
        clearance.setOperateTime(LocalDateTime.now());
        clearance.setReason(reason);
        clearance.setBeforeStatus(beforeStatus);
        clearance.setAfterStatus(RunwayStatusEnum.NORMAL.getCode());

        updateRunwayStatus(runway, RunwayStatusEnum.NORMAL.getCode(), null, null, null);
        clearance.setAfterStatus(RunwayStatusEnum.NORMAL.getCode());

        save(clearance);
    }

    private FodClearance createClearance(ClearanceOperationDTO dto, FodEvent event, Runway runway,
                                          Integer beforeStatus, String operatorRole) {
        FodClearance clearance = new FodClearance();
        clearance.setEventId(dto.getEventId());
        clearance.setEventNo(event.getEventNo());
        clearance.setRunwayId(event.getRunwayId());
        clearance.setRunwayCode(runway.getRunwayCode());
        clearance.setClearanceNo(FodConstants.CLEARANCE_NO_PREFIX + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
            + StrUtil.fillBefore(String.valueOf((int) (Math.random() * 10000)), '0', 4));
        clearance.setOperationType(dto.getOperationType());
        clearance.setOperatorId(dto.getOperatorId() != null ? dto.getOperatorId() : FodConstants.DEFAULT_USER_ID);
        clearance.setOperatorName(dto.getOperatorName() != null ? dto.getOperatorName() : FodConstants.DEFAULT_USER_NAME);
        clearance.setOperateTime(LocalDateTime.now());
        clearance.setReason(dto.getReason());
        clearance.setBeforeStatus(beforeStatus);
        clearance.setRemark(dto.getRemark());
        return clearance;
    }

    private void updateRunwayStatus(Runway runway, Integer status, String freezeReason,
                                     String freezeOperator, String freezeOperatorName) {
        runway.setStatus(status);
        if (RunwayStatusEnum.FROZEN.getCode().equals(status)) {
            runway.setIsFrozen(1);
            runway.setFreezeReason(freezeReason);
            runway.setFreezeTime(LocalDateTime.now());
            runway.setFreezeOperator(freezeOperatorName);
        } else {
            runway.setIsFrozen(0);
            runway.setFreezeReason(null);
            runway.setFreezeTime(null);
            runway.setFreezeOperator(null);
        }
        runwayService.updateById(runway);
    }
}
