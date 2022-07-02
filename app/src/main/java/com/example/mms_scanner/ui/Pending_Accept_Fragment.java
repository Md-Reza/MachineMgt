package com.example.mms_scanner.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.breakdown.breakdown_exception.BreakdownException;
import com.example.mms_scanner.model.breakdown.breakdown_exception.MBDetailDto;
import com.example.mms_scanner.model.breakdownqueue.UpdateQueue;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MachineBreakdownQueue;
import com.example.mms_scanner.model.line.GetLine;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.utils.SharedPref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pending_Accept_Fragment extends Fragment {

    FragmentManager fragmentManager;
    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    View lnView, breView, viewError;

    TextView selectLineText, txtMessage;
    Button btnShow;
    ImageButton backButton;

    List<GetLine> getLines;
    Dialog dialog, dialog1, dialogCustom;
    RecyclerView lineRecyclerView, pending_accept_list, breakdown_exceptionQueueRecyclerView;
    SearchView searchView, search_view, Search_View;
    LinesDialogAdapter linesDialogAdapter;
    BreakdownQueueExceptionAdapter breakdownQueueExceptionAdapter;

    List<MachineBreakdownQueue> machineBreakdownList;
    List<BreakdownQueueException> breakdownQueueExceptionList;
    MachineBreakdownAdapter machineBreakdownAdapter;
    String uToken, lineName, lineId;

    List<BreakdownException> breakdownExceptionList;
    private ArrayList<String> collcetListErId;
    List<MBDetailDto> collcetList;

    Button btnQueueStart, btnQueueCancel, btnOk;

    String MbhId, MachineCode, UserName;
    public static String strValue = null;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pending__accept, container, false);

        lnView = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);
        breView = getActivity().getLayoutInflater().inflate(R.layout.dialog_breakdownqueue_exception_, null);
        viewError = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
        btnQueueStart = breView.findViewById(R.id.btnQueueStart);
        btnQueueCancel = breView.findViewById(R.id.btnQueueCancel);

        fragmentManager = getFragmentManager();
        uToken = SharedPref.read("UserToken", "");
        UserName = SharedPref.read("UserName", "");

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
        dialogCustom.setCancelable(false);

        machineBreakdownList = new ArrayList<MachineBreakdownQueue>();
        pending_accept_list = root.findViewById(R.id.pending_accept_list);

        breakdownExceptionList = new ArrayList<BreakdownException>();
        collcetListErId = new ArrayList<String>();
        collcetList = new ArrayList<MBDetailDto>();
        breakdownQueueExceptionList = new ArrayList<BreakdownQueueException>();

        line();
        machineBreakdown();

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
                public void onFailure(Call<List<GetLine>> call, Throwable t) {
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        });
    }

    private void getMachineBreakdownDetails() {
        String line_Name = selectLineText.getText().toString();
        if (line_Name.isEmpty()) {
            showAlertDialog("Error: ", "Select Line.", getContext(), false);
            return;
        }
        try {
            spotsDialog.show();
            Call<List<MachineBreakdownQueue>> MachineBreakdownCall = retrofitApiInterface.getMechanicCompletedListByLine("Bearer" + " " + uToken, lineId);
            MachineBreakdownCall.enqueue(new Callback<List<MachineBreakdownQueue>>() {
                @Override
                public void onResponse(Call<List<MachineBreakdownQueue>> call, Response<List<MachineBreakdownQueue>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                machineBreakdownList = response.body();
                                machineBreakdownAdapter = new MachineBreakdownAdapter(getContext(), machineBreakdownList);
                                pending_accept_list.setHasFixedSize(true);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                pending_accept_list.setLayoutManager(mLayoutManager);
                                pending_accept_list.setAdapter(machineBreakdownAdapter);
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
    }

    private void machineBreakdown() {
        btnShow.setOnClickListener(v -> {
            getMachineBreakdownDetails();
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

    public class MachineBreakdownAdapter extends RecyclerView.Adapter<MachineBreakdownAdapter.ViewHolder> implements Filterable {

        Context context;
        List<MachineBreakdownQueue> machineBreakdownList;
        List<MachineBreakdownQueue> filteredProgramList;

        public MachineBreakdownAdapter(Context context, List<MachineBreakdownQueue> machineBreakdownList) {
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
                        List<MachineBreakdownQueue> filteredList = new ArrayList<>();
                        for (MachineBreakdownQueue programNumber : machineBreakdownList) {
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
        public MachineBreakdownAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.pending_accept_recycalerview, parent, false);
            return new MachineBreakdownAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MachineBreakdownAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MachineBreakdownQueue machineBreakdown = filteredProgramList.get(position);

            try {
                holder.machineCodeTxt.setText(machineBreakdown.getMachineCode());
                Random r = new Random();
                holder.machineCodeTxt.setBackgroundColor(Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256)));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.ShortCodeTxt.setText("S.Code:");
                holder.ShortCode.setText(machineBreakdown.getMachineInfo().getShortCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryNameTxt.setText("C.Name:");
                holder.CategoryName.setText(machineBreakdown.getMachineInfo().getCategoryName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String startDT = machineBreakdown.getStartDateTime();
                SimpleDateFormat parser_startDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_startDT = formatterr_startDT.format(parser_startDT.parse(startDT));
                    holder.StartTimeTxt.setText("Start Time:");
                    holder.StartTime.setText(result_startDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String endDT = machineBreakdown.getEndDateTime();
                SimpleDateFormat parser_endDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_endDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_endDT = formatterr_endDT.format(parser_endDT.parse(endDT));
                    holder.EndTimeTxt.setText("End Time:");
                    holder.EndTime.setText(result_endDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                int status = Integer.parseInt(machineBreakdown.getStatus());
                if (status == 3) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
                } else if (status == 2) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
                } else if (status == 1) {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
                } else {
                    holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.DurationTxt.setText("Duration:");
                holder.Duration.setText(machineBreakdown.getDuration());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.reparingBy.setText(machineBreakdown.getRepairBy());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            holder.imgDetails.setOnClickListener(view -> {
                String statusCode = null;
                if (machineBreakdown != null) {
                    statusCode = machineBreakdown.getStatus();
                    MachineCode = machineBreakdown.getMachineCode();
                    MbhId = machineBreakdown.getMbhId();
                    breakdownQueueException();
                } else {
                    MbhId = machineBreakdown.getMbhId();
                    MachineCode = machineBreakdown.getMachineCode();
                    breakdownQueueException();
                }
            });

            holder.confirmChBx.setEnabled(false);
            holder.confirmChBx.setChecked(false);
            holder.btnAccept.setVisibility(View.GONE);
            try {
                boolean confirnm = machineBreakdown.getIsMechanicConfirm();
                if (confirnm == true) {
                    holder.confirmChBx.setEnabled(false);
                    holder.confirmChBx.setChecked(true);
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.btnAccept.setOnClickListener(view -> {
                        spotsDialog.show();
                        Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100097", "Approval");
                        getObjectCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            spotsDialog.dismiss();
                                            if (machineBreakdown != null) {
                                                MbhId = machineBreakdown.getMbhId();
                                            }
                                            String MachineCode = machineBreakdown.getMachineCode();
                                            UpdateQueue updateQueue = new UpdateQueue();
                                            updateQueue.setMBHId(MbhId);
                                            updateQueue.setMachineCode(MachineCode);
                                            updateQueue.setRequestBy(UserName);
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
                                                                String mSuccess = response1.body();
                                                                mSuccess = mSuccess.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                                mSuccess = mSuccess.replace("\\r\\nত্রুটি :", "");

                                                                View view = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);
                                                                btnOk = view.findViewById(R.id.btnOk);
                                                                dialogCustom.setContentView(view);
                                                                dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                                txtMessage = view.findViewById(R.id.txtMessage);
                                                                txtMessage.setText(mSuccess);
                                                                dialogCustom.show();
                                                                btnOk.setOnClickListener(v1 -> {
                                                                    dialogCustom.dismiss();
                                                                });

                                                                if (machineBreakdownAdapter != null) {
                                                                    machineBreakdownList.clear();
                                                                    machineBreakdownAdapter.notifyDataSetChanged();
                                                                }
                                                                getMachineBreakdownDetails();
                                                            }
                                                        } else if (!response1.isSuccessful()) {
                                                            if (response1.errorBody() != null) {
                                                                spotsDialog.dismiss();
                                                                Gson gson = new GsonBuilder().create();
                                                                try {
                                                                    String mError = gson.toJson(response1.errorBody().string());
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

                    });
                } else if (confirnm == false) {
                    holder.confirmChBx.setEnabled(false);
                    holder.confirmChBx.setChecked(false);
                    holder.btnAccept.setVisibility(View.GONE);
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
            CardView itemLayout;
            TextView machineCodeTxt, ShortCodeTxt, ShortCode, CategoryNameTxt, CategoryName,
                    StartTimeTxt, StartTime, EndTimeTxt, EndTime, DurationTxt, Duration, reparingBy;
            CheckBox confirmChBx;
            Button btnAccept;
            ImageView imgDetails;

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
                EndTimeTxt = itemView.findViewById(R.id.EndTimeTxt);
                EndTime = itemView.findViewById(R.id.EndTime);
                DurationTxt = itemView.findViewById(R.id.DurationTxt);
                Duration = itemView.findViewById(R.id.Duration);
                reparingBy = itemView.findViewById(R.id.reparingBy);

                confirmChBx = itemView.findViewById(R.id.confirmChBx);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                imgDetails = itemView.findViewById(R.id.imgDetails);
            }
        }
    }

    private void breakdownQueueException() {
        try {
            spotsDialog.show();
            Call<List<BreakdownQueueException>> BreakdownQueueExceptionCall = retrofitApiInterface.getBreakdownQueueException("Bearer" + " " + uToken, MbhId);
            Log.i("info", "MbhId: " + MbhId);
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