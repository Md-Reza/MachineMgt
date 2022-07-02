package com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MbHeader {

    @SerializedName("mbhId")
    @Expose
    private String mbhId;
    @SerializedName("machineCode")
    @Expose
    private String machineCode;
    @SerializedName("machineInfo")
    @Expose
    private String machineInfo;
    @SerializedName("lineId")
    @Expose
    private String lineId;
    @SerializedName("lines")
    @Expose
    private Lines lines;
    @SerializedName("requestDate")
    @Expose
    private String requestDate;
    @SerializedName("requestBy")
    @Expose
    private String requestBy;
    @SerializedName("isUrgent")
    @Expose
    private Boolean isUrgent;
    @SerializedName("startDateTime")
    @Expose
    private String startDateTime;
    @SerializedName("endDateTime")
    @Expose
    private String endDateTime;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("isMechanicConfirm")
    @Expose
    private Boolean isMechanicConfirm;
    @SerializedName("isSupvConfirm")
    @Expose
    private Boolean isSupvConfirm;
    @SerializedName("confirmDate")
    @Expose
    private String confirmDate;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("inactive")
    @Expose
    private Boolean inactive;
    @SerializedName("changedBy")
    @Expose
    private String changedBy;
    @SerializedName("changedType")
    @Expose
    private String changedType;
    @SerializedName("changedDate")
    @Expose
    private String changedDate;

    public String getMbhId() {
        return mbhId;
    }

    public void setMbhId(String mbhId) {
        this.mbhId = mbhId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineInfo() {
        return machineInfo;
    }

    public void setMachineInfo(String machineInfo) {
        this.machineInfo = machineInfo;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Lines getLines() {
        return lines;
    }

    public void setLines(Lines lines) {
        this.lines = lines;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsMechanicConfirm() {
        return isMechanicConfirm;
    }

    public void setIsMechanicConfirm(Boolean isMechanicConfirm) {
        this.isMechanicConfirm = isMechanicConfirm;
    }

    public Boolean getIsSupvConfirm() {
        return isSupvConfirm;
    }

    public void setIsSupvConfirm(Boolean isSupvConfirm) {
        this.isSupvConfirm = isSupvConfirm;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangedType() {
        return changedType;
    }

    public void setChangedType(String changedType) {
        this.changedType = changedType;
    }

    public String getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(String changedDate) {
        this.changedDate = changedDate;
    }

    @Override
    public String toString() {
        return "MbHeader{" +
                "mbhId='" + mbhId + '\'' +
                ", machineCode='" + machineCode + '\'' +
                ", machineInfo='" + machineInfo + '\'' +
                ", lineId='" + lineId + '\'' +
                ", lines=" + lines +
                ", requestDate='" + requestDate + '\'' +
                ", requestBy='" + requestBy + '\'' +
                ", isUrgent=" + isUrgent +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", duration='" + duration + '\'' +
                ", status='" + status + '\'' +
                ", isMechanicConfirm=" + isMechanicConfirm +
                ", isSupvConfirm=" + isSupvConfirm +
                ", confirmDate='" + confirmDate + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType='" + changedType + '\'' +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
