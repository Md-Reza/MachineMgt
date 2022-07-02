package com.example.mms_scanner.retrofit;

import com.example.mms_scanner.model.breakdown.breakdown_exception.BreakdownException;
import com.example.mms_scanner.model.breakdown.breakdown_exception.PostRepairQueueDto;
import com.example.mms_scanner.model.breakdown.machine_breakdown.MachineBreakdown;
import com.example.mms_scanner.model.breakdown.breakdown_exception.PostExceptionDto;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MBRepairDetail;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MachineBreakdownQueue;
import com.example.mms_scanner.model.breakdownqueue.UpdateQueue;
import com.example.mms_scanner.model.line.GetLine;
import com.example.mms_scanner.model.login.AllSection;
import com.example.mms_scanner.model.login.GetLogin;
import com.example.mms_scanner.model.login.PostLogin;
import com.example.mms_scanner.model.machine.MachineInfo;
import com.example.mms_scanner.model.machine.MachineMvtDto;
import com.example.mms_scanner.model.status_report.MCBreakdownReport;
import com.example.mms_scanner.model.user.GetUser;
import com.example.mms_scanner.proces1.RegistrationDevices;
import com.example.mms_scanner.proces1.device_token.DeviceRegistrationToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("Auth/Login")
    Call<GetLogin> getToken(@Body PostLogin postLogin);

    @GET("User/GetUserByUserName/{id}")
    Call<GetUser> getUser(@Header("Authorization") String authHeader, @Path("id") String id);

    @GET("ObjScrn/CheckPermission/{ObjCode}")
    Call<String> getViewer(@Header("Authorization") String authHeader, @Path("ObjCode") String ObjCode, @Query("comd") String comd);

    @GET("MasterData/GetLine/{lineId}")
    Call<GetLine> getLineMasterData(@Header("Authorization") String authHeader, @Path("lineId") String lineId);

    @GET("MasterData/GetLines")
    Call<List<GetLine>> getLines(@Header("Authorization") String authHeader);

    @GET("DataService/GetSections")
    Call<List<AllSection>> getAllSection();

    @GET("MachineMgt/GetMachineInfoByMachineCode/{machineCode}")
    Call<MachineInfo> getMachineInfo(@Header("Authorization") String authHeader, @Path("machineCode") String machineCode);

    @GET("MachineMgt/GetMachineInfoByLineName/{lineName}")
    Call<List<MachineInfo>> getMachineInfoByLine(@Header("Authorization") String authHeader, @Path("lineName") String lineName);

    @POST("MachineMgt/CreateMachineInfoMvt")
    Call<String> machineMvtDtoSave(@Header("Authorization") String authHeader, @Body List<MachineMvtDto> postMachineMvtDto);

    @GET("MachineMgt/GetMachineInfoByLineName/{lineName}")
    Call<List<MachineBreakdown>> machineBreakdown(@Header("Authorization") String authHeader, @Path("lineName") String lineName);

    @GET("MasterData/GetExceptionReasonBySource/{PROBLEMTYPE}")
    Call<List<BreakdownException>> breakdownException(@Header("Authorization") String authHeader, @Path("PROBLEMTYPE") String PROBLEMTYPE);

    @GET("MasterData/GetExceptionReasonBySource/{REPAIRTYPE}")
    Call<List<BreakdownException>> solutionException(@Header("Authorization") String authHeader, @Path("REPAIRTYPE") String REPAIRTYPE);

    @POST("MachineMgt/CreateMB")
    Call<String> createMB(@Header("Authorization") String authHeader, @Body PostExceptionDto postExceptionDto);

    @PUT("MachineMgt/UpdateRepairQueueMB")
    Call<String> updateRepairQueueMB(@Header("Authorization") String authHeader, @Body PostRepairQueueDto postRepairQueueDto);

    @PUT("MachineMgt/ApprovalQueueMB")
    Call<String> approvalQueueMB(@Header("Authorization") String authHeader, @Body UpdateQueue updateQueue);

    @GET("MachineMgt/GetMBPendingList/{sectionId}")
    Call<List<MachineBreakdownQueue>> machineBreakdownQueue(@Header("Authorization") String authHeader, @Path("sectionId") String sectionId);

    @GET("MachineMgt/GetInitiateMBList/{sectionId}")
    Call<List<MachineBreakdownQueue>> getInitiateBreakdownQueue(@Header("Authorization") String authHeader, @Path("sectionId") String sectionId);

    @GET("MachineMgt/GetMBDetailByMachineCode/{machineCode}")
    Call<List<BreakdownQueueException>> breakdownQueueException(@Header("Authorization") String authHeader, @Path("machineCode") String machineCode);

    @GET("MachineMgt/GetMBDetailByCode/{mbhId}")
    Call<List<BreakdownQueueException>> getBreakdownQueueException(@Header("Authorization") String authHeader, @Path("mbhId") String mbhId);

    @GET("MachineMgt/GetMBRepairDetailByCode/{mbhId}")
    Call<List<MBRepairDetail>> getRepairBreakdownQueueException(@Header("Authorization") String authHeader, @Path("mbhId") String mbhId);

    @GET("MachineMgt/GetMBRepairDetailByCode/{mbhId}")
    Call<List<BreakdownQueueException>> getBreakdownRepairQueueException(@Header("Authorization") String authHeader, @Path("mbhId") String mbhId);

    @PUT("MachineMgt/UpdateQueueMB")
    Call<String> updateQueue(@Header("Authorization") String authHeader, @Body UpdateQueue updateQueue);

    @PUT("MachineMgt/CompleteQueueMB")
    Call<String> completeQueueMB(@Header("Authorization") String authHeader, @Body UpdateQueue updateQueue);

    @GET("MachineMgt/GetMechanicCompletedListByLine/{lineId}")
    Call<List<MachineBreakdownQueue>> getMechanicCompletedListByLine(@Header("Authorization") String authHeader, @Path("lineId") String lineId);

    @GET("MachineMgt/GetMBCompletedListByDateLine/{fromDate}/{lineId}")
    Call<List<MachineBreakdownQueue>> getCompleteListDateLine(@Header("Authorization") String authHeader, @Path("fromDate") String fromDate, @Path("lineId") String lineId);

    @GET("MachineMgt/GetPendingListByDateLine/{fromDate}/{lineId}")
    Call<List<MachineBreakdownQueue>> getInCompleteListDateLine(@Header("Authorization") String authHeader, @Path("fromDate") String fromDate, @Path("lineId") String lineId);

    @POST("User/CreateDeviceRegistration")
    Call<String> deviceRegister(@Header("Authorization") String authHeader, @Body RegistrationDevices deviceRegistration);

    @GET("User/GetAllDeviceRegistrationByDept/{deptId}/{sectionId}")
    Call<List<DeviceRegistrationToken>> drToken(@Header("Authorization") String authHeader, @Path("deptId") String deptId, @Path("sectionId") String sectionId);

    @GET("GridReport/GetMechanicCompletedListByDateSection/{fromDate}/{toDate}/{sectionId}")
    Call<List<MCBreakdownReport>> getCompleteListDateBySection(@Header("Authorization") String authHeader, @Path("fromDate") String fromDate, @Path("toDate") String toDate, @Path("sectionId") String sectionId);

    @GET("GridReport/GetMCInProgressListByDateSection/{sectionId}")
    Call<List<MCBreakdownReport>> getInProgressDateBySection(@Header("Authorization") String authHeader, @Path("sectionId") String sectionId);

    @GET("GridReport/GetMCInitiatedListByDateSection/{sectionId}")
    Call<List<MCBreakdownReport>> getMCInitiatedListDateBySection(@Header("Authorization") String authHeader, @Path("sectionId") String sectionId);

    @GET("GridReport/GetTobeConfirmListByDateSection/{fromDate}/{toDate}/{sectionId}")
    Call<List<MCBreakdownReport>> gettoBeVerfiedListDateBySection(@Header("Authorization") String authHeader, @Path("fromDate") String fromDate, @Path("toDate") String toDate, @Path("sectionId") String sectionId);
}