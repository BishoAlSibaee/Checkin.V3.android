package com.syriasoft.hotelservices;


import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.Map;

public class messagingService extends FirebaseMessagingService {

    private RequestQueue FirebaseTokenRegister ;
    SharedPreferences pref ;

    public messagingService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       if (remoteMessage.getData().get("title") != null) {
           Log.d("messageReceived" , remoteMessage.getData().get("title"));
           String title =  remoteMessage.getData().get("title");
           if (title != null && title.equals("message")) {
               FullscreenActivity.openMessageDialog(remoteMessage.getData().get("message"));
           }
       }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        String roomId = pref.getString("RoomID", null);
        if (roomId != null) {
            sendRegistrationToServer(token,roomId);
        }

    }

    public void sendRegistrationToServer(final String token,String roomId) {
        Log.d("registerToken", "start");
        String url = MyApp.ProjectURL + "roomsManagement/modifyRoomFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, response -> Log.d("registerToken", response ), error -> Log.d("registerToken", error.toString() )) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("token",token);
                params.put("room_id",roomId);
                return params;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(this) ;
        }
        FirebaseTokenRegister.add(r);
    }

}
