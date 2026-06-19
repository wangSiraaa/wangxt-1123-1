package com.airport.fod.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EventCloseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "事件ID不能为空")
    private Long eventId;

    private String closeOpinion;

    private String closerId;

    private String closerName;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getCloseOpinion() {
        return closeOpinion;
    }

    public void setCloseOpinion(String closeOpinion) {
        this.closeOpinion = closeOpinion;
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
}
