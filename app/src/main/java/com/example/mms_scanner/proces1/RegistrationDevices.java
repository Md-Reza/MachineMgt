package com.example.mms_scanner.proces1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegistrationDevices {

    @SerializedName("SectionId")
    @Expose
    private String sectionId;
    @SerializedName("LineId")
    @Expose
    private String lineId;
    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("DeptId")
    @Expose
    private String deptId;
    @SerializedName("DeviceName")
    @Expose
    private String deviceName;
    @SerializedName("DeviceTocken")
    @Expose
    private String deviceTocken;
    @SerializedName("IPAdd")
    @Expose
    private String iPAdd;
    @SerializedName("MAKAdd")
    @Expose
    private String mAKAdd;

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
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

    public String getIPAdd() {
        return iPAdd;
    }

    public void setIPAdd(String iPAdd) {
        this.iPAdd = iPAdd;
    }

    public String getMAKAdd() {
        return mAKAdd;
    }

    public void setMAKAdd(String mAKAdd) {
        this.mAKAdd = mAKAdd;
    }

    @Override
    public String toString() {
        return "DeviceRegistration{" +
                "sectionId='" + sectionId + '\'' +
                ", lineId='" + lineId + '\'' +
                ", userId='" + userId + '\'' +
                ", deptId='" + deptId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceTocken='" + deviceTocken + '\'' +
                ", iPAdd='" + iPAdd + '\'' +
                ", mAKAdd='" + mAKAdd + '\'' +
                '}';
    }
}