package com.example.hotelservicesstandalone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.Classes.DefaultExceptionHandler;
import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;
import com.example.hotelservicesstandalone.Classes.DevicesDataDB;
import com.example.hotelservicesstandalone.Classes.Interfaces.GerRoomsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetBuildingsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetDevicesCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetFloorsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetSuitesCallBack;
import com.example.hotelservicesstandalone.Classes.Interfaces.PowerListener;
import com.example.hotelservicesstandalone.Classes.Interfaces.ServiceListener;
import com.example.hotelservicesstandalone.Classes.Interfaces.getDeviceDataCallback;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.PROJECT_VARIABLES;
import com.example.hotelservicesstandalone.Classes.Property.Building;
import com.example.hotelservicesstandalone.Classes.Property.Floor;
import com.example.hotelservicesstandalone.Classes.Property.PropertyDB;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Property.RoomView;
import com.example.hotelservicesstandalone.Classes.Property.Suite;
import com.example.hotelservicesstandalone.Classes.Tuya;
import com.example.hotelservicesstandalone.Dialogs.MessageDialog;
import com.example.hotelservicesstandalone.Dialogs.lodingDialog;
import com.example.hotelservicesstandalone.Interface.RequestCallback;
import com.example.hotelservicesstandalone.Services.checkWorkingReceiver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ReceptionScreen extends AppCompatActivity {

    static RequestQueue REQ;
    static lodingDialog loading;
    Activity act;
    static List<CheckinDevice> Devices ;
    static FirebaseDatabase database ;
    static DatabaseReference ProjectVariablesRef,ServerDevice;
    List<RoomView> roomViews;
    ImageButton CleanupB,LaundryB,DndB,CheckoutB,AllB;
    Button logout;
    LocalDataStore storage;
    DevicesDataDB db;
    PropertyDB pDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_screen);
        setActivity();
        setActivityActions();
        if (MyApp.isInternetConnected) {
            Log.d("bootingOp","connected");
            gettingAndPreparingData(act);
        }
        else {
            Log.d("bootingOp","unconnected");
            try {
                gettingAndPreparingLocalData(act);
            } catch (JSONException e) {
                Log.d("bootingOp","error locally "+e.getMessage());
            }
        }
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        setKeepAppAliveAlarm(getApplicationContext());
    }

    void setActivity() {
        act = this;
        REQ = Volley.newRequestQueue(act);
        database = FirebaseDatabase.getInstance(MyApp.firebaseDBUrl);
        ProjectVariablesRef = database.getReference(MyApp.My_PROJECT.projectName+"ProjectVariables");
        ServerDevice = database.getReference(MyApp.My_PROJECT.projectName+"ServerDevices/"+MyApp.controlDeviceMe.name);
        roomViews = new ArrayList<>();
        CleanupB = findViewById(R.id.cleanup);
        LaundryB = findViewById(R.id.laundry);
        DndB = findViewById(R.id.dnd);
        CheckoutB = findViewById(R.id.checkout);
        AllB = findViewById(R.id.home);
        storage = MyApp.getLocalStorage();
        logout = findViewById(R.id.button4);
        db = new DevicesDataDB(act);
        pDB = new PropertyDB(act);
    }

    void setActivityActions() {
        CleanupB.setOnClickListener(view->{
            RoomView.filterCleanup(roomViews);
            CleanupB.setBackgroundResource(R.drawable.pressed_btn);
            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
        });
        LaundryB.setOnClickListener(view->{
            RoomView.filterLaundry(roomViews);
            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
            LaundryB.setBackgroundResource(R.drawable.pressed_btn);
            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
        });
        DndB.setOnClickListener(view->{
            RoomView.filterDnd(roomViews);
            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
            DndB.setBackgroundResource(R.drawable.pressed_btn);
            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
        });
        CheckoutB.setOnClickListener(view->{
            RoomView.filterCheckout(roomViews);
            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
            CheckoutB.setBackgroundResource(R.drawable.pressed_btn);
            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
        });
        AllB.setOnClickListener(view->{
            RoomView.filterAll(roomViews);
            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
            AllB.setBackgroundResource(R.drawable.pressed_btn);
        });
        AllB.setBackgroundResource(R.drawable.pressed_btn);
        logout.setOnClickListener(view->{
            new AlertDialog.Builder(act).setTitle("Are you sure Logout ?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        MyApp.controlDeviceMe.deleteControlDevice(REQ, new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                storage.deleteAll();
                                pDB.deleteAll();
                                db.deleteAll();
                                startActivity(new Intent(act,Login.class));
                            }

                            @Override
                            public void onFail(String error) {
                                new MessageDialog(error,"error",act);
                            }
                        });
                    }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();


        });
    }

    void gettingAndPreparingData(Activity act) {
        Log.d("bootingOp","getting data");
        loading = new lodingDialog(act);
        PROJECT_VARIABLES.getProjectVariables(REQ,new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootingOp","variables done");
                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
                if (!PROJECT_VARIABLES.getIsProjectVariablesSaved(storage)) {
                    PROJECT_VARIABLES.saveProjectVariablesToStorage(storage);
                }
                Building.getBuildings(REQ, new GetBuildingsCallback() {
                    @Override
                    public void onSuccess(List<Building> buildings) {
                        Log.d("bootingOp","buildings done");
                        MyApp.Buildings = buildings;
                        if (!pDB.isBuildingsInserted()) {
                            PropertyDB.insertAllBuildings(MyApp.Buildings,pDB);
                        }
                        Floor.getFloors(REQ, new GetFloorsCallback() {
                            @Override
                            public void onSuccess(List<Floor> floors) {
                                Log.d("bootingOp","floors done "+floors.size());
                                MyApp.Floors = floors;
                                if (!pDB.isFloorsInserted()) {
                                    PropertyDB.insertAllFloors(MyApp.Floors,pDB);
                                }
                                MyApp.controlDeviceMe.getAllRooms(REQ,MyApp.My_PROJECT.url, new GerRoomsCallback() {
                                    @Override
                                    public void onSuccess(List<Room> rooms) {
                                        Log.d("bootingOp","rooms done "+rooms.size());
                                        MyApp.ROOMS = rooms;
                                        if (!pDB.isRoomsInserted()) {
                                            PropertyDB.insertAllRooms(MyApp.ROOMS,pDB);
                                        }
                                        Room.sortRoomsByNumber(MyApp.ROOMS);
                                        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
                                        Room.setRoomsFireRooms(MyApp.ROOMS,database);
                                        Suite.getSuites(MyApp.My_PROJECT.url,REQ, new GetSuitesCallBack() {
                                            @Override
                                            public void onSuccess(List<Suite> suites) {
                                                Log.d("bootingOp","suites done "+suites.size());
                                                MyApp.suites = suites;
                                                if (!pDB.isSuitesInserted()) {
                                                    PropertyDB.insertAllSuites(MyApp.suites,pDB);
                                                }
                                                Suite.setSuitesBuildingsAndFloors(MyApp.suites,MyApp.Buildings,MyApp.Floors);
                                                Suite.setSuitesFireSuites(MyApp.suites,database);
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
                                                                if (Tuya.getHomesFromStorage(storage).isEmpty()) {
                                                                    Tuya.saveHomesToStorage(storage,homeBeans);
                                                                }
                                                                Tuya.getDevices2(homeBeans,MyApp.ROOMS,MyApp.suites, new GetDevicesCallback() {
                                                                    @Override
                                                                    public void devices(List<CheckinDevice> devices) {
                                                                        Log.d("bootingOp","tuya devices done "+devices.size());
                                                                        Devices = devices;
                                                                        Tuya.gettingInitialDevicesData(Devices,db, new getDeviceDataCallback() {
                                                                            @Override
                                                                            public void onSuccess() {
                                                                                Log.d("bootingOp","getting initial done");
                                                                                Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
                                                                                settingInitialDevicesData(Devices);
                                                                                showRooms(act);
                                                                                setAllListeners();
                                                                                RoomView.filterAll(roomViews);
                                                                                loading.stop();
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
                                                createRestartConfirmationDialog(act,"getting floors failed \n"+error);
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

    void gettingAndPreparingLocalData(Activity act) throws JSONException {
        Log.d("bootingOp","getting data locally");
        loading = new lodingDialog(act);
        PROJECT_VARIABLES.getProjectVariablesFromStorage(storage);
        Log.d("bootingOp","getting variables locally");
        MyApp.Buildings = pDB.getBuildings();
        Log.d("bootingOp","getting buildings locally "+MyApp.Buildings.size());
        MyApp.Floors = pDB.getFloors();
        Log.d("bootingOp","getting floors locally "+MyApp.Floors.size());
        MyApp.ROOMS = pDB.getRooms();
        Log.d("bootingOp","getting rooms locally "+MyApp.ROOMS.size());
        Room.sortRoomsByNumber(MyApp.ROOMS);
        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
        Room.setRoomsFireRooms(MyApp.ROOMS,database);
        MyApp.suites = pDB.getSuites();
        Suite.setSuitesBuildingsAndFloors(MyApp.suites,MyApp.Buildings,MyApp.Floors);
        Suite.setSuitesFireSuites(MyApp.suites,database);
        Log.d("bootingOp","getting suites locally "+MyApp.suites.size());
        MyApp.PROJECT_HOMES = Tuya.getHomesFromStorage(storage);
        Log.d("bootingOp","getting homes locally ");
        Tuya.getLocalDevices(MyApp.PROJECT_HOMES,MyApp.ROOMS,MyApp.suites, new GetDevicesCallback() {
            @Override
            public void devices(List<CheckinDevice> devices) {
                Log.d("bootingOp","tuya devices done locally");
                Devices = devices;
                Tuya.gettingInitialDevicesData(Devices,db, new getDeviceDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("bootingOp","getting initial done");
                        Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
                        settingInitialDevicesData(Devices);
                        showRooms(act);
                        setAllListeners();
                        RoomView.filterAll(roomViews);
                        loading.stop();
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
    }

    void showRooms(Activity act) {
        int row = 0;
        LinearLayout rowLayout = new LinearLayout(act);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout roomsLayout = act.findViewById(R.id.RoomsLayout);
        roomsLayout.addView(rowLayout);
        for (Suite s : MyApp.suites) {
            Log.d("showRooms",s.SuiteNumber+" "+s.RoomsList.size());
            row++;
            if (row < 7) {
                RoomView v = new RoomView(act, s);
                roomViews.add(v);
                rowLayout.addView(v.createSuiteView());
            } else {
                row = 1;
                rowLayout = new LinearLayout(act);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                RoomView v = new RoomView(act, s);
                roomViews.add(v);
                rowLayout.addView(v.createSuiteView());
                roomsLayout.addView(rowLayout);
            }
            for (Room r : s.RoomsList) {
                Log.d("showRooms",r.RoomNumber+" "+r.id);
                row++;
                if (row < 7) {
                    RoomView v = new RoomView(act, r);
                    roomViews.add(v);
                    rowLayout.addView(v.createRoomView());
                } else {
                    row = 1;
                    rowLayout = new LinearLayout(act);
                    rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    RoomView v = new RoomView(act, r);
                    roomViews.add(v);
                    rowLayout.addView(v.createRoomView());
                    roomsLayout.addView(rowLayout);
                }
            }
        }
        roomViews.get(roomViews.size()-1).setCounters(roomViews);
    }

    void createRestartConfirmationDialog(Activity act,String message) {
        AlertDialog.Builder B = new AlertDialog.Builder(act);
        B.setTitle("Restart..?");
        B.setMessage(message);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                gettingAndPreparingData(act);
            }
        },2000);
        act.runOnUiThread(() -> B.create().show());
    }

    void settingInitialDevicesData(List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            cd.setInitialCurrentValues();
        }
    }

    void setAllListeners() {
        for (Suite s :MyApp.suites) {
            RoomView rvs = getSuiteView(roomViews, s);
            if (s.isHasServiceSwitch()) {
                if (s.getMainServiceSwitch().cleanup != null) {
                    if (s.getMainServiceSwitch().cleanup.getCurrent()) {
                        rvs.viewCleanup();
                    } else {
                        rvs.hideCleanup();
                    }
                }
                if (s.getMainServiceSwitch().laundry != null) {
                    if (s.getMainServiceSwitch().laundry.getCurrent()) {
                        rvs.viewLaundry();
                    } else {
                        rvs.hideLaundry();
                    }
                }
                if (s.getMainServiceSwitch().dnd != null) {
                    if (s.getMainServiceSwitch().dnd.getCurrent()) {
                        rvs.viewDND();
                    } else {
                        rvs.hideDND();
                    }
                }
                if (s.getMainServiceSwitch().checkout != null) {
                    if (s.getMainServiceSwitch().checkout.getCurrent()) {
                        rvs.viewCheckout();
                    } else {
                        rvs.hideCheckout();
                    }
                }
                s.getMainServiceSwitch().listen(new ServiceListener() {
                    @Override
                    public void cleanup() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.viewCleanup();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "cleanup on");
                        }
                    }

                    @Override
                    public void cancelCleanup() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.hideCleanup();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "cleanup off");
                        }
                    }

                    @Override
                    public void laundry() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.viewLaundry();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "laundry on");
                        }

                    }

                    @Override
                    public void cancelLaundry() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.hideLaundry();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "laundry off");
                        }
                    }

                    @Override
                    public void dnd() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.viewDND();
                            rvs.setCounters(roomViews);
                        }
                    }

                    @Override
                    public void cancelDnd() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.hideDND();
                            rvs.setCounters(roomViews);
                        }
                    }

                    @Override
                    public void checkout() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.viewCheckout();
                            rvs.setCounters(roomViews);
                        }
                    }

                    @Override
                    public void cancelCheckout() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (rvs != null) {
                            rvs.hideCheckout();
                            rvs.setCounters(roomViews);
                        }
                    }

                    @Override
                    public void online(boolean online) {

                    }
                });
            }
            if (s.isHasPower()) {
                if (s.getPowerModule().dp1.getCurrent() && s.getPowerModule().dp2.getCurrent()) {
                    rvs.setPowerStatusOn();
                } else if (s.getPowerModule().dp1.getCurrent() && !s.getPowerModule().dp2.getCurrent()) {
                    rvs.setPowerStatusCard();
                } else if (!s.getPowerModule().dp1.getCurrent() && !s.getPowerModule().dp2.getCurrent()) {
                    rvs.setPowerStatusOff();
                }
                s.getPowerModule().listen(new PowerListener() {
                    @Override
                    public void powerOn() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        s.getPowerModule().dp1.setCurrent(true);
                        s.getPowerModule().dp2.setCurrent(true);
                        rvs.setPowerStatusOn();
                    }

                    @Override
                    public void powerOff() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        s.getPowerModule().dp1.setCurrent(false);
                        s.getPowerModule().dp2.setCurrent(false);
                        rvs.setPowerStatusOff();
                    }

                    @Override
                    public void powerByCard() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        s.getPowerModule().dp1.setCurrent(true);
                        s.getPowerModule().dp2.setCurrent(false);
                        rvs.setPowerStatusCard();
                    }

                    @Override
                    public void online(boolean online) {

                    }
                });
            }
            if (s.isHasGateway()) {
                if (s.getSuiteGateway() != null) {
                    if (s.getSuiteGateway().device.getIsLocalOnline()) {
                        rvs.setRoomOnline();
                    }
                    else {
                        rvs.setRoomOffline();
                    }
                    s.getSuiteGateway().listen( online ->{
                        if (online) {
                            rvs.setRoomOffline();
                        }
                        else {
                            rvs.setRoomOffline();
                        }
                    });
                }
            }
            Log.d("roomSuite",s.SuiteNumber+"");
            for (Room r : s.RoomsList) {
                RoomView rv = getRoomView(roomViews, r);
                if (r.isHasServiceSwitch()) {
                    Log.d("roomSuite",r.RoomNumber+" service available");
                    if (r.getMainServiceSwitch().cleanup != null) {
                        if (r.getMainServiceSwitch().cleanup.getCurrent()) {
                            rv.viewCleanup();
                        } else {
                            rv.hideCleanup();
                        }
                    }
                    if (r.getMainServiceSwitch().laundry != null) {
                        if (r.getMainServiceSwitch().laundry.getCurrent()) {
                            rv.viewLaundry();
                        } else {
                            rv.hideLaundry();
                        }
                    }
                    if (r.getMainServiceSwitch().dnd != null) {
                        if (r.getMainServiceSwitch().dnd.getCurrent()) {
                            rv.viewDND();
                        } else {
                            rv.hideDND();
                        }
                    }
                    if (r.getMainServiceSwitch().checkout != null) {
                        if (r.getMainServiceSwitch().checkout.getCurrent()) {
                            rv.viewCheckout();
                        } else {
                            rv.hideCheckout();
                        }
                    }
                    r.getMainServiceSwitch().listen(new ServiceListener() {
                        @Override
                        public void cleanup() {
                            if (rv != null) {
                                rv.viewCleanup();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "cleanup on");
                            }
                        }

                        @Override
                        public void cancelCleanup() {
                            if (rv != null) {
                                rv.hideCleanup();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "cleanup off");
                            }
                        }

                        @Override
                        public void laundry() {
                            if (rv != null) {
                                rv.viewLaundry();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "laundry on");
                            }

                        }

                        @Override
                        public void cancelLaundry() {
                            if (rv != null) {
                                rv.hideLaundry();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "laundry off");
                            }
                        }

                        @Override
                        public void dnd() {
                            if (rv != null) {
                                rv.viewDND();
                                rv.setCounters(roomViews);
                            }
                        }

                        @Override
                        public void cancelDnd() {
                            if (rv != null) {
                                rv.hideDND();
                                rv.setCounters(roomViews);
                            }
                        }

                        @Override
                        public void checkout() {
                            if (rv != null) {
                                rv.viewCheckout();
                                rv.setCounters(roomViews);
                            }
                        }

                        @Override
                        public void cancelCheckout() {
                            if (rv != null) {
                                rv.hideCheckout();
                                rv.setCounters(roomViews);
                            }
                        }

                        @Override
                        public void online(boolean online) {

                        }
                    });
                }
                if (r.isHasPower()) {
                    Log.d("roomSuite",r.RoomNumber+" power available");
                    Log.d("powerProblem",r.RoomNumber+" "+r.getPowerModule().device.name);
                    if (r.getPowerModule().dp1.getCurrent() && r.getPowerModule().dp2.getCurrent()) {
                        rv.setPowerStatusOn();
                    } else if (r.getPowerModule().dp1.getCurrent() && !r.getPowerModule().dp2.getCurrent()) {
                        rv.setPowerStatusCard();
                    } else if (!r.getPowerModule().dp1.getCurrent() && !r.getPowerModule().dp2.getCurrent()) {
                        rv.setPowerStatusOff();
                    }
                    r.getPowerModule().listen(new PowerListener() {
                        @Override
                        public void powerOn() {
                            r.getPowerModule().dp1.setCurrent(true);
                            r.getPowerModule().dp2.setCurrent(true);
                            rv.setPowerStatusOn();
                        }

                        @Override
                        public void powerOff() {
                            r.getPowerModule().dp1.setCurrent(false);
                            r.getPowerModule().dp2.setCurrent(false);
                            rv.setPowerStatusOff();
                        }

                        @Override
                        public void powerByCard() {
                            r.getPowerModule().dp1.setCurrent(true);
                            r.getPowerModule().dp2.setCurrent(false);
                            rv.setPowerStatusCard();
                        }

                        @Override
                        public void online(boolean online) {

                        }
                    });
                }
                if (r.isHasGateway()) {
                    if (r.getRoomGateway() != null) {
                        if (r.getRoomGateway().device.getIsLocalOnline()) {
                            rv.setRoomOnline();
                        }
                        else {
                            rv.setRoomOffline();
                        }
                        r.getRoomGateway().listen(online -> {
                            if (online) {
                                rv.setRoomOnline();
                            }
                            else {
                                rv.setRoomOffline();
                            }
                        });
                    }
                }
            }
        }
    }

    RoomView getRoomView(List<RoomView> list,Room r) {
        for (RoomView rv : list) {
            if (rv.room != null && r != null) {
                if (rv.room.id == r.id) {
                    return rv;
                }
            }
        }
        return null ;
    }

    RoomView getSuiteView(List<RoomView> list,Suite s) {
        for (RoomView rv : list) {
            if (rv.suite != null && s != null) {
                if (rv.suite.id == s.id) {
                    return rv;
                }
            }
        }
        return null ;
    }

    public static void setCounters(Activity act) {
        TextView cleanupTv = act.findViewById(R.id.textView15);
        TextView laundryTv = act.findViewById(R.id.textView4);
        TextView checkoutTv = act.findViewById(R.id.textView14);
        TextView dndTv = act.findViewById(R.id.textView13);
        cleanupTv.setText(String.valueOf(RoomView.cleanupCounter));
        laundryTv.setText(String.valueOf(RoomView.laundryCounter));
        checkoutTv.setText(String.valueOf(RoomView.checkoutCounter));
        dndTv.setText(String.valueOf(RoomView.dndCounter));
    }

    void setKeepAppAliveAlarm(Context context) {
        Intent intent = new Intent(context, checkWorkingReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000*60*5) , pendingIntent);
        Log.d("workingAlarm","alarm set");
    }
}