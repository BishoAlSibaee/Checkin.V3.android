package com.example.hotelservicesstandalone.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.example.hotelservicesstandalone.Classes.ControlDevice;
import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;
import com.example.hotelservicesstandalone.Classes.DevicesDataDB;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.PROJECT;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Interface.GetDeviceLastWorkingTime;
import com.example.hotelservicesstandalone.Login;
import com.example.hotelservicesstandalone.MyApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class KeepAliveService_Test extends Service {

    String firebaseDBUrl = "https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app";
    RequestQueue REQ,CHECKOUT_QUEUE,LAUNDRY_QUEUE,CLEANUP_QUEUE;
    FirebaseDatabase database ;
    DatabaseReference ServerDevice , ProjectVariablesRef , DevicesControls , ProjectDevices  ;
    List<CheckinDevice> Devices ;
    DevicesDataDB db;
    Service s;
    PROJECT project;
    ControlDevice controlDevice;
    LocalDataStore storage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ServiceLife","bind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceLife","on create");
        if (project != null && controlDevice != null) {
            setService();
            checkWorking();
        }
//        setService();
//        PROJECT_VARIABLES.getProjectVariables(project,REQ,new RequestCallback() {
//            @Override
//            public void onSuccess() {
//                Log.d("bootingOp","variables done");
//                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
//                Building.getBuildings(project,REQ, new GetBuildingsCallback() {
//                    @Override
//                    public void onSuccess(List<Building> buildings) {
//                        Log.d("bootingOp","buildings done "+buildings.size());
//                        MyApp.Buildings = buildings;
//                        Floor.getFloors(project,REQ, new GetFloorsCallback() {
//                            @Override
//                            public void onSuccess(List<Floor> floors) {
//                                Log.d("bootingOp","floors done "+floors.size());
//                                MyApp.Floors = floors;
//                                controlDevice.getMyRooms(project,ServerDevice,REQ, new GerRoomsCallback() {
//                                    @Override
//                                    public void onSuccess(List<Room> rooms) {
//                                        Log.d("bootingOp","rooms done "+rooms.size());
//                                        MyApp.ROOMS = rooms;
//                                        Room.sortRoomsByNumber(MyApp.ROOMS);
//                                        Room.setRoomsBuildingsAndFloors(MyApp.ROOMS,MyApp.Buildings,MyApp.Floors);
//                                        Room.setRoomsFireRooms(project,MyApp.ROOMS,database);
//                                        //TextView hotelName = act.findViewById(R.id.hotelName);
//                                       /// hotelName.setText(MessageFormat.format("{0} {1} room", MyApp.My_PROJECT.projectName, MyApp.ROOMS.size()));
//                                        Tuya.loginTuya(project, new ILoginCallback() {
//                                            @Override
//                                            public void onSuccess(User user) {
//                                                Log.d("bootingOp","tuya login done");
//                                                MyApp.TuyaUser = user;
//                                                Tuya.getProjectHomes(project, new ITuyaGetHomeListCallback() {
//                                                    @Override
//                                                    public void onSuccess(List<HomeBean> homeBeans) {
//                                                        Log.d("bootingOp","tuya project homes done "+homeBeans.size());
//                                                        MyApp.PROJECT_HOMES = homeBeans;
//                                                        Tuya.getDevices(homeBeans,MyApp.ROOMS, new GetDevicesCallback() {
//                                                            @Override
//                                                            public void devices(List<CheckinDevice> devices) {
//                                                                Log.d("bootingOp","tuya devices done "+devices.size());
//                                                                Devices = devices;
//                                                                Tuya.gettingInitialDevicesData(Devices,db, new getDeviceDataCallback() {
//                                                                    @Override
//                                                                    public void onSuccess() {
//                                                                        Log.d("bootingOp","getting initial done");
//                                                                        settingInitialDevicesData(Devices);
//                                                                        TextView actionsNow = new TextView(s);//act.findViewById(R.id.textView26);
//                                                                        //showRooms(act);
//                                                                        //showDevices(act);
//                                                                        setAllListeners(actionsNow);
//                                                                        //getSceneBGs();
//                                                                        //loading.stop();
//                                                                        //Tuya.setDevicesListenersWatcher(act);
//                                                                        Log.d("bootingOp","finish");
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onError(String error) {
//                                                                        //loading.stop();
//                                                                        //createRestartConfirmationDialog(act,"getting devices failed \n"+error);
//                                                                        Log.d("bootingOp","error getting initial data "+error);
//                                                                    }
//                                                                });
//
//                                                            }
//
//                                                            @Override
//                                                            public void onError(String error) {
//                                                                //loading.stop();
//                                                                //createRestartConfirmationDialog(act,"getting devices failed \n"+error);
//                                                                Log.d("bootingOp","error getting devices "+error);
//                                                            }
//                                                        });
//                                                    }
//
//                                                    @Override
//                                                    public void onError(String errorCode, String error) {
//                                                        //loading.stop();
//                                                        //createRestartConfirmationDialog(act,"getting homes failed \n"+error);
//                                                        Log.d("bootingOp","error getting homes "+error);
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onError(String code, String error) {
//                                                //loading.stop();
//                                                //createRestartConfirmationDialog(act,"login tuya failed \n"+error);
//                                                Log.d("bootingOp","error tuya login "+error);
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                       // loading.stop();
//                                        //createRestartConfirmationDialog(act,"getting rooms failed \n"+error);
//                                        Log.d("bootingOp","error getting rooms "+error);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(String error) {
//                                //loading.stop();
//                                //createRestartConfirmationDialog(act,"getting floors failed \n"+error);
//                                Log.d("bootingOp","error getting floors "+error);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        //loading.stop();
//                        //createRestartConfirmationDialog(act,"getting buildings failed \n"+error);
//                        Log.d("bootingOp","error getting buildings "+error);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFail(String error) {
//                //loading.stop();
//                //createRestartConfirmationDialog(act,"getting project variables failed \n"+error);
//                Log.d("bootingOp","error getting project variables "+error);
//            }
//        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceLife","start command");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceLife","destroy");
    }

    void setService() {
        s = this;
        storage = new LocalDataStore();
        getProjectAndDevice();
        setFirebaseReferences();
        //storage = new LocalDataStore();
        //getProjectAndDevice();
        //setFirebaseReferences();
        //defineLists();
        //db = new DevicesDataDB(this);
        //REQ = Volley.newRequestQueue(this);
        //CLEANUP_QUEUE = Volley.newRequestQueue(this);
        //LAUNDRY_QUEUE = Volley.newRequestQueue(this);
        //CHECKOUT_QUEUE = Volley.newRequestQueue(this);
    }

    void setFirebaseReferences() {
        database = FirebaseDatabase.getInstance(firebaseDBUrl);
        ServerDevice = database.getReference(project.projectName+"ServerDevices/"+controlDevice.name);
        ProjectVariablesRef = database.getReference(project.projectName+"ProjectVariables");
        DevicesControls = database.getReference(project.projectName+"DevicesControls");
        ProjectDevices = database.getReference(project.projectName+"Devices");
    }

    void defineLists() {
        Devices = new ArrayList<>();
    }

    void setAllListeners(TextView actionsNow) {
        Room.setRoomsDevicesListener(MyApp.ROOMS,actionsNow,CLEANUP_QUEUE,LAUNDRY_QUEUE,CHECKOUT_QUEUE);
        Room.setRoomsFireRoomsListener(MyApp.ROOMS);
        Room.setRoomsFireRoomsDevicesControlListener(MyApp.ROOMS);
    }

    void settingInitialDevicesData(List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            cd.setInitialCurrentValues();
        }
    }

    void getProjectAndDevice() {
        controlDevice = storage.getControlDevice("controlDevice");
        project = storage.getProject("project");
    }

    void checkWorking() {
        if (MyApp.isInternetConnected) {
            Log.d("checkWorking","_________________________________"+controlDevice.name+" "+project.projectName);
            Log.d("checkWorking","timer started");
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("checkWorking","timer run");
                    Long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    MyApp.controlDeviceMe.getLastDeviceWorking(ServerDevice,new GetDeviceLastWorkingTime() {
                        @Override
                        public void onSuccess(Long time) {
                            Calendar ca = Calendar.getInstance();
                            ca.setTimeInMillis(time);
                            Log.d("checkWorking","last working value "+time+" "+ca.get(Calendar.HOUR_OF_DAY)+":"+ca.get(Calendar.MINUTE)+":"+ca.get(Calendar.SECOND));
                            if (now > (time+(3000*60))) {
                                Log.d("checkWorking","Device is stop");
                                rerunApplication();
                            }
                            checkWorking();
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("checkWorking","error getting value "+error);
                        }
                    });
                }
            },3000*60L);
        }
    }

    void rerunApplication() {
        startActivity(new Intent(this, Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
