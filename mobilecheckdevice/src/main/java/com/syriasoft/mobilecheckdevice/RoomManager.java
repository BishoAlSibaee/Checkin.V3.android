package com.syriasoft.mobilecheckdevice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.lock.ApiService;
import com.syriasoft.mobilecheckdevice.lock.LockInitResultObj;
import com.syriasoft.mobilecheckdevice.lock.RetrofitAPIManager;
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
import com.tuya.smart.android.ble.api.ScanType;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class RoomManager extends AppCompatActivity {
    public static ROOM Room ;
    private TextView foundLockNewName;
    private TextView foundLock;
    private TextView lock;
    private TextView power;
    private TextView curtain;
    private TextView service;
    private TextView door;
    private TextView motion;
    private TextView switch1;
    private TextView switch2;
    private TextView switch3;
    private TextView switch4;
    private TextView switch5;
    private TextView switch6;
    private TextView switch7;
    private TextView switch8;
    private TextView ac;
    private TextView gateway;
    private TextView selectedWifi;
    private TextView foundWifiDevice;
    private TextView foundZigBeeDevice;
    private TextView foundWireZbGateway;
    private TextView wireZbGatewayNewName;
    private WifiManager wifiManager;
    private static Activity act  ;
    private ListView wifiList;
    private Spinner DeviceTypes , DeviceTypesZ ;
    private EditText wifiPass ;
    private String Token , NewName ,NewNameZ;
    private DeviceBean FOUND ;
    private ITuyaDevice FOUND_D;
    private ITuyaGateway FOUND_G;
    protected static final int REQUEST_PERMISSION_REQ_CODE = 11;
    private ExtendedBluetoothDevice FOUND_LOCK;
    ITuyaActivator mTuyaActivator ;
    ITuyaActivator mTuyaGWActivator ;
    ITuyaGwSearcher mTuyaGwSearcher ;
    ITuyaActivator mITuyaActivator ;
    RequestQueue REQ ;
    static List<SceneBean> SCENES,MY_SCENES,LivingMood,SleepMood,WorkMood,RomanceMood,ReadMood,MasterOffMood ;
    public static List<String> IMAGES ;
    public static CheckInHome HOME;
    public static List<DeviceBean> RoomDevices;

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
        getWifiNetworks();
        initBtService();
        resetRoomDevices(act);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getScanLockCallback();
                }
                break;
            }
            case 10 : {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    searchMultiMoodGateway(new View(act));
                }
                break;
            }
        }
    }

    void getWifiNetworks(View view) {
        getWifiNetworks();
    }

    private void getWifiNetworks() {
        List<ScanResult> results = wifiManager.getScanResults();
        List<String> deviceList = new ArrayList<>();
        for (ScanResult scanResult : results) {
            Log.d("wifiResult",scanResult.SSID);
            deviceList.add(scanResult.SSID);
        }
        WifiAdapter Adapter = new WifiAdapter(deviceList,act);
        wifiList.setAdapter(Adapter);
        wifiList.setNestedScrollingEnabled(true);
        wifiList.setOnItemClickListener((parent, view, position, id) -> selectedWifi.setText(deviceList.get(position)));
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
        RoomDevices = new ArrayList<>();
        Room = MyApp.SelectedRoom;
        if (Room.isRoomUnInstalled()) {
            HOME = Rooms.SelectedHome;
        }
        else {
            for (CheckInHome h : MyApp.PROJECT_HOMES) {
                if (h.Devices != null) {
                    for (DeviceBean d : h.Devices) {
                        if (d.devId.equals(Room.getGATEWAY_B().devId)) {
                            HOME = h;
                            break;
                        }
                    }
                }
            }
        }
        TextView caption = findViewById(R.id.roomManager_caption);
        lock = findViewById(R.id.room_Lock);
        power = findViewById(R.id.room_Power);
        curtain = findViewById(R.id.room_Curtain);
        service = findViewById(R.id.room_Service);
        door = findViewById(R.id.room_Doorsensor);
        motion = findViewById(R.id.room_Motion);
        switch1 = findViewById(R.id.room_Switch1);
        switch2 = findViewById(R.id.room_Switch2);
        switch3 = findViewById(R.id.room_Switch3);
        switch4 = findViewById(R.id.room_Switch4);
        switch5 = findViewById(R.id.room_Switch5);
        switch6 = findViewById(R.id.room_Switch6);
        switch7 = findViewById(R.id.room_Switch7);
        switch8 = findViewById(R.id.room_Switch8);
        selectedWifi = findViewById(R.id.selected_wifi);
        foundWifiDevice = findViewById(R.id.theFoundDevice);
        foundZigBeeDevice = findViewById(R.id.theFoundDeviceZbee);
        foundWireZbGateway = findViewById(R.id.wire_zbgate_found);
        wireZbGatewayNewName = findViewById(R.id.wire_zbgateway_newstaticName);
        foundLock = findViewById(R.id.foundlock);
        foundLockNewName = findViewById(R.id.foundLockNewName);
        ac = findViewById(R.id.room_AC);
        wifiPass = findViewById(R.id.wifi_pass);
        TextView WifiCaption = findViewById(R.id.textView36);
        LinearLayout WifiLayout = findViewById(R.id.wifiLayout);
        WifiCaption.setOnClickListener(view -> {
            if (WifiLayout.getVisibility() == View.VISIBLE) {
                WifiLayout.setVisibility(View.GONE);
                WifiCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down,0,R.drawable.wifi,0);
            }
            else {
                WifiLayout.setVisibility(View.VISIBLE);
                WifiCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up,0,R.drawable.wifi,0);
            }
        });
        LinearLayout ZigbeeLayout = findViewById(R.id.zigbeeLayout);
        TextView ZigbeeCaption = findViewById(R.id.textView37);
        ZigbeeCaption.setOnClickListener(view -> {
            if (ZigbeeLayout.getVisibility() == View.VISIBLE) {
                ZigbeeLayout.setVisibility(View.GONE);
                ZigbeeCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down,0,R.drawable.zigbee,0);
            }
            else {
                ZigbeeLayout.setVisibility(View.VISIBLE);
                ZigbeeCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up,0,R.drawable.zigbee,0);
            }
        });
        TextView LocksCaption = findViewById(R.id.textView2);
        LinearLayout LocksLayout = findViewById(R.id.locksLayout);
        LocksCaption.setOnClickListener(view -> {
            if (LocksLayout.getVisibility() == View.VISIBLE) {
                LocksLayout.setVisibility(View.GONE);
                LocksCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down,0,android.R.drawable.stat_sys_data_bluetooth,0);
            }
            else {
                LocksLayout.setVisibility(View.VISIBLE);
                LocksCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up,0,android.R.drawable.stat_sys_data_bluetooth,0);
            }
        });
        TextView DevicesCaption = findViewById(R.id.textView2vv);
        RecyclerView devicesR = act.findViewById(R.id.devicesRecycler);
        DevicesCaption.setOnClickListener(view -> {
            if (devicesR.getVisibility() == View.VISIBLE) {
                devicesR.setVisibility(View.GONE);
                DevicesCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down,0,R.drawable.devices,0);
            }
            else {
                devicesR.setVisibility(View.VISIBLE);
                DevicesCaption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up,0,R.drawable.devices,0);
            }
        });
        Button getWifi = findViewById(R.id.room_addWifi);
        getWifi.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                getWifiNetworks();
            }
        });
        wifiList = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(act, "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        gateway = findViewById(R.id.room_Gateway);
        DeviceTypes = findViewById(R.id.deviceNames_spinner);
        DeviceTypesZ = findViewById(R.id.deviceNames_spinnerZbee);
        String [] Types = new String[]{Room.RoomNumber+"Power",Room.RoomNumber+"ZGatway",Room.RoomNumber+"AC",Room.RoomNumber+"DoorSensor",Room.RoomNumber+"MotionSensor",Room.RoomNumber+"Curtain",Room.RoomNumber+"ServiceSwitch",Room.RoomNumber+"Switch1",Room.RoomNumber+"Switch2",Room.RoomNumber+"Switch3",Room.RoomNumber+"Switch4",Room.RoomNumber+"Switch5",Room.RoomNumber+"Switch6",Room.RoomNumber+"Switch7",Room.RoomNumber+"Switch8",Room.RoomNumber+"Shutter1",Room.RoomNumber+"Shutter2",Room.RoomNumber+"Shutter3",Room.RoomNumber+"IR",Room.RoomNumber+"Lock"};
        ArrayAdapter<String> x =  new ArrayAdapter<>(act ,R.layout.spinners_item ,Types);
        ArrayAdapter<String> y =  new ArrayAdapter<>(act ,R.layout.spinners_item ,Types);
        DeviceTypesZ.setAdapter(y);
        DeviceTypes.setAdapter(x);
        caption.setText(MessageFormat.format("Manage Room :{0} - Home : {1}", Room.RoomNumber, HOME.Home.getName()));
        setRoomDevices(act);
    }

    static void setRoomDevices(Activity act) {
        TextView lock = act.findViewById(R.id.room_Lock);
        if (Room.getLOCK_B() == null) {
            lock.setText(act.getResources().getString(R.string.no));
            lock.setTextColor(Color.RED);
        }
        else {
            lock.setText(act.getResources().getString(R.string.yes));
            lock.setTextColor(Color.GREEN);
        }
        TextView switch1 = act.findViewById(R.id.room_Switch1);
        if (Room.getSWITCH1_B() == null) {
            switch1.setText(act.getResources().getString(R.string.no));
            switch1.setTextColor(Color.RED);
        }
        else {
            switch1.setText(act.getResources().getString(R.string.yes));
            switch1.setTextColor(Color.GREEN);
        }
        TextView switch2 = act.findViewById(R.id.room_Switch2);
        if (Room.getSWITCH2_B() == null) {
            switch2.setText(act.getResources().getString(R.string.no));
            switch2.setTextColor(Color.RED);
        }
        else {
            switch2.setText(act.getResources().getString(R.string.yes));
            switch2.setTextColor(Color.GREEN);
        }
        TextView switch3 = act.findViewById(R.id.room_Switch3);
        if (Room.getSWITCH3_B() == null) {
            switch3.setText(act.getResources().getString(R.string.no));
            switch3.setTextColor(Color.RED);
        }
        else {
            switch3.setText(act.getResources().getString(R.string.yes));
            switch3.setTextColor(Color.GREEN);
        }
        TextView switch4 = act.findViewById(R.id.room_Switch4);
        if (Room.getSWITCH4_B() == null) {
            switch4.setText(act.getResources().getString(R.string.no));
            switch4.setTextColor(Color.RED);
        }
        else {
            switch4.setText(act.getResources().getString(R.string.yes));
            switch4.setTextColor(Color.GREEN);
        }
        TextView switch5 = act.findViewById(R.id.room_Switch5);
        if (Room.getSWITCH5_B() == null) {
            switch5.setText(act.getResources().getString(R.string.no));
            switch5.setTextColor(Color.RED);
        }
        else {
            switch5.setText(act.getResources().getString(R.string.yes));
            switch5.setTextColor(Color.GREEN);
        }
        TextView switch6 = act.findViewById(R.id.room_Switch6);
        if (Room.getSWITCH6_B() == null) {
            switch6.setText(act.getResources().getString(R.string.no));
            switch6.setTextColor(Color.RED);
        }
        else {
            switch6.setText(act.getResources().getString(R.string.yes));
            switch6.setTextColor(Color.GREEN);
        }
        TextView switch7 = act.findViewById(R.id.room_Switch7);
        if (Room.getSWITCH7_B() == null) {
            switch7.setText(act.getResources().getString(R.string.no));
            switch7.setTextColor(Color.RED);
        }
        else {
            switch7.setText(act.getResources().getString(R.string.yes));
            switch7.setTextColor(Color.GREEN);
        }
        TextView switch8 = act.findViewById(R.id.room_Switch8);
        if (Room.getSWITCH8_B() == null) {
            switch8.setText(act.getResources().getString(R.string.no));
            switch8.setTextColor(Color.RED);
        }
        else {
            switch8.setText(act.getResources().getString(R.string.yes));
            switch8.setTextColor(Color.GREEN);
        }
        TextView curtain = act.findViewById(R.id.room_Curtain);
        if (Room.getCURTAIN_B() == null) {
            curtain.setText(act.getResources().getString(R.string.no));
            curtain.setTextColor(Color.RED);
        }
        else {
            curtain.setText(act.getResources().getString(R.string.yes));
            curtain.setTextColor(Color.GREEN);
        }
        TextView motion = act.findViewById(R.id.room_Motion);
        if (Room.getMOTIONSENSOR_B() == null) {
            motion.setText(act.getResources().getString(R.string.no));
            motion.setTextColor(Color.RED);
        }
        else {
            motion.setText(act.getResources().getString(R.string.yes));
            motion.setTextColor(Color.GREEN);
        }
        TextView door = act.findViewById(R.id.room_Doorsensor);
        if (Room.getDOORSENSOR_B() == null) {
            door.setText(act.getResources().getString(R.string.no));
            door.setTextColor(Color.RED);
        }
        else {
            door.setText(act.getResources().getString(R.string.yes));
            door.setTextColor(Color.GREEN);
        }
        TextView power = act.findViewById(R.id.room_Power);
        if (Room.getPOWER_B() == null) {
            power.setText(act.getResources().getString(R.string.no));
            power.setTextColor(Color.RED);
        }
        else {
            power.setText(act.getResources().getString(R.string.yes));
            power.setTextColor(Color.GREEN);
        }
        TextView service = act.findViewById(R.id.room_Service);
        if (Room.getSERVICE1_B() == null) {
            service.setText(act.getResources().getString(R.string.no));
            service.setTextColor(Color.RED);
        }
        else {
            service.setText(act.getResources().getString(R.string.yes));
            service.setTextColor(Color.GREEN);
        }
        TextView ac = act.findViewById(R.id.room_AC);
        if (Room.getAC_B() == null) {
            ac.setText(act.getResources().getString(R.string.no));
            ac.setTextColor(Color.RED);
        }
        else {
            ac.setText(act.getResources().getString(R.string.yes));
            ac.setTextColor(Color.GREEN);
        }
        TextView gateway = act.findViewById(R.id.room_Gateway);
        if ( Room.getGATEWAY_B() == null ) {
            gateway.setText(act.getResources().getString(R.string.no));
            gateway.setTextColor(Color.RED);
        }
        else {
            gateway.setText(act.getResources().getString(R.string.yes));
            gateway.setTextColor(Color.GREEN);
        }
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
        SCENES = MyApp.SCENES;
        for (SceneBean s : SCENES) {
            if (s.getName().contains(String.valueOf(Room.RoomNumber))) {
                MY_SCENES.add(s);
            }
        }
        if (!MY_SCENES.isEmpty()) {
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

    public void goToLightsControl(View view) {
        if (Room.getSWITCH1_B() == null && Room.getSWITCH2_B() == null && Room.getSWITCH3_B() == null && Room.getSWITCH4_B() == null && Room.getSWITCH5_B() == null && Room.getSWITCH6_B() == null && Room.getSWITCH7_B() == null && Room.getSWITCH8_B() == null) {
            new MessageDialog("no Light Switches Detected","No Lights",act);
            return;
        }
        Intent i = new Intent(act,LightingControl.class);
        startActivity(i);
    }

    void getRoomDevices() {
        RoomDevices = ROOM.getRoomDevices(Room);
        setRoomDevices(act);
        resetRoomDevices(act);
    }

    // for Wifi Devices ___________________________________

    public void searchMultiMoodGateway(View view) {
        Log.d("multiMoodS","start");
        if (ActivityCompat.checkSelfPermission(act,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(act,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(act,Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_COARSE_LOCATION},10);
        }
        ScanningDialog d = new ScanningDialog(act,"Scanning MultiMood Gateway");
        d.show().setOnDismissListener(dialogInterface -> TuyaHomeSdk.getBleOperator().stopLeScan());
        LeScanSetting scanSetting = new LeScanSetting.Builder()
                .setTimeout(60000) // The duration of the scanning. Unit: milliseconds.
                .addScanType(ScanType.SINGLE) // ScanType.SINGLE: scans for Bluetooth LE devices.
                // .addScanType(ScanType.SIG_MESH): scans for other types of devices.
                .build();
        TuyaHomeSdk.getBleOperator().startLeScan(scanSetting, bean -> {
            Log.d("multiMoodS","find "+bean.getName());
            TuyaHomeSdk.getActivatorInstance().getActivatorToken(HOME.Home.getHomeId(), new ITuyaActivatorGetToken() {
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
                    multiModeActivatorBean.homeId = HOME.Home.getHomeId(); // The value of `homeId` for the current home.
                    multiModeActivatorBean.timeout = 120000;
                    TuyaHomeSdk.getActivator().newMultiModeActivator().startActivator(multiModeActivatorBean, new IMultiModeActivatorListener() {
                        @Override
                        public void onSuccess(DeviceBean deviceBean) {
                            Log.d("multiMoodS","device paired "+deviceBean.getName());
                            d.close();
                            TextView deviceName = findViewById(R.id.theFoundDevice);
                            deviceName.setText(deviceBean.getName());
                            FOUND = deviceBean ;
                            FOUND_D = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
                            TuyaHomeSdk.getBleOperator().stopLeScan();
                            new MessageDialog(deviceBean.getName(),"Success",act);
                        }

                        @Override
                        public void onFailure(int code, String msg, Object handle) {
                            Log.d("multiMoodS","pair error "+code+" "+msg);
                            d.close();
                            TuyaHomeSdk.getBleOperator().stopLeScan();
                            try {
                                new MessageDialog(msg + " " + code, "Failed", act);
                            }catch(Exception e) {
                                Log.d("multiMoodS","pair error "+e.getMessage());
                            }
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
        });
    }

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
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(HOME.Home.getHomeId(),
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
                                                     foundWifiDevice.setText(devResp.getName());
                                                     FOUND = devResp ;
                                                     FOUND_D = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
                                                     mTuyaActivator.stop();
                                                     new MessageDialog(devResp.getName(),"Success",act);
                                                 }
                                                 @Override
                                                 public void onStep(String step, Object data) {
                                                     d.close();
                                                     try {
                                                         DeviceBean dd = (DeviceBean) data ;
                                                         foundWifiDevice.setText(dd.getName());
                                                         FOUND = dd ;
                                                         FOUND_D = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
                                                         mTuyaActivator.stop();
                                                         new MessageDialog(dd.getName(),"Success",act);
                                                     } catch (Exception e) {
                                                         new MessageDialog(e.getMessage(),"error",act);
                                                     }
                                                 }
                                             }
                                );
                        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder);
                        mTuyaActivator.start();
                        d.show().setOnDismissListener(dialogInterface -> mTuyaActivator.stop());
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
            LoadingDialog d = new LoadingDialog(act);
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
                        FOUND.setName(NewName);
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
            power.setText(getResources().getString(R.string.yes));
            power.setTextColor(Color.GREEN);
            Room.setPowerSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"ZGatway")) {
            Room.setGATEWAY_B(FOUND);
            Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            gateway.setText(getResources().getString(R.string.yes));
            gateway.setTextColor(Color.GREEN);
            Room.setZBGatewayStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"AC")) {
            Room.setAC_B(FOUND);
            Room.setAC(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            ac.setText(getResources().getString(R.string.yes));
            ac.setTextColor(Color.GREEN);
            Room.setThermostatStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"DoorSensor")) {
            Room.setDOORSENSOR_B(FOUND);
            Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            door.setText(getResources().getString(R.string.yes));
            door.setTextColor(Color.GREEN);
            Room.setDoorSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"MotionSensor")) {
            Room.setMOTIONSENSOR_B(FOUND);
            Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            motion.setText(getResources().getString(R.string.yes));
            motion.setTextColor(Color.GREEN);
            Room.setMotionSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Curtain")) {
            Room.setCURTAIN_B(FOUND);
            Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            curtain.setText(getResources().getString(R.string.yes));
            curtain.setTextColor(Color.GREEN);
            Room.setCurtainSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"ServiceSwitch")) {
            Room.setSERVICE1_B(FOUND);
            Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            service.setText(getResources().getString(R.string.yes));
            service.setTextColor(Color.GREEN);
            Room.setServiceSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch1")) {
            Room.setSWITCH1_B(FOUND);
            Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch1.setText(getResources().getString(R.string.yes));
            switch1.setTextColor(Color.GREEN);
            Room.setSwitch1Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch2")) {
            Room.setSWITCH2_B(FOUND);
            Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch2.setText(getResources().getString(R.string.yes));
            switch2.setTextColor(Color.GREEN);
            Room.setSwitch2Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch3")) {
            Room.setSWITCH3_B(FOUND);
            Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch3.setText(getResources().getString(R.string.yes));
            switch3.setTextColor(Color.GREEN);
            Room.setSwitch3Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch4")) {
            Room.setSWITCH4_B(FOUND);
            Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch4.setText(getResources().getString(R.string.yes));
            switch4.setTextColor(Color.GREEN);
            Room.setSwitch4Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch5")) {
            Room.setSWITCH5_B(FOUND);
            Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch5.setText(getResources().getString(R.string.yes));
            switch5.setTextColor(Color.GREEN);
            Room.setSwitch5Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch6")) {
            Room.setSWITCH6_B(FOUND);
            Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch6.setText(getResources().getString(R.string.yes));
            switch6.setTextColor(Color.GREEN);
            Room.setSwitch6Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch7")) {
            Room.setSWITCH7_B(FOUND);
            Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch7.setText(getResources().getString(R.string.yes));
            switch7.setTextColor(Color.GREEN);
            Room.setSwitch7Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewName.equals(Room.RoomNumber+"Switch8")) {
            Room.setSWITCH8_B(FOUND);
            Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch8.setText(getResources().getString(R.string.yes));
            switch8.setTextColor(Color.GREEN);
            Room.setSwitch8Status(String.valueOf(Room.id),"1",act);
        }
        else {
            Toast.makeText(act,"Device Not Detected" , Toast.LENGTH_LONG).show();
        }
        RoomDevices.add(FOUND);
        resetRoomDevices(act);
        Rooms.refreshSystem();
    }

    //________________________________________________________


    // for Zigbee Devices_______________________________

    public void searchZigBeeDevice(View view) {
        if (Room.getGATEWAY_B() != null ) {
            Log.d("gatewayType","wifi");
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
                                         FOUND = devResp ;
                                         FOUND_D = TuyaHomeSdk.newDeviceInstance(devResp.getDevId());
                                         foundZigBeeDevice.setText(devResp.getName());
                                         mTuyaGWActivator.stop();
                                         new MessageDialog(devResp.getName(),"Found",act);
                                     }
                                     @Override
                                     public void onStep(String step, Object data) {
                                         d.close();
                                         try {
                                             DeviceBean dd = (DeviceBean) data ;
                                             FOUND = dd ;
                                             FOUND_D = TuyaHomeSdk.newDeviceInstance(dd.getDevId());
                                             new MessageDialog(dd.getName(),"Found",act);
                                             foundZigBeeDevice.setText(dd.getName());
                                             mTuyaGWActivator.stop();
                                             new MessageDialog(dd.getName(),"Found",act);
                                         } catch (Exception e) {
                                             new MessageDialog(e.getMessage(),"Error",act);
                                         }
                                     }
                                 });
            mTuyaGWActivator = TuyaHomeSdk.getActivatorInstance(). newGwSubDevActivator(builder);
            mTuyaGWActivator.start();
            d.show().setOnDismissListener(dialogInterface -> mTuyaGWActivator.stop());
        }
        else if (Room.getWiredZBGateway() != null ) {
            Log.d("gatewayType","wired");
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
                                     }

                                     @Override
                                     public void onActiveSuccess(DeviceBean devResp) {
                                         d.close();
                                         FOUND = devResp ;
                                         FOUND_D = TuyaHomeSdk.newDeviceInstance(devResp.getDevId());
                                         Toast.makeText(act,"Device Saved" , Toast.LENGTH_LONG).show();
                                         foundZigBeeDevice.setText(devResp.getName());
                                         mTuyaGWActivator.stop();
                                     }

                                     @Override
                                     public void onStep(String step, Object data) {
                                         d.close();
                                         Rooms.CHANGE_STATUS = true ;
                                         mTuyaGWActivator.stop();
                                     }
                                 }
                    );
            mTuyaGWActivator = TuyaHomeSdk.getActivatorInstance(). newGwSubDevActivator(builder);
            mTuyaGWActivator.start();
            d.show().setOnDismissListener(dialogInterface -> mTuyaGWActivator.stop());
        }
        else {
            Toast.makeText(act,"this Room Has No ZIGBEE Gateway",Toast.LENGTH_LONG).show();
        }
    }

    public void renameDeviceZ(View view) {
        if (!DeviceTypesZ.getSelectedItem().toString().isEmpty()) {
            LoadingDialog d = new LoadingDialog(act);
            if (FOUND_D != null ) {
                FOUND_D.renameDevice(DeviceTypesZ.getSelectedItem().toString(), new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        d.stop();
                        new MessageDialog(error+" "+code,"Failed",act);
                    }
                    @Override
                    public void onSuccess() {
                        d.stop();
                        NewNameZ = DeviceTypesZ.getSelectedItem().toString();
                        FOUND.setName(NewNameZ);
                        foundZigBeeDevice.setText(NewNameZ);
                        foundZigBeeDevice.setTextColor(Color.GREEN);
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

    public void saveZigbeeDevice(View view) {
        if (NewNameZ == null) {
            Toast.makeText(act,"Rename Device First " , Toast.LENGTH_LONG).show();
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Power")) {
            Room.setPOWER_B(FOUND);
            Room.setPOWER(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            power.setText(getResources().getString(R.string.yes));
            power.setTextColor(Color.GREEN);
            Room.setPowerSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"ZGatway")) {
            Room.setGATEWAY_B(FOUND);
            Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            gateway.setText(getResources().getString(R.string.yes));
            gateway.setTextColor(Color.GREEN);
            Room.setZBGatewayStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"AC")) {
            Room.setAC_B(FOUND);
            Room.setAC(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            ac.setText(getResources().getString(R.string.yes));
            ac.setTextColor(Color.GREEN);
            Room.setThermostatStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"DoorSensor")) {
            Room.setDOORSENSOR_B(FOUND);
            Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            door.setText(getResources().getString(R.string.yes));
            door.setTextColor(Color.GREEN);
            Room.setDoorSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"MotionSensor")) {
            Room.setMOTIONSENSOR_B(FOUND);
            Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            motion.setText(getResources().getString(R.string.yes));
            motion.setTextColor(Color.GREEN);
            Room.setMotionSensorStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Curtain")) {
            Room.setCURTAIN_B(FOUND);
            Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            curtain.setText(getResources().getString(R.string.yes));
            curtain.setTextColor(Color.GREEN);
            Room.setCurtainSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"ServiceSwitch")) {
            Room.setSERVICE1_B(FOUND);
            Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            service.setText(getResources().getString(R.string.yes));
            service.setTextColor(Color.GREEN);
            Room.setServiceSwitchStatus(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch1")) {
            Room.setSWITCH1_B(FOUND);
            Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch1.setText(getResources().getString(R.string.yes));
            switch1.setTextColor(Color.GREEN);
            Room.setSwitch1Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch2")) {
            Room.setSWITCH2_B(FOUND);
            Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch2.setText(getResources().getString(R.string.yes));
            switch2.setTextColor(Color.GREEN);
            Room.setSwitch2Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch3")) {
            Room.setSWITCH3_B(FOUND);
            Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch3.setText(getResources().getString(R.string.yes));
            switch3.setTextColor(Color.GREEN);
            Room.setSwitch3Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch4")) {
            Room.setSWITCH4_B(FOUND);
            Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch4.setText(getResources().getString(R.string.yes));
            switch4.setTextColor(Color.GREEN);
            Room.setSwitch4Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch5")) {
            Room.setSWITCH5_B(FOUND);
            Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch5.setText(getResources().getString(R.string.yes));
            switch5.setTextColor(Color.GREEN);
            Room.setSwitch5Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch6")) {
            Room.setSWITCH6_B(FOUND);
            Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch6.setText(getResources().getString(R.string.yes));
            switch6.setTextColor(Color.GREEN);
            Room.setSwitch6Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch7")) {
            Room.setSWITCH7_B(FOUND);
            Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch7.setText(getResources().getString(R.string.yes));
            switch7.setTextColor(Color.GREEN);
            Room.setSwitch7Status(String.valueOf(Room.id),"1",act);
        }
        else if (NewNameZ.equals(Room.RoomNumber+"Switch8")) {
            Room.setSWITCH8_B(FOUND);
            Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            switch8.setText(getResources().getString(R.string.yes));
            switch8.setTextColor(Color.GREEN);
            Room.setSwitch8Status(String.valueOf(Room.id),"1",act);
        }
        else {
            Toast.makeText(act,"Device Not Detected" , Toast.LENGTH_LONG).show();
        }
        if (MyApp.searchDeviceInList(RoomDevices,FOUND.devId) == null) {
            RoomDevices.add(FOUND);
        }
        resetRoomDevices(act);
        Rooms.refreshSystem();
    }

    //____________________________________________________


    // for Wired Gateway Locks _______________________________

    public void searchWireZBGateway(View view) {
        ScanningDialog d = new ScanningDialog(act,"Scanning Wired Gateway Device");
        mTuyaGwSearcher = TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSearcher();
        mTuyaGwSearcher.registerGwSearchListener(hgwBean -> {
            ITuyaDeviceActivator a = TuyaHomeSdk.getActivatorInstance();
            a.getActivatorToken(HOME.Home.getHomeId(), new ITuyaActivatorGetToken() {
                @Override
                public void onSuccess(String token) {
                    Token = token ;
                    TuyaGwActivatorBuilder builder = new TuyaGwActivatorBuilder()
                            .setToken(Token)
                            .setTimeOut(60)
                            .setContext(act)
                            .setHgwBean(hgwBean)
                            .setListener(new ITuyaSmartActivatorListener() {

                                             @Override
                                             public void onError(String errorCode, String errorMsg) {
                                                 d.close();
                                                 mITuyaActivator.stop();
                                                 Toast.makeText(act,errorMsg,Toast.LENGTH_LONG).show();
                                                 new MessageDialog(errorMsg+" "+errorCode,"Failed",act);
                                             }
                                             @Override
                                             public void onActiveSuccess(DeviceBean devResp) {
                                                 d.close();
                                                 FOUND = devResp ;
                                                 FOUND_G = TuyaHomeSdk.newGatewayInstance(devResp.devId);
                                                 foundWireZbGateway.setText(FOUND.getName());
                                                 foundWireZbGateway.setTextColor(Color.GREEN);
                                                 wireZbGatewayNewName.setText(MessageFormat.format("{0}ZGatway", Room.RoomNumber));
                                                 wireZbGatewayNewName.setTextColor(Color.WHITE);
                                                 mITuyaActivator.stop();
                                                 new MessageDialog(devResp.name,"Found",act);
                                             }
                                             @Override
                                             public void onStep(String step, Object data) {
                                                 d.close();
                                                 try {
                                                     DeviceBean dd = (DeviceBean) data;
                                                     Log.d("stepProblem", step + " " + dd.getName());
                                                     FOUND = dd;
                                                     FOUND_G = TuyaHomeSdk.newGatewayInstance(dd.devId);
                                                     foundWireZbGateway.setText(FOUND.getName());
                                                     foundWireZbGateway.setTextColor(Color.GREEN);
                                                     wireZbGatewayNewName.setText(MessageFormat.format("{0}ZGatway", Room.RoomNumber));
                                                     wireZbGatewayNewName.setTextColor(Color.WHITE);
                                                     mITuyaActivator.stop();
                                                     new MessageDialog(dd.name, "Found", act);
                                                 }
                                                 catch (Exception e) {
                                                     new MessageDialog(e.getMessage(), "error", act);
                                                 }
                                             }
                                         });

                    mITuyaActivator = TuyaHomeSdk.getActivatorInstance().newGwActivator(builder);
                    mITuyaActivator.start() ;
                    d.show().setOnDismissListener(dialogInterface -> mITuyaActivator.stop());
                }
                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    new MessageDialog(errorMsg,"error",act);
                }
            });
        });
    }

    public void renameWiredGateway(View view) {
        if (FOUND != null) {
            FOUND_D = TuyaHomeSdk.newDeviceInstance(FOUND.getDevId());
            FOUND_D.renameDevice(wireZbGatewayNewName.getText().toString(), new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    new MessageDialog(error+" "+code,"Failed",act);
                }
                @Override
                public void onSuccess() {
                    FOUND.setName(wireZbGatewayNewName.getText().toString());
                    foundWireZbGateway.setText(wireZbGatewayNewName.getText().toString());
                    foundWireZbGateway.setTextColor(Color.GREEN);
                }
            });
            Log.d("wiredNewName" , FOUND.name);
        }
        else {
            Toast.makeText( act,"Write New Device Name " , Toast.LENGTH_LONG).show();
        }
    }

    public void saveWiredGateway(View view) {
        if (!foundWireZbGateway.getText().toString().isEmpty()) {
            Room.setGATEWAY_B(FOUND);
            Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
            gateway.setText(getResources().getString(R.string.yes));
            gateway.setTextColor(Color.GREEN);
            Room.setZBGatewayStatus(String.valueOf(Room.id),"1",act);
            RoomDevices.add(FOUND);
            resetRoomDevices(act);
            Rooms.refreshSystem();
        }
    }

    //____________________________________________________


    // for Bluetooth Locks _______________________________

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
        LoadingDialog l = new LoadingDialog(act);
        TTLockClient.getDefault().startScanLock(new ScanLockCallback() {
            @Override
            public void onScanLockSuccess(ExtendedBluetoothDevice device)
            {
                l.stop();
                Rooms.CHANGE_STATUS = true ;
                FOUND_LOCK = device ;
                foundLock.setText(device.getName());
                TTLockClient.getDefault().stopScanLock();
                foundLockNewName.setText(MessageFormat.format("{0}Lock", Room.RoomNumber));
            }

            @Override
            public void onFail(LockError error) {
                l.stop();
            }
        });
    }

    public void saveLock(View view) {
        if (!FOUND_LOCK.getName().equals(Room.RoomNumber+"Lock")) {
            Toast.makeText(act,"Please Rename Lock First ",Toast.LENGTH_LONG).show();
        }
        else {
            TTLockClient.getDefault().initLock(FOUND_LOCK, new InitLockCallback()
            {
                @Override
                public void onInitLockSuccess(String lockData) {
                    //this must be done after lock is initialized,call server api to post to your server
                    Rooms.CHANGE_STATUS = true ;
                    if ( FeatureValueUtil.isSupportFeature(lockData, FeatureValue.NB_LOCK) )
                    {
                        setNBServerForNBLock( lockData);
                    }
                    else {
                        LoadingDialog l = new LoadingDialog(act);
                        String url = "";
                        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                            l.stop();
                            if (response.equals("1"))
                            {
                                Toast.makeText(act, "--lock is initialized success--", Toast.LENGTH_LONG).show();
                                lock.setTextColor(Color.GREEN);
                                lock.setText(getResources().getString(R.string.yes));
                                upload2Server(lockData);
                            }
                        }, error -> {
                            l.stop();
                            Toast.makeText(act, error.getMessage(), Toast.LENGTH_LONG).show();
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String,String> Params = new HashMap<>();
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
        if (FOUND_LOCK != null) {
            FOUND_LOCK.setName(Room.RoomNumber+"Lock");
            foundLock.setText(FOUND_LOCK.getName());
            foundLock.setTextColor(Color.GREEN);
        }
        else {
            Toast.makeText(act,"Found Lock Is Null",Toast.LENGTH_LONG).show();
        }
    }

    public static void setNBServerForNBLock(final String lockData) {
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
        String lockAlias = "MyTestLock" + c.get(Calendar.DAY_OF_MONTH);
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<ResponseBody> call = apiService.lockInit(ApiService.CLIENT_ID,Rooms.acc.getAccess_token() , lockData,lockAlias,System.currentTimeMillis());
        RetrofitAPIManager.enqueue(call, new TypeToken<LockInitResultObj>() {
        }, result -> {
            if (!result.success) {
                Toast.makeText(act, "-init fail-to server-", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(act, "--init lock success--", Toast.LENGTH_LONG).show();
        }, requestError -> Toast.makeText(act, requestError.getMessage()+"error", Toast.LENGTH_LONG).show());
    }

    static void resetRoomDevices(Activity act) {
        RoomDevices = ROOM.getRoomDevices(Room);
        setRoomDevices(act);
        RecyclerView devicesR = act.findViewById(R.id.devicesRecycler);
        GridLayoutManager manager = new GridLayoutManager(act,8);
        devicesR.setLayoutManager(manager);
        Device_Adapter adapter = new Device_Adapter(RoomDevices);
        devicesR.setAdapter(adapter);
    }

    //____________________________________________________

}