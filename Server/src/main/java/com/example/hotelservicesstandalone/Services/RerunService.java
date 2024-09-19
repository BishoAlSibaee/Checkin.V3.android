package com.example.hotelservicesstandalone.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hotelservicesstandalone.Login;

public class RerunService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("workingAlarm", "service trying to rerun ");
        this.startActivity(new Intent(this, Login.class));
        this.stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
