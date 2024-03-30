package com.example.mobilecheckdevice;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.Interface.CreteMoodsCallBack;
import com.example.mobilecheckdevice.Interface.RequestCallback;
import com.example.mobilecheckdevice.Interface.RequestCallbackResult;
import com.example.mobilecheckdevice.lock.AccountInfo;
import com.example.mobilecheckdevice.lock.ApiService;
import com.example.mobilecheckdevice.lock.GatewayObj;
import com.example.mobilecheckdevice.lock.LockObj;
import com.example.mobilecheckdevice.lock.MyApplication;
import com.example.mobilecheckdevice.lock.RetrofitAPIManager;
import com.example.mobilecheckdevice.lock.ServerError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;

public class Rooms extends AppCompatActivity {
    static RecyclerView DevicesRecycler;
    static GridView RoomsRecycler;
    static List<ROOM> ROOMS;
    static String getRoomsUrl ;
    static Activity act ;
    static ArrayList<LockObj> Locks ;
    static AccountInfo accountInfo;
    public static AccountInfo acc;
    static List<DeviceBean> Devices ;
    static FirebaseDatabase database ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    static int checkInModeTime = 0 ;
    static int checkOutModeTime = 0 ;
    private LockDB lockDB ;
    LinearLayout btnSLayout,mainLogo ;
    static RequestQueue MessagesQueue;
    static boolean CHANGE_STATUS = false ;
    static LoadingDialog loading;
    static RequestQueue REQ;
    EditText searchText ;
    ListView gatewaysListView ;
    ExtendedBluetoothDevice TheFoundGateway ;
    private ConfigureGatewayInfo configureGatewayInfo;
    static List<SceneBean> SCENES ;
    public static List<String> IMAGES ;
    static DatabaseReference ProjectVariablesRef , DevicesControls , ProjectDevices ,RoomTemplates ;
    public static CheckInHome SelectedHome;
    public static String New_Home_Name ;
    Button locksButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        setActivity();
        getData();
        getSceneBGs();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (CHANGE_STATUS) {
            getRooms(new RequestCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(String error) {

                }
            });
            loginTTLock();
            CHANGE_STATUS = false ;
        }
    }

    private void setActivity() {
        act = this ;
        REQ = Volley.newRequestQueue(act);
        lockDB = new LockDB(act);
        if (!lockDB.isLoggedIn()) {
            lockDB.removeAll();
            lockDB.insertLock("off");
        }
        SCENES = new ArrayList<>();
        getRoomsUrl = MyApp.THE_PROJECT.url + "roomsManagement/getRooms" ;
        configureGatewayInfo = new ConfigureGatewayInfo();
        gatewaysListView = findViewById(R.id.scanLockGatewayList);
        searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (DevicesRecycler.getVisibility() == View.VISIBLE ) {
                    String Text = searchText.getText().toString() ;
                    List<DeviceBean> Results = new ArrayList<>();
                    for (int i = 0 ; i < Devices.size() ; i++) {
                        if (Devices.get(i).getName().contains(Text)) {
                            Results.add(Devices.get(i));
                        }
                    }
                    if (Results.size() > 0 ) {
                        Device_Adapter adapter = new Device_Adapter(Results);
                        DevicesRecycler.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(act,"no results",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        MessagesQueue = Volley.newRequestQueue(act);
        mainLogo = findViewById(R.id.logoLyout) ;
        btnSLayout = findViewById(R.id.btnsLayout);
        locksButton = findViewById(R.id.button17);
        TextView hotelName = findViewById(R.id.hotelName);
        hotelName.setText(MyApp.THE_PROJECT.projectName);
        ROOMS = new ArrayList<>();
        Locks = new ArrayList<>();
        Devices = new ArrayList<>();
        RoomsRecycler = findViewById(R.id.rooms_recycler);
        GridLayoutManager managerD = new GridLayoutManager(act,8);
        DevicesRecycler = findViewById(R.id.DevicesListView);
        DevicesRecycler.setLayoutManager(managerD);
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/"); //  https://hotelservices-ebe66.firebaseio.com/
        ProjectVariablesRef = database.getReference(MyApp.THE_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.THE_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.THE_PROJECT.projectName+"Devices");
        RoomTemplates = database.getReference(MyApp.THE_PROJECT.projectName+"Templates");
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        mainLogo.setOnLongClickListener(v -> {
            Dialog  dd = new Dialog(act);
            dd.setContentView(R.layout.lock_unlock_dialog);
            Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = dd.findViewById(R.id.messageDialog_ok);
            EditText password = dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(v1 -> dd.dismiss());
            lock.setOnClickListener(v12 -> {
                final LoadingDialog loading = new LoadingDialog(act);
                final String pass = password.getText().toString() ;
                StringRequest re = new StringRequest(Request.Method.POST, "", response -> {
                    Log.d("LoginResult" , response );
                    loading.stop();
                    if (response.equals("1")) {
                        lockDB.modifyValue("off");
                        //roomsListView.setVisibility(View.VISIBLE);
                        DevicesRecycler.setVisibility(View.GONE);
                        btnSLayout.setVisibility(View.VISIBLE);
                        mainLogo.setVisibility(View.GONE);
                        dd.dismiss();
                    }
                    else if (response.equals("0")) {
                        Toast.makeText(act,"UnLock Failed",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(act,"No Params",Toast.LENGTH_LONG).show();
                    }

                }, error -> loading.stop()) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> par = new HashMap<>();
                        par.put( "password" , pass ) ;
                        par.put( "hotel" , "1" ) ;
                        return par;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            });
            dd.show();
            return false;
        });
        mainLogo.setVisibility(View.GONE);
        RoomsRecycler.setVisibility(View.VISIBLE);
        DevicesRecycler.setVisibility(View.GONE);
        hideSystemUI();
        searchText.setVisibility(View.GONE);
    }

    public void getData() {
        loading = new LoadingDialog(act);
        getProjectVariables(new RequestCallback() {
            @Override
            public void onSuccess() {
                getRooms(new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        ROOM.sortRoomsByNumber(ROOMS);
                        MyApp.ROOMS = ROOMS ;
                        TextView hotelName = act.findViewById(R.id.hotelName);
                        hotelName.setText(MessageFormat.format("{0} {1} room", MyApp.THE_PROJECT.projectName, ROOMS.size()));
                        loginTTLock();
                        getTuyaDevices(new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                if (Devices.size() == 0) {
                                    Toast.makeText(act,"no devices",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(act,"Devices are: "+Devices.size(),Toast.LENGTH_LONG).show();
                                    setRoomsDevices();
                                }
                                Rooms_Adapter_Base Adapter = new Rooms_Adapter_Base(ROOMS,act);
                                RoomsRecycler.setAdapter(Adapter);
                                quickSort(Devices,0,Devices.size()-1);
                                Device_Adapter adapter = new Device_Adapter(Devices);
                                DevicesRecycler.setAdapter(adapter);
                                setHomesSpinner();
                                loading.stop();
                            }

                            @Override
                            public void onFail(String error) {
                                loading.stop();
                                AlertDialog.Builder b = new AlertDialog.Builder(act);
                                b.setTitle("Getting Devices Failed").setMessage(error)
                                        .setPositiveButton("yes", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            getData();
                                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                            }
                        }) ;
                    }

                    @Override
                    public void onFail(String error) {
                        loading.stop();
                        AlertDialog.Builder b = new AlertDialog.Builder(act);
                        b.setTitle("Getting Rooms Failed").setMessage(error)
                                .setPositiveButton("yes", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    getData();
                                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                    }
                });
            }

            @Override
            public void onFail(String error) {
                loading.stop();
                AlertDialog.Builder b = new AlertDialog.Builder(act);
                b.setTitle("Project Variables Getting Failed").setMessage(error)
                        .setPositiveButton("yes", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            getData();
                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
            }
        });
    }

    /* getting project variables
    * then get rooms */
    private void getProjectVariables(RequestCallback callback) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/getProjectVariables";
        StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("projectVariables" , response);
            try {
                JSONObject row = new JSONObject(response);
                JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                MyApp.ProjectVariables = new PROJECT_VARIABLES(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                MyApp.checkInActions = new CheckInActions(MyApp.ProjectVariables.CheckinActions);
                MyApp.checkOutActions = new CheckoutActions(MyApp.ProjectVariables.CheckoutActions);
                MyApp.clientBackActions = new ClientBackActions(MyApp.ProjectVariables.OnClientBack);
                callback.onSuccess();

            }
            catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> callback.onFail(error.toString()));
        if (REQ == null) {
            REQ = Volley.newRequestQueue(act);
        }
        REQ.add(re);
    }

    /* getting project rooms
    * then get Tuya devices*/
    public static void getRooms(RequestCallback callback) {
        Log.d("GettingRooms","start "+getRoomsUrl);
        StringRequest re = new StringRequest(Request.Method.GET, getRoomsUrl, response -> {
            Log.d("GettingRooms",response);
            if (response.equals("0")) {
                callback.onFail("no rooms detected ");
            }
            try {
                JSONArray arr = new JSONArray(response);
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
                Log.d("GettingRooms",ROOMS.size()+"");
                callback.onSuccess();
            }
            catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> callback.onFail(error.toString()));
        if (REQ == null) {
            REQ = Volley.newRequestQueue(act);
        }
        REQ.add(re);
    }

    static void setHomesSpinner() {
        Spinner homes = act.findViewById(R.id.spinner3);
        String[] hs = new String[MyApp.ProjectHomes.size()];
        for (int i=0;i<MyApp.ProjectHomes.size();i++) {
            hs[i] = MyApp.ProjectHomes.get(i).Home.getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,hs);
        homes.setAdapter(adapter);
        homes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedHome = MyApp.ProjectHomes.get(i);
                TextView devicesCount = act.findViewById(R.id.textView30);
                if (MyApp.ProjectHomes.get(i).Devices == null) {
                    devicesCount.setText(MessageFormat.format("{0} Device", 0));
                } else {
                    devicesCount.setText(MessageFormat.format("{0} Device", SelectedHome.Devices.size()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public static void refreshSystem() {
        loading = new LoadingDialog(act);
        getTuyaDevices(new RequestCallback() {
            @Override
            public void onSuccess() {
                loading.stop();
                Toast.makeText(act,"Devices are: "+Devices.size(),Toast.LENGTH_LONG).show();
                setRoomsDevices();
                quickSort(Devices,0,Devices.size()-1);
                Device_Adapter adapter = new Device_Adapter(Devices);
                DevicesRecycler.setAdapter(adapter);
                Rooms_Adapter_Base Adapter = new Rooms_Adapter_Base(ROOMS,act);
                RoomsRecycler.setAdapter(Adapter);
            }

            @Override
            public void onFail(String error) {
                new MessageDialog(error,"error",act);
                loading.stop();
            }
        });
        ProjectVariablesRef.child("RefreshSystemTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    ProjectVariablesRef.child("RefreshSystemTime").setValue(10);
                    ProjectVariablesRef.child("RefreshSystemTime").setValue(snapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void loginTTLock() {
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

    private static void getLocks() {
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
                            Log.d("locksNum" ,String.valueOf( Locks.size() ));
                        }
                        catch (JSONException e) {
                            Log.d("locksNum" ,e.getMessage());
                        }
                        setLocks(Locks);
                    }
                    else {
                        Log.d("locksNum" , "no locks");
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
            }
        });
    }

    static void getTuyaDevices(RequestCallback callback) {
        Log.d("getDevicesRun","started");
        Devices.clear();
        final int[] x = {0};
        for (int i=0; i< MyApp.ProjectHomes.size();i++) {
            Log.d("getDevicesRun","number "+i);
            int finalI = i;
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("getDevicesRun","number "+ finalI +" run "+MyApp.ProjectHomes.get(finalI).Home.getName());
                    TuyaHomeSdk.newHomeInstance(MyApp.ProjectHomes.get(finalI).Home.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean homeBean) {
                            x[0]++;
                            Log.d("gettingDevices","result "+x[0]+" "+homeBean.getDeviceList().size()+" "+MyApp.ProjectHomes.get(finalI).Home.getName());
                            MyApp.ProjectHomes.get(finalI).Devices = homeBean.getDeviceList();
                            for (DeviceBean d : homeBean.getDeviceList()) {
                                if (MyApp.searchDeviceInList(Devices,d.devId) == null) {
                                    Devices.add(d);
                                }
                            }
                            if (x[0] == MyApp.ProjectHomes.size()) {
                                Log.d("gettingDevices","devices "+Devices.size());
                                callback.onSuccess();
                            }
                        }
                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            Log.d("gettingDevices","error "+errorMsg);
                            callback.onFail(errorCode+" "+errorMsg);
                        }
                    });
                }
            }, (long) i * 4 * 1000);
        }
    }

    static void setRoomsDevices() {
        for (int i=0;i<ROOMS.size();i++) {
            DeviceBean power = searchRoomDevice(Devices,ROOMS.get(i),"Power");
            if (power == null) {
                ROOMS.get(i).PowerSwitch = 0 ;
            }
            else {
                ROOMS.get(i).setPOWER_B(power);
                ROOMS.get(i).setPOWER(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getPOWER_B().devId));
                ROOMS.get(i).PowerSwitch = 1 ;
            }
            DeviceBean ac = searchRoomDevice(Devices,ROOMS.get(i),"AC") ;
            if (ac == null) {
                ROOMS.get(i).Thermostat = 0 ;
            }
            else {
                ROOMS.get(i).setAC_B(ac);
                ROOMS.get(i).setAC(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getAC_B().devId));
                ROOMS.get(i).Thermostat = 1 ;
            }
            DeviceBean ZGatway = searchRoomDevice(Devices,ROOMS.get(i),"ZGatway") ;
            if (ZGatway == null) {
                ROOMS.get(i).ZBGateway = 0 ;
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
            }
            else {
                ROOMS.get(i).setDOORSENSOR_B(DoorSensor);
                ROOMS.get(i).setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getDOORSENSOR_B().devId));
                ROOMS.get(i).DoorSensor = 1 ;
            }
            DeviceBean MotionSensor = searchRoomDevice(Devices,ROOMS.get(i),"MotionSensor") ;
            if (MotionSensor == null) {
                ROOMS.get(i).MotionSensor = 0 ;
            }
            else {
                ROOMS.get(i).setMOTIONSENSOR_B(MotionSensor);
                ROOMS.get(i).setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getMOTIONSENSOR_B().devId));
                ROOMS.get(i).MotionSensor = 1 ;
            }
            DeviceBean Curtain = searchRoomDevice(Devices,ROOMS.get(i),"Curtain") ;
            if (Curtain == null) {
                ROOMS.get(i).CurtainSwitch = 0 ;
            }
            else {
                ROOMS.get(i).setCURTAIN_B(Curtain);
                ROOMS.get(i).setCURTAIN(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getCURTAIN_B().devId));
                ROOMS.get(i).CurtainSwitch = 1 ;
            }
            DeviceBean ServiceSwitch = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch") ;
            if (ServiceSwitch == null) {
                ROOMS.get(i).ServiceSwitch = 0 ;
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
            }
            else {
                ROOMS.get(i).setSWITCH1_B(Switch1);
                ROOMS.get(i).setSWITCH1(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH1_B().devId));
                ROOMS.get(i).Switch1 = 1 ;
            }
            DeviceBean Switch2 = searchRoomDevice(Devices,ROOMS.get(i),"Switch2") ;
            if (Switch2 == null) {
                ROOMS.get(i).Switch2 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH2_B(Switch2);
                ROOMS.get(i).setSWITCH2(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH2_B().devId));
                ROOMS.get(i).Switch2 = 1 ;
            }
            DeviceBean Switch3 = searchRoomDevice(Devices,ROOMS.get(i),"Switch3") ;
            if (Switch3 == null) {
                ROOMS.get(i).Switch3 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH3_B(Switch3);
                ROOMS.get(i).setSWITCH3(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH3_B().devId));
                ROOMS.get(i).Switch3 = 1 ;
            }
            DeviceBean Switch4 = searchRoomDevice(Devices,ROOMS.get(i),"Switch4") ;
            if (Switch4 == null) {
                ROOMS.get(i).Switch4 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH4_B(Switch4);
                ROOMS.get(i).setSWITCH4(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH4_B().devId));
                ROOMS.get(i).Switch4 = 1 ;
            }
            DeviceBean Switch5 = searchRoomDevice(Devices,ROOMS.get(i),"Switch5") ;
            if (Switch5 == null) {
                ROOMS.get(i).Switch5 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH5_B(Switch5);
                ROOMS.get(i).setSWITCH5(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH5_B().devId));
                ROOMS.get(i).Switch5 = 1 ;
            }
            DeviceBean Switch6 = searchRoomDevice(Devices,ROOMS.get(i),"Switch6") ;
            if (Switch6 == null) {
                ROOMS.get(i).Switch6 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH6_B(Switch6);
                ROOMS.get(i).setSWITCH6(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH6_B().devId));
                ROOMS.get(i).Switch6 = 1 ;
            }
            DeviceBean Switch7 = searchRoomDevice(Devices,ROOMS.get(i),"Switch7") ;
            if (Switch7 == null) {
                ROOMS.get(i).Switch7 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH7_B(Switch7);
                ROOMS.get(i).setSWITCH7(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH7_B().devId));
                ROOMS.get(i).Switch7 = 1 ;
            }
            DeviceBean Switch8 = searchRoomDevice(Devices,ROOMS.get(i),"Switch8") ;
            if (Switch8 == null) {
                ROOMS.get(i).Switch8 = 0 ;
            }
            else {
                ROOMS.get(i).setSWITCH8_B(Switch8);
                ROOMS.get(i).setSWITCH8(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH8_B().devId));
                ROOMS.get(i).Switch8 = 1 ;
            }
            DeviceBean lock = searchRoomDevice(Devices,ROOMS.get(i),"Lock") ;
            if (lock == null) {
                ROOMS.get(i).lock = 0 ;
            }
            else {
                ROOMS.get(i).setLOCK_B(lock);
                ROOMS.get(i).setLOCK(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getLOCK_B().devId));
                ROOMS.get(i).lock = 1 ;
            }
            if (ROOMS.get(i).RoomNumber == 505) {
                Log.d("emptyRoom",ROOMS.get(i).PowerSwitch+" "+ROOMS.get(i).ZBGateway);
            }
        }
    }

    static void setLocks(ArrayList<LockObj> Locks) {
        if (Locks.size() == 0) {
            Log.d("locksAre",Locks.size()+"");
            Button locksButton = act.findViewById(R.id.button17);
            locksButton.setVisibility(View.GONE);
        }
        else {
            Log.d("locksAre",Locks.size()+"");
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
    }

    public void refresh(View view) {
        refreshSystem();
    }

    public void toggleRoomsDevices(View view) {
        hideSystemUI();
        Button b = (Button) view ;
        if (RoomsRecycler.getVisibility() == View.VISIBLE) {
            RoomsRecycler.setVisibility(View.GONE);
            DevicesRecycler.setVisibility(View.VISIBLE);
            searchText.setVisibility(View.VISIBLE);
            b.setText(getResources().getString(R.string.rooms));
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.ic_baseline_bedroom_child_24,null);
            b.setCompoundDrawablesWithIntrinsicBounds(null,null,d,null);
            Toast.makeText(act,"devices are "+Devices.size(),Toast.LENGTH_LONG).show();
        }
        else if (RoomsRecycler.getVisibility() == View.GONE) {
            RoomsRecycler.setVisibility(View.VISIBLE);
            DevicesRecycler.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
            b.setText(getResources().getString(R.string.devices));
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.ic_baseline_podcasts_24,null);
            b.setCompoundDrawablesWithIntrinsicBounds(null,null,d,null);
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

    private void getScanGatewayCallback(){
    }

    public void initLockGateway(View view) {
        if (TheFoundGateway == null ) {
            Log.d("gatewayLock","null");
        }
        else {
            GatewayClient.getDefault().connectGateway(TheFoundGateway, new ConnectCallback() {
                @Override
                public void onConnectSuccess(ExtendedBluetoothDevice device) {
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
                            isInitSuccess(deviceInfo);
                        }

                        @Override
                        public void onFail(GatewayError error) {
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

    void getScenes() {
        SCENES.clear();
        MyApp.SCENES.clear();
        final int[] ind = {0};
        for (int i=0;i< MyApp.ProjectHomes.size();i++) {
            int finalI = i;
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    getHomeScenes(MyApp.ProjectHomes.get(finalI).Home, new CreteMoodsCallBack() {
                        @Override
                        public void onSuccess(List<SceneBean> moods) {
                            ind[0]++;
                            SCENES.addAll(moods);
                            MyApp.SCENES.addAll(moods);
                            if (ind[0] == MyApp.ProjectHomes.size()) {
                                Log.d("scenesAre","total: "+SCENES.size());
                                for(SceneBean s : SCENES) {
                                    Log.d("scenesAre",s.getName());
                                }
                            }
                        }

                        @Override
                        public void onFail(String error) {

                        }
                    });
                }
            },(long) i * 1000 * 10);

        }
    }

    void getHomeScenes(HomeBean h, CreteMoodsCallBack callBack) {
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
                new MessageDialog("getting tuya scenes failed "+s1,"error",act);
            }
        });
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
                    MyApp.homeBeans.clear();
                    MyApp.ProjectHomes.clear();
                    dialogInterface.dismiss();
                    SharedPreferences.Editor editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
                    editor.putString("projectName" , null);
                    editor.putString("tuyaUser" , null);
                    editor.putString("tuyaPassword" , null);
                    editor.putString("lockUser" , null);
                    editor.putString("lockPassword" , null);
                    editor.apply();
                    Intent x = new Intent(act,Login.class);
                    act.startActivity(x);
                    act.finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .create().show();
    }

    public void goToTemplates(View view) {
        Intent i = new Intent(act, ProjectTemplates.class);
        startActivity(i);
    }

    static void createTuyaHome(String pName, RequestCallbackResult callback) {
        List<String> rooms = new ArrayList<>();
        TuyaHomeSdk.getHomeManagerInstance().createHome(pName, 0, 0, "", rooms, new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                MyApp.ProjectHomes.add(new CheckInHome(bean,null));
                setHomesSpinner();
                callback.onSuccess(bean.getName());
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                callback.onFail(errorCode+" "+errorMsg);
            }
        });
    }

    public void addHome(View view) {
        addNewHomeDialog d = new addNewHomeDialog(act,MyApp.THE_PROJECT.projectName);
        d.show();
    }

    public void deleteHome(View view) {
        if (SelectedHome == null) {
            new MessageDialog("select home first","select home",act);
            return;
        }
        if (SelectedHome.Devices != null && SelectedHome.Devices.size() > 0) {
            new MessageDialog("this home has devices installed ","home has devices",act);
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(act);
        b.setTitle("Delete "+SelectedHome.Home.getName()+ " Home").setMessage("are you sure ..??")
                .setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> TuyaHomeSdk.newHomeInstance(SelectedHome.Home.getHomeId()).dismissHome(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        new MessageDialog(error,"Delete Failed",act);
                    }

                    @Override
                    public void onSuccess() {
                        dialogInterface.dismiss();
                        new MessageDialog("home deleted successfully","Deleted",act);
                        MyApp.homeBeans.remove(SelectedHome.Home);
                        MyApp.ProjectHomes.remove(SelectedHome);
                        setHomesSpinner();
                    }
                })).create().show();

    }

    static void sortDevicesList(List<DeviceBean> devices) {
        for (int i = 0; i < devices.size(); i++) {
            for (int j = 1; j < (devices.size() - i); j++) {
                String numberOnly = getRoomNumberFromDeviceName(devices.get(j - 1));
                String numberOnly0= getRoomNumberFromDeviceName(devices.get(j));
                Log.d("sortDevices",numberOnly+" "+numberOnly0);
                int x = 0 ;
                int y = 0 ;
                try {
                    if (!numberOnly.isEmpty()) {
                        x = Integer.parseInt(numberOnly);
                    }
                    if (!numberOnly0.isEmpty()) {
                        y = Integer.parseInt(numberOnly0);
                    }
                    if (x > y) {
                        Collections.swap(devices, j, j - 1);
                    }
                }
                catch (Exception e) {
                    Log.d("sortDevices",numberOnly+" "+numberOnly0);
                }
            }
        }
    }

    static void sort(List<DeviceBean> devices) {
        int n = devices.size();
        for (int i = 1; i < n; ++i) {
            DeviceBean key = devices.get(i);
            int j = i - 1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
            String numberOnly = getRoomNumberFromDeviceName(key);
            String numberOnly0 = getRoomNumberFromDeviceName(devices.get(j));
            int x = 0 ;
            int y = 0 ;
            try {
                if (!numberOnly.isEmpty()) {
                    x = Integer.parseInt(numberOnly);
                }
                if (!numberOnly0.isEmpty()) {
                    y = Integer.parseInt(numberOnly0);
                }

                while (j >= 0 && y > x) {
                    DeviceBean j1 = devices.get(j+1);
                    j1 = devices.get(j);
                    j = j - 1;
                }
                DeviceBean j1 = devices.get(j+1);
                j1 = key;
            }
            catch (Exception e) {
                Log.d("sortDevices",numberOnly+" "+numberOnly0);
            }


        }
    }

    static void sortSelection(List<DeviceBean> devices) {
        int n = devices.size();

        // One by one move boundary of unsorted sub array
        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            int min_idx = i;

            for (int j = i+1; j < n; j++) {
                String numberOnly = getRoomNumberFromDeviceName(devices.get(min_idx));
                String numberOnly0 = getRoomNumberFromDeviceName(devices.get(j));
                int x = 0 ;
                int y = 0 ;
                try {
                    if (!numberOnly.isEmpty()) {
                        x = Integer.parseInt(numberOnly);
                    }
                    if (!numberOnly0.isEmpty()) {
                        y = Integer.parseInt(numberOnly0);
                    }
                }
                catch (Exception e) {
                    Log.d("sortDevices",numberOnly+" "+numberOnly0);
                }
                if (y < x)
                    min_idx = j;
            }

            // Swap the found minimum element with the first
            // element
            Collections.swap(devices, min_idx, i);
        }
    }

    static String getRoomNumberFromDeviceName(DeviceBean d) {
        String n = "";
        if (d.getName().contains("Z")) {
            n = d.getName().split("Z")[0];
        }
        else if (d.getName().contains("P")) {
            n = d.getName().split("P")[0];
        }
        else if (d.getName().contains("M")) {
            n = d.getName().split("M")[0];
        }
        else if (d.getName().contains("D")) {
            n = d.getName().split("D")[0];
        }
        else if (d.getName().contains("S")) {
            n = d.getName().split("S")[0];
        }
        else if (d.getName().contains("A")) {
            n = d.getName().split("A")[0];
        }
        else if (d.getName().contains("C")) {
            n = d.getName().split("C")[0];
        }
        else if (d.getName().contains("L")) {
            n = d.getName().split("L")[0];
        }
        return n;
    }

    static int partition(List<DeviceBean> devices, int low, int high) {
        // Choosing the pivot
        DeviceBean pivot = devices.get(high);

        // Index of smaller element and indicates
        // the right position of pivot found so far
        int i = (low - 1);

        for (int j = low; j <= high - 1; j++) {

            // If current element is smaller than the pivot
            String numberOnly = getRoomNumberFromDeviceName(pivot);
            String numberOnly0 = getRoomNumberFromDeviceName(devices.get(j));
            int x = 0 ;
            int y = 0 ;
            try {
                if (!numberOnly.isEmpty()) {
                    x = Integer.parseInt(numberOnly);
                }
                if (!numberOnly0.isEmpty()) {
                    y = Integer.parseInt(numberOnly0);
                }
            }
            catch (Exception e) {
                Log.d("sortDevices",numberOnly+" "+numberOnly0);
            }

            if (y < x) {

                // Increment index of smaller element
                i++;
                Collections.swap(devices, i, j);
            }
        }
        Collections.swap(devices, i+1, high);
        return (i + 1);
    }

    static void quickSort(List<DeviceBean> devices, int low, int high) {
        if (low < high) {

            // pi is partitioning index, arr[p]
            // is now at right place
            int pi = partition(devices, low, high);

            // Separately sort elements before
            // partition and after partition
            quickSort(devices, low, pi - 1);
            quickSort(devices, pi + 1, high);
        }
    }
}