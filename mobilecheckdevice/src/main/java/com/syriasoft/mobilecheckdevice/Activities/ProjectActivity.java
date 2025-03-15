package com.syriasoft.mobilecheckdevice.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;
import com.syriasoft.mobilecheckdevice.Classes.DevicesDataDB;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GerRoomsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetBuildingsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetDevicesCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetFloorsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.getDeviceDataCallback;
import com.syriasoft.mobilecheckdevice.Classes.LocalDataStore;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT_VARIABLES;
import com.syriasoft.mobilecheckdevice.Classes.Property.Building;
import com.syriasoft.mobilecheckdevice.Classes.Property.Floor;
import com.syriasoft.mobilecheckdevice.Classes.Property.PropertyDB;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
import com.syriasoft.mobilecheckdevice.Classes.Tuya;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.syriasoft.mobilecheckdevice.LoadingDialog;
import com.syriasoft.mobilecheckdevice.MessageDialog;
import com.syriasoft.mobilecheckdevice.MyApp;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    Activity act;
    RequestQueue REQ;
    TextView projectName,buildingsCount,floorsCount,suitesCount,roomsCount,devicesCount;
    PropertyDB pDB;
    DevicesDataDB db;
    LocalDataStore storage;
    FirebaseDatabase database ;
    DatabaseReference ProjectVariablesRef;

    List<Room> ROOMS;
    List<Floor> Floors;
    List<Building> Buildings;
    List<HomeBean> ProjectHomes;
    List<CheckinDevice> Devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setActivity();
        getProjectData();
    }

    void setActivity() {
        act = this;
        buildingsCount = findViewById(R.id.textView84);
        floorsCount = findViewById(R.id.textView86);
        suitesCount = findViewById(R.id.textView863);
        roomsCount = findViewById(R.id.textView861);
        devicesCount = findViewById(R.id.textView862);
        projectName = findViewById(R.id.hotelName);
        ROOMS = new ArrayList<>();
        Floors = new ArrayList<>();
        Buildings = new ArrayList<>();
        REQ = Volley.newRequestQueue(act);
        pDB = new PropertyDB(act);
        db = new DevicesDataDB(act);
        storage = new LocalDataStore();
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/"); //  https://hotelservices-ebe66.firebaseio.com/
        ProjectVariablesRef = database.getReference(MyApp.SelectedProject.projectName+"ProjectVariables");
    }

    void getProjectData() {
        pDB.deleteAll();
        LoadingDialog loading = new LoadingDialog(act);
        PROJECT_VARIABLES.getProjectVariables(MyApp.SelectedProject, REQ, new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootingUp","project variables done");
                PROJECT_VARIABLES.setProjectVariablesFirebaseListeners(ProjectVariablesRef);
                Building.getBuildings(MyApp.SelectedProject,REQ, new GetBuildingsCallback() {
                    @Override
                    public void onSuccess(List<Building> buildings) {
                        Log.d("bootingUp","buildings done "+buildings.size());
                        Buildings = buildings;
                        MyApp.SelectedProject.buildings = buildings;
                        Floor.getFloors(MyApp.SelectedProject,REQ, new GetFloorsCallback() {
                            @Override
                            public void onSuccess(List<Floor> floors) {
                                Log.d("bootingUp","floors done "+floors.size());
                                Floors = floors;
                                Building.setBuildingsFloors(MyApp.SelectedProject.buildings,floors);
                                Room.getAllRooms(MyApp.SelectedProject,REQ, new GerRoomsCallback() {
                                    @Override
                                    public void onSuccess(List<Room> rooms) {
                                        Log.d("bootingUp","rooms done "+rooms.size());
                                        ROOMS = rooms;
                                        for (Building b : buildings) {
                                            Floor.setFloorsRooms(b.floorsList,rooms);
                                        }
                                        Room.sortRoomsByNumber(ROOMS);
                                        Room.setRoomsBuildingsAndFloors(ROOMS,Buildings,Floors);
                                        Room.setRoomsFireRooms(MyApp.SelectedProject,ROOMS,database);
                                        Tuya.loginTuya(MyApp.SelectedProject, new ILoginCallback() {
                                            @Override
                                            public void onSuccess(User user) {
                                                Log.d("bootingUp","tuya login done");
                                                MyApp.TuyaUser = user;
                                                Tuya.getProjectHomes(MyApp.SelectedProject, storage, new ITuyaGetHomeListCallback() {
                                                    @Override
                                                    public void onSuccess(List<HomeBean> homeBeans) {
                                                        Log.d("bootingUp","tuya homes done "+homeBeans.size());
                                                        ProjectHomes = homeBeans;
                                                        Tuya.getDevicesNoTimers(ProjectHomes, ROOMS, new GetDevicesCallback() {
                                                            @Override
                                                            public void devices(List<CheckinDevice> devices) {
                                                                Log.d("bootingUp","tuya devices done "+devices.size());
                                                                Devices = devices;
                                                                Tuya.gettingInitialDevicesData(devices, new getDeviceDataCallback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("bootingUp","finish "+123);
                                                                        settingInitialDevicesData(devices);
                                                                        setProjectCounts();
                                                                        loading.stop();
                                                                    }

                                                                    @Override
                                                                    public void onError(String error) {
                                                                        loading.stop();
                                                                        new MessageDialog(error,"error",act);
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onError(String error) {
                                                                loading.stop();
                                                                new MessageDialog(error,"error",act);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(String errorCode, String error) {
                                                        loading.stop();
                                                        new MessageDialog(error,"error",act);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String code, String error) {
                                                loading.stop();
                                                new MessageDialog(error,"error",act);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        loading.stop();
                                        new MessageDialog(error,"error",act);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                loading.stop();
                                new MessageDialog(error,"error",act);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        loading.stop();
                        new MessageDialog(error,"error",act);
                    }
                });
            }

            @Override
            public void onFail(String error) {
                loading.stop();
                new MessageDialog(error,"error",act);
            }
        });
    }

    void setProjectCounts() {
        if (MyApp.SelectedProject!= null) {
            projectName.setText(MyApp.SelectedProject.projectName);
            buildingsCount.setText(String.valueOf(MyApp.SelectedProject.buildings.size()));
            floorsCount.setText(String.valueOf(Floors.size()));
            roomsCount.setText(String.valueOf(ROOMS.size()));
            devicesCount.setText(String.valueOf(Devices.size()));
        }
    }

    void settingInitialDevicesData(List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            cd.setInitialCurrentValues();
        }
    }

}