package com.example.mms_scanner.ui;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mms_scanner.R;
import com.example.mms_scanner.databinding.FragmentMachineMovementBinding;
import com.example.mms_scanner.model.line.GetLine;
import com.example.mms_scanner.model.machine.MachineInfo;
import com.example.mms_scanner.model.machine.MachineMvtDto;
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
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Machine_movement_Fragment extends Fragment {

    static AlertDialog.Builder alertbox;
    static AlertDialog alertDialog;
    private FragmentMachineMovementBinding binding;
    private ApiInterface retrofitApiInterface;
    FragmentManager fragmentManager;
    SearchView search_view, searchView;
    SpotsDialog spotsDialog;
    MachineInfoByCodeAdapter machineInfoByCodeAdapter;
    RecyclerView recyclerView, line_list_dialog_layout_RecyclerView;
    ImageButton backButton;
    TextInputEditText txtLineCode, txtLineName, txtMachineCode;
    Button GoBack, btnSave, btnClear, btnOk, btnScanLine, btnMachineScan;
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    ImageView txtLineScanner, txtMachineCodeScanner;
    ImageButton btnLines;

    private LinearLayout layout;
    String uToken, lineId, lineName, barcodeData, machineCode, IPAdd, DeviceName;
    TextView txtMessage, txtCount;
    Dialog dialogCustom, dialog;

    List<GetLine> getLines;
    List<MachineInfo> getMachineInfo;
    List<MachineMvtDto> postMachineInfo;

    LinesDialogAdapter linesDialogAdapter;

    boolean data = true;
    boolean notFound = true;
    boolean isScanData = true;
    int selectedItemPosition = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_machine_movement_, container, false);

        fragmentManager = getFragmentManager();

        retrofitApiInterface = Client.getRetrofit().create(ApiInterface.class);
        dialogCustom = new Dialog(getActivity());
        dialogCustom.setCancelable(false);

        dialog = new Dialog(getActivity());

        spotsDialog = new SpotsDialog(getContext(), R.style.Custom);
        alertbox = new AlertDialog.Builder(getContext());

        txtLineName = root.findViewById(R.id.txtLineName);
        txtLineName.setEnabled(false);
        txtLineCode = root.findViewById(R.id.txtLineCode);
        txtMachineCode = root.findViewById(R.id.txtMachineCode);

        txtLineScanner = root.findViewById(R.id.txtLineScanner);
        txtMachineCodeScanner = root.findViewById(R.id.txtMachineCodeScanner);

        txtCount = root.findViewById(R.id.txtCount);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = root.findViewById(R.id.surface_view);

        backButton = root.findViewById(R.id.backButton);
        layout = root.findViewById(R.id.layout);

        btnScanLine = root.findViewById(R.id.btnScanLine);
        btnMachineScan = root.findViewById(R.id.btnMachineScan);
        btnLines = root.findViewById(R.id.btnLines);
        btnClear = root.findViewById(R.id.btnClear);
        btnSave = root.findViewById(R.id.btnSave);

        recyclerView = root.findViewById(R.id.machine_movement_recycaler);

        backButton = root.findViewById(R.id.backButton);
        search_view = root.findViewById(R.id.search_view);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().add(R.id.container, homeFragment, homeFragment.getTag()).commit();
        });

        uToken = SharedPref.read("UserToken", "");

        GoBack = root.findViewById(R.id.GoBack);
        GoBack.setOnClickListener(v -> {
            layout.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
            GoBack.setVisibility(View.GONE);
        });

        try {
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            DeviceName = myDevice.getName();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Context context = requireContext().getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        IPAdd = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        getLines = new ArrayList<GetLine>();
        getMachineInfo = new ArrayList<MachineInfo>();
        postMachineInfo = new ArrayList<MachineMvtDto>();

        txtMachineCode.setEnabled(false);

        GetLine();
        GetLineList();

        ClearActivity();
        DbOperation();

        return root;
    }

    private void GetLine() {
        txtLineCode.requestFocus();
        txtLineScanner.setOnClickListener(v -> {
            lineInitialiseDetectorsAndSources();
            layout.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
            GoBack.setVisibility(View.VISIBLE);
        });
        txtLineCode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        lineId = txtLineCode.getText().toString();
                        if (!lineId.isEmpty()) {
                            try {
                                Call<GetLine> dataCall = retrofitApiInterface.getLineMasterData("Bearer" + " " + uToken, lineId);
                                dataCall.enqueue(new Callback<GetLine>() {
                                    @Override
                                    public void onResponse(Call<GetLine> call, Response<GetLine> response) {
                                        try {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    lineName = response.body().getLineName();
                                                    lineId = response.body().getLineId();
                                                    txtLineName.setText(lineName);
                                                    GetMachineDataByMachineCode();
                                                    txtMachineCode.setEnabled(true);
                                                    txtLineCode.setEnabled(false);
                                                    txtMachineCode.requestFocus();
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

                                                    txtLineCode.setText("");
                                                    txtLineName.setText("");
                                                    txtLineName.requestFocus();
                                                }
                                            }
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<GetLine> call, Throwable t) {
                                        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                        btnOk = view.findViewById(R.id.btnOk);
                                        dialogCustom.setContentView(view);
                                        dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        txtMessage = view.findViewById(R.id.txtMessage);
                                        txtMessage.setText("Connection was closed. Please try again.");
                                        dialogCustom.show();
                                        btnOk.setOnClickListener(v1 -> {
                                            dialogCustom.dismiss();
                                        });

                                        txtLineCode.setText("");
                                        txtLineName.setText("");
                                        txtLineCode.requestFocus();
                                    }
                                });
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        btnScanLine.setOnClickListener(v -> {
            lineId = txtLineCode.getText().toString();
            if (lineId.isEmpty()) {
                txtLineCode.setError("লাইন কোড নম্বর প্রবেশ করান।");
            } else {
                if (!lineId.isEmpty()) {
                    try {
                        Call<GetLine> dataCall = retrofitApiInterface.getLineMasterData("Bearer" + " " + uToken, lineId);
                        dataCall.enqueue(new Callback<GetLine>() {
                            @Override
                            public void onResponse(Call<GetLine> call, Response<GetLine> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            spotsDialog.dismiss();
                                            lineName = response.body().getLineName();
                                            lineId = response.body().getLineId();
                                            GetMachineDataByMachineCode();
                                            txtLineCode.setEnabled(false);
                                            txtMachineCode.setEnabled(true);
                                            txtMachineCode.requestFocus();
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

                                            txtLineCode.setText("");
                                            txtLineName.setText("");
                                            txtLineCode.requestFocus();
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<GetLine> call, Throwable t) {
                                View view = getActivity().getLayoutInflater().inflate(R.layout.custom_error, null);
                                btnOk = view.findViewById(R.id.btnOk);
                                dialogCustom.setContentView(view);
                                dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                txtMessage = view.findViewById(R.id.txtMessage);
                                txtMessage.setText("Connection was closed. Please try again.");
                                dialogCustom.show();
                                btnOk.setOnClickListener(v1 -> {
                                    dialogCustom.dismiss();
                                });

                                txtLineCode.setText("");
                                txtLineName.setText("");
                                txtLineCode.requestFocus();
                            }
                        });
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void GetLineList() {
        btnLines.setOnClickListener(v -> {
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
                                    View view1 = getActivity().getLayoutInflater().inflate(R.layout.line_list, null);
                                    dialog.setContentView(view1);
                                    line_list_dialog_layout_RecyclerView = view1.findViewById(R.id.line_list_dialog_layout_RecyclerView);
                                    searchView = view1.findViewById(R.id.searchView);
                                    linesDialogAdapter = new LinesDialogAdapter(getContext(), getLines);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    line_list_dialog_layout_RecyclerView.setHasFixedSize(true);
                                    line_list_dialog_layout_RecyclerView.setLayoutManager(mLayoutManager);
                                    line_list_dialog_layout_RecyclerView.setAdapter(linesDialogAdapter);
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
                                    showAlertDialog("Error: ", "Line List is Empty.", getContext(), false);
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

    private void GetMachineDataByMachineCode() {
        txtMachineCodeScanner.setOnClickListener(v -> {
            machineCodeInitialiseDetectorsAndSources();
            layout.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
            GoBack.setVisibility(View.VISIBLE);
        });
        txtMachineCode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        machineCode = txtMachineCode.getText().toString();
                        if (machineCode.isEmpty()) {
                            txtMachineCode.setError("enter a valid QR code or machine code.");
                        } else {
                            try {
                                Call<MachineInfo> getMachineInfoByLine = retrofitApiInterface.getMachineInfo("Bearer" + " " + uToken, txtMachineCode.getText().toString());
                                getMachineInfoByLine.enqueue(new Callback<MachineInfo>() {
                                    @Override
                                    public void onResponse(Call<MachineInfo> call, Response<MachineInfo> response) {
                                        if (response.isSuccessful()) {
                                            if (response.body() != null) {
                                                for (int i = 0; i < postMachineInfo.size(); i++) {
                                                    if (machineCode.equals(postMachineInfo.get(i).getMachineCode())) {
                                                        isScanData = false;
                                                    }
                                                }
                                                if (isScanData == true) {
                                                    MachineMvtDto postMachine = new MachineMvtDto();
                                                    postMachine.setMachineCode(txtMachineCode.getText().toString());
                                                    postMachine.setFromBlockName(response.body().getBlockName());
                                                    postMachine.setFromLineName(response.body().getLineName());
                                                    postMachine.setToLineName(txtLineName.getText().toString());
                                                    postMachine.setDeviceName(DeviceName);
                                                    postMachine.setIPAdd(IPAdd);
                                                    postMachineInfo.add(postMachine);
                                                    machineInfoByCodeAdapter = new MachineInfoByCodeAdapter(getContext(), postMachineInfo);
                                                    recyclerView.setAdapter(machineInfoByCodeAdapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                    txtCount.setText(String.valueOf("Total :" + postMachineInfo.size()));
                                                    txtMachineCode.setText("");
                                                    txtMachineCode.requestFocus();

                                                    search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                        @Override
                                                        public boolean onQueryTextSubmit(String queryString) {
                                                            machineInfoByCodeAdapter.getFilter().filter(queryString);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onQueryTextChange(String queryString) {
                                                            machineInfoByCodeAdapter.getFilter().filter(queryString);
                                                            return false;
                                                        }
                                                    });
                                                } else if (isScanData == false) {
                                                    ErrorCustomAlertDialog("Machine Code : " + machineCode + " already scanned.");
                                                    isScanData = true;
                                                    txtMachineCode.setText("");
                                                    txtMachineCode.requestFocus();
                                                }
                                            }

                                        } else if (!response.isSuccessful()) {
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

                                    @Override
                                    public void onFailure(Call<MachineInfo> call, Throwable t) {
                                    }
                                });


                            } catch (NullPointerException t) {
                                t.printStackTrace();
                            }
                        }
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        btnMachineScan.setOnClickListener(v -> {
            machineCode = txtMachineCode.getText().toString();
            if (machineCode.isEmpty()) {
                txtMachineCode.setError("enter a valid QR code or machine code.");
            } else {
                try {
                    Call<MachineInfo> getMachineInfoByLine = retrofitApiInterface.getMachineInfo("Bearer" + " " + uToken, txtMachineCode.getText().toString());
                    getMachineInfoByLine.enqueue(new Callback<MachineInfo>() {
                        @Override
                        public void onResponse(Call<MachineInfo> call, Response<MachineInfo> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    for (int i = 0; i < postMachineInfo.size(); i++) {
                                        if (machineCode.equals(postMachineInfo.get(i).getMachineCode())) {
                                            isScanData = false;
                                        }
                                    }
                                    if (isScanData == true) {
                                        MachineMvtDto postMachine = new MachineMvtDto();
                                        postMachine.setMachineCode(txtMachineCode.getText().toString());
                                        postMachine.setFromBlockName(response.body().getBlockName());
                                        postMachine.setFromLineName(response.body().getLineName());
                                        postMachine.setToLineName(txtLineName.getText().toString());
                                        postMachine.setDeviceName(DeviceName);
                                        postMachine.setIPAdd(IPAdd);
                                        postMachineInfo.add(postMachine);
                                        machineInfoByCodeAdapter = new MachineInfoByCodeAdapter(getContext(), postMachineInfo);
                                        recyclerView.setAdapter(machineInfoByCodeAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        txtCount.setText(String.valueOf("Total :" + postMachineInfo.size()));
                                        txtMachineCode.setText("");
                                        txtMachineCode.requestFocus();

                                        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                            @Override
                                            public boolean onQueryTextSubmit(String queryString) {
                                                machineInfoByCodeAdapter.getFilter().filter(queryString);
                                                return false;
                                            }

                                            @Override
                                            public boolean onQueryTextChange(String queryString) {
                                                machineInfoByCodeAdapter.getFilter().filter(queryString);
                                                return false;
                                            }
                                        });

                                    } else if (isScanData == false) {
                                        ErrorCustomAlertDialog("Machine Code : " + machineCode + " already scanned.");
                                        isScanData = true;
                                        txtMachineCode.setText("");
                                        txtMachineCode.requestFocus();
                                    }
                                }

                            } else if (!response.isSuccessful()) {
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

                        @Override
                        public void onFailure(Call<MachineInfo> call, Throwable t) {
                        }
                    });


                } catch (NullPointerException t) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void ScanMachine() {
        txtMachineCode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        machineCode = txtMachineCode.getText().toString();
                        if (machineCode.isEmpty()) {
                            ErrorCustomAlertDialog("Scan Sku or Rolls Qr Code");
                        } else {
                            try {
                                Log.i("Info", "getRollIdByASN: " + getMachineInfo);
                                if (getMachineInfo.size() > 0) {
                                    for (int i = 0; i < getMachineInfo.size(); i++) {
                                        if (machineCode.equals(getMachineInfo.get(i).getMachineCode())) {
                                            notFound = true;
                                            break;
                                        } else {
                                            notFound = false;
                                        }
                                    }
                                    if (notFound == true) {
                                        for (MachineInfo items : getMachineInfo) {

                                            if (items.getMachineCode().equals(machineCode)) {


                                                for (int i = 0; i < postMachineInfo.size(); i++) {
                                                    if (machineCode.equals(postMachineInfo.get(i).getMachineCode())) {
                                                        data = false;
                                                    }
                                                }
                                                if (data == true) {
                                                    MachineMvtDto postMachine = new MachineMvtDto();
                                                    postMachine.setMachineCode(txtMachineCode.getText().toString());
                                                    postMachine.setFromBlockName(items.getBlockName());
                                                    postMachine.setFromLineName(items.getLineName());
                                                    postMachine.setToLineName(txtLineName.getText().toString());
                                                    postMachine.setDeviceName(DeviceName);
                                                    postMachine.setIPAdd(IPAdd);
                                                    postMachineInfo.add(postMachine);

                                                } else if (data == false) {
                                                    ErrorCustomAlertDialog("Machine Code : " + machineCode + " already scanned.");
                                                    data = true;
                                                    txtMachineCode.setText("");
                                                    txtMachineCode.requestFocus();
                                                }
                                            }
                                        }
                                    } else if (notFound == false) {
                                        ErrorCustomAlertDialog("ID Number : " + machineCode + " not found.");
                                        notFound = true;
                                        txtMachineCode.setText("");
                                        txtMachineCode.requestFocus();
                                    }
                                }

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        btnMachineScan.setOnClickListener(v -> {
            machineCode = txtMachineCode.getText().toString();
            if (machineCode.isEmpty()) {
                txtMachineCode.setError("enter a valid QR code or Bar code.");
            } else {
                try {
                    if (getMachineInfo.size() > 0) {
                        for (int i = 0; i < getMachineInfo.size(); i++) {
                            if (machineCode.equals(getMachineInfo.get(i).getMachineCode())) {
                                notFound = true;
                                break;
                            } else {
                                notFound = false;
                            }
                        }
                        if (notFound == true) {
                            for (MachineInfo items : getMachineInfo) {

                                if (items.getMachineCode().equals(machineCode)) {


                                    for (int i = 0; i < postMachineInfo.size(); i++) {
                                        if (machineCode.equals(postMachineInfo.get(i).getMachineCode())) {
                                            data = false;
                                        }
                                    }
                                    if (data == true) {
                                        MachineMvtDto postMachine = new MachineMvtDto();
                                        postMachine.setMachineCode(txtMachineCode.getText().toString());
                                        postMachine.setFromBlockName(items.getBlockName());
                                        postMachine.setFromLineName(items.getLineName());
                                        postMachine.setToLineName(txtLineName.getText().toString());
                                        postMachine.setDeviceName(DeviceName);
                                        postMachine.setIPAdd(IPAdd);
                                        postMachineInfo.add(postMachine);

                                    } else if (data == false) {
                                        ErrorCustomAlertDialog("Machine Code : " + machineCode + " already scanned.");
                                        data = true;
                                        txtMachineCode.setText("");
                                        txtMachineCode.requestFocus();
                                    }
                                }
                            }
                        } else if (notFound == false) {
                            ErrorCustomAlertDialog("ID Number : " + machineCode + " not found.");
                            notFound = true;
                            txtMachineCode.setText("");
                            txtMachineCode.requestFocus();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void DbOperation() {
        btnSave.setOnClickListener(v -> {
            Call<String> getObjectCall = retrofitApiInterface.getViewer("Bearer" + " " + uToken, "302201100096", "Execute");
            getObjectCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.i("info", "permission: " + response.body());
                                spotsDialog.dismiss();
                                if (postMachineInfo.size() > 0) {
                                    Call<String> objectsCall = retrofitApiInterface.machineMvtDtoSave("Bearer" + " " + uToken, postMachineInfo);
                                    objectsCall.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    View view = getActivity().getLayoutInflater().inflate(R.layout.custom_message, null);
                                                    btnOk = view.findViewById(R.id.btnOk);
                                                    dialogCustom.setContentView(view);
                                                    dialogCustom.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                    txtMessage = view.findViewById(R.id.txtMessage);
                                                    txtMessage.setText(response.body());
                                                    dialogCustom.show();
                                                    btnOk.setOnClickListener(v1 -> {
                                                        dialogCustom.dismiss();
                                                    });

                                                    CLear();
                                                }
                                            } else if (!response.isSuccessful()) {
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

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            t.printStackTrace();
                                        }
                                    });

                                } else {
                                    ErrorCustomAlertDialog("Nothing to found for save");
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
                public void onFailure(Call<String> call, Throwable t) {
                    spotsDialog.dismiss();
                    showAlertDialog("Failed", "Connection was closed. Please try again.", getActivity(), false);
                }
            });

        });
    }

    private void CLear() {
        try {

            txtLineCode.setText("");
            txtLineName.setText("");
            recyclerView.setAdapter(null);
            postMachineInfo.clear();
            txtMachineCode.setEnabled(true);
            txtLineCode.setEnabled(true);
            txtLineCode.requestFocus();
            txtCount.setText(String.valueOf(0));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void ClearActivity() {
        btnClear.setOnClickListener(v -> {
            try {
                txtLineCode.setText("");
                txtLineName.setText("");
                recyclerView.setAdapter(null);
                postMachineInfo.clear();
                txtMachineCode.setEnabled(true);
                txtLineCode.setEnabled(true);
                txtLineCode.requestFocus();
                txtCount.setText(String.valueOf(0));

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    private void ErrorCustomAlertDialog(String msg) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setMessage(msg);
        builder.setIcon(R.drawable.error_read_64);
        builder.setTitle("Error.. !!!");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", (dialog, id) -> {
            dialog.cancel();
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
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
            holder.so_number_dialog_layout.setOnClickListener(v -> {
                String lineId = filteredLineList.get(position).getLineId();
                String lineName = filteredLineList.get(position).getLineName();
                txtLineCode.setText(lineId);
                txtLineName.setText(lineName);
                dialog.dismiss();
            });

            try {
                if (position % 2 == 1) {
                    holder.cardSection.setCardBackgroundColor(getResources().getColor(R.color.black));
                } else
                    holder.cardSection.setCardBackgroundColor(getResources().getColor(R.color.lihtgreenLine1));

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
            public LinearLayout so_number_dialog_layout;
            CardView cardSection;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                so_number_dialog_layout = itemView.findViewById(R.id.line_list_dialog_layout);
                cardSection = itemView.findViewById(R.id.cardSection);
                txtLineCode = itemView.findViewById(R.id.txtLinesCode);
                txtLinesName = itemView.findViewById(R.id.txtLinesName);
            }
        }

    }

    private void Delete(int position) {
        try {
            postMachineInfo.remove(position);
            machineInfoByCodeAdapter.notifyItemRemoved(position);
            machineInfoByCodeAdapter.notifyDataSetChanged();
            txtCount.setText(String.valueOf("Total :" + postMachineInfo.size()));
            txtMachineCode.requestFocus();
        } catch (IndexOutOfBoundsException exception) {
            exception.getLocalizedMessage();
        }
    }

    public class MachineInfoByCodeAdapter extends RecyclerView.Adapter<MachineInfoByCodeAdapter.ViewHolder> implements Filterable {

        Context context;
        List<MachineMvtDto> listMachineMvtDto;
        List<MachineMvtDto> filteredList;

        public MachineInfoByCodeAdapter(Context context, List<MachineMvtDto> listMachineMvtDto) {
            this.context = context;
            this.listMachineMvtDto = listMachineMvtDto;
            this.filteredList = listMachineMvtDto;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charSequenceString = constraint.toString();
                    if (charSequenceString.isEmpty()) {
                        filteredList = listMachineMvtDto;
                    } else {
                        List<MachineMvtDto> filterList = new ArrayList<>();
                        for (MachineMvtDto data : listMachineMvtDto) {

                            if (data.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
                                filterList.add(data);
                            }
                            filteredList = filterList;
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredList;
                    Log.i("info", "results: " + results.values);
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<MachineMvtDto>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.machine_movement_recycaler_view, parent, false);
            return new MachineInfoByCodeAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final MachineMvtDto machineMvtDto = filteredList.get(position);
            holder.txtMachineCode.setText(String.valueOf(" Machine Code :" + machineMvtDto.getMachineCode()));
            holder.txtFromLocation.setText(String.valueOf("From Location :" + machineMvtDto.getFromLineName()));
            holder.txtToLocation.setText(String.valueOf("To Location :" + machineMvtDto.getToLineName()));
        }

        @Override
        public int getItemCount() {
            return filteredList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtMachineCode, txtFromLocation, txtToLocation;
            public CardView cardDesign_rec;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardDesign_rec = itemView.findViewById(R.id.cardDesign_rec);
                txtMachineCode = itemView.findViewById(R.id.txtMachineCode);
                txtFromLocation = itemView.findViewById(R.id.txtFromLocation);
                txtToLocation = itemView.findViewById(R.id.txtToLocation);

                itemView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardDesign_rec.setBackgroundResource(R.color.colorPrimary);
                        alertbox.setMessage(String.valueOf("Do you want to delete selection item..? "))
                                .setCancelable(false)
                                .setIcon(R.drawable.alert_information_64)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    selectedItemPosition = getAdapterPosition();
                                    machineInfoByCodeAdapter.notifyDataSetChanged();
                                    Delete(selectedItemPosition);
                                    cardDesign_rec.setBackgroundResource(R.color.white);
                                })
                                .setNegativeButton("No", (dialog, id) -> {
                                    cardDesign_rec.setBackgroundResource(R.color.white);
                                    dialog.cancel();
                                });
                        AlertDialog alert = alertbox.create();
                        alert.show();
                    }
                });
            }
        }

    }

    private void lineInitialiseDetectorsAndSources() {

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
                    txtLineCode.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                txtLineCode.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                txtLineCode.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                GoBack.setVisibility(View.GONE);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                txtLineName.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                GoBack.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        });
    }

    private void machineCodeInitialiseDetectorsAndSources() {

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
                    txtMachineCode.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                txtMachineCode.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                txtMachineCode.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                GoBack.setVisibility(View.GONE);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                txtMachineCode.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                                layout.setVisibility(View.VISIBLE);
                                surfaceView.setVisibility(View.GONE);
                                GoBack.setVisibility(View.GONE);
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
        lineInitialiseDetectorsAndSources();
        machineCodeInitialiseDetectorsAndSources();
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