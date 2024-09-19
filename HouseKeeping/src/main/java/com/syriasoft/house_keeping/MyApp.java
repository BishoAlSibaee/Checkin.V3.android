package com.syriasoft.house_keeping;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.syriasoft.checkin.Classes.PROJECT;
import com.syriasoft.checkin.Classes.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {
    static Application app;
    public static List<ROOM> Rooms;
    public static User My_USER ;
    public static String Token ;
    public static ProjectsVariablesClass ProjectVariables ;
    static String cloudClientId = "d9hyvtdshnm3uvaun59d" , cloudSecret = "825f9def941f456099798ccdc19112e9";
    public static String ErrorsUrl = "https://ratco-solutions.com/Checkin/Test/php/insertError.php";
    public static String applicationSide = "ServiceApp";
    public static List<HomeBean> ProjectHomes;
    public static List<Notification> notifications;
    public static PROJECT MyProject;
    public static List<Activity> actList;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Rooms = new ArrayList<>();
        ProjectHomes = new ArrayList<>();
        notifications = new ArrayList<>();
        actList = new ArrayList<>();
        setTuyaApplication();
    }

    public static void setTuyaApplication() {
        TuyaHomeSdk.init(MyApp.app);
        TuyaHomeSdk.setOnNeedLoginListener(context -> {
            Intent intent = new Intent(context, LogIn.class);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        });
    }

    public static String getResourceString(int id) {
        return app.getResources().getString(id);
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
