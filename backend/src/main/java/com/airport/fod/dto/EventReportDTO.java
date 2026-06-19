package com.airport.fod.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EventReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "跑道ID不能为空")
    private Long runwayId;

    @NotBlank(message = "位置描述不能为空")
    private String location;

    private String locationPoint;

    private String fodType;

    private String fodSize;

    private String description;

    private String reporterId;

    private String reporterName;

    public Long getRunwayId() {
        return runwayId;
    }

    public void setRunwayId(Long runwayId) {
        this.runwayId = runwayId;
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
}
