package com.example.hotelservicesstandalone.Classes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hotelservicesstandalone.Login;
import com.example.hotelservicesstandalone.MyApp;
import com.example.hotelservicesstandalone.Services.ErrorService;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    Activity act ;

    public DefaultExceptionHandler(Activity act) {
        this.act = act ;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d("unExpectedCrash", Objects.requireNonNull(e.getMessage()));
        long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        Log.d("unExpectedCrash",act.getLocalClassName()+" "+ MyApp.My_PROJECT.projectName+" "+0+" "+time);
        Intent i = new Intent(act, ErrorService.class);
        i.putExtra("project",MyApp.My_PROJECT.projectName);
        i.putExtra("room",0);
        i.putExtra("errorMsg",e.getMessage());
        i.putExtra("activityName",act.getLocalClassName()+" "+MyApp.applicationSide);
        act.startService(i);
        Intent intent = new Intent(act, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) MyApp.app.getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,pendingIntent);
        act.finish();            //Stopping application
        System.exit(2);
    }
}
