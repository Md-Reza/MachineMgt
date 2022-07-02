package com.example.mms_scanner.model.breakdown.breakdown_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BreakdownException {

    @SerializedName("erId")
    @Expose
    private Long erId;
    @SerializedName("reasonName")
    @Expose
    private String reasonName;
    @SerializedName("excepStatus")
    @Expose
    private Integer excepStatus;
    @SerializedName("excepSource")
    @Expose
    private Integer excepSource;
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
    private Integer changedType;
    @SerializedName("changedDate")
    @Expose
    private String changedDate;

    public Long getErId() {
        return erId;
    }

    public void setErId(Long erId) {
        this.erId = erId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public Integer getExcepStatus() {
        return excepStatus;
    }

    public void setExcepStatus(Integer excepStatus) {
        this.excepStatus = excepStatus;
    }

    public Integer getExcepSource() {
        return excepSource;
    }

    public void setExcepSource(Integer excepSource) {
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

    public Integer getChangedType() {
        return changedType;
    }

    public void setChangedType(Integer changedType) {
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
        return "BreakdownException{" +
                "erId=" + erId +
                ", reasonName='" + reasonName + '\'' +
                ", excepStatus=" + excepStatus +
                ", excepSource=" + excepSource +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType=" + changedType +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
