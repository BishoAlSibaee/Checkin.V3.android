package com.syriasoft.server.Services;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.syriasoft.server.Classes.ControlDevice;
import com.syriasoft.server.Classes.LocalDataStore;
import com.syriasoft.server.Classes.PROJECT;
import com.syriasoft.server.Interface.GetDeviceLastWorkingTime;
import com.syriasoft.server.Login;

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
        Log.d("workingAlarm", context.getApplicationContext().getPackageName());
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
                    restartApp(context);
                }
                else {
                    setAlarm(context);
                }
            }

            @Override
            public void onError(String error) {
                Log.d("checkWorking","error getting value "+error);
            }
        });
    }

    void rerunApplication(Context context) {
        JobInfo jobInfo = new JobInfo.Builder(33,
                new ComponentName(context, RerunJobService.class))
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(900000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
        Log.d("workingAlarm","start job service");
    }

    void setAlarm(Context context) {
        Intent intent = new Intent(context, checkWorkingReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000*60) , pendingIntent);
        Log.d("workingAlarm","alarm set");
    }

    void restartApp(Context context) {
        Intent i = new Intent(context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
