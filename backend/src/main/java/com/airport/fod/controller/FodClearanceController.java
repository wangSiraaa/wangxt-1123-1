package com.airport.fod.controller;

import com.airport.fod.common.Result;
import com.airport.fod.dto.ClearanceOperationDTO;
import com.airport.fod.entity.FodClearance;
import com.airport.fod.service.FodClearanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "放行管理")
@RestController
@RequestMapping("/clearance")
public class FodClearanceController {

    @Autowired
    private FodClearanceService clearanceService;

    @ApiOperation("执行放行操作（冻结/解冻/允许/禁止）")
    @PostMapping("/operate")
    public Result<FodClearance> operateClearance(
            @RequestBody @Valid ClearanceOperationDTO dto,
            @RequestHeader(value = "X-User-Role", defaultValue = "TOWER_CONTROLLER") String operatorRole) {
        return clearanceService.operateClearance(dto, operatorRole);
    }

    @ApiOperation("查询事件的放行记录")
    @GetMapping("/event/{eventId}")
    public Result<List<FodClearance>> getClearanceByEventId(@PathVariable Long eventId) {
        return clearanceService.getClearanceByEventId(eventId);
    }

    @ApiOperation("查询跑道的放行记录")
    @GetMapping("/runway/{runwayId}")
    public Result<List<FodClearance>> getClearanceByRunwayId(@PathVariable Long runwayId) {
        return clearanceService.getClearanceByRunwayId(runwayId);
    }
}
