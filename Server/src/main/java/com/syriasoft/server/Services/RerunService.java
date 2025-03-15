package com.syriasoft.server.Services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.syriasoft.server.Login;

public class RerunService extends AccessibilityService {


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("accessibilityEvent","connected");
        Intent i = new Intent(this, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.disableSelf();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d("accessibilityEvent",accessibilityEvent.toString());
    }

    @Override
    public void onInterrupt() {
        Log.d("accessibilityEvent","interrupt");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("workingAlarm", "service trying to rerun ");
        Intent dialogIntent = new Intent(this, Login.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
        this.stopSelf();
        return START_STICKY;
    }

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
}
