package com.syriasoft.housekeeping;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.syriasoft.housekeeping.Adapters.OrdersAdapter;
import com.syriasoft.housekeeping.Adapters.OrdersGridAdapter;
import com.syriasoft.housekeeping.Interface.HomeBeanCallBack;
import com.syriasoft.housekeeping.Interface.RequestCallback;
import com.syriasoft.housekeeping.TTLOCK.AccountInfo;
import com.syriasoft.housekeeping.TTLOCK.ApiService;
import com.syriasoft.housekeeping.TTLOCK.LockObj;
import com.syriasoft.housekeeping.TTLOCK.RetrofitAPIManager;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
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

public class MainActivity extends AppCompatActivity {
    private Activity act;
    private static String getRoomsUrl;
    static boolean activityStatus = false;
    private static FirebaseDatabase database;
    private static RecyclerView DNDRecycler,OrdersRecycler;
    private static ValueEventListener[] CleanupListener;
    private static ValueEventListener[] LaundryListener;
    private static ValueEventListener[] RoomServiceListener;
    private static ValueEventListener[] DNDListener;
    private static ValueEventListener[] SOSListener;
    private static ValueEventListener[] MiniBarCheck;
    private static String DEP = "";
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor ;
    public static final String SHARED_PREF_NAME = "MyPref";
    public final static String KEY_MyRooms = "MyRooms";
    private static AccountInfo accountInfo;
    static AccountInfo acc;
    static String password;
    static List<LockObj> lockObjects = new ArrayList<>();
    static List<DeviceBean> Devices;
    static List<ROOM> Rooms;
    static List<cleanOrder> list ;
    List<cleanOrder> dndList;
    OrdersDB orderDB;
    DNDDB dndDB;
    static public DatabaseReference MyFireUser, myRoomsReference,DevicesRef;
    public static List<String> CurrentRoomsStatus;
    public static RequestQueue Q ;
    LoadingDialog loading;
    LinearLayout HOME,ROOMS_BTN;
    OrdersAdapter ADAPTER;
    DND_Adapter dnd_adapter;
    ImageButton ViewButton;
    LinearLayoutManager ordersManager;
    GridLayoutManager ordersGridManager;
    boolean isLists = true;
    Gson g;



