package com.airport.fod.service.impl;

import com.airport.fod.common.Result;
import com.airport.fod.entity.FodEventLog;
import com.airport.fod.mapper.FodEventLogMapper;
import com.airport.fod.service.FodEventLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FodEventLogServiceImpl extends ServiceImpl<FodEventLogMapper, FodEventLog> implements FodEventLogService {

    @Override
    public Result<List<FodEventLog>> getLogsByEventId(Long eventId) {
        LambdaQueryWrapper<FodEventLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodEventLog::getEventId, eventId)
            .orderByDesc(FodEventLog::getOperateTime);
        return Result.success(list(wrapper));
    }

    @Override
    public void saveLog(Long eventId, String eventNo, String operationType,
                        String operatorId, String operatorName, String operatorRole,
                        Integer beforeStatus, Integer afterStatus,
                        Integer beforeRiskLevel, Integer afterRiskLevel,
                        String content) {
        FodEventLog log = new FodEventLog();
        log.setEventId(eventId);
        log.setEventNo(eventNo);
        log.setOperationType(operationType);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(operatorRole);
        log.setOperateTime(LocalDateTime.now());
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setBeforeRiskLevel(beforeRiskLevel);
        log.setAfterRiskLevel(afterRiskLevel);
        log.setContent(content);
        save(log);
    }
}
