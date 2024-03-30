package com.syriasoft.hotelservices;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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
import com.squareup.picasso.Picasso;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.TYDevicePublishModeEnum;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FullscreenActivity extends AppCompatActivity {

    public Activity act  ;
    Button  GymBtn,curtainBtn,ShowAc,ShowMiniBar;
    static public DatabaseReference  RoomDevicesRef,ServiceUsers,myRefLogo,myRefCheckOutDuration,myRefCheckInDuration,myRefToken,myRefDoorWarning, myRefSetPointInterval, myRefSetPoint,myRefFacility,myRefRoomServiceText,myRefServiceSwitch,myRefPowerSwitch, myRefId, myRefRorS,myRefTemp, myRefDep,myRefStatus,myRefReservation ,myRefPower ,myRefCurtain , myRefDoor ,myRefRoomStatus , Room , myRefDND, myRefTabStatus, myRefLaundry , myRefCleanup , myRefRoomService , myRefSos , myRefRestaurant , myRefCheckout ,myRefDoorSensor,myRefMotionSensor,myRefCurtainSwitch,myRefSwitch1,myRefSwitch2,myRefSwitch3,myRefSwitch4,myRefThermostat,myRefLock;
    static boolean DNDStatus=false,LaundryStatus=false,CleanupStatus=false,RoomServiceStatus,SosStatus,RestaurantStatus,CheckoutStatus = false;
    static String roomServiceOrder ="";
    static int  RoomOrSuite =1 , ID ,CURRENT_ROOM_STATUS=0 ,RESERVATION =0 ;
    static  boolean  Switch1Status=false,Switch2Status=false,Switch3Status=false,Switch4Status=false,Switch5Status=false,Switch6Status=false,Switch7Status=false,Switch8Status=false;
    static RESERVATION THE_RESERVATION;
    static OrderDB order ;
    RecyclerView LAUNDRY_MENU, MINIBAR_MENU;
    private List<FACILITY> Facilities;
    private List<LAUNDRY> Laundries ;
    private List<MINIBAR> Minibar ;
    public static List<RESTAURANT_UNIT> Restaurants ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    static ROOM THE_ROOM;
    LinearLayout homeBtn,ServicesBtn,RestaurantBtn,ShowLighting,showAc,LaundryBtn,CheckOutBtn,CleanUpBtn,SOSBtn,RoomServiceBtn,mainLayout,laundryPriceList,minibarPriceList,lightsLayout,serviceLayout,DNDBtn,OpenDoor ;
    static List<Activity> RestaurantActivities ;
    static Resources RESOURCES ;
    private Runnable backHomeThread ;
    static long x = 0 ;
    private Handler H ;
    static String LOGO ;
    public static List<ServiceEmps> EmpS;
    public static LightingDB lightsDB ;
    public static int sosCounter = 1 , roomServiceCounter = 1 ;
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    WindowInsetsControllerCompat windowInsetsController;
    List<SceneBean> SCENES,MY_SCENES,LivingMood,SleepMood,WorkMood,RomanceMood,ReadMood,MasterOffMood ;
    private static RequestQueue FirebaseTokenRegister ;
    DeviceBean Living,Sleep,Work,Romance,Read,MasterOff;
    boolean isPaused;
    public static List<String> IMAGES ;
    static boolean BigScreen = true;
    Timer serviceButtonsTimer;
    int serviceButtonsTimerIndex = 0;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("screenModel",Build.MODEL);
        if (Build.MODEL.equals("YC-55P") || Build.MODEL.equals("YS4B")) {
            setContentView(R.layout.fullscreen_small);
            BigScreen = false ;
        }
        else {
            setContentView(R.layout.activity_fullscreen);
            BigScreen = true;
        }
        Log.d("screenModel",Build.MODEL+BigScreen);
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
        setActivity();
        order = new OrderDB(act);
        order.removeOrder();
        getFirebaseToken();
        setRefreshDevices();
        act.startLockTask();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
        requestTaskLock();
        prepareLights();
        getSceneBGs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //act.finish();
    }

    private void requestTaskLock() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName deviceAdminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);
        if (dpm.isDeviceOwnerApp(this.getPackageName())) {
            String[] packages = {this.getPackageName()};
            dpm.setLockTaskPackages(deviceAdminReceiver, packages);
            if (dpm.isLockTaskPermitted(this.getPackageName())) {
                startLockTask();
            } else {
                Toast.makeText (this, "Lock screen is not permitted", Toast.LENGTH_SHORT).show ();
            }
        } else {
            Toast.makeText (this, "App is not a device administrator", Toast.LENGTH_SHORT).show ();
        }
    }

    void setActivity() {
        act = this ;
        MyApp.mainActivity.add(act);
        THE_ROOM = MyApp.Room ;
        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
        RestaurantBtn = findViewById(R.id.Restaurant);
        GymBtn = findViewById(R.id.button6);
        LaundryBtn = findViewById(R.id.laundry_btn);
        CleanUpBtn = findViewById(R.id.cleanup_btn);
        CheckOutBtn = findViewById(R.id.checkout_btn);
        DNDBtn = findViewById(R.id.dndBtn);
        SOSBtn = findViewById(R.id.sosBtn);
        OpenDoor = findViewById(R.id.Door_Button);
        ServicesBtn = findViewById(R.id.ServicesBtn_cardview);
        showAc = findViewById(R.id.ACBtn_cardview);
        RoomServiceBtn = findViewById(R.id.roomservice_btn);
        curtainBtn = findViewById(R.id.curtain);
        homeBtn = findViewById(R.id.home_Btn);
        homeBtn.setVisibility(View.GONE);
        serviceLayout = findViewById(R.id.Service_Btns);
        lightsLayout = findViewById(R.id.lightingLayout);
        mainLayout = findViewById(R.id.main_layout);
        LAUNDRY_MENU = findViewById(R.id.laundryMenu_recycler);
        MINIBAR_MENU = findViewById(R.id.minibar_recycler);
        setArrayLists();
        LinearLayoutManager laundryManager = new LinearLayoutManager(act, RecyclerView.HORIZONTAL, false);
        final GridLayoutManager manager1 = new GridLayoutManager(this,4);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        LAUNDRY_MENU.setLayoutManager(laundryManager);
        MINIBAR_MENU.setLayoutManager(manager1);
        ShowLighting = findViewById(R.id.LightsBtn_cardview);
        ShowAc = findViewById(R.id.hideShowAcLayout);
        minibarPriceList = findViewById(R.id.minibar_priceList);
        laundryPriceList = findViewById(R.id.laundry_pricelist);
        ShowMiniBar = findViewById(R.id.hideShowMinibarLayout);
        setFirebaseReferences();
        TextView RoomNumber = findViewById(R.id.RoomNumber_MainScreen);
        RoomNumber.setText(String.valueOf(MyApp.Room.RoomNumber));
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        lightsDB = new LightingDB(act) ;
        backHomeThread = new Runnable() {
            @Override
            public void run() {
                H = new Handler();
                x = x+1000 ;
                Log.d("backThread" , x+"");
                H.postDelayed(this,1000);
                if (x >= 20000){
                    LinearLayout v = findViewById(R.id.home_Btn);
                    runOnUiThread(() -> {
                        backToMain(v);
                        H.removeCallbacks(backHomeThread);
                        x=0;
                    });
                }
            }
        };
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        }
        if (BigScreen) {
            setServiceButtonImage();
        }
        getServiceUsersFromFirebase();
        getFacilities();
        setActivityActions();
        setFireRoomListeners();
        blink();
        KeepScreenFull();
        setTheAcLayout();
        setLockButton();
        setBalloons();
    }

    void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            String token = task.getResult();
            sendRegistrationToServer(token);
            myRefToken.setValue(token);
        });
        Timer t = new Timer() ;
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("registerToken", "token timer started");
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    sendRegistrationToServer(token);
                    myRefToken.setValue(token);
                });
            }},1000*60, 1000 * 60 * 60);
    }

    void setRefreshDevices() {
        Timer refreshTimer = new Timer() ;
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("refreshDevices","timer started");
                refreshDevices();
            }
        },1000*60,1000*60*60*6);
    }

    void setBalloons() {
//        ServicesBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("طلب الخدمات \nServices")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
//        LightsBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("تشغيل واغلاق الاضاءة \n Turn Lights On/Off")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
//        ACBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("التحكم بالمكيف \n Control AC")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
//        RestaurantBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("للطلب من المطعم \nRestaurant")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
//        SOSBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("لحالات الطوارئ \n Emergency")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
//        DNDBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("وضع عدم الازعاج \n Don't Disturb ")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
//        DoorBalloon = new Balloon.Builder(getApplicationContext())
//                .setArrowSize(30)
//                .setArrowOrientation(ArrowOrientation.BOTTOM)
//                .setIsVisibleArrow(true)
//                .setArrowPosition(0.5f)
//                .setWidthRatio(0.2f)
//                .setHeight(200)
//                .setTextSize(30f)
//                .setCornerRadius(8f)
//                .setAlpha(0.8f)
//                .setText("فتح قفل باب الغرفة \n Open Door Lock")
//                .setTextColor(act.getColor(R.color.colorPrimary))
//                .setBackgroundColor(act.getColor(R.color.light_blue_A200))
//                .setBalloonAnimation(BalloonAnimation.FADE)
//                .setAutoDismissDuration(3000)
//                .build();
    }

    void setFirebaseReferences() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ServiceUsers = database.getReference(MyApp.ProjectName+"ServiceUsers");
        Room = database.getReference(MyApp.ProjectName+"/B"+MyApp.Room.Building+"/F"+MyApp.Room.Floor+"/R"+MyApp.Room.RoomNumber);
        myRefLaundry = Room.child("Laundry");
        myRefCleanup = Room.child("Cleanup");
        myRefRoomService = Room.child("RoomService");
        myRefRoomServiceText = Room.child("RoomServiceText");
        myRefSos = Room.child("SOS");
        myRefRestaurant = Room.child("Restaurant");
        myRefCheckout = Room.child("Checkout");
        myRefRoomStatus = Room.child("roomStatus");
        myRefStatus = Room.child("Status");
        myRefDND = Room.child("DND");
        myRefDoorSensor = Room.child("DoorSensor");
        myRefMotionSensor = Room.child("MotionSensor");
        myRefThermostat = Room.child("Thermostat");
        myRefCurtainSwitch = Room.child("CurtainSwitch");
        myRefLock = Room.child("Lock");
        myRefSwitch1 = Room.child("Switch1");
        myRefSwitch2 = Room.child("Switch2");
        myRefSwitch3 = Room.child("Switch3");
        myRefSwitch4 = Room.child("Switch4");
        myRefDoor = Room.child("doorStatus");
        myRefCurtain = Room.child("curtainStatus");
        myRefPower = Room.child("powerStatus");
        myRefTabStatus = Room.child("Tablet");
        myRefReservation = Room.child("ReservationNumber");
        myRefDep = Room.child("dep");
        myRefTemp = Room.child("temp");
        myRefRorS=Room.child("SuiteStatus");
        myRefId = Room.child("id");
        myRefPowerSwitch = Room.child("PowerSwitch");
        myRefServiceSwitch = Room.child("ServiceSwitch");
        myRefFacility = Room.child("Facility");
        myRefSetPoint = Room.child("TempSetPoint");
        myRefSetPointInterval = Room.child("SetPointInterval");
        myRefDoorWarning = Room.child("DoorWarning");
        myRefCheckInDuration = Room.child("CheckInModeTime");
        myRefCheckOutDuration = Room.child("CheckOutModeTime");
        myRefLogo = Room.child("Logo");
        myRefToken = Room.child("token");
        RoomDevicesRef = database.getReference(MyApp.ProjectName+"Devices").child(String.valueOf(THE_ROOM.RoomNumber));
    }

    void setArrayLists() {
        Facilities = new ArrayList<>();
        RestaurantActivities = new ArrayList<>();
        EmpS = new ArrayList<>();
        Restaurants = new ArrayList<>();
        Minibar = new ArrayList<>();
        RESOURCES = getResources();
        Laundries = new ArrayList<>();
        IMAGES = new ArrayList<>();
        SCENES = new ArrayList<>();
        MY_SCENES = new ArrayList<>();
        LivingMood = new ArrayList<>();
        SleepMood = new ArrayList<>();
        WorkMood = new ArrayList<>();
        RomanceMood = new ArrayList<>();
        ReadMood = new ArrayList<>();
        MasterOffMood = new ArrayList<>();
    }

    void setActivityActions() {
        TextView roomNumber = findViewById(R.id.RoomNumber_MainScreen);
        ServicesBtn.setOnClickListener(v -> {
            hideMainBtnS();
            ImageView cleanupImage = findViewById(R.id.imageView19);
            ImageView laundryImage = findViewById(R.id.imageView16);
            ImageView roomserviceImage = findViewById(R.id.imageView8);
            if (BigScreen) {
                cleanupImage.setImageAlpha(1);
                laundryImage.setImageAlpha(1);
                roomserviceImage.setImageAlpha(1);
            }
        });
        serviceLayout.setOnClickListener(v -> x=0);
        lightsLayout.setOnClickListener(v -> x=0);
        mainLayout.setOnClickListener(v -> x=0);
        mainLayout.setOnDragListener((view, dragEvent) -> {
            x=0;
            return false;
        });
        showAc.setOnClickListener(v -> {
            createAcLayout();
            x=0;
        });
        laundryPriceList.setOnClickListener(v -> {
        });
        ShowMiniBar.setOnClickListener(v -> {
            LinearLayout Services = findViewById(R.id.ServicesLayout);
            LinearLayout Lighting = findViewById(R.id.lightingLayout);
            LinearLayout AcLayout = findViewById(R.id.ac_layout);
            LinearLayout LaundryLayout = findViewById(R.id.Laundry_layout);
            LinearLayout MinibarLayout = findViewById(R.id.Minibar_layout);
            MinibarLayout.setVisibility(View.VISIBLE);
            LaundryLayout.setVisibility(View.GONE);
            AcLayout.setVisibility(View.GONE);
            Lighting.setVisibility(View.GONE);
            Services.setVisibility(View.GONE);
            if (Minibar.size()>0) {
                getMiniBarMenu(Minibar.get(0).id);
            }
        });
        roomNumber.setOnLongClickListener(v -> {

            Dialog  dd = new Dialog(act);
            dd.setContentView(R.layout.logout_of_room_dialog);
            Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = dd.findViewById(R.id.messageDialog_ok);
            EditText password = dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(v13 -> dd.dismiss());
            lock.setOnClickListener(v12 -> {
                final LoadingDialog loading = new LoadingDialog(act);
                final String pass = password.getText().toString() ;
                StringRequest re = new StringRequest(Request.Method.POST, MyApp.ProjectURL + "users/loginProject", response -> {
                    loading.stop();
                    if (response != null) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            if (resp.getString("result").equals("success")) {
                                dd.dismiss();
                                logout();
                            }
                            else {
                                Toast.makeText(act,"Logout Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(act,"Logout Failed " + e,Toast.LENGTH_LONG).show();
                        }
                    }
                }, error -> {
                    loading.stop();
                    new messageDialog(error.toString(),"Failed",act);
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> par = new HashMap<>();
                        par.put( "password" , pass ) ;
                        par.put( "project_name" ,MyApp.ProjectName) ;
                        return par;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            });
            dd.show();
            return false;
        });
        LaundryBtn.setOnClickListener(view -> {
            if (THE_ROOM.getSERVICE1_B() != null) {
                if (THE_ROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)) != null) {
                    Log.d("serviceClick" ,LaundryStatus +" laundry " );
                    if (LaundryStatus) {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, false, MyApp.ProjectVariables.laundryButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" , "success laundry ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" laundry ");
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                    else {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, true, MyApp.ProjectVariables.laundryButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" , "success laundry ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" laundry ");
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                }
                else {
                    if (LaundryStatus) {
                        myRefLaundry.setValue(0);
                    }
                    else {
                        myRefLaundry.setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                }
            }
            x=0;
        });
        CleanUpBtn.setOnClickListener(view -> {
            if (THE_ROOM.getSERVICE1_B() != null) {
                if (THE_ROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)) != null) {
                    Log.d("serviceClick" ,CleanupStatus +" cleanup " );
                    if (CleanupStatus) {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, false, MyApp.ProjectVariables.cleanupButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" ," cleanup success ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" cleanup " );
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                    else {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, true, MyApp.ProjectVariables.cleanupButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" ," cleanup success ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" cleanup " );
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                }
            }
            x=0;
        });
        CheckOutBtn.setOnClickListener(view -> {
            if (THE_ROOM.getSERVICE1_B() != null) {
                if (THE_ROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                    Log.d("serviceClick" ,CheckoutStatus +" checkout " );
                    if (CheckoutStatus) {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, false, MyApp.ProjectVariables.checkoutButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" ,"success checkout ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" checkout " );
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                    else {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, true, MyApp.ProjectVariables.checkoutButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" ,"success checkout ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" checkout " );
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                }
                else {
                    if (CheckoutStatus) {
                        myRefCheckout.setValue(0);
                    }
                    else {
                        myRefCheckout.setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                }
            }
            else {
                if (CheckoutStatus) {
                    myRefCheckout.setValue(0);
                }
                else {
                    myRefCheckout.setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
            }
            x=0;
        });
        DNDBtn.setOnClickListener(view -> {
            if (THE_ROOM.getSERVICE1_B() != null) {
                if (THE_ROOM.getSERVICE1_B().getDps().get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                    Log.d("serviceClick" ,DNDStatus +" checkout " );
                    if (DNDStatus) {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, false, MyApp.ProjectVariables.dndButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" ,"success dnd ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" dnd ");
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                    else {
                        clickButtonSwitch(THE_ROOM.getSERVICE1_B().devId, true, MyApp.ProjectVariables.dndButton, new CallbackResult() {
                            @Override
                            public void onSuccess() {
                                Log.d("serviceClick" ,"success dnd ");
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d("serviceClick" ,error +" dnd ");
                                new messageDialog(error+" ","failed",act);
                            }
                        });
                    }
                }
            }
        });
        SOSBtn.setOnClickListener(v -> {
            if (CURRENT_ROOM_STATUS == 2) {
                if (!SosStatus) {
                    final Dialog d = new Dialog(act);
                    d.setContentView(R.layout.confermation_dialog);
                    Window w = d.getWindow();
                    w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText(getResources().getString(R.string.sendSOSOrder));
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(v1 -> d.dismiss());
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(v14 -> {
                        SosStatus = true ;
                        sosOn(act);
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        myRefSos.setValue(c.getTimeInMillis());
                        d.dismiss();
                        String url = MyApp.ProjectURL + "reservations/addSOSOrder";
                        StringRequest addOrder = new StringRequest(Request.Method.POST, url , response -> Log.d("sosResp" , response), error -> Log.d("sosResp" , error.toString())) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String,String> params = new HashMap<>();
                                params.put("room_id" ,String.valueOf(THE_ROOM.id));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(act).add(addOrder);
                    });
                    d.show();
                }
                else {
                    SosStatus = false ;
                    sosOff(act);
                    myRefSos.setValue(0);
                    String url = MyApp.ProjectURL + "reservations/cancelServiceOrderControlDevice"+sosCounter;
                    StringRequest removeOrder = new StringRequest(Request.Method.POST, url , response -> Log.d("sosResp" , response), error -> {
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String,String> params = new HashMap<>();
                            params.put("room_id" , String.valueOf(MyApp.Room.id));
                            params.put("order_type" , "SOS");
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(removeOrder);
                    sosCounter++ ;
                    if (sosCounter == 5) {
                        sosCounter = 1 ;
                    }
                }
            }
            else {
                ToastMaker.MakeToast("This Room Is Vacant" , act);
            }
        });
        ShowLighting.setOnLongClickListener(v -> {
            Dialog  dd = new Dialog(act);
            dd.setContentView(R.layout.logout_of_room_dialog);
            Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = dd.findViewById(R.id.messageDialog_ok);
            TextView title = dd.findViewById(R.id.textView2);
            title.setText(getResources().getString(R.string.lights));
            TextView message = dd.findViewById(R.id.confermationDialog_Text);
            message.setText(getResources().getString(R.string.lights));
            EditText password = dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(v15 -> dd.dismiss());
            lock.setOnClickListener(v16 -> {
                LoadingDialog loading = new LoadingDialog(act);
                StringRequest re = new StringRequest(Request.Method.POST, MyApp.ProjectURL + "users/loginProject", response -> {
                    loading.stop();
                    if (response != null) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            if (resp.getString("result").equals("success")) {
                                dd.dismiss();
                                Intent i = new Intent(act,LightingControl.class);
                                startActivity(i);
                            }
                            else {
                                Toast.makeText(act,"Logout Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(act,"Logout Failed " + e,Toast.LENGTH_LONG).show();
                        }
                    }
                }, error -> {
                    loading.stop();
                    new messageDialog(error.toString(),"Failed",act);
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> par = new HashMap<>();
                        par.put( "password" , password.getText().toString() ) ;
                        par.put( "project_name" ,MyApp.ProjectName) ;
                        return par;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            });
            dd.show();
            return false;
        });
        TextView text = findViewById(R.id.textView36);
        text.setOnLongClickListener(view -> {
            executeCommand();
            return false;
        });
    }

    public void setFireRoomListeners() {
        myRefLogo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && !snapshot.getValue().toString().isEmpty() ){
                    LOGO = snapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        dndOn(act);
                        DNDStatus = true ;
                    }
                    else {
                        dndOff(act);
                        DNDStatus = false ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefSos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        SosStatus = true ;
                        sosOn(act);
                    }
                    else {
                        SosStatus = false ;
                        sosOff(act);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefLaundry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        LaundryStatus = true ;
                        laundryOn(act);
                    }
                    else {
                        LaundryStatus = false ;
                        laundryOff(act);
                    }
                }
                xxx();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCleanup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString())>0) {
                        cleanupOn(act);
                        CleanupStatus = true ;
                    }
                    else {
                        cleanupOff(act);
                        CleanupStatus = false ;
                    }
                }
                xxx();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        CheckoutStatus = true ;
                        checkoutOn(act);
                    }
                    else {
                        checkoutOff(act);
                        CheckoutStatus = false ;
                    }
                }
                xxx();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if ( !snapshot.getValue().toString().equals("0") ) {
                        roomServiceOn(act);
                        RoomServiceStatus = true ;
                    }
                    else {
                        RoomServiceStatus = false ;
                        roomServiceOff(act);
                    }
                }
                xxx();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        THE_ROOM.Restaurant = 10 ;
                        RestaurantStatus = true ;
                        restaurantOn(act);
                    }
                    else {
                        THE_ROOM.Restaurant = 0 ;
                        restaurantOff(act);
                        RestaurantStatus = false ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefRorS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Integer.parseInt(snapshot.getValue().toString()) == 1 ) {
                        RoomOrSuite = 1 ;
                    }
                    else if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                        RoomOrSuite = 2 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefReservation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Integer.parseInt(snapshot.getValue().toString()) > 0 ) {
                        RESERVATION = Integer.parseInt(snapshot.getValue().toString()) ;
                        getReservation();
                    }
                    else {
                        RESERVATION = 0 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    ID = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (snapshot.getValue().toString().equals("2")) {
                        TextView text = findViewById(R.id.textView36);
                        text.setText(getResources().getString(R.string.welcomeRoom));
                    }
                    else if (snapshot.getValue().toString().equals("1")) {
                        TextView fName = findViewById(R.id.client_Name);
                        TextView text = findViewById(R.id.textView36);
                        text.setText("");
                        fName.setText(getResources().getString(R.string.roomVacant));
                    }
                    else if (snapshot.getValue().toString().equals("3")) {
                        TextView fName = findViewById(R.id.client_Name);
                        TextView text = findViewById(R.id.textView36);
                        TextView dateS = findViewById(R.id.check_In_Date);
                        TextView dateE = findViewById(R.id.check_out_Date);
                        text.setText("");
                        dateS.setText("");
                        dateE.setText("");
                        fName.setText(getResources().getString(R.string.roomIsUnready));
                    }
                    else if (snapshot.getValue().toString().equals("4")) {
                        TextView fName = findViewById(R.id.client_Name);
                        TextView text = findViewById(R.id.textView36);
                        text.setText("");
                        fName.setText(getResources().getString(R.string.roomIsOutOfService));
                    }
                    CURRENT_ROOM_STATUS = Integer.parseInt( snapshot.getValue().toString() );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void roomServiceShowDialog(View view) {
            if (CURRENT_ROOM_STATUS == 2) {
                if (RoomServiceStatus) {
                    removeRoomServiceOrderInDataBase(act);
                }
                else {
                    addRoomServiceOrderInDataBase(act);
                }
            }
            else {
                ToastMaker.MakeToast("This Room Is Vacant" , act);
            }
        x=0;
    }

    public void goToRestaurant(View view) {
        if (CURRENT_ROOM_STATUS == 2) {
            if (THE_ROOM.Restaurant > 0) {
                new messageDialog("لديك طلب من المطعم مسبقا","Tou have Order",act);
            }
            else {
                Intent i = new Intent(act , RESTAURANTS.class);
                startActivity(i);
            }
        }
        else {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
    }

    public void getReservation() {
        if (RoomOrSuite == 1) {
            String url = MyApp.ProjectURL + "reservations/getRoomReservation";
            StringRequest getReservationRe = new StringRequest(Request.Method.POST, url, response -> {
                Log.d("reservationResp",response);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        JSONObject row = result.getJSONObject("reservation");
                        THE_RESERVATION = new RESERVATION(row.getInt("id"),row.getInt("RoomNumber"),row.getInt("ClientId"),row.getInt("Status"),
                                row.getInt("RoomOrSuite"),row.getInt("MultiRooms"),row.getString("AddRoomNumber"),row.getString("AddRoomId"),row.getString("StartDate"),
                                row.getInt("Nights"),row.getString("EndDate"),row.getInt("Hotel"),row.getInt("BuildingNo"),row.getInt("Floor"),row.getString("ClientFirstName"),row.getString("ClientLastName"),row.getString("IdType"),
                                row.getInt("IdNumber"),row.getInt("MobileNumber"),row.getString("Email"),row.getInt("Rating"));
                        TextView fName = findViewById(R.id.client_Name);
                        TextView checkIn = findViewById(R.id.check_In_Date);
                        TextView checkout = findViewById(R.id.check_out_Date);
                        fName.setText(String.format("%s %s", THE_RESERVATION.ClientFirstName, THE_RESERVATION.ClientLastName));
                        checkIn.setText(String.format("in:%s", THE_RESERVATION.StartDate));
                        checkout.setText(String.format("out:%s", THE_RESERVATION.EndDate));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    new messageDialog(e.getMessage(),"Failed to get reservation",act);
                }
            }, error -> new messageDialog(error.toString(),"Failed to get reservation",act)) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("reservation_id", String.valueOf(RESERVATION));
                    Params.put("room_number" , String.valueOf(THE_ROOM.RoomNumber));
                    return Params;
                }
            };
            if (FirebaseTokenRegister == null) {
                FirebaseTokenRegister = Volley.newRequestQueue(act) ;
            }
            FirebaseTokenRegister.add(getReservationRe);
        }
        else {
            String url = MyApp.ProjectURL + "reservations/getSuiteReservation";
            StringRequest getReservationRe = new StringRequest(Request.Method.POST, url, response -> {
                Log.d("reservationResp",response);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        JSONObject row = result.getJSONObject("reservation");
                        THE_RESERVATION = new RESERVATION(row.getInt("id"),row.getInt("RoomNumber"),row.getInt("ClientId"),row.getInt("Status"),
                                row.getInt("RoomOrSuite"),row.getInt("MultiRooms"),row.getString("AddRoomNumber"),row.getString("AddRoomId"),row.getString("StartDate"),
                                row.getInt("Nights"),row.getString("EndDate"),row.getInt("Hotel"),row.getInt("BuildingNo"),row.getInt("Floor"),row.getString("ClientFirstName"),row.getString("ClientLastName"),row.getString("IdType"),
                                row.getInt("IdNumber"),row.getInt("MobileNumber"),row.getString("Email"),row.getInt("Rating"));
                        TextView fName = findViewById(R.id.client_Name);
                        TextView checkIn = findViewById(R.id.check_In_Date);
                        TextView checkout = findViewById(R.id.check_out_Date);
                        fName.setText(String.format("%s %s", THE_RESERVATION.ClientFirstName, THE_RESERVATION.ClientLastName));
                        checkIn.setText(String.format("in:%s", THE_RESERVATION.StartDate));
                        checkout.setText(String.format("out:%s", THE_RESERVATION.EndDate));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    new messageDialog(e.getMessage(),"Failed to get reservation",act);
                }
            }, error -> new messageDialog(error.toString(),"Failed to get reservation",act)) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> Params = new HashMap<>();
                    Params.put("reservation_id", String.valueOf(RESERVATION));
                    Params.put("suite_number" , String.valueOf(THE_ROOM.SuiteNumber));
                    return Params;
                }
            };
            if (FirebaseTokenRegister == null) {
                FirebaseTokenRegister = Volley.newRequestQueue(act) ;
            }
            FirebaseTokenRegister.add(getReservationRe);
        }
    }

    void setServiceButtonImage() {
        if (serviceButtonsTimer != null) {
            serviceButtonsTimer.cancel();
        }
        serviceButtonsTimer = new Timer();
        ImageView servicesImage = findViewById(R.id.imageView13);
        serviceButtonsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("serviceButtons" , serviceButtonsTimerIndex+" ");
                if (serviceButtonsTimerIndex == 0) {
                    runOnUiThread(() -> {
                        if (CleanupStatus) {
                            servicesImage.setBackgroundResource(R.drawable.cleanup_on_anim);
                        }
                        else {
                            servicesImage.setBackgroundResource(R.drawable.cleanup_anim);
                        }
                        serviceButtonsTimerIndex++;
                    });
                }
                else if (serviceButtonsTimerIndex == 1) {
                    runOnUiThread(() -> {
                        if (LaundryStatus) {
                            servicesImage.setBackgroundResource(R.drawable.laundry_on_anim);
                        }
                        else {
                            servicesImage.setBackgroundResource(R.drawable.laundry_anim);
                        }
                        serviceButtonsTimerIndex++;
                    });
                }
                else if (serviceButtonsTimerIndex == 2) {
                    runOnUiThread(() -> {
                        if (RoomServiceStatus) {
                            servicesImage.setBackgroundResource(R.drawable.roomservice_on_3);
                        }
                        else {
                            servicesImage.setBackgroundResource(R.drawable.roomservice_3);
                        }
                        serviceButtonsTimerIndex++;
                    });
                }
                else if (serviceButtonsTimerIndex == 3) {
                    runOnUiThread(() -> {
                        if (CheckoutStatus) {
                            servicesImage.setBackgroundResource(R.drawable.checkout_on_2);
                        }
                        else {
                            servicesImage.setBackgroundResource(R.drawable.checkout_2);
                        }
                        serviceButtonsTimerIndex = 0;
                    });
                }
            }
        },0,20000);
    }

    void xxx() {
        ImageView servicesImage = findViewById(R.id.imageView13);
        if (serviceButtonsTimerIndex == 0) {
            runOnUiThread(() -> {
                if (CleanupStatus) {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.cleanup_on_anim);
                    }
                }
                else {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.cleanup_anim);
                    }
                }
            });
        }
        else if (serviceButtonsTimerIndex == 1) {
            runOnUiThread(() -> {
                if (LaundryStatus) {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.laundry_on_anim);
                    }
                }
                else {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.laundry_anim);
                    }
                }
            });
        }
        else if (serviceButtonsTimerIndex == 2) {
            runOnUiThread(() -> {
                if (RoomServiceStatus) {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.roomservice_on_3);
                    }
                }
                else {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.roomservice_3);
                    }
                }
            });
        }
        else if (serviceButtonsTimerIndex == 3) {
            runOnUiThread(() -> {
                if (CheckoutStatus) {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.checkout_on_2);
                    }
                }
                else {
                    if (BigScreen) {
                        servicesImage.setBackgroundResource(R.drawable.checkout_2);
                    }
                }
            });
        }
    }