    //--------------------------------------------------------
    //Activity Methods
    @Override
    protected void onStart() {
        super.onStart();
        activityStatus = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStatus = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivity();
        getData();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    void setActivity() {
        defineLists();
        defineViews();
        setFirebase();
        setSharedPreferences();
        DEP = MyApp.My_USER.department;
    }

    void defineViews() {
        act = this;
        MyApp.actList.add(act);
        ViewButton = findViewById(R.id.imageButton3);
        TextView mainText = findViewById(R.id.mainText);
        mainText.setText(String.format("%s Orders", MyApp.My_USER.department));
        DNDRecycler = findViewById(R.id.dnd_recycler);
        OrdersRecycler = findViewById(R.id.orders_recycler);
        ordersManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        ordersGridManager = new GridLayoutManager(act,4);
        OrdersRecycler.setLayoutManager(ordersManager);
        ADAPTER = new OrdersAdapter(list);
        OrdersRecycler.setAdapter(ADAPTER);
        Q = Volley.newRequestQueue(act);
        orderDB = new OrdersDB(act);
        dndDB = new DNDDB(act);
        orderDB.removeAll();
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        DNDRecycler.setLayoutManager(manager);
        dnd_adapter = new DND_Adapter(dndList);
        DNDRecycler.setAdapter(dnd_adapter);
        HOME = findViewById(R.id.homeLayout);
        ROOMS_BTN = findViewById(R.id.roomsLayout);
        ROOMS_BTN.setOnClickListener(v -> {
            Intent i = new Intent(act,ROOMS.class);
            startActivity(i);
        });
        ViewButton.setOnClickListener(v -> {
            if (OrdersRecycler.getLayoutManager() == ordersManager) {
                OrdersRecycler.setLayoutManager(ordersGridManager);
                ADAPTER = new OrdersGridAdapter(list);
                OrdersRecycler.setAdapter(ADAPTER);
                ViewButton.setImageResource(R.drawable.grid_icon);
                isLists = false;
            }
            else {
                OrdersRecycler.setLayoutManager(ordersManager);
                ADAPTER = new OrdersAdapter(list);
                OrdersRecycler.setAdapter(ADAPTER);
                ViewButton.setImageResource(R.drawable.list_icon);
                isLists = true;
            }
        });
        g = new Gson();
    }

    void defineLists() {
        Rooms = new ArrayList<>();
        list = new ArrayList<>();
        dndList = new ArrayList<>();
        Devices = new ArrayList<>();
        CurrentRoomsStatus = new ArrayList<>();
    }

    void setFirebase() {
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/");
        MyFireUser = database.getReference(MyApp.MyProject.projectName + "ServiceUsers/" + MyApp.My_USER.jobNumber);
        myRoomsReference = MyFireUser.child("control");
        DevicesRef = database.getReference(MyApp.MyProject.projectName+"Devices");
        MyFireUser.setValue(MyApp.My_USER);
        Timer t = new Timer() ;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(act, task1 -> {
                    try {
                        if (task1.getResult() != null) {
                            String token = task1.getResult();
                            MyFireUser.child("token").setValue(token);
                            sendRegistrationToServer(token, String.valueOf(MyApp.My_USER.id));
                        }
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"No Google Services On Your Device",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        t.scheduleAtFixedRate(task,0,1000*60*15);
    }

    void setSharedPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.getItem(0).setTitle(MyApp.My_USER.name);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button2:
                Button x = findViewById(R.id.button2);
                sgnOut(x);
                break;
            case R.id.goToRooms:
                Intent i = new Intent(act, ROOMS.class);
                startActivity(i);
                break;
            case R.id.changePassword:
                changePasswordDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // getting data functions
    // ___________________________________________________________________________________________
    // yes
    void getData() {
        Log.d("bootUp","start");
        loading = new LoadingDialog(act);
        loading.show();
        getProjectVariables(new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootUp","project variables success");
                setMyRoomsListener(new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("bootUp","rooms listener success");
                        getRooms(new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("bootUp","rooms success");
                                CleanupListener = new ValueEventListener[Rooms.size()];
                                LaundryListener = new ValueEventListener[Rooms.size()];
                                RoomServiceListener = new ValueEventListener[Rooms.size()];
                                DNDListener = new ValueEventListener[Rooms.size()];
                                SOSListener = new ValueEventListener[Rooms.size()];
                                MiniBarCheck = new ValueEventListener[Rooms.size()];
                                setRoomsListeners();
                                filterOrderByRoomNumber(list, Rooms);
                                filterDNDOrderByRoomNumber(dndList, Rooms);
                                filterOrdersByDepartment(list);
//                                loginToTTLock(MyApp.MyProject.LockUser,MyApp.MyProject.LockPassword, new RequestCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        getTTLockLocks(new RequestCallback() {
//                                            @Override
//                                            public void onSuccess() {
//                                            }
//
//                                            @Override
//                                            public void onFail(String error) {
//                                                new messageDialog(error,"Get Locks Failed",act);
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onFail(String error) {
//                                        new messageDialog(error,"Lock Login Failed",act);
//                                    }
//                                }); // lock
                                goLogInToTuya(MyApp.MyProject.TuyaUser,MyApp.MyProject.TuyaPassword, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("bootUp","t login success");
                                        getTuyaHomes(new RequestCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("bootUp","homes success");
                                                getTuyaDevices(new RequestCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.d("bootUp","devices success");
                                                        setRoomsDevices();
                                                        loading.close();
                                                    }

                                                    @Override
                                                    public void onFail(String error) {
                                                        Log.d("bootUp","devices failed: "+error);
                                                        loading.close();
                                                        createRestartGettingDataConfirmationDialog(error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFail(String error) {
                                                Log.d("bootUp","homes failed: "+error);
                                                loading.close();
                                                createRestartGettingDataConfirmationDialog(error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        Log.d("bootUp","t login failed: "+error);
                                        loading.close();
                                        createRestartGettingDataConfirmationDialog(error);
                                    }
                                }); // tuya
                                if (ROOMS.act != null) {
                                    ROOMS.setTheRooms();
                                }
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("bootUp","get rooms failed: "+error);
                                loading.close();
                                createRestartGettingDataConfirmationDialog(error);
                            }
                        });
                    }

                    @Override
                    public void onFail(String error) {
                        Log.d("bootUp","rooms listener failed: "+error);
                        loading.close();
                        if (error.equals("0")) {
                            createRestartGettingDataConfirmationDialog("no rooms specified for you . please refer to reception");
                        }
                        else {
                            createRestartGettingDataConfirmationDialog(error);
                        }
                    }
                });
            }

            @Override
            public void onFail(String error) {
                Log.d("bootUp","project variables failed: "+error);
                loading.close();
                createRestartGettingDataConfirmationDialog(error);
            }
        });
    }

    // yes
    void getProjectVariables(RequestCallback callback) {
        String url = MyApp.MyProject.url + "roomsManagement/getProjectVariables" ;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject row = new JSONObject(response);
                JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                MyApp.ProjectVariables = new ProjectsVariablesClass(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                callback.onSuccess();
            }
            catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> callback.onFail(error.toString()));
        Q.add(request);
    }

    // yes
    void setMyRoomsListener(RequestCallback callback) {
        myRoomsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    callback.onSuccess();
                } else {
                    callback.onFail("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    // yes
    void getRooms(RequestCallback callback) {
        getRoomsUrl = MyApp.MyProject.url + "users/getUserRooms";
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, response -> {
            if (!response.equals("0")) {
                try {
                    JSONArray arr = new JSONArray(response);
                    removeOldListeners();
                    Rooms.clear();
                    list.clear();
                    dndList.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject row = arr.getJSONObject(i);
                        ROOM room = new ROOM(row.getInt("id"), row.getInt("RoomNumber"), row.getInt("hotel"), row.getInt("Building"), row.getInt("building_id"), row.getInt("Floor"), row.getInt("floor_id"), row.getString("RoomType"), row.getInt("SuiteStatus"), row.getInt("SuiteNumber"), row.getInt("SuiteId"), row.getInt("ReservationNumber"), row.getInt("roomStatus"), row.getInt("Tablet"), row.getString("dep"), row.getInt("Cleanup"), row.getInt("Laundry"), row.getInt("RoomService"), row.getInt("Checkout"), row.getInt("Restaurant"), row.getInt("SOS"), row.getInt("DND"), row.getInt("PowerSwitch"), row.getInt("DoorSensor"), row.getInt("MotionSensor"), row.getInt("Thermostat"), row.getInt("ZBGateway"), row.getInt("CurtainSwitch"), row.getInt("ServiceSwitch"), row.getInt("lock"), row.getInt("Switch1"), row.getInt("Switch2"), row.getInt("Switch3"), row.getInt("Switch4"), row.getString("LockGateway"), row.getString("LockName"), row.getInt("powerStatus"), row.getInt("curtainStatus"), row.getInt("doorStatus"), row.getInt("temp"), row.getString("token"),row.getInt("guestIs"));
                        room.setFireRoom(database.getReference(MyApp.MyProject.projectName + "/B" + room.Building + "/F" + room.Floor + "/R" + room.RoomNumber));
                        Rooms.add(room);
                    }
                    sortRoomsByNumber(Rooms);
                    MyApp.Rooms = Rooms;
                    saveMyRoomsInSharedPreferences(Rooms);
                    callback.onSuccess();
                } catch (JSONException e) {
                    callback.onFail(e.getMessage());
                }
            } else {
                callback.onFail("failed to get my rooms");
            }
        }, error -> callback.onFail(error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> par = new HashMap<>();
                par.put("id", String.valueOf(MyApp.My_USER.id));
                return par;
            }
        };
        Q.add(re);
    }

    // yes
    public static void goLogInToTuya(String TU, String TP , RequestCallback callback) {
        TuyaHomeSdk.getUserInstance().loginWithEmail("966", TU, TP, new ILoginCallback() {
            @Override
            public void onSuccess(com.tuya.smart.android.user.bean.User user) {
                callback.onSuccess();
            }

            @Override
            public void onError(String code, String error) {
                callback.onFail(code+" "+error);
            }
        });
    }

    // yes
    void getTuyaHomes(RequestCallback callback) {
        Log.d("getHomes","start: ");
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onError(String errorCode, String error) {
                callback.onFail(errorCode + " " + error);
            }
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                for (HomeBean h : homeBeans) {
                    if (MyApp.MyProject.projectName.equals("apiTest")) {
                        if (h.getName().equals("Test") || h.getName().contains("apiTest")) {
                            MyApp.ProjectHomes.add(h);
                        }
                    }
                    else if (h.getName().contains(MyApp.MyProject.projectName)) {
                        MyApp.ProjectHomes.add(h);
                    }
                }
                Log.d("getHomes","Done: "+MyApp.ProjectHomes.size());
                callback.onSuccess();
            }
        });
    }

    // yes
    static void getTuyaDevices(RequestCallback callback) {
        Log.d("getDevices","start: ");
        Devices.clear();
        getAllHomesDevices(new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("getDevices","Done: "+ Devices.size());
                callback.onSuccess();
            }

            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }

