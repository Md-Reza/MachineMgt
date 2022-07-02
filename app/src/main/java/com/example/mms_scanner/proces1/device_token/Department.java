package com.example.mms_scanner.proces1.device_token;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Department {

    @SerializedName("deptId")
    @Expose
    private String deptId;
    @SerializedName("deptName")
    @Expose
    private String deptName;
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

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
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
        return "Department{" +
                "deptId='" + deptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType='" + changedType + '\'' +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}