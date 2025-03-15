package com.syriasoft.server.Services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
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

public class RerunJobService extends JobService {

    String firebaseDBUrl = "https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app";
    ControlDevice controlDevice;
    PROJECT project;
    LocalDataStore storage;
    FirebaseDatabase database ;
    DatabaseReference ServerDevice;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("workingAlarm", "job service started");
        rerunApplication();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    void getProjectAndDevice() {
        storage = new LocalDataStore();
        controlDevice = storage.getControlDevice("controlDevice");
        project = storage.getProject("project");
        database = FirebaseDatabase.getInstance(firebaseDBUrl);
        ServerDevice = database.getReference(project.projectName+"ServerDevices/"+controlDevice.name);
    }

    void checkWorking() {
        Long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        controlDevice.getLastDeviceWorking(ServerDevice,new GetDeviceLastWorkingTime() {
            @Override
            public void onSuccess(Long time) {
                Calendar ca = Calendar.getInstance();
                ca.setTimeInMillis(time);
                Log.d("workingAlarm","last working value "+time+" "+ca.get(Calendar.HOUR_OF_DAY)+":"+ca.get(Calendar.MINUTE)+":"+ca.get(Calendar.SECOND));
                if (now > (time+(3000*60))) {
                    Log.d("workingAlarm","Device is stop");
                    rerunApplication();
                }
                setAlarm();
            }

            @Override
            public void onError(String error) {
                Log.d("checkWorking","error getting value "+error);
            }
        });
    }

    void rerunApplication() {
        Intent dialogIntent = new Intent(this, Login.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
        Log.d("workingAlarm", "service trying to rerun ");
    }

    void setAlarm() {
        JobInfo jobInfo = new JobInfo.Builder(33,
                new ComponentName(this, RerunJobService.class))
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(3 * 60 * 1000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
        Log.d("workingAlarm","alarm set");
    }
}
