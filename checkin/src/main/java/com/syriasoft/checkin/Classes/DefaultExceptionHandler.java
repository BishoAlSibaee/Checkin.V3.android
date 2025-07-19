package com.syriasoft.checkin.Classes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import com.syriasoft.checkin.Services.ErrorService;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    Activity act ;
    String projectName;
    Application app;
    Activity target;

    public DefaultExceptionHandler(Activity act, String projectName, Application app,Activity target) {
        this.act = act ;
        this.projectName = projectName;
        this.app = app;
        this.target = target;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d("unExpectedCrash", Objects.requireNonNull(e.getMessage()));
        long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        Log.d("unExpectedCrash",act.getLocalClassName()+" "+ projectName+" "+0+" "+time);
        Intent i = new Intent(act, ErrorService.class);
        i.putExtra("project",projectName);
        i.putExtra("room",0);
        i.putExtra("errorMsg",e.getMessage());
        i.putExtra("activityName",act.getLocalClassName()+" "+app.getApplicationInfo().name);
        act.startService(i);
        Intent intent = new Intent(act,target.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(app, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,pendingIntent);
        act.finish();            //Stopping application
        System.exit(2);
    }
}
