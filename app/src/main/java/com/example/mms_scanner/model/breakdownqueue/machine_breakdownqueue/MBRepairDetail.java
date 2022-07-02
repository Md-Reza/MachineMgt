package com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue;

import com.example.mms_scanner.model.breakdown.machine_breakdown.MbHeader;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.ExceptionReason;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MBRepairDetail {
    @SerializedName("mbrdId")
    @Expose
    private String mbrdId;
    @SerializedName("mbHeader")
    @Expose
    private MbHeader mbHeader;
    @SerializedName("erId")
    @Expose
    private String erId;
    @SerializedName("exceptionReason")
    @Expose
    private ExceptionReason exceptionReason;

    public String getmbrdId() {
        return mbrdId;
    }

    public void setmbrdId(String mbrdId) {
        this.mbrdId = mbrdId;
    }

    public MbHeader getMbHeader() {
        return mbHeader;
    }

    public void setMbHeader(MbHeader mbHeader) {
        this.mbHeader = mbHeader;
    }

    public String getErId() {
        return erId;
    }

    public void setErId(String erId) {
        this.erId = erId;
    }

    public ExceptionReason getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(ExceptionReason exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    @Override
    public String toString() {
        return "MBRepairDetail{" +
                "mbrdId='" + mbrdId + '\'' +
                ", mbHeader=" + mbHeader +
                ", erId='" + erId + '\'' +
                ", exceptionReason=" + exceptionReason +
                '}';
    }
}
