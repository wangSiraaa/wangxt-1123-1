package com.airport.fod.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.airport.fod.common.Result;
import com.airport.fod.constant.FodConstants;
import com.airport.fod.entity.FodEvent;
import com.airport.fod.entity.FodPhoto;
import com.airport.fod.enums.EventStatusEnum;
import com.airport.fod.enums.PhotoTypeEnum;
import com.airport.fod.mapper.FodPhotoMapper;
import com.airport.fod.service.FodEventService;
import com.airport.fod.service.FodPhotoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FodPhotoServiceImpl extends ServiceImpl<FodPhotoMapper, FodPhoto> implements FodPhotoService {

    @Value("${fod.photo.upload-path:/data/fod/photos}")
    private String uploadPath;

    @Value("${fod.photo.access-url:http://localhost:8080/api/photos}")
    private String accessUrl;

    @Autowired
    private FodEventService eventService;

    @PostConstruct
    public void init() {
        FileUtil.mkdir(uploadPath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<FodPhoto>> uploadPhotos(Long eventId, MultipartFile[] files, Integer photoType,
                                               String uploaderId, String uploaderName, String description) {
        FodEvent event = eventService.getById(eventId);
        if (event == null) {
            return Result.error("事件不存在");
        }

        List<FodPhoto> photos = new ArrayList<>();
        String dateDir = DateUtil.format(LocalDateTime.now(), "yyyy/MM/dd");
        String saveDir = uploadPath + File.separator + dateDir;
        FileUtil.mkdir(saveDir);

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String originalName = file.getOriginalFilename();
            String extension = FileUtil.extName(originalName);
            String fileName = IdUtil.simpleUUID() + "." + extension;
            String savePath = saveDir + File.separator + fileName;
            String photoUrl = accessUrl + "/" + dateDir + "/" + fileName;

            try {
                file.transferTo(new File(savePath));
            } catch (IOException e) {
                log.error("文件上传失败", e);
                return Result.error("文件上传失败：" + e.getMessage());
            }

            FodPhoto photo = new FodPhoto();
            photo.setEventId(eventId);
            photo.setEventNo(event.getEventNo());
            photo.setPhotoNo(FodConstants.PHOTO_NO_PREFIX + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + StrUtil.fillBefore(String.valueOf((int) (Math.random() * 10000)), '0', 4));
            photo.setPhotoUrl(photoUrl);
            photo.setFileName(originalName);
            photo.setFileSize(file.getSize());
            photo.setFileType(file.getContentType());
            photo.setPhotoType(photoType != null ? photoType : PhotoTypeEnum.REPORT_PHOTO.getCode());
            photo.setUploaderId(uploaderId != null ? uploaderId : FodConstants.DEFAULT_USER_ID);
            photo.setUploaderName(uploaderName != null ? uploaderName : FodConstants.DEFAULT_USER_NAME);
            photo.setUploadTime(LocalDateTime.now());
            photo.setDescription(description);

            save(photo);
            photos.add(photo);
        }

        eventService.updatePhotoCount(eventId);

        return Result.success("上传成功", photos);
    }

    @Override
    public Result<List<FodPhoto>> getPhotosByEventId(Long eventId) {
        LambdaQueryWrapper<FodPhoto> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FodPhoto::getEventId, eventId)
            .orderByAsc(FodPhoto::getUploadTime);
        return Result.success(list(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deletePhoto(Long id, String operatorId) {
        FodPhoto photo = getById(id);
        if (photo == null) {
            return Result.error("照片不存在");
        }

        FodEvent event = eventService.getById(photo.getEventId());
        if (event != null && EventStatusEnum.CLOSED.getCode().equals(event.getStatus())) {
            return Result.error("事件已关闭，不能删除照片");
        }

        removeById(id);
        eventService.updatePhotoCount(photo.getEventId());

        return Result.success("删除成功", true);
    }
}
