package com.example.mms_scanner.model.breakdownqueue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateQueue {

    @SerializedName("MBHId")
    @Expose
    private String mBHId;
    @SerializedName("MachineCode")
    @Expose
    private String machineCode;
    @SerializedName("RequestBy")
    @Expose
    private String requestBy;

    @SerializedName("RepairRating")
    @Expose
    private Float repairRating;

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

    public String getmBHId() {
        return mBHId;
    }

    public void setmBHId(String mBHId) {
        this.mBHId = mBHId;
    }

    public Float getRepairRating() {
        return repairRating;
    }

    public void setRepairRating(Float repairRating) {
        this.repairRating = repairRating;
    }

    @Override
    public String toString() {
        return "UpdateQueue{" +
                "mBHId='" + mBHId + '\'' +
                ", machineCode='" + machineCode + '\'' +
                ", requestBy='" + requestBy + '\'' +
                ", repairRating=" + repairRating +
                '}';
    }
}