package com.example.mms_scanner.model.breakdown.breakdown_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MBHeaderDto {

    @SerializedName("MBHId")
    @Expose
    private String mBHId;
    @SerializedName("MachineCode")
    @Expose
    private String machineCode;
    @SerializedName("RequestBy")
    @Expose
    private String requestBy;
    @SerializedName("IsUrgent")
    @Expose
    private Boolean isUrgent;
    @SerializedName("LineId")
    @Expose
    private String LineId;

    public String getMBHId() {
        return mBHId;
    }

    public void setMBHId(String mBHId) {
        this.mBHId = mBHId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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

    public String getmBHId() {
        return mBHId;
    }

    public void setmBHId(String mBHId) {
        this.mBHId = mBHId;
    }

    public String getLineId() {
        return LineId;
    }

    public void setLineId(String lineId) {
        LineId = lineId;
    }

    @Override
    public String toString() {
        return "MBHeaderDto{" +
                "mBHId='" + mBHId + '\'' +
                ", machineCode='" + machineCode + '\'' +
                ", requestBy='" + requestBy + '\'' +
                ", isUrgent=" + isUrgent +
                ", LineId='" + LineId + '\'' +
                '}';
    }
}
