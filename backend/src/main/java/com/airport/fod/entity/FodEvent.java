package com.airport.fod.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("fod_event")
public class FodEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventNo;

    private Long runwayId;

    private String runwayCode;

    private String location;

    private String locationPoint;

    private String fodType;

    private String fodSize;

    private String description;

    private Integer status;

    private Integer riskLevel;

    private Integer riskLevelLocked;

    private Integer isTop;

    private Integer affectTakeoff;

    private String reporterId;

    private String reporterName;

    private LocalDateTime reportTime;

    private String evaluatorId;

    private String evaluatorName;

    private LocalDateTime evaluateTime;

    private String evaluateOpinion;

    private String handlerId;

    private String handlerName;

    private LocalDateTime handleStartTime;

    private LocalDateTime handleEndTime;

    private String handleResult;

    private String closerId;

    private String closerName;

    private LocalDateTime closeTime;

    private String closeOpinion;

    private Integer hasPhoto;

    private Integer photoCount;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer dr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public Long getRunwayId() {
        return runwayId;
    }

    public void setRunwayId(Long runwayId) {
        this.runwayId = runwayId;
    }

    public String getRunwayCode() {
        return runwayCode;
    }

    public void setRunwayCode(String runwayCode) {
        this.runwayCode = runwayCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(String locationPoint) {
        this.locationPoint = locationPoint;
    }

    public String getFodType() {
        return fodType;
    }

    public void setFodType(String fodType) {
        this.fodType = fodType;
    }

    public String getFodSize() {
        return fodSize;
    }

    public void setFodSize(String fodSize) {
        this.fodSize = fodSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getRiskLevelLocked() {
        return riskLevelLocked;
    }

    public void setRiskLevelLocked(Integer riskLevelLocked) {
        this.riskLevelLocked = riskLevelLocked;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getAffectTakeoff() {
        return affectTakeoff;
    }

    public void setAffectTakeoff(Integer affectTakeoff) {
        this.affectTakeoff = affectTakeoff;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public LocalDateTime getReportTime() {
        return reportTime;
    }

    public void setReportTime(LocalDateTime reportTime) {
        this.reportTime = reportTime;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(String evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    public LocalDateTime getEvaluateTime() {
        return evaluateTime;
    }

    public void setEvaluateTime(LocalDateTime evaluateTime) {
        this.evaluateTime = evaluateTime;
    }

    public String getEvaluateOpinion() {
        return evaluateOpinion;
    }

    public void setEvaluateOpinion(String evaluateOpinion) {
        this.evaluateOpinion = evaluateOpinion;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public LocalDateTime getHandleStartTime() {
        return handleStartTime;
    }

    public void setHandleStartTime(LocalDateTime handleStartTime) {
        this.handleStartTime = handleStartTime;
    }

    public LocalDateTime getHandleEndTime() {
        return handleEndTime;
    }

    public void setHandleEndTime(LocalDateTime handleEndTime) {
        this.handleEndTime = handleEndTime;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public String getCloserId() {
        return closerId;
    }

    public void setCloserId(String closerId) {
        this.closerId = closerId;
    }

    public String getCloserName() {
        return closerName;
    }

    public void setCloserName(String closerName) {
        this.closerName = closerName;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public String getCloseOpinion() {
        return closeOpinion;
    }

    public void setCloseOpinion(String closeOpinion) {
        this.closeOpinion = closeOpinion;
    }

    public Integer getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(Integer hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }
}
