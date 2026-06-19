package com.airport.fod.service;

import com.airport.fod.common.PageQuery;
import com.airport.fod.common.Result;
import com.airport.fod.dto.*;
import com.airport.fod.entity.FodEvent;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface FodEventService extends IService<FodEvent> {

    Result<Long> reportEvent(EventReportDTO dto);

    Result<FodEvent> getEventDetail(Long id);

    Result<Page<FodEvent>> getEventPage(PageQuery pageQuery, Map<String, Object> params);

    Result<List<FodEvent>> getEventList(Map<String, Object> params);

    Result<FodEvent> evaluateEvent(EventEvaluateDTO dto, String operatorRole);

    Result<FodEvent> startHandle(Long eventId, String handlerId, String handlerName, String operatorRole);

    Result<FodEvent> completeHandle(EventHandleDTO dto, String operatorRole);

    Result<FodEvent> closeEvent(EventCloseDTO dto, String operatorRole);

    Result<FodEvent> cancelEvent(Long eventId, String operatorId, String operatorName, String operatorRole);

    Result<FodEvent> updateRiskLevel(Long eventId, Integer riskLevel, String operatorId, String operatorName, String operatorRole);

    Result<Map<String, Integer>> getStatistics();

    void updatePhotoCount(Long eventId);

    Result<List<FodEvent>> getMergedChildEvents(Long parentEventId);

    Result<FodEvent> getMergedParentEvent(Long childEventId);
}
