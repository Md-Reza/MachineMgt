package com.example.mms_scanner.model.status_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MCBreakdownReport {

    @SerializedName("mbhId")
    @Expose
    private String mbhId;
    @SerializedName("machineCode")
    @Expose
    private String machineCode;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("lineName")
    @Expose
    private String lineName;
    @SerializedName("requestDate")
    @Expose
    private String requestDate;
    @SerializedName("confirmDate")
    @Expose
    private String confirmDate;
    @SerializedName("acceptanceHour")
    @Expose
    private String acceptanceHour;
    @SerializedName("acceptanceMinute")
    @Expose
    private String acceptanceMinute;
    @SerializedName("acceptanceDuration")
    @Expose
    private String acceptanceDuration;
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
    @SerializedName("finshedHour")
    @Expose
    private String finshedHour;
    @SerializedName("finsedMinute")
    @Expose
    private String finsedMinute;
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
    @SerializedName("repairsCompletedRemarks")
    @Expose
    private String repairsCompletedRemarks;
    @SerializedName("acceptanceCompletedRemarks")
    @Expose
    private String acceptanceCompletedRemarks;

    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName;

    @SerializedName("categoryName")
    @Expose
    private String categoryName;

    @SerializedName("shortCode")
    @Expose
    private String shortCode;

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

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getAcceptanceHour() {
        return acceptanceHour;
    }

    public void setAcceptanceHour(String acceptanceHour) {
        this.acceptanceHour = acceptanceHour;
    }

    public String getAcceptanceMinute() {
        return acceptanceMinute;
    }

    public void setAcceptanceMinute(String acceptanceMinute) {
        this.acceptanceMinute = acceptanceMinute;
    }

    public String getAcceptanceDuration() {
        return acceptanceDuration;
    }

    public void setAcceptanceDuration(String acceptanceDuration) {
        this.acceptanceDuration = acceptanceDuration;
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

    public String getFinshedHour() {
        return finshedHour;
    }

    public void setFinshedHour(String finshedHour) {
        this.finshedHour = finshedHour;
    }

    public String getFinsedMinute() {
        return finsedMinute;
    }

    public void setFinsedMinute(String finsedMinute) {
        this.finsedMinute = finsedMinute;
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

    public String getRepairsCompletedRemarks() {
        return repairsCompletedRemarks;
    }

    public void setRepairsCompletedRemarks(String repairsCompletedRemarks) {
        this.repairsCompletedRemarks = repairsCompletedRemarks;
    }

    public String getAcceptanceCompletedRemarks() {
        return acceptanceCompletedRemarks;
    }

    public void setAcceptanceCompletedRemarks(String acceptanceCompletedRemarks) {
        this.acceptanceCompletedRemarks = acceptanceCompletedRemarks;
    }


    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    @Override
    public String toString() {
        return "MCBreakdownReport{" +
                "mbhId='" + mbhId + '\'' +
                ", machineCode='" + machineCode + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", lineName='" + lineName + '\'' +
                ", requestDate='" + requestDate + '\'' +
                ", confirmDate='" + confirmDate + '\'' +
                ", acceptanceHour='" + acceptanceHour + '\'' +
                ", acceptanceMinute='" + acceptanceMinute + '\'' +
                ", acceptanceDuration='" + acceptanceDuration + '\'' +
                ", requestBy='" + requestBy + '\'' +
                ", isUrgent='" + isUrgent + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", finshedHour='" + finshedHour + '\'' +
                ", finsedMinute='" + finsedMinute + '\'' +
                ", duration='" + duration + '\'' +
                ", repairBy='" + repairBy + '\'' +
                ", status='" + status + '\'' +
                ", isMechanicConfirm='" + isMechanicConfirm + '\'' +
                ", isSupvConfirm='" + isSupvConfirm + '\'' +
                ", repairsCompletedRemarks='" + repairsCompletedRemarks + '\'' +
                ", acceptanceCompletedRemarks='" + acceptanceCompletedRemarks + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", shortCode='" + shortCode + '\'' +
                '}';
    }
}
