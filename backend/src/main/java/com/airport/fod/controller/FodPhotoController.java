package com.airport.fod.controller;

import com.airport.fod.common.Result;
import com.airport.fod.entity.FodPhoto;
import com.airport.fod.service.FodPhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "照片管理")
@RestController
@RequestMapping("/photo")
public class FodPhotoController {

    @Autowired
    private FodPhotoService photoService;

    @ApiOperation("上传照片")
    @PostMapping("/upload")
    public Result<List<FodPhoto>> uploadPhotos(
            @RequestParam Long eventId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(required = false) Integer photoType,
            @RequestParam(required = false) String uploaderId,
            @RequestParam(required = false) String uploaderName,
            @RequestParam(required = false) String description) {
        return photoService.uploadPhotos(eventId, files, photoType, uploaderId, uploaderName, description);
    }

    @ApiOperation("查询事件照片列表")
    @GetMapping("/event/{eventId}")
    public Result<List<FodPhoto>> getPhotosByEventId(@PathVariable Long eventId) {
        return photoService.getPhotosByEventId(eventId);
    }

    @ApiOperation("删除照片")
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePhoto(
            @PathVariable Long id,
            @RequestParam(required = false) String operatorId) {
        return photoService.deletePhoto(id, operatorId);
    }
}
