package com.example.hotelservicesstandalone.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hotelservicesstandalone.Classes.ControlDevice;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.PROJECT;
import com.example.hotelservicesstandalone.Interface.GetDeviceLastWorkingTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class checkWorkingReceiver extends BroadcastReceiver {

    String firebaseDBUrl = "https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app";
    ControlDevice controlDevice;
    PROJECT project;
    LocalDataStore storage;
    FirebaseDatabase database ;
    DatabaseReference ServerDevice;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("workingAlarm","receive");
        Log.d("workingAlarm", context.getClass().getName());
        getProjectAndDevice();
        checkWorking(context);
    }

    void getProjectAndDevice() {
        storage = new LocalDataStore();
        controlDevice = storage.getControlDevice("controlDevice");
        project = storage.getProject("project");
        database = FirebaseDatabase.getInstance(firebaseDBUrl);
        ServerDevice = database.getReference(project.projectName+"ServerDevices/"+controlDevice.name);
    }

    void checkWorking(Context context) {
        Long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        controlDevice.getLastDeviceWorking(ServerDevice,new GetDeviceLastWorkingTime() {
            @Override
            public void onSuccess(Long time) {
                Calendar ca = Calendar.getInstance();
                ca.setTimeInMillis(time);
                Log.d("workingAlarm","last working value "+time+" "+ca.get(Calendar.HOUR_OF_DAY)+":"+ca.get(Calendar.MINUTE)+":"+ca.get(Calendar.SECOND));
                if (now > (time+(3000*60))) {
                    Log.d("workingAlarm","Device is stop");
                    rerunApplication(context);
                }
                setAlarm(context);
            }

            @Override
            public void onError(String error) {
                Log.d("checkWorking","error getting value "+error);
            }
        });
    }

    void rerunApplication(Context context) {
        try {
            Log.d("workingAlarm", context.getClass().getName());
            Intent i = new Intent("com.syriasoft.server.Login");
            //i.setClassName("com.syriasoft.server","com.syriasoft.server.Login");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            Log.d("workingAlarm", "rerun trying . . ");
        } catch (Exception e) {
            Log.d("workingAlarm", "rerun error " + e.getMessage());
        }

//        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.myapp.demo");
//        if (launchIntent != null) {
//            context.startActivity(launchIntent);
//        }
    }

    void setAlarm(Context context) {
        Intent intent = new Intent(context, checkWorkingReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000*60) , pendingIntent);
        Log.d("workingAlarm","alarm set");
    }
}
