package com.airport.fod.controller;

import com.airport.fod.common.Result;
import com.airport.fod.entity.Runway;
import com.airport.fod.service.RunwayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "跑道管理")
@RestController
@RequestMapping("/runway")
public class RunwayController {

    @Autowired
    private RunwayService runwayService;

    @ApiOperation("获取所有跑道列表")
    @GetMapping("/list")
    public Result<List<Runway>> getAllRunways() {
        return runwayService.getAllRunways();
    }

    @ApiOperation("根据ID获取跑道信息")
    @GetMapping("/{id}")
    public Result<Runway> getRunwayById(@PathVariable Long id) {
        return runwayService.getRunwayById(id);
    }

    @ApiOperation("根据编号获取跑道信息")
    @GetMapping("/code/{code}")
    public Result<Runway> getRunwayByCode(@PathVariable String code) {
        return runwayService.getRunwayByCode(code);
    }
}
