package com.airport.fod.service;

import com.airport.fod.common.Result;
import com.airport.fod.entity.FodPhoto;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FodPhotoService extends IService<FodPhoto> {

    Result<List<FodPhoto>> uploadPhotos(Long eventId, MultipartFile[] files, Integer photoType,
                                         String uploaderId, String uploaderName, String description);

    Result<List<FodPhoto>> getPhotosByEventId(Long eventId);

    Result<Boolean> deletePhoto(Long id, String operatorId);
}
