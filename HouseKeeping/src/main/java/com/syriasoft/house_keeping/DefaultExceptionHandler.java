package com.syriasoft.house_keeping;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;
import java.util.Locale;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    Activity act ;

    DefaultExceptionHandler(Activity act) {
        this.act = act ;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d("unExpectedCrash",e.getMessage());
        long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        Log.d("unExpectedCrash",act.getLocalClassName()+" "+MyApp.MyProject.projectName+" "+0+" "+time);
        Intent i = new Intent(act,ErrorService.class);
        i.putExtra("project",MyApp.MyProject.projectName);
        i.putExtra("room",0);
        i.putExtra("errorMsg",e.getMessage());
        i.putExtra("activityName",act.getLocalClassName()+" "+MyApp.applicationSide);
        act.startService(i);
        Intent intent = new Intent(act, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) MyApp.app.getBaseContext().getSystemService(Context.ALARM_SERVICE);
        if (ActivityCompat.checkSelfPermission(act,Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(act,new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},200);
            }
        }
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,pendingIntent);
        act.finish();            //Stopping application
        System.exit(2);
    }
}
