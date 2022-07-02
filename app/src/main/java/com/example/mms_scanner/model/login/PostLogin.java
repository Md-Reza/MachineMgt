package com.example.mms_scanner.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Mohammad Kobirul Islam
 * @version 2.2.0
 * @date today
 */

public class PostLogin {

    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("AppId")
    @Expose
    private String appId;
    @SerializedName("AppVer")
    @Expose
    private String appVer;
    @SerializedName("ShiftId")
    @Expose
    private String shiftId;
    @SerializedName("LineId")
    @Expose
    private String lineId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    @Override
    public String toString() {
        return "PostLogin{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", appId='" + appId + '\'' +
                ", appVer='" + appVer + '\'' +
                ", shiftId='" + shiftId + '\'' +
                ", lineId='" + lineId + '\'' +
                '}';
    }
}