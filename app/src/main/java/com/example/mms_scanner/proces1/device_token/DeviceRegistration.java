package com.example.mms_scanner.proces1.device_token;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceRegistration {

    @SerializedName("drId")
    @Expose
    private String drId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("deptId")
    @Expose
    private String deptId;
    @SerializedName("deviceName")
    @Expose
    private String deviceName;
    @SerializedName("deviceTocken")
    @Expose
    private String deviceTocken;
    @SerializedName("ipAdd")
    @Expose
    private String ipAdd;
    @SerializedName("makAdd")
    @Expose
    private String makAdd;
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

    public String getDrId() {
        return drId;
    }

    public void setDrId(String drId) {
        this.drId = drId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceTocken() {
        return deviceTocken;
    }

    public void setDeviceTocken(String deviceTocken) {
        this.deviceTocken = deviceTocken;
    }

    public String getIpAdd() {
        return ipAdd;
    }

    public void setIpAdd(String ipAdd) {
        this.ipAdd = ipAdd;
    }

    public String getMakAdd() {
        return makAdd;
    }

    public void setMakAdd(String makAdd) {
        this.makAdd = makAdd;
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
        return "DeviceRegistration{" +
                "drId='" + drId + '\'' +
                ", userId='" + userId + '\'' +
                ", deptId='" + deptId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceTocken='" + deviceTocken + '\'' +
                ", ipAdd='" + ipAdd + '\'' +
                ", makAdd='" + makAdd + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType='" + changedType + '\'' +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
