package com.example.mms_scanner.ui;

import static android.view.View.INVISIBLE;

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

import android.renderscript.Sampler;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.breakdown.breakdown_exception.BreakdownException;
import com.example.mms_scanner.model.breakdown.breakdown_exception.MBDetailDto;
import com.example.mms_scanner.model.breakdown.breakdown_exception.MBHeaderDto;
import com.example.mms_scanner.model.breakdown.breakdown_exception.PostExceptionDto;
import com.example.mms_scanner.model.breakdown.machine_breakdown.MachineBreakdown;
import com.example.mms_scanner.model.breakdownqueue.UpdateQueue;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MBRepairDetail;
import com.example.mms_scanner.model.line.GetLine;
import com.example.mms_scanner.proces1.FcmNotificationsSender;
import com.example.mms_scanner.proces1.device_token.DeviceRegistrationToken;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachineBreakDown_Fragment extends Fragment {

    LinearLayout mcMaintenance_layout;
    TextInputEditText machineCodeEditTxt;
    ImageView imgMcScanner;
    Button btnMcScan, goBack;
    String barcodeData;

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
    String barCode;

    View lnView, brView, viewSave, viewError, acceptView;

    TextView selectLineText, txtMessage, problemTextView, isUrgentTxt,
            RequestedBy, RequestedDateTime, Duration, RepairCompletedDateTime, RepairedByTxt,txtCommentStatus;
    Button btnShow;
    ImageButton backButton;

    List<GetLine> getLines;
    Dialog dialog, dialog1, dialogCustom;
    RecyclerView lineRecyclerView, machine_breakdownRecyclerView, reqBreakdown_exceptionRecyclerView, accept_exceptionRecyclerView;
    SearchView searchView, search_view;
    LinesDialogAdapter linesDialogAdapter;
    BreakdownExceptionAdapter breakdownExceptionAdapter;
    AcceptBreakdownExceptionAdapter acceptBreakdownExceptionAdapter;

    List<MachineBreakdown> machineBreakdownList;
    MachineBreakdownAdapter machineBreakdownAdapter;
    String uToken, deptId, lineName, lineId, sectionId;

    List<BreakdownException> breakdownExceptionList;
    List<MBRepairDetail> mbRepairDetails;
    private ArrayList<String> collcetListErName;
    private ArrayList<String> collcetListErId;
    List<MBDetailDto> collcetList;

    List<DeviceRegistrationToken> deviceRegistrationTokenList;

    CheckBox urgentCheck;
    Button btnSave, btnCancel, btnOk, btnAcceptConfirm;

    String erid, MbhId, MachineCode, UserName, duration, completedDate, repairDate, repairedBy, requestBy,LoginName,problemName;
    public static String strValue = null;

    List<String> listOfException;
    String urgentMessage = null;
    int selectedItemPosition = -1;

    RatingBar simpleRatingBar;
    Float ratingNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_machine_break_down_, container, false);

        mcMaintenance_layout = root.findViewById(R.id.mcMaintenance_layout);
        machineCodeEditTxt = root.findViewById(R.id.machineCodeEditTxt);
