package hotelservices.syriasoft.cleanup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.syriasoft.hotelservices.BuildConfig;
import com.syriasoft.hotelservices.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import hotelservices.syriasoft.cleanup.Interface.RequestCallback;

public class LogIn extends AppCompatActivity {
    public static String URL_GET_PROJECT = "https://ratco-solutions.com/Checkin/getProjects.php";
    public final String SHARED_PREF_NAME = "MyPref";
    public static String getPro;
    private ArrayList<String> getProList;
    private String jobNumber;
    private String password;
    private Activity act;
    private String loginUrl, checkUserUrl;
    private List<RESTAURANT_UNIT> Restaurants ;
    private Spinner facilities, depS, projectNamesSpinner;
    private RESTAURANT_UNIT THE_RESTAURANT;
    private TextInputLayout pass, job;
    LinearLayout loginLayout,logoLayout;
    TextView versionTV;
    int Version;
    int count = 0;
    ImageView secretProjectChange;
    List<Projects> projectsList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;
    RequestQueue Q;
    LoadingDialog loading;
    Gson g;

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
            getProjects(new RequestCallback() {
                @Override
                public void onSuccess() {
                    MyApp.MyProject = getProjectDataFromSharedPreferences();
                    if (MyApp.MyProject == null) {
                        loading.close();
                        setProjects();
                        prepareDepartments();
                    }
                    else {
                        getRestaurants(new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                projectNamesSpinner.setVisibility(View.GONE);
                                User u = getUserDataFromSharedPreferences();
                                if (u == null) {
                                    loading.close();
                                    prepareDepartments();
                                    loginLayout.setVisibility(View.VISIBLE);
                                }
                                else {
                                    if (u.jobNumber == 0) {
                                        loading.close();
                                        MyApp.My_USER = u;
                                        Intent i = new Intent(act, RestaurantOrders.class);
                                        startActivity(i);
                                        act.finish();
                                    }
                                    else {
                                        checkUser(String.valueOf(u.id),new VolleyCallback() {
                                            @Override
                                            public void onSuccess(String res) {
                                                Log.d("checkUser",res);
                                                loading.close();
                                                if (res.contains("1")) {
                                                    Log.d("checkUser","yes");
                                                    MyApp.My_USER = u;
                                                    Intent i = new Intent(act, MainActivity.class);
                                                    startActivity(i);
                                                    act.finish();
                                                }
                                                else {
                                                    Log.d("checkUser","no");
                                                    loginLayout.setVisibility(View.VISIBLE);
                                                    logoLayout.setVisibility(View.VISIBLE);
                                                    prepareDepartments();
                                                    new messageDialog("Your account has been deleted", "Warning", act);
                                                }
                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                loading.close();
                                                MyApp.My_USER = u;
                                                Intent i = new Intent(act, MainActivity.class);
                                                startActivity(i);
                                                act.finish();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFail(String error) {
                                loading.close();
                                createRestartConfirmationDialog(error);
                            }
                        });
                    }
                }

                @Override
                public void onFail(String error) {
                    loading.close();
                    createRestartConfirmationDialog(error);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void setActivity() {
        defineLists();
        defineViews();
    }

    void defineViews() {
        act = this;
        MyApp.actList.add(act);
        depS = findViewById(R.id.Login_department);
        loginLayout = findViewById(R.id.loginLayout);
        job = findViewById(R.id.Login_jobNumber);
        pass = findViewById(R.id.Login_password);
        facilities = findViewById(R.id.facility_spinner);
        projectNamesSpinner = findViewById(R.id.projectName);
        versionTV = findViewById(R.id.textView10);
        secretProjectChange = findViewById(R.id.imageView5);
        logoLayout = findViewById(R.id.logo_layout);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loginLayout.setVisibility(View.GONE);
        logoLayout.setVisibility(View.VISIBLE);
        Version = BuildConfig.VERSION_CODE;
        versionTV.setText(MessageFormat.format("Version {0}", Version));
        Q = Volley.newRequestQueue(act);
        g = new Gson();
    }

    void defineLists() {
        projectsList = new ArrayList<>();
        Restaurants = new ArrayList<>();
        getProList = new ArrayList<>();
    }

    void setActivityActions() {
        secretProjectChange.setOnLongClickListener(v -> {
            count++;
            if (count == 4) {
                MyApp.MyProject = null ;
                saveProjectDataToSharedPreferences(null);
                setProjects();
            }
            return false;
        });
        projectNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApp.MyProject = projectsList.get(position);
                loading.show();
                getRestaurants(new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        prepareDepartments();
                        loading.close();
                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        facilities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                THE_RESTAURANT = Restaurants.get(position);
                Log.d("selectedFacility", THE_RESTAURANT.Name + " " + THE_RESTAURANT.id);
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
        if (depS.getSelectedItem().toString().equals("Restaurant")) {
            loginUrl = MyApp.MyProject.Url + "facilitys/loginFacilityUser";
        } else {
            loginUrl = MyApp.MyProject.Url + "users/login";
        }
        jobNumber = Objects.requireNonNull(job.getEditText()).getText().toString();
        password = Objects.requireNonNull(pass.getEditText()).getText().toString();
        StringRequest re = new StringRequest(Request.Method.POST, loginUrl, response -> {
            Log.d("loginResp" , response+" "+loginUrl);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    loading.close();
                    Log.d("loginResp" , "success");
                    MyApp.MyProject = getProjectDataFromSharedPreferences();
                    if (MyApp.MyProject == null) {
                        saveProjectDataToSharedPreferences(projectsList.get(projectNamesSpinner.getSelectedItemPosition()));
                        MyApp.MyProject = projectsList.get(projectNamesSpinner.getSelectedItemPosition());
                    }
                    JSONObject user = new JSONObject(result.getString("user"));
                    MyApp.Token = result.getString("my_token");
                    if (depS.getSelectedItem().toString().equals("Restaurant")) {
                        Log.d("loginResp" , "restaurant");
                        MyApp.My_USER = new User(user.getInt("id"),user.getString("Name"),0,user.getInt("Mobile"),"Restaurant",user.getString("token"),"",1,MyApp.Token);
                        saveUserDataToSharedPreferences(MyApp.My_USER);
                        saveRestaurantDataToSharedPreferences(user.getString("Name"));
                        Intent i = new Intent(act, RestaurantOrders.class);
                        startActivity(i);
                        act.finish();
                    }
                    else {
                        if (user.getInt("logedin") == 0) {
                            new messageDialog("Your account has been deleted", "Warning", act);
                        }
                        else {
                            MyApp.My_USER = new User(user.getInt("id"),user.getString("name"),user.getInt("jobNumber"),user.getInt("mobile"),user.getString("department"),user.getString("token"),user.getString("control"),user.getInt("logedin"),MyApp.Token);
                            saveUserDataToSharedPreferences(MyApp.My_USER);
                            Intent i = new Intent(act, MainActivity.class);
                            startActivity(i);
                            act.finish();
                        }
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
                if (depS.getSelectedItem().toString().equals("Restaurant")) {
                    params.put("user", jobNumber);
                    params.put("password", password);
                    params.put("facility_id", String.valueOf(THE_RESTAURANT.id));
                } else {
                    params.put("job_number", jobNumber);
                    params.put("password", password);
                    params.put("department", depS.getSelectedItem().toString());
                }
                return params;
            }
        };
        Q.add(re);
    }

    void prepareDepartments() {
        String[] items = new String[]{"Service", "Laundry", "Cleanup", "RoomService", "Gym"};
        if (Restaurants.size() > 0) {
            items = new String[]{"Service", "Laundry", "Cleanup", "Restaurant", "RoomService", "Gym"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        depS.setAdapter(adapter);
        depS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (depS.getSelectedItem().toString().equals("Restaurant") || depS.getSelectedItem().toString().equals("CoffeeShop")) {
                    facilities.setVisibility(View.VISIBLE);
                    setRestaurants();
                    job.setHint("Enter User ");
                } else {
                    facilities.setVisibility(View.GONE);
                    job.setHint("Enter JobNumber ");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        loginLayout.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void getProjects(RequestCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_PROJECT, response -> {
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    projectsList.add(new Projects(object.getInt("id"), object.getString("projectName"), object.getString("city"), object.getString("salesman"), object.getString("TuyaUser"), object.getString("TuyaPassword"), object.getString("LockUser"), object.getString("LockPassword"),object.getString("url")));
                    getPro = object.getString("projectName");
                    getProList.add(getPro);
                }
                callback.onSuccess();
            } catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> callback.onFail(error.toString()));
        Q.add(request);
    }

    void setProjects() {
        projectNamesSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapterProj = new ArrayAdapter<>(act, R.layout.spinner_item, getProList);
        projectNamesSpinner.setAdapter(adapterProj);
    }

    private void checkUser(String userId,VolleyCallback callback) {
        checkUserUrl = MyApp.MyProject.Url + "users/checkUser";
        Log.d("checkUser",checkUserUrl+" "+userId);
        StringRequest request = new StringRequest(Request.Method.POST, checkUserUrl, response -> {
            Log.d("checkUser" , response);
            callback.onSuccess(response);
        }, error -> {
            Log.d("checkUser" , error.toString());
            callback.onFailed(error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        Q.add(request);
    }

    private void getRestaurants(RequestCallback callback) {
        Restaurants.clear();
        String url = MyApp.MyProject.Url + "facilitys/getfacilitys";
        StringRequest laundryRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    if (row.getString("TypeName").equals("Restaurant") || row.getString("TypeName").equals("CoffeeShop")) {
                        Restaurants.add(new RESTAURANT_UNIT(row.getInt("id"), row.getInt("Hotel"), row.getInt("TypeId"), row.getString("TypeName"), row.getString("Name"), row.getInt("Control"), row.getString("photo")));
                    }
                }
                callback.onSuccess();
            } catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> callback.onFail(error.toString()));
        Q.add(laundryRequest);
    }

    void setRestaurants() {
        String[] RESTAURANTS = new String[Restaurants.size()];
        for (int i = 0; i < Restaurants.size(); i++) {
            RESTAURANTS[i] = Restaurants.get(i).Name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(act, R.layout.spinner_item, RESTAURANTS);
        facilities.setAdapter(adapter);
        facilities.setVisibility(View.VISIBLE);
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

    Projects getProjectDataFromSharedPreferences() {
        String stringProject = sharedPreferences.getString("project",null);
        return g.fromJson(stringProject,Projects.class);
    }

    void saveProjectDataToSharedPreferences(Projects p) {
        if (p == null) {
            editor.remove("project");
            editor.apply();
        }
        else {
            String stringProject = g.toJson(p);
            editor.putString("project",stringProject);
            editor.apply();
        }
    }

    void saveRestaurantDataToSharedPreferences(String name) {
        editor.putString("JobNumber", String.valueOf(0));
        editor.putString("Name", name);
        editor.putString("Department", "Restaurant");
        editor.putString("FacilityId", String.valueOf(THE_RESTAURANT.id));
        editor.putString("FacilityName", THE_RESTAURANT.Name);
        editor.putString("FacilityPhoto", THE_RESTAURANT.photo);
        editor.putString("FacilityType", THE_RESTAURANT.TypeName);
        editor.putString("FacilityTypeId", String.valueOf(THE_RESTAURANT.TypeId));
        editor.apply();
    }

    User getUserDataFromSharedPreferences() {
        String stringUser = sharedPreferences.getString("user",null);
        return g.fromJson(stringUser,User.class);
    }

    void saveUserDataToSharedPreferences(User u) {
        if (u == null) {
            editor.remove("user");
            editor.apply();
        }
        else {
            String stringUser = g.toJson(u);
            editor.putString("user",stringUser);
            editor.apply();
        }
    }
}

