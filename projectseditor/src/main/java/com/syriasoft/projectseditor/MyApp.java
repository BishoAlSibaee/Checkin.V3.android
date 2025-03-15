package com.syriasoft.projectseditor;

import android.app.Application;
import android.util.Log;

import com.syriasoft.projectseditor.Classes.ControlDevice;
import com.syriasoft.projectseditor.Classes.FirebaseDB;
import com.syriasoft.projectseditor.Classes.PROJECT;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {

    public static Application app;
    public static FirebaseDB fbDB;
    public static String getProjectsUrl;
    public static List<PROJECT> projects;
    public static List<ControlDevice> devices;

    public MyApp() {
        Log.d("bootingUp", "MyApp constructor");
        app = this;
        devices = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("bootingUp", "MyApp onCreate");
        fbDB = FirebaseDB.getInstance();
    }
}
