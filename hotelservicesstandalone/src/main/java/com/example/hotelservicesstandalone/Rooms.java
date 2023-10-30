package com.example.hotelservicesstandalone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
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
    static ListView devicesListView , roomsListView ;
    static List<ROOM> ROOMS;
    static final String getRoomsUrl = MyApp.THE_PROJECT.url + "roomsManagement/getRoomsForControllDevice" ;
    static Activity act ;
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
    Button searchBtn ;
    ExtendedBluetoothDevice TheFoundGateway ;
    private ConfigureGatewayInfo configureGatewayInfo;
    static List<SceneBean> SCENES ;
    static List<String> IMAGES ;
    static DatabaseReference ServerDevice , ProjectVariablesRef , DevicesControls , ProjectDevices  ;
    static int addCleanupCounter=1,cancelOrderCounter=1,addLaundryCounter =1,addCheckoutCounter=1,addDNDCounter=1,cancelDNDCounter = 1 ;
    static String PowerUnInstalled,PowerInstalled,GatewayUnInstalled,GatewayInstalled,MotionUnInstalled,MotionInstalled,DoorUnInstalled,DoorInstalled,ServiceUnInstalled,ServiceInstalled,S1UnInstalled,S1Installed,S2UnInstalled,S2Installed,S3UnInstalled,S3Installed,S4UnInstalled,S4Installed,S5UnInstalled,S5Installed,S6UnInstalled,S6Installed,S7UnInstalled,S7Installed,S8UnInstalled,S8Installed,ACUnInstalled,ACInstalled,CurtainUnInstalled,CurtainInstalled,LockUnInstalled,LockInstalled;
    long refreshSystemTime = 12 ;
    Timer refreshTimer;
    private final String projectLoginUrl = "users/loginProject" ;
    static List<DatabaseReference_ValueEventListener> RoomsDevicesReferencesListeners;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        setActivity(act);
        getProjectVariables();
        getServiceUsersFromFirebase();
        hideSystemUI();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            String token = task.getResult();
            Log.e("token" , token);
            sendRegistrationToServer(token);
        });
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
            }},1000*60*15,1000*60*60*12);
        refreshTimer = new Timer() ;
        setActionText("Welcome",act);
        act.startLockTask();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (CHANGE_STATUS) {
            refreshSystem();
            CHANGE_STATUS = false ;
        }
    }

    public static void refreshSystem() {
        setTuyaApplication();
        removeFireRoomsDevicesListeners();
        unregisterDevicesListener();
        getRooms();
    }

    private void setActivity(Activity act) {
        act = this ;
        REQ = Volley.newRequestQueue(act);
        REQ1 = Volley.newRequestQueue(act);
        CLEANUP_QUEUE = Volley.newRequestQueue(act);
        LAUNDRY_QUEUE = Volley.newRequestQueue(act);
        CHECKOUT_QUEUE = Volley.newRequestQueue(act);
        DND_Queue = Volley.newRequestQueue(act);
        lockDB = new LockDB(act);
        if (!lockDB.isLoggedIn()) {
            lockDB.removeAll();
            lockDB.insertLock("off");
        }
        SCENES = new ArrayList<>();
        configureGatewayInfo = new ConfigureGatewayInfo();
        searchText = findViewById(R.id.search_text);
        searchBtn = findViewById(R.id.button16);
        Activity finalAct = act;
        searchBtn.setOnClickListener(v -> {
            if (!searchBtn.getText().toString().equals("X")) {
                if (searchText.getText() == null ) {
                    Toast.makeText(finalAct,"enter text",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (devicesListView.getVisibility() == View.VISIBLE ) {
                    String Text = searchText.getText().toString() ;
                    List<DeviceBean> Results = new ArrayList<>();
                    for (int i = 0 ; i < Devices.size() ; i++) {
                        if (Devices.get(i).getName().contains(Text)) {
                            Results.add(Devices.get(i));
                        }
                    }
                    if (Results.size() > 0 ) {
                        searchBtn.setText("X");
//                            String[] x = new String[Results.size()];
//                            for (int j=0; j<Results.size(); j++) {
//                                x[j] = Results.get(j);
//                            }
                        //ArrayAdapter<String> ad = new ArrayAdapter<String>(act,R.layout.spinners_item,x);
                        Devices_Adapter adapter = new Devices_Adapter(Results, finalAct);
                        devicesListView.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(finalAct,"no results",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                if (devicesListView.getVisibility() == View.VISIBLE) {
                    searchBtn.setText(getResources().getString(R.string.search));
                    Devices_Adapter adapter = new Devices_Adapter(Devices, finalAct);
                    devicesListView.setAdapter(adapter);
                }
            }
        });
        MessagesQueue = Volley.newRequestQueue(act);
        EmpS = new ArrayList<>();
        toggle = findViewById(R.id.button9);
        mainLogo = findViewById(R.id.logoLyout) ;
        resetDevices = findViewById(R.id.button2);
        btnSLayout = findViewById(R.id.btnsLayout);
        TextView hotelName = findViewById(R.id.hotelName);
        hotelName.setText(MyApp.THE_PROJECT.projectName);
        ROOMS = new ArrayList<>();
        Locks = new ArrayList<>();
        Devices = new ArrayList<>();
        RoomsDevicesReferencesListeners = new ArrayList<>();
        roomsListView = findViewById(R.id.RoomsListView);
        devicesListView = findViewById(R.id.DevicesListView);
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        ServerDevice = database.getReference(MyApp.THE_PROJECT.projectName+"ServerDevices/"+MyApp.Device_Name);
        ServiceUsers = database.getReference(MyApp.THE_PROJECT.projectName+"ServiceUsers");
        ProjectVariablesRef = database.getReference(MyApp.THE_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.THE_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.THE_PROJECT.projectName+"Devices");
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        mainLogo.setOnLongClickListener(v -> {
            Dialog  dd = new Dialog(finalAct);
            dd.setContentView(R.layout.lock_unlock_dialog);
            Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = dd.findViewById(R.id.messageDialog_ok);
            EditText password = dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(v1 -> dd.dismiss());
            lock.setOnClickListener(v12 -> {
                final lodingDialog loading = new lodingDialog(finalAct);
                final String pass = password.getText().toString() ;
                StringRequest re = new StringRequest(Request.Method.POST, MyApp.THE_PROJECT.url + projectLoginUrl, response -> {
                    Log.d("lockResp",response);
                    loading.stop();
                    if (response != null) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            if (resp.getString("result").equals("success")) {
                                Toast.makeText(finalAct,"Login Success",Toast.LENGTH_LONG).show();
                                lockDB.modifyValue("off");
                                roomsListView.setVisibility(View.VISIBLE);
                                devicesListView.setVisibility(View.GONE);
                                btnSLayout.setVisibility(View.VISIBLE);
                                mainLogo.setVisibility(View.GONE);
                                dd.dismiss();
                            }
                            else {
                                Toast.makeText(finalAct,"Login Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.d("lockResp",e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(finalAct,"Login Failed " + e,Toast.LENGTH_LONG).show();
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
                Volley.newRequestQueue(finalAct).add(re);
            });
            dd.show();
            return false;
        });
        mainLogo.setVisibility(View.GONE);
        roomsListView.setVisibility(View.VISIBLE);
        devicesListView.setVisibility(View.GONE);
        hideSystemUI();
        searchBtn.setVisibility(View.GONE);
        searchText.setVisibility(View.GONE);
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
        login();
        setServerDeviceRunningFunction();
    }

    private void getProjectVariables() {
        loading = new lodingDialog(act);
        String url = MyApp.THE_PROJECT.url + "roomsManagement/getProjectVariables";
        StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject row = new JSONObject(response);
                JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                MyApp.ProjectVariables = new PROJECT_VARIABLES(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                MyApp.checkInActions = new CheckInActions(MyApp.ProjectVariables.CheckinActions);
                MyApp.checkOutActions = new CheckoutActions(MyApp.ProjectVariables.CheckoutActions);
                MyApp.clientBackActions = new ClientBackActions(MyApp.ProjectVariables.OnClientBack);
                setProjectVariablesListener();
                setControlDeviceListener();
            }
            catch (JSONException e) {
                loading.stop();
                new MessageDialog("error getting project variables "+ e,"error",act);
            }
        }, error -> {
            loading.stop();
            new MessageDialog("error getting project variables "+error.toString(),"error",act);
        });
        Volley.newRequestQueue(act).add(re);
    }

    static void getRooms() {
        Log.d("projects" , MyApp.Device_Id+" "+MyApp.Device_Name);
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, response -> {
            Log.d("roomsAre" , response);
            if (response.equals("0")) {
                loading.stop();
                new MessageDialog("no rooms detected ","No Rooms",act);
                return;
            }
            try {
                JSONObject ress = new JSONObject(response);
                if (ress.getString("result").equals("success")) {
                    JSONArray arr = ress.getJSONArray("rooms");
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
                    loading.stop();
                    new MessageDialog("getting rooms failed "+ress.getString("error"),"error",act);
                }
            }
            catch (JSONException e) {
                loading.stop();
                new MessageDialog("getting rooms failed "+ e,"error",act);
            }
            ROOM.sortRoomsByNumber(ROOMS);
            MyApp.ROOMS = ROOMS ;
            TextView hotelName = act.findViewById(R.id.hotelName);
            hotelName.setText(MyApp.THE_PROJECT.projectName);
            hotelName.setText(MessageFormat.format("{0} {1} room", MyApp.THE_PROJECT.projectName, ROOMS.size()));
            Log.d("refreshSystem","4 get rooms");
            defineVariables();
        }, error -> {
            Log.d("roomsAre" , error.toString()+MyApp.Device_Id);
            loading.stop();
            if (!error.toString().equals("com.android.volley.ClientError")) {
                new MessageDialog("getting rooms failed "+ error +MyApp.Device_Id,"error",act);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id",MyApp.Device_Id);
                return params;
            }
        };
        REQ.add(re);
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
//            if (MyApp.ProjectVariables.Temp != 0) {
//                TempSetPoint[t] = MyApp.ProjectVariables.Temp+"0" ;
//            }
//            int finalT = t;
//            int finalT1 = t;
//            int finalT2 = t;
//            DoorRunnable[t] = new Runnable() {
//                @Override
//                public void run() {
//                    DoorsHandlers[finalT] = new Handler();
//                    DoorsHandlers[finalT].postDelayed(this,1000) ;
//                    Door_Period[finalT] = System.currentTimeMillis() - Door_Start[finalT] ;
//                    if ( Door_Period[finalT] >=  MyApp.ProjectVariables.DoorWarning  && DOOR_STATUS[finalT])
//                    {
//                        ROOMS.get(finalT).getFireRoom().child("doorStatus").setValue(2);
//                        DoorsHandlers[finalT].removeCallbacks(DoorRunnable[finalT]);
//                    }
//                    else if (Door_Period[finalT] >=  MyApp.ProjectVariables.DoorWarning  && !DOOR_STATUS[finalT])
//                    {
//                        DoorsHandlers[finalT].removeCallbacks(DoorRunnable[finalT]);
//                    }
//
//                }
//            };
//            TempRunnableList[t] = new Runnable() {
//                @Override
//                public void run() {
//                    AcHandlers[finalT] = new Handler();
//                    AcHandlers[finalT].postDelayed(TempRunnableList[finalT], 1000);
//                    AC_Period[finalT] = System.currentTimeMillis() - AC_Start[finalT] ;
//                    Log.d("acSenario" ,AC_Period[finalT]+" "+MyApp.ProjectVariables.Interval +" "+AC_SENARIO_Status[finalT]);
//                    if ( AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && AC_SENARIO_Status[finalT]) {
//                        if (ROOMS.get(finalT2).getAC_B() != null ) {
//                            TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalT2).getAC_B().devId).publishDps("{\" "+ROOMS.get(finalT2).acVariables.TempSetDP+"\": "+TempSetPoint[finalT]+"}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("acSenario",error);
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    Log.d("acSenario","done");
//                                    AC_SENARIO_Status[finalT1] = false ;
//                                    AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
//                                }
//                            });
//                        }
//
//                    }
//                    else if (AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && !AC_SENARIO_Status[finalT]) {
//                        AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
//                    }
//                }
//            };
        }
        Log.d("refreshSystem","5 define variables");
        getTuyaDevices() ;
        loginTTLock();
    }

    static void setAcScenario() {
        for (int t = 0; t< ROOMS.size(); t++) {
            if (MyApp.ProjectVariables.Temp != 0) {
                if (ROOMS.get(t).acVariables.TempChars == 2) {
                    ROOMS.get(t).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp ;
                }
                else {
                    ROOMS.get(t).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp * 10 ;
                }
            }
            int finalT = t;
            DoorRunnable[t] = new Runnable() {
                @Override
                public void run() {
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
                                Log.d("acScenario",error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("acScenario","done");
                                AC_SENARIO_Status[finalT] = false ;
                                AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
                            }
                        });
                    }

                }
                else if (AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && !AC_SENARIO_Status[finalT]) {
                    AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
                }
            };
        }
    }

    static void loginTTLock() {
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
                        Log.d("TTLOCKLogin" , "success");
                        accountInfo.setMd5Pwd(finalPass);
                        acc = accountInfo;
                        Log.d("TTLOCKLogin" , accountInfo.getAccess_token());
                        getLocks();
                    }
                    else {
                        new MessageDialog("lock login failed "+accountInfo.errcode,"lock login failed",act);
                    }
                }
                else {
                    new MessageDialog("lock login failed account null","lock login failed",act);
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                new MessageDialog("lock login failed "+t.getMessage(),"lock login failed",act);
            }
        });
    }

    static void getLocks() {
        final Dialog d = new Dialog(act);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID,acc.getAccess_token(), 1, 100, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                d.dismiss();
                String json = response.body();
                if (json != null) {
                    if (json.contains("list")) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray array = jsonObject.getJSONArray("list");
                            Locks = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                            Log.d("locksNum" ,String.valueOf( Locks.size() ));
                        }
                        catch (JSONException e) {
                            Log.d("locksNum" , e.getMessage());
                        }
                        setLocks(Locks);
                    }
                    else {
                        Log.d("locksNum" , "no list");
                    }
                }
                else {
                    Log.d("locksNum" , "null body");
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
            }
        });
    }

    static void getTuyaDevices() {
        TuyaHomeSdk.newHomeInstance(MyApp.HOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Devices.clear();
                Devices = homeBean.getDeviceList();
                if (Devices.size() == 0) {
                    Toast.makeText(act,"no devices",Toast.LENGTH_LONG).show();
                    Log.d("devicesAre " ,"no devices" );
                }
                else {
                    Toast.makeText(act,"Devices are: "+Devices.size(),Toast.LENGTH_LONG).show();
                    for (int i=0;i<ROOMS.size();i++) {
                        DeviceBean power = searchRoomDevice(Devices,ROOMS.get(i),"Power");
                        if (power == null) {
                            ROOMS.get(i).PowerSwitch = 0 ;
                            ROOMS.get(i).getFireRoom().child("powerStatus").setValue(0);
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Power").removeValue();
                        }
                        else {
                            ROOMS.get(i).setPOWER_B(power);
                            ROOMS.get(i).setPOWER(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getPOWER_B().devId));
                            ROOMS.get(i).PowerSwitch = 1 ;
                            if (power.dps.get("1") != null && power.dps.get("2") != null) {
                                if (!Boolean.parseBoolean(Objects.requireNonNull(power.dps.get("1")).toString()) && !Boolean.parseBoolean(Objects.requireNonNull(power.dps.get("2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").setValue(0);
                                }
                                else if (Boolean.parseBoolean(Objects.requireNonNull(power.dps.get("1")).toString()) && !Boolean.parseBoolean(Objects.requireNonNull(power.dps.get("2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").setValue(1);
                                }
                                else if (Boolean.parseBoolean(Objects.requireNonNull(power.dps.get("1")).toString()) && Boolean.parseBoolean(Objects.requireNonNull(power.dps.get("2")).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").setValue(2);
                                }
                            }
                        }
                        DeviceBean ac = searchRoomDevice(Devices,ROOMS.get(i),"AC") ;
                        if (ac == null) {
                            ROOMS.get(i).Thermostat = 0 ;
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"AC").removeValue();
                        }
                        else {
                            ROOMS.get(i).setAC_B(ac);
                            ROOMS.get(i).setAC(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getAC_B().devId));
                            ROOMS.get(i).Thermostat = 1 ;
                            int finalI = i;
                            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(ac.devId, new ITuyaResultCallback<List<TaskListBean>>() {
                                @Override
                                public void onSuccess(List<TaskListBean> result) {
                                    long SetId = 0 ;
                                    long PowerId = 0 ;
                                    long FanId = 0;
                                    for (int i=0 ; i<result.size();i++) {
                                        if (result.get(i).getName().contains("Set temp")) {
                                            SetId = result.get(i).getDpId() ;
                                        }
                                        if (result.get(i).getName().contains("Power")) {
                                            PowerId = result.get(i).getDpId() ;
                                        }
                                        if (result.get(i).getName().contains("Fan")) {
                                            FanId = result.get(i).getDpId() ;
                                        }
                                    }
                                    if (PowerId != 0) {
                                        if (Boolean.parseBoolean(Objects.requireNonNull(ac.dps.get(String.valueOf(PowerId))).toString())) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).setValue(3);
                                        }
                                        else {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).setValue(0);
                                        }
                                    }
                                    if (SetId != 0) {
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(SetId)).setValue(Integer.parseInt(Objects.requireNonNull(ac.dps.get(String.valueOf(SetId))).toString()));
                                    }
                                    if (FanId != 0) {
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(FanId)).setValue(Objects.requireNonNull(ac.dps.get(String.valueOf(FanId))).toString());
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
                            ROOMS.get(i).getFireRoom().child("online").setValue(0);
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"ZGatway").removeValue();
                        }
                        else {
                            ROOMS.get(i).setGATEWAY_B(ZGatway);
                            ROOMS.get(i).setGATEWAY(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getGATEWAY_B().devId));
                            ROOMS.get(i).setWiredZBGateway(TuyaHomeSdk.newGatewayInstance(ROOMS.get(i).getGATEWAY_B().devId));
                            ROOMS.get(i).ZBGateway = 1 ;
                        }
                        DeviceBean DoorSensor = searchRoomDevice(Devices,ROOMS.get(i),"DoorSensor") ;
                        if (DoorSensor == null) {
                            ROOMS.get(i).DoorSensor = 0 ;
                            ROOMS.get(i).getFireRoom().child("doorStatus").setValue(0);
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"DoorSensor").removeValue();
                        }
                        else {
                            ROOMS.get(i).setDOORSENSOR_B(DoorSensor);
                            ROOMS.get(i).setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getDOORSENSOR_B().devId));
                            ROOMS.get(i).DoorSensor = 1 ;
                        }
                        DeviceBean MotionSensor = searchRoomDevice(Devices,ROOMS.get(i),"MotionSensor") ;
                        if (MotionSensor == null) {
                            ROOMS.get(i).MotionSensor = 0 ;
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"MotionSensor").removeValue();
                        }
                        else {
                            ROOMS.get(i).setMOTIONSENSOR_B(MotionSensor);
                            ROOMS.get(i).setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getMOTIONSENSOR_B().devId));
                            ROOMS.get(i).MotionSensor = 1 ;
                        }
                        DeviceBean Curtain = searchRoomDevice(Devices,ROOMS.get(i),"Curtain") ;
                        if (Curtain == null) {
                            ROOMS.get(i).CurtainSwitch = 0 ;
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Curtain").removeValue();
                        }
                        else {
                            ROOMS.get(i).setCURTAIN_B(Curtain);
                            ROOMS.get(i).setCURTAIN(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getCURTAIN_B().devId));
                            ROOMS.get(i).CurtainSwitch = 1 ;
                        }
                        DeviceBean ServiceSwitch = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch") ;
                        if (ServiceSwitch == null) {
                            ROOMS.get(i).ServiceSwitch = 0 ;
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"ServiceSwitch").removeValue();
                        }
                        else {
                            ROOMS.get(i).setSERVICE1_B(ServiceSwitch);
                            ROOMS.get(i).setSERVICE1(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSERVICE1_B().devId));
                            ROOMS.get(i).ServiceSwitch = 1 ;
                        }
                        DeviceBean ServiceSwitch2 = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch2") ;
                        if (ServiceSwitch2 != null) {
                            ROOMS.get(i).setSERVICE2_B(ServiceSwitch);
                            ROOMS.get(i).setSERVICE2(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSERVICE2_B().devId));
                        }
                        DeviceBean Switch1 = searchRoomDevice(Devices,ROOMS.get(i),"Switch1") ;
                        if (Switch1 == null) {
                            ROOMS.get(i).Switch1 = 0 ;
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
                            Log.d("switch2",ROOMS.get(i).RoomNumber+"null");
                            ROOMS.get(i).Switch2 = 0 ;
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
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Switch7").removeValue();
                        }
                        else {
                            ROOMS.get(i).setSWITCH7_B(Switch4);
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
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).RoomNumber+"Lock").removeValue();
                        }
                        else {
                            ROOMS.get(i).setLOCK_B(lock);
                            ROOMS.get(i).setLOCK(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getLOCK_B().devId));
                            ROOMS.get(i).lock = 1 ;
                            setRoomLockId(lock.devId, String.valueOf(ROOMS.get(i).id));
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(lock.name).child("1").setValue(0);
                        }
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
                Rooms_Adapter_Base adapterRooms = new Rooms_Adapter_Base(ROOMS,act);
                roomsListView.setAdapter(adapterRooms);
                Devices_Adapter adapterDevices = new Devices_Adapter(Devices,act);
                devicesListView.setAdapter(adapterDevices);
                Log.d("refreshSystem","6 get devices");
                setDevicesListeners();
                setFireRoomsListener();
                getSceneBGs();
                setAcScenario();
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                loading.stop();
                new MessageDialog("getting tuya devices failed "+errorMsg,"error",act);
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
        logInFunction(MyApp.THE_PROJECT, new CallbackResult() {
            @Override
            public void onSuccess() {
                Log.d("refreshFunction", "login success");
                refreshSystem();
            }

            @Override
            public void onFail(String error) {
                Log.d("refreshFunction", error);
            }
        });
    }

    // set Listeners _______________________________________________

    static public void setDevicesListeners() {
        for (int i = 0; i< ROOMS.size(); i++) {
            int finalI = i;
            if (ROOMS.get(i).getDOORSENSOR_B() != null ) {
                ROOMS.get(i).getDOORSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("doorAction" , dpStr+" "+ROOMS.get(finalI).getDOORSENSOR_B().dps.toString());
                        if (dpStr.get("doorcontact_state") != null ) {
                            if (Objects.requireNonNull(dpStr.get("doorcontact_state")).toString().equals("true") ) {
                                runClientBackActions(ROOMS.get(finalI));
                                ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(1);
                                AC_Start[finalI] = System.currentTimeMillis() ;
                                Door_Start[finalI] = System.currentTimeMillis() ;
                                AC_SENARIO_Status[finalI] = true ;
                                DOOR_STATUS[finalI] = true ;
                                AC_Period[finalI] = 0;
                                Door_Period[finalI]= 0;
                                if (MyApp.ProjectVariables.getAcSenarioActive()) {
                                    TempRunnableList[finalI].run();
                                }
                                DoorRunnable[finalI].run();
                                setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door open",act);
                            }
                            else {
                                ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(0);
                                if (DoorsHandlers[finalI] != null) {
                                    DoorsHandlers[finalI].removeCallbacks(DoorRunnable[finalI]);
                                }
                                DOOR_STATUS[finalI] = false ;
                                setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door closed",act);
                            }
                        }
                        else {
                            if (ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101") != null) {
                                if (Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101")).toString())) {
                                    runClientBackActions(ROOMS.get(finalI));
                                    ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(1);
                                    AC_Start[finalI] = System.currentTimeMillis() ;
                                    Door_Start[finalI] = System.currentTimeMillis() ;
                                    AC_SENARIO_Status[finalI] = true ;
                                    DOOR_STATUS[finalI] = true ;
                                    AC_Period[finalI] = 0;
                                    Door_Period[finalI]= 0;
                                    if (MyApp.ProjectVariables.getAcSenarioActive()) {
                                        TempRunnableList[finalI].run();
                                        Log.d("acSenario" ,"start");
                                    }
                                    DoorRunnable[finalI].run();
                                    setActionText("Room "+ROOMS.get(finalI).RoomNumber+" door open",act);
                                }
                                else {
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
                ROOMS.get(i).getSERVICE1().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("serviceAction" , dpStr.toString());
                        if (ROOMS.get(finalI).roomStatus == 2) {
                                if (dpStr.toString().length() <17) {
                                    Log.d("serviceAction" , "_____________________________________________");
                                    Log.d("serviceAction" , "action start");
                                    Log.d("serviceAction" , "action "+dpStr);
                                    Log.d("serviceAction" , "before cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
                                    Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                                    Log.d("serviceAction" , "length "+dpStr.toString().length());
                                    if (dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton) != null) {
                                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString()) && !CLEANUP[finalI]) {
                                            CLEANUP[finalI] = true;
                                            addCleanupOrder(ROOMS.get(finalI));
                                            ROOMS.get(finalI).Cleanup = 1;
                                            ROOMS.get(finalI).dep = "Cleanup";
                                            ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(time);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order", act);
                                        } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString()) && CLEANUP[finalI]) {
                                            CLEANUP[finalI] = false;
                                            cancelServiceOrder(ROOMS.get(finalI), "Cleanup");
                                            ROOMS.get(finalI).Cleanup = 0;
                                            ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(0);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order finished", act);
                                        }
                                    }
                                    if (dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton) != null) {
                                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString()) && !LAUNDRY[finalI]) {
                                            LAUNDRY[finalI] = true;
                                            addLaundryOrder(ROOMS.get(finalI));
                                            ROOMS.get(finalI).Laundry = 1;
                                            ROOMS.get(finalI).dep = "Laundry";
                                            ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(time);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order", act);
                                        } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString()) && LAUNDRY[finalI]) {
                                            LAUNDRY[finalI] = false;
                                            cancelServiceOrder(ROOMS.get(finalI), "Laundry");
                                            ROOMS.get(finalI).Laundry = 0;
                                            ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(0);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order finished", act);
                                        }
                                    }
                                    if (dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton) != null) {
                                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString()) && !CHECKOUT[finalI]) {
                                            CHECKOUT[finalI] = true;
                                            addCheckoutOrder(ROOMS.get(finalI));
                                            ROOMS.get(finalI).Checkout = 1;
                                            ROOMS.get(finalI).dep = "Checkout";
                                            ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(time);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order", act);
                                        } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString()) && CHECKOUT[finalI]) {
                                            CHECKOUT[finalI] = false;
                                            cancelServiceOrder(ROOMS.get(finalI), "Checkout");
                                            ROOMS.get(finalI).Checkout = 0;
                                            ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(0);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order finished", act);
                                        }
                                    }
                                    if (dpStr.get("switch_" + MyApp.ProjectVariables.dndButton) != null) {
                                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString()) && !DND[finalI]) {
                                            DND[finalI] = true;
                                            addDNDOrder(ROOMS.get(finalI));
                                            ROOMS.get(finalI).DND = 1;
                                            ROOMS.get(finalI).dep = "DND";
                                            ROOMS.get(finalI).getFireRoom().child("DND").setValue(time);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd on", act);
                                        } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString()) && DND[finalI]) {
                                            DND[finalI] = false;
                                            cancelDNDOrder(ROOMS.get(finalI));
                                            ROOMS.get(finalI).DND = 0;
                                            ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
                                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
                                        }
                                    }
                                    Log.d("serviceAction" , "after cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
                                    Log.d("serviceAction" , ROOMS.get(finalI).getSERVICE1_B().dps.toString());
                                    Log.d("serviceAction" , "_____________________________________________");
                                }
