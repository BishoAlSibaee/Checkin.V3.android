package com.example.hotelservicesstandalone.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class LocalDataStore {

    SharedPreferences storage;
    SharedPreferences.Editor editor;
    Gson gson;

    public LocalDataStore(Activity context) {
        storage = context.getPreferences(MODE_PRIVATE);
        editor = storage.edit();
        gson = new Gson();
    }

    public void saveObject(Object object,String objectName) {
        String json = gson.toJson(object);
        editor.putString(objectName, json);
        editor.commit();
    }

    public void saveControlDevice(ControlDevice object,String objectName) {
        String json = gson.toJson(object);
        editor.putString(objectName, json);
        editor.commit();
    }

    public void deleteControlDevice(String deviceName) {
        editor.remove(deviceName);
        editor.commit();
    }

    public void saveProject(PROJECT project, String objectName) {
        String json = gson.toJson(project);
        editor.putString(objectName, json);
        editor.commit();
    }

    public void saveString(String object,String objectName) {
        editor.putString(objectName, object);
        editor.commit();
    }

    public void saveInteger(int object,String objectName) {
        editor.putInt(objectName, object);
        editor.commit();
    }

    public void saveBoolean(boolean object,String objectName) {
        editor.putBoolean(objectName, object);
        editor.commit();
    }

    public Object getObject(String objectName,Class clas) {
        String json = storage.getString(objectName, "");
        return gson.fromJson(json,clas);
    }

    public ControlDevice getControlDevice(String objectName) {
        String json = storage.getString(objectName, null);
        if (json == null) {
            return null;
        }
        else {
            return gson.fromJson(json,ControlDevice.class);
        }
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

    public Object getString(String objectName) {
        return storage.getString(objectName, null);
    }

    public Object getInteger(String objectName) {
        return storage.getInt(objectName, -1);
    }

    public Object getBoolean(String objectName) {
        return storage.getBoolean(objectName, false);
    }
}
