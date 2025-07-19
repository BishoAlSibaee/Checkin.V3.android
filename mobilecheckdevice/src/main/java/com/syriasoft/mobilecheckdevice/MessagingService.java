package com.syriasoft.mobilecheckdevice;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    private RequestQueue FirebaseTokenRegister ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("title") != null ) {
            Log.d("MessageRecieved" , remoteMessage.getData().get("title"));
        }
    }

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    void sendRegistrationToServer(String token) {
        SharedPreferences pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        String url = pref.getString("url", null);
        String deviceId = pref.getString("Device_Id", null);
        if (url != null) {
            String url0 = url + "roomsManagement/modifyServerDeviceFirebaseToken" ;
            StringRequest re  = new StringRequest(Request.Method.POST,url0, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("tokenRegister" , response) ;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("tokenRegister" , error.toString()) ;
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> par = new HashMap<String, String>();
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
