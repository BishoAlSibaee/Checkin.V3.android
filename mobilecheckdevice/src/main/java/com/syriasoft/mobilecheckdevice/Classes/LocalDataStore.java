package com.syriasoft.mobilecheckdevice.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;
import com.syriasoft.mobilecheckdevice.Classes.Devices.DeviceDP;
import com.syriasoft.mobilecheckdevice.MyApp;

import java.util.ArrayList;
import java.util.List;

public class LocalDataStore {

    SharedPreferences storage;
    SharedPreferences.Editor editor;
    Gson gson;

    public LocalDataStore() {
        storage = MyApp.app.getApplicationContext().getSharedPreferences("Server",MODE_PRIVATE);
        editor = storage.edit();
        gson = new Gson();
    }

    public void saveProject(PROJECT project, String objectName) {
        String json = gson.toJson(project);
        editor.putString(objectName, json);
        editor.commit();
    }
    public PROJECT getProject(String objectName) {
        String json = storage.getString(objectName, null);
        if (json == null) {
            return null;
        }
        else {
            return gson.fromJson(json,PROJECT.class);
        }
    }
    public void deleteProject() {
        editor.remove("project");
        editor.commit();
    }

    public void saveUser(USER user) {
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.commit();
    }
    public USER getUser() {
        String json = storage.getString("user", null);
        if (json == null) {
            return null;
        }
        else {
            return gson.fromJson(json,USER.class);
        }
    }
    public void deleteUser() {
        editor.remove("user");
        editor.commit();
    }

    public void saveObject(Object object,String objectName) {
        String json = gson.toJson(object);
        editor.putString(objectName, json);
        editor.commit();
    }
    public Object getObject(String objectName,Class clas) {
        String json = storage.getString(objectName, "");
        return gson.fromJson(json,clas);
    }

    public void saveDeviceData(CheckinDevice cd) {
        saveInteger(cd.deviceDPS.size(),cd.device.name+"DataCount");
        for (int i=0;i<cd.deviceDPS.size();i++) {
            DeviceDP dp = cd.deviceDPS.get(i);
            String json = gson.toJson(dp);
            editor.putString(cd.device.name+"Data"+i,json);
            editor.commit();
        }
    }
    public List<DeviceDP> getDeviceData(CheckinDevice cd) {
        List<DeviceDP> dps = new ArrayList<>();
        int count = getInteger(cd.device.name+"DataCount");
        for (int i=0;i<count;i++) {
            String json = storage.getString(cd.device.name+"Data"+i,"");
            dps.add(gson.fromJson(json,DeviceDP.class));
        }
        return dps;
    }
    public void deleteDeviceData(CheckinDevice cd) {
        int count = getInteger(cd.device.name+"DataCount");
        for (int i=0;i<count;i++) {
            editor.remove(cd.device.name+"Data"+i);
            editor.commit();
        }
        editor.remove(cd.device.name+"DataCount");
        editor.commit();
    }

    public void saveString(String object,String objectName) {
        editor.putString(objectName, object);
        editor.commit();
    }
    public String getString(String objectName) {
        return storage.getString(objectName, null);
    }

    public void saveInteger(int object,String objectName) {
        editor.putInt(objectName, object);
        editor.commit();
    }
    public int getInteger(String objectName) {
        return storage.getInt(objectName, -1);
    }

    public void saveBoolean(boolean object,String objectName) {
        editor.putBoolean(objectName, object);
        editor.commit();
    }
    public boolean getBoolean(String objectName) {
        return storage.getBoolean(objectName, false);
    }

    public void deleteObject(String objectName) {
        editor.remove(objectName);
        editor.commit();
    }

    public void deleteAll() {
        deleteProject();
        PROJECT_VARIABLES.deleteProjectVariablesFromStorage(this);
        Tuya.deleteHomesFromLocalStorage(this);
    }
}
