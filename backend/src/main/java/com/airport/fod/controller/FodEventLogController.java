package com.airport.fod.controller;

import com.airport.fod.common.Result;
import com.airport.fod.entity.FodEventLog;
import com.airport.fod.service.FodEventLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "操作日志")
@RestController
@RequestMapping("/event-log")
public class FodEventLogController {

    @Autowired
    private FodEventLogService eventLogService;

    @ApiOperation("查询事件的操作日志")
    @GetMapping("/event/{eventId}")
    public Result<List<FodEventLog>> getLogsByEventId(@PathVariable Long eventId) {
        return eventLogService.getLogsByEventId(eventId);
    }
}
