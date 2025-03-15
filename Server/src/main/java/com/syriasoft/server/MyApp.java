package com.syriasoft.server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.Volley;
import com.syriasoft.server.Classes.ControlDevice;
import com.syriasoft.server.Classes.Interfaces.GetProjectsCallback;
import com.syriasoft.server.Classes.LocalDataStore;
import com.syriasoft.server.Classes.PROJECT;
import com.syriasoft.server.Classes.Property.Building;
import com.syriasoft.server.Classes.Property.Floor;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyApp  extends Application {

    public static String Running_Tag = "RunningApplication";
    public static PROJECT My_PROJECT;
    public static ControlDevice controlDeviceMe;

    public static Application app ;
    public static User TuyaUser ;
    public static List<HomeBean> PROJECT_HOMES;
    public static String firebaseDBUrl = "https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app";
    public static List<Building> Buildings;
    public static List<Floor> Floors;
    public static List<Room> ROOMS ;
    public static List<Suite> suites ;
    public static String ErrorsUrl = "https://ratco-solutions.com/Checkin/Test/php/insertError.php";
    public static String applicationSide = "ServerApp";
    public static boolean isInternetConnected;
    public static List<Activity> Activities;

    public static LocalDataStore storage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("appLife","onCreate");
        app = this ;
        setTuyaApplication();
        PROJECT_HOMES = new ArrayList<>();
        Activities = new ArrayList<>();
        app.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.d("appLife","activity started "+activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.d("appLife","activity paused "+activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d("appLife","activity stopped "+activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d("appLife","activity destroyed "+activity.getLocalClassName());
                if (activity.getLocalClassName().equals("Rooms")) {
//                    for (Activity act : Activities) {
//                        if (act.getLocalClassName().equals("Rooms")) {
//                            Activities.remove(act);
//                        }
//                    }
                    Intent i = new Intent(activity,Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(i);
                }
                else if (activity.getLocalClassName().equals("ReceptionScreen")) {
                    Log.d("appLife","ReceptionScreen destroyed");
                    Intent i = new Intent(activity,Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("appLife","terminated");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("appLife","low memory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("appLife","trim memory");
    }

    static void setTuyaApplication() {
        TuyaHomeSdk.init(app);
    }

    public static void finishActivities() {
        for (Activity act : Activities) {
            act.finishAndRemoveTask();
        }
    }

    public static void isNetworkAvailable(Application app, RequestCallback callback) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            isInternetAvailable(new RequestCallback() {
                @Override
                public void onSuccess() {
                    isInternetConnected = true;
                    callback.onSuccess();
                }

                @Override
                public void onFail(String error) {
                    isInternetConnected = false;
                    callback.onFail(error);
                }
            });
        }
        else {
            callback.onFail("no internet");
        }
    }

    static void isInternetAvailable(RequestCallback callback) {
        PROJECT.getProjects(Volley.newRequestQueue(app), new GetProjectsCallback() {
            @Override
            public void onSuccess(List<PROJECT> projects) {
                Log.d("bootingOp","internet available");
                callback.onSuccess();
            }

            @Override
            public void onError(String error) {
                Log.d("bootingOp","internet unavailable");
                callback.onFail(error);
            }
        });
    }

    public static void setLocalStorage(LocalDataStore storage) {
        MyApp.storage = storage;
    }

    public static LocalDataStore getLocalStorage() {
        return MyApp.storage ;
    }

    public static boolean deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            Log.d("appLife",dir.getAbsolutePath());
            deleteDir(dir);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteDir(File dir) {
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
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @SuppressLint("NewApi")
    public boolean otherMethod() {
        try {
            return deleteDirFiles(app.getCacheDir());
        } catch (Exception e) {
            Log.d("appLife","cash error "+e.getMessage());
            return false;
        }
    }

    boolean deleteDirFiles(File dir) {
        if (dir.isDirectory()) {
            Log.d("appLife","D "+dir.getName());
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.isFile()) {
                    Log.d("appLife","F "+f.getName());
                    if (!f.delete()) {
                        Log.d("appLife","F "+f.getName()+" delete failed");
                        return false;
                    }
                } else if (f.isDirectory()) {
                    Log.d("appLife","D "+f.getName());
                    deleteDirFiles(f);
                }
            }
        }
        else if (dir.isFile()) {
            Log.d("appLife","F "+dir.getName());
            if (!dir.delete()) {
                Log.d("appLife","F "+dir.getName()+" delete failed");
                return false;
            }
        }
        return true;
    }
}
