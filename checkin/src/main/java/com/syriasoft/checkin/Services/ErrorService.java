package com.syriasoft.checkin.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.syriasoft.checkin.Classes.ErrorRegister;
import com.syriasoft.checkin.Interface.RequestCallback;

import java.util.Calendar;
import java.util.Locale;

public class ErrorService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("unExpectedCrash","service started");
        String project = intent.getExtras().getString("project");
        int room = intent.getExtras().getInt("room");
        String errorMsg = intent.getExtras().getString("errorMsg");
        String activityName = intent.getExtras().getString("activityName");
        long x = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        ErrorRegister.insertError(this.getApplicationContext(), project, room, x, 0, errorMsg, activityName, new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("unExpectedCrash","error inserted");
                stopSelf();
            }

            @Override
            public void onFail(String error) {
                Log.d("unExpectedCrash",error);
                stopSelf();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("unExpectedCrash","service stopped");
    }
}
