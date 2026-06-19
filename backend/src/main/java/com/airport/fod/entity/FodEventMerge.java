package com.airport.fod.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("fod_event_merge")
public class FodEventMerge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentEventId;

    private Long childEventId;

    private String childEventNo;

    private LocalDateTime mergeTime;

    private String mergeReason;

    private String operatorId;

    private String operatorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(Long parentEventId) {
        this.parentEventId = parentEventId;
    }

    public Long getChildEventId() {
        return childEventId;
    }

    public void setChildEventId(Long childEventId) {
        this.childEventId = childEventId;
    }

    public String getChildEventNo() {
        return childEventNo;
    }

    public void setChildEventNo(String childEventNo) {
        this.childEventNo = childEventNo;
    }

    public LocalDateTime getMergeTime() {
        return mergeTime;
    }

    public void setMergeTime(LocalDateTime mergeTime) {
        this.mergeTime = mergeTime;
    }

    public String getMergeReason() {
        return mergeReason;
    }

    public void setMergeReason(String mergeReason) {
        this.mergeReason = mergeReason;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
