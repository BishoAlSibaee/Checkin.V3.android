package com.syriasoft.projectscontrol;

import android.app.Application;

import com.tuya.smart.home.sdk.TuyaHomeSdk;

import java.util.List;

public class MyApp extends Application {

    static Application app ;
    public static List<PROJECT> Projects ;
    public static PROJECT SelectedProject ;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this ;
        initTuya();
    }

    void initTuya () {
        TuyaHomeSdk.init(app);
    }
}
