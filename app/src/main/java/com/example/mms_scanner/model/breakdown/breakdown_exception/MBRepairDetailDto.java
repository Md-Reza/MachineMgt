package com.example.mms_scanner.model.breakdown.breakdown_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MBRepairDetailDto {
    @SerializedName("MBRHId")
    @Expose
    private String mBRHId;

    @SerializedName("MBHId")
    @Expose
    private String mBHId;

    @SerializedName("ERId")
    @Expose
    private String eRId;

    public String getmBRHId() {
        return mBRHId;
    }

    public void setmBRHId(String mBRHId) {
        this.mBRHId = mBRHId;
    }

    public String getmBHId() {
        return mBHId;
    }

    public void setmBHId(String mBHId) {
        this.mBHId = mBHId;
    }

    public String geteRId() {
        return eRId;
    }

    public void seteRId(String eRId) {
        this.eRId = eRId;
    }

    @Override
    public String toString() {
        return "MBRepairDetailDto{" +
                "mBRHId='" + mBRHId + '\'' +
                ", mBHId='" + mBHId + '\'' +
                ", eRId='" + eRId + '\'' +
                '}';
    }
}
