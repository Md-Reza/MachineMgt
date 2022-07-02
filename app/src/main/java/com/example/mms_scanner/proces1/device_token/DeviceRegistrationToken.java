package com.example.mms_scanner.proces1.device_token;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceRegistrationToken {

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("passHash")
    @Expose
    private String passHash;
    @SerializedName("passSalt")
    @Expose
    private String passSalt;
    @SerializedName("appPermit")
    @Expose
    private Boolean appPermit;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("fullNameWithDept")
    @Expose
    private String fullNameWithDept;
    @SerializedName("empId")
    @Expose
    private String empId;
    @SerializedName("department")
    @Expose
    private Department department;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("deviceRegistration")
    @Expose
    private DeviceRegistration deviceRegistration;
    @SerializedName("reportToId")
    @Expose
    private String reportToId;
    @SerializedName("reportToName")
    @Expose
    private String reportToName;
    @SerializedName("offLoc")
    @Expose
    private String offLoc;
    @SerializedName("addInfo")
    @Expose
    private String addInfo;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public String getPassSalt() {
        return passSalt;
    }

    public void setPassSalt(String passSalt) {
        this.passSalt = passSalt;
    }

    public Boolean getAppPermit() {
        return appPermit;
    }

    public void setAppPermit(Boolean appPermit) {
        this.appPermit = appPermit;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFullNameWithDept() {
        return fullNameWithDept;
    }

    public void setFullNameWithDept(String fullNameWithDept) {
        this.fullNameWithDept = fullNameWithDept;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public DeviceRegistration getDeviceRegistration() {
        return deviceRegistration;
    }

    public void setDeviceRegistration(DeviceRegistration deviceRegistration) {
        this.deviceRegistration = deviceRegistration;
    }

    public String getReportToId() {
        return reportToId;
    }

    public void setReportToId(String reportToId) {
        this.reportToId = reportToId;
    }

    public String getReportToName() {
        return reportToName;
    }

    public void setReportToName(String reportToName) {
        this.reportToName = reportToName;
    }

    public String getOffLoc() {
        return offLoc;
    }

    public void setOffLoc(String offLoc) {
        this.offLoc = offLoc;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
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
        return "DeviceRegistrationToken{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", passHash='" + passHash + '\'' +
                ", passSalt='" + passSalt + '\'' +
                ", appPermit=" + appPermit +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", fullNameWithDept='" + fullNameWithDept + '\'' +
                ", empId='" + empId + '\'' +
                ", department=" + department +
                ", designation='" + designation + '\'' +
                ", deviceRegistration=" + deviceRegistration +
                ", reportToId='" + reportToId + '\'' +
                ", reportToName='" + reportToName + '\'' +
                ", offLoc='" + offLoc + '\'' +
                ", addInfo='" + addInfo + '\'' +
                ", isDeleted=" + isDeleted +
                ", inactive=" + inactive +
                ", changedBy='" + changedBy + '\'' +
                ", changedType='" + changedType + '\'' +
                ", changedDate='" + changedDate + '\'' +
                '}';
    }
}