//    static void getTuyaDevicesFromSharedPreferences(RequestCallback callback) {
//        Devices.clear();
//        try {
//            Gson g = new Gson();
//            for (ROOM r : MyApp.Rooms) {
//                String powerString = sharedPreferences.getString(r.RoomNumber + "Power", null);
//                if (powerString != null) {
//                    DeviceBean power = g.fromJson(powerString, DeviceBean.class);
//                    Devices.add(power);
//                    r.setPOWER(power);
//                    r.setPower(TuyaHomeSdk.newDeviceInstance(power.devId));
//                }
//                String lockString = sharedPreferences.getString(r.RoomNumber + "Lock", null);
//                if (lockString != null) {
//                    DeviceBean Lock = g.fromJson(lockString, DeviceBean.class);
//                    Devices.add(Lock);
//                    r.setLOCK_T(Lock);
//                    r.setLock_T(TuyaHomeSdk.newDeviceInstance(Lock.devId));
//                }
//            }
//            callback.onSuccess();
//        }
//        catch (Exception e) {
//            callback.onFail(e.getMessage());
//        }
//    }
//
//    static void removeTuyaDevicesFromSharedPreferences(RequestCallback callback) {
//        try {
//            for (ROOM r : MyApp.Rooms) {
//                String powerString = sharedPreferences.getString(r.RoomNumber + "Power", null);
//                if (powerString != null) {
//                    editor.remove(r.RoomNumber + "Power").commit();
//                }
//                String lockString = sharedPreferences.getString(r.RoomNumber + "Lock", null);
//                if (lockString != null) {
//                    editor.remove(r.RoomNumber + "Lock").commit();
//                }
//            }
//            callback.onSuccess();
//        }
//        catch (Exception e) {
//            callback.onFail(e.getMessage());
//        }
//    }

    static void getHomeDevices(int index, HomeBeanCallBack callBack) {
        HomeBean h = MyApp.ProjectHomes.get(index);
        Log.d("getDevices","home: "+index+" "+h.getName());
        TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                callBack.onSuccess(bean);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                callBack.onFail(errorCode+" "+errorMsg);
            }
        });
    }

    static void getAllHomesDevices(RequestCallback callback) {
        final int[] index = {0};
        getHomeDevices(index[0], new HomeBeanCallBack() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                for (DeviceBean d : homeBean.getDeviceList()) {
                    if (MyApp.searchDeviceInList(Devices,d.devId) == null) {
                        Devices.add(d);
                    }
                }
                Log.d("getDevices","home: "+index[0]+" devices "+Devices.size());
                if (index[0] +1 < MyApp.ProjectHomes.size()) {
                    Log.d("getDevices","not finish");
                    index[0]++;
                    getHomeDevices(index[0],this);
                }
                if (index[0] +1 == MyApp.ProjectHomes.size()) {
                    Log.d("getDevices","finish");
                    callback.onSuccess();
                }
            }

            @Override
            public void onFail(String error) {
                Log.d("getDevices",error);
                callback.onFail(error);
            }
        });
    }

    // yes
    void setRoomsDevices() {
        for (DeviceBean d : Devices) {
            Log.d("tuyaHome", d.name);
            for (ROOM r : Rooms) {
                if (d.getName().equals(r.RoomNumber + "Power")) {
                    r.setPOWER(d);
                    r.setPower(TuyaHomeSdk.newDeviceInstance(r.getPOWER().devId));
                }
                if (d.getName().equals(r.RoomNumber + "Lock")) {
                    Log.d("tuyaHome", "lock selected");
                    r.setLOCK_T(d);
                    r.setLock_T(TuyaHomeSdk.newDeviceInstance(r.getLOCK_T().devId));
                }
            }
        }
    }

    // yes
    public static void loginToTTLock(String LU, String LP,RequestCallback callback) {
        if (MyApp.MyProject.LockUser.equals("no")) {
            Log.d("locksAre","no bluetooth locks");
            return;
        }
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String account = LU.trim();
        password = LP.trim();
        password = DigitUtil.getMD5(password);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", account, password, ApiService.REDIRECT_URI);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
//                String json = response.body();
//                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
//                if (accountInfo != null) {
//                    if (accountInfo.errcode == 0) {
//                        accountInfo.setMd5Pwd(password);
//                        acc = accountInfo;
//                        callback.onSuccess();
//                    }
//                    else {
//                        callback.onFail(String.valueOf(accountInfo.errcode));
//                    }
//                }
//                else {
//                    callback.onFail("lock account null");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                callback.onFail(t.getMessage());
//            }
//        });
    }

    // yes
    public static void getTTLockLocks(RequestCallback callback) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID, acc.getAccess_token(), 1, 100, System.currentTimeMillis());
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
//                String json = response.body();
//                if (Objects.requireNonNull(json).contains("list")) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(json);
//                        JSONArray array = jsonObject.getJSONArray("list");
//                        lockObjects = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>() {
//                        });
//                        for (LockObj o : lockObjects) {
//                            for (ROOM r : Rooms) {
//                                if (o.getLockAlias().equals(r.RoomNumber + "Lock")) {
//                                    r.setLOCK(o);
//                                }
//                            }
//                        }
//                        callback.onSuccess();
//                    } catch (JSONException e) {
//                        callback.onFail(e.getMessage());
//                    }
//                }
//                else {
//                    callback.onFail("no list of locks");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                callback.onFail(t.getMessage());
//            }
//        });

    }

    // ___________________________________________________________________________________________

    // yes
    void sendRegistrationToServer(String token,String id) {
        String url = MyApp.MyProject.url + "users/modifyUserFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, response -> Log.d("TokenResp", response), error -> Log.d("TokenResp", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("id", id);
                return params;
            }
        };
        Q.add(r);
    }

    // yes
    public void sgnOut(View view) {
        loading.show();
        removeOldListeners();
        deleteToken(String.valueOf(MyApp.My_USER.id),new VolleyCallback() {
            @Override
            public void onSuccess(String res) {
                try {
                    loading.close();
                    JSONObject result = new JSONObject(res);
                    if (result.getString("result").equals("success")) {
                        saveUserDataToSharedPreferences(null);
                        list.clear();
                        dndList.clear();
                        for (int i = 0; i < MyApp.actList.size(); i++) {
                            MyApp.actList.get(i).finish();
                        }
                        MyFireUser.child("token").setValue("");
                        Intent i = new Intent(act, LogIn.class);
                        startActivity(i);
                    }
                    else {
                        loading.close();
                        new messageDialog(result.getString("error"),"failed",act);
                    }
                } catch (JSONException e) {
                    loading.close();
                    new messageDialog(e.getMessage(),"failed",act);
                }
            }

            @Override
            public void onFailed(String error) {
                loading.close();
                new messageDialog(error,"failed",act);
            }
        });
    }

    // yes
    public static void sortRoomsByNumber(List<ROOM> room) {
        for (int i = 0; i < room.size(); i++) {
            for (int j = 1; j < (room.size() - i); j++) {
                if (room.get(j - 1).RoomNumber > room.get(j).RoomNumber) {
                    Collections.swap(room, j, j - 1);
                }
            }
        }
    }

    void setInitialCleanupRoomsListener() {
        int[] x = {0};
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            Rooms.get(i).getFireRoom().child("Cleanup").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    x[0]++;
                    if (snapshot.getValue() != null) {
                        if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(snapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Cleanup");
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"Cleanup","",timeE,Rooms.get(finalI)));
                                Log.d("addOrder","cleanup "+Rooms.get(finalI).RoomNumber);
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Cleanup");
                            if (index != -1) {
                                list.remove(index);
                            }
                        }
                    }
                    if (x[0] == Rooms.size()) {
                        Log.d("ordersFinish",list.size()+" cleanup finish");
                        sortList(list);
                        ADAPTER = new OrdersAdapter(list);
                        OrdersRecycler.setAdapter(ADAPTER);
                        setCleanupRoomsListener();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void setInitialLaundryRoomsListener() {
        int[] x = {0};
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            Rooms.get(i).getFireRoom().child("Laundry").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    x[0]++;
                    if (snapshot.getValue() != null) {
                        if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(snapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Laundry");
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"Laundry","",timeE,Rooms.get(finalI)));
                                Log.d("addOrder","laundry "+Rooms.get(finalI).RoomNumber);
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Laundry");
                            if (index != -1) {
                                list.remove(index);
                            }
                        }
                    }
                    if (x[0] == Rooms.size()) {
                        Log.d("ordersFinish",list.size()+" laundry finish");
                        sortList(list);
                        ADAPTER = new OrdersAdapter(list);
                        OrdersRecycler.setAdapter(ADAPTER);
                        setLaundryRoomsListener();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void setInitialRoomServiceRoomsListener() {
        int[] x = {0};
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            Rooms.get(i).getFireRoom().child("RoomService").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    x[0]++;
                    if (snapshot.getValue() != null) {
                        long value = Long.parseLong(snapshot.getValue().toString());
                        int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"RoomService");
                        if (value == 0) {
                            if (index != -1) {
                                list.remove(index);
                            }
                        }
                        else {
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"RoomService","", value,Rooms.get(finalI)));
                            }
                        }
                    }
                    if (x[0] == Rooms.size()) {
                        Log.d("ordersFinish",list.size()+" roomService finish "+x[0] +" "+ Rooms.size());
                        sortList(list);
                        ADAPTER = new OrdersAdapter(list);
                        OrdersRecycler.setAdapter(ADAPTER);
                        for (int i=0;i<list.size();i++) {
                            cleanOrder or = list.get(i);
                            if (or.dep.equals("RoomService")) {
                                int finalI1 = i;
//                                or.room.getRoomServiceText(new RoomServiceCallback() {
//                                    @Override
//                                    public void onSuccess(String res) {
//                                        or.roomServiceText = res;
//                                        ADAPTER.notifyItemChanged(finalI1);
//                                    }
//
//                                    @Override
//                                    public void onFail(String error) {
//
//                                    }
//                                });
                            }
                        }
                        setRoomServiceRoomsListener();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void setInitialSOSRoomsListener() {
        int[] x = {0};
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            Rooms.get(i).getFireRoom().child("SOS").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    x[0]++;
                    if (snapshot.getValue() != null) {
                        if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(snapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"SOS");
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"SOS","",timeE,Rooms.get(finalI)));
                                Log.d("addOrder","SOS "+Rooms.get(finalI).RoomNumber);
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"SOS");
                            if (index != -1) {
                                list.remove(index);
                            }
                        }
                    }
                    if (x[0] == Rooms.size()) {
                        Log.d("ordersFinish",list.size()+" sos finish");
                        sortList(list);
                        ADAPTER = new OrdersAdapter(list);
                        OrdersRecycler.setAdapter(ADAPTER);
                        setSOSRoomsListener();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void setInitialDNDRoomsListener() {
        int[] x = {0};
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            Rooms.get(i).getFireRoom().child("DND").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    x[0]++;
                    if (snapshot.getValue() != null) {
                        if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(snapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(dndList,Rooms.get(finalI).RoomNumber,"DND");
                            if (index == -1) {
                                dndList.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(dndList.size()+1),"DND","",timeE,Rooms.get(finalI)));
                                Log.d("addOrder","DND "+Rooms.get(finalI).RoomNumber);
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(dndList,Rooms.get(finalI).RoomNumber,"DND");
                            if (index != -1) {
                                dndList.remove(index);
                            }
                        }
                    }
                    if (x[0] == Rooms.size()) {
                        Log.d("ordersFinish",dndList.size()+" dnd finish");
                        dnd_adapter = new DND_Adapter(dndList);
                        DNDRecycler.setAdapter(dnd_adapter);
                        setDNDRoomsListener();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // yes
    void setRoomsListeners() {
            switch (DEP) {
                case "Cleanup":
                    setInitialCleanupRoomsListener();
                    setInitialDNDRoomsListener();
                    setInitialSOSRoomsListener();
                    break;
                case "Laundry":
                    setInitialLaundryRoomsListener();
                    setInitialDNDRoomsListener();
                    setInitialSOSRoomsListener();
                    break;
                case "RoomService":
                    setInitialRoomServiceRoomsListener();
                    setInitialDNDRoomsListener();
                    setInitialSOSRoomsListener();
                    break;
                case "Service":
                    setInitialCleanupRoomsListener();
                    setInitialLaundryRoomsListener();
                    setInitialDNDRoomsListener();
                    setInitialSOSRoomsListener();
                    setInitialRoomServiceRoomsListener();
                    break;
            }
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            Rooms.get(i).getFireRoom().child("roomStatus").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("roomStatusAction"+Rooms.get(finalI).RoomNumber,dataSnapshot.getValue().toString());
                        Rooms.get(finalI).roomStatus = Integer.parseInt(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    void setCleanupRoomsListener() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            CleanupListener[i] = Rooms.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(dataSnapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Cleanup");
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"Cleanup","",timeE,Rooms.get(finalI)));
                                refreshList();
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Cleanup");
                            if (index != -1) {
                                list.remove(index);
                                refreshList();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    void setLaundryRoomsListener() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            LaundryListener[i] = Rooms.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(dataSnapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Laundry");
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"Laundry","",timeE,Rooms.get(finalI)));
                                refreshList();
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"Laundry");
                            if (index != -1) {
                                list.remove(index);
                                refreshList();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    void setRoomServiceRoomsListener() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            RoomServiceListener[i] = Rooms.get(i).getFireRoom().child("RoomService").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        long value = Long.parseLong(dataSnapshot.getValue().toString());
                        int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"RoomService");
                        if (value == 0) {
                            if (index != -1) {
                                list.remove(index);
                                refreshList();
                            }
                        }
                        else {
                            if (index == -1) {
//                                Rooms.get(finalI).getRoomServiceText(new RoomServiceCallback() {
//                                    @SuppressLint("NotifyDataSetChanged")
//                                    @Override
//                                    public void onSuccess(String res) {
//                                        int x = list.size()+1;
//                                        list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(x),"RoomService",res,value,Rooms.get(finalI)));
//                                        refreshList();
//                                    }
//
//                                    @Override
//                                    public void onFail(String error) {
//
//                                    }
//                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            Log.d("roomServiceListeners",i+" "+RoomServiceListener[i]);
        }
    }

    void setSOSRoomsListener() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            SOSListener[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(dataSnapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"SOS");
                            if (index == -1) {
                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(list.size()+1),"SOS","",timeE,Rooms.get(finalI)));
                                refreshList();
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(list,Rooms.get(finalI).RoomNumber,"SOS");
                            if (index != -1) {
                                list.remove(index);
                                refreshList();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    void setDNDRoomsListener() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            DNDListener[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            long timeE = Long.parseLong(dataSnapshot.getValue().toString());
                            int index = cleanOrder.searchOrderInList(dndList,Rooms.get(finalI).RoomNumber,"DND");
                            if (index == -1) {
                                dndList.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber),String.valueOf(dndList.size()+1),"DND","",timeE,Rooms.get(finalI)));
                                dnd_adapter.notifyItemInserted(dndList.size()-1);
                            }
                        }
                        else {
                            int index = cleanOrder.searchOrderInList(dndList,Rooms.get(finalI).RoomNumber,"DND");
                            if (index != -1) {
                                dndList.remove(index);
                                dnd_adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    // yes
    public static void removeOldListeners() {
        for (int i = 0; i < Rooms.size(); i++) {
            switch (DEP) {
                case "Cleanup":
                    if (CleanupListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("Cleanup").removeEventListener(CleanupListener[i]);
                    }
                    if (DNDListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListener[i]);
                    }
                    break;
                case "Laundry":
                    if (LaundryListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("Laundry").removeEventListener(LaundryListener[i]);
                    }
                    if (DNDListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListener[i]);
                    }
                    break;
                case "RoomService":
                    if (RoomServiceListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("RoomService").removeEventListener(RoomServiceListener[i]);
                    }
                    if (DNDListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListener[i]);
                    }
                    if (SOSListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("SOS").removeEventListener(SOSListener[i]);
                    }
                    if (MiniBarCheck[i] != null) {
                        Rooms.get(i).getFireRoom().child("MiniBarCheck").removeEventListener(MiniBarCheck[i]);
                    }
                    break;
                case "Service":
                    if (CleanupListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("Cleanup").removeEventListener(CleanupListener[i]);
                    }
                    if (LaundryListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("Laundry").removeEventListener(LaundryListener[i]);
                    }
                    if (DNDListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListener[i]);
                    }
                    if (RoomServiceListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("RoomService").removeEventListener(RoomServiceListener[i]);
                    }
                    if (SOSListener[i] != null) {
                        Rooms.get(i).getFireRoom().child("SOS").removeEventListener(SOSListener[i]);
                    }
                    if (MiniBarCheck[i] != null) {
                        Rooms.get(i).getFireRoom().child("MiniBarCheck").removeEventListener(MiniBarCheck[i]);
                    }
                    break;
            }
        }
    }

    // yes
    void changePasswordDialog() {
        Dialog D = new Dialog(act);
        D.setContentView(R.layout.change_password_dialog);
        Window window = D.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        EditText oldPassword = D.findViewById(R.id.changePassword_oldPassword);
        EditText newPassword = D.findViewById(R.id.changePassword_NewPassword);
        EditText Confirm = D.findViewById(R.id.changePassword_ConNewPassword);
        Button cancel = D.findViewById(R.id.changePassword_cancel);
        Button send = D.findViewById(R.id.changePassword_send);
        cancel.setOnClickListener(v -> D.dismiss());
        send.setOnClickListener(v -> {
            String url = MyApp.MyProject.url + "users/updatePassword";
            if (oldPassword.getText() == null || oldPassword.getText().toString().isEmpty()) {
                Toast.makeText(act, "enter old password", Toast.LENGTH_SHORT).show();
                oldPassword.setHint("old password");
                oldPassword.setHintTextColor(Color.RED);
                return;
            }
            if (newPassword.getText() == null || newPassword.getText().toString().isEmpty()) {
                Toast.makeText(act, "enter new password", Toast.LENGTH_SHORT).show();
                newPassword.setHint("new password");
                newPassword.setHintTextColor(Color.RED);
                return;
            }
            if (Confirm.getText() == null || Confirm.getText().toString().isEmpty()) {
                Toast.makeText(act, "enter password confirmation", Toast.LENGTH_SHORT).show();
                Confirm.setHint("password confirm");
                Confirm.setHintTextColor(Color.RED);
                return;
            }
            LoadingDialog l = new LoadingDialog(act);
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Log.d("passwordResp", response);
                l.close();
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        new messageDialog("updated","updated",act);
                    }
                    else {
                        new messageDialog(result.getString("error"),"error",act);
                    }
                } catch (JSONException e) {
                    new messageDialog(e.getMessage(),"error",act);
                }
            }, error -> {
                Log.d("passwordResp", error.toString());
                l.close();
                new messageDialog(error.toString(),"error",act);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> par = new HashMap<>();
                    par.put("old_password", oldPassword.getText().toString());
                    par.put("new_password", newPassword.getText().toString());
                    par.put("conf_password", newPassword.getText().toString());
                    par.put("job_number", String.valueOf(MyApp.My_USER.jobNumber));
                    par.put("my_token", MyApp.Token);
                    return par;
                }
            };
            Volley.newRequestQueue(act).add(request);
        });
        D.show();
    }

    // yes
    void deleteToken(String id,VolleyCallback callback) {
        String url = MyApp.MyProject.url + "users/modifyUserFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("TokenResp", response);
            callback.onSuccess(response);
        }, error -> {
            Log.d("TokenResp", error.toString());
            callback.onFailed(error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", "0");
                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(r);
    }

    // yes
    void filterOrderByRoomNumber(List<cleanOrder> orders, List<ROOM> rooms) {
        int x=0;
        while (x<orders.size()) {
            boolean status = false;
            for (int i = 0; i < rooms.size(); i++) {
                if (Integer.parseInt(orders.get(x).roomNumber) == rooms.get(i).RoomNumber) {
                    status = true;
                    break;
                }
            }
            if (status) {
                x++;
            }
            else {
                orders.remove(x);
            }
        }
        ADAPTER = new OrdersAdapter(orders);
        OrdersRecycler.setAdapter(ADAPTER);
    }

    // yes
    void filterDNDOrderByRoomNumber(List<cleanOrder> orders, List<ROOM> rooms) {
        int index = 0 ;
        while (index <orders.size()) {
            boolean status = false;
            for (int i = 0; i < rooms.size(); i++) {
                if (Integer.parseInt(orders.get(index).roomNumber) == rooms.get(i).RoomNumber) {
                    status = true;
                    break;
                }
            }
            if (status) {
                orders.remove(index);
            }
            else {
                index++;
            }
        }
        dnd_adapter = new DND_Adapter(orders);
        DNDRecycler.setAdapter(dnd_adapter);
    }

    // yes
    void filterOrdersByDepartment(List<cleanOrder> orders) {
        switch (DEP) {
            case "Cleanup":
                int index = 0;
                while (index < orders.size()) {
                    if (orders.get(index).dep.equals("Laundry") || orders.get(index).dep.equals("RoomService")) {
                        orders.remove(index);
                    }
                    else {
                        index++;
                    }
                }
                break;
            case "Laundry":
                int index0 = 0;
                while(index0 <orders.size()) {
                    if (orders.get(index0).dep.equals("Cleanup") || orders.get(index0).dep.equals("RoomService")) {
                        orders.remove(index0);
                    }
                    else {
                        index0++;
                    }
                }
                break;
            case "RoomService":
                for (int j = 0; j < orders.size(); j++) {
                    if (orders.get(j).dep.equals("Cleanup") || orders.get(j).dep.equals("Laundry")) {
                        orderDB.removeRow(Long.parseLong(orders.get(j).orderNumber));
                    }
                }
                int index1 = 0;
                while(index1 < orders.size()) {
                    if (orders.get(index1).dep.equals("Cleanup") || orders.get(index1).dep.equals("Laundry")) {
                        orders.remove(index1);
                    }
                    else {
                        index1++;
                    }
                }
                break;
        }
        ADAPTER = new OrdersAdapter(orders);
        OrdersRecycler.setAdapter(ADAPTER);
    }

    // yes
    static void saveMyRoomsInSharedPreferences(List<ROOM> rooms) {
        String theRooms = "";
        for (int i = 0; i < rooms.size(); i++) {
            if (i + 1 == rooms.size()) {
                theRooms = MessageFormat.format("{0}{1}", theRooms, rooms.get(i).RoomNumber);
            } else {
                theRooms = theRooms + rooms.get(i).RoomNumber + "-";
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MyRooms, theRooms);
        editor.apply();
    }

    // yes
    void deleteUserDataFromSharedPreferences() {
        editor.putString("Id", null);
        editor.putString("Name", null);
        editor.putString("Control", null);
        editor.putString("JobNumber", null);
        editor.putString("Department", null);
        editor.apply();
    }

    // yes
    void createRestartGettingDataConfirmationDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(MyApp.getResourceString(R.string.restartGettingDataTitle));
        builder.setMessage(MyApp.getResourceString(R.string.restartGettingDataMessage)+"\n"+error);
        builder.setNegativeButton(MyApp.getResourceString(R.string.no), (dialog, which) -> {
            dialog.dismiss();
            act.finish();
        });
        builder.setPositiveButton(MyApp.getResourceString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            getData();
        });
        builder.create().show();
    }

    private void sortList(List<cleanOrder> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j < (list.size() - i); j++) {
                if (list.get(j - 1).date > list.get(j).date) {
                    Collections.swap(list, j, j - 1);
                }
            }
        }
    }

    void refreshList() {
        Log.d("adapterClass",isLists+" ");
        if (isLists) {
            ADAPTER = new OrdersAdapter(list);
            OrdersRecycler.setAdapter(ADAPTER);
        }
        else {
            ADAPTER = new OrdersGridAdapter(list);
            OrdersRecycler.setAdapter(ADAPTER);
        }
    }

    void saveUserDataToSharedPreferences(User u) {
        if (u == null) {
            editor.remove("user");
            editor.apply();
        }
        else {
            String stringUser = g.toJson(u);
            editor.putString("user",stringUser);
            editor.apply();
        }
    }
}

