package com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExceptionReason {

    @SerializedName("erId")
    @Expose
    private String erId;
    @SerializedName("reasonName")
    @Expose
    private String reasonName;
    @SerializedName("excepStatus")
    @Expose
    private String excepStatus;
    @SerializedName("excepSource")
    @Expose
    private String excepSource;
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

    public String getErId() {
        return erId;
    }

    public void setErId(String erId) {
        this.erId = erId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getExcepStatus() {
        return excepStatus;
    }

    public void setExcepStatus(String excepStatus) {
        this.excepStatus = excepStatus;
    }

    public String getExcepSource() {
        return excepSource;
    }

    public void setExcepSource(String excepSource) {
        this.excepSource = excepSource;
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
        return "ExceptionReason{" +
                "erId='" + erId + '\'' +
                ", reasonName='" + reasonName + '\'' +
                ", excepStatus='" + excepStatus + '\'' +
                ", excepSource='" + excepSource + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType='" + changedType + '\'' +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