//-------------------------------------------------------------

    void createAcLayout() {
        if (THE_ROOM.getAC_B() != null) {
            LinearLayout BtnS = findViewById(R.id.MainBtns_Layout);
            TextView Text = findViewById(R.id.RoomNumber_MainScreen);
            TextView Caption = findViewById(R.id.textView37);
            LinearLayout home = findViewById(R.id.home_Btn);
            LinearLayout AcLayout = findViewById(R.id.ac_layout);
            home.setVisibility(View.VISIBLE);
            AcLayout.setVisibility(View.VISIBLE);
            BtnS.setVisibility(View.GONE);
            Text.setVisibility(View.GONE);
            Caption.setVisibility(View.VISIBLE);
            Caption.setText(getResources().getString(R.string.ac));
            startBackHomeThread();
        }
    }

    void setTheAcLayout() {
        TextView clientSelectedTemp = findViewById(R.id.clientTemp);
        TextView fanSpeedText = findViewById(R.id.fanSpeed);
        Button onOf = findViewById(R.id.onOffBtn);
        Button fanSpeed = findViewById(R.id.fanSpeedBtn);
        Button tempUp = findViewById(R.id.tempUpBtn);
        Button tempDown = findViewById(R.id.tempDownBtn);
        if (THE_ROOM.getAC_B() != null) {
            final int[] lastTemp = {0};
            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(THE_ROOM.getAC_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
                @Override
                public void onSuccess(List<TaskListBean> result) {
                    for (int j=0 ; j<result.size();j++) {
                        if (result.get(j).getName().equals("Set temp") || result.get(j).getName().equals("temp_set") || result.get(j).getName().equals("Set Temperature") || result.get(j).getName().equals("Set temperature")) {
                            THE_ROOM.acVariables.TempSetDP = result.get(j).getDpId();
                            THE_ROOM.acVariables.TempMax = result.get(j).getValueSchemaBean().getMax();
                            THE_ROOM.acVariables.TempMin = result.get(j).getValueSchemaBean().getMin();
                            THE_ROOM.acVariables.unit = result.get(j).getValueSchemaBean().getUnit();
                            THE_ROOM.acVariables.step = result.get(j).getValueSchemaBean().getStep();
                            try{
                                String x = String.valueOf(THE_ROOM.acVariables.TempMax);
                                THE_ROOM.acVariables.TempChars = x.length();
                                if (THE_ROOM.acVariables.TempChars == 2) {
                                    THE_ROOM.acVariables.TempSetPoint = MyApp.ProjectVariables.Temp ;
                                    THE_ROOM.acVariables.TempClient = 24 ;
                                }
                                else {
                                    THE_ROOM.acVariables.TempSetPoint = MyApp.ProjectVariables.Temp*10 ;
                                    THE_ROOM.acVariables.TempClient = 240 ;
                                }
                            }catch (Exception e) {
                                Log.d("ac",e.getMessage());
                            }
                        }
                        if (result.get(j).getName().equals("Power") || result.get(j).getName().equals("switch") || result.get(j).getName().equals("Switch")) {
                            THE_ROOM.acVariables.PowerDP = result.get(j).getDpId();
                        }
                        if (result.get(j).getName().equals("Current temp") || result.get(j).getName().equals("temp_current") || result.get(j).getName().equals("Current Temperature") || result.get(j).getName().equals("Current temperature")) {
                            THE_ROOM.acVariables.TempCurrentDP = result.get(j).getDpId() ;
                        }
                        if (result.get(j).getName().contains("Fan") || result.get(j).getName().contains("level") || result.get(j).getName().contains("Gear") || result.get(j).getName().contains("FAN") || result.get(j).getName().contains("fan")) {
                            THE_ROOM.acVariables.FanDP = result.get(j).getDpId() ;
                            try {
                                JSONObject r = new JSONObject(result.get(j).getSchemaBean().property);
                                String[] v = r.getString("range").split(",");
                                for (int y = 0;y<v.length;y++) {
                                    v[y] = v[y].replaceAll("\"","");
                                    v[y] = v[y].replace("]","");
                                    v[y] = v[y].replace("[","");
                                }
                                THE_ROOM.acVariables.FanValues = v;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("setDevicesListAC"+THE_ROOM.RoomNumber,"set dp: "+THE_ROOM.acVariables.TempSetDP+" power dp: "+THE_ROOM.acVariables.PowerDP+" fan dp: "+THE_ROOM.acVariables.FanDP+" current dp: "+THE_ROOM.acVariables.TempCurrentDP+" fan values: "+ Arrays.toString(THE_ROOM.acVariables.FanValues)+" max: "+THE_ROOM.acVariables.TempMax+" min: "+THE_ROOM.acVariables.TempMin+" unit: "+THE_ROOM.acVariables.unit+" step: "+THE_ROOM.acVariables.step+" chars: "+THE_ROOM.acVariables.TempChars+" setPoint: "+THE_ROOM.acVariables.TempSetPoint+" clientTemp: "+THE_ROOM.acVariables.TempClient);
                    View.OnClickListener off = v -> {
                        RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("power").setValue("4");
                        RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("power").setValue("2");
                        x=0;
                    };
                    View.OnClickListener on = v -> {
                        RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("power").setValue("4");
                        RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("power").setValue("1");
                        x=0;
                    };
                    RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("power").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                int Val = Integer.parseInt(snapshot.getValue().toString());
                                if (Val == 1 || Val == 3) {
                                    tempUp.setVisibility(View.VISIBLE);
                                    tempDown.setVisibility(View.VISIBLE);
                                    fanSpeed.setVisibility(View.VISIBLE);
                                    onOf.setBackgroundResource(R.drawable.ac_on);
                                    onOf.setOnClickListener(off);
                                }
                                else if (Val == 0 || Val == 2) {
                                    tempUp.setVisibility(View.INVISIBLE);
                                    tempDown.setVisibility(View.INVISIBLE);
                                    fanSpeed.setVisibility(View.INVISIBLE);
                                    onOf.setBackgroundResource(android.R.drawable.ic_lock_power_off);
                                    onOf.setOnClickListener(on);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("temp").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                int Val = Integer.parseInt(snapshot.getValue().toString());
                                lastTemp[0] = Val;
                                if (THE_ROOM.acVariables.TempChars == 3) {
                                    String res = String.valueOf(Val) ;
                                    clientSelectedTemp.setText(String.format("%s %s", res, THE_ROOM.acVariables.unit));
                                    if (lastTemp[0]*10 > THE_ROOM.acVariables.TempMax) {
                                        ToastMaker.MakeToast("max temp",act);
                                        tempUp.setOnClickListener(v -> {

                                        });
                                    }
                                    else {
                                        tempUp.setOnClickListener(setTempButtonClick(String.valueOf((lastTemp[0]+1)),RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("temp")));
                                    }
                                    if (lastTemp[0]*10 < THE_ROOM.acVariables.TempMin) {
                                        ToastMaker.MakeToast("min temp",act);
                                        tempDown.setOnClickListener(v -> {

                                        });
                                    }
                                    else {
                                        tempDown.setOnClickListener(setTempButtonClick(String.valueOf((lastTemp[0]-1)),RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("temp")));
                                    }
                                }
                                else {
                                    String res = String.valueOf(Val) ;
                                    clientSelectedTemp.setText(String.format("%s %s", res, THE_ROOM.acVariables.unit));
                                    if (lastTemp[0] > THE_ROOM.acVariables.TempMax) {
                                        ToastMaker.MakeToast("max temp",act);
                                        tempUp.setOnClickListener(v -> {

                                        });
                                    }
                                    else {
                                        tempUp.setOnClickListener(setTempButtonClick(String.valueOf((lastTemp[0]+1)),RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("temp")));
                                    }
                                    if (lastTemp[0] < THE_ROOM.acVariables.TempMin) {
                                        ToastMaker.MakeToast("min temp",act);
                                        tempDown.setOnClickListener(v -> {

                                        });
                                    }
                                    else {
                                        tempDown.setOnClickListener(setTempButtonClick(String.valueOf((lastTemp[0]-1)),RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("temp")));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                String Val = snapshot.getValue().toString();
                                fanSpeedText.setText(Val);
                                switch (Val) {
                                    case "low":
                                    case "Low":
                                    case "LOW":
                                    case "0":
                                        switch (Val) {
                                            case "low":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("med", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "Low":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("Med", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "LOW":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("MED", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "0":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("1", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                        }
                                        break;
                                    case "med":
                                    case "Med":
                                    case "MED":
                                    case "middle":
                                    case "1":
                                        switch (Val) {
                                            case "med":
                                            case "middle":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("high", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "Med":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("High", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "MED":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("HIGH", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "1":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("2", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                        }
                                        break;
                                    case "high":
                                    case "High":
                                    case "HIGH":
                                    case "2":
                                        switch (Val) {
                                            case "high":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("auto", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "High":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("Auto", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "HIGH":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("AUTO", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "2":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("3", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                        }
                                        break;
                                    case "auto":
                                    case "Auto":
                                    case "AUTO":
                                    case "3":
                                        switch (Val) {
                                            case "auto":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("low", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "Auto":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("Low", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "AUTO":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("LOW", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                            case "3":
                                                fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("0", RoomDevicesRef.child(THE_ROOM.getAC_B().getName()).child("fan")));
                                                break;
                                        }
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    if (THE_ROOM.acVariables.PowerDP != 0) {
//                        boolean powerStatus = Boolean.parseBoolean(Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.PowerDP))).toString());
//                        if (powerStatus) {
//                            tempUp.setVisibility(View.VISIBLE);
//                            tempDown.setVisibility(View.VISIBLE);
//                            fanSpeed.setVisibility(View.VISIBLE);
//                            onOf.setBackgroundResource(R.drawable.ac_on);
//                        }
//                        else {
//                            tempUp.setVisibility(View.INVISIBLE);
//                            tempDown.setVisibility(View.INVISIBLE);
//                            fanSpeed.setVisibility(View.INVISIBLE);
//                            onOf.setBackgroundResource(R.drawable.ac_off);
//                        }
//                    }
//                    if (THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP)) != null) {
//                        if (Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("low") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("Low") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("1")) {
//                            fanSpeedText.setText(getResources().getString(R.string.low));
//                        }
//                        else if (Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("auto") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("Auto") ) {
//                            fanSpeedText.setText(getResources().getString(R.string.auto));
//                        }
//                        else if (Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("High") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("high") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("3")) {
//                            fanSpeedText.setText(getResources().getString(R.string.high));
//                        }
//                        else if (Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("Med") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("med") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("middle") || Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(THE_ROOM.acVariables.FanDP))).toString().equals("2")) {
//                            fanSpeedText.setText(getResources().getString(R.string.med));
//                        }
//                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });
//            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(THE_ROOM.getAC_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
//                @Override
//                public void onSuccess(List<TaskListBean> result) {
//                    long SetId = 0;
//                    long PowerId = 0;
//                    long FanId = 0;
//                    long CurrentId = 0;
//                    for (int j=0 ; j<result.size();j++) {
//                        Log.d("setDevicesList",result.get(j).getName());
//                        if (result.get(j).getName().equals("Set temp") || result.get(j).getName().equals("temp_set") || result.get(j).getName().equals("Set Temperature") || result.get(j).getName().equals("Set temperature")) {
//                            SetId = result.get(j).getDpId() ;
//                            THE_ROOM.acVariables.TempSetDP = SetId;
//                            THE_ROOM.acVariables.TempMax = result.get(j).getValueSchemaBean().getMax();
//                            THE_ROOM.acVariables.TempMin = result.get(j).getValueSchemaBean().getMin();
//                            try{
//                                String x = String.valueOf(THE_ROOM.acVariables.TempMax);
//                                THE_ROOM.acVariables.TempChars = x.length();
//                                if (THE_ROOM.acVariables.TempChars == 2) {
//                                    THE_ROOM.acVariables.TempSetPoint = MyApp.ProjectVariables.Temp ;
//                                }
//                                else {
//                                    THE_ROOM.acVariables.TempSetPoint = MyApp.ProjectVariables.Temp*10 ;
//                                }
//                            }catch (Exception e) {
//                                Log.d("ac",e.getMessage());
//                            }
//                            Log.d("setDevicesListSet",result.get(j).getSchemaBean().property+" "+THE_ROOM.RoomNumber);
//                        }
//                        if (result.get(j).getName().equals("Power") || result.get(j).getName().equals("switch") || result.get(j).getName().equals("Switch")) {
//                            PowerId = result.get(j).getDpId() ;
//                            THE_ROOM.acVariables.PowerDP = PowerId;
//                            Log.d("setDevicesListPower",result.get(j).getSchemaBean().property+" "+THE_ROOM.RoomNumber);
//                        }
//                        if (result.get(j).getName().equals("Current temp") || result.get(j).getName().equals("temp_current") || result.get(j).getName().equals("Current Temperature") || result.get(j).getName().equals("Current temperature")) {
//                            CurrentId = result.get(j).getDpId() ;
//                            THE_ROOM.acVariables.TempCurrentDP = CurrentId;
//                            Log.d("setDevicesListCur",result.get(j).getSchemaBean().property+" "+THE_ROOM.RoomNumber);
//                        }
//                        if (result.get(j).getName().contains("Fan") || result.get(j).getName().contains("level") || result.get(j).getName().contains("Gear") || result.get(j).getName().contains("FAN") || result.get(j).getName().contains("fan")) {
//                            FanId = result.get(j).getDpId() ;
//                            THE_ROOM.acVariables.FanDP = FanId;
//                            Log.d("setDevicesListFan",result.get(j).getSchemaBean().property+" "+THE_ROOM.RoomNumber);//+result.get(j).getSchemaBean().property
//                            try {
//                                JSONObject r = new JSONObject(result.get(j).getSchemaBean().property);
//                                String[] v = r.getString("range").split(",");
//                                for (int y = 0;y<v.length;y++) {
//                                    v[y] = v[y].replaceAll("\"","");
//                                    v[y] = v[y].replace("]","");
//                                    v[y] = v[y].replace("[","");
//                                    Log.d("setDevicesListFan",v[y]);
//                                }
//                                THE_ROOM.acVariables.FanValues = v;
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    if (THE_ROOM.acVariables.PowerDP != 0) {
//                        if (THE_ROOM.getAC_B().dps.get(String.valueOf(PowerId)) != null) {
//                            long finalPowerId = PowerId;
//                            Log.d("setDevicesList",finalPowerId+" power");
////                            if (Boolean.parseBoolean(Objects.requireNonNull(THE_ROOM.getAC_B().dps.get(String.valueOf(PowerId))).toString())) {
////                                ProjectDevices.child(String.valueOf(THE_ROOM.RoomNumber)).child(THE_ROOM.getAC_B().name).child("power").setValue(3);
////                            }
////                            else {
////                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
////                            }
////                            RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power"),
////                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                            if (snapshot.getValue() != null) {
////                                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
////                                                    ROOMS.get(finalI).getAC().publishDps("{\" "+ finalPowerId +"\":true}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
////                                                        @Override
////                                                        public void onError(String code, String error) {
////                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
////                                                        }
////                                                        @Override
////                                                        public void onSuccess() {
////                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
////                                                        }
////                                                    });
////                                                }
////                                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
////                                                    ROOMS.get(finalI).getAC().publishDps("{\" "+finalPowerId+"\":false}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
////                                                        @Override
////                                                        public void onError(String code, String error) {
////                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(3);
////                                                        }
////                                                        @Override
////                                                        public void onSuccess() {
////                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("power").setValue(0);
////                                                        }
////                                                    });
////                                                }
////                                            }
////                                        }
////                                        @Override
////                                        public void onCancelled(@NonNull DatabaseError error) {
////
////                                        }
////                                    })));
//                        }
//                    }
//                    if (THE_ROOM.acVariables.TempSetDP != 0) {
//                        if (THE_ROOM.getAC_B().dps.get(String.valueOf(SetId)) != null) {
////                            long finalSetId = SetId;
////                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").setValue(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(SetId))).toString());
////                            RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp"),
////                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                            if (snapshot.getValue() != null) {
////                                                Log.d("tempModify" , snapshot.getValue().toString());
////                                                int newTemp = Integer.parseInt(snapshot.getValue().toString());
////                                                ROOMS.get(finalI).getAC().publishDps("{\" "+ finalSetId +"\":"+newTemp+"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
////                                                    @Override
////                                                    public void onError(String code, String error) {
////                                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("temp").setValue(Integer.parseInt(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(finalSetId))).toString()));
////                                                    }
////                                                    @Override
////                                                    public void onSuccess() {
////                                                    }
////                                                });
////                                            }
////                                        }
////                                        @Override
////                                        public void onCancelled(@NonNull DatabaseError error) {
////
////                                        }
////                                    })));
//                        }
//                    }
//                    if (THE_ROOM.acVariables.FanDP != 0) {
//                        if (THE_ROOM.getAC_B().dps.get(String.valueOf(FanId)) != null) {
////                            long finalFanId = FanId;
////                            RoomsDevicesReferencesListeners.add(new DatabaseReference_ValueEventListener(ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan"),
////                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                            if (snapshot.getValue() != null) {
////                                                Log.d("fanModify" , snapshot.getValue().toString());
////                                                String value = snapshot.getValue().toString();
////                                                if (value.equals("high") || value.equals("med") || value.equals("low") || value.equals("auto") || value.equals("middle")) {
////                                                    ROOMS.get(finalI).getAC().publishDps("{\" "+ finalFanId +"\":\""+value+"\"}",TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
////                                                        @Override
////                                                        public void onError(String code, String error) {
////                                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").setValue(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(finalFanId)));
////                                                        }
////                                                        @Override
////                                                        public void onSuccess() {
////                                                        }
////                                                    });
////                                                }
////                                            }
////                                        }
////                                        @Override
////                                        public void onCancelled(@NonNull DatabaseError error) {
////
////                                        }
////                                    })));
////                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("fan").setValue(Objects.requireNonNull(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(FanId))).toString());
//                        }
//                    }
//                    if (THE_ROOM.acVariables.TempCurrentDP != 0) {
//                        Log.d("currentTemp",THE_ROOM.acVariables.TempCurrentDP+" "+CurrentId);
//                    }
//                }
//
//                @Override
//                public void onError(String errorCode, String errorMessage) {
//
//                }
//            });
        }
        else {
            showAc.setVisibility(View.GONE);
        }
    }

    View.OnClickListener setTempButtonClick(String temp,DatabaseReference ref) {
        Log.d("acTempClick",temp+" "+THE_ROOM.acVariables.TempMax+" "+THE_ROOM.acVariables.TempMin);
        return v -> {
            ref.setValue(temp);
            x=0;
        };
    }

    View.OnClickListener setFanSpeedButtonOnClick(String newFan,DatabaseReference ref) {
        return v -> {
            ref.setValue(newFan);
            x=0;
        };
    }

    public void sendRegistrationToServer(final String token) {
        Log.d("registerToken", "start");
        String url = MyApp.ProjectURL + "roomsManagement/modifyRoomFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, response -> Log.d("registerToken", response ), error -> Log.d("registerToken", error.toString() )) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("token",token);
                params.put("room_id",String.valueOf(THE_ROOM.id));
                return params;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(r);
    }

    static void openMessageDialog(final String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            final Dialog d = new Dialog(MyApp.mainActivity.get(0));
            d.setCancelable(false);
            d.setContentView(R.layout.reception_message_dialog);
            TextView m = d.findViewById(R.id.receptionMessage);
            m.setText(message);
            if (MyApp.ProjectVariables.Logo != null) {
                ImageView im = d.findViewById(R.id.imageView3);
                Picasso.get().load(MyApp.ProjectVariables.Logo).into(im);
            }
            Button b = d.findViewById(R.id.closeReceptionMessage);
            b.setOnClickListener(v -> d.dismiss());
            d.show();
        });
    }

    public static void addRoomServiceOrderInDataBase(Context act) {
        final Dialog d = new Dialog(act);
        View v = LayoutInflater.from(act).inflate(R.layout.room_service_dialog,null );
        d.setContentView(v);
        Window w = d.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        w.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText orderEditeText = d.findViewById(R.id.RoomServiceDialog_Text);
        Button cancel = d.findViewById(R.id.RoomServiceDialog_Cancel);
        final String[] xxx = new String[] {"","","","",""};
        final CheckBox slippers = d.findViewById(R.id.checkBox_slippers);
        final CheckBox towels = d.findViewById(R.id.checkBox_towels);
        final CheckBox minibar =d.findViewById(R.id.checkBox_minibar);
        final CheckBox bath = d.findViewById(R.id.checkBox_bath);
        final CheckBox other = d.findViewById(R.id.checkBox_others);
        slippers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (slippers.isChecked())
            {
                xxx[0] = "Slipper";
            }
            else
            {
                xxx[0] = "";
            }
        });
        towels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (towels.isChecked())
            {
                xxx[1] = "Towels";
            }
            else
            {
                xxx[1] = "";
            }
        });
        minibar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (minibar.isChecked())
            {
                xxx[2] = "Mini Bar";
            }
            else
            {
                xxx[2] = "";
            }
        });
        bath.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(bath.isChecked())
            {
                xxx[3] = "BathSet";
            }
            else
            {
                xxx[3] = "";
            }
        });
        other.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (other.isChecked())
            {
                xxx[4] = "Other";
                orderEditeText.setVisibility(View.VISIBLE);
            }
            else
            {
                xxx[4] = "";
                orderEditeText.setVisibility(View.INVISIBLE);
            }
        });
        orderEditeText.setVisibility(View.INVISIBLE);
        d.show();
        cancel.setOnClickListener(v1 -> d.dismiss());
        Button ok = d.findViewById(R.id.RoomServiceDialog_OK);
        ok.setOnClickListener(v12 -> {
            for (String s : xxx) {
                if (!s.equals("") && !s.equals("Other")) {
                    if (roomServiceOrder.equals("")) {
                        roomServiceOrder = s;
                    } else {
                        roomServiceOrder = roomServiceOrder + "-" + s;
                    }

                } else if (s.equals("Other")) {
                    if (roomServiceOrder.equals("")) {
                        roomServiceOrder = orderEditeText.getText().toString();
                    } else {
                        roomServiceOrder = String.format("%s-%s", roomServiceOrder, orderEditeText.getText().toString());
                    }
                }
            }
            if (roomServiceOrder.length() > 0) {
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time =  x.getTimeInMillis();
                RoomServiceStatus = true;
                myRefRoomService.setValue(time);
                myRefRoomServiceText.setValue(roomServiceOrder);
                myRefDep.setValue("RoomService");
                myRefDND.setValue(0);
                String url = MyApp.ProjectURL + "reservations/addRoomServiceOrderRoomDevice";
                StringRequest request = new StringRequest(Request.Method.POST,url, response -> {
                    Log.d("roomServiceResp" , response);
                    roomServiceOrder = "";
                }, error -> Log.d("roomServiceResp" , error.toString())) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("room_id", String.valueOf(MyApp.Room.id));
                        params.put("order", roomServiceOrder);
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(request);
                d.dismiss();
            }
            else
            {
                ToastMaker.MakeToast("Please Enter Your Order" , act);
            }
        });
    }

    public static void removeRoomServiceOrderInDataBase(Context act) {
        RoomServiceStatus = false ;
        myRefRoomService.setValue(0);
        myRefRoomServiceText.setValue("0");
        String url = MyApp.ProjectURL + "reservations/cancelServiceOrderControlDevice"+ roomServiceCounter;
        StringRequest removeOrder = new StringRequest(Request.Method.POST,url, response -> Log.d("roomServiceResp" , response), error -> Log.d("roomServiceResp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("order_type" ,"RoomService");
                params.put("room_id" , String.valueOf( MyApp.Room.id));
                return params;
            }
        };
        Volley.newRequestQueue(act).add(removeOrder);
        roomServiceCounter++ ;
        if (roomServiceCounter == 5) {
            roomServiceCounter = 1 ;
        }
    }

    private void blink() {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler handler = new Handler();
        TextView date = findViewById(R.id.mainDate);
        TextView time = findViewById(R.id.mainTime);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                Log.d("Time is : ",x.getTime().toString());
                String currentTime = x.get(Calendar.HOUR_OF_DAY)+":"+x.get(Calendar.MINUTE)+":"+x.get(Calendar.SECOND);
                time.setText(currentTime);
                String currentDate = x.get(Calendar.DAY_OF_MONTH)+ "-" + (x.get(Calendar.MONTH)+1)+"-" + x.get(Calendar.YEAR);
                date.setText(currentDate);
                blink();
            });
        }).start();
    }

    public void logout() {
        String url = MyApp.ProjectURL + "roomsManagement/logoutRoom" ;
        StringRequest logoutRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("logoutResp" , response);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    Toast.makeText(act,"Logout Success",Toast.LENGTH_LONG).show();
                    editor.putString("projectName" ,null);
                    editor.putString("tuyaUser" , null);
                    editor.putString("tuyaPassword" ,null);
                    editor.putString("lockUser" ,null);
                    editor.putString("lockPassword" ,null);
                    editor.putString("url" ,null);
                    editor.putString("RoomNumber" ,null);
                    editor.apply();
                    Intent i = new Intent(act , LogIn.class);
                    startActivity(i);
                    act.finish();
                }
                else {
                    new messageDialog(result.getString("error"),"failed",act);
                }
            } catch (JSONException e) {
                Log.d("logoutResp" , e.getMessage());
                new messageDialog(e.getMessage(),"failed",act);
            }
        }, error -> {
            Log.d("logoutResp" , error.toString());
            new messageDialog(error.toString(),"failed",act);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(THE_ROOM.id));
                return params;
            }
        };
        Volley.newRequestQueue(act).add(logoutRequest);
    }

    public void getFacilities() {
        String url = MyApp.ProjectURL + "facilitys/getfacilitys" ;
        StringRequest facilityRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("facilitiesResp" , response);
            if (response != null) {
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i=0;i<arr.length();i++) {
                        JSONObject row = arr.getJSONObject(i);
                        Facilities.add(new FACILITY(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                    }
                    getLaundries();
                    getRestaurants();
                    getMiniBar();
                } catch (JSONException e) {
                    Log.d("facilitiesResp" , e.getMessage());
                }
            }
        }, error -> Log.d("facilitiesResp" , error.toString()));
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(facilityRequest);
    }

    private void getLaundries() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("Laundry")) {
                Laundries.add(new LAUNDRY(Facilities.get(i).id,Facilities.get(i).Hotel,Facilities.get(i).TypeId,Facilities.get(i).TypeName,Facilities.get(i).Name,Facilities.get(i).Control,Facilities.get(i).photo));
            }
        }
        if (Laundries.size() > 0 ) {
            LaundryBtn.setVisibility(View.VISIBLE);
            getLaundryMenu();
        }
        else {
            LaundryBtn.setVisibility(View.GONE);
        }
    }

    void getLaundryMenu() {
        if (Laundries.size() > 0) {
            List<LAUNDRYITEM> list = new ArrayList<>();
            String url = MyApp.ProjectURL + "facilitys/getLaundryItemsRoomDevice";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, response -> {
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        result.getString("result");
                        if (result.getString("result").equals("success")) {
                            JSONArray arr = new JSONArray(result.getString("items"));
                            for (int i=0 ; i<arr.length();i++) {
                                JSONObject row = arr.getJSONObject(i);
                                list.add(new LAUNDRYITEM(row.getString("icon"),row.getString("Name"),row.getString("Price")));
                            }
                            if (list.size()>0) {
                                LAUNDRYMENU_ADAPTER adapter = new LAUNDRYMENU_ADAPTER(list);
                                LAUNDRY_MENU.setAdapter(adapter);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {

            })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("facility_id" , String.valueOf(Laundries.get(0).id));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(laundryRequest);
        }
    }

    private void getRestaurants() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("Restaurant") || Facilities.get(i).TypeName.equals("CoffeeShop")) {
                RESTAURANT_UNIT r = new RESTAURANT_UNIT(Facilities.get(i).id,Facilities.get(i).Hotel,Facilities.get(i).TypeId,Facilities.get(i).TypeName,Facilities.get(i).Name,Facilities.get(i).Control,Facilities.get(i).photo);
                Log.d("restaurantsAre", Facilities.get(i).Name +" "+Facilities.get(i).TypeName);
                Restaurants.add(r);
            }
        }
        if (Restaurants.size() > 0) {
            if (BigScreen) {
                ImageView restaurantImage = findViewById(R.id.imageView15);
                restaurantImage.setBackgroundResource(R.drawable.restaurant_anim);
                AnimationDrawable ad = (AnimationDrawable) restaurantImage.getBackground();
                ad.start();
                ImageView smokeImage = findViewById(R.id.imageView5);
                smokeImage.setBackgroundResource(R.drawable.smoke_anim);
                //AnimationDrawable sad = (AnimationDrawable) smokeImage.getBackground();
                //sad.start();
            }
            for (int i=0;i<Restaurants.size();i++) {
                Log.d("restaurantsAre", Restaurants.get(i).Name +" "+Restaurants.get(i).TypeName );
            }
            RestaurantBtn.setVisibility(View.VISIBLE);
        }
        else {
            RestaurantBtn.setVisibility(View.GONE);
        }
    }

    private void getMiniBar() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("MiniBar")) {
                Minibar.add(new MINIBAR(Facilities.get(i).id,Facilities.get(i).Hotel,Facilities.get(i).TypeId,Facilities.get(i).TypeName,Facilities.get(i).Name,Facilities.get(i).Control,Facilities.get(i).photo));
            }
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
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(MyApp.HOME.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                SCENES = result ;
                MY_SCENES.clear();
                if (MyApp.MY_SCENES != null) {
                    MyApp.MY_SCENES.clear();
                }
                LivingMood.clear();
                SleepMood.clear();
                WorkMood.clear();
                RomanceMood.clear();
                ReadMood.clear();
                MasterOffMood.clear();
                Log.d("scenesAre",SCENES.size()+"");
                for (SceneBean s : SCENES) {
                    Log.d("scenesAre",s.getName());
                    if (s.getName().contains(String.valueOf(THE_ROOM.RoomNumber))) {
                        MY_SCENES.add(s);
                    }
                }
                MyApp.MY_SCENES = MY_SCENES ;
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
                    prepareMoodButtons();
                    TextView lightsText = findViewById(R.id.textView40);
                    lightsText.setText(getResources().getString(R.string.lightsAndMoods));
                }
            }
            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.d("scenesAre",errorCode+" "+errorMessage);
            }
        });
    }

    void prepareLights() {
        if (THE_ROOM.getSWITCH1_B() != null) {
            Switch1Status = true ;
        }
        if (THE_ROOM.getSWITCH2_B() != null) {
            Switch1Status = true ;
        }
        if (THE_ROOM.getSWITCH3_B() != null) {
            Switch3Status = true ;
        }
        if (THE_ROOM.getSWITCH4_B() != null) {
            Switch4Status = true ;
        }
        if (THE_ROOM.getSWITCH5_B() != null) {
            Switch5Status = true ;
        }
        if (THE_ROOM.getSWITCH6_B() != null) {
            Switch6Status = true ;
        }
        if (THE_ROOM.getSWITCH7_B() != null) {
            Switch7Status = true ;
        }
        if (THE_ROOM.getSWITCH8_B() != null) {
            Switch8Status = true ;
        }
        if (!Switch1Status && !Switch2Status && !Switch3Status && !Switch4Status && !Switch5Status && !Switch6Status && !Switch7Status && !Switch8Status) {
            ShowLighting.setVisibility(View.GONE);
        }
        else {
            lightsLayout.removeAllViews();
            ShowLighting.setVisibility(View.VISIBLE);
            if (lightsDB.getScreenButtons().size() > 0 ) {
                lightsLayout.setDividerPadding(10);
                for (int i=0 ; i < lightsDB.getScreenButtons().size(); i++) {
                    if (THE_ROOM.getSWITCH1_B() != null ) {
                        String s = THE_ROOM.getSWITCH1_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S1B1);
                                    x=0;
                                });
                                if (THE_ROOM.getSWITCH1_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH1_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b1",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S1B1 = true ;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S1B1 = false ;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH1_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH1_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b2",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S1B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S1B2 = false ;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S1B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH1_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH1_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b3",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S1B3 = true ;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S1B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S1B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH1_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH1_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b4",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S1B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S1B4 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S1B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null ) {
                        String s = THE_ROOM.getSWITCH2_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH2_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH2_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b1",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S2B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S2B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S2B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH2_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH2_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b2",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S2B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S2B2 = false ;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S2B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH2_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH2_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b3",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S2B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S2B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S2B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH2_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH2_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b4",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S2B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S2B4 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S2B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null ) {
                        String s = THE_ROOM.getSWITCH3_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH3_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH3_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S3B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S3B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S3B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH3_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH3_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S3B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S3B2 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S3B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH3_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH3_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S3B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S3B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S3B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH3_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH3_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S3B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S3B4 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S3B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null ) {
                        String s = THE_ROOM.getSWITCH4_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH4_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH4_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S4B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S4B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S4B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH4_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH4_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S4B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S4B2 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S4B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH4_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH4_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S4B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S4B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S4B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH4_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH4_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S4B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S4B4= false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S4B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH5_B() != null ) {
                        String s = THE_ROOM.getSWITCH5_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH5_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH5_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S5B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S5B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH5(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S5B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH5_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH5_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S5B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S5B2 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH5(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S5B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH5_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH4_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S5B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S5B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH5(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S5B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH5_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH5_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S5B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S5B4= false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH5(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S5B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH6_B() != null ) {
                        String s = THE_ROOM.getSWITCH6_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH6_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH6_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S6B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S6B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH6(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S6B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH6_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH6_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S6B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S6B2 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH6(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S6B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH6_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH6_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S6B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S6B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH6(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S6B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH6_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH6_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S6B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S6B4= false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH6(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S6B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH7_B() != null ) {
                        String s = THE_ROOM.getSWITCH7_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH7_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH7_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S7B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S7B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH7(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S7B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH7_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH7_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S7B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S7B2 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH7(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S7B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH7_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH7_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S7B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S7B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH7(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S7B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH7_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH7_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S7B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S7B4= false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH7(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S7B4);
                                    x=0;
                                });
                            }
                        }
                    }
                    if (THE_ROOM.getSWITCH8_B() != null ) {
                        String s = THE_ROOM.getSWITCH8_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THE_ROOM.getSWITCH8_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH8_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S8B1 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S8B1 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH8(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S8B1);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THE_ROOM.getSWITCH8_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH8_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S8B2 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S8B2 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH8(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S8B2);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THE_ROOM.getSWITCH8_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH8_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S8B3 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S8B3 = false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH8(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S8B3);
                                    x=0;
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THE_ROOM.getSWITCH8_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THE_ROOM.getSWITCH8_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THE_ROOM.S8B4 = true;
                                                    makeSwitchOn(text,image);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THE_ROOM.S8B4= false;
                                                    makeSwitchOff(text,image);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(v -> {
                                    clickSwitchButton(THE_ROOM.getSWITCH8(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button), !THE_ROOM.S8B4);
                                    x=0;
                                });
                            }
                        }
                    }
                }
            }
            if (BigScreen) {
                ImageView lightsImage = findViewById(R.id.imageView14);
                lightsImage.setBackgroundResource(R.drawable.lights_icon);
                AnimationDrawable ad = (AnimationDrawable) lightsImage.getBackground();
                ad.start();
            }
        }
    }

    void clickSwitchButton(ITuyaDevice d, String Button, boolean OnOff) {
        d.publishDps("{\" "+Button+"\": "+OnOff+"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("lightTurn" , Button+" "+OnOff+" "+error+ " " + code);
            }

            @Override
            public void onSuccess() {
                Log.d("lightTurn" , THE_ROOM.S1B1+" "+Button+" "+OnOff+" success");
            }
        });
    }

    void makeSwitchOn(TextView text,Button image) {
        image.setBackgroundResource(R.drawable.light_on_new);
        text.setTextColor(Color.WHITE);
    }

    void makeSwitchOff(TextView text,Button image) {
        image.setBackgroundResource(R.drawable.light_off_new);
        text.setTextColor(Color.LTGRAY);
    }

    void prepareMoodButtons() {
        if (LivingMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.livingMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (LivingMood.get(0).getConditions() == null) {
                image.setOnClickListener(v -> {
                    for (int i=0;i<LivingMood.size();i++) {
                        TuyaHomeSdk.newSceneInstance(LivingMood.get(i).getId()).executeScene(new IResultCallback() {
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
            else {
                for (int i=0;i<LivingMood.size();i++) {
                    if (THE_ROOM.getSWITCH1_B() != null) {
                        if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH1_B().devId)) {
                            Living = THE_ROOM.getSWITCH1_B() ;
                            btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null) {
                        if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH2_B().devId)) {
                            Living = THE_ROOM.getSWITCH2_B() ;
                            btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null) {
                        if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH3_B().devId)) {
                            Living = THE_ROOM.getSWITCH3_B() ;
                            btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null) {
                        if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH4_B().devId)) {
                            Living = THE_ROOM.getSWITCH4_B() ;
                            btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                }
            }
            String finalBtn = btn;
            if (Living != null) {
                image.setOnClickListener(v -> {
                    clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Living.devId),finalBtn, !THE_ROOM.living);
                    x=0;
                });
                if (Living.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Living.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THE_ROOM.living = true ;
                                    makeSwitchOn(text,image);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THE_ROOM.living = false ;
                                    makeSwitchOff(text,image);
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
        if (SleepMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.sleepingMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (SleepMood.get(0).getConditions() == null) {
                image.setOnClickListener(v -> {
                    for (int i=0;i<SleepMood.size();i++) {
                        TuyaHomeSdk.newSceneInstance(SleepMood.get(i).getId()).executeScene(new IResultCallback() {
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
            else {
                for (int i=0;i<SleepMood.size();i++) {
                    if (THE_ROOM.getSWITCH1_B() != null) {
                        if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH1_B().devId)) {
                            Sleep = THE_ROOM.getSWITCH1_B() ;
                            btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null) {
                        if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH2_B().devId)) {
                            Sleep = THE_ROOM.getSWITCH2_B() ;
                            btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null) {
                        if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH3_B().devId)) {
                            Sleep = THE_ROOM.getSWITCH3_B() ;
                            btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null) {
                        if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH4_B().devId)) {
                            Sleep = THE_ROOM.getSWITCH4_B() ;
                            btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                }
            }
            String finalBtn = btn;
            if (Sleep != null) {
                image.setOnClickListener(v -> {
                    clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Sleep.devId),finalBtn, !THE_ROOM.sleeping);
                    x=0;
                });
                if (Sleep.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Sleep.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THE_ROOM.sleeping = true ;
                                    makeSwitchOn(text,image);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THE_ROOM.sleeping = false ;
                                    makeSwitchOff(text,image);
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
        if (WorkMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.workMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (WorkMood.get(0).getConditions() == null) {
                image.setOnClickListener(v -> {
                    for (int i=0;i<WorkMood.size();i++) {
                        TuyaHomeSdk.newSceneInstance(WorkMood.get(i).getId()).executeScene(new IResultCallback() {
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
            else {
                for (int i=0;i<WorkMood.size();i++) {
                    if (THE_ROOM.getSWITCH1_B() != null) {
                        if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH1_B().devId)) {
                            Work = THE_ROOM.getSWITCH1_B() ;
                            btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null) {
                        if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH2_B().devId)) {
                            Work = THE_ROOM.getSWITCH2_B() ;
                            btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null) {
                        if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH3_B().devId)) {
                            Work = THE_ROOM.getSWITCH3_B() ;
                            btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null) {
                        if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH4_B().devId)) {
                            Work = THE_ROOM.getSWITCH4_B() ;
                            btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                }
            }
            String finalBtn = btn;
            if (Work != null) {
                image.setOnClickListener(v -> {
                    clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Work.devId),finalBtn, !THE_ROOM.work);
                    x=0;
                });
                if (Work.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Work.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THE_ROOM.work = true ;
                                    makeSwitchOn(text,image);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THE_ROOM.work = false ;
                                    makeSwitchOff(text,image);
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
        if (RomanceMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.romanceMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (RomanceMood.get(0).getConditions() == null) {
                image.setOnClickListener(v -> {
                    for (int i=0;i<RomanceMood.size();i++) {
                        TuyaHomeSdk.newSceneInstance(RomanceMood.get(i).getId()).executeScene(new IResultCallback() {
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
            else {
                for (int i=0;i<RomanceMood.size();i++) {
                    if (THE_ROOM.getSWITCH1_B() != null) {
                        if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH1_B().devId)) {
                            Romance = THE_ROOM.getSWITCH1_B() ;
                            btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null) {
                        if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH2_B().devId)) {
                            Romance = THE_ROOM.getSWITCH2_B() ;
                            btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null) {
                        if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH3_B().devId)) {
                            Romance = THE_ROOM.getSWITCH3_B() ;
                            btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null) {
                        if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH4_B().devId)) {
                            Romance = THE_ROOM.getSWITCH4_B() ;
                            btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                }
            }
            String finalBtn = btn;
            if (Romance != null) {
                image.setOnClickListener(v -> {
                    clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Romance.devId),finalBtn, !THE_ROOM.romance);
                    x=0;
                });
                if (Romance.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Romance.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THE_ROOM.romance = true ;
                                    makeSwitchOn(text,image);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THE_ROOM.romance = false ;
                                    makeSwitchOff(text,image);
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
        if (ReadMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.readingMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (ReadMood.get(0).getConditions() == null) {
                image.setOnClickListener(v -> {
                    for (int i=0;i<ReadMood.size();i++) {
                        TuyaHomeSdk.newSceneInstance(ReadMood.get(i).getId()).executeScene(new IResultCallback() {
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
            else {
                for (int i=0;i<ReadMood.size();i++) {
                    if (THE_ROOM.getSWITCH1_B() != null) {
                        if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH1_B().devId)) {
                            Read = THE_ROOM.getSWITCH1_B() ;
                            btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null) {
                        if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH2_B().devId)) {
                            Read = THE_ROOM.getSWITCH2_B() ;
                            btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null) {
                        if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH3_B().devId)) {
                            Read = THE_ROOM.getSWITCH3_B() ;
                            btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null) {
                        if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH4_B().devId)) {
                            Read = THE_ROOM.getSWITCH4_B() ;
                            btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                }
            }
            String finalBtn = btn;
            if (Read != null) {
                image.setOnClickListener(v -> {
                    clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Read.devId),finalBtn, !THE_ROOM.read);
                    x=0;
                });
                if (Read.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Read.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THE_ROOM.read = true ;
                                    makeSwitchOn(text,image);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THE_ROOM.read = false ;
                                    makeSwitchOff(text,image);
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
        if (MasterOffMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.masterOffMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (MasterOffMood.get(0).getConditions() == null) {
                image.setOnClickListener(v -> {
                    for (int i=0;i<MasterOffMood.size();i++) {
                        TuyaHomeSdk.newSceneInstance(MasterOffMood.get(i).getId()).executeScene(new IResultCallback() {
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
            else {
                for (int i=0;i<MasterOffMood.size();i++) {
                    if (THE_ROOM.getSWITCH1_B() != null) {
                        if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH1_B().devId)) {
                            MasterOff = THE_ROOM.getSWITCH1_B() ;
                            btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH2_B() != null) {
                        if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH2_B().devId)) {
                            MasterOff = THE_ROOM.getSWITCH2_B() ;
                            btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH3_B() != null) {
                        if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH3_B().devId)) {
                            MasterOff = THE_ROOM.getSWITCH3_B() ;
                            btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                    if (THE_ROOM.getSWITCH4_B() != null) {
                        if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THE_ROOM.getSWITCH4_B().devId)) {
                            MasterOff = THE_ROOM.getSWITCH4_B() ;
                            btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                        }
                    }
                }
            }
            String finalBtn = btn;
            if (MasterOff != null) {
                image.setOnClickListener(v -> {
                    clickSwitchButton(TuyaHomeSdk.newDeviceInstance(MasterOff.devId),finalBtn, !THE_ROOM.masterOff);
                    x=0;
                });
                if (MasterOff.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(MasterOff.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THE_ROOM.masterOff = true ;
                                    makeSwitchOn(text,image);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THE_ROOM.masterOff = false ;
                                    makeSwitchOff(text,image);
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
    }

    public void OpenTheDoor(View view) {
        AVLoadingIndicatorView doorLoading = findViewById(R.id.loadingIcon);
        ImageView doorImage = findViewById(R.id.imageView17);
        if (MyApp.BluetoothLock != null) {
            doorImage.setVisibility(View.GONE);
            doorLoading.setVisibility(View.VISIBLE);
            String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
            StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                Log.d("doorOpenResp" , "BT"+response);
                try {
                    JSONObject result = new JSONObject(response);
                    result.getString("result");
                    if (result.getString("result").equals("success")) {
                        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, THE_ROOM.getLock().getLockData(), THE_ROOM.getLock().getLockMac(),new ControlLockCallback() {
                            @Override
                            public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                ToastMaker.MakeToast("door opened",act);
                                doorImage.setVisibility(View.VISIBLE);
                                doorLoading.setVisibility(View.GONE);
                            }
                            @Override
                            public void onFail(LockError error) {
                                ToastMaker.MakeToast(error.getErrorMsg(),act);
                                doorImage.setVisibility(View.VISIBLE);
                                doorLoading.setVisibility(View.GONE);
                            }
                        });
                    }
                    else {
                        ToastMaker.MakeToast(result.getString("error"),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    ToastMaker.MakeToast(e.getMessage(),act);
                    doorImage.setVisibility(View.VISIBLE);
                    doorLoading.setVisibility(View.GONE);
                }
            }, error -> {
                ToastMaker.MakeToast(error.toString(),act);
                doorImage.setVisibility(View.VISIBLE);
                doorLoading.setVisibility(View.GONE);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("room_id", String.valueOf(THE_ROOM.id));
                    return params;
                }
            };
            if (FirebaseTokenRegister == null) {
                FirebaseTokenRegister = Volley.newRequestQueue(act) ;
            }
            FirebaseTokenRegister.add(req);
        }
        else {
            if (THE_ROOM.getLOCK_B() != null) {
                doorImage.setVisibility(View.GONE);
                doorLoading.setVisibility(View.VISIBLE);
                String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
                StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                    Log.d("doorOpenResp" , "ZB"+response);
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
                                                    ToastMaker.MakeToast("door opened",act);
                                                    doorImage.setVisibility(View.VISIBLE);
                                                    doorLoading.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Log.d("openDoorResp" , "res "+error);
                                                    ToastMaker.MakeToast(error,act);
                                                    doorImage.setVisibility(View.VISIBLE);
                                                    doorLoading.setVisibility(View.GONE);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed(String error) {
                                            Log.d("doorOpenResp" , "ticket "+error);
                                            ToastMaker.MakeToast(error,act);
                                            doorImage.setVisibility(View.VISIBLE);
                                            doorLoading.setVisibility(View.GONE);
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(String error) {
                                    Log.d("doorOpenResp" , "token "+error);
                                    ToastMaker.MakeToast(error,act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                            });
                        }
                        else {
                            ToastMaker.MakeToast(result.getString("error"),act);
                            doorImage.setVisibility(View.VISIBLE);
                            doorLoading.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        Log.d("doorOpenResp" , e.getMessage());
                        ToastMaker.MakeToast(e.getMessage(),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                }, error -> {
                    Log.d("doorOpenResp" , error.toString());
                    ToastMaker.MakeToast(error.toString(),act);
                    doorImage.setVisibility(View.VISIBLE);
                    doorLoading.setVisibility(View.GONE);
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params = new HashMap<>();
                        params.put("room_id", String.valueOf(THE_ROOM.id));
                        return params;
                    }
                };
                if (FirebaseTokenRegister == null) {
                    FirebaseTokenRegister = Volley.newRequestQueue(act) ;
                }
                FirebaseTokenRegister.add(req);
            }
            else {
                new messageDialog("no lock detected in this room ","failed",act);
            }
        }
    }

    void getMiniBarMenu(int Facility) {
            LoadingDialog loading = new LoadingDialog(act);
            List<MINIBARITEM> list = new ArrayList<>();
            String url = LogIn.URL+"getMiniBarMenu.php";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, response -> {
                loading.stop();
                if (response.equals("0"))
                {
                    ToastMaker.MakeToast("No Items Recorded" , act );
                }
                else
                {

                    try
                    {
                        JSONArray  arr = new JSONArray(response);
                        for (int i=0 ; i<arr.length();i++)
                        {
                            JSONObject row = arr.getJSONObject(i);
                            list.add(new MINIBARITEM(row.getInt("id"),row.getInt("Hotel"),row.getInt("Facility"),row.getString("Name"),row.getDouble("Price"),row.getString("photo")));
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    if (list.size()>0)
                    {
                        MINIBAR_ADAPTER adapter = new MINIBAR_ADAPTER(list);
                        MINIBAR_MENU.setAdapter(adapter);
                    }
                    else
                    {
                        ToastMaker.MakeToast("No Items Recorded" , act );
                    }
                }
            }, error -> loading.stop())
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Hotel" ,"1");
                    params.put("Facility" , String.valueOf(Facility));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(laundryRequest);
    }

    private void KeepScreenFull() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,100);
                hideSystemUI();
            }
        }).start();
    }

    private void hideSystemUI() {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void hideMainBtnS() {
        LinearLayout BtnS = findViewById(R.id.MainBtns_Layout);
        TextView Text = findViewById(R.id.RoomNumber_MainScreen);
        LinearLayout Services = findViewById(R.id.Service_Btns);
        LinearLayout homeBtn = findViewById(R.id.home_Btn);
        TextView serviceText = findViewById(R.id.textView37);
        serviceText.setVisibility(View.VISIBLE);
        serviceText.setText(getResources().getString(R.string.services));
        homeBtn.setVisibility(View.VISIBLE);
        Services.setVisibility(View.VISIBLE);
        if (Laundries.size()>0){
            laundryPriceList.setVisibility(View.VISIBLE);
            LaundryBtn.setVisibility(View.VISIBLE);
        }
        else {
            laundryPriceList.setVisibility(View.GONE);
            LaundryBtn.setVisibility(View.GONE);
        }
        if (Minibar.size()>0){
              minibarPriceList.setVisibility(View.VISIBLE);
        }
        else {
               minibarPriceList.setVisibility(View.GONE);
        }
        BtnS.setVisibility(View.GONE);
        Text.setVisibility(View.GONE);
        startBackHomeThread();
    }

    private static void roomServiceOn(Activity act) {
        ImageView roomServiceImage = act.findViewById(R.id.imageView8);
        if (BigScreen) {
            roomServiceImage.setBackgroundResource(R.drawable.roomservice_on_3);
            //AnimationDrawable roomserviceAd = (AnimationDrawable) roomServiceImage.getBackground();
            //roomserviceAd.start();
        }
        else {
            roomServiceImage.setImageResource(R.drawable.towels_on);
        }
        ImageView roomServiceIcon = act.findViewById(R.id.imageView7);
        roomServiceIcon.setVisibility(View.VISIBLE);
        TextView roomServiceText = act.findViewById(R.id.textView38);
        roomServiceText.setTextColor(RESOURCES.getColor(R.color.red,null));
    }
    private static void roomServiceOff(Activity act) {
        ImageView roomServiceImage = act.findViewById(R.id.imageView8);
        if (BigScreen) {
            roomServiceImage.setBackgroundResource(R.drawable.roomservice_3);
            //AnimationDrawable roomserviceAd = (AnimationDrawable) roomServiceImage.getBackground();
            //roomserviceAd.start();
        }
        else {
            roomServiceImage.setImageResource(R.drawable.towels);
        }
        ImageView roomServiceIcon = act.findViewById(R.id.imageView7);
        roomServiceIcon.setVisibility(View.GONE);
        TextView roomServiceText = act.findViewById(R.id.textView38);
        roomServiceText.setTextColor(Color.WHITE);
    }

    private static void checkoutOn(Activity act) {
        ImageView checkOutImage = act.findViewById(R.id.imageView11);
        if (BigScreen) {
            checkOutImage.setBackgroundResource(R.drawable.checkout_on_2);
            //AnimationDrawable ad = (AnimationDrawable) checkOutImage.getBackground();
            //ad.start();
        }
        else {
            checkOutImage.setImageResource(R.drawable.checkout_on);
        }
        ImageView checkOutIcon = act.findViewById(R.id.imageView20);
        checkOutIcon.setVisibility(View.VISIBLE);
        TextView checkoutText = act.findViewById(R.id.textView42);
        checkoutText.setTextColor(RESOURCES.getColor(R.color.red,null));
    }
    private static void checkoutOff(Activity act) {
        ImageView checkOutImage = act.findViewById(R.id.imageView11);
        if (BigScreen) {
            checkOutImage.setBackgroundResource(R.drawable.checkout_2);
            //AnimationDrawable ad = (AnimationDrawable) checkOutImage.getBackground();
            //ad.start();
        }
        else {
            checkOutImage.setImageResource(R.drawable.checkout);
        }
        ImageView checkOutIcon = act.findViewById(R.id.imageView20);
        checkOutIcon.setVisibility(View.GONE);
        TextView checkoutText = act.findViewById(R.id.textView42);
        checkoutText.setTextColor(Color.WHITE);
    }

    private static void laundryOn(Activity act) {
        ImageView laundryImage  = act.findViewById(R.id.imageView16);
        if (BigScreen) {
            laundryImage.setBackgroundResource(R.drawable.laundry_on_anim);
            //AnimationDrawable ad = (AnimationDrawable) laundryImage.getBackground();
            //ad.start();
        }
        else {
            laundryImage.setImageResource(R.drawable.laundry_btn_on);
        }
        ImageView laundryIcon = act.findViewById(R.id.imageView10);
        laundryIcon.setVisibility(View.VISIBLE);
        TextView laundryText= act.findViewById(R.id.textView44);
        laundryText.setTextColor(RESOURCES.getColor(R.color.red,null));
    }
    private static void laundryOff(Activity act) {
        ImageView laundryImage  = act.findViewById(R.id.imageView16);
        if (BigScreen) {
            laundryImage.setBackgroundResource(R.drawable.laundry_anim);
            //AnimationDrawable ad = (AnimationDrawable) laundryImage.getBackground();
            //ad.start();
        }
        else {
            laundryImage.setImageResource(R.drawable.laundry_btn);
        }
        ImageView laundryIcon = act.findViewById(R.id.imageView10);
        laundryIcon.setVisibility(View.GONE);
        TextView laundryText= act.findViewById(R.id.textView44);
        laundryText.setTextColor(Color.WHITE);
    }

    private static void cleanupOn(Activity act) {
        ImageView cleanupImage = act.findViewById(R.id.imageView19);
        if (BigScreen) {
            cleanupImage.setBackgroundResource(R.drawable.cleanup_on_anim);
            //AnimationDrawable ad = (AnimationDrawable) cleanupImage.getBackground();
            //ad.start();
        }
        else {
            cleanupImage.setImageResource(R.drawable.cleanup_btn_on);
        }
        ImageView cleanupIcon = act.findViewById(R.id.imageView9);
        cleanupIcon.setVisibility(View.VISIBLE);
        TextView cleanupText = act.findViewById(R.id.textView45);
        cleanupText.setTextColor(RESOURCES.getColor(R.color.red,null));
    }
    private static void cleanupOff(Activity act) {
        ImageView cleanupImage = act.findViewById(R.id.imageView19);
        if (BigScreen) {
            cleanupImage.setBackgroundResource(R.drawable.cleanup_anim);
            //AnimationDrawable ad = (AnimationDrawable) cleanupImage.getBackground();
            //ad.start();
        }
        else {
            cleanupImage.setImageResource(R.drawable.cleanup_btn);
        }
        ImageView cleanupIcon = act.findViewById(R.id.imageView9);
        cleanupIcon.setVisibility(View.GONE);
        TextView cleanupText = act.findViewById(R.id.textView45);
        cleanupText.setTextColor(Color.WHITE);
    }

    private static void dndOn(Activity act) {
        ImageView dndImage = act.findViewById(R.id.DND_Image);

        ImageView dndIcon = act.findViewById(R.id.DND_Icon);
        dndIcon.setVisibility(View.VISIBLE);
        TextView dndText = act.findViewById(R.id.DND_Text);
        dndText.setTextColor(RESOURCES.getColor(R.color.red,null));
        if (BigScreen) {
            dndImage.setImageResource(R.drawable.dndnew_on);
        }
        else {
            dndImage.setImageResource(R.drawable.union_6);
        }
    }
    private static void dndOff(Activity act) {
        ImageView dndImage = act.findViewById(R.id.DND_Image);
        ImageView dndIcon = act.findViewById(R.id.DND_Icon);
        dndIcon.setVisibility(View.GONE);
        TextView dndText = act.findViewById(R.id.DND_Text);
        dndText.setTextColor(Color.WHITE);
        if (BigScreen) {
            dndImage.setImageResource(R.drawable.dndnew);
        }
        else {
            dndImage.setImageResource(R.drawable.union_2);
        }
    }

    private static void sosOn(Activity act) {
        ImageView sosImage = act.findViewById(R.id.SOS_Image);

        ImageView sosIcon = act.findViewById(R.id.SOS_Icon);
        sosIcon.setVisibility(View.VISIBLE);
        TextView sosText = act.findViewById(R.id.SOS_Text);
        sosText.setTextColor(RESOURCES.getColor(R.color.red,null));
        if (BigScreen) {
            sosImage.setImageResource(R.drawable.sosnew_on);
        }
        else {
            sosImage.setImageResource(R.drawable.group_54);
        }
    }
    private static void sosOff(Activity act) {
        ImageView sosImage = act.findViewById(R.id.SOS_Image);
        ImageView sosIcon = act.findViewById(R.id.SOS_Icon);
        sosIcon.setVisibility(View.GONE);
        TextView sosText = act.findViewById(R.id.SOS_Text);
        sosText.setTextColor(Color.WHITE);
        if (BigScreen) {
            sosImage.setImageResource(R.drawable.sosnew);
        }
        else {
            sosImage.setImageResource(R.drawable.group_33);
        }
    }

    private static void restaurantOn(Activity act) {
        ImageView restaurantIcon = act.findViewById(R.id.imageView2);
        restaurantIcon.setVisibility(View.VISIBLE);
    }
    private static void restaurantOff(Activity act) {
        ImageView restaurantIcon = act.findViewById(R.id.imageView2);
        restaurantIcon.setVisibility(View.GONE);
    }

    public void backToMain(View view) {
        LinearLayout BtnS = findViewById(R.id.MainBtns_Layout);
        TextView Text = findViewById(R.id.RoomNumber_MainScreen);
        LinearLayout Services = findViewById(R.id.Service_Btns);
        TextView serviceText = findViewById(R.id.textView37);
        HorizontalScrollView l = findViewById(R.id.light_buttons);
        LinearLayout laundryPricesLayout = findViewById(R.id.laundryList_Layout);
        LinearLayout minibarLayout = findViewById(R.id.Minibar_layout);
        LinearLayout minibarBtn = findViewById(R.id.minibar_priceList);
        LinearLayout home = findViewById(R.id.home_Btn);
        LinearLayout AcLayout = findViewById(R.id.ac_layout);
        serviceText.setVisibility(View.GONE);
        Services.setVisibility(View.GONE);
        laundryPricesLayout.setVisibility(View.GONE);
        laundryPriceList.setVisibility(View.GONE);
        home.setVisibility(View.GONE);
        l.setVisibility(View.GONE);
        minibarLayout.setVisibility(View.GONE);
        minibarBtn.setVisibility(View.GONE);
        AcLayout.setVisibility(View.GONE);
        BtnS.setVisibility(View.VISIBLE);
        Text.setVisibility(View.VISIBLE);
        if (BigScreen) {
            setServiceButtonImage();
        }
        //balloonsRunnable.run();
    }

    public void goToLights(View view) {
        LinearLayout BtnS = findViewById(R.id.MainBtns_Layout);
        TextView Text = findViewById(R.id.RoomNumber_MainScreen);
        BtnS.setVisibility(View.GONE);
        Text.setVisibility(View.GONE);
        //Visible
        TextView Caption = findViewById(R.id.textView37);
        Caption.setVisibility(View.VISIBLE);
        Caption.setText(getResources().getString(R.string.lights));
        HorizontalScrollView lights = findViewById(R.id.light_buttons);
        lights.setVisibility(View.VISIBLE);
        LinearLayout home = findViewById(R.id.home_Btn);
        home.setVisibility(View.VISIBLE);
        startBackHomeThread();
    }

    void startBackHomeThread() {
        Log.d("backThread" , "started");
            backHomeThread.run();
    }

    public void showHideLaundryPriceList(View view) {
        LinearLayout BtnS = findViewById(R.id.Service_Btns);
        LinearLayout l = findViewById(R.id.laundryList_Layout);
        TextView caption = findViewById(R.id.laundryList_caption);
        if (l.getVisibility() == View.GONE){
            BtnS.setVisibility(View.GONE);
            l.setVisibility(View.VISIBLE);
            caption.setText(getResources().getString(R.string.backToService));
        }
        else
        {
            BtnS.setVisibility(View.VISIBLE);
            l.setVisibility(View.GONE);
            caption.setText(getResources().getString(R.string.laundryPriceList));
        }
        x=0;
    }

    public void showHideMinibarPriceList(View view) {
        LinearLayout btnS = findViewById(R.id.Service_Btns);
        LinearLayout m = findViewById(R.id.Minibar_layout);
        if (m.getVisibility() == View.GONE){
            getMiniBarMenu(Minibar.get(0).id);
            btnS.setVisibility(View.GONE);
            m.setVisibility(View.VISIBLE);
            //caption.setText("Back To Services");
        }
        else
        {
            btnS.setVisibility(View.VISIBLE);
            m.setVisibility(View.GONE);
            //caption.setText("Minibar PriceList");
        }
        x=0;
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
                            id = Integer.parseInt(Objects.requireNonNull(child.child("id").getValue()).toString());
                        }
                        String name = "";
                        if (child.child("name").getValue() != null ) {
                            try {
                                name = Objects.requireNonNull(child.child("name").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        int jobNum = 0;
                        if (child.child("jobNumber").getValue() != null ) {
                            try {
                                jobNum = Integer.parseInt(Objects.requireNonNull(child.child("jobNumber").getValue()).toString());
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String department = "";
                        if (child.child("department").getValue() != null ) {
                            try {
                                department = Objects.requireNonNull(child.child("department").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String mobile = "";
                        if (child.child("Mobile").getValue() != null ) {
                            try {
                                mobile = Objects.requireNonNull(child.child("Mobile").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String token = "";
                        if (child.child("token").getValue() != null ) {
                            try {
                                token = Objects.requireNonNull(child.child("token").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        EmpS.add(new ServiceEmps(id,1,name,jobNum,department,mobile,token));
                    }
                    Log.d("EmpsAre", EmpS.size()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMyDevices() {
        TuyaHomeSdk.newHomeInstance(MyApp.HOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Log.d("refreshDevices" , homeBean.getDeviceList().size()+" "+homeBean.getName());
                List<DeviceBean> TheDevicesList = homeBean.getDeviceList();
                if (TheDevicesList.size() == 0) {
                    ToastMaker.MakeToast("no devices detected" , act );
                }
                else {
                    for (int i=0;i<TheDevicesList.size();i++) {
                        if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Power")) {
                            MyApp.Room.setPOWER_B(TheDevicesList.get(i));
                            MyApp.Room.setPOWER(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getPOWER_B().devId));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ZGatway")) {
                            MyApp.Room.setGATEWAY_B(TheDevicesList.get(i));
                            MyApp.Room.setGATEWAY(TuyaHomeSdk.newGatewayInstance(MyApp.Room.getGATEWAY_B().devId));
                        }
                        else if(TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"AC")) {
                            MyApp.Room.setAC_B(TheDevicesList.get(i));
                            MyApp.Room.setAC(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getAC_B().devId));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor")) {
                            MyApp.Room.setDOORSENSOR_B(TheDevicesList.get(i));
                            MyApp.Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getDOORSENSOR_B().devId));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor")) {
                            MyApp.Room.setMOTIONSENSOR_B(TheDevicesList.get(i));
                            MyApp.Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getMOTIONSENSOR_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain")) {
                            MyApp.Room.setCURTAIN_B(TheDevicesList.get(i));
                            MyApp.Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getCURTAIN_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch")) {
                            MyApp.Room.setSERVICE1_B(TheDevicesList.get(i));
                            MyApp.Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSERVICE1_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1")) {
                            MyApp.Room.setSWITCH1_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH1_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2")) {
                            MyApp.Room.setSWITCH2_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH2_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3")) {
                            MyApp.Room.setSWITCH3_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH3_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4")) {
                            MyApp.Room.setSWITCH4_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH4_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch5")) {
                            MyApp.Room.setSWITCH5_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH5_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch6")) {
                            MyApp.Room.setSWITCH6_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH6_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch7")) {
                            MyApp.Room.setSWITCH7_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH7_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch8")) {
                            MyApp.Room.setSWITCH8_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH8_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Lock")) {
                            MyApp.Room.setLOCK_B(TheDevicesList.get(i));
                            MyApp.Room.setLOCK(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getLOCK_B().getDevId()));
                            setLockButton();
                        }
                    }
                    THE_ROOM = MyApp.Room ;
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d("refreshDevices" , errorCode + " " + errorMsg);
            }
        });
    }

    public void clickButtonSwitch(String devId,boolean status,int dpId,CallbackResult callbackResult) {
        TuyaHomeSdk.newDeviceInstance(devId).publishDps("{\" "+dpId+"\": "+status+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                TuyaHomeSdk.newDeviceInstance(devId).publishDps("{\" "+dpId+"\": "+status+"}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        callbackResult.onFail(error);
                    }

                    @Override
                    public void onSuccess() {
                        callbackResult.onSuccess();
                    }
                });
            }

            @Override
            public void onSuccess() {
                callbackResult.onSuccess();
            }
        });
    }

    private void refreshDevices() {
        Log.d("refreshDevices" , "started");
        TuyaHomeSdk.getUserInstance().loginWithEmail("966",MyApp.TuyaUser,MyApp.TuyaPassword, new ILoginCallback() {
            @Override
            public void onSuccess (User user) {
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error) {
                        Log.d("refreshDevices" , error+" "+errorCode);
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans) {
                        // do something
                        for (int i=0;i<homeBeans.size();i++) {
                            if (MyApp.ProjectName.contains(homeBeans.get(i).getName())) {
                                MyApp.HOME = homeBeans.get(i);
                                break;
                            }
                        }
                        if (MyApp.HOME == null) {
                            Log.d("refreshDevices" , "home not found");
                        }
                        else {
                            Log.d("refreshDevices" , "getting devices");
                            getMyDevices();
                        }
                    }
                });
            }
            @Override
            public void onError (String code, String error) {
                Log.d("refreshDevices" , error+" "+code);
            }
        });
    }

    void setLockButton() {
        LinearLayout doorLayout = findViewById(R.id.Door_Button);
        if (MyApp.Room.getLOCK_B() == null) {
            doorLayout.setVisibility(View.GONE);
        }
        else {
            doorLayout.setVisibility(View.VISIBLE);
        }
    }

    private static void executeCommand() {
        Process process = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            String line;
            process = Runtime.getRuntime().exec("adb shell dpm set-device-owner com.syriasoft.hotelservices/com.syriasoft.hotelservices.MyDeviceAdminReceiver"); //adb shell
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = reader.readLine()) != null)
                result.append(line).append("\n");

            Log.d("setDeviceAdmin", result.toString());
        } catch (final Exception e) {
            Log.d("setDeviceAdmin",e.getMessage());
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                Log.d("setDeviceAdmin",e.getMessage());
            }
            try {
                if (process != null)
                    process.destroy();
            } catch (Exception e) {
                Log.d("setDeviceAdmin",e.getMessage());
            }
        }
        Log.d("setDeviceAdmin", result.toString());
//        Intent intent = new Intent(DevicePolicyManager. ACTION_ADD_DEVICE_ADMIN);
//        ComponentName compName = new ComponentName(MyApp.App, MyDeviceAdminReceiver.class);
//        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
//        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Bấm zô nút đồng ý(ACTIVE á)");
//        act.startActivityForResult(intent, 0);
    }
}
