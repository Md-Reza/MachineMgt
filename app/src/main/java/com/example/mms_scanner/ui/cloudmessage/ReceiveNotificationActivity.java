package com.example.mms_scanner.ui.cloudmessage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.breakdownqueue.UpdateQueue;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.breakdownqueue.machine_breakdownqueue.MachineBreakdownQueue;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.utils.SharedPref;
import com.example.mms_scanner.view.MainActivity;
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

public class ReceiveNotificationActivity extends AppCompatActivity {

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;
    View breView, viewSave, viewError;
    Dialog dialog, dialogCustom;

    ImageButton backButton;
    SearchView search_view;
    RecyclerView machine_breakdownQueueRecyclerView, breakdown_exceptionQueueRecyclerView;

    List<MachineBreakdownQueue> machineBreakdownQueueList;
    MachineBreakdownQueueAdapter machineBreakdownQueueAdapter;

    List<BreakdownQueueException> breakdownQueueExceptionList;
    BreakdownQueueExceptionAdapter breakdownQueueExceptionAdapter;

    Button btnQueueStart, btnQueueCancel, btnOk;

    String uToken, sectionId, UserName, MbhId, MachineCode;

    TextView txtMessage;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_notification);


        breView = getLayoutInflater().inflate(R.layout.dialog_breakdownqueue_exception_, null);
        viewSave = getLayoutInflater().inflate(R.layout.custom_message, null);
        viewError = getLayoutInflater().inflate(R.layout.custom_error, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        uToken = SharedPref.read("UserToken", "");
        sectionId = SharedPref.read("sectionId", "");
        UserName = SharedPref.read("UserName", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(this, R.style.Custom);
        SharedPref.init(getApplicationContext());
        gson = new Gson();

        dialog = new Dialog(this);
        dialogCustom = new Dialog(this);

        search_view = findViewById(R.id.search_view);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiveNotificationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        });

        machineBreakdownQueueList = new ArrayList<MachineBreakdownQueue>();
        machine_breakdownQueueRecyclerView = findViewById(R.id.machine_breakdownQueueRecyclerView);
        machine_breakdownQueueRecyclerView.setNestedScrollingEnabled(false);

        breakdownQueueExceptionList = new ArrayList<BreakdownQueueException>();

        machineBreakdownQueue();

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
    }

    private void machineBreakdownQueue() {
        try {
            Call<List<MachineBreakdownQueue>> MachineBreakdownQueueCall = retrofitApiInterface.getInitiateBreakdownQueue("Bearer" + " " + uToken, sectionId);
            MachineBreakdownQueueCall.enqueue(new Callback<List<MachineBreakdownQueue>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<MachineBreakdownQueue>> call, Response<List<MachineBreakdownQueue>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                spotsDialog.dismiss();
                                machineBreakdownQueueList = response.body();
                                machineBreakdownQueueAdapter = new MachineBreakdownQueueAdapter(getApplicationContext(), machineBreakdownQueueList);
                                machine_breakdownQueueRecyclerView.setHasFixedSize(true);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getApplicationContext(), false);
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
            View view = inflater.inflate(R.layout.machine_breakdownqueue_recycalerview, parent, false);
            return new MachineBreakdownQueueAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MachineBreakdownQueueAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MachineBreakdownQueue machineBreakdownQueue = filteredProgramList.get(position);

            try {
                holder.machineCodeQueueTxt.setText(machineBreakdownQueue.getMachineCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.ShortCodeQueueTxt.setText("S.Code:");
                holder.ShortCodeQueue.setText(machineBreakdownQueue.getMachineInfo().getShortCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryNameQueueTxt.setText("C.Name:");
                holder.CategoryNameQueue.setText(machineBreakdownQueue.getMachineInfo().getCategoryName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String startDT = machineBreakdownQueue.getStartDateTime();
                SimpleDateFormat parser_startDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat formatterr_startDT = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                try {
                    String result_startDT = formatterr_startDT.format(parser_startDT.parse(startDT));
                    holder.StartTimeQueueTxt.setText("Start Time:");
                    holder.StartTimeQueue.setText(result_startDT);
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
                    holder.EndTimeQueueTxt.setText("End Time:");
                    holder.EndTimeQueue.setText(result_endDT);
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
                    holder.RequestTimeQueueTxt.setText("Request Time:");
                    holder.RequestTimeQueue.setText(result_reqDT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.DurationQueueTxt.setText("Duration:");
                holder.DurationQueue.setText(machineBreakdownQueue.getDuration());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.lineNameTxt.setText("Line:");
                holder.lineName.setText(machineBreakdownQueue.getLines().getLineName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                String status = machineBreakdownQueue.getStatus();
                boolean isUrgent = machineBreakdownQueue.getIsUrgent();
                if (status.equals("3") && isUrgent == true) {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.redColor)));
                } else if (status.equals("2")) {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.yellowColor)));
                } else if (status.equals("1")) {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.greenColor)));
                } else {
                    holder.itemLayoutQueue.setCardBackgroundColor(Color.parseColor(getString(R.string.whiteColor)));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            holder.imgDetailsQueue.setOnClickListener(view -> {
                try {
                    String statusCode = machineBreakdownQueue.getStatus();
                    if (statusCode.equals("3")) {
                        MbhId = machineBreakdownQueue.getMbhId();
                        MachineCode = machineBreakdownQueue.getMachineCode();
                        breakdownQueueException();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            });

            holder.btnEndQueue.setVisibility(View.GONE);
            try {
                String statusCode = machineBreakdownQueue.getStatus();
                if (statusCode.equals("2")) {
                    holder.btnEndQueue.setVisibility(View.VISIBLE);
                    holder.btnEndQueue.setOnClickListener(view -> {
                        String MbhId = machineBreakdownQueue.getMbhId();
                        String MachineCode = machineBreakdownQueue.getMachineCode();
                        UpdateQueue updateQueue = new UpdateQueue();
                        updateQueue.setMBHId(MbhId);
                        updateQueue.setMachineCode(MachineCode);
                        updateQueue.setRequestBy(UserName);
                        Log.i("info", "CompleteQueue: " + updateQueue);
                        try {
                            spotsDialog.show();
                            Call<String> completeQueueMBCall = retrofitApiInterface.completeQueueMB("Bearer" + " " + uToken, updateQueue);
                            completeQueueMBCall.enqueue(new Callback<String>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        if (response.isSuccessful()) {
                                            if (response.body() != null) {
                                                spotsDialog.dismiss();

                                                String mSuccess = response.body().toString();
                                                mSuccess = mSuccess.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                mSuccess = mSuccess.replace("\\r\\nত্রুটি :", "");
                                                View view = getLayoutInflater().inflate(R.layout.custom_message, null);
                                                btnOk = view.findViewById(R.id.btnOk);
                                                dialogCustom.setContentView(view);
                                                dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                txtMessage = view.findViewById(R.id.txtMessage);
                                                txtMessage.setText(mSuccess);
                                                dialogCustom.show();
                                                btnOk.setOnClickListener(v1 -> {
                                                    dialogCustom.dismiss();
                                                });
                                                if (machineBreakdownQueueAdapter != null) {
                                                    machineBreakdownQueueList.clear();
                                                    machineBreakdownQueueAdapter.notifyDataSetChanged();
                                                }
                                                machineBreakdownQueue();
                                            }
                                        } else if (!response.isSuccessful()) {
                                            if (response.errorBody() != null) {
                                                spotsDialog.dismiss();
                                                Gson gson = new GsonBuilder().create();
                                                try {
                                                    String mError = gson.toJson(response.errorBody().string());
                                                    mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                                    mError = mError.replace("\\r\\nত্রুটি :", "");
                                                    View view = getLayoutInflater().inflate(R.layout.custom_error, null);
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
                                public void onFailure(Call<String> call, Throwable t) {
                                    spotsDialog.dismiss();
                                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getApplicationContext(), false);
                                }
                            });
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    holder.btnEndQueue.setVisibility(View.GONE);
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
                    lineNameTxt, lineName;
            Button btnEndQueue;
            ImageView imgDetailsQueue;

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

                lineNameTxt = itemView.findViewById(R.id.lineNameTxt);
                lineName = itemView.findViewById(R.id.lineName);

                btnEndQueue = itemView.findViewById(R.id.btnEndQueue);
                imgDetailsQueue = itemView.findViewById(R.id.imgDetailsQueue);
            }
        }
    }

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
                                    breakdownQueueExceptionAdapter = new BreakdownQueueExceptionAdapter(getApplicationContext(), breakdownQueueExceptionList);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    breakdown_exceptionQueueRecyclerView = breView.findViewById(R.id.breakdown_exceptionQueueRecyclerView);
                                    breakdown_exceptionQueueRecyclerView.setHasFixedSize(true);
                                    breakdown_exceptionQueueRecyclerView.setLayoutManager(mLayoutManager);
                                    breakdown_exceptionQueueRecyclerView.setAdapter(breakdownQueueExceptionAdapter);
                                    breakdownQueueExceptionAdapter.notifyDataSetChanged();
                                    startQueueException();
                                    dialog.show();
                                } else {
                                    showAlertDialog("Error: ", "No data found.", getApplicationContext(), false);
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
                                    View view = getLayoutInflater().inflate(R.layout.custom_error, null);
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
                public void onFailure(Call<List<BreakdownQueueException>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getApplicationContext(), false);
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

    private void startQueueException() {
        btnQueueCancel = breView.findViewById(R.id.btnQueueCancel);
        btnQueueStart = breView.findViewById(R.id.btnQueueStart);
        btnQueueStart.setOnClickListener(view2 -> {
            UpdateQueue updateQueue = new UpdateQueue();
            updateQueue.setMBHId(MbhId);
            updateQueue.setMachineCode(MachineCode);
            updateQueue.setRequestBy(UserName);
            Log.i("info", "updateQueue: " + updateQueue);
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

                                    String mSuccess = response.body().toString();
                                    mSuccess = mSuccess.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                    mSuccess = mSuccess.replace("\\r\\nত্রুটি :", "");
                                    View view = getLayoutInflater().inflate(R.layout.custom_message, null);
                                    btnOk = view.findViewById(R.id.btnOk);
                                    dialogCustom.setContentView(view);
                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    txtMessage = view.findViewById(R.id.txtMessage);
                                    txtMessage.setText(mSuccess);
                                    dialogCustom.show();
                                    btnOk.setOnClickListener(v1 -> {
                                        dialogCustom.dismiss();
                                    });
                                    if (breakdownQueueExceptionAdapter != null) {
                                        breakdownQueueExceptionList.clear();
                                        breakdownQueueExceptionAdapter.notifyDataSetChanged();
                                    }
                                    dialog.dismiss();
                                    machineBreakdownQueue();
                                }
                            } else if (!response.isSuccessful()) {
                                if (response.errorBody() != null) {
                                    spotsDialog.dismiss();
                                    Gson gson = new GsonBuilder().create();
                                    try {
                                        String mError = gson.toJson(response.errorBody().string());
                                        mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                        mError = mError.replace("\\r\\nত্রুটি :", "");
                                        View view = getLayoutInflater().inflate(R.layout.custom_error, null);
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
                    public void onFailure(Call<String> call, Throwable t) {
                        spotsDialog.dismiss();
                        showAlertDialog("Error: ", "Connection was closed. Please try again.", getApplicationContext(), false);
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
        btnQueueCancel.setOnClickListener(view -> {
            if (breakdownQueueExceptionAdapter != null) {
                breakdownQueueExceptionList.clear();
                breakdownQueueExceptionAdapter.notifyDataSetChanged();
            }
            machineBreakdownQueue();
            dialog.dismiss();
        });
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