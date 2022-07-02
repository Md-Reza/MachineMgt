package com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue;

import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.ExceptionReason;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MbDetail {

    @SerializedName("mbdId")
    @Expose
    private String mbdId;
    @SerializedName("mbHeader")
    @Expose
    private String mbHeader;
    @SerializedName("erId")
    @Expose
    private Long erId;
    @SerializedName("exceptionReason")
    @Expose
    private ExceptionReason exceptionReason;
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

    public String getMbHeader() {
        return mbHeader;
    }

    public void setMbHeader(String mbHeader) {
        this.mbHeader = mbHeader;
    }

    public Long getErId() {
        return erId;
    }

    public void setErId(Long erId) {
        this.erId = erId;
    }

    public ExceptionReason getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(ExceptionReason exceptionReason) {
        this.exceptionReason = exceptionReason;
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
        return "MbDetail{" +
                "mbdId=" + mbdId +
                ", mbHeader=" + mbHeader +
                ", erId=" + erId +
                ", exceptionReason=" + exceptionReason +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy=" + changedBy +
                ", changedType=" + changedType +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
