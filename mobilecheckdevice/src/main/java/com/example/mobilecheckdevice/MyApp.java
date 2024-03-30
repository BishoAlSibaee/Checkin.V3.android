package com.example.mobilecheckdevice;

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

public class MyApp  extends Application {
    public static Application app ;
    public static User TuyaUser ; // the tuya user
    public static List<HomeBean> homeBeans ; // all tuya homes in account
    public static List<CheckInHome> ProjectHomes ; // my project tuya homes
    public static PROJECT THE_PROJECT ; // the project object
    public static PROJECT_VARIABLES ProjectVariables ; // project variables object
    public static String my_token;
    public static CheckInActions checkInActions ;
    public static CheckoutActions checkOutActions ;
    public static ClientBackActions clientBackActions ;
    public static List<ROOM> ROOMS ; // all project rooms
    public static List<SceneBean> SCENES ; // all project scenes
    static String cloudClientId = "d9hyvtdshnm3uvaun59d" , cloudSecret = "825f9def941f456099798ccdc19112e9"; // tuya project variables
    public static String ErrorsUrl = "https://ratco-solutions.com/Checkin/Test/php/insertError.php";
    public static String applicationSide = "CheckApp";
    public static ROOM SelectedRoom;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this ;
        setTuyaApplication();
        ProjectHomes = new ArrayList<>();
        SCENES = new ArrayList<>();
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
            TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
                @Override
                public void onNeedLogin(Context context) {

                }
            });
        }
        catch (Exception e ) {
            Log.d("TuyaError" , e.getMessage());
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
}
