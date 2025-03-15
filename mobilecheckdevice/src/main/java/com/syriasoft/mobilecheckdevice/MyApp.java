package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetProjectsCallback;
import com.syriasoft.mobilecheckdevice.Classes.LocalDataStore;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT_VARIABLES;
import com.syriasoft.mobilecheckdevice.Classes.Property.Building;
import com.syriasoft.mobilecheckdevice.Classes.Property.Floor;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
import com.syriasoft.mobilecheckdevice.Classes.Property.Suite;
import com.syriasoft.mobilecheckdevice.Classes.USER;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyApp  extends Application {
    public static Application app ;
    public static User TuyaUser ; // the tuya user
    public static List<HomeBean> homeBeans ; // all tuya homes in account
    public static List<CheckInHome> PROJECT_HOMES ; // my project tuya homes
    public static List<HomeBean> PROJECT_HOMES0 ;
    public static PROJECT My_PROJECT ; // the project object

    public static PROJECT SelectedProject;
    public static PROJECT_VARIABLES ProjectVariables ; // project variables object
    public static FirebaseDatabase firebase ;
    public static String my_token;
    public static CheckInActions checkInActions ;
    public static CheckoutActions checkOutActions ;
    public static ClientBackActions clientBackActions ;
    public static List<Room> ROOMS ; // all project rooms
    public static List<ROOM> ROOMS0 ;
    public static List<SceneBean> SCENES ; // all project scenes
    static String cloudClientId = "d9hyvtdshnm3uvaun59d" , cloudSecret = "825f9def941f456099798ccdc19112e9"; // tuya project variables
    public static String ErrorsUrl = "https://ratco-solutions.com/Checkin/Test/php/insertError.php";
    public static String applicationSide = "CheckApp";
    public static ROOM SelectedRoom;
    public static boolean isInternetConnected;
    public static LocalDataStore storage;
    public static USER user;
    public static List<Building> Buildings;
    public static List<Floor> Floors;
    public static List<Suite> suites ;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this ;
        setTuyaApplication();
        PROJECT_HOMES = new ArrayList<>();
        PROJECT_HOMES0 = new ArrayList<>();
        SCENES = new ArrayList<>();
        FirebaseApp.initializeApp(app);
        firebase = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app");
        app.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.d("appDestroy","paused: "+activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d("appDestroy","stopped: "+activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d("appDestroy","destroy: "+activity.getLocalClassName());
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        Log.d("AppTerminated","Terminated");
    }

    void setTuyaApplication() {
        try {
            TuyaHomeSdk.init(app);
        }
        catch (Exception e ) {
            Log.d("TuyaError" , Objects.requireNonNull(e.getMessage()));
        }
    }

    public static SceneBean searchSceneInList(List<SceneBean> scenes , String sceneName) {
        for (int i=0;i<scenes.size();i++){
            if (scenes.get(i).getName().equals(sceneName)) {
                return scenes.get(i) ;
            }
        }
        return null ;
    }

    public static DeviceBean searchDeviceInList(List<DeviceBean> devices , String deviceId) {
        for (int i=0;i<devices.size();i++){
            if (devices.get(i).devId.equals(deviceId)) {
                return devices.get(i) ;
            }
        }
        return null ;
    }
    public static void setLocalStorage(LocalDataStore storage) {
        MyApp.storage = storage;
    }
    public static void isInternetAvailable(RequestCallback callback) {
        PROJECT.getProjects(Volley.newRequestQueue(app), new GetProjectsCallback() {
            @Override
            public void onSuccess(List<PROJECT> projects) {
                Log.d("bootingOp","internet available");
                callback.onSuccess();
            }

            @Override
            public void onError(String error) {
                Log.d("bootingOp","internet unavailable");
                callback.onFail(error);
            }
        });
    }
    public static void isNetworkAvailable(Application app, RequestCallback callback) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            isInternetAvailable(new RequestCallback() {
                @Override
                public void onSuccess() {
                    isInternetConnected = true;
                    callback.onSuccess();
                }

                @Override
                public void onFail(String error) {
                    isInternetConnected = false;
                    callback.onFail(error);
                }
            });
        }
        else {
            callback.onFail("no internet");
        }
    }
}
