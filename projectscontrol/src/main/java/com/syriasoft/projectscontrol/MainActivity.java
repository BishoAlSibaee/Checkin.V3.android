package com.syriasoft.projectscontrol;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.projectscontrol.Adapters.DevicesAdapter;
import com.syriasoft.projectscontrol.RequestCallBacks.BuildingsCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.FloorsCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.RequestCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.RoomsCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.ServerDevicesCallBack;
import com.syriasoft.projectscontrol.RequestCallBacks.TuyaUserCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    Activity act ;
    List<PROJECT> Projects ;
    PROJECT THE_PROJECT ;
    String getDevicesUrl ;
    RequestQueue Q;
    List<ServerDevice> AllServerDevices ;
    RecyclerView DevicesRecycler;
    //LoadingDialog loadingDialog;
    FirebaseDatabase FirebaseDB;
    DevicesAdapter Adapter;
    public static List<ServerDevice> SelectedDevicesList ;
    public static PROJECT SelectedProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act = this;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},42);
            }
        }
        setActivity();
        getActiveProjects();

//        setActivity();
//        getActiveProjects();
//        createTuyaAccount("checkinmaden@gmail.com", "Ratco@RDH", "350860", new TuyaUserCallback() {
//            @Override
//            public void onSuccess(User user) {
//
//            }
//
//            @Override
//            public void onFail(String error) {
//
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 42) {
            if (permissions[0].equals(Manifest.permission.SCHEDULE_EXACT_ALARM)) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 42);
                    }
                }
            }
        }
    }

    void setActivity() {
        act = this;
        Projects = new ArrayList<>();
        AllServerDevices = new ArrayList<>();
        SelectedDevicesList = AllServerDevices;
        Q = Volley.newRequestQueue(act);
        DevicesRecycler = findViewById(R.id.devicesRecycler);
        DevicesRecycler.setLayoutManager(new LinearLayoutManager(act,RecyclerView.VERTICAL,false));
        getDevicesUrl = "roomsManagement/getServerDevices";
        //loadingDialog = new LoadingDialog(act);
        FirebaseDB = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/");
        Adapter = new DevicesAdapter(AllServerDevices);
    }

    void getActiveProjects() {
        AllServerDevices.clear();
        //loadingDialog.show();
        String projectsUrl = "https://ratco-solutions.com/Checkin/getProjects.php";
        StringRequest re = new StringRequest(Request.Method.POST, projectsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getProjectsResp" , response);
                if (response != null ) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        String[] Names = new String[arr.length()+1];
                        Names[0] = "all";
                        for(int i=0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            PROJECT p = new PROJECT(row.getInt("id"),row.getString("projectName"),row.getString("city"),row.getString("salesman"),row.getString("TuyaUser"),row.getString("TuyaPassword"),row.getString("LockUser"),row.getString("LockPassword"),row.getString("url"));
                            Projects.add(p);
                            Names[i+1] = row.getString("projectName");
                        }
                        MyApp.Projects = Projects;
                        for (int i=0;i<Projects.size();i++) {
                            PROJECT p = Projects.get(i);
                            Timer t= new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    p.getProjectBuildings(Q, new BuildingsCallback() {
                                        @Override
                                        public void onSuccess(List<BUILDING> buildings) {
                                            p.getProjectFloors(Q, new FloorsCallback() {
                                                @Override
                                                public void onSuccess(List<FLOOR> floors) {
                                                    p.getProjectRooms(Q, new RoomsCallback() {
                                                        @Override
                                                        public void onSuccess(List<ROOM> rooms) {
                                                            p.getProjectServerDevices(Q, new ServerDevicesCallBack() {
                                                                @Override
                                                                public void onSuccess(List<ServerDevice> devices) {
                                                                    //loadingDialog.close();
                                                                    AllServerDevices.addAll(devices);
                                                                    p.ServerDevices = devices;
                                                                    if (Projects.indexOf(p) == Projects.size()-1) {
                                                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.support_simple_spinner_dropdown_item,Names);
                                                                        Spinner PROJECTS_SPINNER = findViewById(R.id.spinner);
                                                                        PROJECTS_SPINNER.setAdapter(adapter);
                                                                        PROJECTS_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                            @Override
                                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                if (Names[i].equals("all")) {
                                                                                    SelectedDevicesList = AllServerDevices;
                                                                                    Adapter = new DevicesAdapter(AllServerDevices);
                                                                                    DevicesRecycler.setAdapter(Adapter);
                                                                                }
                                                                                else {
                                                                                    THE_PROJECT = Projects.get(PROJECTS_SPINNER.getSelectedItemPosition()-1);
                                                                                    SelectedDevicesList = THE_PROJECT.ServerDevices;
                                                                                    Adapter = new DevicesAdapter(THE_PROJECT.ServerDevices);
                                                                                    DevicesRecycler.setAdapter(Adapter);
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                                                            }
                                                                        });
                                                                    }
                                                                    setWorkingFirebaseListeners(p);
                                                                }

                                                                @Override
                                                                public void onFailed(String error) {
                                                                    //loadingDialog.close();
                                                                    new MessageDialog("error getting devices "+error, p.projectName+" error",act);
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFail(String error) {
                                                            new MessageDialog("error getting rooms "+error, p.projectName+" error",act);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    new MessageDialog("error getting floors "+error, p.projectName+" error",act);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            new MessageDialog("error getting buildings "+error, p.projectName+" error",act);
                                        }
                                    });
                                }
                            },10000L*i);

                        }
                    }
                    catch (JSONException e) {
                        //loadingDialog.close();
                        Log.d("getProjectsResp" , e.toString());
                    }
                }
                else {
                    //.close();
                    Log.d("getProjectsResp" , "response null");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loadingDialog.close();
                Log.d("getProjectsResp" , error.toString());
            }
        });
        Q.add(re);
    }

    void setWorkingFirebaseListeners(PROJECT p) {
        LoadingDialog l = new LoadingDialog(act,"checking devices");
        l.show();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                l.close();
                t.cancel();
            }
        },1000*60*2,1000*60*2);
            for (ServerDevice sd : p.ServerDevices) {
                DatabaseReference ref = FirebaseDB.getReference(p.projectName+"ServerDevices/"+sd.name);
                setDeviceWorkingListener(sd,ref);
                setDeviceWorkingTimer(sd);
            }
    }

    void setDeviceWorkingListener(ServerDevice device, DatabaseReference workingReference) {
        workingReference.child("working").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    device.checkValue = Long.parseLong(snapshot.getValue().toString());
                    Log.d("workingTimer",device.checkValue+" "+device.ProjectName+" "+device.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void setDeviceWorkingTimer(ServerDevice device) {
        device.task =new TimerTask() {
            @Override
            public void run() {
                Log.d("workingTimer","checking value "+device.ProjectName+" "+device.name);
                long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                long interval = 1000L *60*device.taskInterval;
                long nowInterval = now - device.checkValue ;
                Log.d("workingTimer",nowInterval+" "+interval);
                if (nowInterval >= interval) {
                    device.working = false ;
                    if (device.checkValue > 0) {
                        CloudMessageController.rerunDevice(device, Q, new RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("workingTimer","rerun message response "+result);
                            }

                            @Override
                            public void onFailed(String error) {
                                Log.d("workingTimer","rerun message error "+error);
                            }
                        });
                        CloudMessageController.makeDeviceOffWarningNotification(device);
                    }
                }
                else {
                    device.working = true;
                }
                Log.d("workingTimer",device.ProjectName+" "+device.name+" result "+device.working);
                if (Adapter != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Adapter.notifyDataSetChanged();
                        }
                    });
                }
                else {
                    Log.d("workingTimer","adapter null");
                }
            }
        };
        device.timer.scheduleAtFixedRate(device.task,0, 1000*60*device.taskInterval);
    }

    public void goToProjects(View view) {
        Intent i = new Intent(act,Projects.class);
        startActivity(i);
    }

    void sendTuyaVerificationCodeToEmail(String email, RequestCallback callback) {
        TuyaHomeSdk.getUserInstance().sendVerifyCodeWithUserName(email, "", "966", 1, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFailed(error);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess("sent");
            }
        });
    }
    static void verifyTuyaVerificationCode(String email,String code , RequestCallback callback) {
        TuyaHomeSdk.getUserInstance().checkCodeWithUserName(email, "", "966", code, 1, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFailed(error);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess("done");
            }
        });
    }

    static void createTuyaAccount(String email, String password, String code, TuyaUserCallback callback) {
        TuyaHomeSdk.getUserInstance().registerAccountWithEmail("966", email, password, code, new IRegisterCallback() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onError(String code, String error) {
                callback.onFail(error);
            }
        });
    }
}