package com.syriasoft.server.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.syriasoft.server.Classes.DefaultExceptionHandler;
import com.syriasoft.server.Classes.Devices.CheckinDevice;
import com.syriasoft.server.Classes.DevicesDataDB;
import com.syriasoft.server.Classes.Interfaces.DevicesListenerWatcherCallback;
import com.syriasoft.server.Classes.Interfaces.GerRoomsCallback;
import com.syriasoft.server.Classes.Interfaces.GetBuildingsCallback;
import com.syriasoft.server.Classes.Interfaces.GetDevicesCallback;
import com.syriasoft.server.Classes.Interfaces.GetFloorsCallback;
import com.syriasoft.server.Classes.Interfaces.getDeviceDataCallback;
import com.syriasoft.server.Classes.LocalDataStore;
import com.syriasoft.server.Classes.PROJECT_VARIABLES;
import com.syriasoft.server.Classes.Property.Building;
import com.syriasoft.server.Classes.Property.Floor;
import com.syriasoft.server.Classes.Property.PropertyDB;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Tuya;
import com.syriasoft.server.Dialogs.MessageDialog;
import com.syriasoft.server.Interface.RequestCallback;
import com.syriasoft.server.Login;
import com.syriasoft.server.MyApp;
import com.syriasoft.server.Rooms;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ServerService2 extends Service {
    public static Service ser;
    DevicesDataDB db;
    LocalDataStore storage;
    RequestQueue REQ, REQ1, CLEANUP_QUEUE, LAUNDRY_QUEUE, CHECKOUT_QUEUE, DND_Queue, FirebaseTokenRegister;
    List<CheckinDevice> Devices;
    DatabaseReference ServerDevice, ProjectVariablesRef, DevicesControls, ProjectDevices;
    FirebaseDatabase database;
    PropertyDB pDB;
    public static boolean isWorking = false;
    static int reqCode = 10;
    static NotificationChannel channel;

    public ServerService2() {
        ser = this;
        db = new DevicesDataDB(MyApp.app);
        pDB = new PropertyDB(MyApp.app);
        storage = new LocalDataStore();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceLife","2 on create");
        MyApp.controlDeviceMe = storage.getControlDevice("controlDevice");
        MyApp.My_PROJECT = storage.getProject("project");
        defineRequestQueues();
        defineLists();
        setFirebaseReferences();
        setServerDeviceRunningFunction();
        //subscribeToTopic();
        getFirebaseTokenContinually();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ServiceLife","2 bind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceLife","2 start command");
        isWorking = true;
        PendingIntent pIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0,new Intent(intent), PendingIntent.FLAG_IMMUTABLE);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this,pIntent));
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("start")) {
                    startForegroundService("service started");
                    Log.d("MessageRecieved" , Rooms.activityRunning+"");
                    if (!Rooms.activityRunning) {
                        Intent i = new Intent(getBaseContext(), Login.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                    gettingAndPreparingData();
                }
                else if (intent.getAction().equals("stop")) {
                    stopForegroundService();
                }
            }
        }
        else {
            startForegroundService("service 2 started");
            gettingAndPreparingData();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isWorking = false;
    }

    void defineRequestQueues() {
        REQ = Volley.newRequestQueue(ser);
        REQ1 = Volley.newRequestQueue(ser);
        CLEANUP_QUEUE = Volley.newRequestQueue(ser);
        LAUNDRY_QUEUE = Volley.newRequestQueue(ser);
        CHECKOUT_QUEUE = Volley.newRequestQueue(ser);
        DND_Queue = Volley.newRequestQueue(ser);
        FirebaseTokenRegister = Volley.newRequestQueue(ser);
    }

    void defineLists() {
        Devices = new ArrayList<>();
    }

    void setFirebaseReferences() {
        database = FirebaseDatabase.getInstance(MyApp.firebaseDBUrl);//https://hotelservices-ebe66.firebaseio.com/
        ServerDevice = database.getReference(MyApp.My_PROJECT.projectName+"ServerDevices/"+MyApp.controlDeviceMe.name);
        ProjectVariablesRef = database.getReference(MyApp.My_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.My_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.My_PROJECT.projectName+"Devices");
    }

    void gettingAndPreparingData() {
        Log.d("bootingOp","getting data");
        PROJECT_VARIABLES.getProjectVariables(REQ,storage,new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootingOp","variables done");
                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
                if (!PROJECT_VARIABLES.getIsProjectVariablesSaved(storage)) {
                    PROJECT_VARIABLES.saveProjectVariablesToStorage(storage);
                }
                Building.getBuildings(REQ,pDB, new GetBuildingsCallback() {
                    @Override
                    public void onSuccess(List<Building> buildings) {
                        Log.d("bootingOp","buildings done "+buildings.size());
                        MyApp.Buildings = buildings;
                        if (!pDB.isBuildingsInserted()) {
                            PropertyDB.insertAllBuildings(MyApp.Buildings,pDB);
                        }
                        Floor.getFloors(REQ,pDB, new GetFloorsCallback() {
                            @Override
                            public void onSuccess(List<Floor> floors) {
                                Log.d("bootingOp","floors done "+floors.size());
                                MyApp.Floors = floors;
                                if (!pDB.isFloorsInserted()) {
                                    PropertyDB.insertAllFloors(MyApp.Floors,pDB);
                                }
                                MyApp.controlDeviceMe.getMyRooms(ServerDevice,REQ,pDB, new GerRoomsCallback() {
                                    @Override
                                    public void onSuccess(List<Room> rooms) {
                                        Log.d("bootingOp","rooms done "+rooms.size());
                                        MyApp.ROOMS = rooms;
                                        Room.sortRoomsByNumber(MyApp.ROOMS);
                                        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
                                        Room.setRoomsFireRooms(MyApp.ROOMS,database);
                                        if (!pDB.isRoomsInserted()) {
                                            PropertyDB.insertAllRooms(MyApp.ROOMS,pDB);
                                        }
                                        Tuya.loginTuya(MyApp.My_PROJECT, new ILoginCallback() {
                                            @Override
                                            public void onSuccess(User user) {
                                                Log.d("bootingOp","tuya login done");
                                                MyApp.TuyaUser = user;
                                                Tuya.getProjectHomes(MyApp.My_PROJECT,storage, new ITuyaGetHomeListCallback() {
                                                    @Override
                                                    public void onSuccess(List<HomeBean> homeBeans) {
                                                        Log.d("bootingOp","tuya project homes done "+homeBeans.size());
                                                        MyApp.PROJECT_HOMES = homeBeans;
                                                        if (Tuya.getHomesFromStorage(storage).isEmpty()) {
                                                            Tuya.saveHomesToStorage(storage,homeBeans);
                                                        }
                                                        Tuya.getDevicesNoTimers(homeBeans,MyApp.ROOMS, new GetDevicesCallback() {
                                                            @Override
                                                            public void devices(List<CheckinDevice> devices) {
                                                                Log.d("bootingOp","tuya devices done "+devices.size());
                                                                Devices = devices;
                                                                Tuya.gettingInitialDevicesData(Devices,db, new getDeviceDataCallback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("bootingOp","getting initial done");
                                                                        Log.d("devicesIds",Tuya.devicesIds.size()+" "+Devices.size());
                                                                        settingInitialDevicesData(Devices);
                                                                        setAllListeners();
                                                                        Tuya.setDevicesListenersWatcher(setDevicesListenersCallback());
                                                                        Log.d("bootingOp","finish");
                                                                        startForegroundService("service 2 booting done");
                                                                    }

                                                                    @Override
                                                                    public void onError(String error) {
                                                                        Log.d("bootingOp","getting devices data failed "+error);
                                                                        restartWhenErrorGettingData("getting devices data failed "+error);
                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onError(String error) {
                                                                Log.d("bootingOp","getting devices failed "+error);
                                                                restartWhenErrorGettingData("getting devices failed "+error);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(String errorCode, String error) {
                                                        Log.d("bootingOp","getting homes failed "+error);
                                                        restartWhenErrorGettingData("getting homes failed "+error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String code, String error) {
                                                Log.d("bootingOp","getting login t failed "+error);
                                                restartWhenErrorGettingData("getting login t failed "+error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.d("bootingOp","getting rooms failed "+error);
                                        restartWhenErrorGettingData("getting rooms failed "+error);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                Log.d("bootingOp","getting floors failed "+error);
                                restartWhenErrorGettingData("getting floors failed "+error);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("bootingOp","getting buildings failed "+error);
                        restartWhenErrorGettingData("getting buildings failed "+error);
                    }
                });
            }

            @Override
            public void onFail(String error) {
                Log.d("bootingOp","getting variables failed "+error);
                restartWhenErrorGettingData("getting variables failed "+error);
            }
        });
    }

    void settingInitialDevicesData(List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            //cd.setInitialCurrentValues();
        }
    }

    void setAllListeners(TextView actionsNow) {
        Log.d("bootingOp","setting listeners");
        Room.setRoomsDevicesListener(MyApp.ROOMS,actionsNow,CLEANUP_QUEUE,LAUNDRY_QUEUE,CHECKOUT_QUEUE);
        Room.setRoomsFireRoomsListener(MyApp.ROOMS);
        Room.setRoomsFireRoomsDevicesControlListener(MyApp.ROOMS);
    }

    void setAllListeners() {
        Log.d("bootingOp","setting listeners");
        //Room.setRoomsDevicesListener(MyApp.ROOMS,CLEANUP_QUEUE,LAUNDRY_QUEUE,CHECKOUT_QUEUE);
        Room.setRoomsFireRoomsListener(MyApp.ROOMS);
        Room.setRoomsFireRoomsDevicesControlListener(MyApp.ROOMS);
    }

    private void startForegroundService(String serviceText) {
        Log.d("ServiceLife", "Start foreground service.");
        // Create notification default intent.
        Intent intent = new Intent(this, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (channel == null) {
                channel = new NotificationChannel("N1", "ServiceNotification", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Service Notification Foreground");
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
        }

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"N1");
        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("CHECKIN Service is On");
        bigTextStyle.bigText(serviceText);
        // Set big text style.
        builder.setStyle(bigTextStyle);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gateway);
        builder.setLargeIcon(largeIconBitmap);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);
        // Add Play button intent in notification.
        //Intent playIntent = new Intent(this, ServerService.class);
        //playIntent.setAction(ACTION_PLAY);
        //PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        //NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        //builder.addAction(playAction);
        // Add Pause button intent in notification.
        //Intent pauseIntent = new Intent(this, ServerService.class);
        //pauseIntent.setAction(ACTION_PAUSE);
        //PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        //NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        //builder.addAction(prevAction);
        // Build the notification.
        Notification notification = builder.build();
        // Start foreground service.
        startForeground(1, notification);
    }

    public static void putAction(String serviceText) {
        Log.d("ServiceLife", "Start foreground service.");
        // Create notification default intent.
        Intent intent = new Intent(ser, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ser, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (channel == null) {
                channel = new NotificationChannel("N1", "ServiceNotification", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Service Notification Foreground");
                NotificationManager manager = ser.getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ser,"N1");

        Calendar ca = Calendar.getInstance();
        String dateTime = ca.get(Calendar.YEAR)+"-"+ca.get(Calendar.MONTH)+"-"+ca.get(Calendar.DAY_OF_MONTH)+" "+ca.get(Calendar.HOUR_OF_DAY)+":"+ca.get(Calendar.MINUTE);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("CHECKIN Service is On");
        bigTextStyle.bigText(serviceText+" "+dateTime);
        // Set big text style.
        builder.setStyle(bigTextStyle);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(ser.getResources(), R.drawable.gateway);
        builder.setLargeIcon(largeIconBitmap);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);
        Notification notification = builder.build();
        // Start foreground service.
        ser.startForeground(1, notification);
    }

    private void stopForegroundService() {
        stopForeground(true);
        ser.stopSelf();
    }

    void setServerDeviceRunningFunction() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                long x = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                ServerDevice.child("working").setValue(x);
                setServerDeviceRunningFunction();
            }
        },1000*60);
    }

    void sendRegistrationToServer(String token) {
        String url = MyApp.My_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseToken" ;
        StringRequest re  = new StringRequest(Request.Method.POST, url, response -> Log.d("tokenRegister" , response), error -> Log.d("tokenRegister" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> par = new HashMap<>();
                par.put("token" , token);
                par.put("device_id", String.valueOf(MyApp.controlDeviceMe.id));
                return par;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(ser) ;
        }
        FirebaseTokenRegister.add(re);
    }
    void getFirebaseTokenContinually() {
        Log.d("tokenRegister" , "start");
        Timer t = new Timer() ;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("tokenRegister" , "run");
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("tokenRegister" , "task null "+ Objects.requireNonNull(task.getException()).getMessage());
                        return;
                    }
                    String token = task.getResult();
                    sendRegistrationToServer(token);
                    Log.d("tokenRegister" , "run finish");
                });
            }
        }, 0,1000*60*60*24);
    }

    DevicesListenerWatcherCallback setDevicesListenersCallback() {
        return new DevicesListenerWatcherCallback() {
            @Override
            public void onListenersStop() {
                if(Tuya.ListenersWorking) {
                    startForegroundService("devices listeners stop");
                }
                Tuya.ListenersWorking = false;
                PROJECT_VARIABLES.setDevicesListenersWorking(0);
                PROJECT_VARIABLES.addServerStop();
                Room.stopAllRoomListeners(MyApp.ROOMS);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    Log.d("ServiceLife","starting service");
//                    Intent i = new Intent(ser, ServerService.class);
//                    i.setAction("start");
//                    startForegroundService(i);
//                }
//                else {
//                    startService(new Intent(ser, ServerService.class));
//                }
//                ser.stopSelf();
                System.exit(0);
            }

            @Override
            public void onListenersWork() {
                if(!Tuya.ListenersWorking) {
                    startForegroundService("devices listeners working");
                }
                Tuya.ListenersWorking = true;
                PROJECT_VARIABLES.setDevicesListenersWorking(1);
                PROJECT_VARIABLES.addServerStart();
                Tuya.setDevicesListenersWorking(this);
            }
        };
    }

    void restartWhenErrorGettingData(String error) {
        startForegroundService(error);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startForegroundService("service started");
        gettingAndPreparingData();
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

    public static void stopService(Service service) {
        service.stopSelf();
    }

    void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("stop").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Subscribed";
                if (!task.isSuccessful()) {
                    msg = "Subscribe failed";
                    Log.d("subscribeTopic", msg);
                }
                Log.d("subscribeTopic", msg);
            }
        });
    }
}
