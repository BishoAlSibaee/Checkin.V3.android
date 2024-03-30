package com.example.hotelservicesstandalone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.example.hotelservicesstandalone.Interface.CreteMoodsCallBack;
import com.example.hotelservicesstandalone.Interface.RequestCallback;
import com.example.hotelservicesstandalone.lock.AccountInfo;
import com.example.hotelservicesstandalone.lock.ApiService;
import com.example.hotelservicesstandalone.lock.GatewayObj;
import com.example.hotelservicesstandalone.lock.LockObj;
import com.example.hotelservicesstandalone.lock.MyApplication;
import com.example.hotelservicesstandalone.lock.RetrofitAPIManager;
import com.example.hotelservicesstandalone.lock.ServerError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.gateway.callback.ConnectCallback;
import com.ttlock.bl.sdk.gateway.callback.InitGatewayCallback;
import com.ttlock.bl.sdk.gateway.model.ConfigureGatewayInfo;
import com.ttlock.bl.sdk.gateway.model.DeviceInfo;
import com.ttlock.bl.sdk.gateway.model.GatewayError;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.TYDevicePublishModeEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
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
    static List<ROOM> ROOMS;
    static final String getRoomsUrl = MyApp.THE_PROJECT.url + "roomsManagement/getRoomsForControllDevice" ;
    Activity act ;
    static ArrayList<LockObj> Locks ;
    static public AccountInfo accountInfo;
    public static AccountInfo acc;
    static List<DeviceBean> Devices ;
    static FirebaseDatabase database ;
    static boolean[] CLEANUP , LAUNDRY , DND , CHECKOUT ;
    static Runnable[] TempRunnableList, DoorRunnable ;
    static Handler[] DoorsHandlers,AcHandlers;
    static boolean[] AC_SENARIO_Status , DOOR_STATUS;
    static long[] AC_Start, AC_Period, Door_Start, Door_Period;
    static String[] Client_Temp, TempSetPoint ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    static int checkInModeTime = 0 ;
    static int checkOutModeTime = 0 ;
    private LockDB lockDB ;
    Button toggle , resetDevices ;
    LinearLayout btnSLayout,mainLogo ;
    private static List<ServiceEmps> EmpS;
    private DatabaseReference ServiceUsers ;
    static RequestQueue MessagesQueue;
    static boolean CHANGE_STATUS = false ;
    static lodingDialog loading;
    static RequestQueue REQ, REQ1 , CLEANUP_QUEUE , LAUNDRY_QUEUE , CHECKOUT_QUEUE ,DND_Queue,FirebaseTokenRegister ;
    EditText searchText ;
    ExtendedBluetoothDevice TheFoundGateway ;
    private ConfigureGatewayInfo configureGatewayInfo;
    static List<SceneBean> SCENES ;
    static List<String> IMAGES ;
    static DatabaseReference ServerDevice , ProjectVariablesRef , DevicesControls , ProjectDevices  ;
    static int addCleanupCounter=1,cancelOrderCounter=1,addLaundryCounter =1,addCheckoutCounter=1,addDNDCounter=1,cancelDNDCounter = 1 ;
    static String PowerUnInstalled,PowerInstalled,GatewayUnInstalled,GatewayInstalled,MotionUnInstalled,MotionInstalled,DoorUnInstalled,DoorInstalled,ServiceUnInstalled,ServiceInstalled,S1UnInstalled,S1Installed,S2UnInstalled,S2Installed,S3UnInstalled,S3Installed,S4UnInstalled,S4Installed,S5UnInstalled,S5Installed,S6UnInstalled,S6Installed,S7UnInstalled,S7Installed,S8UnInstalled,S8Installed,ACUnInstalled,ACInstalled,CurtainUnInstalled,CurtainInstalled,LockUnInstalled,LockInstalled;
    static long refreshSystemTime = 12 ;
    private final String projectLoginUrl = "users/loginProject" ;
    static List<DatabaseReference_ValueEventListener> RoomsDevicesReferencesListeners;
    static List<String> DoorSensor_Open,DoorSensor_Close;


    // boot functions
    //_________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        setActivity();
        setActivityActions(act);
        getFirebaseTokenContinually();
        getServiceUsersFromFirebase();
        gettingAndPreparingData(act);
        hideSystemUI();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        act.startLockTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    private void setActivity() {
        act = this ;
        MyApp.Activities.add(act);
        defineViews();
        defineRequestQueues();
        defineLists();
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        setFirebaseReferences();
        hideSystemUI();
        lockDevice();
        login();
        setServerDeviceRunningFunction();
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
                        List<DeviceBean> Results = new ArrayList<>();
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
            w.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = dd.findViewById(R.id.messageDialog_ok);
            EditText password = dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(v1 -> dd.dismiss());
            lock.setOnClickListener(v12 -> {
                final lodingDialog loading = new lodingDialog(act);
                final String pass = password.getText().toString() ;
                StringRequest re = new StringRequest(Request.Method.POST, MyApp.THE_PROJECT.url + projectLoginUrl, response -> {
                    Log.d("lockResp",response);
                    loading.stop();
                    if (response != null) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            if (resp.getString("result").equals("success")) {
                                Toast.makeText(act,"Login Success",Toast.LENGTH_LONG).show();
                                lockDB.modifyValue("off");
                                roomsListView.setVisibility(View.VISIBLE);
                                devicesListView.setVisibility(View.GONE);
                                btnSLayout.setVisibility(View.VISIBLE);
                                mainLogo.setVisibility(View.GONE);
                                dd.dismiss();
                            }
                            else {
                                Toast.makeText(act,"Login Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.d("lockResp",e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(act,"Login Failed " + e,Toast.LENGTH_LONG).show();
                        }
                    }
                }, error -> {
                    loading.stop();
                    Log.d("lockResp",error.toString());
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> par = new HashMap<>();
                        par.put( "password" , pass ) ;
                        par.put( "project_name" , MyApp.THE_PROJECT.projectName ) ;
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
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app");//https://hotelservices-ebe66.firebaseio.com/
        ServerDevice = database.getReference(MyApp.THE_PROJECT.projectName+"ServerDevices/"+MyApp.Device_Name);
        ServiceUsers = database.getReference(MyApp.THE_PROJECT.projectName+"ServiceUsers");
        ProjectVariablesRef = database.getReference(MyApp.THE_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.THE_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.THE_PROJECT.projectName+"Devices");
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
        hotelName.setText(MyApp.THE_PROJECT.projectName);
        TextView ProjectName = findViewById(R.id.textView6);
        String x = "Project "+MyApp.THE_PROJECT.projectName+" is Running";
        ProjectName.setText(x);
        mainLogo.setVisibility(View.GONE);
        roomsListView.setVisibility(View.VISIBLE);
        devicesListView.setVisibility(View.GONE);
        searchText.setVisibility(View.GONE);
        setActionText("Welcome",act);
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
        EmpS = new ArrayList<>();
        ROOMS = new ArrayList<>();
        Locks = new ArrayList<>();
        Devices = new ArrayList<>();
        RoomsDevicesReferencesListeners = new ArrayList<>();
        configureGatewayInfo = new ConfigureGatewayInfo();
        DoorSensor_Open = new ArrayList<>();
        DoorSensor_Open.add("open"); DoorSensor_Open.add("true");
        DoorSensor_Close = new ArrayList<>();
        DoorSensor_Close.add("closed"); DoorSensor_Close.add("false"); DoorSensor_Close.add("close");
    }
    //_________________________________________________________

    // getting and setting data functions
    // 1
    //_________________________________________________________
    static void gettingAndPreparingData(Activity act) {
        loading = new lodingDialog(act);
        getProjectVariables(act,new RequestCallback() {
            @Override
            public void onSuccess() {
                setControlDeviceListener(act);
            }

            @Override
            public void onFail(String error) {
                loading.stop();
                createRestartConfirmationDialog(act,"getting project variables failed \n"+error);
            }
        });
    }

    // 2
    static void getProjectVariables(Activity act,RequestCallback callback) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/getProjectVariables";
        StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("gettingFirebase",response);
            try {
                JSONObject row = new JSONObject(response);
                JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                MyApp.ProjectVariables = new PROJECT_VARIABLES(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                MyApp.checkInActions = new CheckInActions(MyApp.ProjectVariables.CheckinActions);
                MyApp.checkOutActions = new CheckoutActions(MyApp.ProjectVariables.CheckoutActions);
                MyApp.clientBackActions = new ClientBackActions(MyApp.ProjectVariables.OnClientBack);
                setProjectVariablesListener(act);
                callback.onSuccess();
            }
            catch (JSONException e) {
                callback.onFail(e.getMessage());

            }
        }, error -> callback.onFail(error.toString()));
        REQ.add(re);
    }

    // 3
    static void setControlDeviceListener(Activity act) {
        ServerDevice.child("roomsIds").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Log.d("rerunProblem", Objects.requireNonNull(snapshot.getValue()).toString());
                        removeSystemListeners();
                        getRooms(act,new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                getTuyaDevices(act,new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        showRooms(act);
                                        showDevices(act);
                                        setAllListeners(act);
                                        getSceneBGs();
                                        setAcScenario();
                                        loading.stop();
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        loading.stop();
                                        createRestartConfirmationDialog(act,"getting devices failed \n"+error);
                                    }
                                });
                                loginTTLock(act,new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        getLocks(new RequestCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFail(String error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }

                            @Override
                            public void onFail(String error) {
                                loading.stop();
                                createRestartConfirmationDialog(act,"getting rooms failed \n"+error);
                            }
                        });
                    }
                    else {
                        loading.stop();
                        createRestartConfirmationDialog(act,"device listener null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loading.stop();
                    createRestartConfirmationDialog(act,"set device control failed \n"+error);
                }
            });
    }

    // 4
    static void getRooms(Activity act,RequestCallback callback) {
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, response -> {
            if (response.equals("0")) {
                callback.onFail("no rooms");
            }
            try {
                JSONObject resS = new JSONObject(response);
                if (resS.getString("result").equals("success")) {
                    JSONArray arr = resS.getJSONArray("rooms");
                    ROOMS.clear();
                    for (int i=0;i<arr.length();i++) {
                        JSONObject row = arr.getJSONObject(i);
                        int id = row.getInt("id");
                        int roomNumber = row.getInt("RoomNumber");
                        int status = row.getInt("Status");
                        int hotel = row.getInt("hotel");
                        int building = row.getInt("Building");
                        int building_id = row.getInt("building_id");
                        int floor = row.getInt("Floor");
                        int floor_id = row.getInt("floor_id");
                        String roomType = row.getString("RoomType");
                        int suiteStatus = row.getInt("SuiteStatus");
                        int suiteNumber = row.getInt("SuiteNumber");
                        int suiteId = row.getInt("SuiteId");
                        int reservationNumber = row.getInt("ReservationNumber");
                        int roomStatus = row.getInt("roomStatus");
                        int clientIn = row.getInt("ClientIn");
                        String message = row.getString("message");
                        int selected = row.getInt("selected");
                        int load = row.getInt("loading");
                        int tablet = row.getInt("Tablet");
                        String dep = row.getString("dep");
                        int cleanup = row.getInt("Cleanup");
                        int laundry = row.getInt("Laundry");
                        int roomService = row.getInt("RoomService");
                        String roomServiceText = row.getString("RoomServiceText");
                        int checkout = row.getInt("Checkout");
                        int restaurant = row.getInt("Restaurant");
                        int miniBarCheck = row.getInt("MiniBarCheck");
                        int facility = row.getInt("Facility");
                        int SOS = row.getInt("SOS");
                        int DND = row.getInt("DND");
                        int powerSwitch = row.getInt("PowerSwitch");
                        int doorSensor = row.getInt("DoorSensor");
                        int motionSensor = row.getInt("MotionSensor");
                        int thermostat = row.getInt("Thermostat");
                        int ZBGateway = row.getInt("ZBGateway");
                        int online = row.getInt("online");
                        int curtainSwitch = row.getInt("CurtainSwitch");
                        int serviceSwitch = row.getInt("ServiceSwitch");
                        int lock = row.getInt("lock");
                        int switch1 = row.getInt("Switch1");
                        int switch2 = row.getInt("Switch2");
                        int switch3 = row.getInt("Switch3");
                        int switch4 = row.getInt("Switch4");
                        int switch5 = row.getInt("Switch5");
                        int switch6 = row.getInt("Switch6");
                        int switch7 = row.getInt("Switch7");
                        int switch8 = row.getInt("Switch8");
                        String lockGateway = row.getString("LockGateway");
                        String lockName = row.getString("LockName");
                        int powerStatus = row.getInt("powerStatus");
                        int curtainStatus = row.getInt("curtainStatus");
                        int doorStatus = row.getInt("doorStatus");
                        int doorWarning = row.getInt("DoorWarning");
                        int temp = row.getInt("temp");
                        int tempSetPoint = row.getInt("TempSetPoint");
                        int setPointInterval = row.getInt("SetPointInterval");
                        String welcomeMessage = row.getString("WelcomeMessage");
                        String logo = row.getString("Logo");
                        String token =row.getString("token");
                        ROOM room = new ROOM(id,roomNumber,status,hotel,building,building_id,floor,floor_id,roomType,suiteStatus,suiteNumber,suiteId,reservationNumber,roomStatus,clientIn,message,selected,load,tablet,dep,cleanup,laundry
                                ,roomService,roomServiceText,checkout,restaurant,miniBarCheck,facility,SOS,DND,powerSwitch,doorSensor,motionSensor,thermostat,ZBGateway,online,curtainSwitch,serviceSwitch,lock,switch1,switch2,switch3,switch4,switch5,switch6,switch7,switch8,lockGateway
                                ,lockName,powerStatus,curtainStatus,doorStatus,doorWarning,temp,tempSetPoint,setPointInterval,checkInModeTime,checkOutModeTime,welcomeMessage,logo,token);
                        room.setFireRoom(database.getReference(MyApp.THE_PROJECT.projectName+"/B"+room.Building+"/F"+room.Floor+"/R"+room.RoomNumber));
                        ROOMS.add(room);
                    }
                }
                else {
                    callback.onFail("getting rooms failed "+resS.getString("error"));
                }
            }
            catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
            ROOM.sortRoomsByNumber(ROOMS);
            MyApp.ROOMS = ROOMS ;
            TextView hotelName = act.findViewById(R.id.hotelName);
            hotelName.setText(MyApp.THE_PROJECT.projectName);
            hotelName.setText(MessageFormat.format("{0} {1} room", MyApp.THE_PROJECT.projectName, ROOMS.size()));
            defineVariables();
            callback.onSuccess();
        }, error -> callback.onFail(error.toString())){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id",MyApp.Device_Id);
                return params;
            }
        };
        REQ.add(re);
    }

    // 5
    static void getTuyaDevices(Activity act,RequestCallback callback) {
        Log.d("getDevicesRun","started");
        Devices.clear();
        final int[] counter = {0};
        for (int i = 0; i < MyApp.PROJECT_HOMES.size();i++) {
            Log.d("getDevicesRun","number "+i);
            HomeBean h = MyApp.PROJECT_HOMES.get(i);
            Timer t = new Timer();
            int finalI = i;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("getDevicesRun","number "+ finalI +" done "+h.getName());
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            for (DeviceBean d : bean.getDeviceList()) {
                                if (MyApp.searchDeviceInList(Devices,d.devId) == null) {
                                    Devices.add(d);
                                }
                            }
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                setRoomsDevices(Devices,h);
                                setRoomsDevices(act);
                                callback.onSuccess();
                                Log.d("getDevicesRun","finish "+Devices.size());
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onFail(errorCode+" "+errorMsg);
                        }
                    });
                }
            }, (long) i * 3 * 1000);

        }
    }

    //6
    static void setAllListeners(Activity act) {
        setDevicesListeners(act);
        setFireRoomsListener(act);
    }
    //_________________________________________________________

    // getting and setting helpers functions
    //_________________________________________________________

    static void setProjectVariablesListener(Activity act) {
        final int[] first = {0};
        ProjectVariablesRef.child("CheckinModeActive").setValue(MyApp.ProjectVariables.CheckinModeActive);
        ProjectVariablesRef.child("CheckinModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinModeActive = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckoutModeActive").setValue(MyApp.ProjectVariables.CheckoutModeActive);
        ProjectVariablesRef.child("CheckoutModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutModeActive = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("ACSenarioActive").setValue(MyApp.ProjectVariables.ACSenarioActive);
        ProjectVariablesRef.child("ACSenarioActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.setAcSenarioActive(Integer.parseInt(snapshot.getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckInModeTime").setValue(MyApp.ProjectVariables.CheckinModeTime);
        ProjectVariablesRef.child("CheckInModeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinModeTime = Integer.parseInt(snapshot.getValue().toString());
                    Log.d("checkInTime", MyApp.ProjectVariables.CheckinModeTime + " ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckOutModeTime").setValue(MyApp.ProjectVariables.CheckoutModeTime);
        ProjectVariablesRef.child("CheckOutModeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutModeTime = Integer.parseInt(snapshot.getValue().toString());
                    Log.d("checkoutTime", MyApp.ProjectVariables.CheckoutModeTime + " ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckinActions").setValue(MyApp.ProjectVariables.CheckinActions);
        ProjectVariablesRef.child("CheckinActions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinActions = snapshot.getValue().toString();
                    MyApp.checkInActions = new CheckInActions(MyApp.ProjectVariables.CheckinActions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckoutActions").setValue(MyApp.ProjectVariables.CheckoutActions);
        ProjectVariablesRef.child("CheckoutActions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutActions = snapshot.getValue().toString();
                    MyApp.checkOutActions = new CheckoutActions(MyApp.ProjectVariables.CheckoutActions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("DoorWarning").setValue(MyApp.ProjectVariables.DoorWarning);
        ProjectVariablesRef.child("DoorWarning").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.DoorWarning = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("HKCleanupTime").setValue(MyApp.ProjectVariables.HKCleanTime);
        ProjectVariablesRef.child("HKCleanupTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.HKCleanTime = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("Interval").setValue(MyApp.ProjectVariables.Interval);
        ProjectVariablesRef.child("Interval").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Log.d("intervalChanged", snapshot.getValue().toString());
                    MyApp.ProjectVariables.Interval = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("PoweroffAfterHK").setValue(MyApp.ProjectVariables.PoweroffAfterHK);
        ProjectVariablesRef.child("PoweroffAfterHK").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.PoweroffAfterHK = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("OnClientBack").setValue(MyApp.ProjectVariables.OnClientBack);
        ProjectVariablesRef.child("OnClientBack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.OnClientBack = snapshot.getValue().toString();
                    MyApp.clientBackActions = new ClientBackActions(MyApp.ProjectVariables.OnClientBack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("Temp").setValue(MyApp.ProjectVariables.Temp);
        ProjectVariablesRef.child("Temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    MyApp.ProjectVariables.Temp = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("RefreshSystemTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    try {
                        refreshSystemTime = Integer.parseInt(snapshot.getValue().toString());
                        if (first[0] > 0) {
                            refreshSystem(act);
                            first[0]++;
                        }

                    } catch (Exception e) {
                        Log.d("refreshSystem", e.getMessage());
                    }
                }
//                refreshTimer = new Timer();

//                    refreshTimer.scheduleAtFixedRate(new TimerTask() {
//                        @Override
//                        public void run() {
//                            Log.d("refreshSystem", "started");
//                            refreshSystem(act);
//                        }
//                    }, 1000 * 60 * 60 * refreshSystemTime, 1000 * 60 * 60 * refreshSystemTime);
//                    first[0]++;
//                }
//                else {
//                    refreshTimer.scheduleAtFixedRate(new TimerTask() {
//                        @Override
//                        public void run() {
//                            Log.d("refreshSystem", "started");
//                            refreshSystem(act);
//                        }
//                    }, 0, 1000 * 60 * 60 * refreshSystemTime);
//                }
                Log.d("refreshSystem", refreshSystemTime + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    static void removeSystemListeners() {
        for (int i=0;i<ROOMS.size();i++) {
            removeRoomFirebaseListeners(ROOMS.get(i));
            removeRoomDevicesListeners(ROOMS.get(i));
        }
        removeFireRoomsDevicesListeners();
    }

    static void defineVariables() {
        AC_SENARIO_Status = new boolean[ROOMS.size()];
        DOOR_STATUS = new boolean[ROOMS.size()];
        AC_Start = new long[ROOMS.size()];
        Door_Start = new long[ROOMS.size()];
        AC_Period = new long[ROOMS.size()];
        Door_Period = new long[ROOMS.size()];
        Client_Temp = new String[ROOMS.size()];
        TempSetPoint = new String[ROOMS.size()];
        TempRunnableList = new Runnable[ROOMS.size()];
        DoorRunnable = new Runnable[ROOMS.size()];
        CLEANUP = new boolean[ROOMS.size()];
        LAUNDRY = new boolean[ROOMS.size()];
        DND = new boolean[ROOMS.size()];
        CHECKOUT = new boolean[ROOMS.size()];
        DoorsHandlers = new Handler[ROOMS.size()];
        AcHandlers = new Handler[ROOMS.size()];
        for (int t = 0; t< ROOMS.size(); t++) {
            CLEANUP[t] = false ;
            LAUNDRY[t] = false ;
            DND[t] = false ;
            CHECKOUT[t] = false ;
            AC_SENARIO_Status[t] = false ;
            DOOR_STATUS[t] = false ;
            AC_Start[t] = 0 ;
            Door_Start[t]=0;
            AC_Period[t]=0 ;
            Door_Period[t]=0;
            Client_Temp[t] = "0" ;
        }
    }

    static void setRoomsDevices(List<DeviceBean> Devices,HomeBean h) {
        for (int i=0;i<ROOMS.size();i++) {
            DeviceBean power = searchRoomDevice(Devices,ROOMS.get(i),"Power");
            if (power == null) {
                ROOMS.get(i).PowerSwitch = 0 ;
                ROOMS.get(i).setPOWER_B(null);
                ROOMS.get(i).setPOWER(null);
                ROOMS.get(i).getFireRoom().child("PowerSwitch").setValue(0);
                ROOMS.get(i).getFireRoom().child("powerStatus").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Power").removeValue();
            }
            else {
                ROOMS.get(i).setPOWER_B(power);
                ROOMS.get(i).setPOWER(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getPOWER_B().devId));
                ROOMS.get(i).PowerSwitch = 1 ;
                ROOMS.get(i).getFireRoom().child("PowerSwitch").setValue(1);
                //ROOMS.get(i).roomCheckinDevices.add(new CheckinPowerModule(ROOMS.get(i),power));
            }
            DeviceBean ac = searchRoomDevice(Devices,ROOMS.get(i),"AC") ;
            if (ac == null) {
                ROOMS.get(i).Thermostat = 0 ;
                ROOMS.get(i).setAC_B(null);
                ROOMS.get(i).setAC(null);
                ROOMS.get(i).getFireRoom().child("Thermostat").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"AC").removeValue();
            }
            else {
                ROOMS.get(i).setAC_B(ac);
                ROOMS.get(i).setAC(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getAC_B().devId));
                ROOMS.get(i).Thermostat = 1 ;
                ROOMS.get(i).getFireRoom().child("Thermostat").setValue(1);
                int finalI = i;
                TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(ac.devId, new ITuyaResultCallback<List<TaskListBean>>() {
                    @Override
                    public void onSuccess(List<TaskListBean> result) {
                        long SetId = 0 ;
                        long PowerId = 0 ;
                        long FanId = 0;
                        for (int j=0 ; j<result.size();j++) {
                            if (result.get(j).getName().equals("Set temp") || result.get(j).getName().equals("temp_set") || result.get(j).getName().equals("Set Temperature") || result.get(j).getName().equals("Set temperature")) {
                                SetId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.TempSetDP = SetId;
                                ROOMS.get(finalI).acVariables.TempMax = result.get(j).getValueSchemaBean().getMax();
                                ROOMS.get(finalI).acVariables.TempMin = result.get(j).getValueSchemaBean().getMin();
                                ROOMS.get(finalI).acVariables.unit = result.get(j).getValueSchemaBean().getUnit();
                                ROOMS.get(finalI).acVariables.step = result.get(j).getValueSchemaBean().getStep();
                                try{
                                    String x = String.valueOf(ROOMS.get(finalI).acVariables.TempMax);
                                    ROOMS.get(finalI).acVariables.TempChars = x.length();
                                    if (ROOMS.get(finalI).acVariables.TempChars == 2) {
                                        ROOMS.get(finalI).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp ;
                                        ROOMS.get(finalI).acVariables.TempClient = 24 ;
                                    }
                                    else {
                                        ROOMS.get(finalI).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp*10 ;
                                        ROOMS.get(finalI).acVariables.TempClient = 240 ;
                                    }
                                }catch (Exception e) {
                                    Log.d("ac",e.getMessage());
                                }
                            }
                            if (result.get(j).getName().equals("Power") || result.get(j).getName().equals("switch") || result.get(j).getName().equals("Switch")) {
                                PowerId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.PowerDP = PowerId;
                            }
                            if (result.get(j).getName().equals("Current temp") || result.get(j).getName().equals("temp_current") || result.get(j).getName().equals("Current Temperature") || result.get(j).getName().equals("Current temperature")) {
                                ROOMS.get(finalI).acVariables.TempCurrentDP = result.get(j).getDpId() ;
                            }
                            if (result.get(j).getName().contains("Fan") || result.get(j).getName().contains("level") || result.get(j).getName().contains("Gear") || result.get(j).getName().contains("FAN") || result.get(j).getName().contains("fan")) {
                                FanId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.FanDP = FanId;
                                try {
                                    JSONObject r = new JSONObject(result.get(j).getSchemaBean().property);
                                    String[] v = r.getString("range").split(",");
                                    for (int y = 0;y<v.length;y++) {
                                        v[y] = v[y].replaceAll("\"","");
                                        v[y] = v[y].replace("]","");
                                        v[y] = v[y].replace("[","");
                                    }
                                    ROOMS.get(finalI).acVariables.FanValues = v;

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.d("setDevicesListAC"+ROOMS.get(finalI).RoomNumber,"set dp: "+ROOMS.get(finalI).acVariables.TempSetDP+" power dp: "+ROOMS.get(finalI).acVariables.PowerDP+" fan dp: "+ROOMS.get(finalI).acVariables.FanDP+" current dp: "+ROOMS.get(finalI).acVariables.TempCurrentDP+" fan values: "+ Arrays.toString(ROOMS.get(finalI).acVariables.FanValues)+" max: "+ROOMS.get(finalI).acVariables.TempMax+" min: "+ROOMS.get(finalI).acVariables.TempMin+" unit: "+ROOMS.get(finalI).acVariables.unit+" step: "+ROOMS.get(finalI).acVariables.step+" chars: "+ROOMS.get(finalI).acVariables.TempChars+" setPoint: "+ROOMS.get(finalI).acVariables.TempSetPoint+" clientTemp: "+ROOMS.get(finalI).acVariables.TempClient);
                        if (PowerId != 0) {
                            if (Boolean.parseBoolean(Objects.requireNonNull(ac.dps.get(String.valueOf(PowerId))).toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
                            }
                        }
                        if (SetId != 0) {
                            String val = Objects.requireNonNull(ac.dps.get(String.valueOf(SetId))).toString();
                            Log.d("setDevicesListAC"+ROOMS.get(finalI).RoomNumber,val.length()+"");
                            if (val.length() == 2) {
                                Log.d("setDevicesListAC"+ROOMS.get(finalI).RoomNumber,"is 2");
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").setValue(val);
                            }
                            else if (val.length() == 3) {
                                Log.d("setDevicesListAC"+ROOMS.get(finalI).RoomNumber,"is 3");
                                String newVal = val.substring(0,2);
                                Log.d("setDevicesListAC"+ROOMS.get(finalI).RoomNumber,newVal);
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").setValue(newVal);
                            }
                        }
                        if (FanId != 0) {
                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").setValue(Objects.requireNonNull(ac.dps.get(String.valueOf(FanId))).toString());
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {

                    }
                });
            }
            DeviceBean ZGatway = searchRoomDevice(Devices,ROOMS.get(i),"ZGatway") ;
            if (ZGatway == null) {
                ROOMS.get(i).ZBGateway = 0 ;
                ROOMS.get(i).setGATEWAY_B(null);
                ROOMS.get(i).setGATEWAY(null);
                ROOMS.get(i).getFireRoom().child("ZBGateway").setValue(0);
                ROOMS.get(i).getFireRoom().child("online").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"ZGatway").removeValue();
            }
            else {
                ROOMS.get(i).setGATEWAY_B(ZGatway);
                ROOMS.get(i).setGATEWAY(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getGATEWAY_B().devId));
                ROOMS.get(i).RoomHome = h;
                ROOMS.get(i).ZBGateway = 1 ;
                ROOMS.get(i).getFireRoom().child("ZBGateway").setValue(1);
            }
            DeviceBean DoorSensor = searchRoomDevice(Devices,ROOMS.get(i),"DoorSensor") ;
            if (DoorSensor == null) {
                ROOMS.get(i).DoorSensor = 0 ;
                ROOMS.get(i).setDOORSENSOR_B(null);
                ROOMS.get(i).setGATEWAY(null);
                ROOMS.get(i).getFireRoom().child("DoorSensor").setValue(0);
                ROOMS.get(i).getFireRoom().child("doorStatus").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"DoorSensor").removeValue();
            }
            else {
                ROOMS.get(i).setDOORSENSOR_B(DoorSensor);
                ROOMS.get(i).setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getDOORSENSOR_B().devId));
                ROOMS.get(i).DoorSensor = 1 ;
                ROOMS.get(i).getFireRoom().child("DoorSensor").setValue(1);
                //ROOMS.get(i).roomCheckinDevices.add(new CheckinDoorSensor(ROOMS.get(i),DoorSensor));
            }
            DeviceBean MotionSensor = searchRoomDevice(Devices,ROOMS.get(i),"MotionSensor") ;
            if (MotionSensor == null) {
                ROOMS.get(i).MotionSensor = 0 ;
                ROOMS.get(i).setMOTIONSENSOR_B(null);
                ROOMS.get(i).setMOTIONSENSOR(null);
                ROOMS.get(i).getFireRoom().child("MotionSensor").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"MotionSensor").removeValue();
            }
            else {
                ROOMS.get(i).setMOTIONSENSOR_B(MotionSensor);
                ROOMS.get(i).setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getMOTIONSENSOR_B().devId));
                ROOMS.get(i).MotionSensor = 1 ;
                ROOMS.get(i).getFireRoom().child("MotionSensor").setValue(1);
                //ROOMS.get(i).roomCheckinDevices.add(new CheckinMotionSensor(ROOMS.get(i),MotionSensor));
            }
            DeviceBean Curtain = searchRoomDevice(Devices,ROOMS.get(i),"Curtain") ;
            if (Curtain == null) {
                ROOMS.get(i).CurtainSwitch = 0 ;
                ROOMS.get(i).setCURTAIN_B(null);
                ROOMS.get(i).setCURTAIN(null);
                ROOMS.get(i).getFireRoom().child("CurtainSwitch").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Curtain").removeValue();
            }
            else {
                ROOMS.get(i).setCURTAIN_B(Curtain);
                ROOMS.get(i).setCURTAIN(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getCURTAIN_B().devId));
                ROOMS.get(i).CurtainSwitch = 1 ;
                ROOMS.get(i).getFireRoom().child("CurtainSwitch").setValue(1);
                int finalI = i;
                TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(ROOMS.get(i).getCURTAIN_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
                    @Override
                    public void onSuccess(List<TaskListBean> result) {
                        for (TaskListBean tb:result) {
                            if (tb.getName().equals("Control")) {
                                Object[] obS = tb.getTasks().keySet().toArray();
                                for (Object o : obS) {
                                    if (o.toString().equals("open") || o.toString().equals("Open") || o.toString().equals("OPEN")) {
                                        ROOMS.get(finalI).curtainControl.Open = o.toString();
                                    }
                                    else if (o.toString().equals("close") || o.toString().equals("Close") || o.toString().equals("CLOSE")) {
                                        ROOMS.get(finalI).curtainControl.Close = o.toString();
                                    }
                                    else if (o.toString().equals("stop") || o.toString().equals("Stop") || o.toString().equals("STOP")) {
                                        ROOMS.get(finalI).curtainControl.Stop = o.toString();
                                    }
                                    else if (o.toString().equals("continue") || o.toString().equals("Continue") || o.toString().equals("CONTINUE")) {
                                        ROOMS.get(finalI).curtainControl.Continue = o.toString();
                                    }
                                }
                                if (ROOMS.get(finalI).curtainControl != null) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getCURTAIN_B().name).child("control").setValue(Objects.requireNonNull(ROOMS.get(finalI).getCURTAIN_B().dps.get(ROOMS.get(finalI).curtainControl.ControlDP)).toString());
                                    ROOMS.get(finalI).curtainControl.ControlDP = String.valueOf(tb.getDpId());

                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {

                    }
                });
            }
            DeviceBean ServiceSwitch = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch") ;
            if (ServiceSwitch == null) {
                ROOMS.get(i).ServiceSwitch = 0 ;
                ROOMS.get(i).setSERVICE1_B(null);
                ROOMS.get(i).setSERVICE1(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"ServiceSwitch").removeValue();
            }
            else {
                ROOMS.get(i).setSERVICE1_B(ServiceSwitch);
                ROOMS.get(i).setSERVICE1(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSERVICE1_B().devId));
                ROOMS.get(i).ServiceSwitch = 1 ;
            }
            DeviceBean ServiceSwitch2 = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch2") ;
            if (ServiceSwitch2 != null) {
                ROOMS.get(i).setSERVICE2_B(ServiceSwitch2);
                ROOMS.get(i).setSERVICE2(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSERVICE2_B().devId));
            }
            DeviceBean Switch1 = searchRoomDevice(Devices,ROOMS.get(i),"Switch1") ;
            if (Switch1 == null) {
                ROOMS.get(i).Switch1 = 0 ;
                ROOMS.get(i).setSWITCH1_B(null);
                ROOMS.get(i).setSWITCH1(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch1").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH1_B(Switch1);
                ROOMS.get(i).setSWITCH1(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH1_B().devId));
                ROOMS.get(i).Switch1 = 1 ;
                if (Switch1.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch1.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").removeValue();
                }
                if (Switch1.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch1.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").removeValue();
                }
                if (Switch1.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch1.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").removeValue();
                }
                if (Switch1.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch1.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").removeValue();
                }
            }
            DeviceBean Switch2 = searchRoomDevice(Devices,ROOMS.get(i),"Switch2") ;
            if (Switch2 == null) {
                ROOMS.get(i).Switch2 = 0 ;
                ROOMS.get(i).setSWITCH2_B(null);
                ROOMS.get(i).setSWITCH2(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch2").removeValue();
            }
            else {
                Log.d("switch2",ROOMS.get(i).RoomNumber+"not null");
                ROOMS.get(i).setSWITCH2_B(Switch2);
                ROOMS.get(i).setSWITCH2(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH2_B().devId));
                ROOMS.get(i).Switch2 = 1 ;
                if (Switch2.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch2.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("1").removeValue();
                }
                if (Switch2.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch2.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("2").removeValue();
                }
                if (Switch2.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch2.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("3").removeValue();
                }
                if (Switch2.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch2.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("4").removeValue();
                }
            }
            DeviceBean Switch3 = searchRoomDevice(Devices,ROOMS.get(i),"Switch3") ;
            if (Switch3 == null) {
                ROOMS.get(i).Switch3 = 0 ;
                ROOMS.get(i).setSWITCH3_B(null);
                ROOMS.get(i).setSWITCH3(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch3").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH3_B(Switch3);
                ROOMS.get(i).setSWITCH3(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH3_B().devId));
                ROOMS.get(i).Switch3 = 1 ;
                if (Switch3.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch3.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("1").removeValue();
                }
                if (Switch3.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch3.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("2").removeValue();
                }
                if (Switch3.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch3.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("3").removeValue();
                }
                if (Switch3.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch3.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("4").removeValue();
                }
            }
            DeviceBean Switch4 = searchRoomDevice(Devices,ROOMS.get(i),"Switch4") ;
            if (Switch4 == null) {
                ROOMS.get(i).Switch4 = 0 ;
                ROOMS.get(i).setSWITCH4_B(null);
                ROOMS.get(i).setSWITCH4(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch4").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH4_B(Switch4);
                ROOMS.get(i).setSWITCH4(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH4_B().devId));
                ROOMS.get(i).Switch4 = 1 ;
                if (Switch4.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch4.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("1").removeValue();
                }
                if (Switch4.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch4.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("2").removeValue();
                }
                if (Switch4.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch4.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("3").removeValue();
                }
                if (Switch4.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch4.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("4").removeValue();
                }
            }
            DeviceBean Switch5 = searchRoomDevice(Devices,ROOMS.get(i),"Switch5") ;
            if (Switch5 == null) {
                ROOMS.get(i).Switch5 = 0 ;
                ROOMS.get(i).setSWITCH5_B(null);
                ROOMS.get(i).setSWITCH5(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch5").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH5_B(Switch5);
                ROOMS.get(i).setSWITCH5(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH5_B().devId));
                ROOMS.get(i).Switch5 = 1 ;
                if (Switch5.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch5.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("1").removeValue();
                }
                if (Switch5.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch5.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("2").removeValue();
                }
                if (Switch5.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch5.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("3").removeValue();
                }
                if (Switch5.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch5.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch5.name).child("4").removeValue();
                }
            }
            DeviceBean Switch6 = searchRoomDevice(Devices,ROOMS.get(i),"Switch6") ;
            if (Switch6 == null) {
                ROOMS.get(i).Switch6 = 0 ;
                ROOMS.get(i).setSWITCH6_B(null);
                ROOMS.get(i).setSWITCH6(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch6").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH6_B(Switch6);
                ROOMS.get(i).setSWITCH6(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH6_B().devId));
                ROOMS.get(i).Switch6 = 1 ;
                if (Switch6.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch6.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("1").removeValue();
                }
                if (Switch6.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch6.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("2").removeValue();
                }
                if (Switch6.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch6.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("3").removeValue();
                }
                if (Switch6.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch6.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch6.name).child("4").removeValue();
                }
            }
            DeviceBean Switch7 = searchRoomDevice(Devices,ROOMS.get(i),"Switch7") ;
            if (Switch7 == null) {
                ROOMS.get(i).Switch7 = 0 ;
                ROOMS.get(i).setSWITCH7_B(null);
                ROOMS.get(i).setSWITCH7(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch7").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH7_B(Switch7);
                ROOMS.get(i).setSWITCH7(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH7_B().devId));
                ROOMS.get(i).Switch7 = 1 ;
                if (Switch7.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch7.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("1").removeValue();
                }
                if (Switch7.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch7.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("2").removeValue();
                }
                if (Switch7.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch7.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("3").removeValue();
                }
                if (Switch7.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch7.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch7.name).child("4").removeValue();
                }
            }
            DeviceBean Switch8 = searchRoomDevice(Devices,ROOMS.get(i),"Switch8") ;
            if (Switch8 == null) {
                ROOMS.get(i).Switch8 = 0 ;
                ROOMS.get(i).setSWITCH8_B(null);
                ROOMS.get(i).setSWITCH8(null);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch8").removeValue();
            }
            else {
                ROOMS.get(i).setSWITCH8_B(Switch8);
                ROOMS.get(i).setSWITCH8(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH8_B().devId));
                ROOMS.get(i).Switch8 = 1 ;
                if (Switch8.dps.get("1") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch8.dps.get("1")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("1").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("1").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("1").removeValue();
                }
                if (Switch8.dps.get("2") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch8.dps.get("2")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("2").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("2").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("2").removeValue();
                }
                if (Switch8.dps.get("3") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch8.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("3").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("3").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("3").removeValue();
                }
                if (Switch8.dps.get("4") != null) {
                    if (Boolean.parseBoolean(Objects.requireNonNull(Switch8.dps.get("3")).toString())) {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("4").setValue(3);
                    }
                    else {
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("4").setValue(0);
                    }
                }
                else {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch8.name).child("4").removeValue();
                }
            }
            DeviceBean lock = searchRoomDevice(Devices,ROOMS.get(i),"Lock") ;
            if (lock == null) {
                ROOMS.get(i).lock = 0 ;
                ROOMS.get(i).setLOCK_B(null);
                ROOMS.get(i).setLOCK(null);
                ROOMS.get(i).getFireRoom().child("lock").setValue(0);
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Lock").removeValue();
            }
            else {
                ROOMS.get(i).setLOCK_B(lock);
                ROOMS.get(i).setLOCK(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getLOCK_B().devId));
                ROOMS.get(i).lock = 1 ;
                ROOMS.get(i).getFireRoom().child("lock").setValue(1);
                //setRoomLockId(act,lock.devId, String.valueOf(ROOMS.get(i).id));
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(lock.name).child("1").setValue(0);
            }
        }
    }

    static void setRoomsDevices(Activity act) {
        if (Devices.size() == 0) {
            showRooms(act);
            createRestartConfirmationDialog(act,"no devices detected");
            return;
        }
        for (int i=0;i<ROOMS.size();i++) {
            if (ROOMS.get(i).PowerSwitch == 0) {
                if (i == 0) {
                    PowerUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    PowerUnInstalled = MessageFormat.format("{0}-{1}", PowerUnInstalled, ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).PowerSwitch == 1) {
                if (i == 0) {
                    PowerInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    PowerInstalled = MessageFormat.format("{0}-{1}", PowerInstalled, ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).ZBGateway == 0) {
                if (i == 0) {
                    GatewayUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    GatewayUnInstalled = MessageFormat.format("{0}-{1}",GatewayUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).ZBGateway == 1) {
                if (i == 0) {
                    GatewayInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    GatewayInstalled = MessageFormat.format("{0}-{1}",GatewayInstalled,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).MotionSensor == 0) {
                if (i == 0) {
                    MotionUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    MotionUnInstalled = MessageFormat.format("{0}-{1}",MotionUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).MotionSensor == 1) {
                if (i == 0) {
                    MotionInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    MotionInstalled = MessageFormat.format("{0}-{1}",MotionInstalled,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).DoorSensor == 0) {
                if (i == 0) {
                    DoorUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    DoorUnInstalled = MessageFormat.format("{0}-{1}",DoorUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).DoorSensor == 1) {
                if (i == 0) {
                    DoorInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    DoorInstalled = MessageFormat.format("{0}-{1}",DoorInstalled,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).ServiceSwitch == 0) {
                if (i == 0) {
                    ServiceUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    ServiceUnInstalled = MessageFormat.format("{0}-{1}",ServiceUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).ServiceSwitch == 1) {
                if (i == 0) {
                    ServiceInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    ServiceInstalled = MessageFormat.format("{0}-{1}",ServiceInstalled,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch1 == 0) {
                if (i == 0) {
                    S1UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S1UnInstalled = MessageFormat.format("{0}-{1}",S1UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch1 == 1) {
                if (i == 0) {
                    S1Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S1Installed = MessageFormat.format("{0}-{1}",S1Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch2 == 0) {
                if (i == 0) {
                    S2UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S2UnInstalled = MessageFormat.format("{0}-{1}",S2UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch2 == 1) {
                if (i == 0) {
                    S2Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S2Installed = MessageFormat.format("{0}-{1}",S2Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch3 == 0) {
                if (i == 0) {
                    S3UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S3UnInstalled = MessageFormat.format("{0}-{1}",S3UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch3 == 1) {
                if (i == 0) {
                    S3Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S3Installed = MessageFormat.format("{0}-{1}",S3Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch4 == 0) {
                if (i == 0) {
                    S4UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S4UnInstalled = MessageFormat.format("{0}-{1}",S4UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch4 == 1) {
                if (i == 0) {
                    S4Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S4Installed = MessageFormat.format("{0}-{1}",S4Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch5 == 0) {
                if (i == 0) {
                    S5UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S5UnInstalled = MessageFormat.format("{0}-{1}",S5UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch5 == 1) {
                if (i == 0) {
                    S5Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S5Installed = MessageFormat.format("{0}-{1}",S5Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch6 == 0) {
                if (i == 0) {
                    S6UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S6UnInstalled = MessageFormat.format("{0}-{1}",S6UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch6 == 1) {
                if (i == 0) {
                    S6Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S6Installed = MessageFormat.format("{0}-{1}",S6Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch7 == 0) {
                if (i == 0) {
                    S7UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S7UnInstalled = MessageFormat.format("{0}-{1}",S7UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch7 == 1) {
                if (i == 0) {
                    S7Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S7Installed = MessageFormat.format("{0}-{1}",S7Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Switch8 == 0) {
                if (i == 0) {
                    S8UnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    S8UnInstalled = MessageFormat.format("{0}-{1}",S8UnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Switch8 == 1) {
                if (i == 0) {
                    S8Installed = ROOMS.get(i).id+"" ;
                }
                else {
                    S8Installed = MessageFormat.format("{0}-{1}",S8Installed,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).Thermostat == 0) {
                if (i == 0) {
                    ACUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    ACUnInstalled = MessageFormat.format("{0}-{1}",ACUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).Thermostat == 1) {
                if (i == 0) {
                    ACInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    ACInstalled = MessageFormat.format("{0}-{1}",ACInstalled,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).CurtainSwitch == 0) {
                if (i == 0) {
                    CurtainUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    CurtainUnInstalled = MessageFormat.format("{0}-{1}",CurtainUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).CurtainSwitch == 1) {
                if (i == 0) {
                    CurtainInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    CurtainInstalled = MessageFormat.format("{0}-{1}",CurtainInstalled,ROOMS.get(i).id);
                }
            }
            if (ROOMS.get(i).lock == 0) {
                if (i == 0) {
                    LockUnInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    LockUnInstalled = MessageFormat.format("{0}-{1}",LockUnInstalled,ROOMS.get(i).id);
                }
            }
            else if (ROOMS.get(i).lock == 1) {
                if (i == 0) {
                    LockInstalled = ROOMS.get(i).id+"" ;
                }
                else {
                    LockInstalled = MessageFormat.format("{0}-{1}",LockInstalled,ROOMS.get(i).id);
                }
            }
        }
        setRoomsDevicesInstalledInDB();
    }

    public static void refreshSystem(Activity act) {
        for (ROOM r : ROOMS) {
            removeRoomDevicesListeners(r);
        }
        getTuyaDevices(act,new RequestCallback() {
            @Override
            public void onSuccess() {
                showDevices(act);
                setAllListeners(act);
                getSceneBGs();
                setAcScenario();
                Toast.makeText(act,"Refresh Done",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
            }
        });
        loginTTLock(act,new RequestCallback() {
            @Override
            public void onSuccess() {
                getLocks(new RequestCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    static void showRooms(Activity act) {
        GridView roomsListView = act.findViewById(R.id.RoomsListView);
        Rooms_Adapter_Base adapterRooms = new Rooms_Adapter_Base(ROOMS,act);
        act.runOnUiThread(() -> roomsListView.setAdapter(adapterRooms));

    }

    static void showDevices(Activity act) {
        GridView devicesListView = act.findViewById(R.id.DevicesListView);
        Devices_Adapter adapterDevices = new Devices_Adapter(Devices,act);
        act.runOnUiThread(() -> devicesListView.setAdapter(adapterDevices));

    }

    static void removeRoomFirebaseListeners(ROOM room) {
        if (room.CleanupListener != null) {
            room.getFireRoom().child("Cleanup").removeEventListener(room.CleanupListener);
        }
        if (room.LaundryListener != null) {
            room.getFireRoom().child("Laundry").removeEventListener(room.LaundryListener);
        }
        if (room.CheckoutListener != null) {
            room.getFireRoom().child("Checkout").removeEventListener(room.CheckoutListener);
        }
        if (room.DNDListener != null) {
            room.getFireRoom().child("DND").removeEventListener(room.DNDListener);
        }
        if (room.roomStatusListener != null) {
            room.getFireRoom().child("roomStatus").removeEventListener(room.roomStatusListener);
        }
        if (room.ClientInListener != null) {
            room.getFireRoom().child("ClientIn").removeEventListener(room.ClientInListener);
        }
    }

    static void removeRoomDevicesListeners(ROOM room) {
        if (room.getPOWER() != null) {
            room.getPOWER().unRegisterDevListener();
        }
        if (room.getDOORSENSOR() != null) {
            room.getDOORSENSOR().unRegisterDevListener();
        }
        if (room.getSERVICE1() != null) {
            room.getSERVICE1().unRegisterDevListener();
        }
        if (room.getAC() != null) {
            room.getAC().unRegisterDevListener();
        }
        if (room.getMOTIONSENSOR() != null) {
            room.getMOTIONSENSOR().unRegisterDevListener();
        }
        if (room.getGATEWAY() != null) {
            room.getGATEWAY().unRegisterDevListener();
        }
        if (room.getSWITCH1() != null) {
            room.getSWITCH1().unRegisterDevListener();
        }
        if (room.getSWITCH2() != null) {
            room.getSWITCH2().unRegisterDevListener();
        }
        if (room.getSWITCH3() != null) {
            room.getSWITCH3().unRegisterDevListener();
        }
        if (room.getSWITCH4() != null) {
            room.getSWITCH4().unRegisterDevListener();
        }
        if (room.getSWITCH5() != null) {
            room.getSWITCH5().unRegisterDevListener();
        }
        if (room.getSWITCH6() != null) {
            room.getSWITCH6().unRegisterDevListener();
        }
        if (room.getSWITCH7() != null) {
            room.getSWITCH7().unRegisterDevListener();
        }
        if (room.getSWITCH8() != null) {
            room.getSWITCH8().unRegisterDevListener();
        }
        if (room.getCURTAIN() != null) {
            room.getCURTAIN().unRegisterDevListener();
        }
    }

    static void setAcScenario() {
        if (ROOMS != null && ROOMS.size() > 0) {
            for (int t = 0; t< ROOMS.size(); t++) {
                int finalT = t;
                if (MyApp.ProjectVariables.Temp != 0) {
                    if (ROOMS.get(t).acVariables.TempChars == 2) {
                        ROOMS.get(t).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp ;
                    }
                    else {
                        ROOMS.get(t).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp * 10 ;
                    }
                }
                DoorRunnable[t] = new Runnable() {
                    @Override
                    public void run() {
                        Log.d("doorWarning",Door_Period[finalT] +" "+ MyApp.ProjectVariables.DoorWarning +" "+DOOR_STATUS[finalT]);
                        DoorsHandlers[finalT] = new Handler();
                        DoorsHandlers[finalT].postDelayed(this,1000) ;
                        Door_Period[finalT] = System.currentTimeMillis() - Door_Start[finalT] ;
                        if ( Door_Period[finalT] >=  MyApp.ProjectVariables.DoorWarning  && DOOR_STATUS[finalT]) {
                            ROOMS.get(finalT).getFireRoom().child("doorStatus").setValue(2);
                            DoorsHandlers[finalT].removeCallbacks(DoorRunnable[finalT]);
                        }
                        else if (Door_Period[finalT] >=  MyApp.ProjectVariables.DoorWarning  && !DOOR_STATUS[finalT]) {
                            DoorsHandlers[finalT].removeCallbacks(DoorRunnable[finalT]);
                        }

                    }
                };
                TempRunnableList[t] = () -> {
                    AcHandlers[finalT] = new Handler();
                    AcHandlers[finalT].postDelayed(TempRunnableList[finalT], 1000);
                    AC_Period[finalT] = System.currentTimeMillis() - AC_Start[finalT] ;
                    if ( AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && AC_SENARIO_Status[finalT]) {
                        if (ROOMS.get(finalT).getAC_B() != null ) {
                            TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalT).getAC_B().devId).publishDps("{\" "+ROOMS.get(finalT).acVariables.TempSetDP+"\": "+ROOMS.get(finalT).acVariables.TempSetPoint+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("acScenario"+ROOMS.get(finalT).RoomNumber,error);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("acScenario"+ROOMS.get(finalT).RoomNumber,"done");
                                    AC_SENARIO_Status[finalT] = false ;
                                    AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
                                }
                            });
                        }

                    }
                    else if (AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && !AC_SENARIO_Status[finalT]) {
                        Log.d("acSenario"+ROOMS.get(finalT).RoomNumber,"canceled");
                        AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
                    }
                };
            }
        }
    }

    static void loginTTLock(Activity act,RequestCallback callback) {
        if (MyApp.THE_PROJECT.LockUser.equals("no")) {
            Log.d("locksAre","no bluetooth locks");
            Button locksButton = act.findViewById(R.id.button17);
            locksButton.setVisibility(View.GONE);
            return;
        }
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String pass = MyApp.THE_PROJECT.LockPassword;
        pass = DigitUtil.getMD5(pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", MyApp.THE_PROJECT.LockUser, pass, ApiService.REDIRECT_URI);
        String finalPass = pass;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null) {
                    if (accountInfo.errcode == 0) {
                        accountInfo.setMd5Pwd(finalPass);
                        acc = accountInfo;
                        callback.onSuccess();
                    }
                    else {
                        callback.onFail("lock login failed "+accountInfo.errcode);
                    }
                }
                else {
                    callback.onFail("lock login failed account null");
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                callback.onFail(t.getMessage());
            }
        });
    }

    static void getLocks(RequestCallback callback) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID,acc.getAccess_token(), 1, 100, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                String json = response.body();
                if (json != null) {
                    if (json.contains("list")) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray array = jsonObject.getJSONArray("list");
                            Locks = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                            setLocks(Locks);
                            callback.onSuccess();
                        }
                        catch (JSONException e) {
                            callback.onFail(e.getMessage());
                        }

                    }
                    else {
                        callback.onFail("no list");
                    }
                }
                else {
                    callback.onFail("null body");
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                callback.onFail(t.getMessage());
            }
        });
    }

    static void setLocks(ArrayList<LockObj> Locks) {
        for (int j=0;j<Locks.size();j++) {
            Log.d("locks" , Locks.get(j).getLockName());
            for (int i = 0; i< ROOMS.size(); i++) {
                if (Locks.get(j).getLockName().equals(ROOMS.get(i).RoomNumber+"Lock")) {
                    ROOMS.get(i).setLock(Locks.get(j));
                    break;
                }
            }
        }
    }

    public void refresh(View view) {
        refreshSystem(act);
    }
    //_________________________________________________________

    // set Listeners
    //_________________________________________________________
    static public void setDevicesListeners(Activity act) {
        for (int i = 0; i< ROOMS.size(); i++) {
            int finalI = i;
            if (ROOMS.get(i).getDOORSENSOR_B() != null ) {
                boolean[] doorS = {false};
                if (ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101") != null) {
                    doorS[0] = Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101")).toString());
                }
                long[] lastOpen = {Calendar.getInstance(Locale.getDefault()).getTimeInMillis()};
                ROOMS.get(i).getDOORSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("doorAction" , dpStr+" "+ROOMS.get(finalI).getDOORSENSOR_B().dps.toString());
                        if (searchValuesInList(dpStr.values(),DoorSensor_Open)) {
                            long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            if (ROOMS.get(finalI).firstDoorOpen) {
                                ROOMS.get(finalI).firstDoorOpen = false;
                                checkInModeRoom(ROOMS.get(finalI));
                            }
                            else {
                                runClientBackActions(ROOMS.get(finalI));
                            }
                            ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(1);
                            AC_Start[finalI] = System.currentTimeMillis() ;
                            Door_Start[finalI] = System.currentTimeMillis() ;
                            AC_SENARIO_Status[finalI] = true ;
                            DOOR_STATUS[finalI] = true ;
                            AC_Period[finalI] = 0;
                            Door_Period[finalI]= 0;
                            if (now > (lastOpen[0]+2000)) {
                                lastOpen[0] = now;
                                if (MyApp.ProjectVariables.getAcSenarioActive()) {
                                    Log.d("acSenario" + ROOMS.get(finalI).RoomNumber, "start");
                                    TempRunnableList[finalI].run();
                                }
                            }
                            DoorRunnable[finalI].run();
                            setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door open",act);
                        }
                        else if (searchValuesInList(dpStr.values(),DoorSensor_Close)) {
                            ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(0);
                            if (DoorsHandlers[finalI] != null) {
                                DoorsHandlers[finalI].removeCallbacks(DoorRunnable[finalI]);
                            }
                            DOOR_STATUS[finalI] = false ;
                            setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door closed",act);
                        }
                        else {
                            if (ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101") != null) {
                                long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                                Log.d("doorAction" , "101");
                                if (!doorS[0]) {
                                    Log.d("doorAction" , "open");
                                    doorS[0] = true;
                                    if (ROOMS.get(finalI).firstDoorOpen) {
                                        ROOMS.get(finalI).firstDoorOpen = false;
                                        checkInModeRoom(ROOMS.get(finalI));
                                    }
                                    else {
                                        runClientBackActions(ROOMS.get(finalI));
                                    }
                                    ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(1);
                                    AC_Start[finalI] = System.currentTimeMillis() ;
                                    Door_Start[finalI] = System.currentTimeMillis() ;
                                    AC_SENARIO_Status[finalI] = true ;
                                    DOOR_STATUS[finalI] = true ;
                                    AC_Period[finalI] = 0;
                                    Door_Period[finalI]= 0;
                                    if (now > (lastOpen[0]+2000)) {
                                        lastOpen[0] = now;
                                        if (MyApp.ProjectVariables.getAcSenarioActive()) {
                                            TempRunnableList[finalI].run();
                                            Log.d("acSenario", "start");
                                        }
                                    }
                                    DoorRunnable[finalI].run();
                                    setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door open",act);
                                }
                                else {
                                    Log.d("doorAction" , "close");
                                    doorS[0] = false;
                                    ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(0);
                                    if (DoorsHandlers[finalI] != null) {
                                        DoorsHandlers[finalI].removeCallbacks(DoorRunnable[finalI]);
                                    }
                                    DOOR_STATUS[finalI] = false ;
                                    setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door closed",act);
                                }
                            }
                            if (ROOMS.get(finalI).getDOORSENSOR_B().dps.get("103") != null) {
                                ROOMS.get(finalI).getFireRoom().child("doorSensorBattery").setValue(Objects.requireNonNull(ROOMS.get(finalI).getDOORSENSOR_B().dps.get("103")).toString());
                            }
                            if (ROOMS.get(finalI).getDOORSENSOR_B().dps.get("2") != null) {
                                ROOMS.get(finalI).getFireRoom().child("doorSensorBattery").setValue(Objects.requireNonNull(ROOMS.get(finalI).getDOORSENSOR_B().dps.get("2")).toString());
                            }
                        }
                        if (dpStr.get("battery_percentage") != null) {
                            ROOMS.get(finalI).getFireRoom().child("doorSensorBattery").setValue(Objects.requireNonNull(dpStr.get("battery_percentage")).toString());
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        Log.d("DoorSensor" , "Removed" );
                        ROOMS.get(finalI).setDoorSensorStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {
                        Log.d("DoorSensor" , "status changed " + online );

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {
                        Log.d("DoorSensor" , "network status changed " + status );
                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {
                        Log.d("DoorSensor" , "DevInfo"  );
                    }
                });
            }
            if (ROOMS.get(i).getSERVICE1_B() != null) {
                if (ROOMS.get(i).getSERVICE1_B().getIsOnline()) {
                    setClientInOrOut(ROOMS.get(finalI),"1");
                }
                else {
                    setClientInOrOut(ROOMS.get(finalI),"0");
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)) != null) {
                    CLEANUP[i] = Boolean.getBoolean(Objects.requireNonNull(ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton))).toString()) ;
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)) != null) {
                    LAUNDRY[i] = Boolean.getBoolean( Objects.requireNonNull(ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton))).toString());
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                    DND[i] = Boolean.getBoolean( Objects.requireNonNull(ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton))).toString());
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                    CHECKOUT[i] = Boolean.getBoolean(Objects.requireNonNull(ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton))).toString());
                }
                int[] v1 = {0};
                int[] v2 = {0};
                int[] v3 = {0};
                int[] v4 = {0};
                final Long[] lastCleanup = {Calendar.getInstance(Locale.getDefault()).getTimeInMillis()};
                final Long[] lastLaundry = {Calendar.getInstance(Locale.getDefault()).getTimeInMillis()};
                final Long[] lastCheckout = {Calendar.getInstance(Locale.getDefault()).getTimeInMillis()};
                ROOMS.get(i).getSERVICE1().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("serviceAction" , dpStr.toString());
                        if (ROOMS.get(finalI).roomStatus == 2) {
                            long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            if (dpStr.toString().length() <17) {
                                Log.d("cancelOrderProb",ROOMS.get(finalI).RoomNumber+" v1 "+v1[0]+" v2 "+v2[0]+" v3 "+v3[0]+" v4 "+v4[0]);
                                Log.d("serviceAction" , "_____________________________________________");
                                Log.d("serviceAction" , "action start");
                                Log.d("serviceAction" , "action "+dpStr);
                                Log.d("serviceAction" , "before cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
                                Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                                Log.d("serviceAction" , "length "+dpStr.toString().length());
                                if (dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton) != null) {
                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString()) && !CLEANUP[finalI]) {
                                        CLEANUP[finalI] = true;
                                        if (now > (lastCleanup[0] +5000)) {
                                            Log.d("addCleanupRsp" , "pressed");
                                            lastCleanup[0] = now;
                                            addCleanupOrder(ROOMS.get(finalI));
                                        }
                                        ROOMS.get(finalI).Cleanup = 1;
                                        ROOMS.get(finalI).dep = "Cleanup";
                                        ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(time);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order", act);
                                    } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString()) && CLEANUP[finalI]) {
                                        CLEANUP[finalI] = false;
                                        if (now > (lastCleanup[0]+5000)) {
                                            Log.d("removeCleanupRsp" , "pressed");
                                            lastCleanup[0] = now;
                                            cancelServiceOrder(ROOMS.get(finalI), "Cleanup");
                                        }
                                        ROOMS.get(finalI).Cleanup = 0;
                                        ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(0);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order finished", act);
                                    }
                                }
                                if (dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton) != null) {
                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString()) && !LAUNDRY[finalI]) {
                                        LAUNDRY[finalI] = true;
                                        if (now > lastLaundry[0] +5000) {
                                            Log.d("addLaundryRsp" , "pressed");
                                            lastLaundry[0] = now;
                                            addLaundryOrder(ROOMS.get(finalI));
                                        }
                                        ROOMS.get(finalI).Laundry = 1;
                                        ROOMS.get(finalI).dep = "Laundry";
                                        ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(time);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order", act);
                                    }
                                    else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString()) && LAUNDRY[finalI]) {
                                        LAUNDRY[finalI] = false;
                                        if (now > lastLaundry[0]+5000) {
                                            Log.d("removeLaundryRsp" , "started");
                                            lastLaundry[0] = now;
                                            cancelServiceOrder(ROOMS.get(finalI), "Laundry");
                                        }
                                        ROOMS.get(finalI).Laundry = 0;
                                        ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(0);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order finished", act);
                                    }
                                }
                                if (dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton) != null) {
                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString()) && !CHECKOUT[finalI]) {
                                        CHECKOUT[finalI] = true;
                                        if (now > lastCheckout[0] +5000) {
                                            Log.d("addCheckoutRsp" , "pressed");
                                            lastCheckout[0] = now;
                                            addCheckoutOrder(ROOMS.get(finalI));
                                        }
                                        ROOMS.get(finalI).Checkout = 1;
                                        ROOMS.get(finalI).dep = "Checkout";
                                        ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(time);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order", act);
                                    }
                                    else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString()) && CHECKOUT[finalI]) {
                                        CHECKOUT[finalI] = false;
                                        if (now > lastCheckout[0]) {
                                            Log.d("removeCheckoutRsp" , "pressed");
                                            lastCheckout[0] = now;
                                            cancelServiceOrder(ROOMS.get(finalI), "Checkout");
                                        }
                                        ROOMS.get(finalI).Checkout = 0;
                                        ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(0);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order finished", act);
                                    }
                                }
                                if (dpStr.get("switch_" + MyApp.ProjectVariables.dndButton) != null) {
                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString()) && !DND[finalI]) {
                                        DND[finalI] = true;
                                        if (v4[0] > 0) {
                                            addDNDOrder(ROOMS.get(finalI));
                                        }
                                        if (v4[0] <9) {
                                            v4[0]++;
                                        }
                                        ROOMS.get(finalI).DND = 1;
                                        ROOMS.get(finalI).dep = "DND";
                                        ROOMS.get(finalI).getFireRoom().child("DND").setValue(time);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd on", act);
                                    } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString()) && DND[finalI]) {
                                        DND[finalI] = false;
                                        if (v4[0] > 0) {
                                            cancelDNDOrder(ROOMS.get(finalI));
                                        }
                                        if (v4[0] <9) {
                                            v4[0]++;
                                        }
                                        ROOMS.get(finalI).DND = 0;
                                        ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
                                    }
                                }
                                Log.d("serviceAction" , "after cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
                                Log.d("serviceAction" , ROOMS.get(finalI).getSERVICE1_B().dps.toString());
                                Log.d("serviceAction" , "_____________________________________________");
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setServiceSwitchStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {
                        Log.d("guestIn"+ROOMS.get(finalI).RoomNumber,online+" ");
                        if (online) {
                            setClientInOrOut(ROOMS.get(finalI),"1");
                        }
                        else {
                            setClientInOrOut(ROOMS.get(finalI),"0");
                        }
                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getAC_B() != null) {
                ROOMS.get(i).getAC().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("acAction" , dpStr.toString());
                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " AC action",act);
                        if (dpStr.get("temp_set") != null ) {
                            if (Double.parseDouble(Objects.requireNonNull(dpStr.get("temp_set")).toString()) !=  ROOMS.get(finalI).acVariables.TempSetPoint) {
                                Client_Temp[finalI] = Objects.requireNonNull(dpStr.get("temp_set")).toString();
                                ROOMS.get(finalI).acVariables.TempClient = Integer.parseInt(Objects.requireNonNull(dpStr.get("temp_set")).toString());
                            }
                            if (Objects.requireNonNull(dpStr.get("temp_set")).toString().length() > 2) {
                                double temp = (Integer.parseInt(Objects.requireNonNull(dpStr.get("temp_set")).toString())*0.1);
                                ROOMS.get(finalI).getFireRoom().child("temp").setValue(temp) ;
                            }
                            else {
                                int temp = Integer.parseInt(Objects.requireNonNull(dpStr.get("temp_set")).toString());
                                ROOMS.get(finalI).getFireRoom().child("temp").setValue(temp) ;
                            }
                        }
                        if (dpStr.get("switch") != null) {
                            if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch")).toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setThermostatStatus(String.valueOf(ROOMS.get(finalI).id) , "0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getPOWER_B() != null) {
                final boolean[] v1 = {Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getPOWER_B().dps.get("1")).toString())};
                final boolean[] v2 = {Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getPOWER_B().dps.get("2")).toString())};
                ROOMS.get(i).getPOWER().registerDevListener(new IDevListener() {
                    @Override
                    public void onDpUpdate(String devId, String dpStr) {
                        if (dpStr.length() <18) { //h > (v[0]+1500)
                            Log.d("powerActions","action start");
                            Log.d("powerActions","action"+dpStr);
                            Log.d("powerActions","before action "+v1[0]+" "+v2[0]);
                            try {
                                JSONObject l1 = new JSONObject(dpStr);
                                v1[0] = l1.getBoolean("1");
                            }
                            catch(JSONException e) {
                                Log.d("powerActions","l1 error "+e.getMessage());
                            }
                            try {
                                JSONObject l2 = new JSONObject(dpStr);
                                v2[0] = l2.getBoolean("2");
                            }
                            catch(JSONException e) {
                                Log.d("powerActions","l2 error "+e.getMessage());
                            }
                            if (v1[0] && v2[0]) {
                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(2);
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power on",act);
                            }
                            else if (v1[0]) {
                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(1);
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power byCard",act);
                            }
                            else {
                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(0);
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power off",act);
                            }
                            Log.d("powerActions","after action "+v1[0]+" "+v2[0]);
                            Log.d("powerActions","action finish");
                            Log.d("powerActions","________________________________________________________");
                        }
                    }

                    @Override
                    public void onRemoved(String devId) {

                    }

                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }

                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getCURTAIN_B() != null) {
                ROOMS.get(i).getCURTAIN().registerDevListener(new IDevListener() {
                    @Override
                    public void onDpUpdate(String devId, String dpStr) {
                        Log.d("curtainAction",dpStr);
                        try {
                            JSONObject object = new JSONObject(dpStr);
                            if (Objects.requireNonNull(object.get(ROOMS.get(finalI).curtainControl.ControlDP)).toString().equals(ROOMS.get(finalI).curtainControl.Open)) {
                                ROOMS.get(finalI).curtainStatus = 1;
                                ROOMS.get(finalI).getFireRoom().child("curtainStatus").setValue(1);
                            }
                            else if (Objects.requireNonNull(object.get(ROOMS.get(finalI).curtainControl.ControlDP)).toString().equals(ROOMS.get(finalI).curtainControl.Close)) {
                                ROOMS.get(finalI).curtainStatus = 0;
                                ROOMS.get(finalI).getFireRoom().child("curtainStatus").setValue(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onRemoved(String devId) {

                    }

                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }

                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getMOTIONSENSOR_B() != null ) {
                ROOMS.get(i).getMOTIONSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("motion" , dpStr.toString());
                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " motion detected",act);
                        if (AC_SENARIO_Status[finalI]) {
                            Log.d("acSenario"+ROOMS.get(finalI).RoomNumber ,"stop");
                            AC_SENARIO_Status[finalI] = false ;
                            AcHandlers[finalI].removeCallbacks(TempRunnableList[finalI]);
                        }
                        else {
                            String t ="";
                            if (ROOMS.get(finalI).acVariables != null) {
                                if (ROOMS.get(finalI).acVariables.TempClient == 0) {
                                    if (ROOMS.get(finalI).acVariables.TempChars == 3) {
                                        t="240";
                                    }
                                    else if (ROOMS.get(finalI).acVariables.TempChars == 2) {
                                        t="24";
                                    }
                                }
                                else {
                                    Log.d("acSenario"+ROOMS.get(finalI).RoomNumber ,"chars "+ROOMS.get(finalI).acVariables.TempChars);
//                                    if (ROOMS.get(finalI).acVariables.TempChars == 3) {
//                                        t = String.valueOf((ROOMS.get(finalI).acVariables.TempClient * 10));
//                                    }
//                                    else if (ROOMS.get(finalI).acVariables.TempChars == 2) {
                                    t = String.valueOf(ROOMS.get(finalI).acVariables.TempClient);
//                                    }
                                }
                                String dp = "{\""+ROOMS.get(finalI).acVariables.TempSetDP+"\":"+t+"}";
                                Log.d("acSenario"+ROOMS.get(finalI).RoomNumber ,dp);
                                if (ROOMS.get(finalI).getAC() != null ) {
                                    ROOMS.get(finalI).getAC().publishDps(dp,TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            Log.d("acSenario"+ROOMS.get(finalI).RoomNumber  ,error+" "+code);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            Log.d("acSenario"+ROOMS.get(finalI).RoomNumber ,"clientBack "+ROOMS.get(finalI).acVariables.TempClient);
                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setMotionSensorStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH1_B() != null) {
                ROOMS.get(i).getSWITCH1().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 1 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 1 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 1 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 1 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch1Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH2_B() != null) {
                ROOMS.get(i).getSWITCH2().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 2 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 2 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 2 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 2 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch2Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH3_B() != null) {
                ROOMS.get(i).getSWITCH3().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 3 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 3 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 3 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 3 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch3Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH4_B() != null) {
                ROOMS.get(i).getSWITCH4().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 4 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 4 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 4 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 4 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch4Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH5_B() != null) {
                ROOMS.get(i).getSWITCH5().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 5 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 5 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 5 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 5 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch5Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH6_B() != null) {
                ROOMS.get(i).getSWITCH6().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 6 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 6 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 6 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 6 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch6Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH7_B() != null) {
                ROOMS.get(i).getSWITCH7().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 7 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 7 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 7 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 7 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch7Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH8_B() != null) {
                ROOMS.get(i).getSWITCH8().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.toString().length() <17) {
                            if (dpStr.get("switch_1") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("1").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("1").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 8 Button 1 pressed", act);
                            }
                            if (dpStr.get("switch_2") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 8 Button 2 pressed", act);
                            }
                            if (dpStr.get("switch_3") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("3").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("3").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 8 Button 3 pressed", act);
                            }
                            if (dpStr.get("switch_4") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("4").setValue(3);
                                } else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("4").setValue(0);
                                }
                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " switch 8 Button 4 pressed", act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch8Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getGATEWAY_B() != null) {
                if (ROOMS.get(i).getGATEWAY_B().getIsOnline()) {
                    setRoomOnlineOffline(ROOMS.get(finalI),"1");
                }
                else {
                    setRoomOnlineOffline(ROOMS.get(finalI),"0");
                }
                if (ROOMS.get(i).getGATEWAY() != null) {
                    ROOMS.get(i).getGATEWAY().registerDeviceListener(new IDeviceListener() {
                        @Override
                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        }
                        @Override
                        public void onRemoved(String devId) {
                            ROOMS.get(finalI).setZBGatewayStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                        }
                        @Override
                        public void onStatusChanged(String devId, boolean online) {
                            Log.d("onlineChange" ,ROOMS.get(finalI).RoomNumber + " " +online );
                            if (online) {
                                setRoomOnlineOffline(ROOMS.get(finalI),"1");
                            }
                            else {
                                setRoomOnlineOffline(ROOMS.get(finalI),"0");
                            }
                        }
                        @Override
                        public void onNetworkStatusChanged(String devId, boolean status) {

                        }
                        @Override
                        public void onDevInfoUpdate(String devId) {

                        }
                    });
                }
            }
            if (ROOMS.get(i).getLOCK_B() != null) {
                ROOMS.get(i).getLOCK().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("LockAction" , dpStr.toString());
                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getLOCK_B().name).child("1").setValue(0);
                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " lock action ",act);
                        if (dpStr.get("residual_electricity") != null) {
                            ROOMS.get(finalI).getFireRoom().child("lockBattery").setValue(Objects.requireNonNull(dpStr.get("residual_electricity")).toString());
                        }

                    }

                    @Override
                    public void onRemoved(String devId) {

                    }

                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }

                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
        }
    }

    static public void setFireRoomsListener(Activity act) {
        for (int i=0;i<ROOMS.size();i++) {
            int finalI = i;
            if (ROOMS.get(i).getSERVICE1_B() != null) {
                int[] v1 = {0};
                int[] v2 = {0};
                int[] v3 = {0};
                int[] v4 = {0};
                ROOMS.get(i).CleanupListener = ROOMS.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (CLEANUP[finalI]) {
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\" : false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            CLEANUP[finalI] = false ;
                                        }
                                    });
                                    if (v1[0] > 0) {
                                        cancelServiceOrder(ROOMS.get(finalI),"Cleanup");
                                    }
                                    v1[0] = 1;
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0){
                                if (!CLEANUP[finalI]) {
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\" : true}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            CLEANUP[finalI] = true ;
                                        }
                                    });
                                    addCleanupOrder(ROOMS.get(finalI));
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).LaundryListener = ROOMS.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (LAUNDRY[finalI]) {
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\""+MyApp.ProjectVariables.laundryButton+"\" : false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            LAUNDRY[finalI] = false ;
                                        }
                                    });
                                    if (v2[0] > 0) {
                                        cancelServiceOrder(ROOMS.get(finalI),"Laundry");
                                    }
                                    v2[0] = 1;
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                if (!LAUNDRY[finalI]) {
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\""+MyApp.ProjectVariables.laundryButton+"\" : true}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            LAUNDRY[finalI] = true ;
                                        }
                                    });
                                    addLaundryOrder(ROOMS.get(finalI));
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).CheckoutListener = ROOMS.get(i).getFireRoom().child("Checkout").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B() != null) {
                                    if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                                        if (CHECKOUT[finalI]) {
                                            TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {
                                                    CHECKOUT[finalI] = false ;
                                                }
                                            });
                                            if (v3[0] > 0) {
                                                cancelServiceOrder(ROOMS.get(finalI),"Checkout");
                                            }
                                            v3[0] = 1;
                                        }
                                    }
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B() != null) {
                                    if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                                        if (!CHECKOUT[finalI]) {
                                            TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":true}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {
                                                    CHECKOUT[finalI] = true ;
                                                }
                                            });
                                            addCheckoutOrder(ROOMS.get(finalI));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).DNDListener = ROOMS.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                                    if (DND[finalI]) {
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }
                                            @Override
                                            public void onSuccess() {
                                                DND[finalI] = false ;
                                            }
                                        });
                                    }
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                                    if (!DND[finalI]) {
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":true}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }
                                            @Override
                                            public void onSuccess() {
                                                DND[finalI] = true ;
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (v4[0] > 0) {
                                    cancelServiceOrder(ROOMS.get(finalI),"SOS");
                                }
                                v4[0] = 1;
                            }
                            else {
                                addSOSOrder(ROOMS.get(finalI));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            ROOMS.get(i).roomStatusListener = ROOMS.get(i).getFireRoom().child("roomStatus").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ) {
                            Log.d("roomChangedTo" ,snapshot.getValue().toString() );
                            if (snapshot.getValue().toString().equals("3") && ROOMS.get(finalI).roomStatus != 3) {
                                Log.d("checkoutMood","started");
                                checkoutModeRoom(ROOMS.get(finalI));
                            }
                            else if (snapshot.getValue().toString().equals("2") && ROOMS.get(finalI).roomStatus != 2) {
                                Log.d("checkInModeTest","started");
                                checkInModeRoom(ROOMS.get(finalI));
                                ROOMS.get(finalI).firstDoorOpen = true;
                            }
                            ROOMS.get(finalI).roomStatus = Integer.parseInt(snapshot.getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).ClientInListener = ROOMS.get(i).getFireRoom().child("ClientIn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null ){
                        ROOMS.get(finalI).ClientIn = Integer.parseInt( snapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (ROOMS.get(i).getPOWER_B() != null && ROOMS.get(i).getPOWER() != null) {
                if (ROOMS.get(i).getPOWER_B().dps.get("1") != null) {
                    int[] counter = {0};
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1")
                    ,ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("powerActions",snapshot.getValue().toString());
                                if (counter[0] > 0) {
                                    if (Integer.parseInt(snapshot.getValue().toString()) == 0) {
                                        if (ROOMS.get(finalI).getPOWER() != null ) {
                                            ROOMS.get(finalI).getPOWER().publishDps("{\"1\": false, \"2\": false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                    else if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                        if (ROOMS.get(finalI).getPOWER() != null) {
                                            ROOMS.get(finalI).getPOWER().publishDps("{\"1\": true, \"2\": false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                    else if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                        if (ROOMS.get(finalI).getPOWER() != null) {
                                            ROOMS.get(finalI).getPOWER().publishDps("{\"1\": true, \"2\": true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                    Log.d("powerActions",code+" "+error);
                                                }

                                                @Override
                                                public void onSuccess() {
                                                    Log.d("powerActions","sent");
                                                }
                                            });
                                        }
                                        else {
                                            Log.d("powerActions","null");
                                        }
                                    }
                                }
                                if (counter[0] == 0) {
                                    counter[0]++;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    })));
                }
            }
            if (ROOMS.get(i).getSWITCH1_B() != null && ROOMS.get(i).getSWITCH1() != null) {
                if (ROOMS.get(i).getSWITCH1_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        Log.d("S1FBvalue",snapshot.getValue().toString());
                                        if (snapshot.getValue().toString().equals("1")) {
                                            Log.d("S1FBvalue","is 1");
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH1_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("S1FBvalue","success");
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    Log.d("S1FBvalue",error);
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (snapshot.getValue().toString().equals("2")) {
                                            Log.d("S1FBvalue","is 2");
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH1_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("S1FBvalue","success");
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    Log.d("S1FBvalue",error);
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH1_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2")
                            ,ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("S1FBvalue",snapshot.getValue().toString());
                                if (snapshot.getValue().toString().equals("1")) {
                                    Log.d("S1FBvalue","is 1");
                                    turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH1_B(), "2", new CallbackResult() {
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
                                            Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                if (snapshot.getValue().toString().equals("2")) {
                                    Log.d("S1FBvalue","is 2");
                                    turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH1_B(), "2", new CallbackResult() {
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
                                            Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    })));
                }
                if (ROOMS.get(i).getSWITCH1_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        Log.d("S1FBvalue",snapshot.getValue().toString());
                                        if (snapshot.getValue().toString().equals("1")) {
                                            Log.d("S1FBvalue","is 1");
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH1_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (snapshot.getValue().toString().equals("2")) {
                                            Log.d("S1FBvalue","is 2");
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH1_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH1_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        Log.d("S1FBvalue",snapshot.getValue().toString());
                                        if (snapshot.getValue().toString().equals("1")) {
                                            Log.d("S1FBvalue","is 1");
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH1_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (snapshot.getValue().toString().equals("2")) {
                                            Log.d("S1FBvalue","is 2");
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH1_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getSWITCH2_B() != null && ROOMS.get(i).getSWITCH2() != null) {
                if (ROOMS.get(i).getSWITCH2_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH2_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH2_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH2_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH2_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH2_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH2_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH2_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH2_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH2_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH2_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH2_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getSWITCH3_B() != null && ROOMS.get(i).getSWITCH3() != null) {
                if (ROOMS.get(i).getSWITCH3_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH3_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH3_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH3_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH3_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH3_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH3_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH3_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH3_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH3_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH3_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH3_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getSWITCH4_B() != null && ROOMS.get(i).getSWITCH4() != null) {
                if (ROOMS.get(i).getSWITCH4_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH4_B(), "1", new CallbackResult() {
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
                                            Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH4_B(), "1", new CallbackResult() {
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
                                            Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    })));
                }
                if (ROOMS.get(i).getSWITCH4_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH4_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH4_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(0);
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH4_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH4_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH4_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH4_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH4_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH4_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            }) ));
                }
            }
            if (ROOMS.get(i).getSWITCH5_B() != null && ROOMS.get(i).getSWITCH5() != null) {
                if (ROOMS.get(i).getSWITCH5_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH5_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH5_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH5_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH5_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH5_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            ROOMS.get(finalI).getSWITCH5().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("2").setValue(0);
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH5_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH5_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH5_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH5_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH5_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH5_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH5_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH5_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getSWITCH6_B() != null && ROOMS.get(i).getSWITCH6() != null) {
                if (ROOMS.get(i).getSWITCH6_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH6_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH6_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH6_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH6_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH6_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            ROOMS.get(finalI).getSWITCH6().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("2").setValue(0);
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH6_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH6_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH6_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            }) ));
                }
                if (ROOMS.get(i).getSWITCH6_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH6_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH6_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH6_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH6_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getSWITCH7_B() != null && ROOMS.get(i).getSWITCH7() != null) {
                if (ROOMS.get(i).getSWITCH7_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH7_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH7_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH7_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH7_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH7_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            ROOMS.get(finalI).getSWITCH7().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("2").setValue(0);
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH7_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH7_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH7_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH7_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH7_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH7_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH7_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH7_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getSWITCH8_B() != null && ROOMS.get(i).getSWITCH8() != null) {
                if (ROOMS.get(i).getSWITCH8_B().dps.get("1") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("1"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH8_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("1").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("1").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH8_B(), "1", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("1").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("1").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH8_B().dps.get("2") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("2"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH8_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH8_B(), "2", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            ROOMS.get(finalI).getSWITCH8().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("2").setValue(0);
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH8_B().dps.get("3") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("3"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("3").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH8_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("3").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("3").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH8_B(), "3", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("3").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("3").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
                if (ROOMS.get(i).getSWITCH8_B().dps.get("4") != null) {
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("4"),
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH8_B().name).child("4").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                            turnSwitchButtonOn(ROOMS.get(finalI).getSWITCH8_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("4").setValue(3);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("4").setValue(0);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                            turnSwitchButtonOff(ROOMS.get(finalI).getSWITCH8_B(), "4", new CallbackResult() {
                                                @Override
                                                public void onSuccess() {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("4").setValue(0);
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH8_B().name).child("4").setValue(3);
                                                    Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })));
                }
            }
            if (ROOMS.get(i).getAC_B() != null && ROOMS.get(i).getAC() != null) {
                TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(ROOMS.get(i).getAC_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
                    @Override
                    public void onSuccess(List<TaskListBean> result) {
                        if (ROOMS.get(finalI).acVariables.PowerDP != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(ROOMS.get(finalI).acVariables.PowerDP)) != null) {
                                Log.d("setDevicesList",ROOMS.get(finalI).acVariables.PowerDP+" power");
                                if (Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(ROOMS.get(finalI).acVariables.PowerDP))).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
                                }
                                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power"),
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+ ROOMS.get(finalI).acVariables.PowerDP +"\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
                                                            }
                                                            @Override
                                                            public void onSuccess() {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
                                                            }
                                                        });
                                                    }
                                                    if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+ROOMS.get(finalI).acVariables.PowerDP+"\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
                                                            }
                                                            @Override
                                                            public void onSuccess() {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        })));
                            }
                        }
                        if (ROOMS.get(finalI).acVariables.TempSetDP != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(ROOMS.get(finalI).acVariables.TempSetDP)) != null) {
                                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp"),
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    Log.d("tempModify" , snapshot.getValue().toString());
                                                    try {
                                                        int newTemp = Integer.parseInt(snapshot.getValue().toString());
                                                        if (ROOMS.get(finalI).acVariables.TempChars == 3) {
                                                            newTemp = newTemp*10;
                                                        }
                                                        Log.d("tempModify" , newTemp+"");
                                                        ROOMS.get(finalI).getAC().publishDps("{\" " + ROOMS.get(finalI).acVariables.TempSetDP + "\":" + newTemp + "}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                //ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").setValue(Integer.parseInt(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(ROOMS.get(finalI).acVariables.TempSetDP))).toString()));
                                                            }

                                                            @Override
                                                            public void onSuccess() {
                                                            }
                                                        });
                                                    }
                                                    catch (Exception e) {
                                                        Log.d("tempModify" , e.getMessage());
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        })));
                            }
                        }
                        if (ROOMS.get(finalI).acVariables.FanDP != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(ROOMS.get(finalI).acVariables.FanDP)) != null) {
                                int[] lastIndex = {0};
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null) {
                                            String value = snapshot.getValue().toString();
                                            for (int i=0 ; i < ROOMS.get(finalI).acVariables.FanValues.length;i++) {
                                                if (ROOMS.get(finalI).acVariables.FanValues[i].contains(value)) {
                                                    lastIndex[0] = i;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").setValue(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(ROOMS.get(finalI).acVariables.FanDP))).toString());
                                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan"),
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    Log.d("fanModify"+ROOMS.get(finalI).RoomNumber , snapshot.getValue().toString()+" "+lastIndex[0]);
                                                    if (lastIndex[0]+1 == ROOMS.get(finalI).acVariables.FanValues.length) {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+ ROOMS.get(finalI).acVariables.FanDP +"\":\""+ROOMS.get(finalI).acVariables.FanValues[lastIndex[0]]+"\"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                            }
                                                            @Override
                                                            public void onSuccess() {
                                                            }
                                                        });
                                                        lastIndex[0] = 0 ;
                                                    }
                                                    else {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+ ROOMS.get(finalI).acVariables.FanDP +"\":\""+ROOMS.get(finalI).acVariables.FanValues[lastIndex[0]++]+"\"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                            }
                                                            @Override
                                                            public void onSuccess() {
                                                            }
                                                        });
                                                    }

                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        })));
                            }
                        }
                        if (ROOMS.get(finalI).acVariables.TempCurrentDP != 0) {
                            Log.d("currentTemp",ROOMS.get(finalI).acVariables.TempCurrentDP+" "+ROOMS.get(finalI).acVariables.TempCurrentDP);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {

                    }
                });
            }
            if (ROOMS.get(i).getLOCK_B() != null && ROOMS.get(i).getLOCK() != null) {
                final Calendar[] ca = {Calendar.getInstance(Locale.getDefault())};
                final Long[] lastOpen = {ca[0].getTimeInMillis()};
                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getLOCK_B().name).child("1"),
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getLOCK_B().name).child("1").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("LockAction:",snapshot.toString());
                                if (snapshot.getValue() != null) {
                                    if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                        Calendar ca = Calendar.getInstance(Locale.getDefault());
                                        long now = ca.getTimeInMillis();
                                        if (now > (lastOpen[0] +5000)) {
                                            OpenTheDoor(act,ROOMS.get(finalI), new RequestOrder() {
                                                @Override
                                                public void onSuccess(String token) {
                                                    Calendar ca = Calendar.getInstance(Locale.getDefault());
                                                    lastOpen[0] = ca.getTimeInMillis();
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Log.d("LockAction:",error);
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getLOCK_B().name).child("1").setValue(0);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        })));
            }
            if (ROOMS.get(i).getCURTAIN_B() != null && ROOMS.get(i).getCURTAIN() != null) {
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getCURTAIN_B().name).child("control").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            String order = snapshot.getValue().toString();
                            switch (order) {
                                case "open":
                                case "Open":
                                case "OPEN":
                                    ROOMS.get(finalI).openCurtain();
                                    break;
                                case "close":
                                case "Close":
                                case "CLOSE":
                                    ROOMS.get(finalI).closeCurtain();
                                    break;
                                case "stop":
                                case "Stop":
                                case "STOP":
                                    ROOMS.get(finalI).stopCurtain();
                                    break;
                                case "continue":
                                case "Continue":
                                case "CONTINUE":
                                    ROOMS.get(finalI).continueCurtain();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    static void removeFireRoomsDevicesListeners() {
        for (DatabaseReference_ValueEventListener vel : RoomsDevicesReferencesListeners) {
            vel.ref.removeEventListener(vel.listener);
        }
        Log.d("refreshSystem","2 remove fire rooms listener");
    }
    //_________________________________________________________

    // set Buttons click functions
    //_________________________________________________________
    static void turnSwitchButtonOn(DeviceBean S,String B,CallbackResult c) {
        TuyaHomeSdk.newDeviceInstance(S.devId).publishDps("{\"" + B + "\": true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                TuyaHomeSdk.newDeviceInstance(S.devId).publishDps("{\"" + B + "\": true}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        c.onFail(error);
                    }

                    @Override
                    public void onSuccess() {
                        c.onSuccess();
                    }
                });
            }

            @Override
            public void onSuccess() {
                c.onSuccess();
            }
        });
    }

    static void turnSwitchButtonOff(DeviceBean S,String B,CallbackResult c) {
        TuyaHomeSdk.newDeviceInstance(S.devId).publishDps("{\"" + B + "\": false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                TuyaHomeSdk.newDeviceInstance(S.devId).publishDps("{\"" + B + "\": false}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        c.onFail(error);
                    }

                    @Override
                    public void onSuccess() {
                        c.onSuccess();
                    }
                });
            }

            @Override
            public void onSuccess() {
                c.onSuccess();
            }
        });
    }
    //_________________________________________________________

    // Add & Cancel Orders
    //_________________________________________________________

    public static void addCleanupOrder(ROOM room) {
        Log.d("addCleanupRsp" , "started");
        String url = MyApp.THE_PROJECT.url + "reservations/addCleanupOrderControlDevice"+addCleanupCounter ;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addCleanupRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("addCleanupRsp" , result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("addCleanupRsp" , e.toString());
                }
            }
            else {
                Log.d("addCleanupRsp" , "error cleanup "+room.RoomNumber);
            }
        }, error -> Log.d("addCleanupRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(room.id));
                return params;
            }
        };
        CLEANUP_QUEUE.add(addOrder);
        addCleanupCounter++;
        if (addCleanupCounter == 5) {
            addCleanupCounter = 1 ;
        }
    }

    public static void addLaundryOrder(ROOM room) {
        Log.d("addLaundryRsp" , "started");
        String url = MyApp.THE_PROJECT.url + "reservations/addLaundryOrderControlDevice"+addLaundryCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addLaundryRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("addLaundryRsp" , result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("addLaundryRsp" , e.toString());
                }
            }
            else {
                Log.d("addLaundryRsp" , "error laundry "+room.RoomNumber);
            }
        }, error -> Log.d("addLaundryRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(room.id));
                return params;
            }

        };
        LAUNDRY_QUEUE.add(addOrder);
        addLaundryCounter++;
        if (addLaundryCounter == 5) {
            addLaundryCounter = 1 ;
        }
    }

    public static void addCheckoutOrder (ROOM room) {
        Log.d("addCheckoutRsp" , "started");
        String url = MyApp.THE_PROJECT.url + "reservations/addCheckoutOrderControlDevice"+addCheckoutCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addCheckoutRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("addCheckoutRsp" , result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("addCheckoutRsp" ,e.toString());
                }
            }
            else {
                Log.d("addCheckoutRsp" ,"error checkout "+room.RoomNumber);
            }
        }, error -> Log.d("addCheckoutRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(room.id));
                return params;
            }

        };
        CHECKOUT_QUEUE.add(addOrder);
        addCheckoutCounter++;
        if (addCheckoutCounter == 5) {
            addCheckoutCounter = 1 ;
        }
    }

    public static void addDNDOrder(ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/putRoomOnDNDModeControlDevice"+addDNDCounter;
        StringRequest request = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addDNDRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("addDNDRsp" , result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("addDNDRsp" ,e.toString());
                }
            }
            else {
                Log.d("addDNDRsp" ,"error laundry "+room.RoomNumber);
            }
        }
                , error -> Log.d("addDNDRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("room_id", String.valueOf(room.id));
                return params;
            }
        };
        DND_Queue.add(request);
        addDNDCounter++;
        if (addDNDCounter == 5) {
            addDNDCounter = 1 ;
        }
    }

    public static void addSOSOrder(ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/addSOSOrderControlDevice";
        StringRequest request = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addSOSRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("addSOSRsp" , result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("addSOSRsp" ,e.toString());
                }
            }
            else {
                Log.d("addSOSRsp" ,"error sos "+room.RoomNumber);
            }
        }
                , error -> Log.d("addSOSRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("room_id", String.valueOf(room.id));
                return params;
            }
        };
        DND_Queue.add(request);
        addDNDCounter++;
        if (addDNDCounter == 5) {
            addDNDCounter = 1 ;
        }
    }

    public static void cancelServiceOrder(ROOM room , String type) {
        Log.d("removeCheckoutRsp" , "pressed");
        String url = MyApp.THE_PROJECT.url + "reservations/cancelServiceOrderControlDevice"+cancelOrderCounter;
        StringRequest removeOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("removeCheckoutRsp", response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("removeCheckoutRsp", result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("removeCheckoutRsp", e.toString());
                }
            }
            else {
                Log.d("removeCheckoutRsp", "error " + type +" " +room.RoomNumber);
            }
        }, error -> Log.d("removeCheckoutRsp", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf( room.id));
                params.put("order_type",type);
                return params;
            }
        };
        CLEANUP_QUEUE.add(removeOrder);
        cancelOrderCounter++;
        if (cancelOrderCounter == 5) {
            cancelOrderCounter = 1 ;
        }
    }

    public static void cancelDNDOrder(ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/cancelDNDOrderControlDevice"+cancelDNDCounter;
        StringRequest rrr = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("cancelPressed", response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Log.d("cancelPressed", result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("cancelPressed",e.toString());
                }
            }
            else {
                Log.d("cancelPressed","error dnd " +room.RoomNumber);
            }
        }, error -> Log.d("cancelPressed", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("room_id", String.valueOf(room.id));
                return params;
            }
        };
        DND_Queue.add(rrr);
        cancelDNDCounter++;
        if (cancelDNDCounter == 5) {
            cancelDNDCounter = 1 ;
        }
    }

    //__________________________________________________________

    // set devices installed
    //_________________________________________________________
    static void setDoorSensorStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsDoorSensorInstalled";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("doorSensor" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("doorSensor" , "doorSensor updated successfully");
                    }
                    else {
                        Log.e("doorSensor" , "doorSensor update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("doorSensor" , "doorSensor update failed "+ e);
                }
            }, error -> Log.e("doorSensor" , "doorSensor update failed "+error.toString()))
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids",ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    static void setServiceSwitchStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsServiceSwitchInstalled";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("serviceSwitch" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("serviceSwitch" , "serviceSwitch updated successfully");
                    }
                    else {
                        Log.e("serviceSwitch" , "serviceSwitch update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("serviceSwitch" , "serviceSwitch update failed "+ e);
                }
            }, error -> Log.e("serviceSwitch" , "serviceSwitch update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    static void setThermostatStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsThermostatInstalled";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("thermostat" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("thermostat" , "thermostat updated successfully");
                    }
                    else {
                        Log.e("thermostat" , "thermostat update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("thermostat" , "thermostat update failed "+ e);
                }
            }, error -> Log.e("thermostat" , "thermostat update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    static void setPowerSwitchStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsPowerSwitchInstalled";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("power " , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("power " , "power updated successfully");
                    }
                    else {
                        Log.e("power " , "power update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("power " , "power update failed "+ e);
                }
            }, error -> Log.e("power " , "power update failed "+error.toString()))
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    static void setCurtainSwitchStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsCurtainInstalled";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("curtain" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("curtain" , "curtain updated successfully");
                    }
                    else {
                        Log.e("curtain" , "curtain update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("curtain" , "curtain update failed "+ e);
                }
            }, error -> Log.e("curtain" , "curtain update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    static void setMotionSensorStatus(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsMotionSensorInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("motion" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    Log.e("motion" , "motion updated successfully");
                }
                else {
                    Log.e("motion" , "motion update failed "+res.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("motion" , "motion update failed "+ e);
            }
        }, error -> Log.e("motion" , "motion update failed "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ.add(tabR);
    }

    static void setSwitch1Status(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch1Installed";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("switch1" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch1" , "switch1 updated successfully");
                    }
                    else {
                        Log.e("switch1" , "switch1 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch1" , "switch1 update failed "+ e);
                }
            }, error -> Log.e("switch1" , "switch1 update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    static void setSwitch2Status(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch2Installed";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("switch2" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch2" , "switch2 updated successfully");
                    }
                    else {
                        Log.e("switch2" , "switch2 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch2" , "switch2 update failed "+ e);
                }
            }, error -> Log.e("switch2" , "switch2 update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    static void setSwitch3Status(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch3Installed";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("switch3" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch3" , "switch3 updated successfully");
                    }
                    else {
                        Log.e("switch3" , "switch3 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch3" , "switch3 update failed "+ e);
                }
            }, error -> Log.e("switch3" , "switch3 update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    static void setSwitch4Status(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch4Installed";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("switch4" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch4" , "switch4 updated successfully");
                    }
                    else {
                        Log.e("switch4" , "switch4 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch4" , "switch4 update failed "+ e);
                }
            }, error -> Log.e("switch4" , "switch4 update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    static void setSwitch5Status(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch5Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("switch5" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    Log.e("switch5" , "switch5 updated successfully");
                }
                else {
                    Log.e("switch5" , "switch5 update failed "+res.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("switch5" , "switch5 update failed "+ e);
            }
        }, error -> Log.e("switch5" , "switch5 update failed "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ1.add(tabR);
    }

    static void setSwitch6Status(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch6Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("switch6" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    Log.e("switch6" , "switch6 updated successfully");
                }
                else {
                    Log.e("switch6" , "switch6 update failed "+res.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("switch6" , "switch6 update failed "+ e);
            }
        }, error -> Log.e("switch6" , "switch6 update failed "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ1.add(tabR);
    }

    static void setSwitch7Status(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch7Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("switch7" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    Log.e("switch7" , "switch7 updated successfully");
                }
                else {
                    Log.e("switch7" , "switch7 update failed "+res.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("switch7" , "switch7 update failed "+ e);
            }
        }, error -> Log.e("switch7" , "switch7 update failed "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ1.add(tabR);
    }

    static void setSwitch8Status(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch8Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("switch8" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    Log.e("switch8" , "switch8 updated successfully");
                }
                else {
                    Log.e("switch8" , "switch8 update failed "+res.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("switch8" , "switch8 update failed "+ e);
            }
        }, error -> Log.e("switch8" , "switch8 update failed "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ1.add(tabR);
    }

    static void setZBGatewayStatus(String ids , String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsGatewayInstalled";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
                Log.e("gateway" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("gateway" , "gateway updated successfully");
                    }
                    else {
                        Log.e("gateway" , "gateway update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("gateway" , "gateway update failed "+ e);
                }
            }, error -> Log.e("gateway" , "gateway update failed "+error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    static void setLockStatus(String ids , String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsLockInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("lock" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    Log.e("lock" , "lock updated successfully");
                }
                else {
                    Log.e("lock" , "lock update failed "+res.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("lock" , "lock update failed "+e);
            }
        }, error -> Log.e("lock" , "lock update failed "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ.add(tabR);
    }

    static void setRoomsDevicesInstalledInDB() {
        if (PowerUnInstalled != null) {
            setPowerSwitchStatus(PowerUnInstalled,"0");
        }
        if (GatewayUnInstalled != null) {
            setZBGatewayStatus(GatewayUnInstalled,"0");
        }
        if (ACUnInstalled != null) {
            setThermostatStatus(ACUnInstalled,"0");
        }
        if (MotionUnInstalled != null) {
            setMotionSensorStatus(MotionUnInstalled,"0");
        }
        if (DoorUnInstalled != null) {
            setDoorSensorStatus(DoorUnInstalled,"0");
        }
        if (ServiceUnInstalled != null) {
            setServiceSwitchStatus(ServiceUnInstalled,"0");
        }
        if (S1UnInstalled != null) {
            setSwitch1Status(S1UnInstalled,"0");
        }
        if (S2UnInstalled != null) {
            setSwitch2Status(S2UnInstalled,"0");
        }
        if (S3UnInstalled != null) {
            setSwitch3Status(S3UnInstalled,"0");
        }
        if (S4UnInstalled != null) {
            setSwitch4Status(S4UnInstalled,"0");
        }
        if (S5UnInstalled != null) {
            setSwitch5Status(S5UnInstalled,"0");
        }
        if (S6UnInstalled != null) {
            setSwitch6Status(S6UnInstalled,"0");
        }
        if (S7UnInstalled != null) {
            setSwitch7Status(S7UnInstalled,"0");
        }
        if (S8UnInstalled != null) {
            setSwitch8Status(S8UnInstalled,"0");
        }
        if (CurtainUnInstalled != null) {
            setCurtainSwitchStatus(CurtainUnInstalled,"0");
        }
        if (LockUnInstalled != null) {
            setLockStatus(LockUnInstalled,"0");
        }
        if (PowerInstalled != null) {
            setPowerSwitchStatus(PowerInstalled,"1");
        }
        if (GatewayInstalled != null) {
            setZBGatewayStatus(GatewayInstalled,"1");
        }
        if (ACInstalled != null) {
            setThermostatStatus(ACInstalled,"1");
        }
        if (MotionInstalled != null) {
            setMotionSensorStatus(MotionInstalled,"1");
        }
        if (DoorInstalled != null) {
            setDoorSensorStatus(DoorInstalled,"1");
        }
        if (ServiceInstalled != null) {
            setServiceSwitchStatus(ServiceInstalled,"1");
        }
        if (S1Installed != null) {
            setSwitch1Status(S1Installed,"1");
        }
        if (S2Installed != null) {
            setSwitch2Status(S2Installed,"1");
        }
        if (S3Installed != null) {
            setSwitch3Status(S3Installed,"1");
        }
        if (S4Installed != null) {
            setSwitch4Status(S4Installed,"1");
        }
        if (S5Installed != null) {
            setSwitch5Status(S5Installed,"1");
        }
        if (S6Installed != null) {
            setSwitch6Status(S6Installed,"1");
        }
        if (S7Installed != null) {
            setSwitch7Status(S7Installed,"1");
        }
        if (S8Installed != null) {
            setSwitch8Status(S8Installed,"1");
        }
        if (CurtainInstalled != null) {
            setCurtainSwitchStatus(CurtainInstalled,"1");
        }
        if (LockInstalled != null) {
            setLockStatus(LockInstalled,"1");
        }
    }

    static void setRoomOnlineOffline(ROOM room, String status) {
        room.getFireRoom().child("online").setValue(status);
        room.online = Integer.parseInt(status);
    }

    static void setClientInOrOut(ROOM room, String status) {
        room.getFireRoom().child("ClientIn").setValue(status);
    }
    //_________________________________________________________

    void sendRegistrationToServer(String token) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseToken" ;
        StringRequest re  = new StringRequest(Request.Method.POST, url, response -> Log.d("tokenRegister" , response), error -> Log.d("tokenRegister" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> par = new HashMap<>();
                par.put("token" , token);
                par.put("device_id",MyApp.Device_Id);
                return par;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(re);
    }

    static void checkInModeRoom(ROOM THE_ROOM) {
        // get the reservation type
        Log.d("checkInModeTest","getting reservation type");
        THE_ROOM.getFireRoom().child("reservationType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    // reservation type
                    Log.d("checkInModeTest","type: "+snapshot.getValue().toString());
                    String type = snapshot.getValue().toString();
                    if (MyApp.ProjectVariables.getCheckinModeActive()) {
                        // checkIn mood active
                        Log.d("checkInModeTest","mood: active");
                        if (MyApp.checkInActions != null) {
                            // checkIn actions not null
                            if (MyApp.checkInActions.power) {
                                // checkIn power action on
                                THE_ROOM.powerOnRoom(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (MyApp.checkInActions.lights) {
                                            Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    turnLightsOn(THE_ROOM);
                                                }
                                            }, 3000);
                                        }
                                        if (MyApp.checkInActions.curtain) {
                                            THE_ROOM.openCurtain();
                                        }
                                        if (MyApp.checkInActions.ac) {
                                            if (THE_ROOM.getAC_B() != null && THE_ROOM.getAC() != null) {
                                                if (THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.PowerDP)) != null) {
                                                    THE_ROOM.getAC().publishDps("{\""+THE_ROOM.acVariables.PowerDP+"\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        if (type.equals("0")) {
                                            THE_ROOM.powerByCardAfterMinutes(MyApp.ProjectVariables.CheckinModeTime, new RequestCallback() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onFail(String error) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        THE_ROOM.putPowerOnFailedMessage();
                                    }
                                });
                            }
                            else {
                                // checkIn power action off
                                if (type.equals("0")) {
                                    THE_ROOM.powerByCard(new RequestCallback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            THE_ROOM.putPowerOnFailedMessage();
                                        }
                                    });
                                }
                                else {
                                    THE_ROOM.powerOnRoom(new RequestCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(String error) {
                                            THE_ROOM.putPowerOnFailedMessage();
                                        }
                                    });
                                }
                            }
                        }
                        else {
                            // checkIn actions null
                            if (type.equals("0")) {
                                THE_ROOM.powerByCard(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        THE_ROOM.putPowerOnFailedMessage();
                                    }
                                });
                            }
                            else {
                                THE_ROOM.powerOnRoom(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {
                                        THE_ROOM.putPowerOnFailedMessage();
                                    }
                                });
                            }
                        }
                    }
                    else {
                        // checkIn mood inActive
                        Log.d("checkInModeTest","mood: inActive");
                        if (type.equals("0")) {
                            THE_ROOM.powerByCard(new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFail(String error) {
                                    THE_ROOM.putPowerOnFailedMessage();
                                }
                            });
                        }
                        else {
                            THE_ROOM.powerOnRoom(new RequestCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {
                                    THE_ROOM.putPowerOnFailedMessage();
                                }
                            });
                        }
                    }
                }
                else {
                    // reservation type is null
                    Log.d("checkInModeTest","type: null");
                    if (MyApp.ProjectVariables.getCheckinModeActive()) {
                        // checkIn mood active
                        Log.d("checkInModeTest","mood: active");
                        if (MyApp.checkInActions != null) {
                            // checkIn actions not null
                            if (MyApp.checkInActions.power) {
                                // checkIn power action on
                                THE_ROOM.powerOnRoom(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (MyApp.checkInActions.lights) {
                                            Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    turnLightsOn(THE_ROOM);
                                                }
                                            }, 3000);
                                        }
                                        if (MyApp.checkInActions.curtain) {
                                            THE_ROOM.openCurtain();
                                        }
                                        if (MyApp.checkInActions.ac) {
                                            if (THE_ROOM.getAC_B() != null && THE_ROOM.getAC() != null) {
                                                if (THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.PowerDP)) != null) {
                                                    THE_ROOM.getAC().publishDps("{\""+THE_ROOM.acVariables.PowerDP+"\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        THE_ROOM.powerByCardAfterMinutes(MyApp.ProjectVariables.CheckinModeTime, new RequestCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFail(String error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        THE_ROOM.putPowerOnFailedMessage();
                                    }
                                });
                            }
                            else {
                                // checkIn power action off
                                THE_ROOM.powerByCard(new RequestCallback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            THE_ROOM.putPowerOnFailedMessage();
                                        }
                                    });
                            }
                        }
                        else {
                            // checkIn actions null
                            THE_ROOM.powerByCard(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        THE_ROOM.putPowerOnFailedMessage();
                                    }
                                });
                        }
                    }
                    else {
                        // checkIn mood inActive
                        Log.d("checkInModeTest","mood: inActive");
                        THE_ROOM.powerByCard(new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFail(String error) {
                                    THE_ROOM.putPowerOnFailedMessage();
                                }
                            });
                    }
                }
            }

            // getting reservation  type failed
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (MyApp.ProjectVariables.getCheckinModeActive()) {
                    if (MyApp.checkInActions != null) {
                        if (MyApp.checkInActions.power) {
                            THE_ROOM.powerOnRoom(new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,THE_ROOM.RoomNumber+"power on success");
                                    if (MyApp.checkInActions.lights) {
                                        turnLightsOn(THE_ROOM);
                                    }
                                    if (MyApp.checkInActions.curtain) {
                                        if (THE_ROOM.getCURTAIN_B() != null && THE_ROOM.getCURTAIN() != null) {
                                            if (THE_ROOM.getCURTAIN_B().dps.get("1") != null) {
                                                THE_ROOM.getCURTAIN().publishDps("{\" 1\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                    if (MyApp.checkInActions.ac) {
                                        if (THE_ROOM.getAC_B() != null && THE_ROOM.getAC() != null) {
                                            if (THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.PowerDP)) != null) {
                                                THE_ROOM.getAC().publishDps("{\""+THE_ROOM.acVariables.PowerDP+"\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                    THE_ROOM.powerByCardAfterMinutes(MyApp.ProjectVariables.CheckinModeTime, new RequestCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });
                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                    }
                }
                else {
                    THE_ROOM.powerByCard(new RequestCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFail(String error) {
                            THE_ROOM.putPowerOnFailedMessage();
                        }
                    });
                }
            }
        });
    }

    static void checkoutModeRoom(ROOM THE_ROOM)  {
        Log.d("checkoutMood","getting reservation type");
        THE_ROOM.getFireRoom().child("reservationType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Log.d("checkoutMood","type: "+snapshot.getValue().toString());
                    String type = snapshot.getValue().toString();
                    if (type.equals("0")) {
                        Log.d("checkoutMood","type: by card");
                        if (MyApp.ProjectVariables.getCheckoutModeActive()) {
                            Log.d("checkoutMood","mood: active");
                            if (MyApp.checkOutActions != null) {
                                if (MyApp.checkOutActions.power) {
                                    if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                        THE_ROOM.powerByCardAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFail(String error) {

                                            }
                                        });
                                    }
                                    else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                        THE_ROOM.powerOffRoomAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFail(String error) {

                                            }
                                        });
                                    }
                                    THE_ROOM.closeCurtain();
                                }
                                else {
                                    Timer t = new Timer();
                                    t.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (MyApp.checkOutActions.lights) {
                                                turnLightsOff(THE_ROOM);
                                            }
                                            if (MyApp.checkOutActions.ac) {
                                                THE_ROOM.turnAcOff(String.valueOf(THE_ROOM.acVariables.PowerDP), new RequestCallback() {
                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onFail(String error) {

                                                    }
                                                });
                                            }
                                            if (MyApp.checkOutActions.curtain) {
                                                THE_ROOM.closeCurtain();
                                            }
                                        }
                                    }, (long) MyApp.ProjectVariables.CheckoutModeTime * 60 *1000);
                                }
                            }
                            else {
                                if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                    THE_ROOM.powerByCard(new RequestCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });
                                }
                                else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                    THE_ROOM.powerOffRoom(new RequestCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });
                                }
                            }
                        }
                        else {
                            Log.d("checkoutMood","mood: inactive");
                            if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                THE_ROOM.powerByCard(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                            else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                THE_ROOM.powerOffRoom(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                        }
                    }
                    else {
                        Log.d("checkoutMood","type: by link");
                        if (MyApp.ProjectVariables.getCheckoutModeActive()) {
                            Log.d("checkoutMood","mood: active");
                            if (MyApp.checkOutActions != null) {
                                if (MyApp.checkOutActions.power) {
                                    THE_ROOM.powerOffRoomAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFail(String error) {

                                            }
                                        });
                                    THE_ROOM.closeCurtain();
                                }
                                else {
                                    Timer t = new Timer();
                                    t.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (MyApp.checkOutActions.lights) {
                                                turnLightsOff(THE_ROOM);
                                            }
                                            if (MyApp.checkOutActions.ac) {
                                                THE_ROOM.turnAcOff(String.valueOf(THE_ROOM.acVariables.PowerDP), new RequestCallback() {
                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onFail(String error) {

                                                    }
                                                });
                                            }
                                            if (MyApp.checkOutActions.curtain) {
                                                THE_ROOM.closeCurtain();
                                            }
                                        }
                                    }, (long) MyApp.ProjectVariables.CheckoutModeTime * 60 *1000);
                                }
                            }
                            else {
                                THE_ROOM.powerOffRoomAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                        }
                        else {
                            Log.d("checkoutMood","mood: inactive");
                            THE_ROOM.powerOffRoom(new RequestCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                    }
                }
                else {
                    Log.d("checkoutMood","type: null");
                    if (MyApp.ProjectVariables.getCheckoutModeActive()) {
                        Log.d("checkoutMood","mood: active");
                        if (MyApp.checkOutActions != null) {
                            if (MyApp.checkOutActions.power) {
                                if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                    THE_ROOM.powerByCardAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });
                                }
                                else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                    THE_ROOM.powerOffRoomAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });
                                }
                                THE_ROOM.closeCurtain();
                            }
                            else {
                                Timer t = new Timer();
                                t.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (MyApp.checkOutActions.lights) {
                                            turnLightsOff(THE_ROOM);
                                        }
                                        if (MyApp.checkOutActions.ac) {
                                            THE_ROOM.turnAcOff(String.valueOf(THE_ROOM.acVariables.PowerDP), new RequestCallback() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onFail(String error) {

                                                }
                                            });
                                        }
                                        if (MyApp.checkOutActions.curtain) {
                                            THE_ROOM.closeCurtain();
                                        }
                                    }
                                }, (long) MyApp.ProjectVariables.CheckoutModeTime * 60 *1000);
                            }
                        }
                        else {
                            if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                THE_ROOM.powerByCard(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                            else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                THE_ROOM.powerOffRoom(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                        }
                    }
                    else {
                        Log.d("checkoutMood","mood: inactive");
                        if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                            THE_ROOM.powerByCard(new RequestCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                        else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                            THE_ROOM.powerOffRoom(new RequestCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (MyApp.ProjectVariables.getCheckoutModeActive()) {
                    if (MyApp.checkOutActions != null) {
                        if (MyApp.checkOutActions.power) {
                            if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                THE_ROOM.powerByCardAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                            else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                THE_ROOM.powerOffRoomAfterMinutes(MyApp.ProjectVariables.CheckoutModeTime, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                        }
                        else {
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (MyApp.checkOutActions.lights) {
                                        turnLightsOff(THE_ROOM);
                                    }
                                    if (MyApp.checkOutActions.ac) {
                                        THE_ROOM.turnAcOff(String.valueOf(THE_ROOM.acVariables.PowerDP), new RequestCallback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFail(String error) {

                                            }
                                        });
                                    }
                                    if (MyApp.checkOutActions.curtain) {
                                        if (THE_ROOM.getCURTAIN_B() != null && THE_ROOM.getCURTAIN() != null) {
                                            if (THE_ROOM.getCURTAIN_B().dps.get("1") != null) {
                                                THE_ROOM.getCURTAIN().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }, (long) MyApp.ProjectVariables.CheckoutModeTime * 60 *1000);
                        }
                    }
                    else {
                        if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                            THE_ROOM.powerByCard(new RequestCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                        else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                            THE_ROOM.powerOffRoom(new RequestCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                    }
                }
                else {
                    if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                        THE_ROOM.powerByCard(new RequestCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                    else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                        THE_ROOM.powerOffRoom(new RequestCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                }
            }
        });
    }

    public static void runClientBackActions(ROOM room) {
        // get the reservation type
        if (room.roomStatus == 2) {
            if (room.ClientIn == 0) {
                Log.d("doorAction" , "ok");
                room.getFireRoom().child("reservationType").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            Log.d("doorAction" , "null");
                            room.powerOnRoom(new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    int sec = 2 * 60;
                                    if (MyApp.clientBackActions.lights) {
                                        turnLightsOn(room);
                                    }
                                    if (MyApp.clientBackActions.curtain) {
                                        if (room.getCURTAIN_B() != null && room.getCURTAIN() != null) {
                                            if (room.getCURTAIN_B().dps.get("1") != null) {
                                                room.getCURTAIN().publishDps("{\" 1\":true}", new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }

                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                    if (MyApp.clientBackActions.ac) {
                                        if (room.getAC_B() != null && room.getAC() != null) {
                                            room.getAC().publishDps("{\"" + room.acVariables.PowerDP + "\": true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                    if (room.getPOWER_B().getDps().get("8") != null) {
                                        room.getPOWER().publishDps("{\" 8\":" + sec + "}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    } else if (room.getPOWER_B().getDps().get("10") != null) {
                                        room.getPOWER().publishDps("{\" 10\":" + sec + "}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        } else {
                            String type = snapshot.getValue().toString();
                            if (type.equals("0")) {
                                Log.d("doorAction" , "0");
                                room.powerOnRoom(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        int sec = 2 * 60;
                                        if (MyApp.clientBackActions.lights) {
                                            turnLightsOn(room);
                                        }
                                        if (MyApp.clientBackActions.curtain) {
                                            if (room.getCURTAIN_B() != null && room.getCURTAIN() != null) {
                                                if (room.getCURTAIN_B().dps.get("1") != null) {
                                                    room.getCURTAIN().publishDps("{\" 1\":true}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }

                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        if (MyApp.clientBackActions.ac) {
                                            if (room.getAC_B() != null && room.getAC() != null) {
                                                room.getAC().publishDps("{\"" + room.acVariables.PowerDP + "\": true}", new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }

                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (room.getPOWER_B().getDps().get("8") != null) {
                                            room.getPOWER().publishDps("{\" 8\":" + sec + "}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        } else if (room.getPOWER_B().getDps().get("10") != null) {
                                            room.getPOWER().publishDps("{\" 10\":" + sec + "}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                            }
                            else {
                                Log.d("doorAction" , "1");
                                if (MyApp.clientBackActions.lights) {
                                    turnLightsOn(room);
                                }
                                if (MyApp.clientBackActions.curtain) {
                                    if (room.getCURTAIN_B() != null && room.getCURTAIN() != null) {
                                        if (room.getCURTAIN_B().dps.get("1") != null) {
                                            room.getCURTAIN().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                }
                                if (MyApp.clientBackActions.ac) {
                                    if (room.getAC_B() != null && room.getAC() != null) {
                                        room.getAC().publishDps("{\"" + room.acVariables.PowerDP + "\": true}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        room.powerOnRoom(new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                int sec = 2 * 60;
                                if (MyApp.clientBackActions.lights) {
                                    turnLightsOn(room);
                                }
                                if (MyApp.clientBackActions.curtain) {
                                    if (room.getCURTAIN_B() != null && room.getCURTAIN() != null) {
                                        if (room.getCURTAIN_B().dps.get("1") != null) {
                                            room.getCURTAIN().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                }
                                if (MyApp.clientBackActions.ac) {
                                    if (room.getAC_B() != null && room.getAC() != null) {
                                        room.getAC().publishDps("{\"" + room.acVariables.PowerDP + "\": true}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                }
                                if (room.getPOWER_B().getDps().get("8") != null) {
                                    room.getPOWER().publishDps("{\" 8\":" + sec + "}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                } else if (room.getPOWER_B().getDps().get("10") != null) {
                                    room.getPOWER().publishDps("{\" 10\":" + sec + "}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                });
            }
        }
    }

    static void OpenTheDoor(Activity act,ROOM THE_ROOM,RequestOrder callBack) {
        if (THE_ROOM.getLOCK_B() != null) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/addClientDoorOpen";
            StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                Log.d("doorOpenResp" , response);
                try {
                    JSONObject result = new JSONObject(response);
                    result.getString("result");
                    if (result.getString("result").equals("success")) {
                        ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, act, new RequestOrder() {
                            @Override
                            public void onSuccess(String token) {
                                Log.d("doorOpenResp" , "token "+token);
                                ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret, THE_ROOM.getLOCK_B().devId, act, new RequestOrder() {
                                    @Override
                                    public void onSuccess(String ticket) {
                                        Log.d("doorOpenResp" , "ticket "+ticket);
                                        ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret, THE_ROOM.getLOCK_B().devId, act, new RequestOrder() {
                                            @Override
                                            public void onSuccess(String res) {
                                                Log.d("doorOpenResp" , "res "+res);
                                                callBack.onSuccess(res);
                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                Log.d("openDoorResp" , "res "+error);
                                                callBack.onFailed(error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Log.d("doorOpenResp" , "ticket "+error);
                                        callBack.onFailed(error);
                                    }
                                });
                            }

                            @Override
                            public void onFailed(String error) {
                                Log.d("doorOpenResp" , "token "+error);
                                callBack.onFailed(error);
                            }
                        });
                    }
                    else {
                        callBack.onFailed(result.getString("error"));
                    }

                } catch (JSONException e) {
                    Log.d("doorOpenResp" , e.getMessage());
                    callBack.onFailed(e.getMessage());
                }
            }, error -> {
                Log.d("doorOpenResp" , error.toString());
                callBack.onFailed(error.toString());
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("room_id", String.valueOf(THE_ROOM.id));
                    return params;
                }
            };
            if (REQ == null) {
                REQ = Volley.newRequestQueue(act);
            }
            REQ.add(req);
        }
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

    void getServiceUsersFromFirebase() {
        ServiceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null ) {
                    EmpS.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        int id = 0;
                        if (child.child("id").getValue() != null ) {
                            id = Integer.parseInt( Objects.requireNonNull(child.child("id").getValue()).toString());
                        }
                        String name = "";
                        if (child.child("name").getValue() != null ) {
                            name = Objects.requireNonNull(child.child("name").getValue()).toString();
                        }
                        int jobNum = 0 ;
                        if (child.child("jobNumber").getValue() != null ) {
                            jobNum = Integer.parseInt(Objects.requireNonNull(child.child("jobNumber").getValue()).toString());
                        }
                        String department = "";
                        if (child.child("department").getValue() != null ) {
                            department = Objects.requireNonNull(child.child("department").getValue()).toString();
                        }
                        String mobile = "" ;
                        if (child.child("Mobile").getValue() != null ) {
                            mobile = Objects.requireNonNull(child.child("Mobile").getValue()).toString();
                        }
                        String token = "";
                        if (child.child("token").getValue() != null ) {
                            token = Objects.requireNonNull(child.child("token").getValue()).toString() ;
                        }
                       EmpS.add(new ServiceEmps(id,1,name,jobNum,department,mobile,token));
                    }
                }
                for (ServiceEmps se: EmpS) {
                    Log.d("serviceEmpS",se.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                                setSCENES(SCENES);
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

    static void setRoomServiceScenes(List<SceneBean> list,ROOM room,HomeBean h) {
        if (h != null) {
            if (MyApp.ProjectVariables.cleanupButton != 0 && MyApp.ProjectVariables.dndButton != 0) {
                if (room.getSERVICE1() != null) {
                    if (room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)) != null && room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                        if (searchScene(list, room.RoomNumber + "ServiceSwitchDNDScene2")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (room.getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.dndButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(room.getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                                condS.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.cleanupButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(room.getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        h.getHomeId(),
                                        room.RoomNumber + "ServiceSwitchDNDScene2", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        condS, // The effective period. This parameter is optional.
                                        tasks, // The conditions.
                                        null,     // The tasks.
                                        SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                        new ITuyaResultCallback<SceneBean>() {
                                            @Override
                                            public void onSuccess(SceneBean sceneBean) {
                                                Log.d("SCENE_DND1", "createScene Success");
                                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                        IResultCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("SCENE_DND1", "enable Scene Success");
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String errorMessage) {
                                                                Log.d("SCENE_DND1", errorMessage + " " + errorCode);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                Log.d("SCENE_DND1", errorMessage + " " + errorCode);
                                            }
                                        });
                            }
                        }
                        if (searchScene(list, room.RoomNumber + "ServiceSwitchCleanupScene")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (room.getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.cleanupButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(room.getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.cleanupButton), rule);
                                condS.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.dndButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(room.getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        h.getHomeId(),
                                        room.RoomNumber + "ServiceSwitchCleanupScene", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        condS, // The effective period. This parameter is optional.
                                        tasks, // The conditions.
                                        null,     // The tasks.
                                        SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                        new ITuyaResultCallback<SceneBean>() {
                                            @Override
                                            public void onSuccess(SceneBean sceneBean) {
                                                Log.d("SCENE_Cleanup", "createScene Success");
                                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                        IResultCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("SCENE_Cleanup", "enable Scene Success");
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String errorMessage) {
                                                                Log.d("SCENE_Cleanup", errorMessage);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                Log.d("SCENE_Cleanup", errorMessage);
                                            }
                                        });
                            }
                        }
                    }
                }
            }
            if (MyApp.ProjectVariables.laundryButton != 0 && MyApp.ProjectVariables.dndButton != 0) {
                if (room.getSERVICE1() != null) {
                    if (room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)) != null && room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                        if (searchScene(list, room.RoomNumber + "ServiceSwitchDNDScene3")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (room.getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.dndButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(room.getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                                condS.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.laundryButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(room.getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        h.getHomeId(),
                                        room.RoomNumber + "ServiceSwitchDNDScene3", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        condS, // The effective period. This parameter is optional.
                                        tasks, // The conditions.
                                        null,     // The tasks.
                                        SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                        new ITuyaResultCallback<SceneBean>() {
                                            @Override
                                            public void onSuccess(SceneBean sceneBean) {
                                                Log.d("SCENE_DND2", "createScene Success");
                                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                        IResultCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("SCENE_DND2", "enable Scene Success");
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String errorMessage) {
                                                                Log.d("SCENE_DND2", errorMessage);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                Log.d("SCENE_DND2", errorMessage);
                                            }
                                        });
                            }
                        }
                        if (searchScene(list, room.RoomNumber + "ServiceSwitchLaundryScene")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (room.getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.laundryButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(room.getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.laundryButton), rule);
                                condS.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.dndButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(room.getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        h.getHomeId(),
                                        room.RoomNumber + "ServiceSwitchLaundryScene", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        condS, // The effective period. This parameter is optional.
                                        tasks, // The conditions.
                                        null,     // The tasks.
                                        SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                        new ITuyaResultCallback<SceneBean>() {
                                            @Override
                                            public void onSuccess(SceneBean sceneBean) {
                                                Log.d("SCENE_Laundry", "createScene Success");
                                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                        IResultCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("SCENE_Laundry", "enable Scene Success");
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String errorMessage) {
                                                                Log.d("SCENE_Laundry", errorMessage);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                Log.d("SCENE_Laundry", errorMessage);
                                            }
                                        });
                            }
                        }
                    }
                }
            }
            if (MyApp.ProjectVariables.checkoutButton != 0 && MyApp.ProjectVariables.dndButton != 0) {
                if (room.getSERVICE1() != null) {
                    if (room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null && room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                        if (searchScene(list, room.RoomNumber + "ServiceSwitchDNDScene4")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (room.getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.dndButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(room.getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                                condS.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.checkoutButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(room.getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        h.getHomeId(),
                                        room.RoomNumber + "ServiceSwitchDNDScene4", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        condS, // The effective period. This parameter is optional.
                                        tasks, // The conditions.
                                        null,     // The tasks.
                                        SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                        new ITuyaResultCallback<SceneBean>() {
                                            @Override
                                            public void onSuccess(SceneBean sceneBean) {
                                                Log.d("SCENE_DND2", "createScene Success");
                                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                        IResultCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("SCENE_DND2", "enable Scene Success");
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String errorMessage) {
                                                                Log.d("SCENE_DND2", errorMessage);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                Log.d("SCENE_DND2", errorMessage);
                                            }
                                        });
                            }
                        }
                        if (searchScene(list, room.RoomNumber + "ServiceSwitchCheckoutScene")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (room.getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.checkoutButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(room.getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.checkoutButton), rule);
                                condS.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.dndButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(room.getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        h.getHomeId(),
                                        room.RoomNumber + "ServiceSwitchCheckoutScene", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        condS, // The effective period. This parameter is optional.
                                        tasks, // The conditions.
                                        null,     // The tasks.
                                        SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                        new ITuyaResultCallback<SceneBean>() {
                                            @Override
                                            public void onSuccess(SceneBean sceneBean) {
                                                Log.d("SCENE_Laundry", "createScene Success");
                                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                        IResultCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("SCENE_Laundry", "enable Scene Success");
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String errorMessage) {
                                                                Log.d("SCENE_Laundry", errorMessage);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                Log.d("SCENE_Laundry", errorMessage);
                                            }
                                        });
                            }
                        }
                    }
                }
            }
        }
    }

    static void setSCENES(List<SceneBean> SCENES) {
        for (int i = 0; i< ROOMS.size(); i++) {
            ROOM r = ROOMS.get(i);
            setRoomServiceScenes(SCENES,r,r.RoomHome);
        }
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

    public static boolean searchScene (List<SceneBean> list , String name) {
        for (int i=0 ; i<list.size();i++) {
            if (list.get(i).getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public void goToLocks(View view) {
        Intent i = new Intent(act,Locks.class);
        startActivity(i);
    }

    static DeviceBean searchRoomDevice(List<DeviceBean> devices,ROOM room,String deviceType) {
        DeviceBean d = null ;
        for (int i=0; i<devices.size();i++) {
            if (devices.get(i).name.equals(room.RoomNumber+deviceType)) {
                d = devices.get(i);
                break;
            }
        }
        return d ;
    }

    public void logOut(View view) {
        AlertDialog.Builder b = new AlertDialog.Builder(act);
        b
                .setTitle("Are you sure .?")
                .setMessage("Are you sure to log out ")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    loading = new lodingDialog(act);
                    String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseStatus";
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
                            params.put("device_id",MyApp.Device_Id);
                            params.put("status","0");
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(req);
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("Delete Device", (dialogInterface, i) -> {
                    loading = new lodingDialog(act);
                    String url = MyApp.THE_PROJECT.url + "roomsManagement/deleteControlDevice";
                    StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                        Log.d("deleteDeviceStatus" , response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("result").equals("success")) {
                                dialogInterface.dismiss();
                                loading.stop();
                                SharedPreferences.Editor editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
                                editor.putString("projectName" , null);
                                editor.putString("tuyaUser" , null);
                                editor.putString("tuyaPassword" , null);
                                editor.putString("lockUser" , null);
                                editor.putString("lockPassword" , null);
                                editor.putString("Device_Id" , null);
                                editor.putString("Device_Name" , null);
                                editor.apply();
                                Intent i12 = new Intent(act,Login.class);
                                act.startActivity(i12);
                                act.finish();
                            }
                            else {
                                new MessageDialog("delete device failed" , "failed",act);
                                loading.stop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new MessageDialog(e.toString() , "failed",act);
                            loading.stop();
                        }


                    }, error -> {
                        Log.d("changeDeviceStatus" , error.toString());
                        loading.stop();
                        new MessageDialog(error.toString(),"error",act);
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("device_id",MyApp.Device_Id);
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(req);
                })
                .create().show();
    }

    public void login() {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseStatus";
        StringRequest req = new StringRequest(Request.Method.POST, url, response -> Log.d("changeDeviceStatus" , response), error -> {
            Log.d("changeDeviceStatus" , error.toString());
            loading.stop();
            new MessageDialog(error.toString(),"error",act);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id",MyApp.Device_Id);
                params.put("status","1");
                return params;
            }
        };
        REQ.add(req);
    }

    static List<SceneBean> getRoomScenes(ROOM r,List<SceneBean> list) {
        List<SceneBean> res = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getName().contains(String.valueOf(r.RoomNumber))) {
                res.add(list.get(i));
            }
        }
        return res ;
    }

    static List<SceneBean> getRoomBasicScenes(ROOM r,List<SceneBean> list) {
        List<SceneBean> res = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getName().contains(r.RoomNumber+"Living") || list.get(i).getName().contains(r.RoomNumber+"Sleep") || list.get(i).getName().contains(r.RoomNumber+"Work") || list.get(i).getName().contains(r.RoomNumber+"Romance") || list.get(i).getName().contains(r.RoomNumber+"Read") || list.get(i).getName().contains(r.RoomNumber+"MasterOff") || list.get(i).getName().contains(r.RoomNumber+"LightsOn")) {
                res.add(list.get(i));
            }
        }
        return res ;
    }

    static SceneBean getMood(List<SceneBean> list,String mood) {
        SceneBean s = null ;
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getName().contains(mood)) {
                s = list.get(i) ;
            }
        }
        return s ;
    }

    static DeviceBean getMoodConditionDevice(SceneBean s , ROOM r) {
        DeviceBean d = null ;
        if (s.getConditions() != null) {
            Log.d("checkInModeTest" , "cond not null "+s.getConditions().get(0).getEntityId());
            if (s.getConditions().get(0) != null) {
                Log.d("checkInModeTest" ,"cond 0 not null");
                if (r.getSWITCH1_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH1_B().devId)) {
                        d = r.getSWITCH1_B() ;
                    }
                }
                if (r.getSWITCH2_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH2_B().devId)) {
                        d = r.getSWITCH2_B() ;
                    }
                }
                if (r.getSWITCH3_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH3_B().devId)) {
                        d = r.getSWITCH3_B() ;
                    }
                }
                if (r.getSWITCH4_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH4_B().devId)) {
                        d = r.getSWITCH4_B() ;
                    }
                }
                if (r.getSWITCH5_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH5_B().devId)) {
                        d = r.getSWITCH5_B() ;
                    }
                }
                if (r.getSWITCH6_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH6_B().devId)) {
                        d = r.getSWITCH6_B() ;
                    }
                }
                if (r.getSWITCH7_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH7_B().devId)) {
                        d = r.getSWITCH7_B() ;
                    }
                }
                if (r.getSWITCH8_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH8_B().devId)) {
                        d = r.getSWITCH8_B() ;
                    }
                }
            }
            else {
                Log.d("checkInModeTest" , "cond 0 null");
            }
        }
        else {
            Log.d("checkInModeTest" , "cond null");
        }
        return d ;
    }

    static String getMoodConditionDeviceButton(SceneBean s) {
        String res = null ;
        if (s.getConditions() != null) {
            if (s.getConditions().get(0) != null) {
                res = s.getConditions().get(0).getEntitySubIds();
            }
        }
        return res;
    }

    static void turnLightsOn(ROOM THE_ROOM) {
        Log.d("checkInModeTest"+THE_ROOM.RoomNumber,"turn lights on");
        List<SceneBean> roomMoods = getRoomBasicScenes(THE_ROOM,SCENES);
        SceneBean lightsOn = getMood(roomMoods,"LightsOn");
        if (lightsOn == null) {
            List<LightButton> buttons = ROOM.getRoomLightButtons(THE_ROOM);
            for (SceneBean mood : roomMoods) {
                DeviceBean D = getMoodConditionDevice(mood,THE_ROOM);
                if (D != null) {
                    String button = getMoodConditionDeviceButton(mood);
                    for (LightButton lb:buttons) {
                        if (lb.device.devId.equals(D.devId) && lb.button.equals(button)) {
                            buttons.remove(lb);
                            break;
                        }
                    }
                }
            }
            for (LightButton lb : buttons) {
                Log.d("checkInModeTest"+THE_ROOM.RoomNumber,lb.device.name+" b "+lb.button);
                turnSwitchButtonOn(lb.device, lb.button, new CallbackResult() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
            }
        }
        else {
            Log.d("checkInModeTest"+THE_ROOM.RoomNumber,"LightsOn found");
            DeviceBean D = getMoodConditionDevice(lightsOn,THE_ROOM) ;
            if (D != null) {
                Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"device found");
                String button = getMoodConditionDeviceButton(lightsOn);
                if (button != null) {
                    Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"button found");
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            turnSwitchButtonOn(D, button, new CallbackResult() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String error) {

                                }
                            });
                        }
                    }, 0);
                }
            }
            else {
                Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"device null");
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TuyaHomeSdk.newSceneInstance(lightsOn.getId()).executeScene(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                }, 3000);

            }
        }
    }

    static void turnLightsOff(ROOM THE_ROOM) {
        List<SceneBean> ss = getRoomScenes(THE_ROOM,SCENES);
        Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"room Scenes: "+ss.size());
        SceneBean S = getMood(ss,"MasterOff") ;
        if (S != null) {
            Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"scene found");
            DeviceBean D = getMoodConditionDevice(S,THE_ROOM) ;
            if (D != null) {
                Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"device found");
                String button = getMoodConditionDeviceButton(S);
                if (button != null) {
                    Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"button found");
                    TuyaHomeSdk.newDeviceInstance(D.devId).publishDps("{\" "+button+"\":true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"master off started");
                        }
                    });
                }
            }
            else {
                Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"device null");
                TuyaHomeSdk.newSceneInstance(S.getId()).executeScene(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {

                    }
                });
            }
        }
        else {
            Log.d("checkInModeTest"+THE_ROOM.RoomNumber ,"no master off");
            for (DeviceBean device:ROOM.getRoomDevices(THE_ROOM)) {
                if (device.getName().equals(THE_ROOM.RoomNumber+"Switch1") || device.getName().equals(THE_ROOM.RoomNumber+"Switch2") || device.getName().equals(THE_ROOM.RoomNumber+"Switch3") || device.getName().equals(THE_ROOM.RoomNumber+"Switch4") || device.getName().equals(THE_ROOM.RoomNumber+"Switch5") || device.getName().equals(THE_ROOM.RoomNumber+"Switch6") || device.getName().equals(THE_ROOM.RoomNumber+"Switch7") || device.getName().equals(THE_ROOM.RoomNumber+"Switch8")) {
                    if (device.dps.get("1")!= null) {
                        turnSwitchButtonOff(device, "1", new CallbackResult() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                    if (device.dps.get("2")!= null) {
                        turnSwitchButtonOff(device, "2", new CallbackResult() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                    if (device.dps.get("3")!= null) {
                        turnSwitchButtonOff(device, "3", new CallbackResult() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                    if (device.dps.get("4")!= null) {
                        turnSwitchButtonOff(device, "4", new CallbackResult() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                }
            }
        }
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
        Timer t = new Timer() ;
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();
                sendRegistrationToServer(token);
            });
            }},0,1000*60*60*12);
    }

    static void createRestartConfirmationDialog(Activity act,String message) {
        AlertDialog.Builder B = new AlertDialog.Builder(act);
        B.setTitle("Restart..?");
        B.setMessage(message);
        B.setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss());
        B.setPositiveButton("yes", (dialogInterface, i) -> {
            gettingAndPreparingData(act);
            dialogInterface.dismiss();
        });
        act.runOnUiThread(() -> B.create().show());

    }

    static boolean searchValuesInList(Collection<Object> values,List<String> list) {
        for (Object o : values) {
            for (String s:list) {
                if (o.toString().equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}

class DatabaseReference_ValueEventListener{
    DatabaseReference ref;
    ValueEventListener listener;
    DatabaseReference_ValueEventListener(DatabaseReference ref,ValueEventListener listener) {
        this.ref = ref;
        this.listener = listener;
    }
}