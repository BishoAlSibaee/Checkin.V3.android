package com.syriasoft.house_keeping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.syriasoft.hotelservices.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class messagingService extends FirebaseMessagingService {
    public final static String KEY_MyRooms = "MyRooms";
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_NAME = "MyPref";
    private NotificationManager notificationManager;
    String rooms;
    String[] arrRoom;
    String Project;
    String URL;
    UserDB db;
    String KEY_PROJECT = "proj";

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        Project = sharedPreferences.getString(KEY_PROJECT, null);
        URL = "https://ratco-solutions.com/Checkin/" + Project + "/php/";
        db = new UserDB(this);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {// ...
        rooms = sharedPreferences.getString(KEY_MyRooms, null);
        arrRoom = rooms.split("-");
        Random r = new Random();
        int reqCode = r.nextInt();
        if (remoteMessage.getData().get("title") != null && remoteMessage.getData().get("message") != null) {
            String TITLE = remoteMessage.getData().get("title");
            String MESSAGE = remoteMessage.getData().get("message");
            Log.d("IncomingMessage", "title: "+TITLE + " message: " +MESSAGE);
            boolean roomExists = false;
            if (remoteMessage.getData().get("RoomNumber") != null) {
                int roomNumber = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("RoomNumber")));
                if (arrRoom != null) {
                    for (String s : arrRoom) {
                        if (Integer.parseInt(s) == (roomNumber)) {
                            roomExists = true;
                            break;
                        }
                    }
                }
            }
            if (roomExists) {
                Log.d("IncomingMessage", "room found");
                String roomNumber = Objects.requireNonNull(remoteMessage.getData().get("RoomNumber"));
                Notification n = Notification.searchNotification(MyApp.notifications,roomNumber,Notification.getNotificationOrder(Objects.requireNonNull(TITLE)));
                if (Objects.requireNonNull(remoteMessage.getData().get("title")).contains("SOS") && Objects.requireNonNull(remoteMessage.getData().get("message")).contains("New")) {
                    Log.d("IncomingMessage", "sos found");
                    Intent i = new Intent(getApplicationContext(), SOSService.class);
                    startService(i);
                    if (n == null) {
                        showNotification(getApplicationContext(), remoteMessage.getData().get("title"), remoteMessage.getData().get("message"),reqCode);
                        MyApp.notifications.add(new Notification(reqCode,roomNumber,TITLE,MESSAGE));
                    }
                    else {
                        Notification.removeNotification(MyApp.notifications,n.reqCode);
                        notificationManager.cancel(n.reqCode);
                    }
                }
                else {
                    if (n == null) {
                        Log.d("IncomingMessage", "notification not found");
                        showNotification(getApplicationContext(), TITLE, MESSAGE,reqCode);
                        MyApp.notifications.add(new Notification(reqCode,roomNumber,TITLE,MESSAGE));
                    }
                    else {
                        Log.d("IncomingMessage", "notification found");
                        Log.d("IncomingMessage", n.reqCode+" "+n.room+" "+n.title+" "+n.message);
                        if (Notification.getNotificationType(n.message) != Notification.getNotificationType(Objects.requireNonNull(MESSAGE))) {
                            Notification.removeNotification(MyApp.notifications,n.reqCode);
                            notificationManager.cancel(n.reqCode);
                            showNotification(getApplicationContext(), TITLE, MESSAGE,reqCode);
                            MyApp.notifications.add(new Notification(reqCode,roomNumber,TITLE,MESSAGE));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("tokenChanged","token");
        sendRegistrationToServer(token);
    }

    void sendRegistrationToServer(final String token) {
        String urlBase = sharedPreferences.getString("URL", null);
        String id = sharedPreferences.getString("id", null);
        if (urlBase != null) {
            String url = MyApp.MyProject.url + "users/modifyUserFirebaseToken";
            StringRequest r = new StringRequest(Request.Method.POST, url, response -> Log.d("TokenResp", response), error -> Log.d("TokenResp", error.toString())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", token);
                    params.put("id", id);
                    return params;
                }
            };
            Volley.newRequestQueue(this).add(r);
        }
    }

    public void showNotification(Context context, String title, String message,int reqCode) {
        String CHANNEL_ID = "channel_name";// The id of the channel.
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_sound);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setColor(Color.parseColor("#0E223B"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setSound(soundUri, null);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }
}