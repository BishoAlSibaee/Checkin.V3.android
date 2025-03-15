package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.LoginCallback;
import com.syriasoft.mobilecheckdevice.Classes.LocalDataStore;
import com.syriasoft.mobilecheckdevice.Classes.USER;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;

public class Login extends AppCompatActivity {
//    private Spinner PROJECTS_SPINNER;
//    private String[] Names ;
//    private Activity act;
//    private final String projectLoginUrl = "users/loginProject" ;
//    private EditText password ;
//    static HotelDB THE_HOTEL_DB;
//    private List<HomeBean> Homes;
//    List<PROJECT> projects ;
//    PROJECT THE_PROJECT ;
//    SharedPreferences pref ;
//    SharedPreferences.Editor editor ;
//    String projectName , tuyaUser , tuyaPassword , lockUser , lockPassword;
//    RequestQueue Q;
    private Activity act;
    private EditText password, jobNumber;
    RequestQueue Q;
    public static LocalDataStore storage;
    LinearLayout loadingLayout,loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setActivity();
        setActivityActions();
        bootUp();
//        act = this ;
//        Q = Volley.newRequestQueue(act);
//        PROJECTS_SPINNER = findViewById(R.id.spinner);
//        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
//        editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
//        THE_HOTEL_DB = new HotelDB(act);
//        projects = new ArrayList<>();
//        getProjects(new RequestCallback() {
//            @Override
//            public void onSuccess() {
//                goNext();
//            }
//            @Override
//            public void onFail(String error) {
//                AlertDialog.Builder b = new AlertDialog.Builder(act);
//                b.setTitle("Get Projects Failed").setMessage("getting projects failed ..\n"+error+" \ntry again ??")
//                        .setNegativeButton("Cancel", (dialogInterface, i) -> act.finish()).setPositiveButton("Yes", (dialogInterface, i) -> {
//                            act.finish();
//                            Intent intent = new Intent(act,Login.class);
//                            startActivity(intent);
//                        }).create().show();
//            }
//        });
//        TuyaHomeSdk.getUserInstance().sendVerifyCodeWithUserName("basharse@hotmail.com", "", "966", 1, new IResultCallback() {
//            @Override
//            public void onError(String code, String error) {
//                Log.d("makeNweUser",code+" "+error);
//            }
//
//            @Override
//            public void onSuccess() {
//                Log.d("makeNweUser","check email");
//            }
//        });
//        TuyaHomeSdk.getUserInstance().registerAccountWithEmail("966", "basharse@hotmail.com", "Ratco@", "886811", new IRegisterCallback() {
//            @Override
//            public void onSuccess(User user) {
//                Log.d("makeNweUser","user created");
//            }
//
//            @Override
//            public void onError(String code, String error) {
//                Log.d("makeNweUser",code+" "+error);
//            }
//        });
    }

    /* 1
    * getting the registered projects in the main projects table */
