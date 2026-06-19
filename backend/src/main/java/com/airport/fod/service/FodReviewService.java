package com.airport.fod.service;

import com.airport.fod.common.Result;
import com.airport.fod.dto.ReviewAddDTO;
import com.airport.fod.entity.FodReview;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FodReviewService extends IService<FodReview> {

    Result<FodReview> addReview(ReviewAddDTO dto);

    Result<List<FodReview>> getReviewsByEventId(Long eventId);
}
