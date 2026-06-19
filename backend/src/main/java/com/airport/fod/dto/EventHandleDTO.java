package com.airport.fod.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class EventHandleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "事件ID不能为空")
    private Long eventId;

    @NotBlank(message = "处理结果不能为空")
    private String handleResult;

    private String handlerId;

    private String handlerName;

    private LocalDateTime estimatedRecoveryTime;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
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

    public LocalDateTime getEstimatedRecoveryTime() {
        return estimatedRecoveryTime;
    }

    public void setEstimatedRecoveryTime(LocalDateTime estimatedRecoveryTime) {
        this.estimatedRecoveryTime = estimatedRecoveryTime;
    }
}