//        machineCodeEditTxt.setInputType(InputType.TYPE_NULL);
        btnMcScan = root.findViewById(R.id.btnMcScan);
        imgMcScanner = root.findViewById(R.id.imgMcScanner);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = root.findViewById(R.id.surfaceView);

        goBack = root.findViewById(R.id.goBack);
        goBack.setOnClickListener(v -> {
            mcMaintenance_layout.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
            goBack.setVisibility(View.GONE);
        });

        lnView = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);
        brView = getActivity().getLayoutInflater().inflate(R.layout.request_layout_fordialog, null);
        problemTextView = brView.findViewById(R.id.problemTextView);
        RequestedBy = brView.findViewById(R.id.RequestedBy);
        RequestedDateTime = brView.findViewById(R.id.RequestedDateTime);
        reqBreakdown_exceptionRecyclerView = brView.findViewById(R.id.reqBreakdown_exceptionRecyclerView);


        acceptView = getActivity().getLayoutInflater().inflate(R.layout.accept_layout_fordialog, null);
        accept_exceptionRecyclerView = acceptView.findViewById(R.id.accept_exceptionRecyclerView);
        Duration = acceptView.findViewById(R.id.Duration);
        RepairCompletedDateTime = acceptView.findViewById(R.id.RepairCompletedDateTime);
        RepairedByTxt = acceptView.findViewById(R.id.RepairedByTxt);
        btnAcceptConfirm = acceptView.findViewById(R.id.btnAcceptConfirm);
        simpleRatingBar = acceptView.findViewById(R.id.simpleRatingBar);
        txtCommentStatus = acceptView.findViewById(R.id.txtCommentStatus);

        simpleRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        txtCommentStatus.setText("Very Poor");
                        break;
                    case 2:
                        txtCommentStatus.setText("Bad");
                        break;
                    case 3:
                        txtCommentStatus.setText("Good");
                        break;
                    case 4:
                        txtCommentStatus.setText("Very Good");
                        break;
                    case 5:
                        txtCommentStatus.setText("Excellent");
                        break;
                }
            }
        });

        viewSave = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);
        viewError = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);

        fragmentManager = getFragmentManager();

        //deptId = SharedPref.read("deptId", "");
        deptId = String.valueOf("307201100020");
        uToken = SharedPref.read("UserToken", "");
        sectionId = SharedPref.read("sectionId", "");
        UserName = SharedPref.read("UserName", "");
        LoginName = SharedPref.read("LoginName", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        search_view = root.findViewById(R.id.search_view);
        btnShow = root.findViewById(R.id.btnShow);
        selectLineText = root.findViewById(R.id.selectLineText);

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.container, homeFragment, homeFragment.getTag()).commit();
        });

        getLines = new ArrayList<GetLine>();
        dialog = new Dialog(getActivity());
        dialog1 = new Dialog(getActivity());
        dialogCustom = new Dialog(getActivity());

        machineBreakdownList = new ArrayList<MachineBreakdown>();
        machine_breakdownRecyclerView = root.findViewById(R.id.machine_breakdownRecyclerView);

        breakdownExceptionList = new ArrayList<BreakdownException>();
        mbRepairDetails = new ArrayList<MBRepairDetail>();
        collcetListErName = new ArrayList<String>();
        collcetListErId = new ArrayList<String>();
        collcetList = new ArrayList<MBDetailDto>();
        listOfException = new ArrayList<>();

        deviceRegistrationTokenList = new ArrayList<DeviceRegistrationToken>();

        line();
        machineBreakdown();
        scanMachineCode();
        return root;
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
                selectLineText.setText(lineName);
                getDeviceToken();
                dialog.dismiss();
            });
            try {
                if (position % 2 == 1) {
                    holder.cardSection.setCardBackgroundColor(getResources().getColor(R.color.black));
                } else
                    holder.cardSection.setCardBackgroundColor(getResources().getColor(R.color.lihtgreenLine1));
//                Random r = new Random();
//                holder.cardSectionection.setCardBackgroundColor(Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256)));

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return filteredLineList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView cardSection;
            public CircleImageView circleImg;
            public TextView txtLineCode, txtLinesName;
            public LinearLayout line_dialog_layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                line_dialog_layout = itemView.findViewById(R.id.line_list_dialog_layout);
                cardSection = itemView.findViewById(R.id.cardSection);
                circleImg = itemView.findViewById(R.id.circleImg);
                txtLineCode = itemView.findViewById(R.id.txtLinesCode);
                txtLinesName = itemView.findViewById(R.id.txtLinesName);
            }
        }

    }

    private void machineBreakdown() {
        btnShow.setOnClickListener(v -> {
            String line_Name = selectLineText.getText().toString();
            if (line_Name != null) {
                try {
                    spotsDialog.show();
                    Call<List<MachineBreakdown>> MachineBreakdownCall = retrofitApiInterface.machineBreakdown("Bearer" + " " + uToken, line_Name);
                    MachineBreakdownCall.enqueue(new Callback<List<MachineBreakdown>>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<List<MachineBreakdown>> call, Response<List<MachineBreakdown>> response) {
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        spotsDialog.dismiss();
                                        machineBreakdownList = response.body();
                                        machineBreakdownAdapter = new MachineBreakdownAdapter(getContext(), machineBreakdownList);
                                        machine_breakdownRecyclerView.setHasFixedSize(true);
                                        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                                        machine_breakdownRecyclerView.setLayoutManager(mLayoutManager);
                                        machine_breakdownRecyclerView.setAdapter(machineBreakdownAdapter);
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
                        public void onFailure(Call<List<MachineBreakdown>> call, Throwable t) {
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
                    machineCodeEditTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            final String query = s.toString().toLowerCase().trim();
                            final ArrayList<MachineBreakdown> filteredList = new ArrayList<>();

                            for (int i = 0; i < machineBreakdownList.size(); i++) {

                                final String text = machineBreakdownList.get(i).getMachineCode().toLowerCase();
                                if (text.contains(query)) {
                                    filteredList.add(machineBreakdownList.get(i));
                                }
                            }

                            machine_breakdownRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            machineBreakdownAdapter = new MachineBreakdownAdapter(getContext(), filteredList);
                            machine_breakdownRecyclerView.setAdapter(machineBreakdownAdapter);
                            machineBreakdownAdapter.notifyDataSetChanged();  // data set changed
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                spotsDialog.dismiss();
                showAlertDialog("Error: ", "Select Line.", getContext(), false);
            }
        });
    }

    public class MachineBreakdownAdapter extends RecyclerView.Adapter<MachineBreakdownAdapter.ViewHolder> implements Filterable {

        Context context;
        List<MachineBreakdown> machineBreakdownList;
        List<MachineBreakdown> filteredProgramList;

        public MachineBreakdownAdapter(Context context, List<MachineBreakdown> machineBreakdownList) {
            this.context = context;
            this.machineBreakdownList = machineBreakdownList;
            this.filteredProgramList = machineBreakdownList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredProgramList = machineBreakdownList;
                    } else {
                        List<MachineBreakdown> filteredList = new ArrayList<>();
                        for (MachineBreakdown programNumber : machineBreakdownList) {
                            if (programNumber.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
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
                    filteredProgramList = (List<MachineBreakdown>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public MachineBreakdownAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.machine_breakdown_recycalerview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MachineBreakdownAdapter.ViewHolder holder, int position) {
            final MachineBreakdown machineBreakdown = filteredProgramList.get(position);

            try {
                holder.itemLayout.setOnClickListener(view -> {
                    try {

                        int statusCode = Integer.parseInt(machineBreakdown.getMbHeader().getStatus());
                        if (machineBreakdown.getMbHeader() != null) {
                            Log.i("info", "statusCode: " + statusCode);
                            if (statusCode == 1) {
                                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#65C18C"));
                                MachineCode = machineBreakdown.getMachineCode();
                                MbhId = machineBreakdown.getMbHeader().getMbhId();
                                acceptBreakdownException();

                            } else if (statusCode == 2) {
                                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFE162"));
                                Toast.makeText(getContext(), "Al Ready In Progress", Toast.LENGTH_LONG).show();
                            } else if (statusCode == 3) {
                                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FF6464"));
                                Toast.makeText(getContext(), "Al Ready In Progress", Toast.LENGTH_LONG).show();
                            } else if (String.valueOf(statusCode) == String.valueOf(null)) {
                                MachineCode = machineBreakdown.getMachineCode();
                                MbhId = machineBreakdown.getMbHeader().getMbhId();
                                breakdownException();
                            } else {

                            }
                        }

                    } catch (NullPointerException e) {
                        MachineCode = machineBreakdown.getMachineCode();
                        breakdownException();
                        e.printStackTrace();
                    }

                });

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.machineCodeTxt.setText(machineBreakdown.getMachineCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.ShortCode.setText(machineBreakdown.getShortCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryName.setText(machineBreakdown.getCategoryName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                requestBy = machineBreakdown.getMbHeader().getRequestBy();
                Log.i("info", "RequesBY: " + machineBreakdown.getMbHeader().getRequestBy());

            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            try {
                String startDT = machineBreakdown.getMbHeader().getStartDateTime();
                SimpleDateFormat parser_startDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
//                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("hh:mm a");
//                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("hh:mm:ss");
                try {
                    String result_startDT = formatterr_startDT.format(parser_startDT.parse(startDT));
                    holder.StartTimeTxt.setText("Start:");
                    holder.StartTime.setText(result_startDT);
                    holder.StartTime.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String endDT = machineBreakdown.getMbHeader().getEndDateTime();
                SimpleDateFormat parser_endDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_endDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
//                SimpleDateFormat formatterr_endDT = new SimpleDateFormat("hh:mm a");
                try {
                    String result_endDT = formatterr_endDT.format(parser_endDT.parse(endDT));
                    holder.EndTimeTxt.setText("End:");
                    holder.EndTime.setText(result_endDT);
                    repairDate = result_endDT;
                    holder.EndTime.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                int status = Integer.parseInt(machineBreakdown.getMbHeader().getStatus());
                if (status == 3) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FF6464"));
                    holder.ImageInProgress.setVisibility(INVISIBLE);
                    holder.ImageUrgent.setVisibility(View.VISIBLE);
                } else if (status == 2) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFE162"));
                    holder.ImageInProgress.setVisibility(INVISIBLE);
                    holder.ImageUrgent.setVisibility(View.VISIBLE);
                } else if (status == 1) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#65C18C"));
                    holder.ImageUrgent.setVisibility(INVISIBLE);
                    holder.ImageInProgress.setVisibility(View.VISIBLE);
                    holder.CategoryName.setText("Waiting for Acceptance");
                } else {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
                    holder.ImageInProgress.setVisibility(INVISIBLE);
                    holder.ImageUrgent.setVisibility(INVISIBLE);
                }
            } catch (NullPointerException e) {
                holder.ImageInProgress.setVisibility(INVISIBLE);
                holder.ImageUrgent.setVisibility(INVISIBLE);
                e.printStackTrace();
            }

            try {
                holder.DurationTxt.setText("Duration:");
                holder.Duration.setText(machineBreakdown.getMbHeader().getDuration());
                duration = (machineBreakdown.getMbHeader().getDuration());
                completedDate = (machineBreakdown.getMbHeader().getConfirmDate());
                repairedBy = (machineBreakdown.getMbHeader().getRepairBy());
                holder.Duration.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            holder.confirmChBx.setEnabled(false);
            holder.confirmChBx.setChecked(false);
            holder.btnAccept.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return filteredProgramList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView btnAccept, itemLayout;
            TextView machineCodeTxt, ShortCodeTxt, ShortCode, CategoryNameTxt, CategoryName, StartTimeTxt, StartTime, EndTimeTxt, EndTime, DurationTxt, Duration;
            CheckBox confirmChBx;
            ImageView imgDetails, ImageInProgress, ImageUrgent;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemLayout = itemView.findViewById(R.id.itemLayout);

                machineCodeTxt = itemView.findViewById(R.id.machineCodeTxt);

                ShortCodeTxt = itemView.findViewById(R.id.ShortCodeTxt);
                ShortCode = itemView.findViewById(R.id.ShortCode);
                CategoryNameTxt = itemView.findViewById(R.id.CategoryNameTxt);
                CategoryName = itemView.findViewById(R.id.CategoryName);

                StartTimeTxt = itemView.findViewById(R.id.StartTimeTxt);
                StartTime = itemView.findViewById(R.id.StartTime);
                StartTime.setVisibility(INVISIBLE);
                EndTimeTxt = itemView.findViewById(R.id.EndTimeTxt);
                EndTime = itemView.findViewById(R.id.EndTime);
                EndTime.setVisibility(INVISIBLE);
                DurationTxt = itemView.findViewById(R.id.DurationTxt);
                Duration = itemView.findViewById(R.id.Duration);
                Duration.setVisibility(INVISIBLE);

                confirmChBx = itemView.findViewById(R.id.confirmChBx);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                imgDetails = itemView.findViewById(R.id.imgDetails);
                ImageUrgent = itemView.findViewById(R.id.ImageUrgent);
                ImageInProgress = itemView.findViewById(R.id.ImageInProgress);
            }
        }
    }

    private void breakdownException() {
        try {
            spotsDialog.show();
            Call<List<BreakdownException>> BreakdownExceptionCall = retrofitApiInterface.breakdownException("Bearer" + " " + uToken, "PROBLEMTYPE");
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
//                                    collcetListErName.clear();
                                    breakdownExceptionAdapter = new BreakdownExceptionAdapter(getContext(), breakdownExceptionList);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    reqBreakdown_exceptionRecyclerView.setHasFixedSize(true);
                                    reqBreakdown_exceptionRecyclerView.setLayoutManager(mLayoutManager);
                                    reqBreakdown_exceptionRecyclerView.setAdapter(breakdownExceptionAdapter);
                                    breakdownExceptionAdapter.notifyDataSetChanged();
                                    saveException();
                                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
                                    dialog1.getWindow().setLayout(width, height);
                                    RequestedBy.setText(UserName);
                                    Date currentTime = Calendar.getInstance().getTime();
                                    RequestedDateTime.setText(String.valueOf(currentTime));
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

        public BreakdownExceptionAdapter(Context context, List<BreakdownException> breakdownExceptionList) {
            this.context = context;
            this.breakdownExceptionList = breakdownExceptionList;
            this.filteredProgramList = breakdownExceptionList;
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
            View view = inflater.inflate(R.layout.request_layout_fordialog_recycaler_view, parent, false);
            return new BreakdownExceptionAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BreakdownExceptionAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final BreakdownException breakdownException = filteredProgramList.get(position);
            holder.exceptionLayout.setOnClickListener(view -> {
                selectedItemPosition = position;
                notifyDataSetChanged();
            });
            if (selectedItemPosition == position) {
                collcetListErId.clear();
                Log.i("info", "collcetListErId: " + collcetListErId);
                erid = breakdownException.getErId().toString();
                collcetListErId.add(erid);
                Log.i("info", "collcetListErId: " + collcetListErId);
                holder.exceptionLayout.setCardBackgroundColor(Color.parseColor("#37808D"));
            } else {
                holder.exceptionLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
            holder.problemTextView.setText(breakdownException.getReasonName());
            problemName=breakdownException.getReasonName();
        }

        @Override
        public int getItemCount() {
            return filteredProgramList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView exceptionLayout;
            TextView problemTextView, txtRadioInfo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exceptionLayout = itemView.findViewById(R.id.exceptionLayout);
                problemTextView = itemView.findViewById(R.id.problemTextView);
                txtRadioInfo = itemView.findViewById(R.id.txtRadioInfo);
            }

        }
    }

    private void acceptBreakdownException() {
        try {
            spotsDialog.show();
            Call<List<MBRepairDetail>> objCall = retrofitApiInterface.getRepairBreakdownQueueException("Bearer" + " " + uToken, MbhId);
            Toast.makeText(getContext(), "Al Ready In Click", Toast.LENGTH_LONG).show();
            objCall.enqueue(new Callback<List<MBRepairDetail>>() {
                @Override
                public void onResponse(Call<List<MBRepairDetail>> call, Response<List<MBRepairDetail>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                mbRepairDetails = response.body();
                                Log.i("info", "acceptBreakdownExceptionAdapter: " + response.body());
                                if (!mbRepairDetails.isEmpty()) {
                                    dialog1.setContentView(acceptView);
                                    dialog1.setCancelable(true);
                                    acceptBreakdownExceptionAdapter = new AcceptBreakdownExceptionAdapter(getContext(), mbRepairDetails);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    accept_exceptionRecyclerView.setHasFixedSize(true);
                                    accept_exceptionRecyclerView.setLayoutManager(mLayoutManager);
                                    accept_exceptionRecyclerView.setAdapter(acceptBreakdownExceptionAdapter);
                                    acceptBreakdownExceptionAdapter.notifyDataSetChanged();
                                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
                                    dialog1.getWindow().setLayout(width, height);
                                    Duration.setText(duration);
                                    RepairCompletedDateTime.setText(repairDate);
                                    RepairedByTxt.setText(repairedBy);
                                    simpleRatingBar.setRating(0);
                                    acceptException();
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
                public void onFailure(Call<List<MBRepairDetail>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again." + t.getMessage(), getContext(), false);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class AcceptBreakdownExceptionAdapter extends RecyclerView.Adapter<AcceptBreakdownExceptionAdapter.ViewHolder> {

        Context context;
        List<MBRepairDetail> mbRepairDetails;
        List<MBRepairDetail> filteredProgramList;

        public AcceptBreakdownExceptionAdapter(Context context, List<MBRepairDetail> mbRepairDetails) {
            this.context = context;
            this.mbRepairDetails = mbRepairDetails;
            this.filteredProgramList = mbRepairDetails;
        }

        @NonNull
        @Override
        public AcceptBreakdownExceptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.accept_exception_recyclerview, parent, false);
            return new AcceptBreakdownExceptionAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AcceptBreakdownExceptionAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MBRepairDetail mbRepairDetail = filteredProgramList.get(position);
            Log.i("info", "mbRepairDetail: " + mbRepairDetail);
            try {
                holder.problemTextView.setText(mbRepairDetail.getExceptionReason().getReasonName());
                Log.i("info", "rez: " + mbRepairDetail.getExceptionReason().getReasonName());

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return filteredProgramList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView acceptExceptionqueueLayout;
            TextView problemTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                acceptExceptionqueueLayout = itemView.findViewById(R.id.acceptExceptionqueueLayout);
                problemTextView = itemView.findViewById(R.id.problemTextView);
            }

        }
    }

    private void saveException() {
        urgentCheck = brView.findViewById(R.id.urgentCheck);
        btnCancel = brView.findViewById(R.id.btnCancel);
        btnSave = brView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view2 -> {
            spotsDialog.show();
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100097", "Create");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                if (!collcetListErId.isEmpty()) {
                                    MBHeaderDto mbHeaderDto = new MBHeaderDto();
                                    mbHeaderDto.setMBHId("0");
                                    mbHeaderDto.setMachineCode(MachineCode);
                                    mbHeaderDto.setRequestBy(UserName);
                                    mbHeaderDto.setLineId(lineId);
                                    if (urgentCheck.isChecked()) {
                                        strValue = "True";
                                    } else {
                                        strValue = "False";
                                    }
                                    mbHeaderDto.setIsUrgent(Boolean.valueOf(strValue));
                                    for (int i = 0; i < collcetListErId.size(); i++) {
                                        collcetList.clear();
                                        MBDetailDto mbDetailDto = new MBDetailDto();
                                        mbDetailDto.setERId(collcetListErId.get(i));
                                        collcetList.add(mbDetailDto);
                                    }
                                    PostExceptionDto postExceptionDto = new PostExceptionDto();
                                    postExceptionDto.setMBHeaderDto(mbHeaderDto);
                                    postExceptionDto.setMBDetailDto(collcetList);
                                    Log.i("info", "postExceptionDto: " + postExceptionDto);
                                    spotsDialog.show();
                                    Call<String> exceptionCall = retrofitApiInterface.createMB("Bearer" + " " + uToken, postExceptionDto);
                                    exceptionCall.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call1, Response<String> response1) {
                                            try {
                                                if (response1.isSuccessful()) {
                                                    if (response1.body() != null) {
                                                        spotsDialog.dismiss();
                                                        String mSuccess = response1.body().toString();
                                                        mSuccess = mSuccess.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                        mSuccess = mSuccess.replace("\\r\\nত্রুটি :", "");
                                                        btnOk = viewSave.findViewById(R.id.btnOk);
                                                        dialogCustom.setContentView(viewSave);
                                                        dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                        txtMessage = viewSave.findViewById(R.id.txtMessage);
                                                        txtMessage.setText(mSuccess);
                                                        dialogCustom.show();
                                                        btnOk.setOnClickListener(v1 -> {
                                                            dialogCustom.dismiss();
                                                        });
                                                        sendNotification();
                                                        collcetListErId.clear();
                                                        collcetList.clear();
                                                        if (breakdownExceptionAdapter != null) {
                                                            breakdownExceptionList.clear();
                                                            breakdownExceptionAdapter.notifyDataSetChanged();
                                                        }
                                                        urgentCheck.setChecked(false);
                                                        selectedItemPosition = -1;
                                                        refresh();
                                                        dialog1.dismiss();
                                                    }
                                                } else if (!response1.isSuccessful()) {
                                                    if (response1.errorBody() != null) {
                                                        spotsDialog.dismiss();
                                                        Gson gson = new GsonBuilder().create();
                                                        try {
                                                            String mError = gson.toJson(response1.errorBody().string());
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
                                        public void onFailure(Call<String> call1, Throwable t) {
                                            spotsDialog.dismiss();
                                            showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                                        }
                                    });
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
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                    Log.i("info", "Failed: " + t);
                }
            });
        });
        btnCancel.setOnClickListener(view -> {
            collcetListErId.clear();
            collcetList.clear();
            if (breakdownExceptionAdapter != null) {
                breakdownExceptionList.clear();
                breakdownExceptionAdapter.notifyDataSetChanged();
            }
            urgentCheck.setChecked(false);
            selectedItemPosition = -1;
            refresh();
            dialog1.dismiss();
        });
    }

    private void acceptException() {
        btnAcceptConfirm.setOnClickListener(v -> {
            Log.i("info", "requestBy : " + requestBy);
            if (requestBy.equals(LoginName)) {
                try {
                    spotsDialog.show();
                    Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100097", "Approval");
                    getObjectCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        spotsDialog.dismiss();
                                        ratingNumber = simpleRatingBar.getRating(); // get rating number from a rating bar
                                        UpdateQueue updateQueue = new UpdateQueue();
                                        updateQueue.setMBHId(MbhId);
                                        updateQueue.setMachineCode(MachineCode);
                                        updateQueue.setRequestBy(UserName);
                                        updateQueue.setRepairRating(ratingNumber);
                                        Log.i("info", "ApprovalQueue: " + updateQueue);
                                        spotsDialog.show();
                                        Call<String> approvalQueueCall = retrofitApiInterface.approvalQueueMB("Bearer" + " " + uToken, updateQueue);
                                        approvalQueueCall.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call1, Response<String> response1) {
                                                try {
                                                    if (response1.isSuccessful()) {
                                                        if (response1.body() != null) {
                                                            spotsDialog.dismiss();
                                                            dialog1.dismiss();
                                                            ratingNumber = Float.valueOf(0);
                                                            String mSuccess = response1.body().toString();
                                                            mSuccess = mSuccess.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                            mSuccess = mSuccess.replace("\\r\\nত্রুটি :", "");
//                                                        btnOk = viewSave.findViewById(R.id.btnOk);
//                                                        dialogCustom.setContentView(viewSave);
//                                                        dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                                                        txtMessage = viewSave.findViewById(R.id.txtMessage);
//                                                        txtMessage.setText(mSuccess);
//                                                        dialogCustom.show();
//                                                        btnOk.setOnClickListener(v1 -> {
//                                                            dialogCustom.dismiss();
//                                                        });
                                                            if (machineBreakdownAdapter != null) {
                                                                machineBreakdownList.clear();
                                                                machineBreakdownAdapter.notifyDataSetChanged();
                                                            }
                                                            refresh();
                                                        }
                                                    } else if (!response1.isSuccessful()) {
                                                        if (response1.errorBody() != null) {
                                                            spotsDialog.dismiss();
                                                            dialog1.dismiss();
                                                            Gson gson = new GsonBuilder().create();
                                                            try {
                                                                String mError = gson.toJson(response1.errorBody().string());
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
                                                                    refresh();
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
                        public void onFailure(Call<String> call, Throwable t) {
                            spotsDialog.dismiss();
                            showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                            Log.i("info", "Failed: " + t);
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                dialog1.dismiss();
                showAlertDialog("Error: ", "You are not authorised to confirm queue.", getContext(), false);
            }

        });
    }

    private void sendNotification() {
        String message = null;
        StringBuilder sb = new StringBuilder();
        for (String item : collcetListErName) {
            sb.append(item.toString() + "," + "\n");
        }
        if (strValue.equals("True")) {
            urgentMessage = "Urgent Repairing";
        } else {
            urgentMessage = "Normal Repairing";
        }
        message = "Machine Code: " + MachineCode + "\nLine Name/Location :" + lineName + "\nProblems : " + problemName + "\nRequest By :" + UserName + "\nRequired Type :" + urgentMessage;
        for (DeviceRegistrationToken deviceRT : deviceRegistrationTokenList) {
            Log.i("Info", "deviceRT: " + deviceRT.getDeviceRegistration().getDeviceTocken());
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(deviceRT.getDeviceRegistration().getDeviceTocken(),
                    "Breakdown Notification",
                    message,
                    getContext(), getActivity());
            notificationsSender.SendNotifications();

        }
    }

    private void refresh() {
        String line_Name = selectLineText.getText().toString();
        if (line_Name != null) {
            try {
                spotsDialog.show();
                Call<List<MachineBreakdown>> MachineBreakdownCall = retrofitApiInterface.machineBreakdown("Bearer" + " " + uToken, line_Name);
                MachineBreakdownCall.enqueue(new Callback<List<MachineBreakdown>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<List<MachineBreakdown>> call, Response<List<MachineBreakdown>> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    spotsDialog.dismiss();
                                    machineBreakdownList = response.body();
                                    machineBreakdownAdapter = new MachineBreakdownAdapter(getContext(), machineBreakdownList);
                                    machine_breakdownRecyclerView.setHasFixedSize(true);
                                    GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                                    machine_breakdownRecyclerView.setLayoutManager(mLayoutManager);
                                    machine_breakdownRecyclerView.setAdapter(machineBreakdownAdapter);
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
                    public void onFailure(Call<List<MachineBreakdown>> call, Throwable t) {
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
                machineCodeEditTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        final String query = s.toString().toLowerCase().trim();
                        final ArrayList<MachineBreakdown> filteredList = new ArrayList<>();

                        for (int i = 0; i < machineBreakdownList.size(); i++) {

                            final String text = machineBreakdownList.get(i).getMachineCode().toLowerCase();
                            if (text.contains(query)) {
                                filteredList.add(machineBreakdownList.get(i));
                            }
                        }

                        machine_breakdownRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        machineBreakdownAdapter = new MachineBreakdownAdapter(getContext(), filteredList);
                        machine_breakdownRecyclerView.setAdapter(machineBreakdownAdapter);
                        machineBreakdownAdapter.notifyDataSetChanged();  // data set changed
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            spotsDialog.dismiss();
            showAlertDialog("Error: ", "Select Line.", getContext(), false);
        }
    }

    private void getDeviceToken() {
        try {
            spotsDialog.show();
            Call<List<DeviceRegistrationToken>> DeviceRegistrationTokenCall = retrofitApiInterface.drToken("Bearer" + " " + uToken, deptId, sectionId);
            DeviceRegistrationTokenCall.enqueue(new Callback<List<DeviceRegistrationToken>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<DeviceRegistrationToken>> call, Response<List<DeviceRegistrationToken>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                deviceRegistrationTokenList = response.body();
                                Log.i("info", "deviceRegistrationTokenList: " + deviceRegistrationTokenList);
                            }
                        } else if (!response.isSuccessful()) {
                            if (response.errorBody() != null) {
                                spotsDialog.dismiss();
//                                Gson gson = new GsonBuilder().create();
//                                try {
//                                    String mError = gson.toJson(response.errorBody().string());
//                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
//                                    mError = mError.replace("\\r\\nত্রুটি :", "");
//
//                                    btnOk = viewError.findViewById(R.id.btnOk);
//                                    dialogCustom.setContentView(viewError);
//                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                                    txtMessage = viewError.findViewById(R.id.txtMessage);
//                                    txtMessage.setText(mError);
//                                    dialogCustom.show();
//                                    btnOk.setOnClickListener(v1 -> {
//                                        dialogCustom.dismiss();
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<DeviceRegistrationToken>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void scanMachineCode() {
        mcMaintenance_layout.setVisibility(View.VISIBLE);
        machineCodeEditTxt.requestFocus();
        imgMcScanner.setOnClickListener(v -> {
            mcCodeDetectorsAndSources();
            mcMaintenance_layout.setVisibility(View.GONE);
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

                                mcMaintenance_layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                goBack.setVisibility(View.GONE);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                machineCodeEditTxt.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                mcMaintenance_layout.setVisibility(View.VISIBLE);
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