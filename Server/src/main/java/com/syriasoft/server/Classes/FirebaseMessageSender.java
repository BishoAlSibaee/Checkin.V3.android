package com.syriasoft.server.Classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.server.Interface.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseMessageSender {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    static String url = "https://fcm.googleapis.com/v1/projects/checkin-62774/messages:send";
    private static final String contentType = "application/json";
    private static final String serverKey = "key=" + "AAAAXBAcZWU:APA91bHrhYpq6HfE6IsKIVF2mREXrOD0PuW5OpD6HhM92YTInukMF3AgzAhgmnvug3PGUhXhxfuIYWrF-lZ5dmBZh2iW-2mWRyrk6diP6YS4g-xfNVxPygqoLmOyJ92jbYhWfMEtzzJo";

    public static void  sendMessage(String Title , String Message , String token, RequestQueue queue, RequestCallback callback) {
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", Title);
            notificationBody.put("message", Message);
            notification.put("to", token);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.d("sendFirebaseMessage", Objects.requireNonNull(e.getMessage()));
            callback.onFail(Objects.requireNonNull(e.getMessage()));
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, FCM_MESSAGE_URL, notification, response -> {
                Log.d("sendFirebaseMessage", response.toString());
                callback.onSuccess();
            }, error -> {
            Log.d("sendFirebaseMessage", error.toString());
            callback.onFail(error.toString());
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };

        queue.add(req);
    }

    public static void  sendMessage(RequestQueue queue, String token, RequestCallback callback) {
        StringRequest req = new StringRequest(Request.Method.POST, "https://samples.checkin.ratco-solutions.com/api/reservations/sendReRunMessage", response -> {
            Log.d("sendFirebaseMessage", response);
            callback.onSuccess();
        }, error -> {
            Log.d("sendFirebaseMessage", error.toString());
            callback.onFail(error.toString());
        })
        {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("token",token);
                return params;
            }
        };
        queue.add(req);
    }

    public static void  sendMessageTopic(RequestQueue queue, String topic, RequestCallback callback) {
        JSONObject notification = new JSONObject();
        JSONObject message = new JSONObject();
        try {
            notification.put("title","reRun");
            message.put("topic",topic);
            message.put("notification",notification);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, message, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("sendFirebaseMessage", response.toString());
                callback.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sendFirebaseMessage", error.toString());
                callback.onFail(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        queue.add(req);
    }
}
