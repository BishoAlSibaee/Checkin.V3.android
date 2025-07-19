package com.syriasoft.checkin.Classes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.syriasoft.checkin.Interface.RequestOrder;
import com.tuya.smart.android.common.utils.SHA256Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ZigbeeLock {

    public static void getTokenFromApi (String clientId, String secret, Context co, RequestOrder order) {
        Calendar c = Calendar.getInstance();
        String d = String.valueOf(c.getTimeInMillis());
        String url = "/v1.0/token?grant_type=1";
        String tokenUrl = "https://openapi.tuyaeu.com/v1.0/token?grant_type=1";

        String stringToSign = "GET" + "\n" + "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" + "\n" +"\n"+ url ;

        String sign = clientId+d+stringToSign;

        String signRequest = RequestSignUtils.Sha256Util.sha256HMAC(sign,secret).toUpperCase();
        Log.d("sha265",signRequest);

        JsonObjectRequest tokenReq = new JsonObjectRequest(Request.Method.GET, tokenUrl, null, response -> {
            Log.d("ticketRequestResult",response.toString());
            try {
                boolean status = response.getBoolean("success");
                if (status) {
                    JSONObject result = response.getJSONObject("result");
                    String token = result.getString("access_token");
                    order.onSuccess(token);
                }
                else {
                    order.onFailed("failed");
                }
            } catch (JSONException e) {
                order.onFailed(e.getMessage());
            }
        }
                , error -> order.onFailed(error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("t", d);
                params.put("sign_method", "HMAC-SHA256");
                params.put("sign", signRequest);
                return params;
            }
        };
        Volley.newRequestQueue(co).add(tokenReq);
    }

    public static void getTicketId(String token,String clientId,String secret,String deviceId,Context co,RequestOrder order) {
        Calendar c = Calendar.getInstance();
        String t = String.valueOf(c.getTimeInMillis());
        String url = "/v1.0/devices/"+deviceId+"/door-lock/password-ticket";
        String ticketUrl = "https://openapi.tuyaeu.com"+url;
        String nonce = "";
        String stringToSign = "POST\n"+
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n"+
                "\n"+
                url;

        String str = clientId + token + t + nonce + stringToSign ;

        String sign = RequestSignUtils.Sha256Util.sha256HMAC(str,secret).toUpperCase();
        JsonObjectRequest tokenReq = new JsonObjectRequest(Request.Method.POST, ticketUrl, null, response -> {
            Log.d("ticketRequestResult",response.toString());
            try {
                boolean status = response.getBoolean("success");
                if (status) {
                    JSONObject result = response.getJSONObject("result");
                    String ticketId = result.getString("ticket_id");
                    order.onSuccess(ticketId);
                }
                else {
                    order.onFailed("failed");
                }
            } catch (JSONException e) {
                order.onFailed(e.getMessage());
            }
        }
                , error -> order.onFailed(error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("t", t);
                params.put("sign_method", "HMAC-SHA256");
                params.put("sign", sign);
                params.put("access_token",token);
                return params;
            }

            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", deviceId);
                return params;
            }
        };
        Volley.newRequestQueue(co).add(tokenReq);
    }

    public static void unlockWithoutPassword(String token,String ticketId,String clientId,String secret,String deviceId,Context co,RequestOrder order) {
        Calendar c = Calendar.getInstance();
        String t = String.valueOf(c.getTimeInMillis());
        String url = "/v1.0/devices/"+deviceId+"/door-lock/password-free/open-door";
        String unlockUrl = "https://openapi.tuyaeu.com"+url;
        String nonce = "";
        String contentSHA256;
        JSONObject params = new JSONObject();
        try {
            params.put("ticket_id", ticketId);
        } catch (JSONException e) {
            order.onFailed(e.getMessage());
        }

        contentSHA256 = SHA256Util.sha256(params.toString().getBytes(StandardCharsets.UTF_8));

        String stringToSign = "POST\n"+
                contentSHA256+"\n"+  //"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
                "\n"+
                url;

        String str = clientId + token + t + nonce + stringToSign ;

        String signn = RequestSignUtils.Sha256Util.sha256HMAC(str,secret).toUpperCase();

        JsonObjectRequest tokenReq = new JsonObjectRequest(Request.Method.POST, unlockUrl, params, response -> {
            Log.d("ticketRequestResult",response.toString());
            try {
                boolean status = response.getBoolean("success");
                if (status) {
                    order.onSuccess("success");
                }
                else {
                    order.onFailed("failed");
                }
            } catch (JSONException e) {
                order.onFailed(e.getMessage());
            }
        }
                , error -> order.onFailed(error.toString())) {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("t", t);
                params.put("sign_method", "HMAC-SHA256");
                params.put("sign", signn);
                params.put("access_token",token);
                return params;
            }

        };
        Volley.newRequestQueue(co).add(tokenReq);
    }
}

