package com.airport.fod.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EventEvaluateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "事件ID不能为空")
    private Long eventId;

    @NotNull(message = "风险等级不能为空")
    private Integer riskLevel;

    @NotNull(message = "是否影响起降不能为空")
    private Integer affectTakeoff;

    private String evaluateOpinion;

    private String evaluatorId;

    private String evaluatorName;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getAffectTakeoff() {
        return affectTakeoff;
    }

    public void setAffectTakeoff(Integer affectTakeoff) {
        this.affectTakeoff = affectTakeoff;
    }

    public String getEvaluateOpinion() {
        return evaluateOpinion;
    }

    public void setEvaluateOpinion(String evaluateOpinion) {
        this.evaluateOpinion = evaluateOpinion;
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
}
