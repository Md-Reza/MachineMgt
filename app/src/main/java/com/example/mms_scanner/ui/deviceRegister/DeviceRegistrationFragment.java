package com.example.mms_scanner.ui.deviceRegister;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.line.GetLine;
import com.example.mms_scanner.proces1.RegistrationDevices;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.ui.HomeFragment;
import com.example.mms_scanner.utils.SharedPref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceRegistrationFragment extends Fragment {

    FragmentManager fragmentManager;

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    TextView txtMessage, txtSelectLine, txtUserId, txtDepartmentId, txtDeviceName, txtDeviceToken, txtIpAddress, txtMacAddress;
    Button btnRegistration;
    ImageButton backButton;

    String uToken, lineName, lineId, sectionId, userId, deptId, deviceName, ipAddress, macAddress, fcmToken;

    List<GetLine> getLines;
    Dialog dialog, dialogCustom;
    RecyclerView lineRecyclerView;
    SearchView searchView;
    LinesDialogAdapter linesDialogAdapter;
    Button btnOk;

    public DeviceRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_registration, container, false);

        fragmentManager = getFragmentManager();

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.container, homeFragment, homeFragment.getTag()).commit();
        });

        uToken = SharedPref.read("UserToken", "");

        txtSelectLine = root.findViewById(R.id.txtSelectLine);
        line();
        getLines = new ArrayList<GetLine>();
        dialog = new Dialog(getActivity());
        dialogCustom = new Dialog(getActivity());

        txtUserId = root.findViewById(R.id.txtUserId);
        userId = SharedPref.read("userId", "");
        txtUserId.setText("User Id: " + " " + userId);

        txtDepartmentId = root.findViewById(R.id.txtDepartmentId);
        deptId = SharedPref.read("deptId", "");
        txtDepartmentId.setText("Department Id: " + " " + deptId);

        txtDeviceName = root.findViewById(R.id.txtDeviceName);
        deviceName = SharedPref.read("deviceName", "");
        txtDeviceName.setText("Device Name: " + " " + deviceName);

        txtIpAddress = root.findViewById(R.id.txtIpAddress);
        ipAddress = SharedPref.read("ipAddress", "");
        txtIpAddress.setText("Ip Address: " + " " + ipAddress);

        txtMacAddress = root.findViewById(R.id.txtMacAddress);
        macAddress = SharedPref.read("macAddress", "");
        txtMacAddress.setText("Mac Address: " + " " + macAddress);

        txtDeviceToken = root.findViewById(R.id.txtDeviceToken);
        fcmToken = SharedPref.read("fcmToken", "");
        txtDeviceToken.setText("FCM Token: " + " " + fcmToken);

        btnRegistration = root.findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(view -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100000", "Create");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                RegistrationDevices deviceRegistration = new RegistrationDevices();
                                deviceRegistration.setSectionId(sectionId);
                                deviceRegistration.setLineId(lineId);
                                deviceRegistration.setUserId(userId);
                                deviceRegistration.setDeptId(deptId);
                                deviceRegistration.setDeviceName(deviceName);
                                deviceRegistration.setIPAdd(ipAddress);
                                deviceRegistration.setMAKAdd(macAddress);
                                deviceRegistration.setDeviceTocken(fcmToken);
                                Log.i("info", "DeviceRegistration: " + deviceRegistration);
                                Call<String> deviceRegisterCall = retrofitApiInterface.deviceRegister("Bearer" + " " + uToken, deviceRegistration);
                                deviceRegisterCall.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        try {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    spotsDialog.dismiss();
                                                    String mSuccess = response.body().toString();
                                                    mSuccess = mSuccess.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                    mSuccess = mSuccess.replace("\\r\\nত্রুটি :", "");
                                                    dialogCustom.setCancelable(false);
                                                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);
                                                    dialogCustom.setContentView(dialogView);
                                                    TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
                                                    txtMessage.setText(mSuccess);
                                                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                                                    btnOk.setOnClickListener(v1 -> {
                                                        dialogCustom.dismiss();
                                                    });
                                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                    dialogCustom.show();
                                                }
                                            } else if (!response.isSuccessful()) {
                                                if (response.errorBody() != null) {
                                                    spotsDialog.dismiss();
                                                    Gson gson = new GsonBuilder().create();
                                                    try {
                                                        String mError = gson.toJson(response.errorBody().string());
                                                        mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                        mError = mError.replace("\\r\\nত্রুটি :", "");
                                                        dialogCustom.setCancelable(false);
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
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setCancelable(false);
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

        return root;
    }

    private void line() {
        txtSelectLine.setOnClickListener(v -> {
            Call<List<GetLine>> soList = retrofitApiInterface.getLines("Bearer" + " " + uToken);
            soList.enqueue(new Callback<List<GetLine>>() {
                @Override
                public void onResponse(Call<List<GetLine>> call, Response<List<GetLine>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                getLines = response.body();
                                if (!getLines.isEmpty()) {
                                    dialog.setCancelable(true);
                                    View lnView = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);
                                    dialog.setContentView(lnView);
                                    lineRecyclerView = lnView.findViewById(R.id.line_list_dialog_layout_RecyclerView);
                                    searchView = lnView.findViewById(R.id.searchView);
                                    linesDialogAdapter = new LinesDialogAdapter(getContext(), getLines);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    lineRecyclerView.setHasFixedSize(true);
                                    lineRecyclerView.setLayoutManager(mLayoutManager);
                                    lineRecyclerView.setAdapter(linesDialogAdapter);
                                    linesDialogAdapter.notifyDataSetChanged();
                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String queryString) {
                                            linesDialogAdapter.getFilter().filter(queryString);
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String queryString) {
                                            linesDialogAdapter.getFilter().filter(queryString);
                                            return false;
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    showAlertDialog("Error: ", "No data found.", getContext(), false);
                                }
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setCancelable(false);
                                    View view = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                    btnOk = view.findViewById(R.id.btnOk);
                                    dialogCustom.setContentView(view);
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    txtMessage = view.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mError);
                                    dialogCustom.show();
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
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
                public void onFailure(Call<List<GetLine>> call, Throwable t) {
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        });
    }

    public class LinesDialogAdapter extends RecyclerView.Adapter<LinesDialogAdapter.ViewHolder> implements Filterable {

        Context context;
        List<GetLine> lineList;
        List<GetLine> filteredLineList;

        public LinesDialogAdapter(Context context, List<GetLine> lineList) {
            this.context = context;
            this.lineList = lineList;
            this.filteredLineList = lineList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredLineList = lineList;
                    } else {
                        List<GetLine> filteredList = new ArrayList<>();
                        for (GetLine line : lineList) {

                            if (line.getLineId().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(line);
                            }
                            if (line.getLineName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(line);
                            }
                            filteredLineList = filteredList;
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredLineList;
                    Log.i("info", "results: " + results.values);
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredLineList = (List<GetLine>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.line_list_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final GetLine so_number = filteredLineList.get(position);
            holder.txtLineCode.setText(so_number.getLineId().toString());
            holder.txtLinesName.setText(so_number.getLineName().toString());
            holder.line_dialog_layout.setOnClickListener(v -> {
                sectionId = filteredLineList.get(position).getSections().getSectionId();
                lineId = filteredLineList.get(position).getLineId();
                lineName = filteredLineList.get(position).getLineName();
                txtSelectLine.setText(lineName);
                dialog.dismiss();
            });

            try {
//                Random r = new Random();
//                holder.cardSection.setCardBackgroundColor(Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256)));
                if (position % 2 == 1) {
                    holder.cardSection.setCardBackgroundColor(getResources().getColor(R.color.hash));
                } else
                    holder.cardSection.setCardBackgroundColor(getResources().getColor(R.color.lihtgreenLine2));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return filteredLineList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtLineCode, txtLinesName;
            public LinearLayout line_dialog_layout;
            public CardView cardSection;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                line_dialog_layout = itemView.findViewById(R.id.line_list_dialog_layout);
                cardSection = itemView.findViewById(R.id.cardSection);
                txtLineCode = itemView.findViewById(R.id.txtLinesCode);
                txtLinesName = itemView.findViewById(R.id.txtLinesName);
            }
        }

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