package com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lines {

    @SerializedName("lineId")
    @Expose
    private String lineId;
    @SerializedName("sections")
    @Expose
    private String sections;
    @SerializedName("lineName")
    @Expose
    private String lineName;
    @SerializedName("lineDesc")
    @Expose
    private String lineDesc;
    @SerializedName("sapLine")
    @Expose
    private String sapLine;
    @SerializedName("frLine")
    @Expose
    private String frLine;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
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

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineDesc() {
        return lineDesc;
    }

    public void setLineDesc(String lineDesc) {
        this.lineDesc = lineDesc;
    }

    public String getSapLine() {
        return sapLine;
    }

    public void setSapLine(String sapLine) {
        this.sapLine = sapLine;
    }

    public String getFrLine() {
        return frLine;
    }

    public void setFrLine(String frLine) {
        this.frLine = frLine;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
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
        return "Lines{" +
                "lineId=" + lineId +
                ", sections=" + sections +
                ", lineName=" + lineName +
                ", lineDesc=" + lineDesc +
                ", sapLine=" + sapLine +
                ", frLine=" + frLine +
                ", sectionName=" + sectionName +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy=" + changedBy +
                ", changedType=" + changedType +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
