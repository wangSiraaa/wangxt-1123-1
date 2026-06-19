package com.airport.fod.service;

import com.airport.fod.entity.FodEventMerge;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FodEventMergeService extends IService<FodEventMerge> {

    List<FodEventMerge> getByParentEventId(Long parentEventId);

    List<FodEventMerge> getByChildEventId(Long childEventId);
}
