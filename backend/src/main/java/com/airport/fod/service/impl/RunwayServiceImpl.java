package com.airport.fod.service.impl;

import com.airport.fod.common.Result;
import com.airport.fod.entity.Runway;
import com.airport.fod.mapper.RunwayMapper;
import com.airport.fod.service.RunwayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunwayServiceImpl extends ServiceImpl<RunwayMapper, Runway> implements RunwayService {

    @Override
    public Result<List<Runway>> getAllRunways() {
        LambdaQueryWrapper<Runway> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Runway::getRunwayCode);
        return Result.success(list(wrapper));
    }

    @Override
    public Result<Runway> getRunwayById(Long id) {
        Runway runway = getById(id);
        if (runway == null) {
            return Result.error("跑道不存在");
        }
        return Result.success(runway);
    }

    @Override
    public Result<Runway> getRunwayByCode(String code) {
        LambdaQueryWrapper<Runway> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Runway::getRunwayCode, code);
        return Result.success(getOne(wrapper));
    }
}