//    private void getProjects(RequestCallback callback) {
//        String projectsUrl = "https://ratco-solutions.com/Checkin/getProjects.php";
//        StringRequest re = new StringRequest(Request.Method.POST, projectsUrl, response -> {
//            Log.d("getProjectsResp" , response);
//            try {
//                JSONArray arr = new JSONArray(response);
//                Names = new String[arr.length()];
//                for (int i = 0; i < arr.length(); i++) {
//                    JSONObject row = arr.getJSONObject(i);
//                    projects.add(new PROJECT(row.getInt("id"), row.getString("projectName"), row.getString("city"), row.getString("salesman"), row.getString("TuyaUser"), row.getString("TuyaPassword"), row.getString("LockUser"), row.getString("LockPassword"), row.getString("url")));
//                    Names[i] = row.getString("projectName");
//                }
//            } catch (JSONException e) {
//                Log.d("getProjectsResp", e.toString());
//                callback.onFail(e.getMessage());
//            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,Names);
//            PROJECTS_SPINNER.setAdapter(adapter);
//            PROJECTS_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
//                    MyApp.THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
//            callback.onSuccess();
//        }, error -> {
//            Log.d("getProjectsResp" , error.toString());
//            callback.onFail(error.toString());
//        });
//        Q.add(re);
//    }
//
//    /* 2
//    * if there is project saved in shared preferences save it and go to tuya login
//    * if not show the login form */
//    private void goNext() {
//        projectName = pref.getString("projectName", null);
//        tuyaUser = pref.getString("tuyaUser", null);
//        tuyaPassword = pref.getString("tuyaPassword", null);
//        lockUser = pref.getString("lockUser", null);
//        lockPassword = pref.getString("lockPassword", null);
//        if (projectName == null) {
//            LinearLayout loginLayout = findViewById(R.id.login_layout);
//            LinearLayout loadingLayout = findViewById(R.id.loading_layout);
//            loadingLayout.setVisibility(View.GONE);
//            loginLayout.setVisibility(View.VISIBLE);
//            password = findViewById(R.id.editTextTextPersonName);
//        }
//        else {
//            for (int i=0;i<projects.size();i++) {
//                if (projectName.equals(projects.get(i).projectName)) {
//                    THE_PROJECT = projects.get(i);
//                    MyApp.THE_PROJECT = projects.get(i);
//                    logInFunction(THE_PROJECT);
//                    break;
//                }
//            }
//        }
//    }
//
//    /* 3
//    * login to tuya and get the registered homes and get the the project homes
//    * if he did not find project homes , make one */
//    void logInFunction(PROJECT p) {
//        MyApp.ProjectHomes.clear();
//        String COUNTRY_CODE = "966";
//        TuyaHomeSdk.getUserInstance().loginWithEmail(COUNTRY_CODE,p.TuyaUser ,p.TuyaPassword , new ILoginCallback() {
//            @Override
//            public void onSuccess (User user) {
//                Log.d("tuyaLoginResp",user.getNickName());
//                MyApp.TuyaUser = user ;
//                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
//                    @Override
//                    public void onError(String errorCode, String error) {
//                        Log.d("tuyaLoginResp",error+" "+errorCode);
//                        AlertDialog.Builder b = new AlertDialog.Builder(act);
//                        b.setTitle("Tuya Login Error")
//                                .setMessage("login to tuya failed \n "+error+" \n try again ?")
//                                .setNegativeButton("Cancel", (dialogInterface, i) -> act.finish())
//                                .setPositiveButton("Yes", (dialogInterface, i) -> logInFunction(MyApp.THE_PROJECT))
//                                .create().show();
//                    }
//                    @Override
//                    public void onSuccess(List<HomeBean> homeBeans) {
//                        MyApp.homeBeans = homeBeans ;
//                        Homes = homeBeans ;
//                        for(int i = 0; i< Homes.size(); i++) {
//                            Log.d("tuyaLoginResp", Homes.get(i).getName());
//                            if (MyApp.THE_PROJECT.projectName.equals("apiTest")) {
//                                if (Homes.get(i).getName().contains("Test")) {
//                                    MyApp.ProjectHomes.add(new CheckInHome(Homes.get(i),null));
//                                }
//                            }
//                            else if (Homes.get(i).getName().contains(MyApp.THE_PROJECT.projectName)) {
//                                MyApp.ProjectHomes.add(new CheckInHome(Homes.get(i),null));
//                            }
//                        }
//                        if (!MyApp.ProjectHomes.isEmpty()) {
//                            Intent i = new Intent(act , Rooms.class);
//                            act.startActivity(i);
//                            act.finish();
//                        }
//                        else {
//                            TuyaHomeSdk.getHomeManagerInstance().createHome(MyApp.THE_PROJECT.projectName, 0, 0,"ksa",new ArrayList<>(), new ITuyaHomeResultCallback() {
//                                @Override
//                                public void onSuccess(HomeBean bean) {
//                                    MyApp.ProjectHomes.add(new CheckInHome(bean,null));
//                                    Intent i = new Intent(act , Rooms.class);
//                                    act.startActivity(i);
//                                    act.finish();
//                                }
//                                @Override
//                                public void onError(String errorCode, String errorMsg) {
//                                    // do something
//                                    new MessageDialog(errorMsg+" "+errorCode,"Create Home Failed",act);
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onError (String code, String error) {
//                Log.d("tuyaLoginResp",error+" "+code);
//                AlertDialog.Builder b = new AlertDialog.Builder(act);
//                b.setTitle("Tuya Login Error")
//                        .setMessage("login to tuya failed \n "+error+" \n try again ?")
//                        .setNegativeButton("Cancel", (dialogInterface, i) -> act.finish()).setPositiveButton("Yes", (dialogInterface, i) -> logInFunction(MyApp.THE_PROJECT)).create().show();
//            }
//        });
//    }
//
//    /*login to new project and save project information to shared preferences then login to tuya and get project homes*/
//    public void LogIn(View view) {
//        if (THE_PROJECT != null ) {
//            final LoadingDialog loading = new LoadingDialog(act);
//            final String pass = password.getText().toString();
//            StringRequest re = new StringRequest(Request.Method.POST, THE_PROJECT.url + projectLoginUrl, response -> {
//                Log.d("projectIs" ,response);
//                loading.stop();
//                try {
//                    JSONObject resp = new JSONObject(response);
//                    if (resp.getString("result").equals("success")) {
//                        Toast.makeText(act, "Login Success", Toast.LENGTH_LONG).show();
//                        editor.putString("projectName", THE_PROJECT.projectName);
//                        editor.putString("tuyaUser", THE_PROJECT.TuyaUser);
//                        editor.putString("tuyaPassword", THE_PROJECT.TuyaPassword);
//                        editor.putString("lockUser", THE_PROJECT.LockUser);
//                        editor.putString("lockPassword", THE_PROJECT.LockPassword);
//                        editor.putString("url", THE_PROJECT.url);
//                        editor.apply();
//                        MyApp.my_token = resp.getString("token");
//                        THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
//                        MyApp.THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
//                        Log.d("GettingRooms", "start " + MyApp.THE_PROJECT.url);
//                        logInFunction(THE_PROJECT);
//                    } else {
//                        new MessageDialog(resp.getString("error"), "Login Failed ", act);
//                    }
//                } catch (JSONException e) {
//                    new MessageDialog(e.getMessage(), "Login Failed ", act);
//                }
//            }, error -> {
//                loading.stop();
//                new MessageDialog(error.toString(),"Login Failed ",act);
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String,String> par = new HashMap<>();
//                    par.put( "password" , pass ) ;
//                    par.put( "project_name" , THE_PROJECT.projectName ) ;
//                    return par;
//                }
//            };
//            Q.add(re);
//        }
//        else {
//            new MessageDialog("please select project","Select Project",act);
//        }
//    }
//
//    public void Continue(View view) {
//        Intent i = new Intent(act , Rooms.class);
//        act.startActivity(i);
//    }

    void setActivity() {
        act = this ;
        setViews();
        Q = Volley.newRequestQueue(act);
        storage = new LocalDataStore();
        MyApp.setLocalStorage(storage);
    }

    void setViews() {
        jobNumber = findViewById(R.id.userjobnumber);
        password = findViewById(R.id.editTextTextPersonName);
        loadingLayout = findViewById(R.id.loading_layout);
        loginLayout = findViewById(R.id.login_layout);
    }

    void setActivityActions() {
        Button login = findViewById(R.id.button);
        login.setOnClickListener(view -> {
            showError("");
            showCaption("logging in ...");
            final String pass = password.getText().toString();
            final String jNum = jobNumber.getText().toString();
            USER user0 = new USER();
            user0.login(jNum, pass, Q, new LoginCallback() {
                @Override
                public void loggedIn(USER user) {
                    showCaption("");
                    storage.saveUser(user);
                    MyApp.user = user;
                    goToProjects();
                }

                @Override
                public void onUserOrPasswordMistake() {
                    showCaption("");
                    showError("login failed \n invalid job number or password");
                }

                @Override
                public void onError(String error) {
                    showCaption("");
                    showError("login failed \n"+error);
                }
            });
            storage.saveBoolean(true,"firstRun");
//            PROJECT.loginProject(Q, selectedProject, pass, new ProjectLoginCallback() {
//                @Override
//                public void onSuccess() {
//                    showCaption("adding new control devices");
//                    MyApp.My_PROJECT = selectedProject;
//                    storage.saveProject(selectedProject,"project");
//                    showCaption("login to T");
//                    Tuya.loginTuya(MyApp.My_PROJECT, new ILoginCallback() {
//                        @Override
//                        public void onSuccess(User user) {
//                            showCaption("getting homes");
//                            MyApp.TuyaUser = user;
//                            Tuya.getProjectHomes(MyApp.My_PROJECT,storage, new ITuyaGetHomeListCallback() {
//                                @Override
//                                public void onSuccess(List<HomeBean> homeBeans) {
//                                    //MyApp.PROJECT_HOMES = homeBeans;
//                                    goToRooms();
//                                }
//
//                                @Override
//                                public void onError(String errorCode, String error) {
//                                    showError("getting tuya homes failed \n"+error);
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onError(String code, String error) {
//                            showError("login tuya failed \n"+error);
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(String error) {
//                    showError("adding new control device failed \n"+error);
//                }
//            });
        });
    }

    void bootUp() {
        MyApp.user = storage.getUser();
        if (MyApp.user == null) {
            MyApp.isNetworkAvailable(MyApp.app, new RequestCallback() {
                @Override
                public void onSuccess() {
                    prepareLoginLayout();
                }

                @Override
                public void onFail(String error) {
                    prepareLoginLayout();
                }
            });
        }
        else {
            MyApp.isNetworkAvailable(MyApp.app, new RequestCallback() {
                @Override
                public void onSuccess() {
                    goToProjects();
                }

                @Override
                public void onFail(String error) {
                    goToProjects();
                }
            });
            storage.saveBoolean(false,"firstRun");
        }
    }

    void boot() {
        showCaption(getResources().getString(R.string.loading));
        MyApp.My_PROJECT = storage.getProject("project");
        if (MyApp.My_PROJECT == null) {
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
                    goToProjects();
                }

                @Override
                public void onFail(String error) {
                    goToProjects();
                }
            });
        }
    }

    void prepareLoginLayout() {
        loadingLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
//        PROJECT.getProjects(Q, new GetProjectsCallback() {
//            @Override
//            public void onSuccess(List<PROJECT> projects) {
//                String[] Names = new String[projects.size()];
//                for(int i=0;i<projects.size();i++) {
//                    Names[i] = projects.get(i).projectName;
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,Names);
//                PROJECTS_SPINNER.setAdapter(adapter);
//                PROJECTS_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        selectedProject = projects.get(i);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onError(String error) {
//                showError(error);
//                //ControlDevice.restartApplication(5,act);
//            }
//        });
    }

    void goToProjects() {
        Intent i = new Intent(act , Projects.class);
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