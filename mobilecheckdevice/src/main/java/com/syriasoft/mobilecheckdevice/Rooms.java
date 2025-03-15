package com.syriasoft.mobilecheckdevice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;
import com.syriasoft.mobilecheckdevice.Adapters.BuildingAdapter;
import com.syriasoft.mobilecheckdevice.Adapters.FloorAdapter;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;
import com.syriasoft.mobilecheckdevice.Classes.DevicesDataDB;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GerRoomsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetBuildingsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetDevicesCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetFloorsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.getDeviceDataCallback;
import com.syriasoft.mobilecheckdevice.Classes.LocalDataStore;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT_VARIABLES;
import com.syriasoft.mobilecheckdevice.Classes.Property.Building;
import com.syriasoft.mobilecheckdevice.Classes.Property.Floor;
import com.syriasoft.mobilecheckdevice.Classes.Property.PropertyDB;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
import com.syriasoft.mobilecheckdevice.Classes.Tuya;
import com.syriasoft.mobilecheckdevice.Interface.CreteMoodsCallBack;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallbackResult;
import com.syriasoft.mobilecheckdevice.lock.AccountInfo;
import com.syriasoft.mobilecheckdevice.lock.ApiService;
import com.syriasoft.mobilecheckdevice.lock.GatewayObj;
import com.syriasoft.mobilecheckdevice.lock.LockObj;
import com.syriasoft.mobilecheckdevice.lock.MyApplication;
import com.syriasoft.mobilecheckdevice.lock.RetrofitAPIManager;
import com.syriasoft.mobilecheckdevice.lock.ServerError;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.gateway.model.DeviceInfo;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
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
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class Rooms extends AppCompatActivity {
    static RecyclerView DevicesRecycler;
    RecyclerView BuildingsRecycler,FloorsRecycler,RoomsRecycler;
    //static GridView RoomsRecycler;
    static List<ROOM> ROOMS;
    static Activity act ;
    static ArrayList<LockObj> Locks ;
    static AccountInfo accountInfo;
    public static AccountInfo acc;
    static List<DeviceBean> Devices ;
    static List<CheckinDevice> Devices0 ;
    static FirebaseDatabase database ;
    static int checkInModeTime = 0 ;
    static int checkOutModeTime = 0 ;
    LinearLayout btnSLayout ;
    static boolean CHANGE_STATUS = false ;
    static LoadingDialog loading;
    static RequestQueue REQ;
    EditText searchText ;
    ListView gatewaysListView ;
    ExtendedBluetoothDevice TheFoundGateway ;
    static List<SceneBean> SCENES ;
    public static List<String> IMAGES ;
    static DatabaseReference ProjectVariablesRef , DevicesControls , ProjectDevices ,RoomTemplates ;
    public static CheckInHome SelectedHome;
    public static String New_Home_Name ;
    Button locksButton;
    PropertyDB pDB;
    DevicesDataDB db;
    LocalDataStore storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        setActivity();
        setActivityActions();
        setFirebase();
        getProjectData();
        getSceneBGs();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setActivity() {
        act = this ;
        BuildingsRecycler = findViewById(R.id.buildingsRecycler);
        FloorsRecycler = findViewById(R.id.floorsRecycler);
        RoomsRecycler = findViewById(R.id.roomsRecycler);
        searchText = findViewById(R.id.search_text);
        btnSLayout = findViewById(R.id.btnsLayout);
        locksButton = findViewById(R.id.button17);
        DevicesRecycler = findViewById(R.id.DevicesListView);
        gatewaysListView = findViewById(R.id.scanLockGatewayList);
        TextView hotelName = findViewById(R.id.hotelName);
        hotelName.setText(MyApp.My_PROJECT.projectName);
        ROOMS = new ArrayList<>();
        Locks = new ArrayList<>();
        Devices = new ArrayList<>();
        SCENES = new ArrayList<>();
        REQ = Volley.newRequestQueue(act);
        searchText.setVisibility(View.GONE);
        RoomsRecycler.setVisibility(View.VISIBLE);
        DevicesRecycler.setVisibility(View.GONE);
        GridLayoutManager managerD = new GridLayoutManager(act,8);
        DevicesRecycler.setLayoutManager(managerD);

        pDB = new PropertyDB(act);
        db = new DevicesDataDB(act);
        storage = new LocalDataStore();
    }

    void setActivityActions() {
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
                    if (!Results.isEmpty()) {
                        Device_Adapter adapter = new Device_Adapter(Results);
                        DevicesRecycler.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(act,"no results",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void setFirebase() {
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/"); //  https://hotelservices-ebe66.firebaseio.com/
        ProjectVariablesRef = database.getReference(MyApp.My_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.My_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.My_PROJECT.projectName+"Devices");
        RoomTemplates = database.getReference(MyApp.My_PROJECT.projectName+"Templates");
    }

    public void getData() {
        loading = new LoadingDialog(act);
//        getProjectVariables(new RequestCallback() {
//            @Override
//            public void onSuccess() {
//                getRooms(new RequestCallback() {
//                    @Override
//                    public void onSuccess() {
//                        ROOM.sortRoomsByNumber(ROOMS);
//                        //MyApp.ROOMS = ROOMS ;
//                        TextView hotelName = act.findViewById(R.id.hotelName);
//                        hotelName.setText(MessageFormat.format("{0} {1} room", MyApp.My_PROJECT.projectName, ROOMS.size()));
//                        loginTTLock();
//                        getTuyaDevices(new RequestCallback() {
//                            @Override
//                            public void onSuccess() {
//                                if (Devices.isEmpty()) {
//                                    Toast.makeText(act,"no devices",Toast.LENGTH_LONG).show();
//                                }
//                                else {
//                                    Toast.makeText(act,"Devices are: "+Devices.size(),Toast.LENGTH_LONG).show();
//                                    setRoomsDevices();
//                                }
//                                Rooms_Adapter_Base Adapter = new Rooms_Adapter_Base(ROOMS,act);
//                                RoomsRecycler.setAdapter(Adapter);
//                                quickSort(Devices,0,Devices.size()-1);
//                                Device_Adapter adapter = new Device_Adapter(Devices);
//                                DevicesRecycler.setAdapter(adapter);
//                                setHomesSpinner();
//                                loading.stop();
//                            }
//
//                            @Override
//                            public void onFail(String error) {
//                                loading.stop();
//                                AlertDialog.Builder b = new AlertDialog.Builder(act);
//                                b.setTitle("Getting Devices Failed").setMessage(error)
//                                        .setPositiveButton("yes", (dialogInterface, i) -> {
//                                            dialogInterface.dismiss();
//                                            getData();
//                                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
//                            }
//                        }) ;
//                    }
//
//                    @Override
//                    public void onFail(String error) {
//                        loading.stop();
//                        AlertDialog.Builder b = new AlertDialog.Builder(act);
//                        b.setTitle("Getting Rooms Failed").setMessage(error)
//                                .setPositiveButton("yes", (dialogInterface, i) -> {
//                                    dialogInterface.dismiss();
//                                    getData();
//                                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
//                    }
//                });
//            }
//
//            @Override
//            public void onFail(String error) {
//                loading.stop();
//                AlertDialog.Builder b = new AlertDialog.Builder(act);
//                b.setTitle("Project Variables Getting Failed").setMessage(error)
//                        .setPositiveButton("yes", (dialogInterface, i) -> {
//                            dialogInterface.dismiss();
//                            getData();
//                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
//            }
//        });
    }

    void getProjectData() {
        loading = new LoadingDialog(act);
        PROJECT_VARIABLES.getProjectVariables(MyApp.My_PROJECT, REQ, new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootingUp","project variables done");
                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
                if (!PROJECT_VARIABLES.getIsProjectVariablesSaved(storage)) {
                    PROJECT_VARIABLES.saveProjectVariablesToStorage(storage);
                }
                Building.getBuildings(REQ, pDB, new GetBuildingsCallback() {
                    @Override
                    public void onSuccess(List<Building> buildings) {
                        Log.d("bootingUp","buildings done "+buildings.size());
                        MyApp.Buildings = buildings;
                        MyApp.My_PROJECT.buildings = MyApp.Buildings;
                        if (!pDB.isBuildingsInserted()) {
                            PropertyDB.insertAllBuildings(MyApp.Buildings,pDB);
                        }
                        showBuildings();
                        Floor.getFloors(REQ, pDB, new GetFloorsCallback() {
                            @Override
                            public void onSuccess(List<Floor> floors) {
                                Log.d("bootingUp","floors done "+floors.size());
                                MyApp.Floors = floors;
                                if (!pDB.isFloorsInserted()) {
                                    PropertyDB.insertAllFloors(MyApp.Floors,pDB);
                                }
                                Room.getAllRooms(REQ, new GerRoomsCallback() {
                                    @Override
                                    public void onSuccess(List<Room> rooms) {
                                        Log.d("bootingUp","rooms done "+rooms.size());
                                        MyApp.ROOMS = rooms;
                                        Room.sortRoomsByNumber(MyApp.ROOMS);
                                        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
                                        Room.setRoomsFireRooms(MyApp.ROOMS,database);
                                        if (!pDB.isRoomsInserted()) {
                                            PropertyDB.insertAllRooms(MyApp.ROOMS,pDB);
                                        }
                                        showFloors();
                                        Tuya.loginTuya(MyApp.My_PROJECT, new ILoginCallback() {
                                            @Override
                                            public void onSuccess(User user) {
                                                Log.d("bootingUp","tuya login done");
                                                MyApp.TuyaUser = user;
                                                Tuya.getProjectHomes(MyApp.My_PROJECT, storage, new ITuyaGetHomeListCallback() {
                                                    @Override
                                                    public void onSuccess(List<HomeBean> homeBeans) {
                                                        Log.d("bootingUp","tuya homes done "+homeBeans.size());
                                                        MyApp.PROJECT_HOMES0 = homeBeans;
                                                        if (Tuya.getHomesFromStorage(storage).isEmpty()) {
                                                            Tuya.saveHomesToStorage(storage,homeBeans);
                                                        }
                                                        Tuya.getDevicesNoTimers(MyApp.PROJECT_HOMES0, MyApp.ROOMS, new GetDevicesCallback() {
                                                            @Override
                                                            public void devices(List<CheckinDevice> devices) {
                                                                Log.d("bootingUp","tuya devices done "+devices.size());
                                                                Devices0 = devices;
                                                                Tuya.gettingInitialDevicesData(Devices0, db, new getDeviceDataCallback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("bootingUp","finish");
                                                                        settingInitialDevicesData(Devices0);
                                                                        loading.stop();
                                                                    }

                                                                    @Override
                                                                    public void onError(String error) {
                                                                        loading.stop();
                                                                        new MessageDialog(error,"error",act);
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onError(String error) {
                                                                loading.stop();
                                                                new MessageDialog(error,"error",act);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(String errorCode, String error) {
                                                        loading.stop();
                                                        new MessageDialog(error,"error",act);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String code, String error) {
                                                loading.stop();
                                                new MessageDialog(error,"error",act);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        loading.stop();
                                        new MessageDialog(error,"error",act);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                loading.stop();
                                new MessageDialog(error,"error",act);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        loading.stop();
                        new MessageDialog(error,"error",act);
                    }
                });
            }

            @Override
            public void onFail(String error) {
                loading.stop();
                new MessageDialog(error,"error",act);
            }
        });
    }

    void showBuildings() {
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        BuildingsRecycler.setLayoutManager(manager);
        BuildingAdapter adapter = new BuildingAdapter(MyApp.My_PROJECT.buildings);
        BuildingsRecycler.setAdapter(adapter);
    }
    void showFloors() {
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        FloorsRecycler.setLayoutManager(manager);
        FloorAdapter adapter = new FloorAdapter(MyApp.Floors);
        FloorsRecycler.setAdapter(adapter);
    }

    void settingInitialDevicesData(List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            cd.setInitialCurrentValues();
        }
    }

    static void setHomesSpinner() {
        Spinner homes = act.findViewById(R.id.spinner3);
        String[] hs = new String[MyApp.PROJECT_HOMES.size()];
        for (int i=0;i<MyApp.PROJECT_HOMES.size();i++) {
            hs[i] = MyApp.PROJECT_HOMES.get(i).Home.getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,hs);
        homes.setAdapter(adapter);
        homes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedHome = MyApp.PROJECT_HOMES.get(i);
                TextView devicesCount = act.findViewById(R.id.textView30);
                if (MyApp.PROJECT_HOMES.get(i).Devices == null) {
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
                //RoomsRecycler.setAdapter(Adapter);
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

    private void loginTTLock() {
        if (MyApp.My_PROJECT.LockUser.equals("no")) {
            Log.d("locksAre","no bluetooth locks");
            Button locksButton = act.findViewById(R.id.button17);
            locksButton.setVisibility(View.GONE);
            return;
        }
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String pass = MyApp.My_PROJECT.LockPassword;
        pass = DigitUtil.getMD5(pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", MyApp.My_PROJECT.LockUser, pass, ApiService.REDIRECT_URI);
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

    private void getLocks() {
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
                            Log.d("locksNum" , Objects.requireNonNull(e.getMessage()));
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
        for (int i=0; i< MyApp.PROJECT_HOMES.size();i++) {
            Log.d("getDevicesRun","number "+i);
            int finalI = i;
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("getDevicesRun","number "+ finalI +" run "+MyApp.PROJECT_HOMES.get(finalI).Home.getName());
                    TuyaHomeSdk.newHomeInstance(MyApp.PROJECT_HOMES.get(finalI).Home.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean homeBean) {
                            x[0]++;
                            Log.d("gettingDevices","result "+x[0]+" "+homeBean.getDeviceList().size()+" "+MyApp.PROJECT_HOMES.get(finalI).Home.getName());
                            MyApp.PROJECT_HOMES.get(finalI).Devices = homeBean.getDeviceList();
                            for (DeviceBean d : homeBean.getDeviceList()) {
                                if (MyApp.searchDeviceInList(Devices,d.devId) == null) {
                                    Devices.add(d);
                                    ROOM r = getRoomFromDeviceName(d,ROOMS);
                                    if (r != null) {
                                        r.Home = MyApp.PROJECT_HOMES.get(finalI).Home;
                                    }
                                }
                            }
                            if (x[0] == MyApp.PROJECT_HOMES.size()) {
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
            }, (long) i * 1000);
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
            DeviceBean Shutter1 = searchRoomDevice(Devices,ROOMS.get(i),"Shutter1");
            if (Shutter1 != null) {
                ROOMS.get(i).setSHUTTER1(Shutter1);
            }
            DeviceBean Shutter2 = searchRoomDevice(Devices,ROOMS.get(i),"Shutter2");
            if (Shutter2 != null) {
                ROOMS.get(i).setSHUTTER2(Shutter2);
            }
            DeviceBean Shutter3 = searchRoomDevice(Devices,ROOMS.get(i),"Shutter3");
            if (Shutter3 != null) {
                ROOMS.get(i).setSHUTTER3(Shutter3);
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

    void setLocks(ArrayList<LockObj> Locks) {
        if (Locks.isEmpty()) {
            Log.d("locksAre",Locks.size()+"");
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
        for (int i=0;i< MyApp.PROJECT_HOMES.size();i++) {
            int finalI = i;
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    getHomeScenes(MyApp.PROJECT_HOMES.get(finalI).Home, new CreteMoodsCallBack() {
                        @Override
                        public void onSuccess(List<SceneBean> moods) {
                            ind[0]++;
                            SCENES.addAll(moods);
                            MyApp.SCENES.addAll(moods);
                            if (ind[0] == MyApp.PROJECT_HOMES.size()) {
                                Log.d("scenesAre","total: "+SCENES.size());
                                for(SceneBean s : SCENES) {
                                    Log.d("scenesAre",s.getName());
//                                    if (s.getName().equals("إغلاق ستارة") || s.getName().equals("فتح ستارة")) {
//                                        Log.d("scenesAre",s.getName());
//                                        TuyaHomeSdk.newSceneInstance(s.getId()).deleteScene(new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//                                                Log.d("scenesAre",error);
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("scenesAre","deleted");
//                                            }
//                                        });
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onFail(String error) {

                        }
                    });
                }
            },(long) i * 1000);

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
                    MyApp.PROJECT_HOMES.clear();
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
                MyApp.PROJECT_HOMES.add(new CheckInHome(bean,null));
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
        addNewHomeDialog d = new addNewHomeDialog(act,MyApp.My_PROJECT.projectName);
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
                        MyApp.PROJECT_HOMES.remove(SelectedHome);
                        setHomesSpinner();
                    }
                })).create().show();

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

    static ROOM getRoomFromDeviceName(DeviceBean d,List<ROOM> rooms) {
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
        for (ROOM r:rooms) {
            if (String.valueOf(r.RoomNumber).equals(n)) {
                return r;
            }
        }
        return null;
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