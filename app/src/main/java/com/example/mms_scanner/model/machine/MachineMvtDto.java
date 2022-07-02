package com.example.mms_scanner.model.machine;

public class MachineMvtDto {
    public String MachineCode;
    public String FromSectionName;
    public String FromLineName;
    public String ToBlockName;
    public String ToLineName;
    public String DeviceName;
    public String IPAdd;
    public String MAKAdd;

    public String getMachineCode() {
        return MachineCode;
    }

    public void setMachineCode(String machineCode) {
        MachineCode = machineCode;
    }

    public String getFromBlockName() {
        return FromSectionName;
    }

    public void setFromBlockName(String fromSectionName) {
        FromSectionName = fromSectionName;
    }

    public String getFromLineName() {
        return FromLineName;
    }

    public void setFromLineName(String fromLineName) {
        FromLineName = fromLineName;
    }

    public String getToBlockName() {
        return ToBlockName;
    }

    public void setToBlockName(String toBlockName) {
        ToBlockName = toBlockName;
    }

    public String getToLineName() {
        return ToLineName;
    }

    public void setToLineName(String toLineName) {
        ToLineName = toLineName;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getIPAdd() {
        return IPAdd;
    }

    public void setIPAdd(String IPAdd) {
        this.IPAdd = IPAdd;
    }

    public String getMAKAdd() {
        return MAKAdd;
    }

    public void setMAKAdd(String MAKAdd) {
        this.MAKAdd = MAKAdd;
    }

    @Override
    public String toString() {
        return "MachineMvtDto{" +
                "MachineCode='" + MachineCode + '\'' +
                ", FromSectionName='" + FromSectionName + '\'' +
                ", FromLineName='" + FromLineName + '\'' +
                ", ToBlockName='" + ToBlockName + '\'' +
                ", ToLineName='" + ToLineName + '\'' +
                ", DeviceName='" + DeviceName + '\'' +
                ", IPAdd='" + IPAdd + '\'' +
                ", MAKAdd='" + MAKAdd + '\'' +
                '}';
    }
}
