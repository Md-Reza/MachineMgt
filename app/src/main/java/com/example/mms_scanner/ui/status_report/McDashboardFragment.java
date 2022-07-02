package com.example.mms_scanner.ui.status_report;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.breakdownqueue.breakdownqueue_exception.BreakdownQueueException;
import com.example.mms_scanner.model.login.AllSection;
import com.example.mms_scanner.model.status_report.MCBreakdownReport;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.ui.HomeFragment;
import com.example.mms_scanner.utils.SharedPref;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class McDashboardFragment extends Fragment {

    FragmentManager fragmentManager;

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    ImageButton backButton;
    TextView showTxt, selectSectionTxt, toDateTxt, fromDateTxt, txtMessage;
    TabLayout tabLayout;
    FrameLayout frame_layout;

    String uToken, UserName, MbhId, MachineCode, fromDate, toDate, section_Id, section_Name;

    List<AllSection> getAllSections;
    Dialog dialog, dialogCustom;
    RecyclerView lineRecyclerView;
    SearchView searchView;
    LinesDialogAdapter linesDialogAdapter;
    Button btnQueueStart, btnQueueCancel, btnOk;

    private DatePickerDialog picker;

    View lnView, breView, viewSave, viewError;

    SearchView search_view;
    RecyclerView machine_breakdownQueueRecyclerView, breakdown_exceptionQueueRecyclerView;

    List<MCBreakdownReport> machineBreakdownQueueList;
    MachineBreakdownQueueAdapter machineBreakdownQueueAdapter;

    List<BreakdownQueueException> breakdownQueueExceptionList;
    BreakdownQueueExceptionAdapter breakdownQueueExceptionAdapter;

    public McDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mc_dashboard, container, false);

        fragmentManager = getFragmentManager();

        lnView = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);
        lineRecyclerView = lnView.findViewById(R.id.line_list_dialog_layout_RecyclerView);
        searchView = lnView.findViewById(R.id.searchView);

        breView = getActivity().getLayoutInflater().inflate(R.layout.dialog_breakdownqueue_exception_, null);
        breakdown_exceptionQueueRecyclerView = breView.findViewById(R.id.breakdown_exceptionQueueRecyclerView);
        btnQueueStart = breView.findViewById(R.id.btnQueueStart);
        btnQueueCancel = breView.findViewById(R.id.btnQueueCancel);

        viewSave = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);

        viewError = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
        btnOk = viewError.findViewById(R.id.btnOk);
        txtMessage = viewError.findViewById(R.id.txtMessage);

        uToken = SharedPref.read("UserToken", "");
        UserName = SharedPref.read("UserName", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        SharedPref.init(getContext());
        gson = new Gson();

        dialog = new Dialog(getContext());
        dialogCustom = new Dialog(getContext());

        search_view = root.findViewById(R.id.search_view);
        machine_breakdownQueueRecyclerView = root.findViewById(R.id.machine_breakdownQueueRecyclerView);

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.container, homeFragment, homeFragment.getTag()).commit();
        });

        showTxt = root.findViewById(R.id.showTxt);
        selectSectionTxt = root.findViewById(R.id.selectSectionTxt);
        toDateTxt = root.findViewById(R.id.toDateTxt);
        fromDateTxt = root.findViewById(R.id.fromDateTxt);

        tabLayout = root.findViewById(R.id.tabLayout);
        frame_layout = root.findViewById(R.id.frame_layout);

        getAllSections = new ArrayList<AllSection>();
        machineBreakdownQueueList = new ArrayList<MCBreakdownReport>();
        breakdownQueueExceptionList = new ArrayList<BreakdownQueueException>();

        fromDate();
        toDate();
        section();
        dataShow();

        return root;
    }

    private void fromDate() {
        final Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        fromDateTxt.setText(new StringBuilder()
                .append(yy).append("-").append(mm + 1).append("-")
                .append(dd));
        fromDateTxt.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> fromDateTxt.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth), year, month, day);
            picker.show();
        });
    }

    private void toDate() {
        final Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        toDateTxt.setText(new StringBuilder()
                .append(yy).append("-").append(mm + 1).append("-")
                .append(dd));
        toDateTxt.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> toDateTxt.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth), year, month, day);
            picker.show();
        });
    }

    private void section() {
        selectSectionTxt.setOnClickListener(v -> {
            Call<List<AllSection>> soList = retrofitApiInterface.getAllSection();
            soList.enqueue(new Callback<List<AllSection>>() {
                @Override
                public void onResponse(Call<List<AllSection>> call, Response<List<AllSection>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                getAllSections = response.body();
                                if (!getAllSections.isEmpty()) {
                                    dialog.setCancelable(true);
                                    dialog.setContentView(lnView);
                                    linesDialogAdapter = new LinesDialogAdapter(getContext(), getAllSections);
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
                                    spotsDialog.dismiss();
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
                public void onFailure(Call<List<AllSection>> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Error: ", "Connection was closed. Please try again.", getContext(), false);
                }
            });
        });
    }

    public class LinesDialogAdapter extends RecyclerView.Adapter<LinesDialogAdapter.ViewHolder> implements Filterable {

        Context context;
        List<AllSection> getAllSectionList;
        List<AllSection> filteredLineList;

        public LinesDialogAdapter(Context context, List<AllSection> getAllSectionList) {
            this.context = context;
            this.getAllSectionList = getAllSectionList;
            this.filteredLineList = getAllSectionList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredLineList = getAllSectionList;
                    } else {
                        List<AllSection> filteredList = new ArrayList<>();
                        for (AllSection section : getAllSectionList) {

                            if (section.getSectionId().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(section);
                            }
                            if (section.getSectionName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(section);
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
                    filteredLineList = (List<AllSection>) results.values;
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
            final AllSection so_number = filteredLineList.get(position);
            holder.txtLineCode.setText(so_number.getSectionId());
            holder.txtLinesName.setText(so_number.getSectionName());
            holder.line_dialog_layout.setOnClickListener(v -> {
                section_Id = filteredLineList.get(position).getSectionId();
                SharedPref.write("section_Id", section_Id);
                section_Name = filteredLineList.get(position).getSectionName();
                selectSectionTxt.setText(section_Name);
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

            if (so_number.getSectionName().equals("Block-A")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_a));
            } else if (so_number.getSectionName().equals("Block-B")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_b));

            } else if (so_number.getSectionName().equals("Block-C")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_c));

            } else if (so_number.getSectionName().equals("Block-D")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_d));

            } else if (so_number.getSectionName().equals("Block-E")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_e));

            } else if (so_number.getSectionName().equals("Block-J")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_j));

            } else if (so_number.getSectionName().equals("SAM")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_s));

            } else if (so_number.getSectionName().equals("FIN")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_f));

            } else if (so_number.getSectionName().equals("AQL")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_a));

            } else if (so_number.getSectionName().equals("TEC")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_t));

            } else if (so_number.getSectionName().equals("FGWH")) {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.icon_f));

            } else {
                holder.circleImg.setImageDrawable(
                        getResources().getDrawable(
                                R.drawable.newlogo));
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

    private void dataShow() {
        showTxt.setOnClickListener(v -> {
            fromDate = fromDateTxt.getText().toString();
            SharedPref.write("fromDate", fromDate);
            toDate = toDateTxt.getText().toString();
            SharedPref.write("toDate", toDate);
            section_Name = selectSectionTxt.getText().toString();
            if (fromDate.isEmpty()) {
                fromDateTxt.requestFocus();
                showAlertDialog("warning.: ", "select From Date.", getContext(), false);
            } else if (toDate.isEmpty()) {
                toDateTxt.requestFocus();
                showAlertDialog("warning.: ", "select To Date.", getContext(), false);
            } else if (section_Name.isEmpty()) {
                selectSectionTxt.requestFocus();
                showAlertDialog("warning.: ", "select section name.", getContext(), false);
            } else {
                MC_Breakdown_Queue_Fragment mc_breakdown_queue_fragment = new MC_Breakdown_Queue_Fragment();
                fragmentManager.beginTransaction().replace(R.id.frame_layout, mc_breakdown_queue_fragment, mc_breakdown_queue_fragment.getTag()).commit();
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                MC_Breakdown_Queue_Fragment mc_breakdown_queue_fragment = new MC_Breakdown_Queue_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.frame_layout, mc_breakdown_queue_fragment, mc_breakdown_queue_fragment.getTag()).commit();
                                break;
                            case 1:
                                Pending_MachinesList_Fragment pending_machinesList_fragment = new Pending_MachinesList_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.frame_layout, pending_machinesList_fragment, pending_machinesList_fragment.getTag()).commit();
                                break;
                            case 2:
                                TobeVerified_Fragment tobeVerified_fragment = new TobeVerified_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.frame_layout, tobeVerified_fragment, tobeVerified_fragment.getTag()).commit();
                                break;

                            case 3:
                                Repairs_CompleteList_Fragment repairs_completeList_fragment = new Repairs_CompleteList_Fragment();
                                fragmentManager.beginTransaction().replace(R.id.frame_layout, repairs_completeList_fragment, repairs_completeList_fragment.getTag()).commit();
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
                try {
                    spotsDialog.show();
                    Call<List<MCBreakdownReport>> MachineBreakdownQueueCall = retrofitApiInterface.getMCInitiatedListDateBySection("Bearer" + " " + uToken, section_Id);
                    MachineBreakdownQueueCall.enqueue(new Callback<List<MCBreakdownReport>>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<List<MCBreakdownReport>> call, Response<List<MCBreakdownReport>> response) {
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        spotsDialog.dismiss();
                                        machineBreakdownQueueList = response.body();
                                        machineBreakdownQueueAdapter = new MachineBreakdownQueueAdapter(getContext(), machineBreakdownQueueList);
                                        machine_breakdownQueueRecyclerView.setHasFixedSize(true);
                                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
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
        });
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
                            if (programNumber.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
                            if (programNumber.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filteredList.add(programNumber);
                            }
//                            if (programNumber.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
//                            if (programNumber.getLineName().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
//                            if (programNumber.getRequestBy().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
//                            if (programNumber.getRepairBy().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
//                            if (programNumber.getCategoryName().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
//                            if (programNumber.getShortCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
//                            if (programNumber.getSubCategoryName().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                                filteredList.add(programNumber);
//                            }
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

        @NonNull
        @Override
        public MachineBreakdownQueueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.machine_breakdownqueue_recycalerview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final MCBreakdownReport machineBreakdownQueue = filteredProgramList.get(position);

            try {
                holder.machineCodeQueueTxt.setText(machineBreakdownQueue.getMachineCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.ShortCodeQueueTxt.setText("S.Code:");
                holder.ShortCodeQueue.setText(machineBreakdownQueue.getShortCode());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                holder.CategoryNameQueueTxt.setText("C.Name:");
                holder.CategoryNameQueue.setText(machineBreakdownQueue.getCategoryName());
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
                holder.lineName.setText(machineBreakdownQueue.getLineName());
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