//                                else {
//                                    if (ca[0] == 0) {
//                                        Log.d("serviceAction" , "action start "+ca[0]);
//                                        Log.d("serviceAction" , "action "+dpStr);
//                                        Log.d("serviceAction" , "before cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
//                                        boolean clAfter = CLEANUP[finalI];
//                                        boolean laAfter = LAUNDRY[finalI];
//                                        boolean chAfter = CHECKOUT[finalI];
//                                        boolean dnAfter = DND[finalI];
//                                        if (dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton) != null) {
//                                            clAfter = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString());
//                                        }
//                                        if (dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton) != null) {
//                                            laAfter = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString());
//                                        }
//                                        if (dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton) != null) {
//                                            chAfter = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString());
//                                        }
//                                        if (dpStr.get("switch_" + MyApp.ProjectVariables.dndButton) != null) {
//                                            dnAfter = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString());
//                                        }
//                                        Log.d("serviceAction" , "length "+dpStr.toString().length());
//                                        Log.d("serviceAction" , "after cleanup "+clAfter+" laundry "+laAfter+" dnd "+dnAfter+" checkout "+chAfter);
//                                        Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                                        if (CLEANUP[finalI] != clAfter) {
//                                            if (clAfter) {
//                                                CLEANUP[finalI] = true;
//                                                addCleanupOrder(ROOMS.get(finalI));
//                                                ROOMS.get(finalI).Cleanup = 1;
//                                                ROOMS.get(finalI).dep = "Cleanup";
//                                                ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(time);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order", act);
//                                            }
//                                            else {
//                                                CLEANUP[finalI] = false;
//                                                cancelServiceOrder(ROOMS.get(finalI), "Cleanup");
//                                                ROOMS.get(finalI).Cleanup = 0;
//                                                ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(0);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order finished", act);
//                                            }
//                                        }
//                                        if (LAUNDRY[finalI] != laAfter) {
//                                            if (laAfter) {
//                                                LAUNDRY[finalI] = true;
//                                                addLaundryOrder(ROOMS.get(finalI));
//                                                ROOMS.get(finalI).Laundry = 1;
//                                                ROOMS.get(finalI).dep = "Laundry";
//                                                ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(time);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order", act);
//                                                if (DND[finalI]) {
//                                                    DND[finalI] = false;
//                                                    cancelDNDOrder(ROOMS.get(finalI));
//                                                    ROOMS.get(finalI).DND = 0;
//                                                    ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
//                                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
//                                                }
//                                            }
//                                            else {
//                                                LAUNDRY[finalI] = false;
//                                                cancelServiceOrder(ROOMS.get(finalI), "Laundry");
//                                                ROOMS.get(finalI).Laundry = 0;
//                                                ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(0);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order finished", act);
//                                            }
//                                        }
//                                        if (CHECKOUT[finalI] != chAfter) {
//                                            if (chAfter) {
//                                                CHECKOUT[finalI] = true;
//                                                addCheckoutOrder(ROOMS.get(finalI));
//                                                ROOMS.get(finalI).Checkout = 1;
//                                                ROOMS.get(finalI).dep = "Checkout";
//                                                ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(time);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order", act);
//                                                if (DND[finalI]) {
//                                                    DND[finalI] = false;
//                                                    cancelDNDOrder(ROOMS.get(finalI));
//                                                    ROOMS.get(finalI).DND = 0;
//                                                    ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
//                                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
//                                                }
//                                            }
//                                            else {
//                                                CHECKOUT[finalI] = false;
//                                                cancelServiceOrder(ROOMS.get(finalI), "Checkout");
//                                                ROOMS.get(finalI).Checkout = 0;
//                                                ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(0);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order finished", act);
//                                            }
//                                        }
//                                        if (DND[finalI] != dnAfter) {
//                                            if (dnAfter) {
//                                                DND[finalI] = true;
//                                                addDNDOrder(ROOMS.get(finalI));
//                                                ROOMS.get(finalI).DND = 1;
//                                                ROOMS.get(finalI).dep = "DND";
//                                                ROOMS.get(finalI).getFireRoom().child("DND").setValue(time);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd on", act);
//                                            }
//                                            else {
//                                                DND[finalI] = false;
//                                                cancelDNDOrder(ROOMS.get(finalI));
//                                                ROOMS.get(finalI).DND = 0;
//                                                ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
//                                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
//                                            }
//                                        }
//                                        Log.d("serviceAction" , "_____________________________________________");
//                                        ca[0] = 1 ;
//                                        Timer t = new Timer();
//                                        t.schedule(new TimerTask() {
//                                            @Override
//                                            public void run() {
//                                                ca[0] = 0;
//                                            }
//                                        },2000);
//                                    }
//
////                                Log.d("serviceAction" , "action start");
////                                Log.d("serviceAction" , "action "+dpStr);
////                                Log.d("serviceAction" , "before cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
////                                //Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
////                                Log.d("serviceAction" , "length "+dpStr.toString().length());
////                                if (dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton) != null) {
////                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString()) && !CLEANUP[finalI]) {
////                                        CLEANUP[finalI] = true;
////                                        addCleanupOrder(ROOMS.get(finalI));
////                                        ROOMS.get(finalI).Cleanup = 1;
////                                        ROOMS.get(finalI).dep = "Cleanup";
////                                        ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(time);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order", act);
////                                    } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.cleanupButton)).toString()) && CLEANUP[finalI]) {
////                                        CLEANUP[finalI] = false;
////                                        cancelServiceOrder(ROOMS.get(finalI), "Cleanup");
////                                        ROOMS.get(finalI).Cleanup = 0;
////                                        ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(0);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order finished", act);
////                                    }
////                                }
////                                if (dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton) != null) {
////                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString()) && !LAUNDRY[finalI]) {
////                                        LAUNDRY[finalI] = true;
////                                        addLaundryOrder(ROOMS.get(finalI));
////                                        ROOMS.get(finalI).Laundry = 1;
////                                        ROOMS.get(finalI).dep = "Laundry";
////                                        ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(time);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order", act);
////                                    } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.laundryButton)).toString()) && LAUNDRY[finalI]) {
////                                        LAUNDRY[finalI] = false;
////                                        cancelServiceOrder(ROOMS.get(finalI), "Laundry");
////                                        ROOMS.get(finalI).Laundry = 0;
////                                        ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(0);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order finished", act);
////                                    }
////                                }
////                                if (dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton) != null) {
////                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString()) && !CHECKOUT[finalI]) {
////                                        CHECKOUT[finalI] = true;
////                                        addCheckoutOrder(ROOMS.get(finalI));
////                                        ROOMS.get(finalI).Checkout = 1;
////                                        ROOMS.get(finalI).dep = "Checkout";
////                                        ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(time);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order", act);
////                                    } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.checkoutButton)).toString()) && CHECKOUT[finalI]) {
////                                        CHECKOUT[finalI] = false;
////                                        cancelServiceOrder(ROOMS.get(finalI), "Checkout");
////                                        ROOMS.get(finalI).Checkout = 0;
////                                        ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(0);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order finished", act);
////                                    }
////                                }
////                                if (dpStr.get("switch_" + MyApp.ProjectVariables.dndButton) != null) {
////                                    if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString()) && !DND[finalI]) {
////                                        DND[finalI] = true;
////                                        addDNDOrder(ROOMS.get(finalI));
////                                        ROOMS.get(finalI).DND = 1;
////                                        ROOMS.get(finalI).dep = "DND";
////                                        ROOMS.get(finalI).getFireRoom().child("DND").setValue(time);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd on", act);
////                                    } else if (!Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_" + MyApp.ProjectVariables.dndButton)).toString()) && DND[finalI]) {
////                                        DND[finalI] = false;
////                                        cancelDNDOrder(ROOMS.get(finalI));
////                                        ROOMS.get(finalI).DND = 0;
////                                        ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
////                                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
////                                    }
////                                }
////                                Log.d("serviceAction" , "after cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
////                                Log.d("serviceAction" , ROOMS.get(finalI).getSERVICE1_B().dps.toString());
////                                Log.d("serviceAction" , "_____________________________________________");
//                           }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setServiceSwitchStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {
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
//                long[] v = {0};
//                ROOMS.get(i).getSERVICE1().registerDevListener(new IDevListener() {
//                    @Override
//                    public void onDpUpdate(String devId, String dpStr) {
//
//                        Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                        if (ROOMS.get(finalI).roomStatus == 2) {
//                            long h = Calendar.getInstance().getTimeInMillis();
//                            if (h > (v[0]+2000)) {
//                                v[0] = h;
//                                Log.d("serviceAction" , "action start");
//                                Log.d("serviceAction" , "action "+dpStr);
//                                Log.d("serviceAction" , "before cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
//                                try {
//                                    JSONObject cleanup = new JSONObject(dpStr);
//                                    CLEANUP[finalI] = cleanup.getBoolean(String.valueOf(MyApp.ProjectVariables.cleanupButton));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                try {
//                                    JSONObject laundry = new JSONObject(dpStr);
//                                    LAUNDRY[finalI] = laundry.getBoolean(String.valueOf(MyApp.ProjectVariables.laundryButton));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                try {
//                                    JSONObject dnd = new JSONObject(dpStr);
//                                    DND[finalI] = dnd.getBoolean(String.valueOf(MyApp.ProjectVariables.dndButton));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                try {
//                                    JSONObject checkout = new JSONObject(dpStr);
//                                    CHECKOUT[finalI] = checkout.getBoolean(String.valueOf(MyApp.ProjectVariables.checkoutButton));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                Log.d("serviceAction" , "after cleanup "+CLEANUP[finalI]+" laundry "+LAUNDRY[finalI]+" dnd "+DND[finalI]+" checkout "+CHECKOUT[finalI]);
//                                if (CLEANUP[finalI]) {
//                                    addCleanupOrder(ROOMS.get(finalI));
//                                    ROOMS.get(finalI).Cleanup = 1;
//                                    ROOMS.get(finalI).dep = "Cleanup";
//                                    ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(time);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order", act);
//                                } else {
//                                    cancelServiceOrder(ROOMS.get(finalI), "Cleanup");
//                                    ROOMS.get(finalI).Cleanup = 0;
//                                    ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(0);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " cleanup order finished", act);
//                                }
//                                if (LAUNDRY[finalI]) {
//                                    addLaundryOrder(ROOMS.get(finalI));
//                                    ROOMS.get(finalI).Laundry = 1;
//                                    ROOMS.get(finalI).dep = "Laundry";
//                                    ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(time);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order", act);
//                                } else {
//                                    cancelServiceOrder(ROOMS.get(finalI), "Laundry");
//                                    ROOMS.get(finalI).Laundry = 0;
//                                    ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(0);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " laundry order finished", act);
//                                }
//                                if (DND[finalI]) {
//                                    addDNDOrder(ROOMS.get(finalI));
//                                    ROOMS.get(finalI).DND = 1;
//                                    ROOMS.get(finalI).dep = "DND";
//                                    ROOMS.get(finalI).getFireRoom().child("DND").setValue(time);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd on", act);
//                                } else {
//                                    cancelDNDOrder(ROOMS.get(finalI));
//                                    ROOMS.get(finalI).DND = 0;
//                                    ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " dnd off", act);
//                                }
//                                if (CHECKOUT[finalI]) {
//                                    addCheckoutOrder(ROOMS.get(finalI));
//                                    ROOMS.get(finalI).Checkout = 1;
//                                    ROOMS.get(finalI).dep = "Checkout";
//                                    ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(time);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order", act);
//                                } else {
//                                    cancelServiceOrder(ROOMS.get(finalI), "Checkout");
//                                    ROOMS.get(finalI).Checkout = 0;
//                                    ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(0);
//                                    setActionText("Room " + ROOMS.get(finalI).RoomNumber + " checkout order finished", act);
//                                }
//                                Log.d("serviceAction" , "_____________________________________________");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
            }
            if (ROOMS.get(i).getAC_B() !=null) {
                ROOMS.get(i).getAC().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("acAction" , dpStr.toString());
                        setActionText("Room " + ROOMS.get(finalI).RoomNumber + " AC action",act);
                        if (dpStr.get("temp_current") != null) {
                            double temp = (Integer.parseInt(Objects.requireNonNull(dpStr.get("temp_current")).toString())*0.1);
                            ROOMS.get(finalI).getFireRoom().child("temp").setValue(temp) ;
                        }
                        if (dpStr.get("temp_set") != null ) {
                            if (Double.parseDouble(Objects.requireNonNull(dpStr.get("temp_set")).toString()) !=  ROOMS.get(finalI).acVariables.TempSetPoint) {
                                Client_Temp[finalI] = Objects.requireNonNull(dpStr.get("temp_set")).toString();
                                ROOMS.get(finalI).acVariables.TempClient = Integer.parseInt(Objects.requireNonNull(dpStr.get("temp_set")).toString());
                            }
                        }
                        if (dpStr.get("switch") != null) {
                            if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch")).toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("1").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("1").setValue(0);
                            }
                        }
                        if (dpStr.get("level") != null) {
                            String newFan = Objects.requireNonNull(dpStr.get("level")).toString();
                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("5").setValue(newFan);
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
                long[] v = {0};
                ROOMS.get(i).getPOWER().registerDevListener(new IDevListener() {
                    @Override
                    public void onDpUpdate(String devId, String dpStr) {
                        long h = Calendar.getInstance().getTimeInMillis();
                        if (h > (v[0]+1500)) {
                            Log.d("powerActions","action start");
                            Log.d("powerActions","action"+dpStr);
                            Log.d("powerActions","before action "+v1[0]+" "+v2[0]);
                            v[0] = h;
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
//                        try {
//                            JSONObject s = new JSONObject(dpStr);
//                            v1[0] = s.getBoolean("1");
//                            v2[0] = s.getBoolean("2");
//                            if (v1[0] && v2[0]) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(2);
//                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power on",act);
//                            }
//                            else if (v1[0]) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(1);
//                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power byCard",act);
//                            }
//                            else if (!v2[0]) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(0);
//                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power off",act);
//                            }
//                            Log.d("powerActions0","after action "+v1[0]+" "+v2[0]);
//                            Log.d("powerActions0","action finish");
//                            Log.d("powerActions0","________________________________________________________");
//                        } catch (JSONException e) {
//                            Log.d("powerActions",e.getMessage());
//                            if (e.getMessage().equals("No value for 1")) {
//
//                                v2[0] = Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getPOWER_B().dps.get("2")).toString());
//                            }
//                            if (v1[0] && v2[0]) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(2);
//                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power on",act);
//                            }
//                            else if (v1[0]) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(1);
//                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power byCard",act);
//                            }
//                            else if (!v2[0]) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(0);
//                                setActionText("Room " + ROOMS.get(finalI).RoomNumber + " power off",act);
//                            }
//                            Log.d("powerActions","after action "+v1[0]+" "+v2[0]);
//                            Log.d("powerActions","action finish");
//                            Log.d("powerActions","________________________________________________________");
//                        }
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
                ROOMS.get(i).getCURTAIN().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {

                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setCurtainSwitchStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
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
                            AC_SENARIO_Status[finalI] = false ;
                            Log.d("acSenario" ,"stop");
                        }
                        else {
                            String t ="";
                            if (ROOMS.get(finalI).acVariables.TempClient == 0) {
                                if (ROOMS.get(finalI).acVariables.TempChars == 3) {
                                    t="240";
                                }
                                else if (ROOMS.get(finalI).acVariables.TempChars == 2) {
                                    t="24";
                                }
                            }
                            else {
                                if (ROOMS.get(finalI).acVariables.TempChars == 3) {
                                    t = String.valueOf((ROOMS.get(finalI).acVariables.TempClient * 10));
                                }
                                else if (ROOMS.get(finalI).acVariables.TempChars == 2) {
                                    t = String.valueOf(ROOMS.get(finalI).acVariables.TempClient);
                                }
                            }
                            String dp = "{\" "+ROOMS.get(finalI).acVariables.TempSetDP+"\": "+t+"}";
                            Log.d("acSenario" ,dp);
                            if (ROOMS.get(finalI).getAC() != null ) {
                                ROOMS.get(finalI).getAC().publishDps(dp,TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        Log.d("acSenario" ,error);
                                    }
                                    @Override
                                    public void onSuccess() {
                                        Log.d("acSenario" ,"clientBack "+ROOMS.get(finalI).acVariables.TempClient);
                                    }
                                });
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
            if (ROOMS.get(i).getLOCK_B() != null) {
                ROOMS.get(i).getLOCK().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("doorAction" , dpStr.toString());
                        if (dpStr != null) {
                            setActionText("Room " + ROOMS.get(finalI).RoomNumber + " lock action ",act);
                            if (dpStr.get("residual_electricity") != null) {
                                ROOMS.get(finalI).getFireRoom().child("lockBattery").setValue(Objects.requireNonNull(dpStr.get("residual_electricity")).toString());
                            }
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
        Log.d("refreshSystem","7 set devices listener");
    }

    static void unregisterDevicesListener() {
        for (int i = 0; i< ROOMS.size(); i++) {
            if (ROOMS.get(i).getDOORSENSOR_B() != null ) {
                ROOMS.get(i).getDOORSENSOR().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSERVICE1_B() != null) {
                ROOMS.get(i).getSERVICE1().unRegisterDevListener();
            }
            if (ROOMS.get(i).getAC_B() !=null) {
                ROOMS.get(i).getAC().unRegisterDevListener();
            }
            if (ROOMS.get(i).getPOWER_B() != null) {
                ROOMS.get(i).getPOWER().unRegisterDevListener();
            }
            if (ROOMS.get(i).getCURTAIN_B() != null) {
                ROOMS.get(i).getCURTAIN().unRegisterDevListener();
            }
            if (ROOMS.get(i).getMOTIONSENSOR_B() != null ) {
                ROOMS.get(i).getMOTIONSENSOR().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH1_B() != null) {
                ROOMS.get(i).getSWITCH1().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH2_B() != null) {
                ROOMS.get(i).getSWITCH2().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH3_B() != null) {
                ROOMS.get(i).getSWITCH3().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH4_B() != null) {
                ROOMS.get(i).getSWITCH4().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH5_B() != null) {
                ROOMS.get(i).getSWITCH5().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH6_B() != null) {
                ROOMS.get(i).getSWITCH6().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH7_B() != null) {
                ROOMS.get(i).getSWITCH7().unRegisterDevListener();
            }
            if (ROOMS.get(i).getSWITCH8_B() != null) {
                ROOMS.get(i).getSWITCH8().unRegisterDevListener();
            }
            if (ROOMS.get(i).getGATEWAY_B() != null) {
                ROOMS.get(i).getGATEWAY().unRegisterDevListener();
            }
            if (ROOMS.get(i).getLOCK_B() != null) {
                ROOMS.get(i).getLOCK().unRegisterDevListener();
            }
        }
        Log.d("refreshSystem","3 remove devices listener");
    }

    static public void setFireRoomsListener() {
        for (int i=0;i<ROOMS.size();i++) {
            int finalI = i;
            if (ROOMS.get(i).getSERVICE1_B() != null) {
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
                                    cancelServiceOrder(ROOMS.get(finalI),"Cleanup");
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
                                    cancelServiceOrder(ROOMS.get(finalI),"Laundry");
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
                                            cancelServiceOrder(ROOMS.get(finalI),"Checkout");
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
                                cancelServiceOrder(ROOMS.get(finalI),"SOS");
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
            ROOMS.get(i).SetPointIntervalListener = ROOMS.get(i).getFireRoom().child("SetPointInterval").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                                MyApp.ProjectVariables.Interval = 1000*60* Integer.parseInt(snapshot.getValue().toString());
                                Log.d("intervalsetpoint" , MyApp.ProjectVariables.Interval+"" );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).DoorWarningListener = ROOMS.get(i).getFireRoom().child("DoorWarning").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ) {
                            try {
                                MyApp.ProjectVariables.DoorWarning = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                                Log.d("DoorInterval", MyApp.ProjectVariables.DoorWarning + "");
                            } catch (Exception e) {
                                Log.d("DoorInterval",e.getMessage());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).roomStatusListener = ROOMS.get(i).getFireRoom().child("roomStatus").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ) {
                            Log.d("roomChangedTo" ,snapshot.getValue().toString() );
                            if (snapshot.getValue().toString().equals("3") && ROOMS.get(finalI).roomStatus != 3) {
                                checkoutModeRoom(ROOMS.get(finalI));
                            }
                            else if (snapshot.getValue().toString().equals("2") && ROOMS.get(finalI).roomStatus != 2) {
                                checkInModeRoom(ROOMS.get(finalI));
                            }
                            ROOMS.get(finalI).roomStatus = Integer.parseInt(snapshot.getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).CheckInModeTimeListener = ROOMS.get(i).getFireRoom().child("CheckInModeTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ) {
                            if (!snapshot.getValue().toString().equals("0")) {
                                MyApp.ProjectVariables.CheckinModeTime = Integer.parseInt( snapshot.getValue().toString());
                                Log.d("checkinModeDuration" , "check in chenged to "+checkInModeTime);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).CheckOutModeTimeListener = ROOMS.get(i).getFireRoom().child("CheckOutModeTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ){
                            if (!snapshot.getValue().toString().equals("0")) {
                                MyApp.ProjectVariables.CheckoutModeTime = Integer.parseInt( snapshot.getValue().toString());
                                Log.d("checkoutModeDuration" , "changed to "+checkOutModeTime+"");
                            }
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
                    RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1")
                    ,ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 0) {
                                    if (ROOMS.get(finalI).getPOWER_B() != null) {
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getPOWER_B().devId).publishDps("{\"1\": false, \"2\": false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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
                                    if (ROOMS.get(finalI).getPOWER_B() != null) {
//                                        turnSwitchButtonOn(ROOMS.get(finalI).getPOWER_B(), "1", new CallbackResult() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("powerActions","success");
//                                            }
//
//                                            @Override
//                                            public void onFail(String error) {
//                                                Log.d("powerActions",error);
//                                            }
//                                        });
//                                        turnSwitchButtonOff(ROOMS.get(finalI).getPOWER_B(), "2", new CallbackResult() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("powerActions","success");
//                                            }
//
//                                            @Override
//                                            public void onFail(String error) {
//                                                Log.d("powerActions",error);
//                                            }
//                                        });
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getPOWER_B().devId).publishDps("{\"1\": true, \"2\": false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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
                                    if (ROOMS.get(finalI).getPOWER_B() != null) {
//                                        turnSwitchButtonOn(ROOMS.get(finalI).getPOWER_B(), "1", new CallbackResult() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("powerActions","success");
//                                            }
//
//                                            @Override
//                                            public void onFail(String error) {
//                                                Log.d("powerActions",error);
//                                            }
//                                        });
//                                        turnSwitchButtonOn(ROOMS.get(finalI).getPOWER_B(), "2", new CallbackResult() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("powerActions","success");
//                                            }
//
//                                            @Override
//                                            public void onFail(String error) {
//                                                Log.d("powerActions",error);
//                                            }
//                                        });
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getPOWER_B().devId).publishDps("{\"1\": true, \"2\": true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                    else {
                                        Log.d("powerActions","null");
                                    }
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\"1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.d("S1FBvalue","success");
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 3) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\": false}", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\": true}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//                                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.d("S1FBvalue","success");
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 0) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\": true}", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//                                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 2\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
//                                            new MessageDialog(error+" "+code,"error",act);
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 0) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("{\" 2\": true}", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 2\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
//                                            new MessageDialog(error+" "+code,"error",act);
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 3) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("\" 3\": false", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("\" 3\": true", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
//                                            new MessageDialog(error+" "+code,"error",act);
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 0) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("\" 3\": true", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("\" 3\": false", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 3) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("\" 4\": false", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("\" 4\": true", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.getValue() != null) {
//                                                        if (Integer.parseInt(snapshot.getValue().toString()) != 0) {
//                                                            ROOMS.get(finalI).getSWITCH1().publishDps("\" 4\": true", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    ROOMS.get(finalI).getSWITCH1().publishDps("\" 4\": false", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 2\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 2\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
//                                        }
//                                    });
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
//                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//                                        @Override
//                                        public void onSuccess() {
//                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
//                                        }
//                                    });
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
                        Log.d("setDevicesList",ROOMS.get(finalI).RoomNumber+" "+result.size());
                        long SetId = 0 ;
                        long PowerId = 0 ;
                        long CurrentId = 0 ;
                        long FanId = 0;
                        for (int j=0 ; j<result.size();j++) {
                            Log.d("setDevicesList",result.get(j).getName());
                            if (result.get(j).getName().equals("Set temp") || result.get(j).getName().equals("temp_set") || result.get(j).getName().equals("Set Temperature") || result.get(j).getName().equals("Set temperature")) {
                                SetId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.TempSetDP = SetId;
                                ROOMS.get(finalI).acVariables.TempMax = result.get(j).getValueSchemaBean().getMax();
                                ROOMS.get(finalI).acVariables.TempMin = result.get(j).getValueSchemaBean().getMin();
                                try{
                                    String x = String.valueOf(ROOMS.get(finalI).acVariables.TempMax);
                                    ROOMS.get(finalI).acVariables.TempChars = x.length();
                                    if (ROOMS.get(finalI).acVariables.TempChars == 2) {
                                        ROOMS.get(finalI).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp ;
                                    }
                                    else {
                                        ROOMS.get(finalI).acVariables.TempSetPoint = MyApp.ProjectVariables.Temp*10 ;
                                    }
                                }catch (Exception e) {
                                    Log.d("ac",e.getMessage());
                                }
                                Log.d("setDevicesListSet",result.get(j).getSchemaBean().property+" "+ROOMS.get(finalI).RoomNumber);
                            }
                            if (result.get(j).getName().equals("Power") || result.get(j).getName().equals("switch") || result.get(j).getName().equals("Switch")) {
                                PowerId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.PowerDP = PowerId;
                                Log.d("setDevicesListPower",result.get(j).getSchemaBean().property+" "+ROOMS.get(finalI).RoomNumber);
                            }
                            if (result.get(j).getName().equals("Current temp") || result.get(j).getName().equals("temp_current") || result.get(j).getName().equals("Current Temperature") || result.get(j).getName().equals("Current temperature")) {
                                CurrentId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.TempCurrentDP = CurrentId;
                                Log.d("setDevicesListCur",result.get(j).getSchemaBean().property+" "+ROOMS.get(finalI).RoomNumber);
                            }
                            if (result.get(j).getName().contains("Fan") || result.get(j).getName().contains("level") || result.get(j).getName().contains("Gear") || result.get(j).getName().contains("FAN") || result.get(j).getName().contains("fan")) {
                                FanId = result.get(j).getDpId() ;
                                ROOMS.get(finalI).acVariables.FanDP = FanId;
                                Log.d("setDevicesListFan",result.get(j).getSchemaBean().property+" "+ROOMS.get(finalI).RoomNumber);//+result.get(j).getSchemaBean().property
                                try {
                                    JSONObject r = new JSONObject(result.get(j).getSchemaBean().property);
                                    String[] v = r.getString("range").split(",");
                                    for (int y = 0;y<v.length;y++) {
                                        v[y] = v[y].replaceAll("\"","");
                                        v[y] = v[y].replace("]","");
                                        v[y] = v[y].replace("[","");
                                        Log.d("setDevicesListFan",v[y]);
                                    }
                                    ROOMS.get(finalI).acVariables.FanValues = v;

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.d("setDevicesList", "set "+SetId+" power "+PowerId+" fan "+FanId + " current "+CurrentId +ROOMS.get(finalI).getAC_B().dps.toString());
                        if (ROOMS.get(finalI).acVariables.PowerDP != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(PowerId)) != null) {
                                long finalPowerId = PowerId;
                                Log.d("setDevicesList",finalPowerId+" power");
                                if (Boolean.parseBoolean(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(PowerId))).toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).setValue(0);
                                }
                                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)),
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+ finalPowerId +"\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(0);
                                                            }
                                                            @Override
                                                            public void onSuccess() {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(3);
                                                            }
                                                        });
                                                    }
                                                    if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+finalPowerId+"\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(3);
                                                            }
                                                            @Override
                                                            public void onSuccess() {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(0);
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
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(SetId)) != null) {
                                long finalSetId = SetId;
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(SetId)).setValue(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(SetId))).toString());
                                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(SetId)),
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(SetId)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    Log.d("tempModify" , snapshot.getValue().toString());
                                                    int newTemp = Integer.parseInt(snapshot.getValue().toString());
                                                    ROOMS.get(finalI).getAC().publishDps("{\" "+ finalSetId +"\":"+newTemp+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {
                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalSetId)).setValue(Integer.parseInt(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(finalSetId))).toString()));
                                                        }
                                                        @Override
                                                        public void onSuccess() {
                                                        }
                                                    });
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        })));
                            }
                        }
                        if (ROOMS.get(finalI).acVariables.FanDP != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(FanId)) != null) {
                                long finalFanId = FanId;
                                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(FanId)),
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(FanId)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    Log.d("fanModify" , snapshot.getValue().toString());
                                                    String value = snapshot.getValue().toString();
                                                    if (value.equals("high") || value.equals("med") || value.equals("low") || value.equals("auto") || value.equals("middle")) {
                                                        ROOMS.get(finalI).getAC().publishDps("{\" "+ finalFanId +"\":\""+value+"\"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalFanId)).setValue(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(finalFanId)));
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
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(FanId)).setValue(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(FanId))).toString());
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {

                    }
                });
            }
            if (ROOMS.get(i).getLOCK_B() != null && ROOMS.get(i).getLOCK() != null) {
                RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getLOCK_B().name).child("1"),
                        ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getLOCK_B().name).child("1").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                        OpenTheDoor(ROOMS.get(finalI), new RequestOrder() {
                                            @Override
                                            public void onSuccess(String token) {
                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getLOCK_B().name).child("1").setValue(0);
                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getLOCK_B().name).child("1").setValue(0);
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
        Log.d("refreshSystem","8 fire rooms listener");
    }

    static void removeFireRoomsDevicesListeners() {
        for (DatabaseReference_ValueEventListener vel : RoomsDevicesReferencesListeners) {
            vel.ref.removeEventListener(vel.listener);
        }
        Log.d("refreshSystem","2 remove fire rooms listener");
    }

    static void turnSwitchButtonOn(DeviceBean S,String B,CallbackResult c) {
        Log.d("powerActions",S.getIsOnline()+" "+S.getIsLocalOnline());
        TuyaHomeSdk.newDeviceInstance(S.devId).publishDps("{\"" + B + "\": true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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

    static void turnSwitchButtonOff(DeviceBean S,String B,CallbackResult c) {
        TuyaHomeSdk.newDeviceInstance(S.devId).publishDps("{\"" + B + "\": false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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

    public void setProjectVariablesListener() {
        Log.d("projectV","setListeners");
        ProjectVariablesRef.child("CheckinModeActive").setValue(MyApp.ProjectVariables.CheckinModeActive);
        ProjectVariablesRef.child("CheckinModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinModeTime = Integer.parseInt(snapshot.getValue().toString());
                    Log.d("checkinTime" ,MyApp.ProjectVariables.CheckinModeTime+" " );
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
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutModeTime = Integer.parseInt(snapshot.getValue().toString());
                    Log.d("checkoutTime" ,MyApp.ProjectVariables.CheckoutModeTime+" " );
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.DoorWarning = Integer.parseInt(snapshot.getValue().toString());
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
                    Log.d("intervalChanged" , snapshot.getValue().toString());
                    MyApp.ProjectVariables.Interval = 1000*60* Integer.parseInt(snapshot.getValue().toString());
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
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
                if(snapshot.getValue() != null) {
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
                    Log.d("refreshSystem",snapshot.getValue().toString());
                    try {
                        refreshSystemTime = Integer.parseInt(snapshot.getValue().toString());
                    }
                    catch (Exception e) {
                        Log.d("refreshSystem",e.getMessage());
                    }
                }
                refreshTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("refreshSystem","started");
                        refreshSystem();
                    }
                },1000*60*60*refreshSystemTime,1000*60*60*refreshSystemTime);
                Log.d("refreshSystem",refreshSystemTime+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setControlDeviceListener() {
        ServerDevice.child("roomsIds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("rerunProblem" , Objects.requireNonNull(snapshot.getValue()).toString());
                for (int i=0;i<ROOMS.size();i++) {
                    if (ROOMS.get(i).CleanupListener != null) {
                        ROOMS.get(i).getFireRoom().child("Cleanup").removeEventListener(ROOMS.get(i).CleanupListener);
                    }
                    if (ROOMS.get(i).LaundryListener != null) {
                        ROOMS.get(i).getFireRoom().child("Laundry").removeEventListener(ROOMS.get(i).LaundryListener);
                    }
                    if (ROOMS.get(i).CheckoutListener != null) {
                        ROOMS.get(i).getFireRoom().child("Checkout").removeEventListener(ROOMS.get(i).CheckoutListener);
                    }
                    if (ROOMS.get(i).DNDListener != null) {
                        ROOMS.get(i).getFireRoom().child("DND").removeEventListener(ROOMS.get(i).DNDListener);
                    }
                    if (ROOMS.get(i).SetPointIntervalListener != null) {
                        ROOMS.get(i).getFireRoom().child("SetPointInterval").removeEventListener(ROOMS.get(i).SetPointIntervalListener);
                    }
                    if (ROOMS.get(i).DoorWarningListener != null) {
                        ROOMS.get(i).getFireRoom().child("DoorWarning").removeEventListener(ROOMS.get(i).DoorWarningListener);
                    }
                    if (ROOMS.get(i).roomStatusListener != null) {
                        ROOMS.get(i).getFireRoom().child("roomStatus").removeEventListener(ROOMS.get(i).roomStatusListener);
                    }
                    if (ROOMS.get(i).CheckInModeTimeListener != null) {
                        ROOMS.get(i).getFireRoom().child("CheckInModeTime").removeEventListener(ROOMS.get(i).CheckInModeTimeListener);
                    }
                    if (ROOMS.get(i).CheckOutModeTimeListener != null) {
                        ROOMS.get(i).getFireRoom().child("CheckOutModeTime").removeEventListener(ROOMS.get(i).CheckOutModeTimeListener);
                    }
                    if (ROOMS.get(i).ClientInListener != null) {
                        ROOMS.get(i).getFireRoom().child("ClientIn").removeEventListener(ROOMS.get(i).ClientInListener);
                    }
                    if (ROOMS.get(i).getPOWER() != null) {
                        ROOMS.get(i).getPOWER().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getDOORSENSOR() != null) {
                        ROOMS.get(i).getDOORSENSOR().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSERVICE1() != null) {
                        ROOMS.get(i).getSERVICE1().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getAC() != null) {
                        ROOMS.get(i).getAC().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getMOTIONSENSOR() != null) {
                        ROOMS.get(i).getMOTIONSENSOR().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getGATEWAY() != null) {
                        ROOMS.get(i).getGATEWAY().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH1() != null) {
                        ROOMS.get(i).getSWITCH1().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH2() != null) {
                        ROOMS.get(i).getSWITCH2().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH3() != null) {
                        ROOMS.get(i).getSWITCH3().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH4() != null) {
                        ROOMS.get(i).getSWITCH4().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH5() != null) {
                        ROOMS.get(i).getSWITCH5().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH6() != null) {
                        ROOMS.get(i).getSWITCH6().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH7() != null) {
                        ROOMS.get(i).getSWITCH7().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH8() != null) {
                        ROOMS.get(i).getSWITCH8().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getCURTAIN() != null) {
                        ROOMS.get(i).getCURTAIN().unRegisterDevListener();
                    }
                }
                refreshSystem();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Add & Cancel Orders ____________________________________

    public static void addCleanupOrder(ROOM room) {
            String url = MyApp.THE_PROJECT.url + "reservations/addCleanupOrderControlDevice"+addCleanupCounter ;
            StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
                Log.d("addCleanupRsp" , response);
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (!result.getString("result").equals("success")) {
                            Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act,"error cleanup "+room.RoomNumber,Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Log.d("addCleanupRsp" , error.toString());
                //Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
            }) {
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
        String url = MyApp.THE_PROJECT.url + "reservations/addLaundryOrderControlDevice"+addLaundryCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addLaundryRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("addLaundryRsp" , e.toString());
                    Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(act,"error laundry "+room.RoomNumber,Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.d("addLaundryRsp" , error.toString());
            Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
        }) {
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
        String url = MyApp.THE_PROJECT.url + "reservations/addCheckoutOrderControlDevice"+addCheckoutCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addCheckoutRsp" , response);
            if (response != null) {
                try {
                    JSONObject result = new JSONObject(response);
                    if (!result.getString("result").equals("success")) {
                        Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(act,"error checkout "+room.RoomNumber,Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.d("addCheckoutRsp" , error.toString());
            Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
        }) {
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
                        Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(act,"error laundry "+room.RoomNumber,Toast.LENGTH_SHORT).show();
            }
        }
                , error -> {
                    Log.d("addDNDRsp" , error.toString());
                    Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }) {
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
                        Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(act,"error sos "+room.RoomNumber,Toast.LENGTH_SHORT).show();
            }
        }
                , error -> {
                    Log.d("addSOSRsp" , error.toString());
                    Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }) {
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
            String url = MyApp.THE_PROJECT.url + "reservations/cancelServiceOrderControlDevice"+cancelOrderCounter;
            StringRequest removOrder = new StringRequest(Request.Method.POST,url, response -> {
                Log.d("cancelPressed", response);
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (!result.getString("result").equals("success")) {
                            Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act,"error " + type +" " +room.RoomNumber,Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Log.d("cancelPressed", error.toString());
                Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("room_id" ,String.valueOf( room.id));
                    params.put("order_type",type);
                    return params;
                }
            };
            CLEANUP_QUEUE.add(removOrder);
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
                        Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(act,"error dnd " +room.RoomNumber,Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.d("cancelPressed", error.toString());
            Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
        }) {
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
//        String url = MyApp.THE_PROJECT.url + "reservations/setClientInOrOut";
//        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("clientInStatus" , response +" " + status);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("clientInStatus" , error.toString() +" " + status);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> Params = new HashMap<>();
//                Params.put("room_id", String.valueOf(room.id));
//                Params.put("status" , status);
//                return Params;
//            }
//        };
//        Volley.newRequestQueue(act).add(tabR);
    }

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

    static void setRoomLockId(String ID,String roomId) {
        Log.d("lockIdRegister" , ID+" "+roomId) ;
        String url = MyApp.THE_PROJECT.url + "roomsManagement/setRoomLockId" ;
        StringRequest re  = new StringRequest(Request.Method.POST, url, response -> Log.d("lockIdRegister" , response), error -> Log.d("lockIdRegister" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> par = new HashMap<>();
                par.put("room_id" ,roomId);
                par.put("lock_id",ID);
                return par;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(re);
    }

    static void checkInModeRoom(ROOM THE_ROOM) {
        if (MyApp.ProjectVariables.getCheckinModeActive()) {
            if (MyApp.checkInActions != null) {
                if (MyApp.checkInActions.power) {
                    if (THE_ROOM.getPOWER_B() != null && THE_ROOM.getPOWER() != null) {
                        THE_ROOM.getPOWER().publishDps("{\" 1\":true,\" 2\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }
                            @Override
                            public void onSuccess() {
                                Log.d("checkinModeTest" ,"power on success");
                                if (MyApp.checkInActions.lights) {
                                    turnLightsOn(THE_ROOM);
//                                    if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH1() != null) {
//                                        if (THEROOM.getSWITCH1_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH1_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH1_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH1_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (THEROOM.getSWITCH2_B() != null && THEROOM.getSWITCH2() != null) {
//                                        if (THEROOM.getSWITCH2_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH2_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH2_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH2_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (THEROOM.getSWITCH3_B() != null && THEROOM.getSWITCH3() != null) {
//                                        if (THEROOM.getSWITCH3_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH3_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH3().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH3_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH3().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH3_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (THEROOM.getSWITCH4_B() != null && THEROOM.getSWITCH4() != null) {
//                                        if (THEROOM.getSWITCH4_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH4_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH4_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH4_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
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
                                        if (THE_ROOM.getAC_B().dps.get("1") != null) {
                                            THE_ROOM.getAC().publishDps("{\" 1\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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
                                if (THE_ROOM.getPOWER_B().dps.get("8") != null) {
                                    int sec = MyApp.ProjectVariables.CheckinModeTime*60 ;
                                    THE_ROOM.getPOWER().publishDps("{\" 8\":"+sec+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            Log.d("checkinModeTest" ,"power status change success");
                                        }
                                    });
                                }
                                else if (THE_ROOM.getPOWER_B().dps.get("10") != null) {
                                    int sec = MyApp.ProjectVariables.CheckinModeTime*60 ;
                                    THE_ROOM.getPOWER().publishDps("{\" 10\":"+sec+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            Log.d("checkinModeTest" ,"power status change success");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    static void checkoutModeRoom(ROOM THE_ROOM)  {
        if (MyApp.ProjectVariables.getCheckoutModeActive()) {
            if (MyApp.checkOutActions != null) {
                if (MyApp.checkOutActions.power) {
                    if (THE_ROOM.getPOWER_B() != null && THE_ROOM.getPOWER() != null) {
                        if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                            if (THE_ROOM.getPOWER_B().dps.get("8") != null) {
                                THE_ROOM.getPOWER().publishDps("{\" 8\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                            else if (THE_ROOM.getPOWER_B().dps.get("10") != null) {
                                THE_ROOM.getPOWER().publishDps("{\" 10\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                            if (THE_ROOM.getPOWER_B().dps.get("8") != null) {
                                THE_ROOM.getPOWER().publishDps("{\" 8\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+" , \" 7\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                            else if (THE_ROOM.getPOWER_B().dps.get("10") != null) {
                                THE_ROOM.getPOWER().publishDps("{\" 10\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+" , \" 9\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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
                else {
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep((long) MyApp.ProjectVariables.CheckoutModeTime * 60 * 1000);
                            if (MyApp.ProjectVariables.getCheckoutModeActive()) {
                                if (MyApp.checkOutActions != null) {
                                    if (MyApp.checkOutActions.lights) {
                                        if (THE_ROOM.getSWITCH1_B() != null && THE_ROOM.getSWITCH1() != null) {
                                            if (THE_ROOM.getSWITCH1_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH1().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH1_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH1().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH1_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH1().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH1_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH1().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH2_B() != null && THE_ROOM.getSWITCH2() != null) {
                                            if (THE_ROOM.getSWITCH2_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH2().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH2_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH2().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH2_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH2().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH2_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH2().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH3_B() != null && THE_ROOM.getSWITCH3() != null) {
                                            if (THE_ROOM.getSWITCH3_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH3().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH3_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH3().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH3_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH3().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH3_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH3().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH4_B() != null && THE_ROOM.getSWITCH4() != null) {
                                            if (THE_ROOM.getSWITCH4_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH4().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH4_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH4().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH4_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH4().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH4_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH4().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH5_B() != null && THE_ROOM.getSWITCH5() != null) {
                                            if (THE_ROOM.getSWITCH5_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH5().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH5_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH5().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH5_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH5().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH5_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH5().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH6_B() != null && THE_ROOM.getSWITCH6() != null) {
                                            if (THE_ROOM.getSWITCH6_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH6().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH6_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH6().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH6_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH6().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH6_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH6().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH7_B() != null && THE_ROOM.getSWITCH7() != null) {
                                            if (THE_ROOM.getSWITCH7_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH7().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH7_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH7().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH7_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH7().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH7_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH7().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        if (THE_ROOM.getSWITCH8_B() != null && THE_ROOM.getSWITCH8() != null) {
                                            if (THE_ROOM.getSWITCH8_B().dps.get("1") != null) {
                                                THE_ROOM.getSWITCH8().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH8_B().dps.get("2") != null) {
                                                THE_ROOM.getSWITCH8().publishDps("{\" 2\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH8_B().dps.get("3") != null) {
                                                THE_ROOM.getSWITCH8().publishDps("{\" 3\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }
                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                            if (THE_ROOM.getSWITCH8_B().dps.get("4") != null) {
                                                THE_ROOM.getSWITCH8().publishDps("{\" 4\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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
                                    if (MyApp.checkOutActions.ac) {
                                        if (THE_ROOM.getAC_B() != null && THE_ROOM.getAC() != null) {
                                            if (THE_ROOM.getAC_B().dps.get("1") != null) {
                                                THE_ROOM.getAC().publishDps("{\" 1\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                }
            }
        }
//        String Duration = "" ;
//        if (checkOutModeTime != 0 ) {
//            Duration = String.valueOf(checkOutModeTime * 60);
//        }
//        else {
//            Duration = "60" ;
//        }
//        Log.d("checkoutModeDuration" , Duration+" "+checkOutModeTime );
//        if (THEROOM.getPOWER_B() != null) {
//            THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    Toast.makeText(act, "Checkout failed room "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onSuccess() {
//                    THEROOM.getPOWER().publishDps("{\"2\": false}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Toast.makeText(act, "Checkout failed room "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(act, "Checkout room success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//        }
//        for (ServiceEmps u : Emps) {
//            if (u.department.equals("Cleanup") || u.department.equals("Service")) {
//                makemessage(u.token, "Cleanup", true, THEROOM.RoomNumber);
//            }
//        }
    }

    public static void runClientBackActions(ROOM room) {
        if (MyApp.clientBackActions.lights ) {
            if (room.roomStatus == 2) {
                if (room.ClientIn == 0) {
                    if (room.getPOWER_B() != null && room.getPOWER() != null) {
                        if (room.getPOWER_B().getDps().get("1") != null && room.getPOWER_B().getDps().get("2") != null) {
                            room.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                    int sec = 2*60 ;
                                    if (room.getPOWER_B().getDps().get("8")!= null) {
                                        room.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                    else if (room.getPOWER_B().getDps().get("10")!= null) {
                                        room.getPOWER().publishDps("{\" 10\":"+sec+"}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                    turnLightsOn(room);
//                                    if (room.getSWITCH1_B() != null && room.getSWITCH1() != null) {
//                                        if (room.getSWITCH1_B().dps.get("1") != null) {
//                                            room.getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH1_B().dps.get("2") != null) {
//                                            room.getSWITCH1().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH1_B().dps.get("3") != null) {
//                                            room.getSWITCH1().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH1_B().dps.get("4") != null) {
//                                            room.getSWITCH1().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (room.getSWITCH2_B() != null && room.getSWITCH2() != null) {
//                                        if (room.getSWITCH2_B().dps.get("1") != null) {
//                                            room.getSWITCH2().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH2_B().dps.get("2") != null) {
//                                            room.getSWITCH2().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH2_B().dps.get("3") != null) {
//                                            room.getSWITCH2().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH2_B().dps.get("4") != null) {
//                                            room.getSWITCH2().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (room.getSWITCH3_B() != null && room.getSWITCH3() != null) {
//                                        if (room.getSWITCH3_B().dps.get("1") != null) {
//                                            room.getSWITCH3().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH3_B().dps.get("2") != null) {
//                                            room.getSWITCH3().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH3_B().dps.get("3") != null) {
//                                            room.getSWITCH3().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH3_B().dps.get("4") != null) {
//                                            room.getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (room.getSWITCH4_B() != null && room.getSWITCH4() != null) {
//                                        if (room.getSWITCH4_B().dps.get("1") != null) {
//                                            room.getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH4_B().dps.get("2") != null) {
//                                            room.getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH4_B().dps.get("3") != null) {
//                                            room.getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH4_B().dps.get("4") != null) {
//                                            room.getSWITCH4().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        if (MyApp.clientBackActions.curtain) {
            if (room.roomStatus == 2) {
                if (room.ClientIn == 0) {
                    if (room.getPOWER_B() != null && room.getPOWER() != null) {
                        if (room.getPOWER_B().getDps().get("1") != null && room.getPOWER_B().getDps().get("2") != null) {
                            room.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                    int sec = 2*60 ;
                                    room.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
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
                            });
                        }
                    }
                }
            }
        }
        if (MyApp.clientBackActions.ac) {
            if (room.roomStatus == 2) {
                if (room.ClientIn == 0) {
                    if (room.getPOWER_B() != null && room.getPOWER() != null) {
                        room.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }
                            @Override
                            public void onSuccess() {
                                int sec = 2*60 ;
                                if (room.getPOWER_B().getDps().get("8")!= null) {
                                    room.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                                else if (room.getPOWER_B().getDps().get("10")!= null) {
                                    room.getPOWER().publishDps("{\" 10\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                                if (room.getAC_B() != null && room.getAC() != null) {
                                    room.getAC().publishDps("{\" 1\": true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    static void OpenTheDoor(ROOM THE_ROOM,RequestOrder callBack) {
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
            searchBtn.setVisibility(View.VISIBLE);
            Toast.makeText(act,"devices are "+Devices.size(),Toast.LENGTH_LONG).show();
        }
        else if (roomsListView.getVisibility() == View.GONE) {
            roomsListView.setVisibility(View.VISIBLE);
            devicesListView.setVisibility(View.GONE);
            searchBtn.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
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
            lockDB.modifyValue("on");
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnSLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
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
                        int jobnum = 0 ;
                        if (child.child("jobNumber").getValue() != null ) {
                            jobnum = Integer.parseInt(Objects.requireNonNull(child.child("jobNumber").getValue()).toString());
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
                       EmpS.add(new ServiceEmps(id,1,name,jobnum,department,mobile,token));
                    }
                    Log.d("EmpsAre ", EmpS.size()+"");
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
//        GatewayClient.getDefault().startScanGateway(new ScanGatewayCallback() {
//            @Override
//            public void onScanGatewaySuccess(ExtendedBluetoothDevice device) {
////                LogUtil.d("device:" + device);
//                TheFoundGateway = device ;
//                gatewaysList.add(device);
//                String[] xx = new String[gatewaysList.size()];
//                for (int i=0; i<gatewaysList.size();i++) {
//                    xx[i] = gatewaysList.get(i).getName();
//                }
//                ArrayAdapter<String> ad = new ArrayAdapter<String>(act,R.layout.gateway_list_item,xx);
//                gatewaysListView.setAdapter(ad);
//                GatewayClient.getDefault().stopScanGateway();
////                if (mListApapter != null)
////                    mListApapter.updateData(device);
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//
//            }
//        });
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
                                LogUtil.d("gateway init success");
                                Toast.makeText(act,"gateway inited",Toast.LENGTH_LONG).show();
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
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(Login.THEHOME.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                loading.stop();
                SCENES = result ;
                Log.d("scenesAre",SCENES.size()+"");
                for (SceneBean s : SCENES) {
                    Log.d("scenesAre",s.getName());
//                    if (s.getName().contains("104") ) { //|| s.getName().contains("ServiceSwitchLaundryScene") || s.getName().contains("ServiceSwitchCheckoutScene") || s.getName().contains("ServiceSwitchDNDScene")
//                        TuyaHomeSdk.newSceneInstance(s.getId()).deleteScene(new IResultCallback() {
//                            @Override
//                            public void onSuccess() {
//                                //Log.d(TAG, "Delete Scene Success");
//                            }
//                            @Override
//                            public void onError(String errorCode, String errorMessage) {
//                            }
//                        });
//                    }
                }
                setSCENES(SCENES);
            }
            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.d("scenesAre",errorCode+" "+errorMessage);
                new MessageDialog("getting tuya sceins failed "+errorMessage,"error",act);
            }
        });
    }

    static void setSCENES(List<SceneBean> SCENES) {
        for (int i = 0; i< ROOMS.size(); i++) {
            if (MyApp.ProjectVariables.cleanupButton != 0 && MyApp.ProjectVariables.dndButton != 0) {
                if (ROOMS.get(i).getSERVICE1() != null) {
                    if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)) != null && ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                    if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene2")) {
                        List<SceneCondition> condS = new ArrayList<>();
                        List<SceneTask> tasks = new ArrayList<>();
                        if (ROOMS.get(i).getSERVICE1_B() != null) {
                            BoolRule rule = BoolRule.newInstance("dp"+MyApp.ProjectVariables.dndButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(MyApp.ProjectVariables.cleanupButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                            tasks.add(task);
                            TuyaHomeSdk.getSceneManagerInstance().createScene(
                                    Login.THEHOME.getHomeId(),
                                    ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene2", // The name of the scene.
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
                    if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchCleanupScene")) {
                        List<SceneCondition> conds = new ArrayList<>();
                        List<SceneTask> tasks = new ArrayList<>();
                        if (ROOMS.get(i).getSERVICE1_B() != null) {
                            BoolRule rule = BoolRule.newInstance("dp"+MyApp.ProjectVariables.cleanupButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.cleanupButton), rule);
                            conds.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(MyApp.ProjectVariables.dndButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                            tasks.add(task);
                            TuyaHomeSdk.getSceneManagerInstance().createScene(
                                    Login.THEHOME.getHomeId(),
                                    ROOMS.get(i).RoomNumber + "ServiceSwitchCleanupScene", // The name of the scene.
                                    false,
                                    IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                    conds, // The effective period. This parameter is optional.
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
                if (ROOMS.get(i).getSERVICE1() != null) {
                    if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)) != null && ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                        if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene3")) {
                            List<SceneCondition> conds = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (ROOMS.get(i).getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.dndButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                                conds.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.laundryButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        Login.THEHOME.getHomeId(),
                                        ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene3", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        conds, // The effective period. This parameter is optional.
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
                        if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchLaundryScene")) {
                            List<SceneCondition> conds = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (ROOMS.get(i).getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.laundryButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.laundryButton), rule);
                                conds.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.dndButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        Login.THEHOME.getHomeId(),
                                        ROOMS.get(i).RoomNumber + "ServiceSwitchLaundryScene", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        conds, // The effective period. This parameter is optional.
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
                if (ROOMS.get(i).getSERVICE1() != null) {
                    if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null && ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                        if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene4")) {
                            List<SceneCondition> conds = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (ROOMS.get(i).getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.dndButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                                conds.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.checkoutButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        Login.THEHOME.getHomeId(),
                                        ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene4", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        conds, // The effective period. This parameter is optional.
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
                        if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchCheckoutScene")) {
                            List<SceneCondition> conds = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            if (ROOMS.get(i).getSERVICE1_B() != null) {
                                BoolRule rule = BoolRule.newInstance("dp" + MyApp.ProjectVariables.checkoutButton, true);
                                SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), String.valueOf(MyApp.ProjectVariables.dndButton), rule);
                                conds.add(cond);
                                HashMap<String, Object> taskMap = new HashMap<>();
                                taskMap.put(String.valueOf(MyApp.ProjectVariables.dndButton), false); // Starts a device.
                                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                                tasks.add(task);
                                TuyaHomeSdk.getSceneManagerInstance().createScene(
                                        Login.THEHOME.getHomeId(),
                                        ROOMS.get(i).RoomNumber + "ServiceSwitchCheckoutScene", // The name of the scene.
                                        false,
                                        IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                                        conds, // The effective period. This parameter is optional.
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
                new MessageDialog("getting tuya sceins failed "+s1,"error",act);
            }
        });
    }

    public static boolean searchScene (List<SceneBean> list , String name) {
        boolean res = false ;
        for (int i=0 ; i<list.size();i++) {
            if (list.get(i).getName().equals(name)) {
                res = true ;
                break;
            }
        }
        return res ;
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

    public void login(){
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
        Volley.newRequestQueue(act).add(req);
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
        Log.d("checkinModeTest" , "here");
        DeviceBean d = null ;
        if (s.getConditions() != null) {
            Log.d("checkinModeTest" , "cond not null "+s.getConditions().get(0).getEntityId());
            if (s.getConditions().get(0) != null) {
                Log.d("checkinModeTest" ,"cond 0 not null");
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
                Log.d("checkinModeTest" , "cond 0 null");
            }
        }
        else {
            Log.d("checkinModeTest" , "cond null");
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
        List<SceneBean> ss = getRoomScenes(THE_ROOM,SCENES);
        SceneBean S = getMood(ss,"Living") ;
        if (S != null) {
            Log.d("checkinModeTest" ,"scene found");
            DeviceBean D = getMoodConditionDevice(S,THE_ROOM) ;
            if (D != null) {
                Log.d("checkinModeTest" ,"device found");
                String button = getMoodConditionDeviceButton(S);
                if (button != null) {
                    Log.d("checkinModeTest" ,"button found");
                    TuyaHomeSdk.newDeviceInstance(D.devId).publishDps("{\" "+button+"\":true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("checkinModeTest" ,error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("checkinModeTest" ,"living started");
                        }
                    });
                }
            }
            else {
                Log.d("checkinModeTest" ,"device null");
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
    }

    static void setActionText(String action , Activity act) {
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
//        Timer T = new Timer();
//        final long[] x = {0};
//        T.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                ServerDevice.child("working").setValue(x[0]);
//                x[0]++;
//            }
//        },0,1000*60);
    }

    static void logInFunction(PROJECT project,CallbackResult callback) {
        String COUNTRY_CODE = "966";
        TuyaHomeSdk.getUserInstance().loginWithEmail(COUNTRY_CODE,project.TuyaUser ,project.TuyaPassword , new ILoginCallback() {
            @Override
            public void onSuccess (User user) {
                Log.d("tuyaLoginResp",project.projectName);
                MyApp.TuyaUser = user ;
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error) {
                        //Toast.makeText(act,"TUya Login Failed" + error,Toast.LENGTH_LONG).show();
                        callback.onFail(error);
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans) {
                        MyApp.homeBeans = homeBeans ;
                        for(int i=0;i<MyApp.homeBeans.size();i++) {
                            Log.d("tuyaLoginResp",MyApp.homeBeans.get(i).getName());
                            if (MyApp.THE_PROJECT.projectName.equals("apiTest")) {
                                if (MyApp.homeBeans.get(i).getName().equals("Test")) {
                                    MyApp.HOME = MyApp.homeBeans.get(i) ;
                                    break;
                                }
                            }
                            else if (MyApp.THE_PROJECT.projectName.contains(MyApp.homeBeans.get(i).getName())) {
                                MyApp.HOME = MyApp.homeBeans.get(i);
                                break;
                            }
                        }
                        callback.onSuccess();
                    }
                });
            }

            @Override
            public void onError (String code, String error) {
                Log.d("tuyaLoginResp",error+" "+code);
                callback.onFail(error);
            }
        });
    }

    static void setTuyaApplication() {
        Log.d("refreshSystem","1 app init");
        TuyaHomeSdk.init(MyApp.app);
        TuyaHomeSdk.setOnNeedLoginListener(context -> {
            TuyaHomeSdk.init(MyApp.app);
            logInFunction(MyApp.THE_PROJECT, new CallbackResult() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(String error) {

                }
            });
        });
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