package com.syriasoft.projectscontrol;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.projectscontrol.Adapters.DevicesAdapter;
import com.syriasoft.projectscontrol.RequestCallBacks.DeviceActionListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ServerDevice {
    public String ProjectName;
    public int id;
    public String name;
    public String roomsIds;
    public int status;
    public String token;
    public boolean working;
    long checkValue = 0;
    int taskInterval = 2;
    public PROJECT Project;

    final Timer timer ;
    TimerTask task ;

    ServerDevice(String projectName,int id,String name,String roomsIds,int status,String token,boolean working,PROJECT p) {
        this.ProjectName = projectName;
        this.id = id;
        this.name = name;
        this.roomsIds = roomsIds;
        this.status = status;
        this.token = token;
        this.working = true;
        this.timer = new Timer();
        this.Project = p;
    }

    void setDeviceWorkingListener(DatabaseReference workingReference) {
        workingReference.child("working").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {
                    Log.d("workingTimer",snapshot.getValue()+"");
                    checkValue = Long.parseLong(snapshot.getValue().toString());
                    Log.d("workingTimer",checkValue+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void setDeviceWorkingTimer(DevicesAdapter adapter) {
        task =new TimerTask() {
            @Override
            public void run() {
                Log.d("workingTimer","checking value"+checkValue);
                long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                Log.d("workingTimer",now+" now");
                long interval = 1000*60*taskInterval;
                Log.d("workingTimer",interval+" interval");
                long nowInterval = now - checkValue ;
                Log.d("workingTimer",nowInterval+" now interval");
                if (nowInterval >= interval) {
                    working = false ;
                }
                else {
                    working = true;
                }
            }
        };
        timer.scheduleAtFixedRate(task,0, 1000*60*taskInterval);
    }

    void setDeviceWorkingListener(DatabaseReference workingReference, DeviceActionListener listener) {
        workingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    listener.actionDetected(Long.getLong(snapshot.getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
