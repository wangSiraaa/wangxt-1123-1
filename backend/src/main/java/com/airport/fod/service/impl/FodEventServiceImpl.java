package com.airport.fod.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.airport.fod.common.PageQuery;
import com.airport.fod.common.Result;
import com.airport.fod.constant.BusinessRules;
import com.airport.fod.constant.FodConstants;
import com.airport.fod.dto.*;
import com.airport.fod.entity.FodEvent;
import com.airport.fod.entity.FodEventLog;
import com.airport.fod.entity.FodEventMerge;
import com.airport.fod.entity.FodPhoto;
import com.airport.fod.entity.Runway;
import com.airport.fod.enums.EventStatusEnum;
import com.airport.fod.enums.RoleEnum;
import com.airport.fod.mapper.FodEventMapper;
import com.airport.fod.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FodEventServiceImpl extends ServiceImpl<FodEventMapper, FodEvent> implements FodEventService {

    @Autowired
    private RunwayService runwayService;

    @Autowired
    private FodPhotoService photoService;

    @Autowired
    private FodClearanceService clearanceService;

    @Autowired
    private FodEventLogService eventLogService;

    @Autowired
    private FodEventMergeService eventMergeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> reportEvent(EventReportDTO dto) {
        Runway runway = runwayService.getById(dto.getRunwayId());
        if (runway == null) {
            return Result.error("跑道不存在");
        }

        FodEvent existingEvent = baseMapper.findSameLocationToday(dto.getRunwayId(), dto.getLocation());
        if (existingEvent != null && BusinessRules.canMergeEvent(existingEvent.getStatus())) {
            return mergeIntoExistingEvent(existingEvent, dto);
        }

        FodEvent event = new FodEvent();
        event.setEventNo(FodConstants.EVENT_NO_PREFIX + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
            + StrUtil.fillBefore(String.valueOf((int) (Math.random() * 10000)), '0', 4));
        event.setRunwayId(dto.getRunwayId());
        event.setRunwayCode(runway.getRunwayCode());
        event.setLocation(dto.getLocation());
        event.setLocationPoint(dto.getLocationPoint());
        event.setFodType(dto.getFodType());
        event.setFodSize(dto.getFodSize());
        event.setDescription(dto.getDescription());
        event.setStatus(EventStatusEnum.REPORTED.getCode());
        event.setRiskLevelLocked(0);
        event.setIsTop(0);
        event.setHasPhoto(0);
        event.setPhotoCount(0);
        event.setMergeCount(0);
        event.setMergedParentId(null);
        event.setReporterId(dto.getReporterId() != null ? dto.getReporterId() : FodConstants.DEFAULT_USER_ID);
        event.setReporterName(dto.getReporterName() != null ? dto.getReporterName() : FodConstants.DEFAULT_USER_NAME);
        event.setReportTime(LocalDateTime.now());

        save(event);

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_REPORT,
            event.getReporterId(), event.getReporterName(), RoleEnum.FIELD_INSPECTOR.getCode(),
            null, event.getStatus(), null, null, "上报异物事件");

        return Result.success("上报成功", event.getId());
    }

    private Result<Long> mergeIntoExistingEvent(FodEvent existingEvent, EventReportDTO dto) {
        FodEvent newEvent = new FodEvent();
        newEvent.setEventNo(FodConstants.EVENT_NO_PREFIX + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
            + StrUtil.fillBefore(String.valueOf((int) (Math.random() * 10000)), '0', 4));
        newEvent.setRunwayId(dto.getRunwayId());
        newEvent.setRunwayCode(existingEvent.getRunwayCode());
        newEvent.setLocation(dto.getLocation());
        newEvent.setLocationPoint(dto.getLocationPoint());
        newEvent.setFodType(dto.getFodType());
        newEvent.setFodSize(dto.getFodSize());
        newEvent.setDescription(dto.getDescription());
        newEvent.setStatus(EventStatusEnum.CANCELLED.getCode());
        newEvent.setRiskLevelLocked(0);
        newEvent.setIsTop(0);
        newEvent.setHasPhoto(0);
        newEvent.setPhotoCount(0);
        newEvent.setMergeCount(0);
        newEvent.setMergedParentId(existingEvent.getId());
        newEvent.setReporterId(dto.getReporterId() != null ? dto.getReporterId() : FodConstants.DEFAULT_USER_ID);
        newEvent.setReporterName(dto.getReporterName() != null ? dto.getReporterName() : FodConstants.DEFAULT_USER_NAME);
        newEvent.setReportTime(LocalDateTime.now());

        save(newEvent);

        existingEvent.setMergeCount(existingEvent.getMergeCount() != null ? existingEvent.getMergeCount() + 1 : 1);
        if (dto.getDescription() != null) {
            String mergedDesc = existingEvent.getDescription() != null
                ? existingEvent.getDescription() + "；[合并追加]" + dto.getDescription()
                : "[合并追加]" + dto.getDescription();
            existingEvent.setDescription(mergedDesc);
        }
        updateById(existingEvent);

        FodEventMerge mergeRecord = new FodEventMerge();
        mergeRecord.setParentEventId(existingEvent.getId());
        mergeRecord.setChildEventId(newEvent.getId());
        mergeRecord.setChildEventNo(newEvent.getEventNo());
        mergeRecord.setMergeTime(LocalDateTime.now());
        mergeRecord.setMergeReason("同一位置当日重复上报，自动合并");
        mergeRecord.setOperatorId(FodConstants.DEFAULT_USER_ID);
        mergeRecord.setOperatorName(FodConstants.DEFAULT_USER_NAME);
        eventMergeService.save(mergeRecord);

        eventLogService.saveLog(newEvent.getId(), newEvent.getEventNo(), FodConstants.OPERATION_MERGE,
            newEvent.getReporterId(), newEvent.getReporterName(), RoleEnum.FIELD_INSPECTOR.getCode(),
            null, newEvent.getStatus(), null, null,
            "同一位置重复上报，合并到事件" + existingEvent.getEventNo());

        eventLogService.saveLog(existingEvent.getId(), existingEvent.getEventNo(), FodConstants.OPERATION_MERGE,
            FodConstants.DEFAULT_USER_ID, FodConstants.DEFAULT_USER_NAME, null,
            null, null, null, null,
            "合并子事件" + newEvent.getEventNo() + "，累计合并" + existingEvent.getMergeCount() + "次");

        return Result.success("同一位置当日已有上报，已自动合并到事件" + existingEvent.getEventNo(), existingEvent.getId());
    }

    @Override
    public Result<FodEvent> getEventDetail(Long id) {
        FodEvent event = getById(id);
        if (event == null) {
            return Result.error("事件不存在");
        }
        return Result.success(event);
    }

    @Override
    public Result<Page<FodEvent>> getEventPage(PageQuery pageQuery, Map<String, Object> params) {
        LambdaQueryWrapper<FodEvent> wrapper = new LambdaQueryWrapper<>();

        if (params != null) {
            if (params.get("status") != null) {
                wrapper.eq(FodEvent::getStatus, params.get("status"));
            }
            if (params.get("runwayId") != null) {
                wrapper.eq(FodEvent::getRunwayId, params.get("runwayId"));
            }
            if (params.get("isTop") != null) {
                wrapper.eq(FodEvent::getIsTop, params.get("isTop"));
            }
            if (params.get("riskLevel") != null) {
                wrapper.eq(FodEvent::getRiskLevel, params.get("riskLevel"));
            }
            if (params.get("keyword") != null) {
                String keyword = "%" + params.get("keyword") + "%";
                wrapper.and(w -> w.like(FodEvent::getEventNo, keyword)
                    .or().like(FodEvent::getLocation, keyword)
                    .or().like(FodEvent::getDescription, keyword));
            }
        }

        wrapper.orderByDesc(FodEvent::getIsTop)
            .orderByDesc(FodEvent::getCreateTime);

        Page<FodEvent> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page(page, wrapper);

        return Result.success(page);
    }

    @Override
    public Result<List<FodEvent>> getEventList(Map<String, Object> params) {
        LambdaQueryWrapper<FodEvent> wrapper = new LambdaQueryWrapper<>();

        if (params != null) {
            if (params.get("status") != null) {
                wrapper.eq(FodEvent::getStatus, params.get("status"));
            }
            if (params.get("runwayId") != null) {
                wrapper.eq(FodEvent::getRunwayId, params.get("runwayId"));
            }
        }

        wrapper.orderByDesc(FodEvent::getIsTop)
            .orderByDesc(FodEvent::getCreateTime);

        return Result.success(list(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodEvent> evaluateEvent(EventEvaluateDTO dto, String operatorRole) {
        FodEvent event = getById(dto.getEventId());
        if (event == null) {
            return Result.error("事件不存在");
        }

        if (!BusinessRules.canOperateStatus(operatorRole, event.getStatus())) {
            return Result.error("当前角色无权限执行此操作");
        }

        Integer beforeStatus = event.getStatus();
        Integer beforeRiskLevel = event.getRiskLevel();

        event.setRiskLevel(dto.getRiskLevel());
        event.setAffectTakeoff(dto.getAffectTakeoff());
        event.setEvaluateOpinion(dto.getEvaluateOpinion());
        event.setEvaluatorId(dto.getEvaluatorId() != null ? dto.getEvaluatorId() : FodConstants.DEFAULT_USER_ID);
        event.setEvaluatorName(dto.getEvaluatorName() != null ? dto.getEvaluatorName() : FodConstants.DEFAULT_USER_NAME);
        event.setEvaluateTime(LocalDateTime.now());

        Integer newStatus = dto.getAffectTakeoff() == 1
            ? EventStatusEnum.AFFECT.getCode()
            : EventStatusEnum.NOT_AFFECT.getCode();

        if (!BusinessRules.isValidStatusTransition(event.getStatus(), newStatus)) {
            return Result.error("无效的状态流转");
        }

        event.setStatus(newStatus);
        event.setRiskLevelLocked(0);

        boolean isTop = BusinessRules.isTopStatus(newStatus);
        event.setIsTop(isTop ? 1 : 0);

        updateById(event);

        if (BusinessRules.shouldRestrictRunway(newStatus)) {
            clearanceService.restrictRunway(event.getRunwayId(), event.getId(), event.getEventNo(),
                event.getEvaluatorId(), event.getEvaluatorName(), "异物影响起降，跑道切换为受限状态");
        }

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_EVALUATE,
            event.getEvaluatorId(), event.getEvaluatorName(), operatorRole,
            beforeStatus, event.getStatus(), beforeRiskLevel, event.getRiskLevel(),
            "评估完成，风险等级：" + dto.getRiskLevel() + "，" + (dto.getAffectTakeoff() == 1 ? "影响起降" : "不影响起降"));

        return Result.success("评估完成", event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodEvent> startHandle(Long eventId, String handlerId, String handlerName, String operatorRole) {
        FodEvent event = getById(eventId);
        if (event == null) {
            return Result.error("事件不存在");
        }

        if (!BusinessRules.canOperateStatus(operatorRole, event.getStatus())) {
            return Result.error("当前角色无权限执行此操作");
        }

        Integer beforeStatus = event.getStatus();
        Integer newStatus = EventStatusEnum.HANDLING.getCode();

        if (!BusinessRules.isValidStatusTransition(event.getStatus(), newStatus)) {
            return Result.error("无效的状态流转");
        }

        event.setStatus(newStatus);
        event.setHandlerId(handlerId != null ? handlerId : FodConstants.DEFAULT_USER_ID);
        event.setHandlerName(handlerName != null ? handlerName : FodConstants.DEFAULT_USER_NAME);
        event.setHandleStartTime(LocalDateTime.now());

        updateById(event);

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_HANDLE,
            event.getHandlerId(), event.getHandlerName(), operatorRole,
            beforeStatus, event.getStatus(), null, null, "开始处理跑道异物");

        return Result.success("已开始处理", event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodEvent> completeHandle(EventHandleDTO dto, String operatorRole) {
        FodEvent event = getById(dto.getEventId());
        if (event == null) {
            return Result.error("事件不存在");
        }

        if (!BusinessRules.canOperateStatus(operatorRole, event.getStatus())) {
            return Result.error("当前角色无权限执行此操作");
        }

        Integer beforeStatus = event.getStatus();
        Integer newStatus = EventStatusEnum.PENDING_CLOSE.getCode();

        if (!BusinessRules.isValidStatusTransition(event.getStatus(), newStatus)) {
            return Result.error("无效的状态流转");
        }

        event.setStatus(newStatus);
        event.setHandleResult(dto.getHandleResult());
        event.setHandlerId(dto.getHandlerId() != null ? dto.getHandlerId() : FodConstants.DEFAULT_USER_ID);
        event.setHandlerName(dto.getHandlerName() != null ? dto.getHandlerName() : FodConstants.DEFAULT_USER_NAME);
        event.setHandleEndTime(LocalDateTime.now());
        event.setEstimatedRecoveryTime(dto.getEstimatedRecoveryTime());

        updateById(event);

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_HANDLE,
            event.getHandlerId(), event.getHandlerName(), operatorRole,
            beforeStatus, event.getStatus(), null, null, "处理完成，结果：" + dto.getHandleResult());

        return Result.success("处理完成", event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodEvent> closeEvent(EventCloseDTO dto, String operatorRole) {
        FodEvent event = getById(dto.getEventId());
        if (event == null) {
            return Result.error("事件不存在");
        }

        if (!BusinessRules.canOperateStatus(operatorRole, event.getStatus())) {
            return Result.error("当前角色无权限执行此操作");
        }

        if (!BusinessRules.canClose(event.getHasPhoto() == 1)) {
            return Result.error("缺少现场照片，不能关闭事件");
        }

        Integer beforeStatus = event.getStatus();
        Integer newStatus = EventStatusEnum.CLOSED.getCode();

        if (!BusinessRules.isValidStatusTransition(event.getStatus(), newStatus)) {
            return Result.error("无效的状态流转");
        }

        event.setStatus(newStatus);
        event.setCloseOpinion(dto.getCloseOpinion());
        event.setCloserId(dto.getCloserId() != null ? dto.getCloserId() : FodConstants.DEFAULT_USER_ID);
        event.setCloserName(dto.getCloserName() != null ? dto.getCloserName() : FodConstants.DEFAULT_USER_NAME);
        event.setCloseTime(LocalDateTime.now());
        event.setIsTop(0);
        event.setRiskLevelLocked(1);

        updateById(event);

        if (event.getAffectTakeoff() != null && event.getAffectTakeoff() == 1) {
            clearanceService.unrestrictRunway(event.getRunwayId(), event.getId(), event.getEventNo(),
                event.getCloserId(), event.getCloserName(), "事件已关闭，解除跑道限制");
        }

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_CLOSE,
            event.getCloserId(), event.getCloserName(), operatorRole,
            beforeStatus, event.getStatus(), null, null, "关闭事件");

        return Result.success("事件已关闭", event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodEvent> cancelEvent(Long eventId, String operatorId, String operatorName, String operatorRole) {
        FodEvent event = getById(eventId);
        if (event == null) {
            return Result.error("事件不存在");
        }

        if (!BusinessRules.canOperateStatus(operatorRole, event.getStatus())) {
            return Result.error("当前角色无权限执行此操作");
        }

        Integer beforeStatus = event.getStatus();
        Integer newStatus = EventStatusEnum.CANCELLED.getCode();

        if (!BusinessRules.isValidStatusTransition(event.getStatus(), newStatus)) {
            return Result.error("无效的状态流转，当前状态不允许取消");
        }

        event.setStatus(newStatus);
        event.setIsTop(0);

        updateById(event);

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_CANCEL,
            operatorId != null ? operatorId : FodConstants.DEFAULT_USER_ID,
            operatorName != null ? operatorName : FodConstants.DEFAULT_USER_NAME,
            operatorRole, beforeStatus, event.getStatus(), null, null, "取消事件");

        return Result.success("事件已取消", event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodEvent> updateRiskLevel(Long eventId, Integer riskLevel, String operatorId, String operatorName, String operatorRole) {
        FodEvent event = getById(eventId);
        if (event == null) {
            return Result.error("事件不存在");
        }

        if (!BusinessRules.canChangeRiskLevel(event.getStatus(), event.getRiskLevelLocked() == 1, operatorRole)) {
            return Result.error("塔台已放行或当前状态不允许修改风险等级");
        }

        Integer beforeRiskLevel = event.getRiskLevel();
        event.setRiskLevel(riskLevel);
        updateById(event);

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_UPDATE,
            operatorId != null ? operatorId : FodConstants.DEFAULT_USER_ID,
            operatorName != null ? operatorName : FodConstants.DEFAULT_USER_NAME,
            operatorRole, null, null, beforeRiskLevel, riskLevel, "修改风险等级");

        return Result.success("风险等级已更新", event);
    }

    @Override
    public Result<Map<String, Integer>> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", (int) count());
        stats.put("top", baseMapper.countTopEvents());
        stats.put("reported", baseMapper.countByStatus(EventStatusEnum.REPORTED.getCode()));
        stats.put("evaluating", baseMapper.countByStatus(EventStatusEnum.EVALUATING.getCode()));
        stats.put("affect", baseMapper.countByStatus(EventStatusEnum.AFFECT.getCode()));
        stats.put("handling", baseMapper.countByStatus(EventStatusEnum.HANDLING.getCode()));
        stats.put("pendingClose", baseMapper.countByStatus(EventStatusEnum.PENDING_CLOSE.getCode()));
        stats.put("closed", baseMapper.countByStatus(EventStatusEnum.CLOSED.getCode()));
        return Result.success(stats);
    }

    @Override
    public void updatePhotoCount(Long eventId) {
        LambdaQueryWrapper<FodPhoto> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodPhoto::getEventId, eventId);
        long countLong = photoService.count(wrapper);
        Integer count = (int) countLong;

        FodEvent event = new FodEvent();
        event.setId(eventId);
        event.setPhotoCount(count);
        event.setHasPhoto(count > 0 ? 1 : 0);
        updateById(event);
    }

    @Override
    public Result<List<FodEvent>> getMergedChildEvents(Long parentEventId) {
        List<FodEventMerge> mergeRecords = eventMergeService.getByParentEventId(parentEventId);
        if (mergeRecords.isEmpty()) {
            return Result.success(java.util.Collections.emptyList());
        }
        List<Long> childIds = new java.util.ArrayList<>();
        for (FodEventMerge merge : mergeRecords) {
            childIds.add(merge.getChildEventId());
        }
        return Result.success(listByIds(childIds));
    }

    @Override
    public Result<FodEvent> getMergedParentEvent(Long childEventId) {
        List<FodEventMerge> mergeRecords = eventMergeService.getByChildEventId(childEventId);
        if (mergeRecords.isEmpty()) {
            return Result.error("该事件未被合并");
        }
        Long parentEventId = mergeRecords.get(0).getParentEventId();
        FodEvent parent = getById(parentEventId);
        if (parent == null) {
            return Result.error("父事件不存在");
        }
        return Result.success(parent);
    }
}
