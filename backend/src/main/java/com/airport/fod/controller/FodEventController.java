package com.airport.fod.controller;

import com.airport.fod.common.PageQuery;
import com.airport.fod.common.Result;
import com.airport.fod.dto.*;
import com.airport.fod.entity.FodEvent;
import com.airport.fod.service.FodEventService;
import com.airport.fod.service.FodReviewService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "事件管理")
@RestController
@RequestMapping("/event")
public class FodEventController {

    @Autowired
    private FodEventService eventService;

    @Autowired
    private FodReviewService reviewService;

    @ApiOperation("上报异物事件")
    @PostMapping("/report")
    public Result<Long> reportEvent(@RequestBody @Valid EventReportDTO dto) {
        return eventService.reportEvent(dto);
    }

    @ApiOperation("获取事件详情")
    @GetMapping("/{id}")
    public Result<FodEvent> getEventDetail(@PathVariable Long id) {
        return eventService.getEventDetail(id);
    }

    @ApiOperation("分页查询事件列表")
    @PostMapping("/page")
    public Result<Page<FodEvent>> getEventPage(
            @RequestBody PageQuery pageQuery,
            @RequestParam(required = false) Map<String, Object> params) {
        return eventService.getEventPage(pageQuery, params);
    }

    @ApiOperation("查询事件列表")
    @GetMapping("/list")
    public Result<List<FodEvent>> getEventList(@RequestParam(required = false) Map<String, Object> params) {
        return eventService.getEventList(params);
    }

    @ApiOperation("塔台评估事件")
    @PostMapping("/evaluate")
    public Result<FodEvent> evaluateEvent(
            @RequestBody @Valid EventEvaluateDTO dto,
            @RequestHeader(value = "X-User-Role", defaultValue = "TOWER_CONTROLLER") String operatorRole) {
        return eventService.evaluateEvent(dto, operatorRole);
    }

    @ApiOperation("开始处理")
    @PostMapping("/handle/start/{eventId}")
    public Result<FodEvent> startHandle(
            @PathVariable Long eventId,
            @RequestParam(required = false) String handlerId,
            @RequestParam(required = false) String handlerName,
            @RequestHeader(value = "X-User-Role", defaultValue = "MAINTENANCE_TEAM") String operatorRole) {
        return eventService.startHandle(eventId, handlerId, handlerName, operatorRole);
    }

    @ApiOperation("完成处理")
    @PostMapping("/handle/complete")
    public Result<FodEvent> completeHandle(
            @RequestBody @Valid EventHandleDTO dto,
            @RequestHeader(value = "X-User-Role", defaultValue = "MAINTENANCE_TEAM") String operatorRole) {
        return eventService.completeHandle(dto, operatorRole);
    }

    @ApiOperation("关闭事件")
    @PostMapping("/close")
    public Result<FodEvent> closeEvent(
            @RequestBody @Valid EventCloseDTO dto,
            @RequestHeader(value = "X-User-Role", defaultValue = "TOWER_CONTROLLER") String operatorRole) {
        return eventService.closeEvent(dto, operatorRole);
    }

    @ApiOperation("取消事件")
    @PostMapping("/cancel/{eventId}")
    public Result<FodEvent> cancelEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String operatorId,
            @RequestParam(required = false) String operatorName,
            @RequestHeader(value = "X-User-Role", defaultValue = "FIELD_INSPECTOR") String operatorRole) {
        return eventService.cancelEvent(eventId, operatorId, operatorName, operatorRole);
    }

    @ApiOperation("修改风险等级")
    @PostMapping("/risk-level")
    public Result<FodEvent> updateRiskLevel(
            @RequestParam Long eventId,
            @RequestParam Integer riskLevel,
            @RequestParam(required = false) String operatorId,
            @RequestParam(required = false) String operatorName,
            @RequestHeader(value = "X-User-Role", defaultValue = "TOWER_CONTROLLER") String operatorRole) {
        return eventService.updateRiskLevel(eventId, riskLevel, operatorId, operatorName, operatorRole);
    }

    @ApiOperation("获取统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Integer>> getStatistics() {
        return eventService.getStatistics();
    }

    @ApiOperation("追加复盘记录")
    @PostMapping("/review")
    public Result<?> addReview(@RequestBody @Valid ReviewAddDTO dto) {
        return reviewService.addReview(dto);
    }

    @ApiOperation("查询事件的复盘记录")
    @GetMapping("/review/{eventId}")
    public Result<?> getReviewsByEventId(@PathVariable Long eventId) {
        return reviewService.getReviewsByEventId(eventId);
    }

    @ApiOperation("查询合并的子事件列表")
    @GetMapping("/merged-children/{parentEventId}")
    public Result<List<FodEvent>> getMergedChildEvents(@PathVariable Long parentEventId) {
        return eventService.getMergedChildEvents(parentEventId);
    }

    @ApiOperation("查询合并的父事件")
    @GetMapping("/merged-parent/{childEventId}")
    public Result<FodEvent> getMergedParentEvent(@PathVariable Long childEventId) {
        return eventService.getMergedParentEvent(childEventId);
    }
}
