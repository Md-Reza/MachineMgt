package com.example.mms_scanner.model.breakdown.breakdown_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MBDetailDto {

    @SerializedName("ERId")
    @Expose
    private String eRId;

    @SerializedName("ReasonName")
    @Expose
    private String reasonName;

    public String geteRId() {
        return eRId;
    }

    public void seteRId(String eRId) {
        this.eRId = eRId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getERId() {
        return eRId;
    }

    public void setERId(String eRId) {
        this.eRId = eRId;
    }

    @Override
    public String toString() {
        return "MBDetailDto{" +
                "eRId='" + eRId + '\'' +
                ", reasonName='" + reasonName + '\'' +
                '}';
    }
}
