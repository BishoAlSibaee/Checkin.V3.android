package com.syriasoft.projectscontrol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.syriasoft.projectscontrol.RequestCallBacks.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CloudMessageController {

    final static String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final static String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    final static String contentType = "application/json";
    static NotificationManager notificationManager;
    static int deviceOffWarningNotificationReqCode = 1020;

    public static void rerunDevice(ServerDevice sd,RequestQueue Q,RequestCallback callback) {
        makeMessage("reRun",sd,Q,callback);
    }

    static void makeMessage(String title,ServerDevice sd,RequestQueue Q,RequestCallback callback) {

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notification.put("to",sd.token);
            notification.put("data",notificationBody);
        } catch (JSONException e) {
            Log.d("sendRerun",e.getMessage());
        }
        sendNotification(notification,Q,callback);
    }

    static void sendNotification(final JSONObject notification , RequestQueue MessagesQueue, RequestCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FCM_MESSAGE_URL, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("sendRerun",response.toString());
                callback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sendRerun",error.toString());
                callback.onFailed(error.toString());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MessagesQueue.add(jsonObjectRequest);
    }

    static void makeDeviceOffWarningNotification(ServerDevice sd) {
        deviceOffWarningNotificationReqCode = new Random().nextInt(1000);
        notificationManager = (NotificationManager) MyApp.app.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(MyApp.app, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyApp.app, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(sd.ProjectName)
                .setContentText("Device "+sd.name+" of "+sd.ProjectName+" is off")
                .setAutoCancel(false)
                .setColor(Color.RED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(deviceOffWarningNotificationReqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }
}
