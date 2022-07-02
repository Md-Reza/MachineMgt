package com.example.mms_scanner.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.model.login.AllSection;
import com.example.mms_scanner.model.login.GetLogin;
import com.example.mms_scanner.model.login.PostLogin;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.ui.deviceRegister.DeviceRegistrationFragment;
import com.example.mms_scanner.utils.SharedPref;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    private TextInputEditText user_id, user_password;
    private Button btnLogin, btnCancel;
    private TextView txtMessage, txtSelectLine;

    String sectionName, sectionId;

    List<AllSection> getAllSections;
    Dialog dialog, dialogCustom;
    RecyclerView lineRecyclerView;
    SearchView searchView;
    LinesDialogAdapter linesDialogAdapter;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                }
            }).check();
        }

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        gson = new Gson();
        SharedPref.init(getApplicationContext());

        user_id = findViewById(R.id.user_id);
        user_password = findViewById(R.id.user_password);

        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            user_id.setText("");
            user_password.setText("");
            user_id.requestFocus();
        });

        txtSelectLine = findViewById(R.id.txtSelectLine);
        section();
        getAllSections = new ArrayList<AllSection>();
        dialog = new Dialog(this);
        dialogCustom = new Dialog(this);

        userLogin();
    }

    private void section() {
        txtSelectLine.setOnClickListener(v -> {
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
                                    View lnView = getLayoutInflater().inflate(R.layout.line_list, null);
                                    dialog.setContentView(lnView);
//                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    lineRecyclerView = lnView.findViewById(R.id.line_list_dialog_layout_RecyclerView);
                                    searchView = lnView.findViewById(R.id.searchView);
                                    linesDialogAdapter = new LinesDialogAdapter(getApplicationContext(), getAllSections);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                                    AlertCustomAlertDialog("No data found.");
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
                    AlertCustomAlertDialog("Connection was closed. Please try again." + t.getMessage());
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
                sectionId = filteredLineList.get(position).getSectionId();
                SharedPref.write("sectionId", sectionId);
                sectionName = filteredLineList.get(position).getSectionName();
                txtSelectLine.setText(sectionName);
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

    private void userLogin() {
        btnLogin.setOnClickListener(v -> {
            String sectionName = txtSelectLine.getText().toString();
            String userId = user_id.getText().toString();
            SharedPref.write("UserID", userId);
            String userPassword = user_password.getText().toString();
            SharedPref.write("Password", userPassword);
            if (sectionName.isEmpty()) {
                txtSelectLine.requestFocus();
                AlertCustomAlertDialog("Please select section.");
            } else if (userId.isEmpty()) {
                user_id.requestFocus();
                user_id.setError("Enter valid user id");
            } else if (userPassword.isEmpty()) {
                user_password.requestFocus();
                user_password.setError("Enter valid password");
            } else {
                PostLogin postLogin = new PostLogin();
                postLogin.setUserName(userId);
                postLogin.setPassword(userPassword);
                postLogin.setAppId("30020110001");
                postLogin.setAppVer("2.2.0");
                Log.i("info", "postLogin: " + postLogin);
                spotsDialog.show();
                retrofitApiInterface.getToken(postLogin).enqueue(new Callback<GetLogin>() {
                    @Override
                    public void onResponse(Call<GetLogin> call, Response<GetLogin> response) {
                        try {
                            if (response.body().getToken() != null) {
                                spotsDialog.dismiss();
                                String token = response.body().getToken();
                                Log.i("info", "token: " + token);
                                SharedPref.write("UserToken", token);
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                                        getApplicationContext(),
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                );
                                startActivity(i, options.toBundle());
                            } else if (!response.isSuccessful()) {
                                spotsDialog.dismiss();
                                if (response.errorBody() != null) {
                                    Gson gson = new GsonBuilder().create();
                                    try {
                                        String mError = gson.toJson(response.errorBody().string());
                                        mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                        mError = mError.replace("\\r\\nত্রুটি :", "");
                                        ErrorCustomAlertDialog(mError);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    user_id.setText("");
                                    user_password.setText("");
                                    user_id.requestFocus();
                                }
                            }
                        } catch (NullPointerException e) {
                            spotsDialog.dismiss();
                            AlertCustomAlertDialog("Invalid Credentials");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetLogin> call, Throwable t) {
                        spotsDialog.dismiss();
                        AlertCustomAlertDialog("Connection was closed. Please try again." + t.getMessage());
                        Log.i("info", "Failed: " + t);
                    }
                });
            }
        });
    }

    private void AlertCustomAlertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        //builder.setIcon(this.getDrawable(R.drawable.alert_information_64));
        builder.setTitle("Warning.. !!!");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", (dialog, id) -> {
            dialog.cancel();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void ErrorCustomAlertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        //builder.setIcon(this.getDrawable(R.drawable.error_read_64));
        builder.setTitle("Error.. !!!");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", (dialog, id) -> {
            dialog.cancel();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}