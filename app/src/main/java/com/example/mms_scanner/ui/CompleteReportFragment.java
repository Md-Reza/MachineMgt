package com.example.mms_scanner.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MachineBreakdownQueue;
import com.example.mms_scanner.model.line.GetLine;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.utils.SharedPref;
import com.example.mms_scanner.ui.HomeFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteReportFragment extends Fragment {

    FragmentManager fragmentManager;
    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    View lnView, breView, viewError;
    SearchView search_view, searchView;
    RecyclerView completeList_recycaler_view, breakdown_exceptionQueueRecyclerView, lineRecyclerView;

    List<GetLine> getLines;
    List<MachineBreakdownQueue> machineBreakdownList;
    List<BreakdownQueueException> breakdownQueueExceptionList;
    LinesDialogAdapter linesDialogAdapter;
    MachineBreakdownQueueAdapter machineBreakdownAdapter;
    BreakdownQueueExceptionAdapter breakdownQueueExceptionAdapter;

    Dialog dialog, dialog1, dialogCustom;
    private DatePickerDialog picker;
    TextView dateTxtView, selectLineText;
    Button btnShow, btnOk, btnQueueStart, btnQueueCancel;
    String uToken, MbhId, MachineCode, lineName, lineId;
    TextView txtMessage;
    ImageButton backButton;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_complete_report, container, false);

        fragmentManager = getFragmentManager();
        lnView = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);
        breView = getActivity().getLayoutInflater().inflate(R.layout.dialog_breakdownqueue_exception_, null);
        viewError = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
        viewError = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        btnQueueStart = breView.findViewById(R.id.btnQueueStart);
        btnQueueCancel = breView.findViewById(R.id.btnQueueCancel);

        uToken = SharedPref.read("UserToken", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.container, homeFragment, homeFragment.getTag()).commit();
        });

        completeList_recycaler_view = root.findViewById(R.id.completeList_recycaler_view);
        completeList_recycaler_view = root.findViewById(R.id.completeList_recycaler_view);
        search_view = root.findViewById(R.id.search_view);
        btnShow = root.findViewById(R.id.btnShow);
        dateTxtView = root.findViewById(R.id.dateTxtView);
        selectLineText = root.findViewById(R.id.selectLineText);
        dialogCustom = new Dialog(getActivity());
        dialog = new Dialog(getActivity());
        dialog1 = new Dialog(getActivity());

        getLines = new ArrayList<GetLine>();
        machineBreakdownList = new ArrayList<MachineBreakdownQueue>();
        breakdownQueueExceptionList = new ArrayList<BreakdownQueueException>();

        line();
        getDateTime();
        machineBreakdown();

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

        return root;
    }

    private void getDateTime() {
        final Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        dateTxtView.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(yy).append("-").append(mm + 1).append("-")
                .append(dd));
        dateTxtView.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> dateTxtView.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth), year, month, day);
            picker.show();
        });
    }

    private void line() {
        selectLineText.setOnClickListener(v -> {
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
                public void onFailure(Call<List<GetLine>> call, Throwable t) {
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        });
    }

    private void machineBreakdown() {
        btnShow.setOnClickListener(v -> {
            String line_Name = selectLineText.getText().toString();
            String date = dateTxtView.getText().toString();

            if (line_Name.isEmpty()) {
                showAlertDialog("Error: ", "Select Line.", getContext(), false);
                return;
            }
            try {
                spotsDialog.show();
                Call<List<MachineBreakdownQueue>> MachineBreakdownCall = retrofitApiInterface.getCompleteListDateLine("Bearer" + " " + uToken, date, lineId);
                MachineBreakdownCall.enqueue(new Callback<List<MachineBreakdownQueue>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<List<MachineBreakdownQueue>> call, Response<List<MachineBreakdownQueue>> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    spotsDialog.dismiss();
                                    machineBreakdownList = response.body();
                                    machineBreakdownAdapter = new MachineBreakdownQueueAdapter(getContext(), machineBreakdownList);
                                    completeList_recycaler_view.setHasFixedSize(true);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    completeList_recycaler_view.setLayoutManager(mLayoutManager);
                                    completeList_recycaler_view.setAdapter(machineBreakdownAdapter);
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

                                        btnOk = viewError.findViewById(R.id.btnOk);
                                        dialogCustom.setContentView(viewError);
                                        dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        txtMessage = viewError.findViewById(R.id.txtMessage);
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
                    public void onFailure(Call<List<MachineBreakdownQueue>> call, Throwable t) {
                        spotsDialog.dismiss();
                        showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                    }
                });
                search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        });
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
                                Log.i("info", "list: " + breakdownQueueExceptionList);
                                dialog.setContentView(breView);
                                dialog.setCancelable(true);
                                btnQueueStart.setVisibility(View.GONE);
                                btnQueueCancel.setVisibility(View.GONE);
                                breakdownQueueExceptionAdapter = new BreakdownQueueExceptionAdapter(getContext(), breakdownQueueExceptionList);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                breakdown_exceptionQueueRecyclerView = breView.findViewById(R.id.breakdown_exceptionQueueRecyclerView);
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
        public LinesDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.line_list_layout, parent, false);
            return new LinesDialogAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LinesDialogAdapter.ViewHolder holder, int position) {
            final GetLine so_number = filteredLineList.get(position);
            holder.txtLineCode.setText(so_number.getLineId().toString());
            holder.txtLinesName.setText(so_number.getLineName().toString());
            holder.so_number_dialog_layout.setOnClickListener(v -> {
                lineId = filteredLineList.get(position).getLineId();
                lineName = filteredLineList.get(position).getLineName();
                selectLineText.setText(lineName);
                dialog.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return filteredLineList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtLineCode, txtLinesName;
            public LinearLayout so_number_dialog_layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                so_number_dialog_layout = itemView.findViewById(R.id.line_list_dialog_layout);
                txtLineCode = itemView.findViewById(R.id.txtLinesCode);
                txtLinesName = itemView.findViewById(R.id.txtLinesName);
            }
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

    public class MachineBreakdownQueueAdapter extends RecyclerView.Adapter<MachineBreakdownQueueAdapter.ViewHolder> implements Filterable {

        Context context;
        List<MachineBreakdownQueue> machineBreakdownQueueList;
        List<MachineBreakdownQueue> filteredProgramList;

        public MachineBreakdownQueueAdapter(Context context, List<MachineBreakdownQueue> machineBreakdownQueueList) {
            this.context = context;
            this.machineBreakdownQueueList = machineBreakdownQueueList;
            this.filteredProgramList = machineBreakdownQueueList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredProgramList = machineBreakdownQueueList;
                    } else {
                        List<MachineBreakdownQueue> filteredList = new ArrayList<>();
                        for (MachineBreakdownQueue programNumber : machineBreakdownQueueList) {
                            if (programNumber.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
                            if (programNumber.getLines().getLineName().toLowerCase().contains(charSequenceString.toLowerCase())) {
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
                    filteredProgramList = (List<MachineBreakdownQueue>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public MachineBreakdownQueueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.complete_report_recycalerview, parent, false);
            return new MachineBreakdownQueueAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MachineBreakdownQueueAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MachineBreakdownQueue machineBreakdownQueue = filteredProgramList.get(position);

            if (position % 2 == 1) {
                holder.itemLayout.setBackgroundColor(Color.WHITE);
            } else {
                holder.itemLayout.setBackgroundColor(Color.LTGRAY);
            }
            try {
                holder.txtMachineCode.setText(String.valueOf(machineBreakdownQueue.getMachineCode()));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.downTimeDuration.setText(machineBreakdownQueue.getDuration());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryName.setText(machineBreakdownQueue.getMachineInfo().getCategoryName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String startDT = machineBreakdownQueue.getStartDateTime();
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
                    String result_reqDT = formatterr_reqDT.format(parser_reqDT.parse(reqDT));
                    holder.requestedDateTime.setText(result_reqDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String comDT = machineBreakdownQueue.getConfirmDate();
                SimpleDateFormat parser_comDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_comDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_reqDT = formatterr_comDT.format(parser_comDT.parse(comDT));
                    holder.confirmDateTime.setText(result_reqDT);
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
                holder.txtRepairBy.setText("Repair By: " + machineBreakdownQueue.getRepairBy());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                boolean confirnm = machineBreakdownQueue.getIsSupvConfirm();
                holder.isSupConfirm.setEnabled(false);
                if (confirnm == true) {
                    holder.isSupConfirm.setChecked(true);
                } else {
                    holder.isSupConfirm.setChecked(false);
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView itemLayout;
            TextView txtLineName, txtMachineCode, CategoryName, requestedDateTime, requestedBy,
                    confirmDateTime, downTimeDuration, txtStartDateTime, txtEndDateTime, txtRepairBy,txtDuration;
            ImageView imgDetails;
            CheckBox isSupConfirm;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemLayout = itemView.findViewById(R.id.itemLayout);
                txtMachineCode = itemView.findViewById(R.id.txtMachineCode);

                txtLineName = itemView.findViewById(R.id.txtLineName);
                CategoryName = itemView.findViewById(R.id.CategoryName);

                requestedDateTime = itemView.findViewById(R.id.requestedDateTime);
                requestedBy = itemView.findViewById(R.id.requestedBy);

                confirmDateTime = itemView.findViewById(R.id.confirmDateTime);
                downTimeDuration = itemView.findViewById(R.id.downTimeDuration);
                txtStartDateTime = itemView.findViewById(R.id.txtStartDateTime);
                txtEndDateTime = itemView.findViewById(R.id.txtEndDateTime);

                txtDuration = itemView.findViewById(R.id.txtDuration);

                imgDetails = itemView.findViewById(R.id.imgDetails);
                txtRepairBy = itemView.findViewById(R.id.txtRepairBy);
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