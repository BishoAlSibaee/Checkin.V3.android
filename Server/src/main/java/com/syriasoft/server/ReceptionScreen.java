package com.syriasoft.server;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.syriasoft.server.Adapters.ReceptionRoom_Adapter;
import com.syriasoft.server.Adapters.RoomOffline_Adapter;
import com.syriasoft.server.Adapters.RoomOrder_Adapter;
import com.syriasoft.server.Adapters.RoomPower_Adapter;
import com.syriasoft.server.Classes.DefaultExceptionHandler;
import com.syriasoft.server.Classes.Devices.CheckinDevice;
import com.syriasoft.server.Classes.DevicesDataDB;
import com.syriasoft.server.Classes.Interfaces.GerRoomsCallback;
import com.syriasoft.server.Classes.Interfaces.GetBuildingsCallback;
import com.syriasoft.server.Classes.Interfaces.GetDevicesCallback;
import com.syriasoft.server.Classes.Interfaces.GetFloorsCallback;
import com.syriasoft.server.Classes.Interfaces.GetSuitesCallBack;
import com.syriasoft.server.Classes.Interfaces.PowerListener;
import com.syriasoft.server.Classes.Interfaces.ServiceListener;
import com.syriasoft.server.Classes.Interfaces.getDeviceDataCallback;
import com.syriasoft.server.Classes.LocalDataStore;
import com.syriasoft.server.Classes.PROJECT_VARIABLES;
import com.syriasoft.server.Classes.Property.Bed;
import com.syriasoft.server.Classes.Property.Building;
import com.syriasoft.server.Classes.Property.Floor;
import com.syriasoft.server.Classes.Property.PropertyDB;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.RoomView;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Classes.Tuya;
import com.syriasoft.server.Dialogs.MessageDialog;
import com.syriasoft.server.Dialogs.ProgressDialog;
import com.syriasoft.server.Dialogs.loadingDialog;
import com.syriasoft.server.Interface.RequestCallback;
import com.syriasoft.server.Services.checkWorkingReceiver;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ReceptionScreen extends AppCompatActivity {

    static RequestQueue REQ;
    loadingDialog loading;
    Activity act;
    static List<CheckinDevice> Devices ;
    static FirebaseDatabase database ;
    static DatabaseReference ProjectVariablesRef,ServerDevice;
    List<RoomView> roomViews;
    ImageButton CleanupB,LaundryB,DndB,CheckoutB,AllB;
    Button refresh,logout;
    LocalDataStore storage;
    DevicesDataDB db;
    PropertyDB pDB;
    public static List<Bed> beds,cleanupBeds,laundryBeds,dndBeds,checkoutBeds,powerOnBeds,powerCardBeds,powerOffBeds,offlineBeds;
    ReceptionRoom_Adapter adapter;
    int ind = 0;
    static int cleanupCounter=0,laundryCounter=0,dndCounter=0,checkoutCounter=0;
    static RoomOrder_Adapter cleanupAdapter,laundryAdapter,checkoutAdapter,dndAdapter;
    RoomPower_Adapter powerOnAdapter,powerOffAdapter,powerCardAdapter;
    RoomOffline_Adapter offlineAdapter;
    boolean servicesVisibility = true;
    boolean powerVisibility = false;
    boolean offlineVisibility = false;
    MediaPlayer player0,player1,player2,player3;
    public static Timer terminateTimer;
    boolean getOnlineGatewayDone = false;

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
        PendingIntent pIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0,new Intent(getIntent()), PendingIntent.FLAG_IMMUTABLE);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this,pIntent));
        setRefreshTimer();
    }

    @Override
    public void onBackPressed() {
        MyApp.finishActivities();
    }

    void setActivity() {
        act = this;
        MyApp.Activities.add(act);
        REQ = Volley.newRequestQueue(act);
        database = FirebaseDatabase.getInstance(MyApp.firebaseDBUrl);
        ProjectVariablesRef = database.getReference(MyApp.My_PROJECT.projectName+"ProjectVariables");
        ServerDevice = database.getReference(MyApp.My_PROJECT.projectName+"ServerDevices/"+MyApp.controlDeviceMe.name);
        roomViews = new ArrayList<>();
        beds = new ArrayList<>();
        cleanupBeds = new ArrayList<>();
        laundryBeds = new ArrayList<>();
        dndBeds = new ArrayList<>();
        checkoutBeds = new ArrayList<>();
        powerCardBeds = new ArrayList<>();
        powerOffBeds = new ArrayList<>();
        powerOnBeds = new ArrayList<>();
        offlineBeds = new ArrayList<>();
        CleanupB = findViewById(R.id.cleanup);
        LaundryB = findViewById(R.id.laundry);
        DndB = findViewById(R.id.dnd);
        CheckoutB = findViewById(R.id.checkout);
        AllB = findViewById(R.id.home);
        storage = MyApp.getLocalStorage();
        refresh = findViewById(R.id.button4);
        logout = findViewById(R.id.button4r);
        db = new DevicesDataDB(act);
        pDB = new PropertyDB(act);
        LinearLayout servicesLayout = findViewById(R.id.servicesLayout);
        LinearLayout powerLayout = findViewById(R.id.powersLayout);
        LinearLayout offlineLayout = findViewById(R.id.offlineLayout);
        servicesLayout.setVisibility(View.VISIBLE);
        powerLayout.setVisibility(View.GONE);
        offlineLayout.setVisibility(View.GONE);
        player0 = MediaPlayer.create(act,R.raw.notification_sound);
        player1 = MediaPlayer.create(act,R.raw.notification_sound);
        player2 = MediaPlayer.create(act,R.raw.notification_sound);
        player3 = MediaPlayer.create(act,R.raw.notification_sound);
    }

    void setActivityActions() {
        CleanupB.setOnClickListener(view->{
            //RoomView.filterCleanup(roomViews);
//            CleanupB.setBackgroundResource(R.drawable.pressed_btn);
//            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
//            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
//            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
//            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
//            showRoomsRecycler(cleanupBeds);
        });
        LaundryB.setOnClickListener(view->{
            //RoomView.filterLaundry(roomViews);
//            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
//            LaundryB.setBackgroundResource(R.drawable.pressed_btn);
//            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
//            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
//            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
//            showRoomsRecycler(laundryBeds);
        });
        DndB.setOnClickListener(view->{
            //RoomView.filterDnd(roomViews);
            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
            DndB.setBackgroundResource(R.drawable.pressed_btn);
            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
            showRoomsRecycler(dndBeds);
        });
        CheckoutB.setOnClickListener(view->{
            //RoomView.filterCheckout(roomViews);
//            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
//            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
//            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
//            CheckoutB.setBackgroundResource(R.drawable.pressed_btn);
//            AllB.setBackgroundResource(R.drawable.btn_bg_normal);
//            showRoomsRecycler(checkoutBeds);
        });
        AllB.setOnClickListener(view->{
            //RoomView.filterAll(roomViews);
//            CleanupB.setBackgroundResource(R.drawable.btn_bg_normal);
//            LaundryB.setBackgroundResource(R.drawable.btn_bg_normal);
//            DndB.setBackgroundResource(R.drawable.btn_bg_normal);
//            CheckoutB.setBackgroundResource(R.drawable.btn_bg_normal);
//            AllB.setBackgroundResource(R.drawable.pressed_btn);
//            showRoomsRecycler(beds);
        });
        AllB.setBackgroundResource(R.drawable.pressed_btn);
        AllB.setVisibility(View.GONE);
        refresh.setOnClickListener(view-> new AlertDialog.Builder(act).setTitle("Are you sure Refresh ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    refreshApp();
                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show());
        logout.setOnClickListener(view ->{
            new AlertDialog.Builder(act).setTitle("Logout Are you sure ?").setPositiveButton("Yes", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                MyApp.controlDeviceMe.deleteControlDevice(REQ, new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        storage.deleteAll();
                        db.deleteAll();
                        pDB.deleteAll();
                        startActivity(new Intent(act,Login.class));
                        act.finish();
                    }

                    @Override
                    public void onFail(String error) {
                        new MessageDialog(error,"error",act);
                    }
                });
            }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();

        });
        TextView servicesCaption = findViewById(R.id.textView34);
        servicesCaption.setOnClickListener(view -> {
            servicesVisibility = !servicesVisibility;
            LinearLayout servicesLayout = findViewById(R.id.servicesLayout);
            if (servicesVisibility) {
                servicesLayout.setVisibility(View.VISIBLE);
            }
            else {
                servicesLayout.setVisibility(View.GONE);
            }
        });
        TextView powerCaption = findViewById(R.id.textView300);
        powerCaption.setOnClickListener(view -> {
            //resetCloseTimer();
            powerVisibility = !powerVisibility;
            LinearLayout powerLayout = findViewById(R.id.powersLayout);
            if (powerVisibility) {
                powerLayout.setVisibility(View.VISIBLE);
            }
            else {
                powerLayout.setVisibility(View.GONE);
            }
        });
        TextView offlineCaption = findViewById(R.id.textView30000);
        offlineCaption.setOnClickListener(view->{
            //resetCloseTimer();
            offlineVisibility = !offlineVisibility;
            LinearLayout offlineLayout = findViewById(R.id.offlineLayout);
            if (offlineVisibility) {
                offlineLayout.setVisibility(View.VISIBLE);
            }
            else {
                offlineLayout.setVisibility(View.GONE);
            }
        });
    }

    void gettingAndPreparingData(Activity act) {
        Log.d("bootingOp","getting data");
        ProgressDialog progress = new ProgressDialog(act,"getting project variables",9);
        progress.show();
        PROJECT_VARIABLES.getProjectVariables(REQ,storage,new RequestCallback() {
            @Override
            public void onSuccess() {
                progress.setProgress(1,"getting buildings");
                Log.d("bootingOp","variables done");
                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
                if (!PROJECT_VARIABLES.getIsProjectVariablesSaved(storage)) {
                    PROJECT_VARIABLES.saveProjectVariablesToStorage(storage);
                }
                Building.getBuildings(REQ,pDB, new GetBuildingsCallback() {
                    @Override
                    public void onSuccess(List<Building> buildings) {
                        progress.setProgress(2,"getting floors");
                        Log.d("bootingOp","buildings done");
                        MyApp.Buildings = buildings;
                        if (!pDB.isBuildingsInserted()) {
                            PropertyDB.insertAllBuildings(MyApp.Buildings,pDB);
                        }
                        Floor.getFloors(REQ,pDB, new GetFloorsCallback() {
                            @Override
                            public void onSuccess(List<Floor> floors) {
                                progress.setProgress(3,"getting rooms");
                                Log.d("bootingOp","floors done "+floors.size());
                                MyApp.Floors = floors;
                                if (!pDB.isFloorsInserted()) {
                                    PropertyDB.insertAllFloors(MyApp.Floors,pDB);
                                }
                                MyApp.controlDeviceMe.getAllRooms(REQ,MyApp.My_PROJECT.url, new GerRoomsCallback() {
                                    @Override
                                    public void onSuccess(List<Room> rooms) {
                                        progress.setProgress(4,"getting suites");
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
                                                progress.setProgress(5,"login to t");
                                                Log.d("bootingOp","suites done "+suites.size());
                                                MyApp.suites = suites;
                                                if (!pDB.isSuitesInserted()) {
                                                    PropertyDB.insertAllSuites(MyApp.suites,pDB);
                                                }
                                                Suite.setSuitesBuildingsAndFloors(MyApp.suites,MyApp.Buildings,MyApp.Floors);
                                                Suite.setSuitesFireSuites(MyApp.suites,database);
                                                beds = setBeds(MyApp.suites);
                                                Tuya.loginTuya(MyApp.My_PROJECT, new ILoginCallback() {
                                                    @Override
                                                    public void onSuccess(User user) {
                                                        progress.setProgress(6,"getting homes");
                                                        Log.d("bootingOp","tuya login done");
                                                        MyApp.TuyaUser = user;
                                                        Tuya.getProjectHomes(MyApp.My_PROJECT,storage, new ITuyaGetHomeListCallback() {
                                                            @Override
                                                            public void onSuccess(List<HomeBean> homeBeans) {
                                                                progress.setProgress(7,"getting devices");
                                                                Log.d("bootingOp","tuya project homes done "+homeBeans.size());
                                                                MyApp.PROJECT_HOMES = homeBeans;
                                                                if (Tuya.getHomesFromStorage(storage).isEmpty()) {
                                                                    Tuya.saveHomesToStorage(storage,homeBeans);
                                                                }
                                                                Tuya.getDevices2(homeBeans,MyApp.ROOMS,MyApp.suites, new GetDevicesCallback() {
                                                                    @Override
                                                                    public void devices(List<CheckinDevice> devices) {
                                                                        progress.setProgress(8,"getting devices data");
                                                                        Log.d("bootingOp","tuya devices done "+devices.size());
                                                                        Devices = devices;
                                                                        Tuya.gettingInitialDevicesData(Devices,db, new getDeviceDataCallback() {
                                                                            @Override
                                                                            public void onSuccess() {
                                                                                Log.d("bootingOp","getting initial done");
                                                                                Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
                                                                                settingInitialDevicesData(Devices, new RequestCallback() {
                                                                                    @Override
                                                                                    public void onSuccess() {
                                                                                        runOnUiThread(() -> {
                                                                                            setCounters();
                                                                                            showRoomsOrders();
                                                                                            progress.setProgress(9);
                                                                                        });
                                                                                        setAllListenersRecycler();
                                                                                        Log.d("bootingOp","finish");
                                                                                    }

                                                                                    @Override
                                                                                    public void onFail(String error) {

                                                                                    }
                                                                                });


                                                                            }

                                                                            @Override
                                                                            public void onError(String error) {
                                                                                progress.close();
                                                                                createRestartConfirmationDialog(act,"getting devices failed \n"+error);
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onError(String error) {
                                                                        progress.close();
                                                                        createRestartConfirmationDialog(act,"getting devices failed \n"+error);
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onError(String errorCode, String error) {
                                                                progress.close();
                                                                createRestartConfirmationDialog(act,"getting homes failed \n"+error);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(String code, String error) {
                                                        progress.close();
                                                        createRestartConfirmationDialog(act,"login tuya failed \n"+error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String error) {
                                                progress.close();
                                                createRestartConfirmationDialog(act,"getting floors failed \n"+error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        progress.close();
                                        createRestartConfirmationDialog(act,"getting rooms failed \n"+error);
                                    }
                                });

                            }

                            @Override
                            public void onError(String error) {
                                progress.close();
                                createRestartConfirmationDialog(act,"getting floors failed \n"+error);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        progress.close();
                        createRestartConfirmationDialog(act,"getting buildings failed \n"+error);
                    }
                });
            }

            @Override
            public void onFail(String error) {
                progress.close();
                createRestartConfirmationDialog(act,"getting project variables failed \n"+error);
            }
        });
    }

    void gettingAndPreparingLocalData(Activity act) throws JSONException {
        Log.d("bootingOp","getting data locally done ");
        if (loading == null) {
            loading = new loadingDialog(act);
        }
//        ProgressDialog progress = new ProgressDialog(act,"getting project variables",9);
//        progress.show();
//        progress.setProgress(1,"getting project variables");
        PROJECT_VARIABLES.getProjectVariablesFromStorage(storage);
        Log.d("bootingOp","getting variables locally done ");
        //progress.setProgress(2,"getting buildings");
        MyApp.Buildings = pDB.getBuildings();
        Log.d("bootingOp","getting buildings locally done "+MyApp.Buildings.size());
        ///progress.setProgress(3,"getting floors");
        MyApp.Floors = pDB.getFloors();
        Log.d("bootingOp","getting floors locally done "+MyApp.Floors.size());
        //progress.setProgress(4,"getting rooms");
        MyApp.ROOMS = pDB.getRooms();
        Log.d("bootingOp","getting rooms locally done "+MyApp.ROOMS.size());
        Room.sortRoomsByNumber(MyApp.ROOMS);
        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
        Room.setRoomsFireRooms(MyApp.ROOMS,database);
        //progress.setProgress(5,"getting suites");
        MyApp.suites = pDB.getSuites();
        Suite.setSuitesBuildingsAndFloors(MyApp.suites,MyApp.Buildings,MyApp.Floors);
        Suite.setSuitesFireSuites(MyApp.suites,database);
        Log.d("bootingOp","getting suites locally done "+MyApp.suites.size());
        beds = setBeds(MyApp.suites);
        //progress.setProgress(6,"getting homes");
        MyApp.PROJECT_HOMES = Tuya.getHomesFromStorage(storage);
        Log.d("bootingOp","getting homes locally done "+MyApp.PROJECT_HOMES.size());
        //progress.setProgress(7,"getting devices");
        Tuya.getLocalDevices(MyApp.PROJECT_HOMES,MyApp.ROOMS,MyApp.suites, new GetDevicesCallback() {
            @Override
            public void devices(List<CheckinDevice> devices) {
                //progress.setProgress(8,"getting devices data");
                Log.d("bootingOp","tuya devices locally done "+devices.size());
                Devices = devices;
                Tuya.gettingInitialDevicesData(Devices,db, new getDeviceDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("bootingOp","getting initial done");
                        Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
                        settingInitialDevicesDataOffline();
                        //deleteDevicesCurrentValues();
                        runOnUiThread(() -> {
                            setOfflineCounters();
                            refreshOffline();
                            setPowerCounters();
                            refreshPower();
                            setCounters();
                            showRoomsOrders();
                            setAllListenersRecycler();
                            if (loading != null) {
                                loading.stop();
                            }
                            Log.d("bootingOp","finish");
                        });
//                        settingInitialDevicesData(Devices, new RequestCallback() {
//                            @Override
//                            public void onSuccess() {
//                                runOnUiThread(() -> {
//                                    setOfflineCounters();
//                                    refreshOffline();
//                                    setPowerCounters();
//                                    refreshPower();
//                                    setCounters();
//                                    showRoomsOrders();
//                                    setAllListenersRecycler();
//                                    if (loading != null) {
//                                        loading.stop();
//                                    }
//                                });
//                                Log.d("bootingOp","finish");
//                            }
//
//                            @Override
//                            public void onFail(String error) {
//                                if (loading != null) {
//                                    loading.stop();
//                                }
//                                createRestartConfirmationDialog(act,"setting initial data failed \n"+error);
//                            }
//                        });
                        //progress.setProgress(9);
                    }

                    @Override
                    public void onError(String error) {
                        if (loading != null) {
                            loading.stop();
                        }
                        createRestartConfirmationDialog(act,"getting devices failed \n"+error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (loading != null) {
                    loading.stop();
                }
                createRestartConfirmationDialog(act,"getting devices failed \n"+error);
            }
        });
    }

    void showRooms(Activity act) {
        int row = 0;
        LinearLayout rowLayout = new LinearLayout(act);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout roomsLayout = act.findViewById(R.id.RoomsLayout);
        LinearLayout finalRowLayout = rowLayout;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomsLayout.addView(finalRowLayout);
            }
        });

        for (Suite s : MyApp.suites) {
            Log.d("showRooms",s.SuiteNumber+" "+s.RoomsList.size());
            row++;
            if (row < 7) {
                RoomView v = new RoomView(act, s);
                roomViews.add(v);
                LinearLayout finalRowLayout1 = rowLayout;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finalRowLayout1.addView(v.createSuiteView());
                    }
                });
            } else {
                row = 1;
                rowLayout = new LinearLayout(act);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                RoomView v = new RoomView(act, s);
                roomViews.add(v);
                LinearLayout finalRowLayout2 = rowLayout;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finalRowLayout2.addView(v.createSuiteView());
                        roomsLayout.addView(finalRowLayout2);
                    }
                });

            }
            for (Room r : s.RoomsList) {
                Log.d("showRooms",r.RoomNumber+" "+r.id);
                row++;
                if (row < 7) {
                    RoomView v = new RoomView(act, r);
                    roomViews.add(v);
                    LinearLayout finalRowLayout3 = rowLayout;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalRowLayout3.addView(v.createRoomView());
                        }
                    });

                } else {
                    row = 1;
                    rowLayout = new LinearLayout(act);
                    rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    RoomView v = new RoomView(act, r);
                    roomViews.add(v);
                    LinearLayout finalRowLayout4 = rowLayout;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalRowLayout4.addView(v.createRoomView());
                            roomsLayout.addView(finalRowLayout4);
                        }
                    });
                }
            }
        }
        roomViews.get(roomViews.size()-1).setCounters(roomViews);
    }

    void showRoomsRecycler(List<Bed> beds) {
        RecyclerView roomsRecycler = findViewById(R.id.roomsRecycler);
        adapter = new ReceptionRoom_Adapter(beds);
        GridLayoutManager manager = new GridLayoutManager(act,6,RecyclerView.VERTICAL,false);
        roomsRecycler.setLayoutManager(manager);
        roomsRecycler.setAdapter(adapter);
    }

    void showRoomsOrders() {
        RecyclerView cleanupRecycler = findViewById(R.id.cleanupRecycler);
        RecyclerView laundryRecycler = findViewById(R.id.laundryRecycler);
        RecyclerView checkoutRecycler = findViewById(R.id.checkoutRecycler);
        RecyclerView dndRecycler = findViewById(R.id.dndRecycler);
        RecyclerView powerOnRecycler = findViewById(R.id.powerOnRecycler);
        RecyclerView powerOffRecycler = findViewById(R.id.powerOffRecycler);
        RecyclerView powerCardRecycler = findViewById(R.id.powerCardRecycler);
        RecyclerView offlineRecycler = findViewById(R.id.offlineRecycler);
        cleanupAdapter = new RoomOrder_Adapter(cleanupBeds);
        laundryAdapter = new RoomOrder_Adapter(laundryBeds);
        checkoutAdapter = new RoomOrder_Adapter(checkoutBeds);
        dndAdapter = new RoomOrder_Adapter(dndBeds);
        powerOnAdapter = new RoomPower_Adapter(powerOnBeds);
        powerOffAdapter = new RoomPower_Adapter(powerOffBeds);
        powerCardAdapter = new RoomPower_Adapter(powerCardBeds);
        offlineAdapter = new RoomOffline_Adapter(offlineBeds);
        Log.d("listsCount","cleanupBeds "+cleanupBeds.size());
        Log.d("listsCount","laundryBeds "+laundryBeds.size());
        Log.d("listsCount","checkoutBeds "+checkoutBeds.size());
        Log.d("listsCount","dndBeds "+dndBeds.size());
        Log.d("listsCount","powerOnBeds "+powerOnBeds.size());
        Log.d("listsCount","powerOffBeds "+powerOffBeds.size());
        Log.d("listsCount","powerCardBeds "+powerCardBeds.size());
        Log.d("listsCount","offlineBeds "+offlineBeds.size());
        GridLayoutManager manager0 = new GridLayoutManager(act,3);
        GridLayoutManager manager1 = new GridLayoutManager(act,3);
        GridLayoutManager manager2 = new GridLayoutManager(act,3);
        GridLayoutManager manager3 = new GridLayoutManager(act,3);
        GridLayoutManager manager4 = new GridLayoutManager(act,4);
        GridLayoutManager manager5 = new GridLayoutManager(act,4);
        GridLayoutManager manager6 = new GridLayoutManager(act,4);
        GridLayoutManager manager7 = new GridLayoutManager(act,8);
        cleanupRecycler.setLayoutManager(manager0);
        laundryRecycler.setLayoutManager(manager1);
        checkoutRecycler.setLayoutManager(manager2);
        dndRecycler.setLayoutManager(manager3);
        powerOnRecycler.setLayoutManager(manager4);
        powerOffRecycler.setLayoutManager(manager5);
        powerCardRecycler.setLayoutManager(manager6);
        offlineRecycler.setLayoutManager(manager7);
        cleanupRecycler.setAdapter(cleanupAdapter);
        laundryRecycler.setAdapter(laundryAdapter);
        checkoutRecycler.setAdapter(checkoutAdapter);
        dndRecycler.setAdapter(dndAdapter);
        powerOnRecycler.setAdapter(powerOnAdapter);
        powerCardRecycler.setAdapter(powerCardAdapter);
        powerOffRecycler.setAdapter(powerOffAdapter);
        offlineRecycler.setAdapter(offlineAdapter);
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

    void settingInitialDevicesData(List<CheckinDevice> devices,RequestCallback callback) {
        for (CheckinDevice cd : devices) {
            cd.setInitialCurrentValues(new RequestCallback() {
                @Override
                public void onSuccess() {
                    if (cd == devices.get(devices.size()-1)) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFail(String error) {
                    callback.onFail(error);
                }
            });
        }
    }

    void settingInitialDevicesDataOffline() {
        for (int i=0;i<beds.size();i++) {
            Bed b = beds.get(i);
            if (b.isRoom()) {
                Room r = b.room;
                if (r.isHasServiceSwitch()) {
                    r.getMainServiceSwitch().setInitialCurrentValuesOffline(storage);
                }
                if (r.isHasPower()) {
                    r.getPowerModule().setInitialCurrentValuesOffline(storage);
                }
                if (r.isHasGateway()) {
                    r.getRoomGateway().setInitialCurrentValuesOffline(storage);
                }
            }
            else if (b.isSuite()) {
                Suite s = b.suite;
                if (s.isHasServiceSwitch()) {
                    s.getMainServiceSwitch().setInitialCurrentValuesOffline(storage);
                }
                if (s.isHasPower()) {
                    s.getPowerModule().setInitialCurrentValuesOffline(storage);
                }
                if (s.isHasGateway()) {
                    s.getSuiteGateway().setInitialCurrentValuesOffline(storage);
                }
            }
        }
    }

    void deleteDevicesCurrentValues() {
        for (int i=0;i<beds.size();i++) {
            Bed b = beds.get(i);
            if (b.isRoom()) {
                Room r = b.room;
                if (r.isHasServiceSwitch()) {
                    r.getMainServiceSwitch().deleteServiceValues(storage);
                }
                if (r.isHasPower()) {
                    r.getPowerModule().deletePowerValues(storage);
                }
                if (r.isHasGateway()) {
                    r.getRoomGateway().deleteOnlineValue(storage);
                }
            }
            else if (b.isSuite()) {
                Suite s = b.suite;
                if (s.isHasServiceSwitch()) {
                    s.getMainServiceSwitch().deleteServiceValues(storage);
                }
                if (s.isHasPower()) {
                    s.getPowerModule().deletePowerValues(storage);
                }
                if (s.isHasGateway()) {
                    s.getSuiteGateway().deleteOnlineValue(storage);
                }
            }
        }
    }

    void setAllListeners() {
        Log.d("bootingOp","roo views are "+roomViews.size());
        for (Suite s :MyApp.suites) {
            RoomView rvs = getSuiteView(roomViews, s);
            if (rvs != null) {
                if (s.isHasServiceSwitch()) {
                    if (s.getMainServiceSwitch().cleanup != null) {
                        if (s.getMainServiceSwitch().cleanup.getCurrent()) {
                            if (s.getMainServiceSwitch().device.getIsLocalOnline()) {
                                rvs.viewCleanup();
                            }
                            else {
                                rvs.hideCleanup();
                            }
                        }
                        else {
                            rvs.hideCleanup();
                        }
                    }
                    if (s.getMainServiceSwitch().laundry != null) {
                        if (s.getMainServiceSwitch().laundry.getCurrent()) {
                            if (s.getMainServiceSwitch().device.getIsLocalOnline()) {
                                rvs.viewLaundry();
                            }
                            else {
                                rvs.hideLaundry();
                            }
                        } else {
                            rvs.hideLaundry();
                        }
                    }
                    if (s.getMainServiceSwitch().dnd != null) {
                        if (s.getMainServiceSwitch().dnd.getCurrent()) {
                            if (s.getMainServiceSwitch().device.getIsLocalOnline()) {
                                rvs.viewDND();
                            }
                            else {
                                rvs.hideDND();
                            }
                        } else {
                            rvs.hideDND();
                        }
                    }
                    if (s.getMainServiceSwitch().checkout != null) {
                        if (s.getMainServiceSwitch().checkout.getCurrent()) {
                            if (s.getMainServiceSwitch().device.getIsLocalOnline()) {
                                rvs.viewCheckout();
                            }
                            else {
                                rvs.hideCheckout();
                            }
                        } else {
                            rvs.hideCheckout();
                        }
                    }
                    s.getMainServiceSwitch().listen(new ServiceListener() {
                        @Override
                        public void cleanup() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.viewCleanup();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "cleanup on");
                        }

                        @Override
                        public void cancelCleanup() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.hideCleanup();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "cleanup off");
                        }

                        @Override
                        public void laundry() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.viewLaundry();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "laundry on");

                        }

                        @Override
                        public void cancelLaundry() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.hideLaundry();
                            rvs.setCounters(roomViews);
                            Log.d("serviceAction", "laundry off");
                        }

                        @Override
                        public void dnd() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.viewDND();
                            rvs.setCounters(roomViews);
                        }

                        @Override
                        public void cancelDnd() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.hideDND();
                            rvs.setCounters(roomViews);
                        }

                        @Override
                        public void checkout() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.viewCheckout();
                            rvs.setCounters(roomViews);
                        }

                        @Override
                        public void cancelCheckout() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            rvs.hideCheckout();
                            rvs.setCounters(roomViews);
                        }

                        @Override
                        public void online(boolean online) {

                        }

                        @Override
                        public void lightOn() {}

                        @Override
                        public void lightOff() {

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
                    Log.d("suiteOnline" , s.SuiteNumber+" has gateway ");
                    if (s.getSuiteGateway() != null) {
                        Log.d("suiteOnline" , s.SuiteNumber+" "+s.getSuiteGateway().device.name);
                        if (s.getSuiteGateway().device.getIsLocalOnline()) {
                            Log.d("suiteOnline" , s.SuiteNumber+" online");
                            rvs.setRoomOnline();
                        }
                        else {
                            Log.d("suiteOnline" , s.SuiteNumber+" offline");
                            rvs.setRoomOffline();
                        }
                        s.getSuiteGateway().listen( online ->{
                            if (online) {
                                rvs.setRoomOnline();
                            }
                            else {
                                rvs.setRoomOffline();
                            }
                        });
                    }
                }
            }
            Log.d("roomSuite",s.SuiteNumber+"");
            for (Room r : s.RoomsList) {
                RoomView rv = getRoomView(roomViews, r);
                if (rv != null) {
                    if (r.isHasServiceSwitch()) {
                        Log.d("roomSuite", r.RoomNumber + " service available");
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
                                rv.viewCleanup();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "cleanup on");
                            }

                            @Override
                            public void cancelCleanup() {
                                rv.hideCleanup();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "cleanup off");
                            }

                            @Override
                            public void laundry() {
                                rv.viewLaundry();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "laundry on");

                            }

                            @Override
                            public void cancelLaundry() {
                                rv.hideLaundry();
                                rv.setCounters(roomViews);
                                Log.d("serviceAction", "laundry off");
                            }

                            @Override
                            public void dnd() {
                                rv.viewDND();
                                rv.setCounters(roomViews);
                            }

                            @Override
                            public void cancelDnd() {
                                rv.hideDND();
                                rv.setCounters(roomViews);
                            }

                            @Override
                            public void checkout() {
                                rv.viewCheckout();
                                rv.setCounters(roomViews);
                            }

                            @Override
                            public void cancelCheckout() {
                                rv.hideCheckout();
                                rv.setCounters(roomViews);
                            }

                            @Override
                            public void online(boolean online) {

                            }

                            @Override
                            public void lightOn() {}

                            @Override
                            public void lightOff() {}
                        });
                    }
                    if (r.isHasPower()) {
                        Log.d("roomSuite", r.RoomNumber + " power available");
                        //Log.d("powerProblem", r.RoomNumber + " " + r.getPowerModule().device.name);
                        if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
                            if (r.getPowerModule().dp1.getCurrent() && r.getPowerModule().dp2.getCurrent()) {
                                rv.setPowerStatusOn();
                            }
                            else if (r.getPowerModule().dp1.getCurrent() && !r.getPowerModule().dp2.getCurrent()) {
                                rv.setPowerStatusCard();
                            }
                            else if (!r.getPowerModule().dp1.getCurrent() && !r.getPowerModule().dp2.getCurrent()) {
                                rv.setPowerStatusOff();
                            }
                        }
                        else {
                            ind++;
                            Log.d("powerProblem", r.RoomNumber + " dp1 dp2 null "+ind );
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
                            } else {
                                rv.setRoomOffline();
                            }
                            r.getRoomGateway().listen(online -> {
                                if (online) {
                                    rv.setRoomOnline();
                                } else {
                                    rv.setRoomOffline();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    void setAllListenersRecycler() {
        Log.d("Listeners","start "+beds.size());
//        for (int i=0;i<beds.size();i++) {
//            Bed b = beds.get(i);
//            if (b.isRoom()) {
//                Room r = b.room;
//                Log.d("Listeners","room "+r.RoomNumber);
//                if (r.isHasServiceSwitch()) {
//                    Log.d("Listeners","service not null");
//                    r.getMainServiceSwitch().listen(new ServiceListener() {
//                        @Override
//                        public void cleanup() {
//                            Log.d("ListenersCleanup","cleanup "+r.RoomNumber);
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player0.start();
//                            r.getMainServiceSwitch().storeCleanupValue(storage,true);
//                            if (r.getMainServiceSwitch().cleanup != null) {
//                                Log.d("ListenersCleanup","not null");
//                                if (r.getRoomGateway().device.getIsLocalOnline()) {
//                                    Log.d("ListenersCleanup","local online");
//                                    r.getMainServiceSwitch().cleanup.setCurrent(true);
//                                }
//                                else {
//                                    Log.d("ListenersCleanup","offline");
//                                }
//                            }
//                            else {
//                                Log.d("ListenersCleanup","null");
//                            }
//                            setCleanupLists();
//                            refreshCleanup();
//                        }
//
//                        @Override
//                        public void cancelCleanup() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getMainServiceSwitch().storeCleanupValue(storage,false);
//                            if (r.getMainServiceSwitch().cleanup != null) {
//                                r.getMainServiceSwitch().cleanup.setCurrent(false);
//                            }
//                            setCleanupLists();
//                            refreshCleanup();
//                        }
//
//                        @Override
//                        public void laundry() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player1.start();
//                            r.getMainServiceSwitch().storeLaundryValue(storage,true);
//                            if (r.getMainServiceSwitch().laundry != null) {
//                                if (r.getRoomGateway().device.getIsLocalOnline()) {
//                                    r.getMainServiceSwitch().laundry.setCurrent(true);
//                                }
//                            }
//                            setLaundryLists();
//                            refreshLaundry();
//                        }
//
//                        @Override
//                        public void cancelLaundry() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getMainServiceSwitch().storeLaundryValue(storage,false);
//                            if (r.getMainServiceSwitch().laundry != null) {
//                                r.getMainServiceSwitch().laundry.setCurrent(false);
//                            }
//                            setLaundryLists();
//                            refreshLaundry();
//                        }
//
//                        @Override
//                        public void dnd() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player2.start();
//                            r.getMainServiceSwitch().storeDNDValue(storage,true);
//                            if (r.getMainServiceSwitch().dnd != null) {
//                                if (r.getRoomGateway().device.getIsLocalOnline()) {
//                                    r.getMainServiceSwitch().dnd.setCurrent(true);
//                                }
//                            }
//                            setDndLists();
//                            refreshDND();
//                        }
//
//                        @Override
//                        public void cancelDnd() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getMainServiceSwitch().storeDNDValue(storage,false);
//                            if (r.getMainServiceSwitch().dnd != null) {
//                                r.getMainServiceSwitch().dnd.setCurrent(false);
//                            }
//                            setDndLists();
//                            refreshDND();
//                        }
//
//                        @Override
//                        public void checkout() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player3.start();
//                            r.getMainServiceSwitch().storeCheckoutValue(storage,true);
//                            if (r.getMainServiceSwitch().checkout != null) {
//                                if (r.getRoomGateway().device.getIsLocalOnline()) {
//                                    r.getMainServiceSwitch().checkout.setCurrent(true);
//                                }
//                            }
//                            setCheckoutLists();
//                            refreshCheckout();
//                        }
//
//                        @Override
//                        public void cancelCheckout() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getMainServiceSwitch().storeCheckoutValue(storage,false);
//                            if (r.getMainServiceSwitch().checkout != null) {
//                                r.getMainServiceSwitch().checkout.setCurrent(false);
//                            }
//                            setCheckoutLists();
//                            refreshCheckout();
//                        }
//
//                        @Override
//                        public void online(boolean online) {
//                            r.getMainServiceSwitch().online = online;
//                        }
//                        @Override
//                        public void lightOn() {}
//
//                        @Override
//                        public void lightOff() {}
//                    });
//                }
//                if (r.isHasPower()) {
//                    Log.d("Listeners","power not null");
//                    r.getPowerModule().listen(new PowerListener() {
//                        @Override
//                        public void powerOn() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getPowerModule().storeDp1Value(storage,true);
//                            r.getPowerModule().storeDp2Value(storage,true);
//                            if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
//                                r.getPowerModule().dp1.setCurrent(true);
//                                r.getPowerModule().dp2.setCurrent(true);
//                            }
//                            setPowerCounters();
//                            refreshPower();
//                        }
//
//                        @Override
//                        public void powerOff() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getPowerModule().storeDp1Value(storage,false);
//                            r.getPowerModule().storeDp2Value(storage,false);
//                            if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
//                                r.getPowerModule().dp1.setCurrent(false);
//                                r.getPowerModule().dp2.setCurrent(false);
//                            }
//                            setPowerCounters();
//                            refreshPower();
//                        }
//
//                        @Override
//                        public void powerByCard() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            r.getPowerModule().storeDp1Value(storage,true);
//                            r.getPowerModule().storeDp2Value(storage,false);
//                            if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
//                                r.getPowerModule().dp1.setCurrent(true);
//                                r.getPowerModule().dp2.setCurrent(false);
//                            }
//                            setPowerCounters();
//                            refreshPower();
//                        }
//
//                        @Override
//                        public void online(boolean online) {
//                            r.getPowerModule().online = online;
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                        }
//                    });
//                }
//                if (r.isHasGateway()) {
//                    Log.d("Listeners","gateway not null");
//                    r.getRoomGateway().listen(online -> {
//                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                        Log.d("Listeners","online "+online);
//                        r.getRoomGateway().storeOnlineValue(storage,online);
//                        r.getRoomGateway().currentOnline = online;
//                        setOfflineCounters();
//                        refreshOffline();
//                    });
//                }
//            }
//            else if (b.isSuite()) {
//                Suite s = b.suite;
//                Log.d("Listeners","suite "+s.SuiteNumber);
//                if (s.isHasServiceSwitch()) {
//                    Log.d("Listeners","service not null");
//                    s.getMainServiceSwitch().listen(new ServiceListener() {
//                        @Override
//                        public void cleanup() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player0.start();
//                            s.getMainServiceSwitch().storeCleanupValue(storage,true);
//                            if (s.getMainServiceSwitch().cleanup != null) {
//                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
//                                    s.getMainServiceSwitch().cleanup.setCurrent(true);
//                                }
//                            }
//                            setCleanupLists();
//                            refreshCleanup();
//                        }
//
//                        @Override
//                        public void cancelCleanup() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getMainServiceSwitch().storeCleanupValue(storage,false);
//                            if (s.getMainServiceSwitch().cleanup != null) {
//                                s.getMainServiceSwitch().cleanup.setCurrent(false);
//                            }
//                            setCleanupLists();
//                            refreshCleanup();
//                        }
//
//                        @Override
//                        public void laundry() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player1.start();
//                            s.getMainServiceSwitch().storeLaundryValue(storage,true);
//                            if (s.getMainServiceSwitch().laundry != null) {
//                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
//                                    s.getMainServiceSwitch().laundry.setCurrent(true);
//                                }
//                            }
//                            setLaundryLists();
//                            refreshLaundry();
//                        }
//
//                        @Override
//                        public void cancelLaundry() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getMainServiceSwitch().storeLaundryValue(storage,false);
//                            if (s.getMainServiceSwitch().laundry != null) {
//
//                                s.getMainServiceSwitch().laundry.setCurrent(false);
//                            }
//                            setLaundryLists();
//                            refreshLaundry();
//                        }
//
//                        @Override
//                        public void dnd() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player2.start();
//                            s.getMainServiceSwitch().storeDNDValue(storage,true);
//                            if (s.getMainServiceSwitch().dnd != null) {
//                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
//                                    s.getMainServiceSwitch().dnd.setCurrent(true);
//                                }
//                            }
//                            setDndLists();
//                            refreshDND();
//                        }
//
//                        @Override
//                        public void cancelDnd() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getMainServiceSwitch().storeDNDValue(storage,false);
//                            if (s.getMainServiceSwitch().dnd != null) {
//                                s.getMainServiceSwitch().dnd.setCurrent(false);
//                            }
//                            setDndLists();
//                            refreshDND();
//                        }
//
//                        @Override
//                        public void checkout() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            player3.start();
//                            s.getMainServiceSwitch().storeCheckoutValue(storage,true);
//                            if (s.getMainServiceSwitch().checkout != null) {
//                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
//                                    s.getMainServiceSwitch().checkout.setCurrent(true);
//                                }
//                            }
//                            setCheckoutLists();
//                            refreshCheckout();
//                        }
//
//                        @Override
//                        public void cancelCheckout() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getMainServiceSwitch().storeCheckoutValue(storage,false);
//                            if (s.getMainServiceSwitch().checkout != null) {
//                                s.getMainServiceSwitch().checkout.setCurrent(false);
//                            }
//                            setCheckoutLists();
//                            refreshCheckout();
//                        }
//
//                        @Override
//                        public void online(boolean online) {
//                            s.getMainServiceSwitch().online = online;
//                        }
//
//                        @Override
//                        public void lightOn() {}
//
//                        @Override
//                        public void lightOff() {}
//                    });
//                }
//                if (s.isHasPower()) {
//                    s.getPowerModule().listen(new PowerListener() {
//                        @Override
//                        public void powerOn() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getPowerModule().storeDp1Value(storage,true);
//                            s.getPowerModule().storeDp2Value(storage,true);
//                            if (s.getPowerModule().dp1 != null && s.getPowerModule().dp2 != null) {
//                                s.getPowerModule().dp1.setCurrent(true);
//                                s.getPowerModule().dp2.setCurrent(true);
//                            }
//                            setPowerCounters();
//                            refreshPower();
//                        }
//
//                        @Override
//                        public void powerOff() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getPowerModule().storeDp1Value(storage,false);
//                            s.getPowerModule().storeDp2Value(storage,false);
//                            if (s.getPowerModule().dp1 != null && s.getPowerModule().dp2 != null) {
//                                s.getPowerModule().dp1.setCurrent(false);
//                                s.getPowerModule().dp2.setCurrent(false);
//                            }
//                            setPowerCounters();
//                            refreshPower();
//                        }
//
//                        @Override
//                        public void powerByCard() {
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getPowerModule().storeDp1Value(storage,true);
//                            s.getPowerModule().storeDp2Value(storage,false);
//                            if (s.getPowerModule().dp1 != null && s.getPowerModule().dp2 != null) {
//                                s.getPowerModule().dp1.setCurrent(true);
//                                s.getPowerModule().dp2.setCurrent(false);
//                            }
//                            setPowerCounters();
//                            refreshPower();
//                        }
//
//                        @Override
//                        public void online(boolean online) {
//                            s.getPowerModule().online = online;
//                        }
//                    });
//                }
//                if (s.isHasGateway()) {
//                    Log.d("suiteOnline" , s.SuiteNumber+" has gateway ");
//                    if (s.getSuiteGateway() != null) {
//                        s.getSuiteGateway().listen( online ->{
//                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
//                            s.getSuiteGateway().storeOnlineValue(storage,online);
//                            s.getSuiteGateway().currentOnline = online;
//                            setOfflineCounters();
//                            refreshOffline();
//                        });
//                    }
//                }
//            }
//        }
        setGatewaysListeners();
        setPowerListeners();
        setServiceListeners();
    }

    void setGatewaysListeners() {
        for (int i=0;i<beds.size();i++) {
            Bed b = beds.get(i);
            if (b.isRoom()) {
                Room r = b.room;
                if (r.isHasGateway()) {
                    Log.d("Listeners","gateway not null");
                    r.getRoomGateway().listen(online -> {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        Log.d("Listeners","online "+online);
                        r.getRoomGateway().storeOnlineValue(storage,online);
                        r.getRoomGateway().currentOnline = online;
                        setOfflineCounters();
                        refreshOffline();
                    });
                }
            }
            else if (b.isSuite()) {
                Suite s = b.suite;
                if (s.isHasGateway()) {
                    Log.d("suiteOnline" , s.SuiteNumber+" has gateway ");
                    if (s.getSuiteGateway() != null) {
                        s.getSuiteGateway().listen( online ->{
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getSuiteGateway().storeOnlineValue(storage,online);
                            s.getSuiteGateway().currentOnline = online;
                            setOfflineCounters();
                            refreshOffline();
                        });
                    }
                }
            }
        }
    }
    void setPowerListeners() {
        for (int i=0;i<beds.size();i++) {
            Bed b = beds.get(i);
            if (b.isRoom()) {
                Room r = b.room;
                if (r.isHasPower()) {
                    Log.d("Listeners","power not null");
                    r.getPowerModule().listen(new PowerListener() {
                        @Override
                        public void powerOn() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getPowerModule().storeDp1Value(storage,true);
                            r.getPowerModule().storeDp2Value(storage,true);
                            if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
                                r.getPowerModule().dp1.setCurrent(true);
                                r.getPowerModule().dp2.setCurrent(true);
                            }
                            setPowerCounters();
                            refreshPower();
                        }

                        @Override
                        public void powerOff() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getPowerModule().storeDp1Value(storage,false);
                            r.getPowerModule().storeDp2Value(storage,false);
                            if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
                                r.getPowerModule().dp1.setCurrent(false);
                                r.getPowerModule().dp2.setCurrent(false);
                            }
                            setPowerCounters();
                            refreshPower();
                        }

                        @Override
                        public void powerByCard() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getPowerModule().storeDp1Value(storage,true);
                            r.getPowerModule().storeDp2Value(storage,false);
                            if (r.getPowerModule().dp1 != null && r.getPowerModule().dp2 != null) {
                                r.getPowerModule().dp1.setCurrent(true);
                                r.getPowerModule().dp2.setCurrent(false);
                            }
                            setPowerCounters();
                            refreshPower();
                        }

                        @Override
                        public void online(boolean online) {
                            r.getPowerModule().online = online;
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        }
                    });
                }
            }
            else if (b.isSuite()) {
                Suite s = b.suite;
                if (s.isHasPower()) {
                    s.getPowerModule().listen(new PowerListener() {
                        @Override
                        public void powerOn() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getPowerModule().storeDp1Value(storage,true);
                            s.getPowerModule().storeDp2Value(storage,true);
                            if (s.getPowerModule().dp1 != null && s.getPowerModule().dp2 != null) {
                                s.getPowerModule().dp1.setCurrent(true);
                                s.getPowerModule().dp2.setCurrent(true);
                            }
                            setPowerCounters();
                            refreshPower();
                        }

                        @Override
                        public void powerOff() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getPowerModule().storeDp1Value(storage,false);
                            s.getPowerModule().storeDp2Value(storage,false);
                            if (s.getPowerModule().dp1 != null && s.getPowerModule().dp2 != null) {
                                s.getPowerModule().dp1.setCurrent(false);
                                s.getPowerModule().dp2.setCurrent(false);
                            }
                            setPowerCounters();
                            refreshPower();
                        }

                        @Override
                        public void powerByCard() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getPowerModule().storeDp1Value(storage,true);
                            s.getPowerModule().storeDp2Value(storage,false);
                            if (s.getPowerModule().dp1 != null && s.getPowerModule().dp2 != null) {
                                s.getPowerModule().dp1.setCurrent(true);
                                s.getPowerModule().dp2.setCurrent(false);
                            }
                            setPowerCounters();
                            refreshPower();
                        }

                        @Override
                        public void online(boolean online) {
                            s.getPowerModule().online = online;
                        }
                    });
                }
            }
        }
    }
    void setServiceListeners() {
        for (int i=0;i<beds.size();i++) {
            Bed b = beds.get(i);
            if (b.isRoom()) {
                Room r = b.room;
                if (r.isHasServiceSwitch()) {
                    Log.d("Listeners","service not null");
                    r.getMainServiceSwitch().listen(new ServiceListener() {
                        @Override
                        public void cleanup() {
                            Log.d("ListenersCleanup","cleanup "+r.RoomNumber);
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player0.start();
                            r.getMainServiceSwitch().storeCleanupValue(storage,true);
                            if (r.getMainServiceSwitch().cleanup != null) {
                                Log.d("ListenersCleanup","not null");
                                if (r.getRoomGateway().device.getIsLocalOnline()) {
                                    Log.d("ListenersCleanup","local online");
                                    r.getMainServiceSwitch().cleanup.setCurrent(true);
                                }
                                else {
                                    Log.d("ListenersCleanup","offline");
                                }
                            }
                            else {
                                Log.d("ListenersCleanup","null");
                            }
                            setCleanupLists(act);
                            refreshCleanup(act);
                        }

                        @Override
                        public void cancelCleanup() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getMainServiceSwitch().storeCleanupValue(storage,false);
                            if (r.getMainServiceSwitch().cleanup != null) {
                                r.getMainServiceSwitch().cleanup.setCurrent(false);
                            }
                            setCleanupLists(act);
                            refreshCleanup(act);
                        }

                        @Override
                        public void laundry() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player1.start();
                            r.getMainServiceSwitch().storeLaundryValue(storage,true);
                            if (r.getMainServiceSwitch().laundry != null) {
                                if (r.getRoomGateway().device.getIsLocalOnline()) {
                                    r.getMainServiceSwitch().laundry.setCurrent(true);
                                }
                            }
                            setLaundryLists(act);
                            refreshLaundry(act);
                        }

                        @Override
                        public void cancelLaundry() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getMainServiceSwitch().storeLaundryValue(storage,false);
                            if (r.getMainServiceSwitch().laundry != null) {
                                r.getMainServiceSwitch().laundry.setCurrent(false);
                            }
                            setLaundryLists(act);
                            refreshLaundry(act);
                        }

                        @Override
                        public void dnd() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player2.start();
                            r.getMainServiceSwitch().storeDNDValue(storage,true);
                            if (r.getMainServiceSwitch().dnd != null) {
                                if (r.getRoomGateway().device.getIsLocalOnline()) {
                                    r.getMainServiceSwitch().dnd.setCurrent(true);
                                }
                            }
                            setDndLists(act);
                            refreshDND(act);
                        }

                        @Override
                        public void cancelDnd() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getMainServiceSwitch().storeDNDValue(storage,false);
                            if (r.getMainServiceSwitch().dnd != null) {
                                r.getMainServiceSwitch().dnd.setCurrent(false);
                            }
                            setDndLists(act);
                            refreshDND(act);
                        }

                        @Override
                        public void checkout() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player3.start();
                            r.getMainServiceSwitch().storeCheckoutValue(storage,true);
                            if (r.getMainServiceSwitch().checkout != null) {
                                if (r.getRoomGateway().device.getIsLocalOnline()) {
                                    r.getMainServiceSwitch().checkout.setCurrent(true);
                                }
                            }
                            setCheckoutLists(act);
                            refreshCheckout(act);
                        }

                        @Override
                        public void cancelCheckout() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            r.getMainServiceSwitch().storeCheckoutValue(storage,false);
                            if (r.getMainServiceSwitch().checkout != null) {
                                r.getMainServiceSwitch().checkout.setCurrent(false);
                            }
                            setCheckoutLists(act);
                            refreshCheckout(act);
                        }

                        @Override
                        public void online(boolean online) {
                            r.getMainServiceSwitch().online = online;
                        }
                        @Override
                        public void lightOn() {}

                        @Override
                        public void lightOff() {}
                    });
                }
            }
            else if (b.isSuite()) {
                Suite s = b.suite;
                if (s.isHasServiceSwitch()) {
                    Log.d("Listeners","service not null");
                    s.getMainServiceSwitch().listen(new ServiceListener() {
                        @Override
                        public void cleanup() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player0.start();
                            s.getMainServiceSwitch().storeCleanupValue(storage,true);
                            if (s.getMainServiceSwitch().cleanup != null) {
                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
                                    s.getMainServiceSwitch().cleanup.setCurrent(true);
                                }
                            }
                            setCleanupLists(act);
                            refreshCleanup(act);
                        }

                        @Override
                        public void cancelCleanup() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getMainServiceSwitch().storeCleanupValue(storage,false);
                            if (s.getMainServiceSwitch().cleanup != null) {
                                s.getMainServiceSwitch().cleanup.setCurrent(false);
                            }
                            setCleanupLists(act);
                            refreshCleanup(act);
                        }

                        @Override
                        public void laundry() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player1.start();
                            s.getMainServiceSwitch().storeLaundryValue(storage,true);
                            if (s.getMainServiceSwitch().laundry != null) {
                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
                                    s.getMainServiceSwitch().laundry.setCurrent(true);
                                }
                            }
                            setLaundryLists(act);
                            refreshLaundry(act);
                        }

                        @Override
                        public void cancelLaundry() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getMainServiceSwitch().storeLaundryValue(storage,false);
                            if (s.getMainServiceSwitch().laundry != null) {

                                s.getMainServiceSwitch().laundry.setCurrent(false);
                            }
                            setLaundryLists(act);
                            refreshLaundry(act);
                        }

                        @Override
                        public void dnd() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player2.start();
                            s.getMainServiceSwitch().storeDNDValue(storage,true);
                            if (s.getMainServiceSwitch().dnd != null) {
                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
                                    s.getMainServiceSwitch().dnd.setCurrent(true);
                                }
                            }
                            setDndLists(act);
                            refreshDND(act);
                        }

                        @Override
                        public void cancelDnd() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getMainServiceSwitch().storeDNDValue(storage,false);
                            if (s.getMainServiceSwitch().dnd != null) {
                                s.getMainServiceSwitch().dnd.setCurrent(false);
                            }
                            setDndLists(act);
                            refreshDND(act);
                        }

                        @Override
                        public void checkout() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            player3.start();
                            s.getMainServiceSwitch().storeCheckoutValue(storage,true);
                            if (s.getMainServiceSwitch().checkout != null) {
                                if (s.getSuiteGateway().device.getIsLocalOnline()) {
                                    s.getMainServiceSwitch().checkout.setCurrent(true);
                                }
                            }
                            setCheckoutLists(act);
                            refreshCheckout(act);
                        }

                        @Override
                        public void cancelCheckout() {
                            Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                            s.getMainServiceSwitch().storeCheckoutValue(storage,false);
                            if (s.getMainServiceSwitch().checkout != null) {
                                s.getMainServiceSwitch().checkout.setCurrent(false);
                            }
                            setCheckoutLists(act);
                            refreshCheckout(act);
                        }

                        @Override
                        public void online(boolean online) {
                            s.getMainServiceSwitch().online = online;
                        }

                        @Override
                        public void lightOn() {}

                        @Override
                        public void lightOff() {}
                    });
                }
            }
        }
    }

    void removeListeners() {
        for (Suite s :MyApp.suites) {
            RoomView rvs = getSuiteView(roomViews, s);
            if (rvs != null) {
                if (s.isHasServiceSwitch()) {
                    s.getMainServiceSwitch().unListen();
                }
                if (s.isHasPower()) {
                    s.getPowerModule().unListen();
                }
                if (s.isHasGateway()) {
                    Log.d("suiteOnline" , s.SuiteNumber+" has gateway ");
                    if (s.getSuiteGateway() != null) {
                        s.getSuiteGateway().unListen();
                    }
                }
            }
            Log.d("roomSuite",s.SuiteNumber+"");
            for (Room r : s.RoomsList) {
                RoomView rv = getRoomView(roomViews, r);
                if (rv != null) {
                    if (r.isHasServiceSwitch()) {
                        r.getMainServiceSwitch().unListen();
                    }
                    if (r.isHasPower()) {
                        r.getPowerModule().unListen();
                    }
                    if (r.isHasGateway()) {
                        if (r.getRoomGateway() != null) {
                            r.getRoomGateway().unListen();
                        }
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
    void setCounters() {
        cleanupCounter = 0;
        laundryCounter = 0;
        dndCounter = 0;
        checkoutCounter = 0;
        cleanupBeds.clear();
        laundryBeds.clear();
        dndBeds.clear();
        checkoutBeds.clear();
        powerOnBeds.clear();
        powerOffBeds.clear();
        powerCardBeds.clear();
        offlineBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                if (b.suite.getSuiteGateway() != null) {
                    if (!b.suite.getSuiteGateway().currentOnline) {
                        offlineBeds.add(b);
                    }
                }
                if (b.suite.getMainServiceSwitch() != null) {
                    if (b.suite.getMainServiceSwitch().cleanup != null) {
                        if (b.suite.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.suite.getMainServiceSwitch().cleanup.getCurrent()) {
                                if (!cleanupBeds.contains(b)) {
                                    cleanupBeds.add(b);
                                }
                                cleanupCounter++;
                            }
                        }
                    }
                    if (b.suite.getMainServiceSwitch().laundry != null) {
                        if (b.suite.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.suite.getMainServiceSwitch().laundry.getCurrent()) {
                                if (!laundryBeds.contains(b)) {
                                    laundryBeds.add(b);
                                }
                                laundryCounter++;
                            }
                        }
                    }
                    if (b.suite.getMainServiceSwitch().dnd != null) {
                        if (b.suite.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.suite.getMainServiceSwitch().dnd.getCurrent()) {
                                if (!dndBeds.contains(b)) {
                                    dndBeds.add(b);
                                }
                                dndCounter++;
                            }
                        }
                    }
                    if (b.suite.getMainServiceSwitch().checkout != null) {
                        if (b.suite.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.suite.getMainServiceSwitch().checkout.getCurrent()) {
                                if (!checkoutBeds.contains(b)) {
                                    checkoutBeds.add(b);
                                }
                                checkoutCounter++;
                            }
                        }
                    }
                }
                if (b.suite.getPowerModule() != null) {
                        if (b.suite.getPowerModule().dp1 != null && b.suite.getPowerModule().dp2 != null) {
                            if (b.suite.getPowerModule().dp1.getCurrent() && b.suite.getPowerModule().dp2.getCurrent()) {
                                powerOnBeds.add(b);
                            }
                            else if (b.suite.getPowerModule().dp1.getCurrent() && !b.suite.getPowerModule().dp2.getCurrent()) {
                                powerCardBeds.add(b);
                            }
                            else if (!b.suite.getPowerModule().dp1.getCurrent() && !b.suite.getPowerModule().dp2.getCurrent()) {
                                powerOffBeds.add(b);
                            }
                        }
                    else {
                        Log.d("PowerNull",b.suite.SuiteNumber+" dp");
                    }
                }
                else {
                    Log.d("PowerNull",b.suite.SuiteNumber+" module");
                }
            }
            if (b.isRoom()) {
                if (b.room.getRoomGateway() != null) {
                    if (!b.room.getRoomGateway().currentOnline) {
                        offlineBeds.add(b);
                    }
                }
                if (b.room.getMainServiceSwitch() != null) {
                    if (b.room.getMainServiceSwitch().cleanup != null) {
                        if (b.room.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.room.getMainServiceSwitch().cleanup.getCurrent()) {
                                if (!cleanupBeds.contains(b)) {
                                    cleanupBeds.add(b);
                                }
                                cleanupCounter++;
                            }
                        }
                    }
                    if (b.room.getMainServiceSwitch().laundry != null) {
                        if (b.room.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.room.getMainServiceSwitch().laundry.getCurrent()) {
                                if (!laundryBeds.contains(b)) {
                                    laundryBeds.add(b);
                                }
                                laundryCounter++;
                            }
                        }
                    }
                    if (b.room.getMainServiceSwitch().dnd != null) {
                        if (b.room.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.room.getMainServiceSwitch().dnd.getCurrent()) {
                                if (!dndBeds.contains(b)) {
                                    dndBeds.add(b);
                                }
                                dndCounter++;
                            }
                        }
                    }
                    if (b.room.getMainServiceSwitch().checkout != null) {
                        if (b.room.getMainServiceSwitch().device.getIsLocalOnline()) {
                            if (b.room.getMainServiceSwitch().checkout.getCurrent()) {
                                if (!checkoutBeds.contains(b)) {
                                    checkoutBeds.add(b);
                                }
                                checkoutCounter++;
                            }
                        }
                    }
                }
                if (b.room.getPowerModule() != null) {
                        if (b.room.getPowerModule().dp1 != null && b.room.getPowerModule().dp2 != null) {
                            if (b.room.getPowerModule().dp1.getCurrent() && b.room.getPowerModule().dp2.getCurrent()) {
                                powerOnBeds.add(b);
                            } else if (b.room.getPowerModule().dp1.getCurrent() && !b.room.getPowerModule().dp2.getCurrent()) {
                                powerCardBeds.add(b);
                            } else if (!b.room.getPowerModule().dp1.getCurrent() && !b.room.getPowerModule().dp2.getCurrent()) {
                                powerOffBeds.add(b);
                            }
                        } else {
                            Log.d("PowerNull", b.room.RoomNumber + " dp");
                        }
                }
                else {
                    Log.d("PowerNull",b.room.RoomNumber+" module");
                }
            }
        }
        TextView cleanupTv = act.findViewById(R.id.textView15);
        TextView laundryTv = act.findViewById(R.id.textView4);
        TextView checkoutTv = act.findViewById(R.id.textView14);
        TextView dndTv = act.findViewById(R.id.textView13);
        cleanupTv.setText(String.valueOf(cleanupCounter));
        laundryTv.setText(String.valueOf(laundryCounter));
        checkoutTv.setText(String.valueOf(checkoutCounter));
        dndTv.setText(String.valueOf(dndCounter));
    }

    void setServiceCounters() {
        cleanupCounter = 0;
        laundryCounter = 0;
        dndCounter = 0;
        checkoutCounter = 0;
        cleanupBeds.clear();
        laundryBeds.clear();
        dndBeds.clear();
        checkoutBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
//                if (b.suite.getSuiteGateway() != null) {
//                    if (!b.suite.getSuiteGateway().currentOnline) {
//                        offlineBeds.add(b);
//                    }
//                }
                if (b.suite.getMainServiceSwitch() != null) {
                    if (b.suite.getMainServiceSwitch().cleanup != null) {
                        if (b.suite.getMainServiceSwitch().cleanup.getCurrent()) {
                            if (!cleanupBeds.contains(b)) {
                                cleanupBeds.add(b);
                            }
                            cleanupCounter++;
                        }
                    }
                    if (b.suite.getMainServiceSwitch().laundry != null) {
                        if (b.suite.getMainServiceSwitch().laundry.getCurrent()) {
                            if (!laundryBeds.contains(b)) {
                                laundryBeds.add(b);
                            }
                            laundryCounter++;
                        }
                    }
                    if (b.suite.getMainServiceSwitch().dnd != null) {
                        if (b.suite.getMainServiceSwitch().dnd.getCurrent()) {
                            if (!dndBeds.contains(b)) {
                                dndBeds.add(b);
                            }
                            dndCounter++;
                        }
                    }
                    if (b.suite.getMainServiceSwitch().checkout != null) {
                        if (b.suite.getMainServiceSwitch().checkout.getCurrent()) {
                            if (!checkoutBeds.contains(b)) {
                                checkoutBeds.add(b);
                            }
                            checkoutCounter++;
                        }
                    }
                }
//                if (b.suite.getPowerModule() != null) {
//                    if (b.suite.getPowerModule().dp1 != null && b.suite.getPowerModule().dp2 != null) {
//                        if (b.suite.getPowerModule().dp1.getCurrent() && b.suite.getPowerModule().dp2.getCurrent()) {
//                            powerOnBeds.add(b);
//                        }
//                        else if (b.suite.getPowerModule().dp1.getCurrent() && !b.suite.getPowerModule().dp2.getCurrent()) {
//                            powerCardBeds.add(b);
//                        }
//                        else if (!b.suite.getPowerModule().dp1.getCurrent() && !b.suite.getPowerModule().dp2.getCurrent()) {
//                            powerOffBeds.add(b);
//                        }
//                    }
//                    else {
//                        Log.d("PowerNull",b.suite.SuiteNumber+" dp");
//                    }
//                }
//                else {
//                    Log.d("PowerNull",b.suite.SuiteNumber+" module");
//                }
            }
            if (b.isRoom()) {
//                if (b.room.getRoomGateway() != null) {
//                    if (!b.room.getRoomGateway().currentOnline) {
//                        offlineBeds.add(b);
//                    }
//                }
                if (b.room.getMainServiceSwitch() != null) {
                    if (b.room.getMainServiceSwitch().cleanup != null) {
                        if (b.room.getMainServiceSwitch().cleanup.getCurrent()) {
                            if (!cleanupBeds.contains(b)) {
                                cleanupBeds.add(b);
                            }
                            cleanupCounter++;
                        }
                    }
                    if (b.room.getMainServiceSwitch().laundry != null) {
                        if (b.room.getMainServiceSwitch().laundry.getCurrent()) {
                            if (!laundryBeds.contains(b)) {
                                laundryBeds.add(b);
                            }
                            laundryCounter++;
                        }
                    }
                    if (b.room.getMainServiceSwitch().dnd != null) {
                        if (b.room.getMainServiceSwitch().dnd.getCurrent()) {
                            if (!dndBeds.contains(b)) {
                                dndBeds.add(b);
                            }
                            dndCounter++;
                        }
                    }
                    if (b.room.getMainServiceSwitch().checkout != null) {
                        if (b.room.getMainServiceSwitch().checkout.getCurrent()) {
                            if (!checkoutBeds.contains(b)) {
                                checkoutBeds.add(b);
                            }
                            checkoutCounter++;
                        }
                    }
                }
//                if (b.room.getPowerModule() != null) {
//                    if (b.room.getPowerModule().dp1 != null && b.room.getPowerModule().dp2 != null) {
//                        if (b.room.getPowerModule().dp1.getCurrent() && b.room.getPowerModule().dp2.getCurrent()) {
//                            powerOnBeds.add(b);
//                        }
//                        else if (b.room.getPowerModule().dp1.getCurrent() && !b.room.getPowerModule().dp2.getCurrent()) {
//                            powerCardBeds.add(b);
//                        }
//                        else if (!b.room.getPowerModule().dp1.getCurrent() && !b.room.getPowerModule().dp2.getCurrent()) {
//                            powerOffBeds.add(b);
//                        }
//                    }
//                    else {
//                        Log.d("PowerNull",b.room.RoomNumber+" dp");
//                    }
//                }
//                else {
//                    Log.d("PowerNull",b.room.RoomNumber+" module");
//                }
            }
        }
        TextView cleanupTv = act.findViewById(R.id.textView15);
        TextView laundryTv = act.findViewById(R.id.textView4);
        TextView checkoutTv = act.findViewById(R.id.textView14);
        TextView dndTv = act.findViewById(R.id.textView13);
        cleanupTv.setText(String.valueOf(cleanupCounter));
        laundryTv.setText(String.valueOf(laundryCounter));
        checkoutTv.setText(String.valueOf(checkoutCounter));
        dndTv.setText(String.valueOf(dndCounter));
    }

    public static void setCleanupLists(Activity act) {
        cleanupCounter = 0;
        cleanupBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                Log.d("roomCleanup","suite "+b.suite.SuiteNumber);
                if (b.suite.getMainServiceSwitch() != null) {
                    Log.d("roomCleanup","service not null");
                    if (b.suite.getMainServiceSwitch().cleanup != null) {
                        Log.d("roomCleanup","cleanup not null");
                        if (b.suite.getMainServiceSwitch().cleanup.getCurrent()) {
                            Log.d("roomCleanup","current on");
                            if (!cleanupBeds.contains(b)) {
                                cleanupBeds.add(b);
                            }
                            cleanupCounter++;
                        }
                    }
                }
            }
            if (b.isRoom()) {
                Log.d("roomCleanup","room "+b.room.RoomNumber);
                if (b.room.getMainServiceSwitch() != null) {
                    Log.d("roomCleanup","service not null");
                    if (b.room.getMainServiceSwitch().cleanup != null) {
                        Log.d("roomCleanup","cleanup not null");
                        if (b.room.getMainServiceSwitch().cleanup.getCurrent()) {
                            Log.d("roomCleanup","current on");
                            if (!cleanupBeds.contains(b)) {
                                cleanupBeds.add(b);
                            }
                            cleanupCounter++;
                        }
                    }
                }
            }
        }
        TextView cleanupTv = act.findViewById(R.id.textView15);
        cleanupTv.setText(String.valueOf(cleanupCounter));
    }
    public static void setLaundryLists(Activity act) {
        laundryCounter = 0;
        laundryBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                if (b.suite.getMainServiceSwitch() != null) {
                    if (b.suite.getMainServiceSwitch().laundry != null) {
                        if (b.suite.getMainServiceSwitch().laundry.getCurrent()) {
                            if (!laundryBeds.contains(b)) {
                                laundryBeds.add(b);
                            }
                            laundryCounter++;
                        }
                    }
                }
            }
            if (b.isRoom()) {
                if (b.room.getMainServiceSwitch() != null) {
                    if (b.room.getMainServiceSwitch().laundry != null) {
                        if (b.room.getMainServiceSwitch().laundry.getCurrent()) {
                            if (!laundryBeds.contains(b)) {
                                laundryBeds.add(b);
                            }
                            laundryCounter++;
                        }
                    }
                }
            }
        }
        TextView laundryTv = act.findViewById(R.id.textView4);
        laundryTv.setText(String.valueOf(laundryCounter));
    }
    public static void setCheckoutLists(Activity act) {
        checkoutCounter = 0;
        checkoutBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                if (b.suite.getMainServiceSwitch() != null) {
                    if (b.suite.getMainServiceSwitch().checkout != null) {
                        if (b.suite.getMainServiceSwitch().checkout.getCurrent()) {
                            if (!checkoutBeds.contains(b)) {
                                checkoutBeds.add(b);
                            }
                            checkoutCounter++;
                        }
                    }
                }
            }
            if (b.isRoom()) {
                if (b.room.getMainServiceSwitch() != null) {
                    if (b.room.getMainServiceSwitch().checkout != null) {
                        if (b.room.getMainServiceSwitch().checkout.getCurrent()) {
                            if (!checkoutBeds.contains(b)) {
                                checkoutBeds.add(b);
                            }
                            checkoutCounter++;
                        }
                    }
                }
            }
        }
        TextView checkoutTv = act.findViewById(R.id.textView14);
        checkoutTv.setText(String.valueOf(checkoutCounter));
    }
    public static void setDndLists(Activity act) {
        dndCounter = 0;
        dndBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                if (b.suite.getMainServiceSwitch() != null) {
                    if (b.suite.getMainServiceSwitch().dnd != null) {
                        if (b.suite.getMainServiceSwitch().dnd.getCurrent()) {
                            if (!dndBeds.contains(b)) {
                                dndBeds.add(b);
                            }
                            dndCounter++;
                        }
                    }
                }
            }
            if (b.isRoom()) {
                if (b.room.getMainServiceSwitch() != null) {
                    if (b.room.getMainServiceSwitch().dnd != null) {
                        if (b.room.getMainServiceSwitch().dnd.getCurrent()) {
                            if (!dndBeds.contains(b)) {
                                dndBeds.add(b);
                            }
                            dndCounter++;
                        }
                    }
                }
            }
        }
        TextView dndTv = act.findViewById(R.id.textView13);
        dndTv.setText(String.valueOf(dndCounter));
    }

    void setPowerCounters() {
        powerOnBeds.clear();
        powerOffBeds.clear();
        powerCardBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                if (b.suite.getPowerModule() != null) {
                    if (b.suite.getPowerModule().dp1 != null && b.suite.getPowerModule().dp2 != null) {
                        if (b.suite.getPowerModule().dp1.getCurrent() && b.suite.getPowerModule().dp2.getCurrent()) {
                            powerOnBeds.add(b);
                        }
                        else if (b.suite.getPowerModule().dp1.getCurrent() && !b.suite.getPowerModule().dp2.getCurrent()) {
                            powerCardBeds.add(b);
                        }
                        else if (!b.suite.getPowerModule().dp1.getCurrent() && !b.suite.getPowerModule().dp2.getCurrent()) {
                            powerOffBeds.add(b);
                        }
                    }
                    else {
                        Log.d("PowerNull",b.suite.SuiteNumber+" dp");
                    }
                }
                else {
                    Log.d("PowerNull",b.suite.SuiteNumber+" module");
                }
            }
            if (b.isRoom()) {
                if (b.room.getPowerModule() != null) {
                    if (b.room.getPowerModule().dp1 != null && b.room.getPowerModule().dp2 != null) {
                        if (b.room.getPowerModule().dp1.getCurrent() && b.room.getPowerModule().dp2.getCurrent()) {
                            powerOnBeds.add(b);
                        }
                        else if (b.room.getPowerModule().dp1.getCurrent() && !b.room.getPowerModule().dp2.getCurrent()) {
                            powerCardBeds.add(b);
                        }
                        else if (!b.room.getPowerModule().dp1.getCurrent() && !b.room.getPowerModule().dp2.getCurrent()) {
                            powerOffBeds.add(b);
                        }
                    }
                    else {
                        Log.d("PowerNull",b.room.RoomNumber+" dp");
                    }
                }
                else {
                    Log.d("PowerNull",b.room.RoomNumber+" module");
                }
            }
        }
    }

    void setOfflineCounters() {
        offlineBeds.clear();
        for (Bed b:beds) {
            if (b.isSuite()) {
                if (b.suite.getSuiteGateway() != null) {
                    if (!b.suite.getSuiteGateway().currentOnline) {
                        offlineBeds.add(b);
                    }
                }
            }
            if (b.isRoom()) {
                if (b.room.getRoomGateway() != null) {
                    if (!b.room.getRoomGateway().currentOnline) {
                        offlineBeds.add(b);
                    }
                }
            }
        }
    }

    void setKeepAppAliveAlarm(Context context) {
        Intent intent = new Intent(context, checkWorkingReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000*60*5) , pendingIntent);
        Log.d("workingAlarm","alarm set");
    }

    void refreshApp() {
        Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(i);
        Process.killProcess(Process.myPid());
    }

    void setRefreshTimer() {
        Log.d("closeTimer","start");
        Timer terminateTimer0 = new Timer();
        terminateTimer0.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("closeTimer","terminate");
                Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
                if (i != null) {
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(i);
                act.finishAffinity();
            }
        },1000 * 60 * 5);
    }

    List<Bed> setBeds(List<Suite> suites) {
        List<Bed> beds = new ArrayList<>();
        for (Suite s:suites) {
            beds.add(new Bed().setBed(s));
            for (Room r:s.RoomsList) {
                beds.add(new Bed().setBed(r));
            }
        }
        return beds;
    }

    void refreshSuite(int index) {
        if (adapter.beds == beds) {
            adapter.notifyItemChanged(index);
        }
        else if(adapter.beds == cleanupBeds) {
            showRoomsRecycler(cleanupBeds);
        }
        else if (adapter.beds == laundryBeds) {
            showRoomsRecycler(laundryBeds);
        }
        else if (adapter.beds == dndBeds) {
            showRoomsRecycler(dndBeds);
        }
        else if (adapter.beds == checkoutBeds) {
            showRoomsRecycler(checkoutBeds);
        }
    }

    void refreshRoom(int index) {
        if (adapter.beds == beds) {
            adapter.notifyItemChanged(index);
        }
        else if(adapter.beds == cleanupBeds) {
            Log.d("ServiceProb","cleanup list");
            showRoomsRecycler(cleanupBeds);
        }
        else if (adapter.beds == laundryBeds) {
            showRoomsRecycler(laundryBeds);
        }
        else if (adapter.beds == dndBeds) {
            showRoomsRecycler(dndBeds);
        }
        else if (adapter.beds == checkoutBeds) {
            showRoomsRecycler(checkoutBeds);
        }
    }

    public static void refreshCleanup(Activity act) {
        RecyclerView cleanupRecycler = act.findViewById(R.id.cleanupRecycler);
        cleanupAdapter = new RoomOrder_Adapter(cleanupBeds);
        cleanupRecycler.setAdapter(cleanupAdapter);
    }

    public static void refreshLaundry(Activity act) {
        RecyclerView laundryRecycler = act.findViewById(R.id.laundryRecycler);
        laundryAdapter = new RoomOrder_Adapter(laundryBeds);
        laundryRecycler.setAdapter(laundryAdapter);
    }

    public static void refreshCheckout(Activity act) {
        RecyclerView checkoutRecycler = act.findViewById(R.id.checkoutRecycler);
        checkoutAdapter = new RoomOrder_Adapter(checkoutBeds);
        checkoutRecycler.setAdapter(checkoutAdapter);
    }

    public static void refreshDND(Activity act) {
        RecyclerView dndRecycler = act.findViewById(R.id.dndRecycler);
        dndAdapter = new RoomOrder_Adapter(dndBeds);
        dndRecycler.setAdapter(dndAdapter);
    }

    void refreshPower() {
        RecyclerView powerOnRecycler = findViewById(R.id.powerOnRecycler);
        powerOnAdapter = new RoomPower_Adapter(powerOnBeds);
        powerOnRecycler.setAdapter(powerOnAdapter);
        RecyclerView powerCardRecycler = findViewById(R.id.powerCardRecycler);
        powerCardAdapter = new RoomPower_Adapter(powerCardBeds);
        powerCardRecycler.setAdapter(powerCardAdapter);
        RecyclerView powerOffRecycler = findViewById(R.id.powerOffRecycler);
        powerOffAdapter = new RoomPower_Adapter(powerOffBeds);
        powerOffRecycler.setAdapter(powerOffAdapter);
    }

    void refreshOffline() {
        RecyclerView offlineRecycler = findViewById(R.id.offlineRecycler);
        offlineAdapter = new RoomOffline_Adapter(offlineBeds);
        offlineRecycler.setAdapter(offlineAdapter);
    }

    public boolean trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            return true;
        } catch (Exception e) {
            new MessageDialog(e.getMessage(),"error",context);
            return false;
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        }
        else {
            return false;
        }
    }
}