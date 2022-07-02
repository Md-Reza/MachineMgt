package com.example.mms_scanner.model.machine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MachineInfo {
    @SerializedName("machineId")
    @Expose
    private Integer machineId;
    @SerializedName("machineCode")
    @Expose
    private String machineCode;
    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("modelName")
    @Expose
    private String modelName;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName;
    @SerializedName("shortCode")
    @Expose
    private String shortCode;
    @SerializedName("supplier")
    @Expose
    private String supplier;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("specification")
    @Expose
    private String specification;
    @SerializedName("isRent")
    @Expose
    private Boolean isRent;
    @SerializedName("serialNo")
    @Expose
    private String serialNo;
    @SerializedName("purchaseDate")
    @Expose
    private String purchaseDate;
    @SerializedName("warrantyExpiryDate")
    @Expose
    private String warrantyExpiryDate;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("blockName")
    @Expose
    private String blockName;
    @SerializedName("lineName")
    @Expose
    private String lineName;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("inactive")
    @Expose
    private Boolean inactive;
    @SerializedName("changedBy")
    @Expose
    private Object changedBy;
    @SerializedName("changedType")
    @Expose
    private Integer changedType;
    @SerializedName("changedDate")
    @Expose
    private String changedDate;

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Boolean getIsRent() {
        return isRent;
    }

    public void setIsRent(Boolean isRent) {
        this.isRent = isRent;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(String warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
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

    public Object getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Object changedBy) {
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
        return "MachineInfo{" +
                "machineId=" + machineId +
                ", machineCode='" + machineCode + '\'' +
                ", brandName='" + brandName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", supplier='" + supplier + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", specification='" + specification + '\'' +
                ", isRent=" + isRent +
                ", serialNo='" + serialNo + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", warrantyExpiryDate='" + warrantyExpiryDate + '\'' +
                ", price=" + price +
                ", isActive=" + isActive +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", blockName='" + blockName + '\'' +
                ", lineName='" + lineName + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy=" + changedBy +
                ", changedType=" + changedType +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
