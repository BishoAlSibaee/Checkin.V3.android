package com.example.hotelservicesstandalone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.Interface.RequestCallback;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class
Login extends AppCompatActivity {
    private Spinner PROJECTS_SPINNER;
    private String[] Names ;
    private Activity act;
    private final String projectLoginUrl = "users/loginProject" ;
    private EditText password ;
    private List<HomeBean> Homes;
    TextView caption;
    List<PROJECT> projects ;
    PROJECT THE_PROJECT ;
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    String projectName , tuyaUser , tuyaPassword , lockUser , lockPassword ,Device_ID , Device_Name ;
    RequestQueue Q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setActivity();
        caption.setText(getResources().getString(R.string.gettingProjects));
        getProjects(new loginCallback() {
            @Override
            public void onSuccess() {
                goNext();
            }

            @Override
            public void onFailed() {
                Toast.makeText(act,"get projects failed",Toast.LENGTH_LONG).show();
                restartApplication(5);
            }
        });
    }

    void setActivity() {
        act = this ;
        MyApp.Activities.add(act);
        Q = Volley.newRequestQueue(act);
        PROJECTS_SPINNER = findViewById(R.id.spinner);
        caption = findViewById(R.id.textView28);
        password = findViewById(R.id.editTextTextPersonName);
        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
        projects = new ArrayList<>();
        caption.setText(getResources().getString(R.string.loading));
    }

    private void getProjects(loginCallback callback) {
        String projectsUrl = "https://ratco-solutions.com/Checkin/getChinaProjects.php";
        StringRequest re = new StringRequest(Request.Method.POST, projectsUrl, response -> {
            Log.d("getProjectsResp" , response);
            if (response != null ) {
                try {
                    JSONArray arr = new JSONArray(response);
                    Names = new String[arr.length()];
                    for(int i=0;i<arr.length();i++) {
                        JSONObject row = arr.getJSONObject(i);
                        projects.add(new PROJECT(row.getInt("id"),row.getString("projectName"),row.getString("city"),row.getString("salesman"),row.getString("TuyaUser"),row.getString("TuyaPassword"),row.getString("LockUser"),row.getString("LockPassword"),row.getString("url")));
                        Names[i] = row.getString("projectName");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("getProjectsResp" , e.toString());
                    callback.onFailed();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,Names);
                PROJECTS_SPINNER.setAdapter(adapter);
                PROJECTS_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
                        MyApp.THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                callback.onSuccess();
            }
        }, error -> {
            Log.d("getProjectsResp" , error.toString());
            callback.onFailed();
        });
        Q.add(re);
    }

    private void goNext() {
        getProjectFromSharedPreferences();
        if (projectName == null) {
            caption.setText("");
            prepareLoginLayout();
        }
        else {
            getTheFullProjectObject();
            if (Device_ID == null && Device_Name == null) {
                caption.setText(getResources().getString(R.string.addControlDevice));
                addControlDevice(new loginCallback() {
                    @Override
                    public void onSuccess() {
                        caption.setText(getResources().getString(R.string.loginToT));
                        logInFunction(THE_PROJECT, new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                caption.setText(getResources().getString(R.string.gettingHomes));
                                getTuyaHomes(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        goToRooms();
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        new MessageDialog("get project homes Failed","Failed",act);
                                        restartApplication(5);
                                    }
                                });
                            }

                            @Override
                            public void onFail(String error) {
                                new MessageDialog(error,"tuya login failed",act);
                                restartApplication(5);
                            }
                        });
                    }
                    @Override
                    public void onFailed() {
                        new MessageDialog("Add Server Device Failed","Failed",act);
                        restartApplication(5);
                    }
                });
            }
            else {
                caption.setText(getResources().getString(R.string.loginToT));
                logInFunction(THE_PROJECT, new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        caption.setText(getResources().getString(R.string.gettingHomes));
                        Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getTuyaHomes(new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        goToRooms();
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        new MessageDialog("get project homes Failed\n"+error,"Failed",act);
                                        restartApplication(10);
                                    }
                                });
                            }
                        },5000);

                    }

                    @Override
                    public void onFail(String error) {
                        new MessageDialog(error,"tuya login failed",act);
                        restartApplication(5);
                    }
                });
            }
        }
    }

    public void addControlDevice(loginCallback callback) {
        StringRequest re = new StringRequest(Request.Method.GET, THE_PROJECT.url + "roomsManagement/addControlDevice", response -> {
            Log.d("addControlDevice" , response);
            if (response != null) {
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("result").equals("success")) {
                        Log.d("addControlDevice" , resp.getString("result"));
                        JSONObject device = resp.getJSONObject("device");
                        editor.putString("Device_Id" , String.valueOf(device.getInt("id")));
                        editor.putString("Device_Name" , device.getString("name"));
                        MyApp.Device_Id = String.valueOf(device.getInt("id"));
                        MyApp.Device_Name = device.getString("name");
                        editor.apply();
                        callback.onSuccess();
                    }
                    else {
                        Log.d("addControlDevice" , resp.getString("error"));
                        callback.onFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("addControlDevice" , e.toString());
                    callback.onFailed();
                }
            }
            else {
                Log.d("addControlDevice" , "response null");
                callback.onFailed();
            }
        }, error -> {
            Log.d("addControlDevice" , error.toString());
            callback.onFailed();
        });
        Q.add(re);
    }

    void logInFunction(PROJECT project, RequestCallback callback) {
        String COUNTRY_CODE = "966";
        TuyaHomeSdk.getUserInstance().loginWithEmail(COUNTRY_CODE,project.TuyaUser ,project.TuyaPassword , new ILoginCallback() {
            @Override
            public void onSuccess (User user) {
                Log.d("tuyaLoginResp",project.projectName);
                MyApp.TuyaUser = user;
                callback.onSuccess();
            }

            @Override
            public void onError (String code, String error) {
                Log.d("tuyaLoginResp",error+" "+code);
                callback.onFail(error+" "+code);
            }
        });
    }

    void getTuyaHomes(RequestCallback callback) {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onError(String errorCode, String error) {
                callback.onFail("Tuya Login Failed" + error);
            }
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                MyApp.homeBeans = homeBeans ;
                Homes = homeBeans ;
                for(int i = 0; i< Homes.size(); i++) {
                    Log.d("tuyaLoginResp", Homes.get(i).getName());
                    if (MyApp.THE_PROJECT.projectName.equals("apiTest")) {
                        if (Homes.get(i).getName().contains("Test")) {
                            MyApp.PROJECT_HOMES.add(Homes.get(i));
                        }
                    }
                    else if (Homes.get(i).getName().contains(MyApp.THE_PROJECT.projectName)) {
                        MyApp.PROJECT_HOMES.add(Homes.get(i));
                    }
                }
                if (MyApp.PROJECT_HOMES.size() > 0) {
                    callback.onSuccess();
                }
                else {
                    TuyaHomeSdk.getHomeManagerInstance().createHome(THE_PROJECT.projectName, 0, 0,"ksa",null, new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            MyApp.PROJECT_HOMES.add(bean);
                            callback.onSuccess();
                        }
                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onFail(errorMsg+" "+errorCode);
                        }
                    });
                }
            }
        });
    }

    public void LogIn(View view) {
        if (THE_PROJECT != null ) {
            caption.setText(getResources().getString(R.string.gettingProjects));
            final lodingDialog loading = new lodingDialog(act);
            final String pass = password.getText().toString();
            StringRequest re = new StringRequest(Request.Method.POST, THE_PROJECT.url + projectLoginUrl, response -> {
                loading.stop();
                if (response != null) {
                    try {
                        JSONObject resp = new JSONObject(response);
                        if (resp.getString("result").equals("success")) {
                            Toast.makeText(act,"Login Success",Toast.LENGTH_LONG).show();
                            saveProjectToSharedPreferences();
                            getDeviceInfoFromSharedPreferences();
                            MyApp.my_token = resp.getString("token");
                            if (Device_ID == null && Device_Name == null) {
                                Log.d("deviceName" , "null");
                                caption.setText(getResources().getString(R.string.addControlDevice));
                                addControlDevice(new loginCallback() {
                                    @Override
                                    public void onSuccess() {
                                        caption.setText(getResources().getString(R.string.loginToT));
                                        logInFunction(THE_PROJECT, new RequestCallback() {
                                            @Override
                                            public void onSuccess() {
                                                caption.setText(getResources().getString(R.string.gettingHomes));
                                                getTuyaHomes(new RequestCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        goToRooms();
                                                    }

                                                    @Override
                                                    public void onFail(String error) {
                                                        new MessageDialog("get project homes Failed","Failed",act);
                                                        restartApplication(5);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFail(String error) {
                                                new MessageDialog(error,"tuya login failed",act);
                                                restartApplication(5);
                                            }
                                        });
                                    }
                                    @Override
                                    public void onFailed() {
                                        Toast.makeText(act,"Add Device Failed",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Log.d("deviceName" , Device_Name);
                                caption.setText(getResources().getString(R.string.loginToT));
                                logInFunction(THE_PROJECT, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        caption.setText(getResources().getString(R.string.gettingHomes));
                                        getTuyaHomes(new RequestCallback() {
                                            @Override
                                            public void onSuccess() {
                                                goToRooms();
                                            }

                                            @Override
                                            public void onFail(String error) {
                                                new MessageDialog("get project homes Failed","Failed",act);
                                                restartApplication(5);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        new MessageDialog(error,"tuya login failed",act);
                                        restartApplication(6);
                                    }
                                });
                            }
                        }
                        else {
                            Toast.makeText(act,"Login Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act,"Login Failed " + e,Toast.LENGTH_LONG).show();
                    }
                }
            }, error -> loading.stop()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> par = new HashMap<>();
                    par.put( "password" , pass ) ;
                    par.put( "project_name" , THE_PROJECT.projectName ) ;
                    return par;
                }
            };
            Q.add(re);
        }
        else {
            new MessageDialog("please select project","Project ?",act);
        }
    }

    public void Continue(View view) {
        Intent i = new Intent(act , Rooms.class);
        act.startActivity(i);
    }

    void getProjectFromSharedPreferences() {
        projectName = pref.getString("projectName", null);
        tuyaUser = pref.getString("tuyaUser", null);
        tuyaPassword = pref.getString("tuyaPassword", null);
        lockUser = pref.getString("lockUser", null);
        lockPassword = pref.getString("lockPassword", null);
    }

    void saveProjectToSharedPreferences() {
        editor.putString("projectName" , THE_PROJECT.projectName);
        editor.putString("tuyaUser" , THE_PROJECT.TuyaUser);
        editor.putString("tuyaPassword" , THE_PROJECT.TuyaPassword);
        editor.putString("lockUser" , THE_PROJECT.LockUser);
        editor.putString("lockPassword" , THE_PROJECT.LockPassword);
        editor.putString("url" , THE_PROJECT.url);
        editor.apply();
    }

    void getDeviceInfoFromSharedPreferences() {
        Device_ID = pref.getString("Device_Id", null);
        MyApp.Device_Id = Device_ID;
        Device_Name = pref.getString("Device_Name", null);
        MyApp.Device_Name = Device_Name;
    }

    void prepareLoginLayout() {
        LinearLayout loginLayout = findViewById(R.id.login_layout);
        LinearLayout loadingLayout = findViewById(R.id.loading_layout);
        loadingLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    void getTheFullProjectObject() {
        for (int i=0;i<projects.size();i++) {
            if (projectName.equals(projects.get(i).projectName)) {
                THE_PROJECT = projects.get(i);
                MyApp.THE_PROJECT = projects.get(i);
                Device_ID = pref.getString("Device_Id", null);
                MyApp.Device_Id = Device_ID;
                Device_Name = pref.getString("Device_Name", null);
                MyApp.Device_Name = Device_Name;
                break;
            }
        }
    }

    void restartApplication(int seconds) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(act,Login.class);
                act.startActivity(i);
                act.finish();
            }
        }, 1000L *seconds);
    }

    void goToRooms() {
        Intent i = new Intent(act , Rooms.class);
        act.startActivity(i);
        act.finish();
    }
}

interface loginCallback {
    void onSuccess();
    void onFailed();
}