package com.example.hotelservicesstandalone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.Adapters.Devices_Adapter;
import com.example.hotelservicesstandalone.Adapters.Rooms_Adapter_Base;
import com.example.hotelservicesstandalone.Classes.DefaultExceptionHandler;
import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;
import com.example.hotelservicesstandalone.Classes.DevicesDataDB;
import com.example.hotelservicesstandalone.Classes.Interfaces.GerRoomsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetBuildingsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetDevicesCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetFloorsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.getDeviceDataCallback;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.LockDB;
import com.example.hotelservicesstandalone.Classes.PROJECT_VARIABLES;
import com.example.hotelservicesstandalone.Classes.Property.Building;
import com.example.hotelservicesstandalone.Classes.Property.Floor;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Tuya;
import com.example.hotelservicesstandalone.Dialogs.MessageDialog;
import com.example.hotelservicesstandalone.Dialogs.lodingDialog;
import com.example.hotelservicesstandalone.Interface.CreteMoodsCallBack;
import com.example.hotelservicesstandalone.Interface.RequestCallback;
import com.example.hotelservicesstandalone.Services.checkWorkingReceiver;
import com.example.hotelservicesstandalone.lock.AccountInfo;
import com.example.hotelservicesstandalone.lock.ApiService;
import com.example.hotelservicesstandalone.lock.GatewayObj;
import com.example.hotelservicesstandalone.lock.LockObj;
import com.example.hotelservicesstandalone.lock.MyApplication;
import com.example.hotelservicesstandalone.lock.RetrofitAPIManager;
import com.example.hotelservicesstandalone.lock.ServerError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.gateway.callback.ConnectCallback;
import com.ttlock.bl.sdk.gateway.callback.InitGatewayCallback;
import com.ttlock.bl.sdk.gateway.model.ConfigureGatewayInfo;
import com.ttlock.bl.sdk.gateway.model.DeviceInfo;
import com.ttlock.bl.sdk.gateway.model.GatewayError;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class Rooms extends AppCompatActivity {
    GridView devicesListView , roomsListView ;
    Activity act ;
    static ArrayList<LockObj> Locks ;
    public static AccountInfo acc;
    static List<CheckinDevice> Devices ;
    static FirebaseDatabase database ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    private LockDB lockDB ;
    Button toggle , resetDevices ;
    LinearLayout btnSLayout,mainLogo ;
    static RequestQueue MessagesQueue;
    static lodingDialog loading;
    static RequestQueue REQ, REQ1 , CLEANUP_QUEUE , LAUNDRY_QUEUE , CHECKOUT_QUEUE ,DND_Queue,FirebaseTokenRegister ;
    EditText searchText ;
    ExtendedBluetoothDevice TheFoundGateway ;
    private ConfigureGatewayInfo configureGatewayInfo;
    static List<SceneBean> SCENES ;
    static List<String> IMAGES ;
    static DatabaseReference ServerDevice , ProjectVariablesRef , DevicesControls , ProjectDevices  ;
    private final String projectLoginUrl = "users/loginProject" ;
    static List<String> DoorSensor_Open,DoorSensor_Close;
    LocalDataStore storage;
    DevicesDataDB db;
    boolean alarmSet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        Log.d("bootingOp","on create");
        setActivity();
        setActivityActions(act);
        getFirebaseTokenContinually();
        gettingAndPreparingData(act);
        setKeepAppAliveAlarm(act);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    private void setActivity() {
        act = this ;
        db = new DevicesDataDB(act);
        defineViews();
        defineRequestQueues();
        defineLists();
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        setFirebaseReferences();
        hideSystemUI();
        lockDevice();
        login();
        setServerDeviceRunningFunction();
        Log.d("bootingOp","set activity");
    }

    void setActivityActions(Activity act) {
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (devicesListView.getVisibility() == View.VISIBLE ) {
                    String Text = searchText.getText().toString();
                    if (Text.isEmpty()) {
                        Devices_Adapter adapter = new Devices_Adapter(Devices, act);
                        devicesListView.setAdapter(adapter);
                    }
                    else {
                        List<CheckinDevice> Results = new ArrayList<>();
                        for (int i = 0; i < Devices.size(); i++) {
                            if (Devices.get(i).getName().contains(Text)) {
                                Results.add(Devices.get(i));
                            }
                        }
                        Devices_Adapter adapter = new Devices_Adapter(Results, act);
                        devicesListView.setAdapter(adapter);
                    }
                }
            }
        });
        mainLogo.setOnLongClickListener(v -> {
            Dialog  dd = new Dialog(act);
            dd.setContentView(R.layout.lock_unlock_dialog);
            Window w = dd.getWindow();
            if (w != null) {
                w.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = dd.findViewById(R.id.messageDialog_ok);
            EditText password = dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(v1 -> dd.dismiss());
            lock.setOnClickListener(v12 -> {
                final lodingDialog loading = new lodingDialog(act);
                final String pass = password.getText().toString() ;
                StringRequest re = new StringRequest(Request.Method.POST, MyApp.My_PROJECT.url + projectLoginUrl, response -> {
                    Log.d("lockResp",response);
                    loading.stop();
                    try {
                        JSONObject resp = new JSONObject(response);
                        if (resp.getString("result").equals("success")) {
                            Toast.makeText(act, "Login Success", Toast.LENGTH_LONG).show();
                            lockDB.modifyValue("off");
                            roomsListView.setVisibility(View.VISIBLE);
                            devicesListView.setVisibility(View.GONE);
                            btnSLayout.setVisibility(View.VISIBLE);
                            mainLogo.setVisibility(View.GONE);
                            dd.dismiss();
                        } else {
                            Toast.makeText(act, "Login Failed " + resp.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.d("lockResp", Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(act, "Login Failed " + e, Toast.LENGTH_LONG).show();
                    }
                }, error -> {
                    loading.stop();
                    Log.d("lockResp",error.toString());
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> par = new HashMap<>();
                        par.put( "password" , pass ) ;
                        par.put( "project_name" , MyApp.My_PROJECT.projectName ) ;
                        return par;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            });
            dd.show();
            return false;
        });
    }

    void lockDevice() {
        lockDB = new LockDB(act);
        if (!lockDB.isLoggedIn()) {
            lockDB.removeAll();
            lockDB.insertLock("off");
        }
        if (lockDB.getLockValue().equals("off")) {
            roomsListView.setVisibility(View.VISIBLE);
            devicesListView.setVisibility(View.GONE);
            btnSLayout.setVisibility(View.VISIBLE);
            mainLogo.setVisibility(View.GONE);
        }
        else if (lockDB.getLockValue().equals("on")) {
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnSLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
        else {
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnSLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
    }

    void setFirebaseReferences() {
        database = FirebaseDatabase.getInstance(MyApp.firebaseDBUrl);//https://hotelservices-ebe66.firebaseio.com/
        ServerDevice = database.getReference(MyApp.My_PROJECT.projectName+"ServerDevices/"+MyApp.controlDeviceMe.name);
        ProjectVariablesRef = database.getReference(MyApp.My_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.My_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.My_PROJECT.projectName+"Devices");
    }

    void defineViews() {
        searchText = findViewById(R.id.search_text);
        toggle = findViewById(R.id.button9);
        mainLogo = findViewById(R.id.logoLyout) ;
        resetDevices = findViewById(R.id.button2);
        btnSLayout = findViewById(R.id.btnsLayout);
        roomsListView = findViewById(R.id.RoomsListView);
        devicesListView = findViewById(R.id.DevicesListView);
        TextView hotelName = findViewById(R.id.hotelName);
        hotelName.setText(MyApp.My_PROJECT.projectName);
        TextView ProjectName = findViewById(R.id.textView6);
        String x = "Project "+MyApp.My_PROJECT.projectName+" is Running";
        ProjectName.setText(x);
        mainLogo.setVisibility(View.GONE);
        roomsListView.setVisibility(View.VISIBLE);
        devicesListView.setVisibility(View.GONE);
        searchText.setVisibility(View.GONE);
        setActionText("Welcome",act);
        storage = new LocalDataStore();
    }

    void defineRequestQueues() {
        REQ = Volley.newRequestQueue(act);
        REQ1 = Volley.newRequestQueue(act);
        CLEANUP_QUEUE = Volley.newRequestQueue(act);
        LAUNDRY_QUEUE = Volley.newRequestQueue(act);
        CHECKOUT_QUEUE = Volley.newRequestQueue(act);
        DND_Queue = Volley.newRequestQueue(act);
        MessagesQueue = Volley.newRequestQueue(act);
    }

    void defineLists() {
        SCENES = new ArrayList<>();
        Locks = new ArrayList<>();
        Devices = new ArrayList<>();
        configureGatewayInfo = new ConfigureGatewayInfo();
        DoorSensor_Open = new ArrayList<>();
        DoorSensor_Open.add("open");
        DoorSensor_Open.add("true");
        DoorSensor_Close = new ArrayList<>();
        DoorSensor_Close.add("closed"); DoorSensor_Close.add("false"); DoorSensor_Close.add("close");
    }
    //_________________________________________________________

    void gettingAndPreparingData(Activity act) {
        Log.d("bootingOp","getting data");
        loading = new lodingDialog(act);
        PROJECT_VARIABLES.getProjectVariables(REQ,new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootingOp","variables done");
                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
                Building.getBuildings(REQ, new GetBuildingsCallback() {
                    @Override
                    public void onSuccess(List<Building> buildings) {
                        Log.d("bootingOp","buildings done "+buildings.size());
                        MyApp.Buildings = buildings;
                        Floor.getFloors(REQ, new GetFloorsCallback() {
                            @Override
                            public void onSuccess(List<Floor> floors) {
                                Log.d("bootingOp","floors done "+floors.size());
                                MyApp.Floors = floors;
                                MyApp.controlDeviceMe.getMyRooms(ServerDevice,REQ, new GerRoomsCallback() {
                                    @Override
                                    public void onSuccess(List<Room> rooms) {
                                        Log.d("bootingOp","rooms done "+rooms.size());
                                        MyApp.ROOMS = rooms;
                                        Room.sortRoomsByNumber(MyApp.ROOMS);
                                        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
                                        Room.setRoomsFireRooms(MyApp.ROOMS,database);
                                        TextView hotelName = act.findViewById(R.id.hotelName);
                                        hotelName.setText(MessageFormat.format("{0} {1} room", MyApp.My_PROJECT.projectName, MyApp.ROOMS.size()));
                                        Tuya.loginTuya(MyApp.My_PROJECT, new ILoginCallback() {
                                            @Override
                                            public void onSuccess(User user) {
                                                Log.d("bootingOp","tuya login done");
                                                MyApp.TuyaUser = user;
                                                Tuya.getProjectHomes(MyApp.My_PROJECT, new ITuyaGetHomeListCallback() {
                                                    @Override
                                                    public void onSuccess(List<HomeBean> homeBeans) {
                                                        Log.d("bootingOp","tuya project homes done "+homeBeans.size());
                                                        MyApp.PROJECT_HOMES = homeBeans;
                                                        Tuya.getDevices(homeBeans,MyApp.ROOMS, new GetDevicesCallback() {
                                                            @Override
                                                            public void devices(List<CheckinDevice> devices) {
                                                                Log.d("bootingOp","tuya devices done "+devices.size());
                                                                Devices = devices;
                                                                Tuya.gettingInitialDevicesData(act,Devices,db, new getDeviceDataCallback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("bootingOp","getting initial done");
                                                                        for (String s :Tuya.devicesIds) {
                                                                            Log.d("devicesIds",s);
                                                                        }
                                                                        Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
                                                                        settingInitialDevicesData(Devices);
                                                                        TextView actionsNow = act.findViewById(R.id.textView26);
                                                                        showRooms(act);
                                                                        showDevices(act);
                                                                        setAllListeners(actionsNow);
                                                                        getSceneBGs();
                                                                        loading.stop();
                                                                        Tuya.setDevicesListenersWatcher(act);
                                                                        Log.d("bootingOp","finish");
                                                                    }

                                                                    @Override
                                                                    public void onError(String error) {
                                                                        loading.stop();
                                                                        createRestartConfirmationDialog(act,"getting devices failed \n"+error);
                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onError(String error) {
                                                                loading.stop();
                                                                createRestartConfirmationDialog(act,"getting devices failed \n"+error);
                                                            }
                                                        });
//                                                        boolean firstRun = storage.getBoolean("firstRun");
//                                                        if (firstRun) {
//                                                            Tuya.getDevices(homeBeans,MyApp.ROOMS, new GetDevicesCallback() {
//                                                                @Override
//                                                                public void devices(List<CheckinDevice> devices) {
//                                                                    Log.d("bootingOp","tuya devices done "+devices.size());
//                                                                    Devices = devices;
//
//                                                                    Tuya.gettingInitialDevicesData(act,Devices,db, new getDeviceDataCallback() {
//                                                                        @Override
//                                                                        public void onSuccess() {
//                                                                            Log.d("bootingOp","getting initial done");
//                                                                            for (String s :Tuya.devicesIds) {
//                                                                                Log.d("devicesIds",s);
//                                                                            }
//                                                                            Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
//                                                                            settingInitialDevicesData(Devices);
//                                                                            TextView actionsNow = act.findViewById(R.id.textView26);
//                                                                            showRooms(act);
//                                                                            showDevices(act);
//                                                                            setAllListeners(actionsNow);
//                                                                            getSceneBGs();
//                                                                            loading.stop();
//                                                                            Tuya.setDevicesListenersWatcher(act);
//                                                                            Log.d("bootingOp","finish");
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onError(String error) {
//                                                                            loading.stop();
//                                                                            createRestartConfirmationDialog(act,"getting devices failed \n"+error);
//                                                                        }
//                                                                    });
//
//                                                                }
//
//                                                                @Override
//                                                                public void onError(String error) {
//                                                                    loading.stop();
//                                                                    createRestartConfirmationDialog(act,"getting devices failed \n"+error);
//                                                                }
//                                                            });
//                                                        }
//                                                        else {
//                                                            Tuya.getLocalDevices(homeBeans, MyApp.ROOMS, new ArrayList<>(), new GetDevicesCallback() {
//                                                                @Override
//                                                                public void devices(List<CheckinDevice> devices) {
//                                                                    Log.d("bootingOp","tuya devices done locally "+devices.size());
//                                                                    Devices = devices;
//
//                                                                    Tuya.gettingInitialDevicesData(act,Devices,db, new getDeviceDataCallback() {
//                                                                        @Override
//                                                                        public void onSuccess() {
//                                                                            Log.d("bootingOp","getting initial done");
//                                                                            for (String s :Tuya.devicesIds) {
//                                                                                Log.d("devicesIds",s);
//                                                                            }
//                                                                            Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
//                                                                            settingInitialDevicesData(Devices);
//                                                                            TextView actionsNow = act.findViewById(R.id.textView26);
//                                                                            showRooms(act);
//                                                                            showDevices(act);
//                                                                            setAllListeners(actionsNow);
//                                                                            getSceneBGs();
//                                                                            loading.stop();
//                                                                            Tuya.setDevicesListenersWatcher(act);
//                                                                            Log.d("bootingOp","finish");
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onError(String error) {
//                                                                            loading.stop();
//                                                                            createRestartConfirmationDialog(act,"getting devices failed \n"+error);
//                                                                        }
//                                                                    });
//                                                                }
//
//                                                                @Override
//                                                                public void onError(String error) {
//
//                                                                }
//                                                            });
//                                                        }
                                                    }

                                                    @Override
                                                    public void onError(String errorCode, String error) {
                                                        loading.stop();
                                                        createRestartConfirmationDialog(act,"getting homes failed \n"+error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String code, String error) {
                                                loading.stop();
                                                createRestartConfirmationDialog(act,"login tuya failed \n"+error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        loading.stop();
                                        createRestartConfirmationDialog(act,"getting rooms failed \n"+error);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                loading.stop();
                                createRestartConfirmationDialog(act,"getting floors failed \n"+error);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        loading.stop();
                        createRestartConfirmationDialog(act,"getting buildings failed \n"+error);
                    }
                });
            }

            @Override
            public void onFail(String error) {
                loading.stop();
                createRestartConfirmationDialog(act,"getting project variables failed \n"+error);
            }
        });
    }

    static void setAllListeners(TextView actionsNow) {
        Log.d("bootingOp","setting listeners");
        Room.setRoomsDevicesListener(MyApp.ROOMS,actionsNow,CLEANUP_QUEUE,LAUNDRY_QUEUE,CHECKOUT_QUEUE);
        Room.setRoomsFireRoomsListener(MyApp.ROOMS);
        Room.setRoomsFireRoomsDevicesControlListener(MyApp.ROOMS);
    }

    static void showRooms(Activity act) {
        GridView roomsListView = act.findViewById(R.id.RoomsListView);
        Rooms_Adapter_Base adapterRooms = new Rooms_Adapter_Base(MyApp.ROOMS,act);
        act.runOnUiThread(() -> roomsListView.setAdapter(adapterRooms));
    }

    static void showDevices(Activity act) {
        GridView devicesListView = act.findViewById(R.id.DevicesListView);
        Devices_Adapter adapterDevices = new Devices_Adapter(Devices,act);
        act.runOnUiThread(() -> devicesListView.setAdapter(adapterDevices));
        devicesListView.setAdapter(adapterDevices);
    }

    void sendRegistrationToServer(String token) {
        String url = MyApp.My_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseToken" ;
        StringRequest re  = new StringRequest(Request.Method.POST, url, response -> Log.d("tokenRegister" , response), error -> Log.d("tokenRegister" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> par = new HashMap<>();
                par.put("token" , token);
                par.put("device_id", String.valueOf(MyApp.controlDeviceMe.id));
                return par;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(re);
    }

    public void toggleRoomsDevices(View view) {
        hideSystemUI();
        if (roomsListView.getVisibility() == View.VISIBLE) {
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.VISIBLE);
            searchText.setVisibility(View.VISIBLE);
            Button b = (Button) view;
            b.setText(getResources().getString(R.string.rooms));
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.ic_baseline_bedroom_child_24,null);
            b.setCompoundDrawablesWithIntrinsicBounds(null,null,d,null);
        }
        else if (roomsListView.getVisibility() == View.GONE) {
            roomsListView.setVisibility(View.VISIBLE);
            devicesListView.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
            Button b = (Button) view;
            b.setText(getResources().getString(R.string.devices));
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.ic_baseline_podcasts_24,null);
            b.setCompoundDrawablesWithIntrinsicBounds(null,null,d,null);
        }
    }

    public void lockAndUnlock(View view) {
        hideSystemUI();
         if (lockDB.getLockValue().equals("off")) {
            lockDB.modifyValue("on");
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnSLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
        else {
            lockDB.modifyValue("off");
            roomsListView.setVisibility(View.VISIBLE);
            devicesListView.setVisibility(View.GONE);
            btnSLayout.setVisibility(View.VISIBLE);
            mainLogo.setVisibility(View.GONE);
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void scanLockGateway(View view) {
        int REQUEST_PERMISSION_REQ_CODE = 18 ;
        if (act.checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
        getScanGatewayCallback();
    }

    private void getScanGatewayCallback() {
    }

    public void initLockGateway(View view) {

        if (TheFoundGateway == null ) {
            Log.d("lockGateway","no");
        }
        else {
                GatewayClient.getDefault().connectGateway(TheFoundGateway, new ConnectCallback() {
                    @Override
                    public void onConnectSuccess(ExtendedBluetoothDevice device) {
                        Toast.makeText(act,"gateway connected",Toast.LENGTH_LONG).show();
                        EditText wifiName = findViewById(R.id.wifiName);
                        EditText wifiPassword = findViewById(R.id.wifiPassword);
                        configureGatewayInfo.uid = acc.getUid();
                        configureGatewayInfo.userPwd = acc.getMd5Pwd();
                        configureGatewayInfo.ssid = wifiName.getText().toString().trim();
                        configureGatewayInfo.wifiPwd = wifiPassword.getText().toString().trim();
                        configureGatewayInfo.plugName = device.getAddress();
                        GatewayClient.getDefault().initGateway(configureGatewayInfo, new InitGatewayCallback() {
                            @Override
                            public void onInitGatewaySuccess(DeviceInfo deviceInfo) {
                                Toast.makeText(act,"gateway init done",Toast.LENGTH_LONG).show();
                                isInitSuccess(deviceInfo);
                            }

                            @Override
                            public void onFail(GatewayError error) {
                                Toast.makeText(act,error.getDescription(),Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(act, "gateway_out_of_time", Toast.LENGTH_LONG).show();
                    }

                });


        }
    }

    private void isInitSuccess(DeviceInfo deviceInfo) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.gatewayIsInitSuccess(ApiService.CLIENT_ID, MyApplication.getmInstance().getAccountInfo().getAccess_token(), TheFoundGateway.getAddress(), System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    GatewayObj gatewayObj = GsonUtil.toObject(json, GatewayObj.class);
                    if (gatewayObj.errcode == 0) {
                        Toast.makeText(act, "init success", Toast.LENGTH_LONG).show();
                        uploadGatewayDetail(deviceInfo, gatewayObj.getGatewayId());
                    }
                    else Toast.makeText(act,gatewayObj.errmsg,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(act,t.getMessage(),Toast.LENGTH_LONG).show();
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    private void uploadGatewayDetail(DeviceInfo deviceInfo, int gatewayId) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        EditText wifiName = findViewById(R.id.wifiName);
        Call<String> call = apiService.uploadGatewayDetail(ApiService.CLIENT_ID, MyApplication.getmInstance().getAccountInfo().getAccess_token(), gatewayId, deviceInfo.getModelNum(), deviceInfo.hardwareRevision, deviceInfo.getFirmwareRevision(), wifiName.getText().toString(), System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    ServerError error = GsonUtil.toObject(json, ServerError.class);
                    if (error.errcode == 0)
                        Toast.makeText(act,"Done",Toast.LENGTH_LONG).show();
                    else Toast.makeText(act,error.errmsg,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(act,t.getMessage(),Toast.LENGTH_LONG).show();
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    static void getScenes() {
        SCENES.clear();
        final int[] ind = {0};
        for (int i=0;i<MyApp.PROJECT_HOMES.size();i++) {
            HomeBean h = MyApp.PROJECT_HOMES.get(i);
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    getHomeScenes(h, new CreteMoodsCallBack() {
                        @Override
                        public void onSuccess(List<SceneBean> moods) {
                            ind[0]++;
                            SCENES.addAll(moods);
                            if (ind[0] == MyApp.PROJECT_HOMES.size()) {
                                Log.d("scenesAre","total: "+SCENES.size());
                                //TODO service scenario making check
                            }
                        }

                        @Override
                        public void onFail(String error) {

                        }
                    });
                }
            },(long) i * 5 * 1000);

        }
    }

    static void getHomeScenes(HomeBean h, CreteMoodsCallBack callBack) {
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(h.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                callBack.onSuccess(result);
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                callBack.onFail(errorCode+" "+errorMessage);
            }
        });
    }

    static void getSceneBGs() {
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

    public void goToLocks(View view) {
        Intent i = new Intent(act,Locks.class);
        startActivity(i);
    }

    public void logOut(View view) {
        AlertDialog.Builder b = new AlertDialog.Builder(act);
        b
                .setTitle("Are you sure .?")
                .setMessage("Are you sure to log out ")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    clearData();
                    loading = new lodingDialog(act);
                    String url = MyApp.My_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseStatus";
                    StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                        Log.d("changeDeviceStatus" , response);
                        dialogInterface.dismiss();
                        loading.stop();
                        SharedPreferences.Editor editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
                        editor.putString("projectName" , null);
                        editor.putString("tuyaUser" , null);
                        editor.putString("tuyaPassword" , null);
                        editor.putString("lockUser" , null);
                        editor.putString("lockPassword" , null);
                        editor.apply();
                        Intent i1 = new Intent(act,Login.class);
                        act.startActivity(i1);
                        act.finish();
                    }, error -> {
                        Log.d("changeDeviceStatus" , error.toString());
                        loading.stop();
                        new MessageDialog(error.toString(),"error",act);
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("device_id", String.valueOf(MyApp.controlDeviceMe.id));
                            params.put("status","0");
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(req);
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("Delete Device", (dialogInterface, i) -> {
                    loading = new lodingDialog(act);
                    MyApp.controlDeviceMe.deleteControlDevice(REQ, new RequestCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("deleteServer","done");
                            loading.stop();
                            dialogInterface.dismiss();
                            storage.deleteControlDevice();
                            startActivity(new Intent(act,Login.class));
                            act.finish();
                        }

                        @Override
                        public void onFail(String error) {
                            Log.d("deleteServer","error "+error);
                            loading.stop();
                            new MessageDialog(error,"error",act);
                        }
                    });
                })
                .create().show();
    }

    public void login() {
        String url = MyApp.My_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseStatus";
        StringRequest req = new StringRequest(Request.Method.POST, url, response -> Log.d("changeDeviceStatus" , response), error -> {
            Log.d("changeDeviceStatus" , error.toString());
            loading.stop();
            new MessageDialog(error.toString(),"error",act);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", String.valueOf(MyApp.controlDeviceMe.id));
                params.put("status","1");
                return params;
            }
        };
        REQ.add(req);
    }

    static void setActionText(String action,Activity act) {
        int month = Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH) + 1 ;
        String time = Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR)+"-"+month+"-"+Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH)
                +" "+Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance(Locale.getDefault()).get(Calendar.MINUTE);
        TextView actionsNow = act.findViewById(R.id.textView26);
        actionsNow.setText(MessageFormat.format("{0}-{1}",action,time));
    }

    void setServerDeviceRunningFunction() {
        Handler h = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                h.postDelayed(this,1000*60);
                long x = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                ServerDevice.child("working").setValue(x);
            }
        };
        r.run();
    }

    void getFirebaseTokenContinually() {
        Log.d("tokenRegister" , "start");
        Timer t = new Timer() ;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("tokenRegister" , "run");
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("tokenRegister" , "task null "+task.getException().getMessage());
                        return;
                    }
                    String token = task.getResult();
                    sendRegistrationToServer(token);
                    Log.d("tokenRegister" , "run finish");
                });
            }
        }, 1000*60*60*24,1000*60*60*24);
    }

    void createRestartConfirmationDialog(Activity act,String message) {
        AlertDialog.Builder B = new AlertDialog.Builder(act);
        B.setTitle("Restart..?");
        B.setMessage(message);
        Dialog b = B.create();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                b.dismiss();
                gettingAndPreparingData(act);
            }
        },2000);
        act.runOnUiThread(b::show);
    }
    private void clearData() {
        try {
            ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        } catch (Exception e) {
            Log.d("clearDataError", Objects.requireNonNull(e.getMessage()));
        }
    }


    void settingInitialDevicesData(List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            cd.setInitialCurrentValues();
        }
    }

    void setKeepAppAliveAlarm(Context context) {
        if (!alarmSet) {
            Intent intent = new Intent(getApplicationContext(), checkWorkingReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000*60) , pendingIntent);
            Log.d("workingAlarm","alarm set");
            alarmSet = true;
        }
    }
}