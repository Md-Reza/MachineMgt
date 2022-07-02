package com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MachineBreakdownQueue {

    @SerializedName("mbhId")
    @Expose
    private String mbhId;
    @SerializedName("machineCode")
    @Expose
    private String machineCode;
    @SerializedName("machineInfo")
    @Expose
    private MachineInfo machineInfo;
    @SerializedName("mbDetail")
    @Expose
    private MbDetail mbDetail;
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
    @SerializedName("repairBy")
    @Expose
    private String repairBy;
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
    @SerializedName("supvConfirmDuration")
    @Expose
    private String supvConfirmDuration;
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

    public MachineInfo getMachineInfo() {
        return machineInfo;
    }

    public void setMachineInfo(MachineInfo machineInfo) {
        this.machineInfo = machineInfo;
    }

    public MbDetail getMbDetail() {
        return mbDetail;
    }

    public void setMbDetail(MbDetail mbDetail) {
        this.mbDetail = mbDetail;
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

    public String getRepairBy() {
        return repairBy;
    }

    public void setRepairBy(String repairBy) {
        this.repairBy = repairBy;
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

    public String getSupvConfirmDuration() {
        return supvConfirmDuration;
    }

    public void setSupvConfirmDuration(String supvConfirmDuration) {
        this.supvConfirmDuration = supvConfirmDuration;
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
}
