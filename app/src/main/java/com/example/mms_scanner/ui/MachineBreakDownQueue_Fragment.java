package com.example.mms_scanner.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import android.widget.Toast;


import com.example.mms_scanner.R;
import com.example.mms_scanner.interfaces.OnItemClickListener;
import com.example.mms_scanner.model.breakdown.breakdown_exception.BreakdownException;
import com.example.mms_scanner.model.breakdown.breakdown_exception.MBRepairDetailDto;
import com.example.mms_scanner.model.breakdown.breakdown_exception.MBHeaderDto;
import com.example.mms_scanner.model.breakdown.breakdown_exception.PostRepairQueueDto;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MachineBreakdownQueue;
import com.example.mms_scanner.model.breakdownqueue.UpdateQueue;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.utils.SharedPref;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.textfield.TextInputEditText;
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

public class MachineBreakDownQueue_Fragment extends Fragment {

    LinearLayout mcBreakDownQueue_layout;
    TextInputEditText machineCodeEditTxt;
    ImageView imgMcScanner;
    Button btnMcScan, goBack;
    String barcodeData, barCode;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;

    FragmentManager fragmentManager;

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;
    View dialog_view, breView, brView, viewSave, dialogStart;
    Dialog dialog, dialog1, dialogCustom;

    ImageButton backButton;
    SearchView search_view;
    RecyclerView machine_breakdownQueueRecyclerView, breakdown_exceptionQueueRecyclerView, breakdown_queue_exceptionRecyclerView;

    List<MachineBreakdownQueue> machineBreakdownQueueList;
    MachineBreakdownQueueAdapter machineBreakdownQueueAdapter;

    List<BreakdownQueueException> breakdownQueueExceptionList;
    BreakdownQueueExceptionAdapter breakdownQueueExceptionAdapter;

    Button btnQueueStart, btnQueueCancel, btnOk, btnFinish, btnSaveFinish;

    String uToken, sectionId, UserName, MbhId, MachineCode, repairBy, LoginName;

    TextView txtMessage, problemTextView,
            RequestedBy, RequestedDateTime, ReasonName, startDateTimeTxt,
            finishDateTimeTxt, repairByTxt, durationTxt;

    List<BreakdownException> breakdownExceptionList;
    private ArrayList<String> collcetListErName;
    private ArrayList<String> collcetListErId;
    List<MBRepairDetailDto> collcetList;
    BreakdownExceptionAdapter breakdownExceptionAdapter;
    CheckBox urgentCheck, item_check;
    Button btnCancel;
    public static String strValue = null;

    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout mechanic_Layout, duration_layout, startTime_Layout, finishTime_Layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_machine_break_down_queue_, container, false);

        mcBreakDownQueue_layout = root.findViewById(R.id.mcBreakDownQueue_layout);
        machineCodeEditTxt = root.findViewById(R.id.machineCodeEditTxt);

        btnMcScan = root.findViewById(R.id.btnMcScan);
        imgMcScanner = root.findViewById(R.id.imgMcScanner);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = root.findViewById(R.id.surfaceView);

        goBack = root.findViewById(R.id.goBack);
        goBack.setOnClickListener(v -> {
            mcBreakDownQueue_layout.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
            goBack.setVisibility(View.GONE);
        });

        dialog_view = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
        btnOk = dialog_view.findViewById(R.id.btnOk);
        txtMessage = dialog_view.findViewById(R.id.txtMessage);

        breView = getActivity().getLayoutInflater().inflate(R.layout.dialog_breakdownqueue_exception_, null);
        breakdown_exceptionQueueRecyclerView = breView.findViewById(R.id.breakdown_exceptionQueueRecyclerView);
        btnQueueCancel = breView.findViewById(R.id.btnQueueCancel);
        btnQueueStart = breView.findViewById(R.id.btnQueueStart);

