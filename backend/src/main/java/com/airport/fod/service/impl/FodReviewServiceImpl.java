package com.airport.fod.service.impl;

import com.airport.fod.common.Result;
import com.airport.fod.constant.FodConstants;
import com.airport.fod.dto.ReviewAddDTO;
import com.airport.fod.entity.FodEvent;
import com.airport.fod.entity.FodReview;
import com.airport.fod.mapper.FodReviewMapper;
import com.airport.fod.service.FodEventLogService;
import com.airport.fod.service.FodEventService;
import com.airport.fod.service.FodReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FodReviewServiceImpl extends ServiceImpl<FodReviewMapper, FodReview> implements FodReviewService {

    @Autowired
    private FodEventService eventService;

    @Autowired
    private FodEventLogService eventLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FodReview> addReview(ReviewAddDTO dto) {
        FodEvent event = eventService.getById(dto.getEventId());
        if (event == null) {
            return Result.error("事件不存在");
        }

        FodReview review = new FodReview();
        review.setEventId(dto.getEventId());
        review.setEventNo(event.getEventNo());
        review.setReviewContent(dto.getReviewContent());
        review.setReviewType(dto.getReviewType() != null ? dto.getReviewType() : 1);
        review.setReviewerId(dto.getReviewerId() != null ? dto.getReviewerId() : FodConstants.DEFAULT_USER_ID);
        review.setReviewerName(dto.getReviewerName() != null ? dto.getReviewerName() : FodConstants.DEFAULT_USER_NAME);
        review.setReviewTime(LocalDateTime.now());
        review.setAttachmentUrls(dto.getAttachmentUrls());
        review.setRemark(dto.getRemark());

        save(review);

        eventLogService.saveLog(event.getId(), event.getEventNo(), FodConstants.OPERATION_REVIEW,
            review.getReviewerId(), review.getReviewerName(), null,
            null, null, null, null, "追加复盘记录");

        return Result.success("复盘记录已添加", review);
    }

    @Override
    public Result<List<FodReview>> getReviewsByEventId(Long eventId) {
        LambdaQueryWrapper<FodReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodReview::getEventId, eventId)
            .orderByDesc(FodReview::getReviewTime);
        return Result.success(list(wrapper));
    }
}
