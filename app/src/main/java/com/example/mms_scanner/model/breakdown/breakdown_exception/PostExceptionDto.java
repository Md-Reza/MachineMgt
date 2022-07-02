package com.example.mms_scanner.model.breakdown.breakdown_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostExceptionDto {

    @SerializedName("MBHeaderDto")
    @Expose
    private MBHeaderDto mBHeaderDto;
    @SerializedName("MBDetailDto")
    @Expose
    private List<MBDetailDto> mBDetailDto = null;

    public MBHeaderDto getMBHeaderDto() {
        return mBHeaderDto;
    }

    public void setMBHeaderDto(MBHeaderDto mBHeaderDto) {
        this.mBHeaderDto = mBHeaderDto;
    }

    public List<MBDetailDto> getMBDetailDto() {
        return mBDetailDto;
    }

    public void setMBDetailDto(List<MBDetailDto> mBDetailDto) {
        this.mBDetailDto = mBDetailDto;
    }

    @Override
    public String toString() {
        return "PostExceptionDto{" +
                "mBHeaderDto=" + mBHeaderDto +
                ", mBDetailDto=" + mBDetailDto +
                '}';
    }
}
