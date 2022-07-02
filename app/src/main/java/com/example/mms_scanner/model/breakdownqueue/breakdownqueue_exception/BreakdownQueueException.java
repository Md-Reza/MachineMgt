package com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BreakdownQueueException {

    @SerializedName("mbdId")
    @Expose
    private String mbdId;
    @SerializedName("mbHeader")
    @Expose
    private MbHeader mbHeader;
    @SerializedName("erId")
    @Expose
    private String erId;
    @SerializedName("exceptionReason")
    @Expose
    private ExceptionReason exceptionReason;
    @SerializedName("isCompleted")
    @Expose
    private Boolean isCompleted;
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

    public String getMbdId() {
        return mbdId;
    }

    public void setMbdId(String mbdId) {
        this.mbdId = mbdId;
    }

    public MbHeader getMbHeader() {
        return mbHeader;
    }

    public void setMbHeader(MbHeader mbHeader) {
        this.mbHeader = mbHeader;
    }

    public String getErId() {
        return erId;
    }

    public void setErId(String erId) {
        this.erId = erId;
    }

    public ExceptionReason getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(ExceptionReason exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
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
        return "BreakdownQueueException{" +
                "mbdId='" + mbdId + '\'' +
                ", mbHeader=" + mbHeader +
                ", erId='" + erId + '\'' +
                ", exceptionReason=" + exceptionReason +
                ", isCompleted=" + isCompleted +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType='" + changedType + '\'' +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
