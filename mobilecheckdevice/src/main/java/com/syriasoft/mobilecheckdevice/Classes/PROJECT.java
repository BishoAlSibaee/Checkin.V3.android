package com.syriasoft.mobilecheckdevice.Classes;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetProjectsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.ProjectLoginCallback;
import com.syriasoft.mobilecheckdevice.Classes.Property.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PROJECT {

    private static final String getProjectsUrl = "https://ratco-solutions.com/Checkin/getProjects.php";
    private static final String projectLoginUrl = "users/loginProject" ;

    public int id ;
    public String projectName ;
    public String city ;
    public String salesman ;
    public String TuyaUser;
    public String TuyaPassword;
    public String LockUser;
    public String LockPassword;
    public String url;
    static String myToken;

    public List<Building> buildings;


    public PROJECT(int id, String projectName, String city, String salesman, String tuyaUser, String tuyaPassword, String lockUser, String lockPassword, String url) {
        this.id = id;
        this.projectName = projectName;
        this.city = city;
        this.salesman = salesman;
        TuyaUser = tuyaUser;
        TuyaPassword = tuyaPassword;
        LockUser = lockUser;
        LockPassword = lockPassword;
        this.url = url;
    }

    public List<PROJECT> makeProjectsList (List<Object> list) {
        List<PROJECT> projects = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            PROJECT x = (PROJECT) list.get(i);
            projects.add(x);
        }
        return projects ;
    }

    public PROJECT getMyProject(int project_id,List<PROJECT> projects) {
        for (PROJECT p : projects) {
            if (p.id == project_id) {
                return p;
            }
        }
        return null;
    }

    public static void getProjects(RequestQueue queue, GetProjectsCallback callback) {
        queue.add(new StringRequest(Request.Method.POST, getProjectsUrl, response -> {
            try {
                List<PROJECT> projects = new ArrayList<>();
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    projects.add(new PROJECT(row.getInt("id"),row.getString("projectName"),row.getString("city"),row.getString("salesman"),row.getString("TuyaUser"),row.getString("TuyaPassword"),row.getString("LockUser"),row.getString("LockPassword"),row.getString("url")));
                }
                callback.onSuccess(projects);
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, error -> callback.onError(error.toString())));
    }

    public static void loginProject(RequestQueue queue, PROJECT project, String password, ProjectLoginCallback result) {
        queue.add(new StringRequest(Request.Method.POST, project.url + projectLoginUrl, response -> {
            try {
                JSONObject resp = new JSONObject(response);
                if (resp.getString("result").equals("success")) {
                    myToken = resp.getString("token");
                    result.onSuccess();
                }
                else {
                    result.onError(resp.getString("error"));
                }
            }
            catch (JSONException e) {
                result.onError(e.getMessage());
            }
        }, error -> result.onError(error.toString())){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> par = new HashMap<>();
                par.put( "password" , password) ;
                par.put( "project_name" , project.projectName) ;
                return par;
            }
        });
    }
}

