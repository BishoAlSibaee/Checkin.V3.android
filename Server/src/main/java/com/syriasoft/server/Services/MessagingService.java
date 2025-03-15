package com.syriasoft.server.Services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.syriasoft.server.Classes.LocalDataStore;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Login;
import com.syriasoft.server.MyApp;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessagingService extends FirebaseMessagingService {

    private RequestQueue FirebaseTokenRegister ;
    LocalDataStore storage;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("MessageRecieved" , "message arrived");
        Log.d(MyApp.Running_Tag,"message received successfully");
        if (remoteMessage.getData().get("title") != null ) {
            Log.d("MessageRecieved" , Objects.requireNonNull(remoteMessage.getData().get("title")));
            String title =  remoteMessage.getData().get("title");
            if (title != null) {
                switch (title) {
                    case "poweroff": {
                        int roomNumber;
                        if (remoteMessage.getData().get("room") != null) {
                            roomNumber = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("room")));
                            Room r = Room.searchRoomInList(MyApp.ROOMS, roomNumber);
                            r.powerOffRoom(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        break;
                    }
                    case "poweron": {
                        int roomNumber;
                        if (remoteMessage.getData().get("room") != null) {
                            roomNumber = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("room")));
                            Room r = Room.searchRoomInList(MyApp.ROOMS, roomNumber);
                            r.powerOnRoom(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        break;
                    }
                    case "bycard": {
                        int roomNumber;
                        if (remoteMessage.getData().get("room") != null) {
                            roomNumber = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("room")));
                            Room r = Room.searchRoomInList(MyApp.ROOMS, roomNumber);
                            r.powerByCardRoom(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        break;
                    }
                    case "reRun":
                        Intent i = new Intent(getBaseContext(), Login.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        Log.d("MessageRecieved" , "start");
                        break;
                }
            }
        }
    }
    @Override
    public void onNewToken(@NonNull String token) {
        sendRegistrationToServer(token);
    }
    void sendRegistrationToServer(String token) {
        storage = new LocalDataStore();
        MyApp.My_PROJECT = storage.getProject("project");
        MyApp.controlDeviceMe = storage.getControlDevice("controlDevice");
        if (MyApp.My_PROJECT != null && MyApp.controlDeviceMe != null) {
            StringRequest re  = new StringRequest(Request.Method.POST,MyApp.My_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseToken", response -> Log.d("tokenRegister" , response), error -> Log.d("tokenRegister" , error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> par = new HashMap<>();
                    par.put("token" , token);
                    par.put("device_id", String.valueOf(MyApp.controlDeviceMe.id));
                    return par;
                }
            };
            if (FirebaseTokenRegister == null) {
                FirebaseTokenRegister = Volley.newRequestQueue(MyApp.app) ;
            }
            FirebaseTokenRegister.add(re);
        }

    }
}
