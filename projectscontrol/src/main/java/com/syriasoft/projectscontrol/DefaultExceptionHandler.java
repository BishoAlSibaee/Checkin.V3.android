package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    Activity act ;

    DefaultExceptionHandler(Activity act) {
        this.act = act ;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d("unExpectedCrash",e.getMessage());
        Intent intent = new Intent(this.act, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        AlarmManager mgr = (AlarmManager) MyApp.app.getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,pendingIntent);
        act.finish();            //Stopping application
        System.exit(2);
    }
}
