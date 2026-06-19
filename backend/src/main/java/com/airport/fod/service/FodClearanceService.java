package com.airport.fod.service;

import com.airport.fod.common.Result;
import com.airport.fod.dto.ClearanceOperationDTO;
import com.airport.fod.entity.FodClearance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FodClearanceService extends IService<FodClearance> {

    Result<FodClearance> operateClearance(ClearanceOperationDTO dto, String operatorRole);

    Result<List<FodClearance>> getClearanceByEventId(Long eventId);

    Result<List<FodClearance>> getClearanceByRunwayId(Long runwayId);

    void freezeRunway(Long runwayId, Long eventId, String eventNo, String operatorId, String operatorName, String reason);

    void unfreezeRunway(Long runwayId, Long eventId, String eventNo, String operatorId, String operatorName, String reason);
}
