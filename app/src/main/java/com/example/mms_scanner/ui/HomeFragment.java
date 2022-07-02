package com.example.mms_scanner.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.user.GetUser;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.ui.deviceRegister.DeviceRegistrationFragment;
import com.example.mms_scanner.ui.status_report.McDashboardFragment;
import com.example.mms_scanner.utils.SharedPref;
import com.example.mms_scanner.view.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentManager fragmentManager;

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    ImageButton backButton, logOutBtn;
    TextView name, designation;

    CardView crdMachineMovement, crdMachineBreakDown, crdMachineBreakDownQueue, crdAcceptPendingList;
    LinearLayout llReportsOnCompleteList, llReportsOnInprogressList, llDeviceRegistration,mcDashboard;

    String uToken, UserID, Password;

    String userId, deptId, deviceName, ipAddress, macAddress, fcmToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.logout_message, null);

        fragmentManager = getFragmentManager();

        UserID = SharedPref.read("UserID", "");
        Password = SharedPref.read("Password", "");
        uToken = SharedPref.read("UserToken", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        Dialog dialogCustom = new Dialog(getActivity());
        dialogCustom.setCancelable(false);

        alertbox = new AlertDialog.Builder(getContext());
        name = root.findViewById(R.id.name);
        designation = root.findViewById(R.id.designation);

        Call<GetUser> getUserCall = retrofitApiInterface.getUser("Bearer" + " " + uToken, UserID);
        getUserCall.enqueue(new Callback<GetUser>() {
            @Override
            public void onResponse(Call<GetUser> call, Response<GetUser> response) {
                if (response.body() != null) {

                    userId = response.body().getUserId();
                    SharedPref.write("userId", userId);
                    deptId = response.body().getDepartment().getDeptId();
                    SharedPref.write("deptId", deptId);

                    name.setText(response.body().getFullName());
                    String UserName = name.getText().toString();
                    SharedPref.write("UserName", UserName);

                    String LoginName = response.body().getUserName().toLowerCase(Locale.ROOT);
                    Log.i("info","LoginName: "+LoginName);
                    SharedPref.write("LoginName", LoginName);

                    designation.setText(String.valueOf(response.body().getDesignation().getDegName()));
                    String Designation = designation.getText().toString();
                    SharedPref.write("Designation", Designation);
                }
            }

            @Override
            public void onFailure(Call<GetUser> call, Throwable t) {
                t.printStackTrace();
            }
        });


        logOutBtn = root.findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(v -> {

            dialogCustom.setContentView(view);
            TextView txtMessage = view.findViewById(R.id.txtMessage);
            txtMessage.setText("Are you sure, you want to exit?");
            Button btnOk = view.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(v1 -> {
                dialogCustom.dismiss();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            });
            Button btnClose = view.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(v1 -> {
                dialogCustom.dismiss();
            });
            dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogCustom.show();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                dialogCustom.setContentView(view);
                TextView txtMessage = view.findViewById(R.id.txtMessage);
                txtMessage.setText("Are you sure, you want to exit?");
                Button btnOk = view.findViewById(R.id.btnOk);
                btnOk.setOnClickListener(v1 -> {
                    dialogCustom.dismiss();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                });
                Button btnClose = view.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(v1 -> {
                    dialogCustom.dismiss();
                });
                dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogCustom.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        crdMachineMovement = root.findViewById(R.id.crdMachineMovement);
        crdMachineMovement.setOnClickListener(v -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100096", "View");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                Machine_movement_Fragment machine_movement_fragment = new Machine_movement_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.container, machine_movement_fragment, machine_movement_fragment.getTag()).commit();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");

                                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                    dialogCustom.setContentView(dialogView);
                                    TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mError);
                                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialogCustom.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                    Log.i("info", "Failed: " + t);
                }
            });
        });

        crdMachineBreakDown = root.findViewById(R.id.crdMachineBreakDown);
        crdMachineBreakDown.setOnClickListener(v -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100097", "View");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                MachineBreakDown_Fragment machineBreakDown_fragment = new MachineBreakDown_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.container, machineBreakDown_fragment, machineBreakDown_fragment.getTag()).commit();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");

                                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                    dialogCustom.setContentView(dialogView);
                                    TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mError);
                                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialogCustom.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                    Log.i("info", "Failed: " + t);
                }
            });
        });

        crdMachineBreakDownQueue = root.findViewById(R.id.crdMachineBreakDownQueue);
        crdMachineBreakDownQueue.setOnClickListener(v -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100098", "View");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                MachineBreakDownQueue_Fragment machineBreakDownQueue_fragment = new MachineBreakDownQueue_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.container, machineBreakDownQueue_fragment, machineBreakDownQueue_fragment.getTag()).commit();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");

                                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                    dialogCustom.setContentView(dialogView);
                                    TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mError);
                                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialogCustom.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                    Log.i("info", "Failed: " + t);
                }
            });
        });

        crdAcceptPendingList = root.findViewById(R.id.crdAcceptPendingList);
        crdAcceptPendingList.setOnClickListener(v -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100097", "View");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                Pending_Accept_Fragment pending_accept_fragment = new Pending_Accept_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.container, pending_accept_fragment, pending_accept_fragment.getTag()).commit();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");

                                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                    dialogCustom.setContentView(dialogView);
                                    TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mError);
                                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialogCustom.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                    Log.i("info", "Failed: " + t);
                }
            });
        });

        llReportsOnCompleteList = root.findViewById(R.id.llReportsOnCompleteList);
        llReportsOnCompleteList.setOnClickListener(v -> {
            CompleteReportFragment completeReportFragment = new CompleteReportFragment();
            fragmentManager.beginTransaction().replace(R.id.container, completeReportFragment, completeReportFragment.getTag()).commit();
        });

        llReportsOnInprogressList = root.findViewById(R.id.llReportsOnInprogressList);
        llReportsOnInprogressList.setOnClickListener(v -> {
            InProgressReportFragment inProgressReportFragment = new InProgressReportFragment();
            fragmentManager.beginTransaction().replace(R.id.container, inProgressReportFragment, inProgressReportFragment.getTag()).commit();
        });

        llDeviceRegistration = root.findViewById(R.id.llDeviceRegistration);
        llDeviceRegistration.setOnClickListener(v -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100000", "View");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                DeviceRegistrationFragment deviceRegistrationFragment = new DeviceRegistrationFragment();
                                fragmentManager.beginTransaction().replace(R.id.container, deviceRegistrationFragment, deviceRegistrationFragment.getTag()).commit();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");

                                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                    dialogCustom.setContentView(dialogView);
                                    TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mError);
                                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialogCustom.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                    Log.i("info", "Failed: " + t);
                }
            });
        });

        mcDashboard = root.findViewById(R.id.mcDashboard);
        mcDashboard.setOnClickListener(v -> {
            McDashboardFragment mcDashboardFragment = new McDashboardFragment();
            fragmentManager.beginTransaction().replace(R.id.container, mcDashboardFragment, mcDashboardFragment.getTag()).commit();
        });

        try {
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            deviceName = myDevice.getName();
            SharedPref.write("deviceName", deviceName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ipAddress = getLocalIpAddress();
        SharedPref.write("ipAddress", ipAddress);
        macAddress = getMacAddress();
        SharedPref.write("macAddress", macAddress);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fcmToken = task.getResult();
                        SharedPref.write("fcmToken", fcmToken);
                    }
                });

        return root;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static void showAlertDialog(final String title, String message,
                                       final Context context, final boolean redirectToPreviousScreen) {
        if (alertDialog != null && alertDialog.isShowing()) {
        } else {
            alertbox = new AlertDialog.Builder(context);
            alertbox.setMessage(message);
            alertbox.setTitle(title);
            alertbox.setNeutralButton("Ok", (arg0, arg1) -> alertDialog.dismiss());
            alertDialog = alertbox.create();
            alertDialog.show();
        }
    }
}