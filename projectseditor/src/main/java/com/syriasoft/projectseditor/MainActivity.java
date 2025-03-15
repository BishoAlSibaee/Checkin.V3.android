package com.syriasoft.projectseditor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.projectseditor.Adapters.devicesAdapter;
import com.syriasoft.projectseditor.Adapters.projectsAdapter;
import com.syriasoft.projectseditor.Classes.ControlDevice;
import com.syriasoft.projectseditor.Classes.FirebaseMessageSender;
import com.syriasoft.projectseditor.Classes.PROJECT;
import com.syriasoft.projectseditor.Classes.PROJECT_VARIABLES;
import com.syriasoft.projectseditor.Dialog.MessageDialog;
import com.syriasoft.projectseditor.Interfaces.GetControlDevicesCallback;
import com.syriasoft.projectseditor.Interfaces.GetProjectVariables;
import com.syriasoft.projectseditor.Interfaces.GetProjectsCallback;
import com.syriasoft.projectseditor.Interfaces.RequestCallback;
import com.syriasoft.projectseditor.Interfaces.getStringCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Activity act;
    DatabaseReference getProjectsRef;
    RequestQueue queue;
    devicesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("bootingUp", "onCreate");
        setActivity();
        getData(new RequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("bootingUp", "final "+MyApp.projects.size()+" "+MyApp.devices.size());
                for (int i=0;i<MyApp.devices.size();i++) {
                    ControlDevice cd = MyApp.devices.get(i);
                    if (cd.deviceReference != null) {
                        int finalI = i;
                        cd.deviceReference.child(cd.name).child("working").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue()!= null) {
                                    Calendar now = Calendar.getInstance(Locale.getDefault());
                                    long nowValue = now.getTimeInMillis();
                                    cd.lastWorking = Long.parseLong(snapshot.getValue().toString());
                                    long minutes = 1000 * 60 * 2;
                                    cd.isWorking = nowValue <= (cd.lastWorking + minutes);
                                    refreshDevice(finalI);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("workingListeners", cd.myProject.projectName+ " "+cd.name+" error "+error);
                            }
                        });
                        cd.deviceReference.child(cd.name).child("token").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    cd.token = snapshot.getValue().toString();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                showDevices();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(act,error,Toast.LENGTH_LONG).show();
            }
        });
        checkTimer();
    }

    void setActivity() {
        Log.d("bootingUp", "setActivity");
        act = this;
        queue = Volley.newRequestQueue(act);
        setFirebaseReferences();
    }

    void setFirebaseReferences() {
        Log.d("bootingUp", "setFirebaseReferences");
        getProjectsRef = MyApp.fbDB.createDBReference("getProjects/");
    }

    void getGetProjectsUrl(getStringCallback callback) {
        Log.d("bootingUp", "getGetProjectsUrl");
        getProjectsRef.child("url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callback.onSuccess(snapshot.getValue().toString());
                }
                else {
                    callback.onError("getGetProjectsUrl null");
                    getProjectsRef.child("url").setValue("https://ratco-solutions.com/Checkin/getProjects.php");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }

    void showProjects() {
        RecyclerView projectsRecycler = findViewById(R.id.projectsRecycler);
        projectsAdapter adapter = new projectsAdapter(MyApp.projects);
        projectsRecycler.setLayoutManager(new LinearLayoutManager(act,LinearLayoutManager.VERTICAL,false));
        projectsRecycler.setAdapter(adapter);
    }
    void refreshProject(int position) {
        adapter.notifyItemChanged(position);
    }
    void showDevices() {
        RecyclerView projectsRecycler = findViewById(R.id.projectsRecycler);
        adapter = new devicesAdapter(MyApp.devices);
        GridLayoutManager manager = new GridLayoutManager(act,4);
        projectsRecycler.setLayoutManager(manager);
        projectsRecycler.setAdapter(adapter);
    }
    void refreshDevice(int index) {
        adapter.notifyItemChanged(index);
    }
    void checkTimer() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("timerRun","timer started");
                runOnUiThread(() -> {
                    for (ControlDevice cd : MyApp.devices) {
                        Calendar now = Calendar.getInstance(Locale.getDefault());
                        long nowValue = now.getTimeInMillis();
                        long minutes = 1000 * 60 * 2;
                        cd.isWorking = nowValue <= (cd.lastWorking + minutes);
                        if (!cd.isWorking) {
                            FirebaseMessageSender.sendMessage(queue,cd.token,new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    //new MessageDialog("rerun sent to "+cd.name+" "+cd.myProject.projectName,"done",act);
                                }

                                @Override
                                public void onFail(String error) {
                                    new MessageDialog(error,"error sending rerun",act);
                                }
                            });
                        }
                    }
                    showDevices();
                });
                checkTimer();
            }
        },1000 * 60 * 2L);
    }
    void getData(RequestCallback callback) {
        getGetProjectsUrl(new getStringCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("bootingUp", "getGetProjectsUrl success "+result);
                MyApp.getProjectsUrl = result;
                PROJECT.getProjects(queue, MyApp.getProjectsUrl, new GetProjectsCallback() {
                    @Override
                    public void onSuccess(List<PROJECT> projects) {
                        Log.d("bootingUp", "getProjects success "+ projects.size());
                        MyApp.projects = projects;
                        final int[] counter = {0};
                        List<Timer> timers = new ArrayList<>();
                        for (int i = 0 ; i < projects.size() ; i++) {
                            PROJECT p = projects.get(i);
                            Timer t = new Timer();
                            timers.add(t);
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    PROJECT_VARIABLES.getProjectVariables(p, queue, new GetProjectVariables() {
                                        @Override
                                        public void onSuccess(PROJECT_VARIABLES variables) {
                                            Log.d("bootingUp", "getProjectVariables success "+p.projectName);
                                            p.myProjectVariables = variables;
                                            DatabaseReference variablesRef = MyApp.fbDB.createDBReference(p.projectName+"/ProjectVariables/");
                                            p.myProjectVariables.setProjectVariablesFirebaseListeners(variablesRef);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.d("bootingUp", "getProjectVariables error "+p.projectName+" "+error);
                                        }
                                    });
                                    ControlDevice.getControlDevices(queue, p, new GetControlDevicesCallback() {
                                        @Override
                                        public void onSuccess(List<ControlDevice> devices) {
                                            Log.d("bootingUp", "getControlDevices success "+p.projectName+" "+ devices.size());
                                            timers.remove(t);
                                            counter[0]++;
                                            p.myControlDevices = devices;
                                            for (ControlDevice cd : p.myControlDevices) {
                                                cd.setMyProject(p);
                                                cd.setMyFBReference(MyApp.fbDB.createDBReference(p.projectName+"ServerDevices"));
                                                MyApp.devices.add(cd);
                                            }
                                            if (counter[0] == MyApp.projects.size()) {
                                                Log.d("bootingUp", "finish");
                                                callback.onSuccess();
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.d("bootingUp", "getControlDevices error "+p.projectName+" "+error);
                                            counter[0]++;
                                            timers.remove(t);
                                            for (Timer tt  :timers) {
                                                tt.cancel();
                                                timers.remove(tt);
                                            }
                                            callback.onFail(error);
                                        }
                                    });

                                }
                            },2000);

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("bootingUp", "getProjects failed "+ error);
                        callback.onFail(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.d("bootingUp", "getGetProjectsUrl failed "+error);
                callback.onFail(error);
            }
        });
    }
}