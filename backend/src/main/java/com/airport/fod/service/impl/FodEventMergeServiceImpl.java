package com.airport.fod.service.impl;

import com.airport.fod.entity.FodEventMerge;
import com.airport.fod.mapper.FodEventMergeMapper;
import com.airport.fod.service.FodEventMergeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FodEventMergeServiceImpl extends ServiceImpl<FodEventMergeMapper, FodEventMerge> implements FodEventMergeService {

    @Override
    public List<FodEventMerge> getByParentEventId(Long parentEventId) {
        LambdaQueryWrapper<FodEventMerge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodEventMerge::getParentEventId, parentEventId)
            .orderByDesc(FodEventMerge::getMergeTime);
        return list(wrapper);
    }

    @Override
    public List<FodEventMerge> getByChildEventId(Long childEventId) {
        LambdaQueryWrapper<FodEventMerge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodEventMerge::getChildEventId, childEventId)
            .orderByDesc(FodEventMerge::getMergeTime);
        return list(wrapper);
    }
}
