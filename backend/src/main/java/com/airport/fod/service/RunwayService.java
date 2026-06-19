package com.airport.fod.service;

import com.airport.fod.common.Result;
import com.airport.fod.entity.Runway;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RunwayService extends IService<Runway> {

    Result<List<Runway>> getAllRunways();

    Result<Runway> getRunwayById(Long id);

    Result<Runway> getRunwayByCode(String code);
}
