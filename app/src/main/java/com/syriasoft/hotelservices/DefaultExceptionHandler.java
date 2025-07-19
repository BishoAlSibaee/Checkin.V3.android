package com.syriasoft.hotelservices;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

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
        Log.d("unExpectedCrash",act.getLocalClassName()+" "+MyApp.ProjectName+" "+MyApp.Room.RoomNumber+" "+time);
        Intent i = new Intent(act,ErrorService.class);
        i.putExtra("project",MyApp.ProjectName);
        i.putExtra("room",MyApp.Room.RoomNumber);
        i.putExtra("errorMsg",e.getMessage());
        i.putExtra("activityName",act.getLocalClassName()+" "+MyApp.applicationSide);
        act.startService(i);
        Intent intent = new Intent(act, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(MyApp.App.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        AlarmManager mgr = (AlarmManager) MyApp.App.getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,pendingIntent);
        act.finish();            //Stopping application
        System.exit(2);
    }
}
