package com.example.hotelservicesstandalone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.Classes.ControlDevice;
import com.example.hotelservicesstandalone.Classes.Interfaces.ControlDeviceCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetProjectsCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.ProjectLoginCallback;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.PROJECT;
import com.example.hotelservicesstandalone.Classes.Tuya;
import com.example.hotelservicesstandalone.Interface.RequestCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;

import java.util.ArrayList;
import java.util.List;

public class
Login extends AppCompatActivity {

    private Spinner PROJECTS_SPINNER;
    private Activity act;
    private EditText password ;
    List<PROJECT> projects ;
    PROJECT selectedProject;
    RequestQueue Q;
    public static LocalDataStore storage;
    LinearLayout loadingLayout,loginLayout;
    FirebaseDatabase database ;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setActivity();
        setActivityActions();
        boot();
    }

    void setActivity() {
        act = this ;
        setViews();
        Q = Volley.newRequestQueue(act);
        projects = new ArrayList<>();
        storage = new LocalDataStore();
        MyApp.setLocalStorage(storage);
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app");
    }

    void setViews() {
        PROJECTS_SPINNER = findViewById(R.id.spinner);
        password = findViewById(R.id.editTextTextPersonName);
        loadingLayout = findViewById(R.id.loading_layout);
        loginLayout = findViewById(R.id.login_layout);
    }

    void setActivityActions() {
        Button login = findViewById(R.id.button);
        login.setOnClickListener(view -> {
            showCaption("getting projects");
            final String pass = password.getText().toString();
            storage.saveBoolean(true,"firstRun");
            PROJECT.loginProject(Q, selectedProject, pass, new ProjectLoginCallback() {
                @Override
                public void onSuccess() {
                    showCaption("adding new control devices");
                    MyApp.My_PROJECT = selectedProject;
                    storage.saveProject(selectedProject,"project");
                    ControlDevice.addNewControlDevice(Q, new ControlDeviceCallback() {
                        @Override
                        public void onSuccess(ControlDevice device) {
                            showCaption("login to T");
                            MyApp.controlDeviceMe = device;
                            storage.saveControlDevice(device,"controlDevice");
                            myRef = database.getReference(MyApp.My_PROJECT.projectName+"ServerDevices/"+MyApp.controlDeviceMe.name);
                            device.addControlDeviceToFirebase(myRef);
                            Tuya.loginTuya(MyApp.My_PROJECT, new ILoginCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    showCaption("getting homes");
                                    MyApp.TuyaUser = user;
                                    Tuya.getProjectHomes(MyApp.My_PROJECT, new ITuyaGetHomeListCallback() {
                                        @Override
                                        public void onSuccess(List<HomeBean> homeBeans) {
                                            MyApp.PROJECT_HOMES = homeBeans;
                                            goToRooms();
                                        }

                                        @Override
                                        public void onError(String errorCode, String error) {
                                            showError("getting tuya homes failed \n"+error);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String code, String error) {
                                    showError("login tuya failed \n"+error);
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            showError("adding new control device failed \n"+error);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    showError("adding new control device failed \n"+error);
                }
            });
        });
    }

    void boot() {
        showCaption(getResources().getString(R.string.loading));
        MyApp.controlDeviceMe = storage.getControlDevice("controlDevice");
        MyApp.My_PROJECT = storage.getProject("project");
        if (MyApp.controlDeviceMe == null || MyApp.My_PROJECT == null) {
            // go to login
            MyApp.isNetworkAvailable(MyApp.app,new RequestCallback() {
                @Override
                public void onSuccess() {
                    showCaption(getResources().getString(R.string.login));
                    prepareLoginLayout();
                }

                @Override
                public void onFail(String error) {
                    showCaption(getResources().getString(R.string.login));
                    prepareLoginLayout();
                }
            });
            storage.saveBoolean(false,"firstRun");
        }
        else {
            // continue
            MyApp.isNetworkAvailable(MyApp.app,new RequestCallback() {
                @Override
                public void onSuccess() {
                    goToRooms();
                }

                @Override
                public void onFail(String error) {
                    goToRooms();
                }
            });
        }
    }

    void prepareLoginLayout() {
        loadingLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
        PROJECT.getProjects(Q, new GetProjectsCallback() {
            @Override
            public void onSuccess(List<PROJECT> projects) {
                String[] Names = new String[projects.size()];
                for(int i=0;i<projects.size();i++) {
                    Names[i] = projects.get(i).projectName;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,Names);
                PROJECTS_SPINNER.setAdapter(adapter);
                PROJECTS_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedProject = projects.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onError(String error) {
                showError(error);
                ControlDevice.restartApplication(5,act);
            }
        });
    }

    void goToRooms() {
        Intent i = new Intent(act , Rooms.class);
        act.startActivity(i);
        act.finish();
    }

    void showCaption(String state) {
        TextView caption = findViewById(R.id.textView28);
        caption.setText(state);
    }

    void showError(String error) {
        TextView errorText = findViewById(R.id.textView);
        errorText.setText(error);
    }

    void clearError() {
        TextView errorText = findViewById(R.id.textView);
        errorText.setText("");
    }
}