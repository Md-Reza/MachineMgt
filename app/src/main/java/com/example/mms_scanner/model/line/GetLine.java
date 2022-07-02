package com.example.mms_scanner.model.line;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetLine {

    @SerializedName("lineId")
    @Expose
    private String lineId;
    @SerializedName("sections")
    @Expose
    private Sections sections;
    @SerializedName("lineName")
    @Expose
    private String lineName;
    @SerializedName("lineDesc")
    @Expose
    private String lineDesc;
    @SerializedName("sapLine")
    @Expose
    private Object sapLine;
    @SerializedName("frLine")
    @Expose
    private String frLine;
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
    private Integer changedType;
    @SerializedName("changedDate")
    @Expose
    private String changedDate;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Sections getSections() {
        return sections;
    }

    public void setSections(Sections sections) {
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

    public Object getSapLine() {
        return sapLine;
    }

    public void setSapLine(Object sapLine) {
        this.sapLine = sapLine;
    }

    public String getFrLine() {
        return frLine;
    }

    public void setFrLine(String frLine) {
        this.frLine = frLine;
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

    public Integer getChangedType() {
        return changedType;
    }

    public void setChangedType(Integer changedType) {
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
        return "GetLine{" +
                "lineId=" + lineId +
                ", sections=" + sections +
                ", lineName='" + lineName + '\'' +
                ", lineDesc='" + lineDesc + '\'' +
                ", sapLine=" + sapLine +
                ", frLine='" + frLine + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType=" + changedType +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
