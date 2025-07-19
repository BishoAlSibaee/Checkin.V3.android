package com.syriasoft.server.Classes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.syriasoft.server.Dialogs.MessageDialog;
import com.syriasoft.server.Login;
import com.syriasoft.server.MyApp;
import com.syriasoft.server.Services.ErrorService;

import java.io.File;
import java.util.Objects;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    Service service ;
    Activity act;
    PendingIntent pendingIntent;

    public DefaultExceptionHandler(Service ser,PendingIntent pendingIntent) {
        service = ser ;
        this.pendingIntent = pendingIntent;
    }

    public DefaultExceptionHandler(Activity act, PendingIntent pendingIntent) {
        this.act = act ;
        this.pendingIntent = pendingIntent;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d("unExpectedCrash", Objects.requireNonNull(e.getMessage()));
        if (service != null) {
            Log.d("unExpectedCrash", "service");
            LocalDataStore storage = new LocalDataStore();
            PROJECT p = storage.getProject("project");
            Intent i = new Intent(service, ErrorService.class);
            i.putExtra("project",p.projectName);
            i.putExtra("room",0);
            i.putExtra("errorMsg",e.getMessage());
            i.putExtra("activityName",service.getClass().getName()+" "+MyApp.applicationSide);
            service.startService(i);
            Intent intent = new Intent(act, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager mgr = (AlarmManager) MyApp.app.getBaseContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,pendingIntent);
            service.stopSelf();
            MyApp.finishActivities(); //Stopping application
            trimCache(MyApp.app);
            System.exit(2);
        }
        else if (act != null) {
            Log.d("unExpectedCrash", "activity");
            LocalDataStore storage = new LocalDataStore();
            PROJECT p = storage.getProject("project");
            Intent i = new Intent(act, ErrorService.class);
            i.putExtra("project",p.projectName);
            i.putExtra("room",0);
            i.putExtra("errorMsg",e.getMessage());
            i.putExtra("activityName",act.getClass().getName()+" "+MyApp.applicationSide);
            act.startService(i);
            Intent intent = new Intent(act, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager mgr = (AlarmManager) MyApp.app.getBaseContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,pendingIntent);
            MyApp.finishActivities(); //Stopping application
            trimCache(MyApp.app);
            System.exit(2);
        }
    }

    public boolean trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            return true;
        } catch (Exception e) {
            new MessageDialog(e.getMessage(),"error",context);
            return false;
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        }
        else {
            return false;
        }
    }
}
