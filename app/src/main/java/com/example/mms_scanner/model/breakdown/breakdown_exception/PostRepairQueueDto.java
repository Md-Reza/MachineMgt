package com.example.mms_scanner.model.breakdown.breakdown_exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostRepairQueueDto {
    @SerializedName("MBHeaderDto")
    @Expose
    private MBHeaderDto mBHeaderDto;
    @SerializedName("MBRepairDetailDto")
    @Expose
    private List<MBRepairDetailDto> mbRepairDetailDtos = null;

    public MBHeaderDto getMBHeaderDto() {
        return mBHeaderDto;
    }

    public void setMBHeaderDto(MBHeaderDto mBHeaderDto) {
        this.mBHeaderDto = mBHeaderDto;
    }

    public List<MBRepairDetailDto> getMBRepairDetailDto() {
        return mbRepairDetailDtos;
    }

    public void setMBRepairDetailDto(List<MBRepairDetailDto> mbRepairDetailDtos) {
        this.mbRepairDetailDtos = mbRepairDetailDtos;
    }

    @Override
    public String toString() {
        return "PostRepairQueueDto{" +
                "mBHeaderDto=" + mBHeaderDto +
                ", mbRepairDetailDtos=" + mbRepairDetailDtos +
                '}';
    }
}