//new Dialog
        dialogStart = getActivity().getLayoutInflater().inflate(R.layout.queue_layout_fordialog, null);
        RequestedBy = dialogStart.findViewById(R.id.RequestedBy);
        mechanic_Layout = dialogStart.findViewById(R.id.mechanic_Layout);
        duration_layout = dialogStart.findViewById(R.id.duration_layout);
        startTime_Layout = dialogStart.findViewById(R.id.startTime_Layout);
        finishTime_Layout = dialogStart.findViewById(R.id.finishTime_Layout);
        RequestedDateTime = dialogStart.findViewById(R.id.RequestedDateTime);
        ReasonName = dialogStart.findViewById(R.id.ReasonName);
        startDateTimeTxt = dialogStart.findViewById(R.id.startDateTimeTxt);
        finishDateTimeTxt = dialogStart.findViewById(R.id.finishDateTimeTxt);
        repairByTxt = dialogStart.findViewById(R.id.repairByTxt);
        durationTxt = dialogStart.findViewById(R.id.durationTxt);
        btnQueueStart = dialogStart.findViewById(R.id.btnQueueStart);
        btnFinish = dialogStart.findViewById(R.id.btnFinish);

        //finished dialog
        brView = getActivity().getLayoutInflater().inflate(R.layout.finished_queue_layout_fordialog, null);
        breakdown_queue_exceptionRecyclerView = brView.findViewById(R.id.breakdown_queue_exceptionRecyclerView);
        problemTextView = brView.findViewById(R.id.problemTextView);
        item_check = brView.findViewById(R.id.item_check);
        btnCancel = brView.findViewById(R.id.btnCancel);
        btnSaveFinish = brView.findViewById(R.id.btnSaveFinish);

        viewSave = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);

        fragmentManager = getFragmentManager();

        uToken = SharedPref.read("UserToken", "");
        sectionId = SharedPref.read("sectionId", "");
        UserName = SharedPref.read("UserName", "");
        LoginName = SharedPref.read("LoginName", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        dialog = new Dialog(getActivity());
        dialog1 = new Dialog(getActivity());
        dialogCustom = new Dialog(getActivity());

        search_view = root.findViewById(R.id.search_view);
        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.container, homeFragment, homeFragment.getTag()).commit();
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            machineBreakdownQueue();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_purple),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_blue_bright)
        );

        machine_breakdownQueueRecyclerView = root.findViewById(R.id.machine_breakdownQueueRecyclerView);
        machine_breakdownQueueRecyclerView.setNestedScrollingEnabled(false);

        machineBreakdownQueueList = new ArrayList<MachineBreakdownQueue>();
        breakdownQueueExceptionList = new ArrayList<BreakdownQueueException>();
        breakdownExceptionList = new ArrayList<BreakdownException>();

        collcetListErName = new ArrayList<String>();
        collcetListErId = new ArrayList<String>();
        collcetList = new ArrayList<MBRepairDetailDto>();
        machineBreakdownQueue();
        scanMachineCode();
        return root;
    }

    private void machineBreakdownQueue() {
        try {
            spotsDialog.show();
            Call<List<MachineBreakdownQueue>> MachineBreakdownQueueCall = retrofitApiInterface.machineBreakdownQueue("Bearer" + " " + uToken, sectionId);
            MachineBreakdownQueueCall.enqueue(new Callback<List<MachineBreakdownQueue>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<MachineBreakdownQueue>> call, Response<List<MachineBreakdownQueue>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                machineBreakdownQueueList = response.body();
                                machineBreakdownQueueAdapter = new MachineBreakdownQueueAdapter(getContext(), machineBreakdownQueueList);
                                machine_breakdownQueueRecyclerView.setHasFixedSize(true);
                                GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                                machine_breakdownQueueRecyclerView.setLayoutManager(mLayoutManager);
                                machine_breakdownQueueRecyclerView.setAdapter(machineBreakdownQueueAdapter);
                                machineBreakdownQueueAdapter.notifyDataSetChanged();
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setContentView(dialog_view);
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
                public void onFailure(Call<List<MachineBreakdownQueue>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
            search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryString) {
                    machineBreakdownQueueAdapter.getFilter().filter(queryString);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String queryString) {
                    machineBreakdownQueueAdapter.getFilter().filter(queryString);
                    return false;
                }
            });
            machineCodeEditTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final String query = s.toString().toLowerCase().trim();
                    final ArrayList<MachineBreakdownQueue> filteredList = new ArrayList<>();

                    for (int i = 0; i < machineBreakdownQueueList.size(); i++) {

                        final String text = machineBreakdownQueueList.get(i).getMachineCode().toLowerCase();
                        if (text.contains(query)) {
                            filteredList.add(machineBreakdownQueueList.get(i));
                        }
                    }

                    machine_breakdownQueueRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    machineBreakdownQueueAdapter = new MachineBreakdownQueueAdapter(getContext(), filteredList);
                    machine_breakdownQueueRecyclerView.setAdapter(machineBreakdownQueueAdapter);
                    machineBreakdownQueueAdapter.notifyDataSetChanged();  // data set changed
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
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
                            if (programNumber.getMachineInfo().getCategoryName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
                            if (programNumber.getMachineInfo().getShortCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
                            if (programNumber.getMachineInfo().getSubCategoryName().toLowerCase().contains(charSequenceString.toLowerCase())) {
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.machine_breakdownqueue_recycalerview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MachineBreakdownQueue mcBreakdownQueue = filteredProgramList.get(position);

            holder.itemLayoutQueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String mbhId = mcBreakdownQueue.getMbhId();
                        MbhId = mcBreakdownQueue.getMbhId();
                        MachineCode = mcBreakdownQueue.getMachineCode();
                        repairBy = mcBreakdownQueue.getRepairBy();
                        String statusCode = mcBreakdownQueue.getStatus();
                        Log.i("info", "statusCode: " + statusCode);
                        Log.i("info", "repairBy11: " + repairBy);
                        Log.i("info", "MbhId+MachineCode: " + MbhId + "," + MachineCode);
                        if (!mbhId.isEmpty()) {
                            dialog.setContentView(dialogStart);
                            dialog.setCancelable(true);
                            try {
                                String status = mcBreakdownQueue.getStatus();
                                boolean isUrgent = mcBreakdownQueue.getIsUrgent();
                                Log.i("info", "status: " + status);
                                if (status.equals("3") && isUrgent == true) {
                                    btnQueueStart.setVisibility(View.VISIBLE);
                                    btnFinish.setVisibility(View.INVISIBLE);
                                    mechanic_Layout.setVisibility(View.VISIBLE);
                                    finishTime_Layout.setVisibility(View.INVISIBLE);
                                    startTime_Layout.setVisibility(View.INVISIBLE);
                                    duration_layout.setVisibility(View.INVISIBLE);
                                } else if (status.equals("2")) {
                                    btnQueueStart.setVisibility(View.INVISIBLE);
                                    btnFinish.setVisibility(View.VISIBLE);
                                    mechanic_Layout.setVisibility(View.VISIBLE);
                                    startTime_Layout.setVisibility(View.VISIBLE);
                                    finishTime_Layout.setVisibility(View.INVISIBLE);
                                    duration_layout.setVisibility(View.INVISIBLE);
                                } else if (status.equals("1")) {
                                    btnQueueStart.setVisibility(View.INVISIBLE);
                                    btnFinish.setVisibility(View.INVISIBLE);
                                    mechanic_Layout.setVisibility(View.VISIBLE);
                                    finishTime_Layout.setVisibility(View.VISIBLE);
                                    startTime_Layout.setVisibility(View.VISIBLE);
                                    duration_layout.setVisibility(View.VISIBLE);
                                } else {
                                    btnFinish.setVisibility(View.INVISIBLE);
                                    btnQueueStart.setVisibility(View.VISIBLE);
                                    finishTime_Layout.setVisibility(View.INVISIBLE);
                                    startTime_Layout.setVisibility(View.INVISIBLE);
                                    duration_layout.setVisibility(View.INVISIBLE);
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            try {
                                RequestedBy.setText(mcBreakdownQueue.getRequestBy());
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            try {
                                repairByTxt.setText(LoginName);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            try {
                                durationTxt.setText(mcBreakdownQueue.getDuration());
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            try {
                                String reqDT = mcBreakdownQueue.getRequestDate();
                                SimpleDateFormat parser_reqDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                SimpleDateFormat formatterr_reqDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                                try {
                                    String result_reqDT = formatterr_reqDT.format(parser_reqDT.parse(reqDT));
                                    RequestedDateTime.setText(result_reqDT);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            try {
                                String startDT = mcBreakdownQueue.getStartDateTime();
                                SimpleDateFormat parser_startDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                                try {
                                    String result_startDT = formatterr_startDT.format(parser_startDT.parse(startDT));
                                    startDateTimeTxt.setText(result_startDT);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            try {
                                String finishDT = mcBreakdownQueue.getEndDateTime();
                                SimpleDateFormat parser_finishDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                SimpleDateFormat formatterr_finishDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                                try {
                                    String result_finishDT = formatterr_finishDT.format(parser_finishDT.parse(finishDT));
                                    finishDateTimeTxt.setText(result_finishDT);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            ReasonName.setText(mcBreakdownQueue.getMbDetail().getExceptionReason().getReasonName());
                            ReasonName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_radio_button_checked_24, 0, 0, 0);

                            startQueueException();
                            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
                            dialog.getWindow().setLayout(width, height);
                            dialog.show();
                        } else {
                            showAlertDialog("Error: ", "No data found.", getContext(), false);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                holder.machineCodeQueueTxt.setText(mcBreakdownQueue.getMachineCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.ShortCodeQueue.setText(mcBreakdownQueue.getMachineInfo().getShortCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryNameQueueTxt.setText("Category:");
                holder.CategoryNameQueue.setText(mcBreakdownQueue.getMachineInfo().getCategoryName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String endDT = mcBreakdownQueue.getEndDateTime();
                SimpleDateFormat parser_endDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_endDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_endDT = formatterr_endDT.format(parser_endDT.parse(endDT));
                    holder.EndTimeQueueTxt.setText("End Time:");
                    holder.EndTimeQueue.setText(result_endDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.DurationQueueTxt.setText("Duration:");
                holder.DurationQueue.setText(mcBreakdownQueue.getDuration());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.lineName.setText(mcBreakdownQueue.getLines().getLineName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.requestByTxt.setText("Req. By: " + mcBreakdownQueue.getRequestBy());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                repairBy = mcBreakdownQueue.getRepairBy();
                Log.i("info", "Repair By : " + mcBreakdownQueue.getRepairBy());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String status = mcBreakdownQueue.getStatus();
                boolean isUrgent = mcBreakdownQueue.getIsUrgent();
                Log.i("info", "status: " + status);
                if (status.equals("3") && isUrgent == true) {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.redColor)));
                    holder.ImageUrgent.setVisibility(View.VISIBLE);
                    holder.ImageInProgress.setVisibility(View.INVISIBLE);
                } else if (status.equals("2")) {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.yellowColor)));
                    holder.ImageUrgent.setVisibility(View.INVISIBLE);
                    holder.ImageInProgress.setVisibility(View.INVISIBLE);
                } else if (status.equals("1")) {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.greenColor)));
                    holder.ImageInProgress.setVisibility(View.VISIBLE);
                    holder.ImageUrgent.setVisibility(View.INVISIBLE);
                } else {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.greyColor)));
                    holder.ImageUrgent.setVisibility(View.INVISIBLE);
                    holder.ImageInProgress.setVisibility(View.INVISIBLE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            holder.imgDetailsQueue.setOnClickListener(view -> {
                String statusCode = mcBreakdownQueue.getStatus();
                try {
                    if (statusCode.equals("3")) {
                        breakdownQueueException();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            });

            holder.btnEndQueue.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
            try {
                if (mcBreakdownQueue.getStatus().equals("2")) {
                    btnFinish.setVisibility(View.VISIBLE);
                    btnFinish.setOnClickListener(view -> {
                        if (repairBy.equals(LoginName)) {
                            repairTypeException();
                        } else {
                            dialog1.dismiss();
                            dialog.dismiss();
                            showAlertDialog("Error: ", "You are not authorised to finish the queue.", getContext(), false);
                        }

                    });
                } else {
                    btnFinish.setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return filteredProgramList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView itemLayoutQueue;
            TextView machineCodeQueueTxt, ShortCodeQueueTxt, ShortCodeQueue,
                    CategoryNameQueueTxt, CategoryNameQueue, RequestTimeQueueTxt, RequestTimeQueue,
                    StartTimeQueueTxt, StartTimeQueue, EndTimeQueueTxt, EndTimeQueue, DurationQueueTxt, DurationQueue,
                    requestByTxt,
                    lineNameTxt, lineName;
            Button btnEndQueue;

            ImageView imgDetailsQueue, ImageUrgent, ImageInProgress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemLayoutQueue = itemView.findViewById(R.id.itemLayoutQueue);
                machineCodeQueueTxt = itemView.findViewById(R.id.machineCodeQueueTxt);

                RequestTimeQueueTxt = itemView.findViewById(R.id.RequestTimeQueueTxt);
                RequestTimeQueue = itemView.findViewById(R.id.RequestTimeQueue);

                ShortCodeQueueTxt = itemView.findViewById(R.id.ShortCodeQueueTxt);
                ShortCodeQueue = itemView.findViewById(R.id.ShortCodeQueue);
                CategoryNameQueueTxt = itemView.findViewById(R.id.CategoryNameQueueTxt);
                CategoryNameQueue = itemView.findViewById(R.id.CategoryNameQueue);

                StartTimeQueueTxt = itemView.findViewById(R.id.StartTimeQueueTxt);
                StartTimeQueue = itemView.findViewById(R.id.StartTimeQueue);
                EndTimeQueueTxt = itemView.findViewById(R.id.EndTimeQueueTxt);
                EndTimeQueue = itemView.findViewById(R.id.EndTimeQueue);
                DurationQueueTxt = itemView.findViewById(R.id.DurationQueueTxt);
                DurationQueue = itemView.findViewById(R.id.DurationQueue);
                requestByTxt = itemView.findViewById(R.id.requestByTxt);

                lineNameTxt = itemView.findViewById(R.id.lineNameTxt);
                lineName = itemView.findViewById(R.id.lineName);

                btnEndQueue = itemView.findViewById(R.id.btnEndQueue);
                imgDetailsQueue = itemView.findViewById(R.id.imgDetailsQueue);
                ImageUrgent = itemView.findViewById(R.id.ImageUrgent);
                ImageInProgress = itemView.findViewById(R.id.ImageInProgress);
            }
        }
    }

    //old
    private void breakdownQueueException() {
        try {
            spotsDialog.show();
            Call<List<BreakdownQueueException>> BreakdownQueueExceptionCall = retrofitApiInterface.breakdownQueueException("Bearer" + " " + uToken, MachineCode);
            BreakdownQueueExceptionCall.enqueue(new Callback<List<BreakdownQueueException>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<BreakdownQueueException>> call, Response<List<BreakdownQueueException>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                breakdownQueueExceptionList = response.body();
                                if (!breakdownQueueExceptionList.isEmpty()) {
                                    dialog.setContentView(breView);
                                    dialog.setCancelable(true);
                                    breakdownQueueExceptionAdapter = new BreakdownQueueExceptionAdapter(getContext(), breakdownQueueExceptionList);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    breakdown_exceptionQueueRecyclerView.setHasFixedSize(true);
                                    breakdown_exceptionQueueRecyclerView.setLayoutManager(mLayoutManager);
                                    breakdown_exceptionQueueRecyclerView.setAdapter(breakdownQueueExceptionAdapter);
                                    breakdownQueueExceptionAdapter.notifyDataSetChanged();
                                    startQueueException();
                                    dialog.show();
                                } else {
                                    showAlertDialog("Error: ", "No data found.", getContext(), false);
                                }
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setContentView(dialog_view);
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.breakdown_exception_queue_recyclerview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    private void startQueueException() {
        btnQueueStart.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Started", Toast.LENGTH_SHORT).show();
            UpdateQueue updateQueue = new UpdateQueue();
            updateQueue.setMBHId(MbhId);
            updateQueue.setMachineCode(MachineCode);
            updateQueue.setRequestBy(UserName);
            Log.i("info", "updateQueue: " + updateQueue);

            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100098", "Update");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                try {
                                    spotsDialog.show();
                                    Call<String> updateQueueCall = retrofitApiInterface.updateQueue("Bearer" + " " + uToken, updateQueue);
                                    updateQueueCall.enqueue(new Callback<String>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            try {
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        spotsDialog.dismiss();
                                                        dialog.dismiss();
                                                        if (breakdownQueueExceptionAdapter != null) {
                                                            breakdownQueueExceptionList.clear();
                                                            breakdownQueueExceptionAdapter.notifyDataSetChanged();
                                                        }
                                                        machineBreakdownQueue();
                                                    }
                                                } else if (!response.isSuccessful()) {
                                                    if (response.errorBody() != null) {
                                                        spotsDialog.dismiss();
                                                        dialog.dismiss();
                                                        Gson gson = new GsonBuilder().create();
                                                        try {
                                                            String mError = gson.toJson(response.errorBody().string());
                                                            mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                            mError = mError.replace("\\r\\nত্রুটি :", "");
                                                            dialogCustom.setContentView(dialog_view);
                                                            dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                            txtMessage.setText(mError);
                                                            dialogCustom.show();
                                                            btnOk.setOnClickListener(v3 -> {
                                                                dialogCustom.dismiss();
                                                                machineBreakdownQueue();
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
                                        public void onFailure(Call<String> call, Throwable t) {
                                            spotsDialog.dismiss();
                                            dialogCustom.dismiss();
                                            showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                                        }
                                    });
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setContentView(dialog_view);
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    txtMessage.setText(mError);
                                    dialogCustom.show();
                                    btnOk.setOnClickListener(view -> {
                                        dialogCustom.dismiss();
                                        machineBreakdownQueue();
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
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    dialogCustom.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                }
            });

        });
    }

    private void repairTypeException() {
        try {
            spotsDialog.show();
            Call<List<BreakdownException>> BreakdownExceptionCall = retrofitApiInterface.breakdownException("Bearer" + " " + uToken, "REPAIRTYPE");
            BreakdownExceptionCall.enqueue(new Callback<List<BreakdownException>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<BreakdownException>> call, Response<List<BreakdownException>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                breakdownExceptionList = response.body();
                                if (!breakdownExceptionList.isEmpty()) {
                                    dialog1.setContentView(brView);
                                    dialog1.setCancelable(true);
                                    collcetListErName.clear();
                                    collcetListErId.clear();
                                    breakdownExceptionAdapter = new BreakdownExceptionAdapter(getContext(), breakdownExceptionList, breakdownException -> {
                                        if (collcetListErId.size() <= 0) {
                                            collcetListErId.add(breakdownException.getErId().toString());
                                            collcetListErName.add(breakdownException.getReasonName());
                                        } else {
                                            if (collcetListErId.contains(breakdownException.getErId().toString()) == true) {
                                                collcetListErId.remove(breakdownException.getErId().toString());
                                                collcetListErName.remove(breakdownException.getReasonName());
                                            } else {
                                                collcetListErId.add(breakdownException.getErId().toString());
                                                collcetListErName.add(breakdownException.getReasonName());
                                            }
                                        }
                                    });
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    breakdown_queue_exceptionRecyclerView.setHasFixedSize(true);
                                    breakdown_queue_exceptionRecyclerView.setLayoutManager(mLayoutManager);
                                    breakdown_queue_exceptionRecyclerView.setAdapter(breakdownExceptionAdapter);
                                    breakdownExceptionAdapter.notifyDataSetChanged();
                                    saveException();
                                    dialog1.show();
                                } else {
                                    showAlertDialog("Error: ", "No data found.", getContext(), false);
                                }
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
                                Gson gson = new GsonBuilder().create();
                                try {
                                    String mError = gson.toJson(response.errorBody().string());
                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                    dialogCustom.setContentView(dialog_view);
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
                public void onFailure(Call<List<BreakdownException>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class BreakdownExceptionAdapter extends RecyclerView.Adapter<BreakdownExceptionAdapter.ViewHolder> implements Filterable {

        Context context;
        List<BreakdownException> breakdownExceptionList;
        List<BreakdownException> filteredProgramList;
        private OnItemClickListener listener;

        public BreakdownExceptionAdapter(Context context, List<BreakdownException> breakdownExceptionList, OnItemClickListener listener) {
            this.context = context;
            this.breakdownExceptionList = breakdownExceptionList;
            this.filteredProgramList = breakdownExceptionList;
            this.listener = listener;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredProgramList = breakdownExceptionList;
                    } else {
                        List<BreakdownException> filteredList = new ArrayList<>();
                        for (BreakdownException programNumber : breakdownExceptionList) {
                            if (programNumber.getReasonName().toLowerCase().contains(charSequenceString.toLowerCase())) {
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
                    filteredProgramList = (List<BreakdownException>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public BreakdownExceptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.breakdown_exceptionqueuee_recyclerview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BreakdownExceptionAdapter.ViewHolder holder, int position) {
            final BreakdownException breakdownException = filteredProgramList.get(position);
            holder.reasonTxt.setText(breakdownException.getReasonName());
            holder.bind(breakdownExceptionList.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return filteredProgramList.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView exceptionqueueLayout;
            CheckBox item_check;
            TextView reasonTxt;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exceptionqueueLayout = itemView.findViewById(R.id.exceptionqueueLayout);
                item_check = itemView.findViewById(R.id.item_check);
                reasonTxt = itemView.findViewById(R.id.reasonTxt);
            }

            public void bind(BreakdownException breakdownException, OnItemClickListener listener) {
                reasonTxt.setText(breakdownException.getReasonName());
                item_check.setOnClickListener(v -> listener.onItemClick(breakdownException));
            }

        }
    }

    private void saveException() {
        btnSaveFinish.setOnClickListener(view2 -> {
            if (collcetListErId.size() < 0) {
                showAlertDialog("Warning: ", "Select exception.", getContext(), false);
            } else {
                spotsDialog.show();
                Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100098", "Execute");
                getObjectCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    spotsDialog.dismiss();
                                    if (!collcetListErId.isEmpty()) {
                                        MBHeaderDto mbHeaderDto = new MBHeaderDto();
                                        mbHeaderDto.setMBHId(MbhId);
                                        mbHeaderDto.setMachineCode(MachineCode);
                                        mbHeaderDto.setRequestBy(UserName);
                                        mbHeaderDto.setIsUrgent(Boolean.valueOf(strValue));
                                        for (int i = 0; i < collcetListErId.size(); i++) {
                                            MBRepairDetailDto mbRepairDetailDto = new MBRepairDetailDto();
                                            mbRepairDetailDto.seteRId(collcetListErId.get(i));
                                            collcetList.add(mbRepairDetailDto);
                                        }
                                        PostRepairQueueDto postRepairQueueDto = new PostRepairQueueDto();
                                        postRepairQueueDto.setMBHeaderDto(mbHeaderDto);
                                        postRepairQueueDto.setMBRepairDetailDto(collcetList);
                                        Log.i("info", "postRepairQueueDto: " + postRepairQueueDto);
                                        spotsDialog.dismiss();
                                        Call<String> exceptionCall = retrofitApiInterface.updateRepairQueueMB("Bearer" + " " + uToken, postRepairQueueDto);
                                        exceptionCall.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call1, Response<String> response1) {
                                                try {
                                                    if (response1.isSuccessful()) {
                                                        if (response1.body() != null) {
                                                            spotsDialog.dismiss();
                                                            collcetListErId.clear();
                                                            collcetList.clear();
                                                            if (breakdownExceptionAdapter != null) {
                                                                breakdownExceptionList.clear();
                                                                breakdownExceptionAdapter.notifyDataSetChanged();
                                                            }
                                                            dialog.dismiss();
                                                            dialog1.dismiss();
                                                            machineBreakdownQueue();
                                                        }
                                                    } else if (!response1.isSuccessful()) {
                                                        if (response1.errorBody() != null) {
                                                            spotsDialog.dismiss();
                                                            dialog.dismiss();
                                                            dialog1.dismiss();
                                                            Gson gson = new GsonBuilder().create();
                                                            try {
                                                                String mError = gson.toJson(response1.errorBody().string());
                                                                mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                                mError = mError.replace("\\r\\nত্রুটি :", "");
                                                                dialogCustom.setContentView(dialog_view);
                                                                dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                                txtMessage.setText(mError);
                                                                dialogCustom.show();
                                                                btnOk.setOnClickListener(v1 -> {
                                                                    dialogCustom.dismiss();
                                                                    machineBreakdownQueue();
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
                                            public void onFailure(Call<String> call1, Throwable t) {
                                                spotsDialog.dismiss();
                                                showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                                            }
                                        });
                                        MbhId = null;
                                        MachineCode = null;
                                        MbhId.equals(null);
                                        MachineCode.equals(null);
                                    } else {
                                        urgentCheck.setChecked(false);
                                        showAlertDialog("Warning: ", "Select exception.", getContext(), false);
                                    }
                                }
                            } else if (!response.isSuccessful()) {
                                if (response.errorBody() != null) {
                                    spotsDialog.dismiss();
                                    Gson gson = new GsonBuilder().create();
                                    try {
                                        String mError = gson.toJson(response.errorBody().string());
                                        mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                        mError = mError.replace("\\r\\nত্রুটি :", "");
                                        dialogCustom.setContentView(dialog_view);
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
                    public void onFailure(Call<String> call, Throwable t) {
                        spotsDialog.dismiss();
                        showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                        Log.i("info", "Failed: " + t);
                    }
                });
            }


        });
    }

    private void scanMachineCode() {
        mcBreakDownQueue_layout.setVisibility(View.VISIBLE);
        machineCodeEditTxt.requestFocus();
        imgMcScanner.setOnClickListener(v -> {
            mcCodeDetectorsAndSources();
            mcBreakDownQueue_layout.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
            goBack.setVisibility(View.VISIBLE);
        });
        btnMcScan.setOnClickListener(view -> {
            barCode = machineCodeEditTxt.getText().toString();
            if (barCode.isEmpty()) {
                machineCodeEditTxt.setError("Please scan machine code.");
            } else {

            }
        });
    }

    private void mcCodeDetectorsAndSources() {
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    machineCodeEditTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                machineCodeEditTxt.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                machineCodeEditTxt.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                mcBreakDownQueue_layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                goBack.setVisibility(View.GONE);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                machineCodeEditTxt.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                mcBreakDownQueue_layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                goBack.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        mcCodeDetectorsAndSources();
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