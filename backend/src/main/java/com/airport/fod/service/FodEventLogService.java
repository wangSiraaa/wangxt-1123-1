package com.airport.fod.service;

import com.airport.fod.common.Result;
import com.airport.fod.entity.FodEventLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FodEventLogService extends IService<FodEventLog> {

    Result<List<FodEventLog>> getLogsByEventId(Long eventId);

    void saveLog(Long eventId, String eventNo, String operationType,
                 String operatorId, String operatorName, String operatorRole,
                 Integer beforeStatus, Integer afterStatus,
                 Integer beforeRiskLevel, Integer afterRiskLevel,
                 String content);
}
