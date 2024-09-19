package com.syriasoft.housekeeping;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.datatransport.BuildConfig;
import com.google.android.material.textfield.TextInputLayout;
import com.syriasoft.checkin.Classes.Interfaces.GetFacilitiesCallback;
import com.syriasoft.checkin.Classes.Interfaces.GetProjectsCallback;
import com.syriasoft.checkin.Classes.Interfaces.GetUserCallBack;
import com.syriasoft.checkin.Classes.LocalDataStore;
import com.syriasoft.checkin.Classes.PROJECT;
import com.syriasoft.checkin.Classes.Property.Facility;
import com.syriasoft.checkin.Classes.User;
import com.syriasoft.hotelservices.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private ArrayList<String> getProList;
    private String jobNumber;
    private String password;
    private Activity act;
    private String loginUrl;
    private Spinner depS, projectNamesSpinner;
    private TextInputLayout pass, job;
    LinearLayout loginLayout,logoLayout;
    TextView versionTV;
    int Version;
    int count = 0;
    ImageView secretProjectChange;
    List<PROJECT> projectsList;
    RequestQueue Q;
    LoadingDialog loading;
    LocalDataStore storage;
    int projectId;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setActivity();
        setActivityActions();
        if (!isNetworkConnected()) {
            // no internet connection
            new messageDialog(MyApp.getResourceString(R.string.noInternetMessage),MyApp.getResourceString(R.string.noInternetTitle),act,true);
        }
        else {
            // internet available
            loading = new LoadingDialog(this);
            loading.show();
            PROJECT.getProjects(Q, new GetProjectsCallback() {
                @Override
                public void onSuccess(List<PROJECT> projects) {
                    projectsList = projects;
                    projectId = storage.getProjectId();
                    MyApp.MyProject = PROJECT.findMyProject(projectsList,projectId);
                    if (MyApp.MyProject == null) {
                        loading.close();
                        setProjects();
                        prepareDepartments();
                    }
                    else {
                        Facility.getFacilities(MyApp.MyProject.url, Q, new GetFacilitiesCallback() {
                            @Override
                            public void onSuccess(List<Facility> facilities) {
                                userId = storage.getUserId();
                                if (userId == 0) {
                                    loading.close();
                                    prepareDepartments();
                                    loginLayout.setVisibility(View.VISIBLE);
                                }
                                else {
                                    User.getUserById(Q, MyApp.MyProject.url, userId, new GetUserCallBack() {
                                        @Override
                                        public void onSuccess(User u) {
                                            MyApp.My_USER = u;
                                            if (!u.getIsUserAvailable()) {
                                                loginLayout.setVisibility(View.VISIBLE);
                                                logoLayout.setVisibility(View.VISIBLE);
                                                prepareDepartments();
                                                new messageDialog("Your account has been deleted", "Warning", act);
                                            }
                                            else {
                                                Intent i = new Intent(act, MainActivity.class);
                                                startActivity(i);
                                                act.finish();
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            loading.close();
                                            createRestartConfirmationDialog(error);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(String error) {
                                loading.close();
                                createRestartConfirmationDialog(error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    loading.close();
                    createRestartConfirmationDialog(error);
                }
            });
//            getProjects(new RequestCallback() {
//                @Override
//                public void onSuccess() {
//                    //MyApp.MyProject = getProjectDataFromSharedPreferences();
//                    if (MyApp.MyProject == null) {
//                        loading.close();
//                        setProjects();
//                        prepareDepartments();
//                    }
//                    else {
//
//                        getRestaurants(new RequestCallback() {
//                            @Override
//                            public void onSuccess() {
//                                projectNamesSpinner.setVisibility(View.GONE);
//                                User u = getUserDataFromSharedPreferences();
//                                if (u == null) {
//                                    loading.close();
//                                    prepareDepartments();
//                                    loginLayout.setVisibility(View.VISIBLE);
//                                }
//                                else {
//                                    if (u.jobNumber == 0) {
//                                        loading.close();
//                                        MyApp.My_USER = u;
//                                        Intent i = new Intent(act, RestaurantOrders.class);
//                                        startActivity(i);
//                                        act.finish();
//                                    }
//                                    else {
//                                        checkUser(String.valueOf(u.id),new VolleyCallback() {
//                                            @Override
//                                            public void onSuccess(String res) {
//                                                Log.d("checkUser",res);
//                                                loading.close();
//                                                if (res.contains("1")) {
//                                                    Log.d("checkUser","yes");
//                                                    MyApp.My_USER = u;
//                                                    Intent i = new Intent(act, MainActivity.class);
//                                                    startActivity(i);
//                                                    act.finish();
//                                                }
//                                                else {
//                                                    Log.d("checkUser","no");
//                                                    loginLayout.setVisibility(View.VISIBLE);
//                                                    logoLayout.setVisibility(View.VISIBLE);
//                                                    prepareDepartments();
//                                                    new messageDialog("Your account has been deleted", "Warning", act);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailed(String error) {
//                                                loading.close();
//                                                MyApp.My_USER = u;
//                                                Intent i = new Intent(act, MainActivity.class);
//                                                startActivity(i);
//                                                act.finish();
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFail(String error) {
//                                loading.close();
//                                createRestartConfirmationDialog(error);
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFail(String error) {
//                    loading.close();
//                    createRestartConfirmationDialog(error);
//                }
//            });
        }
    }

    void setActivity() {
        defineLists();
        defineViews();
    }

    void defineViews() {
        act = this;
        MyApp.actList.add(act);
        storage = new LocalDataStore(act);
        depS = findViewById(R.id.Login_department);
        loginLayout = findViewById(R.id.loginLayout);
        job = findViewById(R.id.Login_jobNumber);
        pass = findViewById(R.id.Login_password);
        projectNamesSpinner = findViewById(R.id.projectName);
        versionTV = findViewById(R.id.textView10);
        secretProjectChange = findViewById(R.id.imageView5);
        logoLayout = findViewById(R.id.logo_layout);
        loginLayout.setVisibility(View.GONE);
        logoLayout.setVisibility(View.VISIBLE);
        Version = BuildConfig.VERSION_CODE;
        versionTV.setText(MessageFormat.format("Version {0}", Version));
        Q = Volley.newRequestQueue(act);
    }

    void defineLists() {
        projectsList = new ArrayList<>();
        getProList = new ArrayList<>();
    }

    void setActivityActions() {
        secretProjectChange.setOnLongClickListener(v -> {
            count++;
            if (count == 4) {
                MyApp.MyProject = null ;
                storage.saveInteger(0,"projectId");
                setProjects();
            }
            return false;
        });
        projectNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApp.MyProject = projectsList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void logInBtn(View view) {
        loading.show();
        if (Objects.requireNonNull(job.getEditText()).getText() == null) {
            new messageDialog("enter Job Number", "Job Number ?", act);
            return;
        }
        if (Objects.requireNonNull(pass.getEditText()).getText() == null) {
            new messageDialog("enter Password", "Password ?", act);
            return;
        }
        loginUrl = MyApp.MyProject.url + "users/login";
        jobNumber = Objects.requireNonNull(job.getEditText()).getText().toString();
        password = Objects.requireNonNull(pass.getEditText()).getText().toString();
        StringRequest re = new StringRequest(Request.Method.POST, loginUrl, response -> {
            Log.d("loginResp" , response+" "+loginUrl);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    loading.close();
                    Log.d("loginResp" , "success");
                    storage.saveInteger(MyApp.MyProject.id,"projectId");
                    JSONObject user = new JSONObject(result.getString("user"));
                    try {
                        MyApp.My_USER = new User(user);
                        storage.saveInteger(MyApp.My_USER.id,"userId");
                        Intent i = new Intent(act, MainActivity.class);
                        startActivity(i);
                        act.finish();
                    }
                    catch (JSONException e) {
                        loading.close();
                        new messageDialog(e.getMessage(),"Failed",act);
                    }
                }
                else {
                    loading.close();
                   new messageDialog(result.getString("error"),"Failed",act);
                }
            } catch (JSONException e) {
                loading.close();
                Log.d("loginResp" , e.getMessage());
                new messageDialog(e.getMessage(),"Failed",act);
            }
        }, error -> {
            loading.close();
            new messageDialog(error.toString(),"Failed",act);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_number", jobNumber);
                params.put("password", password);
                params.put("department", depS.getSelectedItem().toString());
                return params;
            }
        };
        Q.add(re);
    }

    void prepareDepartments() {
        String[] items = new String[]{"Service", "Laundry", "Cleanup", "RoomService", "Gym"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        depS.setAdapter(adapter);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    void setProjects() {
        projectNamesSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapterProj = new ArrayAdapter<>(act, R.layout.spinner_item, getProList);
        projectNamesSpinner.setAdapter(adapterProj);
    }

    void restartApp() {
        act.finish();
        Intent i = new Intent(act,LogIn.class);
        startActivity(i);
    }

    void createRestartConfirmationDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(MyApp.getResourceString(R.string.restartDialogTitle));
        builder.setMessage(MyApp.getResourceString(R.string.restartDialogMessage)+"\n"+error);
        builder.setNegativeButton(MyApp.getResourceString(R.string.no), (dialog, which) -> {
            dialog.dismiss();
            act.finish();
        });
        builder.setPositiveButton(MyApp.getResourceString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            restartApp();
        });
        builder.create().show();
    }
}

