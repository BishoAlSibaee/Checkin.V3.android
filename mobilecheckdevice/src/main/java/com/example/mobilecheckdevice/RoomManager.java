package com.example.mobilecheckdevice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.lock.ApiResponse;
import com.example.mobilecheckdevice.lock.ApiResult;
import com.example.mobilecheckdevice.lock.ApiService;
import com.example.mobilecheckdevice.lock.LockInitResultObj;
import com.example.mobilecheckdevice.lock.RetrofitAPIManager;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.InitLockCallback;
import com.ttlock.bl.sdk.callback.ScanLockCallback;
import com.ttlock.bl.sdk.callback.SetNBServerCallback;
import com.ttlock.bl.sdk.constant.FeatureValue;
import com.ttlock.bl.sdk.entity.LockError;
import com.ttlock.bl.sdk.util.FeatureValueUtil;
import com.tuya.smart.android.ble.api.LeScanSetting;
import com.tuya.smart.android.ble.api.ScanDeviceBean;
import com.tuya.smart.android.ble.api.ScanType;
import com.tuya.smart.android.ble.api.TyBleScanResponse;
import com.tuya.smart.android.hardware.bean.HgwBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.IGwSearchListener;
import com.tuya.smart.home.sdk.api.ITuyaDeviceActivator;
import com.tuya.smart.home.sdk.api.ITuyaGwSearcher;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IMultiModeActivatorListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.api.ITuyaGateway;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.bean.MultiModeActivatorBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class RoomManager extends AppCompatActivity
{
    public static ROOM Room ;
    private TextView foundLockNewName,foundLock,caption , lock,power,curtain,service,door,motion,switch1,switch2,switch3,switch4,switch5,switch6,switch7,switch8,ac,gateway,selectedWifi,foundWifiDevice,foundZbeeDevice,foundwireZbGateway,wirezbGatewayNewName;
    private Button getWifi ;
    private WifiManager wifiManager;
    private WifiReceiver receiverWifi;
    private static Activity act  ;
    private ListView wifiList;
    private Spinner DeviceTypes , DeviceTypesZ ;
    private EditText wifiPass ;
    private String Token , NewName ,NewNameZ;
    private DeviceBean FOUND ;
    private ITuyaDevice FOUNDD ;
    private ITuyaGateway FOUNDG ;
    protected static final int REQUEST_PERMISSION_REQ_CODE = 11;
    private ExtendedBluetoothDevice FOUNDLOCK ;
    ITuyaActivator mTuyaActivator ;
    ITuyaActivator mTuyaGWActivator ;
    ITuyaGwSearcher mTuyaGwSearcher ;
    ITuyaActivator mITuyaActivator ;
    RequestQueue REQ ;
    static List<SceneBean> SCENES,MY_SCENES,LivingMood,SleepMood,WorkMood,RomanceMood,ReadMood,MasterOffMood ;
    public static List<String> IMAGES ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_manager);
        act = this ;
        REQ = Volley.newRequestQueue(act);
        setActivity();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSceneBGs();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList,selectedWifi);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(act, "permission granted", Toast.LENGTH_SHORT).show();
                    //wifiManager.startScan();
                }
                else {
                    Toast.makeText(act, "permission not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getScanLockCallback();
                } else {
                    if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){

                    }
                }
                break;
            }
        }
    }

    private void getWifi() {
        WifiManager wifiManager = (WifiManager) act.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        act.registerReceiver(wifiScanReceiver, intentFilter);
        boolean success = wifiManager.startScan();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
//            }
//            else {
//                wifiManager.startScan();
//            }
//        }
//        else {
//            wifiManager.startScan();
//        }
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        ArrayList<String> deviceList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (ScanResult scanResult : results) {
            sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
            deviceList.add(scanResult.SSID);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(act, R.layout.spinners_item, deviceList.toArray());
        wifiList.setAdapter(arrayAdapter);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedWifi.setText(deviceList.get(position));
            }
        });
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
    }

    private void setActivity() {
        IMAGES = new ArrayList<>();
        SCENES = new ArrayList<>();
        MY_SCENES = new ArrayList<>();
        LivingMood = new ArrayList<>();
        SleepMood = new ArrayList<>();
        WorkMood = new ArrayList<>();
        RomanceMood = new ArrayList<>();
        ReadMood = new ArrayList<>();
        MasterOffMood = new ArrayList<>();
        int ID = getIntent().getExtras().getInt("RoomId");
        for (int i = 0; i< Rooms.ROOMS.size(); i++) {
            if (Rooms.ROOMS.get(i).id == ID) {
                Room = Rooms.ROOMS.get(i) ;
            }
        }
        caption = findViewById(R.id.roomManager_caption);
        lock = findViewById(R.id.room_Lock);
        power = findViewById(R.id.room_Power);
        power.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (Room.getPOWER() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Power Switch");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Power ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        curtain = findViewById(R.id.room_Curtain);
        curtain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getCURTAIN() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Curtain ");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Curtain ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getCURTAIN().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        service = findViewById(R.id.room_Service);
        service.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSERVICE1() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Service Switch");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Service Switch ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSERVICE1().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        door = findViewById(R.id.room_Doorsensor);
        door.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getDOORSENSOR() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Door Sensor");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Door Sensor ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getDOORSENSOR().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }


                return false;
            }
        });
        motion = findViewById(R.id.room_Motion);
        motion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getMOTIONSENSOR() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Motion Sensor");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Motion Sensor ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getMOTIONSENSOR().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }

                return false;
            }
        });
        switch1 = findViewById(R.id.room_Switch1);
        switch1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH1() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 1");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 1 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH1().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch2 = findViewById(R.id.room_Switch2);
        switch2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH2() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 2");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 2 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH2().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch3 = findViewById(R.id.room_Switch3);
        switch3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH3() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 3");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 3 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH3().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch4 = findViewById(R.id.room_Switch4);
        switch4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH4() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 4");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 4 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH4().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch5 = findViewById(R.id.room_Switch5);
        switch5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH5() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 5");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 5 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH5().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch6 = findViewById(R.id.room_Switch6);
        switch6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH6() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 6");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 6 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH6().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch7 = findViewById(R.id.room_Switch7);
        switch7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH7() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 7");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 7 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH7().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        switch8 = findViewById(R.id.room_Switch8);
        switch8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getSWITCH8() == null ) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 8");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 8 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getSWITCH8().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        selectedWifi = findViewById(R.id.selected_wifi);
        foundWifiDevice = findViewById(R.id.theFoundDevice);
        foundZbeeDevice = findViewById(R.id.theFoundDeviceZbee);
        foundwireZbGateway = findViewById(R.id.wire_zbgate_found);
        wirezbGatewayNewName = findViewById(R.id.wire_zbgateway_newstaticName);
        foundLock = findViewById(R.id.foundlock);
        foundLockNewName = findViewById(R.id.foundLockNewName);
        ac = findViewById(R.id.room_AC);
        ac.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getAC() == null) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No AC Controller");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("Delete Ac Controller .. ?");
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getAC().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });
        wifiPass = findViewById(R.id.wifi_pass);
        getWifi = findViewById(R.id.room_addWifi);
        getWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } else {
                    wifiManager.startScan();
                }
            }

        });
        wifiList = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(MyApp.app.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(act, "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        gateway = findViewById(R.id.room_Gateway);
        gateway.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Room.getGATEWAY() == null) {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No ZBGateway");
                    d.create().show();
                }
                else {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("Delete ZBGateway .. ?");
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Room.getGATEWAY().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });
        DeviceTypes = findViewById(R.id.deviceNames_spinner);
        DeviceTypesZ = findViewById(R.id.deviceNames_spinnerZbee);
        String [] Types = new String[]{Room.RoomNumber+"Power",Room.RoomNumber+"ZGatway",Room.RoomNumber+"AC",Room.RoomNumber+"DoorSensor",Room.RoomNumber+"MotionSensor",Room.RoomNumber+"Curtain",Room.RoomNumber+"ServiceSwitch",Room.RoomNumber+"Switch1",Room.RoomNumber+"Switch2",Room.RoomNumber+"Switch3",Room.RoomNumber+"Switch4",Room.RoomNumber+"Switch5",Room.RoomNumber+"Switch6",Room.RoomNumber+"Switch7",Room.RoomNumber+"Switch8",Room.RoomNumber+"IR",Room.RoomNumber+"Lock"};
        ArrayAdapter<String> x =  new ArrayAdapter<>(act ,R.layout.spinners_item ,Types);
        ArrayAdapter<String> y =  new ArrayAdapter<>(act ,R.layout.spinners_item ,Types);
        DeviceTypesZ.setAdapter(y);
        DeviceTypes.setAdapter(x);
        caption.setText("Manage Room : "+ Room.RoomNumber);
        if (Room.lock == 0 ) {
            lock.setText("NO");
            lock.setTextColor(Color.RED);
        }
        else {
            lock.setText("YES");
            lock.setTextColor(Color.GREEN);
        }
        if (Room.Switch1 == 0) {
            switch1.setText("NO");
            switch1.setTextColor(Color.RED);
        }
        else {
            switch1.setText("YES");
            switch1.setTextColor(Color.GREEN);
        }
        if (Room.Switch2 == 0) {
            switch2.setText("NO");
            switch2.setTextColor(Color.RED);
        }
        else {
            switch2.setText("YES");
            switch2.setTextColor(Color.GREEN);
        }
        if (Room.Switch3 == 0) {
            switch3.setText("NO");
            switch3.setTextColor(Color.RED);
        }
        else {
            switch3.setText("YES");
            switch3.setTextColor(Color.GREEN);
        }
        if (Room.Switch4 == 0) {
            switch4.setText("NO");
            switch4.setTextColor(Color.RED);
        }
        else {
            switch4.setText("YES");
            switch4.setTextColor(Color.GREEN);
        }
        if (Room.Switch5 == 0) {
            switch5.setText("NO");
            switch5.setTextColor(Color.RED);
        }
        else {
            switch5.setText("YES");
            switch5.setTextColor(Color.GREEN);
        }
        if (Room.Switch6 == 0) {
            switch6.setText("NO");
            switch6.setTextColor(Color.RED);
        }
        else {
            switch6.setText("YES");
            switch6.setTextColor(Color.GREEN);
        }
        if (Room.Switch7 == 0) {
            switch7.setText("NO");
            switch7.setTextColor(Color.RED);
        }
        else {
            switch7.setText("YES");
            switch7.setTextColor(Color.GREEN);
        }
        if (Room.Switch8 == 0) {
            switch8.setText("NO");
            switch8.setTextColor(Color.RED);
        }
        else {
            switch8.setText("YES");
            switch8.setTextColor(Color.GREEN);
        }
        if (Room.CurtainSwitch == 0) {
            curtain.setText("NO");
            curtain.setTextColor(Color.RED);
        }
        else {
            curtain.setText("YES");
            curtain.setTextColor(Color.GREEN);
        }
        if (Room.MotionSensor == 0) {
            motion.setText("NO");
            motion.setTextColor(Color.RED);
        }
        else {
            motion.setText("YES");
            motion.setTextColor(Color.GREEN);
        }
        if (Room.DoorSensor == 0) {
            door.setText("NO");
            door.setTextColor(Color.RED);
        }
        else {
            door.setText("YES");
            door.setTextColor(Color.GREEN);
        }
        if (Room.PowerSwitch == 0) {
            power.setText("NO");
            power.setTextColor(Color.RED);
        }
        else {
            power.setText("YES");
            power.setTextColor(Color.GREEN);
        }
        if (Room.ServiceSwitch == 0) {
            service.setText("NO");
            service.setTextColor(Color.RED);
        }
        else {
            service.setText("YES");
            service.setTextColor(Color.GREEN);
        }
        if (Room.Thermostat == 0) {
            ac.setText("NO");
            ac.setTextColor(Color.RED);
        }
        else {
            ac.setText("YES");
            ac.setTextColor(Color.GREEN);
        }
        if ( Room.getGATEWAY_B() != null ) {
            gateway.setText("YES");
            gateway.setTextColor(Color.GREEN);
        }
        else {
            gateway.setText("NO");
            gateway.setTextColor(Color.RED);
        }
    }

    // for Wifi Devices ___________________________________
    public void searchWifiDevice(View view) {
        if (selectedWifi.getText() == null || selectedWifi.getText().toString().isEmpty()) {
            new MessageDialog("please select wifi network or write wifi name","wifi name ?",act);
            return;
        }
        if (wifiPass.getText() == null || wifiPass.getText().toString().isEmpty()) {
            new MessageDialog("please enter wifi password","wifi password ?",act);
            return;
        }
        ScanningDialog d = new ScanningDialog(act,"Scanning Wifi Device");
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(Login.THEHOME.getHomeId(),
                new ITuyaActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d("scanWifiDevice" , "token "+token+" "+selectedWifi.getText().toString()+" "+wifiPass.getText().toString());
                        Token = token ;
                        ActivatorBuilder builder = new ActivatorBuilder()
                                .setSsid(selectedWifi.getText().toString())
                                .setContext(act)
                                .setPassword(wifiPass.getText().toString())
                                .setActivatorModel(ActivatorModelEnum.TY_EZ)
                                .setTimeOut(100)
                                .setToken(Token)
                                .setListener(new ITuyaSmartActivatorListener() {

                                                 @Override
                                                 public void onError(String errorCode, String errorMsg) {
                                                     d.close();
                                                     mTuyaActivator.stop();
                                                     new MessageDialog(errorMsg+" "+errorCode,"Failed",act);
                                                 }
                                                 @Override
                                                 public void onActiveSuccess(DeviceBean devResp) {
                                                     Log.d("scanWifiDevice" , "success ");
                                                     d.close();
                                                     Rooms.CHANGE_STATUS = true ;
                                                     foundWifiDevice.setText(devResp.getName());
                                                     FOUND = devResp ;
                                                     FOUNDD = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
                                                     new MessageDialog(devResp.getName(),"Success",act);
                                                     mTuyaActivator.stop();
                                                 }
                                                 @Override
                                                 public void onStep(String step, Object data) {
                                                     d.close();
                                                     Rooms.CHANGE_STATUS = true ;
                                                     mTuyaActivator.stop();
                                                     Rooms.refreshSystem();
                                                     new MessageDialog("device found \n check devices list ","Success",act);
                                                 }
                                             }
                                );
                        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder);
                        mTuyaActivator.start();
                        d.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                mTuyaActivator.stop();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        mTuyaActivator.stop();
                        new MessageDialog(s1+" "+s,"Failed",act);
                    }
                });
    }

    public void renameDevice(View view) {
        if (!DeviceTypes.getSelectedItem().toString().isEmpty()) {
            lodingDialog d = new lodingDialog(act);
            if (FOUND != null ) {
                TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()).renameDevice(DeviceTypes.getSelectedItem().toString(), new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        d.stop();
                        new MessageDialog(error+" "+code,"Failed",act);
                    }

                    @Override
                    public void onSuccess() {
                        d.stop();
                        Toast.makeText(act, "Name Changed Successfully", Toast.LENGTH_LONG).show();
                        NewName = DeviceTypes.getSelectedItem().toString();
                        foundWifiDevice.setText(NewName);
                        foundWifiDevice.setTextColor(Color.GREEN);
                    }
                });
            }
            else {
                d.stop();
                Toast.makeText( act,"Device is null " , Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText( act,"Write New Device Name " , Toast.LENGTH_LONG).show();
        }
    }

    public void saveDevice(View view) {
        if (NewName == null) {
            Toast.makeText(act,"Rename Device First " , Toast.LENGTH_LONG).show();
        }
        else if (NewName.equals(Room.RoomNumber+"Power")) {
            Room.setPOWER_B(FOUND);
            Room.setPOWER(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            power.setText("YES");
            power.setTextColor(Color.GREEN);
            Room.setPowerSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"ZGatway")) {
            Room.setGATEWAY_B(FOUND);
            Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            gateway.setText("YES");
            gateway.setTextColor(Color.GREEN);
            Room.setZBGatewayStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"AC")) {
            Room.setAC_B(FOUND);
            Room.setAC(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            ac.setText("YES");
            ac.setTextColor(Color.GREEN);
            Room.setThermostatStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"DoorSensor")) {
            Room.setDOORSENSOR_B(FOUND);
            Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            door.setText("YES");
            door.setTextColor(Color.GREEN);
            Room.setDoorSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"MotionSensor")) {
            Room.setMOTIONSENSOR_B(FOUND);
            Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            motion.setText("YES");
            motion.setTextColor(Color.GREEN);
            Room.setMotionSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Curtain")) {
            Room.setCURTAIN_B(FOUND);
            Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            curtain.setText("YES");
            curtain.setTextColor(Color.GREEN);
            Room.setCurtainSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"ServiceSwitch")) {
            Room.setSERVICE1_B(FOUND);
            Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            service.setText("YES");
            service.setTextColor(Color.GREEN);
            Room.setServiceSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch1")) {
            Room.setSWITCH1_B(FOUND);
            Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch1.setText("YES");
            switch1.setTextColor(Color.GREEN);
            Room.setSwitch1Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch2")) {
            Room.setSWITCH2_B(FOUND);
            Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch2.setText("YES");
            switch2.setTextColor(Color.GREEN);
            Room.setSwitch2Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch3")) {
            Room.setSWITCH3_B(FOUND);
            Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch3.setText("YES");
            switch3.setTextColor(Color.GREEN);
            Room.setSwitch3Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch4")) {
            Room.setSWITCH4_B(FOUND);
            Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch4.setText("YES");
            switch4.setTextColor(Color.GREEN);
            Room.setSwitch4Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch5")) {
            Room.setSWITCH5_B(FOUND);
            Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch5.setText("YES");
            switch5.setTextColor(Color.GREEN);
            Room.setSwitch5Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch6")) {
            Room.setSWITCH6_B(FOUND);
            Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch6.setText("YES");
            switch6.setTextColor(Color.GREEN);
            Room.setSwitch6Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch7")) {
            Room.setSWITCH7_B(FOUND);
            Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch7.setText("YES");
            switch7.setTextColor(Color.GREEN);
            Room.setSwitch7Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch8")) {
            Room.setSWITCH8_B(FOUND);
            Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch8.setText("YES");
            switch8.setTextColor(Color.GREEN);
            Room.setSwitch8Status(String.valueOf(Room.id),"1",act);
        }
        else {
            Toast.makeText(act,"Device Not Detected" , Toast.LENGTH_LONG).show();
        }
        Rooms.refreshSystem();
    }
    //________________________________________________________

    // for Zigbee _______________________________
    public void searchZbeeDevice(View view) {
        if (Room.getGATEWAY_B() != null ) {
            ScanningDialog d = new ScanningDialog(act,"Scanning ZIGBEE Device");
            TuyaGwSubDevActivatorBuilder builder = new TuyaGwSubDevActivatorBuilder()
                    .setDevId(Room.getGATEWAY_B().devId)
                    .setTimeOut(90)
                    .setListener(new ITuyaSmartActivatorListener() {

                                     @Override
                                     public void onError(String errorCode, String errorMsg) {
                                         d.close();
                                         mTuyaGWActivator.stop();
                                         new MessageDialog(errorMsg+" "+errorCode,"Failed",act);
                                     }
                                     @Override
                                     public void onActiveSuccess(DeviceBean devResp) {
                                         d.close();
                                         Rooms.CHANGE_STATUS = true ;
                                         FOUND = devResp ;
                                         FOUNDD = TuyaHomeSdk.newDeviceInstance(devResp.getDevId());
                                         new MessageDialog(devResp.getName(),"Found",act);
                                         foundZbeeDevice.setText(devResp.getName());
                                         mTuyaGWActivator.stop();
                                     }
                                     @Override
                                     public void onStep(String step, Object data) {
                                         d.close();
                                         Rooms.CHANGE_STATUS = true ;
                                         mTuyaGWActivator.stop();
                                         Rooms.refreshSystem();
                                         new MessageDialog("device found \n check devices list ","Found",act);
                                     }
                                 }
                    );
            mTuyaGWActivator = TuyaHomeSdk.getActivatorInstance(). newGwSubDevActivator(builder);
            mTuyaGWActivator.start();
            d.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mTuyaGWActivator.stop();
                }
            });
        }
        else if (Room.getWiredZBGateway() != null ) {
            ScanningDialog d = new ScanningDialog(act,"Scanning ZIGBEE Device");
            TuyaGwSubDevActivatorBuilder builder = new TuyaGwSubDevActivatorBuilder()
                    .setDevId(Room.getGATEWAY_B().devId)
                    .setTimeOut(150)
                    .setListener(new ITuyaSmartActivatorListener() {

                                     @Override
                                     public void onError(String errorCode, String errorMsg)
                                     {
                                         d.close();
                                         mTuyaGWActivator.stop();
                                         Log.d("ZBdeviceSearch" , "errm "+errorMsg +" errc "+errorCode);
                                     }

                                     @Override
                                     public void onActiveSuccess(DeviceBean devResp)
                                     {
                                         d.close();
                                         Rooms.CHANGE_STATUS = true ;
                                         FOUND = devResp ;
                                         FOUNDD = TuyaHomeSdk.newDeviceInstance(devResp.getDevId());
                                         Toast.makeText(act,"Device Saved" , Toast.LENGTH_LONG).show();
                                         foundZbeeDevice.setText(devResp.getName());
                                         mTuyaGWActivator.stop();
                                         Log.d("ZBdeviceSearch" , devResp.name);
                                     }

                                     @Override
                                     public void onStep(String step, Object data)
                                     {
                                         d.close();
                                         Rooms.CHANGE_STATUS = true ;
                                         mTuyaGWActivator.stop();
                                         Log.d("ZBdeviceSearch" , "step "+step);
                                     }
                                 }
                    );
            mTuyaGWActivator = TuyaHomeSdk.getActivatorInstance(). newGwSubDevActivator(builder);
            mTuyaGWActivator.start();
            d.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mTuyaGWActivator.stop();
                }
            });
        }
        else {
            Toast.makeText(act,"this Room Has No ZIGBEE Gateway",Toast.LENGTH_LONG).show();
        }
    }

    public void renameDeviceZ(View view) {
        if (!DeviceTypesZ.getSelectedItem().toString().isEmpty()) {
            lodingDialog d = new lodingDialog(act);
            if (FOUNDD != null ) {
                FOUNDD.renameDevice(DeviceTypesZ.getSelectedItem().toString(), new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        d.stop();
                        new MessageDialog(error+" "+code,"Failed",act);
                    }
                    @Override
                    public void onSuccess() {
                        d.stop();
                        NewNameZ = DeviceTypesZ.getSelectedItem().toString();
                        foundZbeeDevice.setText(NewNameZ);
                        foundZbeeDevice.setTextColor(Color.GREEN);
                        Toast.makeText(act, "Name Changed Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                d.stop();
                Toast.makeText(act,"No Found Device",Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText( act,"Write New Device Name " , Toast.LENGTH_LONG).show();
        }
    }

    public void saveDeviceZ(View view) {
        if (NewNameZ == null) {
            Toast.makeText(act,"Rename Device First " , Toast.LENGTH_LONG).show();
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Power")) {
            Room.setPOWER_B(FOUND);
            Room.setPOWER(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            power.setText("YES");
            power.setTextColor(Color.GREEN);
            Room.setPowerSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"ZGatway")) {
            Room.setGATEWAY_B(FOUND);
            Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            gateway.setText("YES");
            gateway.setTextColor(Color.GREEN);
            Room.setZBGatewayStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"AC")) {
            Room.setAC_B(FOUND);
            Room.setAC(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            ac.setText("YES");
            ac.setTextColor(Color.GREEN);
            Room.setThermostatStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"DoorSensor")) {
            Room.setDOORSENSOR_B(FOUND);
            Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            door.setText("YES");
            door.setTextColor(Color.GREEN);
            Room.setDoorSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"MotionSensor")) {
            Room.setMOTIONSENSOR_B(FOUND);
            Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            motion.setText("YES");
            motion.setTextColor(Color.GREEN);
            Room.setMotionSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Curtain")) {
            Room.setCURTAIN_B(FOUND);
            Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            curtain.setText("YES");
            curtain.setTextColor(Color.GREEN);
            Room.setCurtainSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"ServiceSwitch")) {
            Room.setSERVICE1_B(FOUND);
            Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            service.setText("YES");
            service.setTextColor(Color.GREEN);
            Room.setServiceSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch1")) {
            Room.setSWITCH1_B(FOUND);
            Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch1.setText("YES");
            switch1.setTextColor(Color.GREEN);
            Room.setSwitch1Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch2")) {
            Room.setSWITCH2_B(FOUND);
            Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch2.setText("YES");
            switch2.setTextColor(Color.GREEN);
            Room.setSwitch2Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch3")) {
            Room.setSWITCH3_B(FOUND);
            Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch3.setText("YES");
            switch3.setTextColor(Color.GREEN);
            Room.setSwitch3Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch4")) {
            Room.setSWITCH4_B(FOUND);
            Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch4.setText("YES");
            switch4.setTextColor(Color.GREEN);
            Room.setSwitch4Status(String.valueOf(Room.id),"1",act);
        }
        else {
            Toast.makeText(act,"Device Not Detected" , Toast.LENGTH_LONG).show();
        }
        Rooms.refreshSystem();
    }

    public void searchWireZBGateway(View view) {
        Log.e("wiregateway" , "started");
        ScanningDialog d = new ScanningDialog(act,"Scanning Wired Gateway Device");
        mTuyaGwSearcher = TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSearcher();
        mTuyaGwSearcher.registerGwSearchListener(new IGwSearchListener() {
            @Override
            public void onDevFind(HgwBean hgwBean) {
                Log.e("wiregateway" , "id "+hgwBean.gwId + " " +Login.THEHOME.getName() );
                ITuyaDeviceActivator a = TuyaHomeSdk.getActivatorInstance();
                a.getActivatorToken(Login.THEHOME.getHomeId(), new ITuyaActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        Token = token ;
                        Log.e("wiregateway" , "token "+Token);
                                TuyaGwActivatorBuilder builder = new TuyaGwActivatorBuilder()
                                        .setToken(Token)
                                        .setTimeOut(60)
                                        .setContext(act)
                                        .setHgwBean(hgwBean)
                                        .setListener(new ITuyaSmartActivatorListener() {

                                                         @Override
                                                         public void onError(String errorCode, String errorMsg) {
                                                             d.close();
                                                             Log.e("wiregateway" , "error "+errorMsg+" "+errorCode);
                                                             mITuyaActivator.stop();
                                                             Toast.makeText(act,errorMsg,Toast.LENGTH_LONG).show();
                                                             new MessageDialog(errorMsg+" "+errorCode,"Failed",act);
                                                         }
                                                         @Override
                                                         public void onActiveSuccess(DeviceBean devResp) {
                                                             d.close();
                                                             FOUND = devResp ;
                                                             FOUNDG = TuyaHomeSdk.newGatewayInstance(devResp.devId);
                                                             foundwireZbGateway.setText(FOUND.getName());
                                                             foundwireZbGateway.setTextColor(Color.GREEN);
                                                             wirezbGatewayNewName.setText(Room.RoomNumber+"ZGatway");
                                                             mITuyaActivator.stop();
                                                             new MessageDialog(devResp.name,"Found",act);
                                                         }
                                                         @Override
                                                         public void onStep(String step, Object data) {
                                                             d.close();
                                                             mITuyaActivator.stop();
                                                             Rooms.refreshSystem();
                                                             new MessageDialog("device found \n check devices list ","Found",act);
                                                         }
                                                     });

                        mITuyaActivator = TuyaHomeSdk.getActivatorInstance().newGwActivator(builder);
                        mITuyaActivator.start() ;
                        d.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                mITuyaActivator.stop();
                            }
                        });
                    }
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        Log.e("wiregateway" , errorMsg);
                    }
                });
            }
        });
    }

    public void renameWiredGateway(View view) {
        if (FOUND != null) {
            FOUNDD = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
            FOUNDD.renameDevice(wirezbGatewayNewName.getText().toString(), new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    new MessageDialog(error+" "+code,"Failed",act);
                }
                @Override
                public void onSuccess() {
                    foundwireZbGateway.setText(wirezbGatewayNewName.getText().toString());
                    foundwireZbGateway.setTextColor(Color.GREEN);
                }
            });
            Log.d("wiredNewName" , FOUND.name);
        }
        else {
            Toast.makeText( act,"Write New Device Name " , Toast.LENGTH_LONG).show();
        }
    }

    public void saveWiredGateway(View view) {
        if (!foundwireZbGateway.getText().toString().isEmpty()) {
            Room.setGATEWAY_B(FOUND);
            Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            gateway.setText("YES");
            gateway.setTextColor(Color.GREEN);
            Room.setZBGatewayStatus(String.valueOf(Room.id),"1",act);
            Rooms.refreshSystem();
        }
    }
    //_______________________________________________

    private void initBtService() {
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
    }

    public void scanLocks(View view) {
        //check BT active
        boolean isBtEnable = TTLockClient.getDefault().isBLEEnabled(act);
        if (!isBtEnable) {
            TTLockClient.getDefault().requestBleEnable(act);
        }
        //start scan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }

        getScanLockCallback();
    }

    private void getScanLockCallback() {
        lodingDialog l = new lodingDialog(act);
        TTLockClient.getDefault().startScanLock(new ScanLockCallback() {
            @Override
            public void onScanLockSuccess(ExtendedBluetoothDevice device)
            {
                l.stop();
                Rooms.CHANGE_STATUS = true ;
                FOUNDLOCK = device ;
                foundLock.setText(device.getName());
                TTLockClient.getDefault().stopScanLock();
                foundLockNewName.setText(Room.RoomNumber+"Lock");
            }

            @Override
            public void onFail(LockError error)
            {
                l.stop();
                Log.e("tttt",error.getErrorMsg());
                //ToastMaker.MakeToast(error.getErrorMsg(),act);
            }
        });
    }

    public void saveLock(View view) {
        if (!FOUNDLOCK.getName().equals(Room.RoomNumber+"Lock")) {
            Toast.makeText(act,"Please Rename Lock First ",Toast.LENGTH_LONG).show();
        }
        else {
            TTLockClient.getDefault().initLock(FOUNDLOCK, new InitLockCallback()
            {
                @Override
                public void onInitLockSuccess(String lockData) {
                    //this must be done after lock is initialized,call server api to post to your server
                    Rooms.CHANGE_STATUS = true ;
                    if ( FeatureValueUtil.isSupportFeature(lockData, FeatureValue.NB_LOCK) )
                    {
                        setNBServerForNBLock( lockData,FOUNDLOCK.getAddress());
                    }
                    else
                    {
                        //ToastMaker.MakeToast("--lock is initialized success--",act);
                        lodingDialog l = new lodingDialog(act);
                        String url = "";//Login.SelectedHotel.URL+"setLockStatusValue.php";
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                l.stop();
                                if (response.equals("1"))
                                {
                                    Toast.makeText(act, "--lock is initialized success--", Toast.LENGTH_LONG).show();
                                    lock.setTextColor(Color.GREEN);
                                    lock.setText("YES");
                                    upload2Server(lockData);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                l.stop();
                                Toast.makeText(act, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String,String> Params = new HashMap<String,String>();
                                Params.put("room", String.valueOf(Room.RoomNumber));
                                Params.put("id",String.valueOf(Room.id) );
                                Params.put("value" , "1");
                                return Params;
                            }
                        };
                        Volley.newRequestQueue(act).add(request);
                    }
                }

                @Override
                public void onFail(LockError error)
                {
                    Toast.makeText(act, error.getErrorMsg(), Toast.LENGTH_LONG).show();
                    //ToastMaker.MakeToast(error.getErrorMsg(),act);
                }
            });
        }
    }

    public void renameLock(View view) {
        if (FOUNDLOCK != null) {
            FOUNDLOCK.setName(Room.RoomNumber+"Lock");
            foundLock.setText(FOUNDLOCK.getName());
            foundLock.setTextColor(Color.GREEN);
        }
        else {
            Toast.makeText(act,"Found Lock Is Null",Toast.LENGTH_LONG).show();
        }
    }

    public static void setNBServerForNBLock(final String lockData, String lockMac) {
        //NB server port
        short mNBServerPort = 8011;
        String mNBServerAddress = "192.127.123.11";
        TTLockClient.getDefault().setNBServerInfo(mNBServerPort, mNBServerAddress, lockData, new SetNBServerCallback() {
            @Override
            public void onSetNBServerSuccess(int battery) {
                Toast.makeText(act, "--set NB server success--", Toast.LENGTH_LONG).show();
                upload2Server(lockData);
            }
            @Override
            public void onFail(LockError error) {
                Toast.makeText(act, error.getErrorMsg(), Toast.LENGTH_LONG).show();
                //no matter callback is success or fail,upload lockData to server.
                upload2Server(lockData);
            }
        });
    }

    public static void upload2Server(String lockData) {
        Calendar c = Calendar.getInstance() ;
        c.setTimeInMillis(System.currentTimeMillis());
        String lockAlias = "MyTestLock" + c.get(Calendar.DAY_OF_MONTH);//DateUtils.getMillsTimeFormat(System.currentTimeMillis());
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<ResponseBody> call = apiService.lockInit(ApiService.CLIENT_ID,Rooms.acc.getAccess_token() , lockData,lockAlias,System.currentTimeMillis());
        RetrofitAPIManager.enqueue(call, new TypeToken<LockInitResultObj>() {
        }, new ApiResponse.Listener<ApiResult<LockInitResultObj>>() {
            @Override
            public void onResponse(ApiResult<LockInitResultObj> result) {
                if (!result.success) {
                    Toast.makeText(act, "-init fail-to server-", Toast.LENGTH_LONG).show();
                    //if upload fail you should cache lockData and upload again until success,or you should reset lock and do init again.
                    return;
                }
                Toast.makeText(act, "--init lock success--", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(act, UserLockActivity.class);
               // act.startActivity(intent);
                //act.finish();

            }
        }, new ApiResponse.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable requestError) {
                Toast.makeText(act, requestError.getMessage()+"error", Toast.LENGTH_LONG).show();
            }
        });
    }

    void getSceneBGs() {
        TuyaHomeSdk.getSceneManagerInstance().getSceneBgs(new ITuyaResultCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> strings) {
                Log.d("scenesAre","images get done");
                IMAGES = strings ;
                getScenes();
            }

            @Override
            public void onError(String s, String s1) {
                Log.d("scenesAre",s+" "+s1);
            }
        });
    }

    void getScenes() {
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(MyApp.HOME.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                SCENES = result ;
                MY_SCENES.clear();
                LivingMood.clear();
                SleepMood.clear();
                WorkMood.clear();
                RomanceMood.clear();
                ReadMood.clear();
                MasterOffMood.clear();
                Log.d("scenesAre",SCENES.size()+"");
                for (SceneBean s : SCENES) {
                    Log.d("scenesAre",s.getName());
                    if (s.getName().contains(String.valueOf(Room.RoomNumber))) {
                        MY_SCENES.add(s);
                    }
                }
                if (MY_SCENES.size() > 0) {
                    for (int i=0;i<MY_SCENES.size();i++) {
                        Log.d("scenesAre","my scenes "+MY_SCENES.get(i).getName());
                        if (MY_SCENES.get(i).getName().contains("Living")) {
                            LivingMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Sleep")) {
                            SleepMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Work")) {
                            WorkMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Romance")) {
                            RomanceMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Read")) {
                            ReadMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("MasterOff")) {
                            MasterOffMood.add(MY_SCENES.get(i));
                        }
                    }
                }
            }
            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.d("scenesAre",errorCode+" "+errorMessage);
            }
        });
    }

    public void goToLightsControl(View view) {
        if (Room.getSWITCH1_B() == null && Room.getSWITCH2_B() == null && Room.getSWITCH3_B() == null && Room.getSWITCH4_B() == null && Room.getSWITCH5_B() == null && Room.getSWITCH6_B() == null && Room.getSWITCH7_B() == null && Room.getSWITCH8_B() == null) {
            new MessageDialog("no Light Switches Detected","No Lights",act);
            return;
        }
        Intent i = new Intent(act,LightingControl.class);
        startActivity(i);
    }

    public void searchMultiMoodGateway(View view) {
        Log.d("multiMoodS","start");
        ScanningDialog d = new ScanningDialog(act,"Scanning MultiMood Gateway");
        d.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                TuyaHomeSdk.getBleOperator().stopLeScan();
            }
        });
        LeScanSetting scanSetting = new LeScanSetting.Builder()
                .setTimeout(60000) // The duration of the scanning. Unit: milliseconds.
                .addScanType(ScanType.SINGLE) // ScanType.SINGLE: scans for Bluetooth LE devices.
                // .addScanType(ScanType.SIG_MESH): scans for other types of devices.
                .build();
        TuyaHomeSdk.getBleOperator().startLeScan(scanSetting, new TyBleScanResponse() {
            @Override
            public void onResult(ScanDeviceBean bean) {
                Log.d("multiMoodS","find "+bean.getName());
                TuyaHomeSdk.getActivatorInstance().getActivatorToken(MyApp.HOME.getHomeId(), new ITuyaActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d("multiMoodS","token "+token);
                        MultiModeActivatorBean multiModeActivatorBean = new MultiModeActivatorBean();
                        multiModeActivatorBean.deviceType = bean.getDeviceType(); // The type of device.
                        multiModeActivatorBean.uuid = bean.getUuid(); // The UUID of the device.
                        multiModeActivatorBean.address = bean.getAddress(); // The IP address of the device.
                        multiModeActivatorBean.mac = bean.getMac(); // The MAC address of the device.
                        multiModeActivatorBean.ssid = selectedWifi.getText().toString(); // The SSID of the target Wi-Fi network.
                        multiModeActivatorBean.pwd = wifiPass.getText().toString(); // The password of the target Wi-Fi network.
                        multiModeActivatorBean.token = token; // The pairing token.
                        multiModeActivatorBean.homeId = MyApp.HOME.getHomeId(); // The value of `homeId` for the current home.
                        multiModeActivatorBean.timeout = 120000;
                        TuyaHomeSdk.getActivator().newMultiModeActivator().startActivator(multiModeActivatorBean, new IMultiModeActivatorListener() {
                            @Override
                            public void onSuccess(DeviceBean deviceBean) {
                                Log.d("multiMoodS","device paired "+deviceBean.getName());
                                d.close();
                                Rooms.CHANGE_STATUS = true ;
                                foundWifiDevice.setText(deviceBean.getName());
                                FOUND = deviceBean ;
                                FOUNDD = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
                                TuyaHomeSdk.getBleOperator().stopLeScan();
                                new MessageDialog(deviceBean.getName(),"Success",act);
                            }

                            @Override
                            public void onFailure(int code, String msg, Object handle) {
                                Log.d("multiMoodS","pair error "+code+" "+msg);
                                d.close();
                                TuyaHomeSdk.getBleOperator().stopLeScan();
                                new MessageDialog(msg+" "+code,"Failed",act);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        d.close();
                        TuyaHomeSdk.getBleOperator().stopLeScan();
                        new MessageDialog(errorMsg+" "+errorCode,"Failed",act);
                    }
                });
            }
        });
//        TuyaHomeSdk.getBleOperator().startLeScan(scanSetting, new BleScanResponse() {
//            @Override
//            public void onResult(ScanDeviceBean bean) {
//                Log.d("multiMoodS","scan result "+bean.getName());
//                ThingHomeSdk.getActivatorInstance().getActivatorToken(MyApp.HOME.getHomeId(),
//                        new IThingActivatorGetToken() {
//
//                            @Override
//                            public void onSuccess(String token) {
//                                Log.d("multiMoodS","token "+token);
//                                MultiModeActivatorBean multiModeActivatorBean = new MultiModeActivatorBean();
//                                multiModeActivatorBean.deviceType = bean.getDeviceType(); // The type of device.
//                                multiModeActivatorBean.uuid = bean.getUuid(); // The UUID of the device.
//                                multiModeActivatorBean.address = bean.getAddress(); // The IP address of the device.
//                                multiModeActivatorBean.mac = bean.getMac(); // The MAC address of the device.
//                                multiModeActivatorBean.ssid = selectedWifi.getText().toString(); // The SSID of the target Wi-Fi network.
//                                multiModeActivatorBean.pwd = wifiPass.getText().toString(); // The password of the target Wi-Fi network.
//                                multiModeActivatorBean.token = token; // The pairing token.
//                                multiModeActivatorBean.homeId = MyApp.HOME.getHomeId(); // The value of `homeId` for the current home.
//                                multiModeActivatorBean.timeout = 120000;
//                                ThingHomeSdk.getActivator().newMultiModeActivator().startActivator(multiModeActivatorBean, new IMultiModeActivatorListener() {
//                                    @Override
//                                    public void onSuccess(DeviceBean deviceBean) {
//                                        // The device is paired.
//                                        Log.d("multiMoodS","device paired "+deviceBean.getName());
//                                    }
//
//                                    @Override
//                                    public void onFailure(int code, String msg, Object handle) {
//                                        // Failed to pair the device.
//                                        Log.d("multiMoodS","pair error "+code+" "+msg);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(String s, String s1) {
//                                Log.d("multiMoodS","token error "+s+" "+s1);
//                            }
//                        });
//
//
//            }
//        });
    }
}