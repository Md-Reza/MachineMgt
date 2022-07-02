package com.example.mms_scanner.ui.status_report;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.status_report.MCBreakdownReport;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.ui.HomeFragment;
import com.example.mms_scanner.utils.SharedPref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pending_MachinesList_Fragment extends Fragment {

    FragmentManager fragmentManager;

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    View lnView, breView, viewSave, viewError;
    Dialog dialog, dialog1, dialogCustom;
    Button btnQueueStart, btnQueueCancel, btnOk;
    TextView txtMessage;

    SearchView searchView;
    RecyclerView inprogressList_recycaler_view, breakdown_exceptionQueueRecyclerView;

    List<MCBreakdownReport> machineBreakdownList;
    List<BreakdownQueueException> breakdownQueueExceptionList;

    MachineBreakdownQueueAdapter machineBreakdownAdapter;
    BreakdownQueueExceptionAdapter breakdownQueueExceptionAdapter;

    String uToken, UserName, MbhId, MachineCode, fromDate, toDate, section_Id;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pending__machines_list_, container, false);

        fragmentManager = getFragmentManager();

        lnView = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);

        breView = getActivity().getLayoutInflater().inflate(R.layout.dialog_breakdownqueue_exception_, null);
        breakdown_exceptionQueueRecyclerView = breView.findViewById(R.id.breakdown_exceptionQueueRecyclerView);
        btnQueueStart = breView.findViewById(R.id.btnQueueStart);
        btnQueueCancel = breView.findViewById(R.id.btnQueueCancel);

        viewSave = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);

        viewError = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
        btnOk = viewError.findViewById(R.id.btnOk);
        txtMessage = viewError.findViewById(R.id.txtMessage);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            machineBreakdown();
            mSwipeRefreshLayout.setRefreshing(false);
        });
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_purple),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_blue_bright)
        );

        uToken = SharedPref.read("UserToken", "");
        UserName = SharedPref.read("UserName", "");
        fromDate = SharedPref.read("fromDate", "");
        toDate = SharedPref.read("toDate", "");
        section_Id = SharedPref.read("section_Id", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        dialog = new Dialog(getActivity());
        dialog1 = new Dialog(getActivity());
        dialogCustom = new Dialog(getActivity());

        searchView = root.findViewById(R.id.searchView);
        inprogressList_recycaler_view = root.findViewById(R.id.inprogressList_recycaler_view);

        machineBreakdownList = new ArrayList<MCBreakdownReport>();
        breakdownQueueExceptionList = new ArrayList<BreakdownQueueException>();

        machineBreakdown();

        return root;
    }

    private void machineBreakdown() {
        try {
            spotsDialog.show();
            Call<List<MCBreakdownReport>> MachineBreakdownCall = retrofitApiInterface.getInProgressDateBySection("Bearer" + " " + uToken, section_Id);
            MachineBreakdownCall.enqueue(new Callback<List<MCBreakdownReport>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<MCBreakdownReport>> call, Response<List<MCBreakdownReport>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                machineBreakdownList = response.body();
                                machineBreakdownAdapter = new MachineBreakdownQueueAdapter(getContext(), machineBreakdownList);
                                inprogressList_recycaler_view.setHasFixedSize(true);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                inprogressList_recycaler_view.setLayoutManager(mLayoutManager);
                                inprogressList_recycaler_view.setAdapter(machineBreakdownAdapter);
                                machineBreakdownAdapter.notifyDataSetChanged();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setContentView(viewError);
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
                public void onFailure(Call<List<MCBreakdownReport>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryString) {
                    machineBreakdownAdapter.getFilter().filter(queryString);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String queryString) {
                    machineBreakdownAdapter.getFilter().filter(queryString);
                    return false;
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class MachineBreakdownQueueAdapter extends RecyclerView.Adapter<MachineBreakdownQueueAdapter.ViewHolder> implements Filterable {

        Context context;
        List<MCBreakdownReport> mcBreakdownReportList;
        List<MCBreakdownReport> filteredProgramList;

        public MachineBreakdownQueueAdapter(Context context, List<MCBreakdownReport> mcBreakdownReportList) {
            this.context = context;
            this.mcBreakdownReportList = mcBreakdownReportList;
            this.filteredProgramList = mcBreakdownReportList;
        }

        @NonNull
        @Override
        public MachineBreakdownQueueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.inprogress_report_recycalerview, parent, false);
            return new MachineBreakdownQueueAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MachineBreakdownQueueAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MCBreakdownReport machineBreakdownQueue = filteredProgramList.get(position);

            if (position % 2 == 1) {
                holder.itemLayout.setCardBackgroundColor(Color.WHITE);
            } else {
                holder.itemLayout.setCardBackgroundColor(Color.LTGRAY);
            }
            try {
                holder.txtMachineCode.setText(String.valueOf("M/C:"+machineBreakdownQueue.getMachineCode()));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.txtLineName.setText(" "+machineBreakdownQueue.getLineName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryName.setText("Category: "+machineBreakdownQueue.getCategoryName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.requestedBy.setText(machineBreakdownQueue.getRequestBy());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String startDT = machineBreakdownQueue.getRequestDate();
                SimpleDateFormat parser_startDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_startDT = formatterr_startDT.format(parser_startDT.parse(startDT));
                    holder.txtStartDateTime.setText(result_startDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String endDT = machineBreakdownQueue.getEndDateTime();
                SimpleDateFormat parser_endDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_endDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_endDT = formatterr_endDT.format(parser_endDT.parse(endDT));
                    holder.txtEndDateTime.setText(result_endDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String reqDT = machineBreakdownQueue.getRequestDate();
                SimpleDateFormat parser_reqDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_reqDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_reqDT = formatterr_reqDT.format(parser_reqDT.parse(reqDT));;
                    holder.requestedDateTime.setText(result_reqDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.txtDuration.setText(machineBreakdownQueue.getDuration());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.txtRepairBy.setText(machineBreakdownQueue.getRepairBy());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String status = machineBreakdownQueue.getStatus();
                holder.Status.setText(status);
                if (status.equals("3")) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FF6464"));
                    holder.Status.setText("Created");
                } else if (status.equals("2")) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFE162"));
                    holder.Status.setText("InProgress");
                } else if (status.equals("1")) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#65C18C"));
                } else {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            try {
                boolean confirnm = machineBreakdownQueue.getIsMechanicConfirm();
                holder.isMechanicConfirm.setEnabled(false);
                if (confirnm == true) {
                    holder.isMechanicConfirm.setChecked(true);
                } else {
                    holder.isMechanicConfirm.setChecked(false);
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            holder.imgDetails.setOnClickListener(view -> {
                try {
                    MbhId = machineBreakdownQueue.getMbhId();
                    MachineCode = machineBreakdownQueue.getMachineCode();
                    breakdownQueueException();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            });

        }

        @Override
        public int getItemCount() {
            return filteredProgramList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredProgramList = mcBreakdownReportList;
                    } else {
                        List<MCBreakdownReport> filteredList = new ArrayList<>();
                        for (MCBreakdownReport programNumber : mcBreakdownReportList) {
                            if (programNumber.getLineName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
                            filteredProgramList = filteredList;
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredProgramList;
                    Log.i("info", "results: " + results.values);
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredProgramList = (List<MCBreakdownReport>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView itemLayout;
            TextView txtLineName, txtMachineCode, CategoryName, requestedDateTime, requestedBy,
                    confirmDateTime, downTimeDuration, txtStartDateTime, txtEndDateTime, txtRepairBy,txtDuration,Status;
            ImageView imgDetails;
            CheckBox isMechanicConfirm;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemLayout = itemView.findViewById(R.id.itemLayout);
                txtLineName = itemView.findViewById(R.id.txtLineName);

                txtMachineCode = itemView.findViewById(R.id.txtMachineCode);
                CategoryName = itemView.findViewById(R.id.CategoryName);

                requestedDateTime = itemView.findViewById(R.id.requestedDateTime);
                requestedBy = itemView.findViewById(R.id.requestedBy);

                confirmDateTime = itemView.findViewById(R.id.confirmDateTime);
                downTimeDuration = itemView.findViewById(R.id.downTimeDuration);
                txtStartDateTime = itemView.findViewById(R.id.txtStartDateTime);
                txtEndDateTime = itemView.findViewById(R.id.txtEndDateTime);


                imgDetails = itemView.findViewById(R.id.imgDetails);
                Status = itemView.findViewById(R.id.Status);
                txtRepairBy = itemView.findViewById(R.id.txtRepairBy);
                txtDuration = itemView.findViewById(R.id.txtDuration);
            }
        }
    }

    private void breakdownQueueException() {
        try {
            spotsDialog.show();
            Call<List<BreakdownQueueException>> BreakdownQueueExceptionCall = retrofitApiInterface.getBreakdownQueueException("Bearer" + " " + uToken, MbhId);
            BreakdownQueueExceptionCall.enqueue(new Callback<List<BreakdownQueueException>>() {
                @Override
                public void onResponse(Call<List<BreakdownQueueException>> call, Response<List<BreakdownQueueException>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                breakdownQueueExceptionList = response.body();
                                dialog.setContentView(breView);
                                dialog.setCancelable(true);
                                btnQueueStart.setVisibility(View.GONE);
                                btnQueueCancel.setVisibility(View.GONE);
                                breakdownQueueExceptionAdapter = new BreakdownQueueExceptionAdapter(getContext(), breakdownQueueExceptionList);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                breakdown_exceptionQueueRecyclerView.setHasFixedSize(true);
                                breakdown_exceptionQueueRecyclerView.setLayoutManager(mLayoutManager);
                                breakdown_exceptionQueueRecyclerView.setAdapter(breakdownQueueExceptionAdapter);
                                breakdownQueueExceptionAdapter.notifyDataSetChanged();
                                dialog.show();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    showAlertDialog("Error: ", mError, getContext(), false);
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
                public void onFailure(Call<List<BreakdownQueueException>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class BreakdownQueueExceptionAdapter extends RecyclerView.Adapter<BreakdownQueueExceptionAdapter.ViewHolder> {

        Context context;
        List<BreakdownQueueException> breakdownQueueExceptionList;

        public BreakdownQueueExceptionAdapter(Context context, List<BreakdownQueueException> breakdownQueueExceptionList) {
            this.context = context;
            this.breakdownQueueExceptionList = breakdownQueueExceptionList;
        }

        @NonNull
        @Override
        public BreakdownQueueExceptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.breakdown_exception_queue_recyclerview, parent, false);
            return new BreakdownQueueExceptionAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BreakdownQueueExceptionAdapter.ViewHolder holder, int position) {
            final BreakdownQueueException breakdownQueueException = breakdownQueueExceptionList.get(position);
            holder.reasonQueueTxt.setText(breakdownQueueException.getExceptionReason().getReasonName());
        }

        @Override
        public int getItemCount() {
            return breakdownQueueExceptionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView exceptionQueueLayout;
            TextView reasonQueueTxt;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exceptionQueueLayout = itemView.findViewById(R.id.exceptionQueueLayout);
                reasonQueueTxt = itemView.findViewById(R.id.reasonQueueTxt);
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