package com.example.hotelservicesstandalone;

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
import com.example.hotelservicesstandalone.Classes.ControlDevice;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetProjectsCallback;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.PROJECT;
import com.example.hotelservicesstandalone.Classes.Property.Building;
import com.example.hotelservicesstandalone.Classes.Property.Floor;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Property.Suite;
import com.example.hotelservicesstandalone.Interface.RequestCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class MyApp  extends Application {

    public static PROJECT My_PROJECT;
    public static ControlDevice controlDeviceMe;

    public static Application app ;
    public static User TuyaUser ;
    public static List<HomeBean> PROJECT_HOMES;
    public static String firebaseDBUrl = "https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app";
    public static List<Building> Buildings;
    public static List<Floor> Floors;
    public static List<Room> ROOMS ;
    public static List<Suite> suites ;
    public static String ErrorsUrl = "https://ratco-solutions.com/Checkin/Test/php/insertError.php";
    public static String applicationSide = "ServerApp";
    public static boolean isInternetConnected;
    public static List<Activity> Activities;

    public static LocalDataStore storage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("appLife","onCreate");
        app = this ;
        setTuyaApplication();
        PROJECT_HOMES = new ArrayList<>();
        Activities = new ArrayList<>();
        app.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.d("appLife","activity started "+activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d("appLife","activity destroyed "+activity.getLocalClassName());
                if (activity.getLocalClassName().equals("com.example.hotelservicesstandalone.Rooms")) {
                    Intent i = new Intent(activity,Rooms.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                else if (activity.getLocalClassName().equals("com.example.hotelservicesstandalone.ReceptionScreen")) {
                    Intent i = new Intent(activity,ReceptionScreen.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("appLife","terminated");
        Intent i = new Intent(this,Login.class);
        startActivity(i);
    }

    static void setTuyaApplication() {
        TuyaHomeSdk.init(app);
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

    public static void restartServer() {
        for (Activity act : Activities) {
            act.finish();
        }
    }

    public static void isNetworkAvailable(Application app,RequestCallback callback) {
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

    static void isInternetAvailable(RequestCallback callback) {
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

    public static void setLocalStorage(LocalDataStore storage) {
        MyApp.storage = storage;
    }

    public static LocalDataStore getLocalStorage() {
        return MyApp.storage ;
    }
}
