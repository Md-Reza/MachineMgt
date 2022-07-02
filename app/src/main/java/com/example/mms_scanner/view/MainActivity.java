package com.example.mms_scanner.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.retrofit.ApiInterface;
import com.example.mms_scanner.retrofit.Client;
import com.example.mms_scanner.ui.HomeFragment;
import com.example.mms_scanner.ui.MachineBreakDownQueue_Fragment;
import com.example.mms_scanner.ui.Pending_Accept_Fragment;
import com.example.mms_scanner.ui.status_report.McDashboardFragment;
import com.example.mms_scanner.utils.SharedPref;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ApiInterface retrofitApiInterface;
    SpotsDialog spotsDialog;
    Gson gson;

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;

    Dialog dialogCustom;

    String uToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uToken = SharedPref.read("UserToken", "");

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        spotsDialog = new SpotsDialog(this, R.style.Custom);
        SharedPref.init(getApplicationContext());
        gson = new Gson();

        dialogCustom = new Dialog(getApplicationContext());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.navigation_dashboard:
                dashboardViewPermission();
                return true;
            case R.id.navigation_notifications:
                notificationViewPermission();
                return true;
        }
        return false;
    }

    private void dashboardViewPermission() {
        spotsDialog.show();
        Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100097", "View");
        getObjectCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            spotsDialog.dismiss();
                            McDashboardFragment mcDashboardFragment = new McDashboardFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mcDashboardFragment).commit();
                        }
                    } else if (!response.isSuccessful()) {
                        if (response.errorBody() != null) {
                            spotsDialog.dismiss();
                            Gson gson = new GsonBuilder().create();
                            try {
                                String mError = gson.toJson(response.errorBody().string());
                                mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                mError = mError.replace("\\r\\nত্রুটি :", "");

                                View viewError = getLayoutInflater().inflate(R.layout.custom_error, null);
                                dialogCustom.setContentView(viewError);
                                TextView txtMessage = viewError.findViewById(R.id.txtMessage);
                                txtMessage.setText(mError);
                                Button btnOk = viewError.findViewById(R.id.btnOk);
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
                showAlertDialog("Failed", "Connection was closed. Please try again.", getApplicationContext(), false);
                Log.i("info", "Failed: " + t);
            }
        });
    }

    private void notificationViewPermission() {
        spotsDialog.show();
        Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100098", "View");
        getObjectCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            spotsDialog.dismiss();
                            MachineBreakDownQueue_Fragment receiveNotificationFragment = new MachineBreakDownQueue_Fragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, receiveNotificationFragment).commit();
                        }
                    } else if (!response.isSuccessful()) {
                        if (response.errorBody() != null) {
                            spotsDialog.dismiss();
                            Gson gson = new GsonBuilder().create();
                            try {
                                String mError = gson.toJson(response.errorBody().string());
                                mError = mError.replace("System can\\u0027t perform this operation. \\r\\nMessage :", "");
                                mError = mError.replace("\\r\\nত্রুটি :", "");

                                View viewError = getLayoutInflater().inflate(R.layout.custom_error, null);
                                dialogCustom.setContentView(viewError);
                                TextView txtMessage = viewError.findViewById(R.id.txtMessage);
                                txtMessage.setText(mError);
                                Button btnOk = viewError.findViewById(R.id.btnOk);
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
                showAlertDialog("Failed", "Connection was closed. Please try again.", getApplicationContext(), false);
                Log.i("info", "Failed: " + t);
            }
        });
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