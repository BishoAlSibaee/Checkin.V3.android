package hotelservices.syriasoft.cleanup;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import hotelservices.syriasoft.cleanup.Interface.RequestCallback;

public  class ErrorRegister {

    static RequestQueue Q ;

    public static void insertError(Context c , String hotel, int room, long dateTime, int errorCode, String errorMsg, String errorCaption) {
        StringRequest request = new StringRequest(Request.Method.POST, MyApp.ErrorsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("unExpectedCrash",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("unExpectedCrash",error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("hotel" , hotel ) ;
                params.put("room" ,String.valueOf( room )) ;
                params.put("dateTime" , String.valueOf(dateTime));
                params.put("errorCode" , String.valueOf(errorCode));
                params.put("errorMsg" , errorMsg) ;
                params.put("caption" , errorCaption) ;
                 return params;
            }
        };
        if (Q == null) {
            Q = Volley.newRequestQueue(c);
        }
        Q.add(request);
    }

    public static void insertError(Context c , String hotel, int room, long dateTime, int errorCode, String errorMsg, String errorCaption, RequestCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, MyApp.ErrorsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("unExpectedCrash",response);
                callback.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("unExpectedCrash",error.toString());
                callback.onFail(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("hotel" , hotel ) ;
                params.put("room" ,String.valueOf( room )) ;
                params.put("dateTime" , String.valueOf(dateTime));
                params.put("errorCode" , String.valueOf(errorCode));
                params.put("errorMsg" , errorMsg) ;
                params.put("caption" , errorCaption) ;
                return params;
            }
        };
        if (Q == null) {
            Q = Volley.newRequestQueue(c);
        }
        Q.add(request);
    }
}
