package com.example.hotelservicesstandalone.Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Login;
import com.example.hotelservicesstandalone.MyApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessagingService extends FirebaseMessagingService {

    private RequestQueue FirebaseTokenRegister ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
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
                        Intent i = new Intent(this, Login.class);
                        startActivity(i);
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
        SharedPreferences pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        String url = pref.getString("url", null);
        String deviceId = pref.getString("Device_Id", null);
        if (url != null) {
            String url0 = url + "roomsManagement/modifyServerDeviceFirebaseToken" ;
            StringRequest re  = new StringRequest(Request.Method.POST,url0, response -> Log.d("tokenRegister" , response), error -> Log.d("tokenRegister" , error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> par = new HashMap<>();
                    par.put("token" , token);
                    par.put("device_id",deviceId);
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
