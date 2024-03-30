package com.example.hotelservicesstandalone;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.sdk.api.INeedLoginListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyApp  extends Application {

    public static Application app ;
    public static User TuyaUser ;
    public static List<HomeBean> homeBeans ;
    public static List<HomeBean> PROJECT_HOMES;
    public static PROJECT THE_PROJECT ;
    public static String Device_Id;
    public static String Device_Name;
    public static PROJECT_VARIABLES ProjectVariables ;
    public static String my_token;
    public static CheckInActions checkInActions ;
    public static CheckoutActions checkOutActions ;
    public static ClientBackActions clientBackActions ;
    public static List<ROOM> ROOMS ;
    static String cloudClientId = "d9hyvtdshnm3uvaun59d" , cloudSecret = "825f9def941f456099798ccdc19112e9";
    public static String ErrorsUrl = "https://ratco-solutions.com/Checkin/Test/php/insertError.php";
    public static String applicationSide = "ServerApp";
    public static List<Activity> Activities;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this ;
        setTuyaApplication();
        PROJECT_HOMES = new ArrayList<>();
        Activities = new ArrayList<>();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        Log.d("AppTerminated","Terminated");
    }

    static void setTuyaApplication() {
        TuyaHomeSdk.init(app);
        TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
            @Override
            public void onNeedLogin(Context context) {
                TuyaHomeSdk.init(app);
            }
        });
    }

    public static SceneBean searchSceneInList(List<SceneBean> scenes , String sceneName) {
        for (int i=0;i<scenes.size();i++){
            if (scenes.get(i).getName().equals(sceneName)) {
                return scenes.get(i) ;
            }
        }
        return null ;
    }

    public static void restartApp(int seconds)
    {
        if (seconds == 0) {
            for (Activity act :Activities) {
                act.finish();
            }
            Intent i = new Intent(app,Login.class);
            app.startActivity(i);
        }
        else {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (Activity act :Activities) {
                        act.finish();
                    }
                    Intent i = new Intent(app,Login.class);
                    app.startActivity(i);
                }
            }, 1000L *seconds);
        }
    }

    public static DeviceBean searchDeviceInList(List<DeviceBean> devices , String deviceId) {
        for (int i=0;i<devices.size();i++){
            if (devices.get(i).devId.equals(deviceId)) {
                return devices.get(i) ;
            }
        }
        return null ;
    }
}
