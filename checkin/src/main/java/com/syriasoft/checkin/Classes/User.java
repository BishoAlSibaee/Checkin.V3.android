package com.syriasoft.checkin.Classes;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.checkin.Classes.Interfaces.GetRoomsCallback;
import com.syriasoft.checkin.Classes.Interfaces.GetUserCallBack;
import com.syriasoft.checkin.Classes.Interfaces.GetUsersCallback;
import com.syriasoft.checkin.Classes.Property.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String getUserRoomsUrl = "users/getUserRooms";

    public int id;
    int projectId;
    public String name;
    public int jobNumber;
    public String department;
    public String mobile;
    String token;
    String mytoken;
    public String control;
    int logedin;

    static List<User> users;
    static List<Room> my_rooms ;

    public User(int id, int projectId, String name, int jobNumber, String department, String mobile, String token, String mytoken, String control, int logedin) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.jobNumber = jobNumber;
        this.department = department;
        this.mobile = mobile;
        this.token = token;
        this.mytoken = mytoken;
        this.control = control;
        this.logedin = logedin;
    }

    public User(JSONObject obj) throws JSONException {
        try {
            this.id = obj.getInt("id");
            this.projectId = obj.getInt("projectId");
            this.name = obj.getString("name");
            this.jobNumber = obj.getInt("jobNumber");
            this.department = obj.getString("department");
            this.mobile = obj.getString("mobile");
            this.token = obj.getString("token");
            this.mytoken = obj.getString("mytoken");
            this.control = obj.getString("control");
            this.logedin = obj.getInt("logedin");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getIsUserAvailable() {
        return logedin == 1;
    }

    public void getMyRooms(RequestQueue Q, String projectUrl, GetRoomsCallback callback) {
        List<Room> rooms = new ArrayList<>();
        Q.add(new StringRequest(Request.Method.POST, projectUrl + getUserRoomsUrl, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    rooms.add(new Room(arr.getJSONObject(i)));
                }
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }

            callback.onSuccess(rooms);
        }, error -> {
            callback.onError(error.toString());
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        });
    }

    public static void getUserById(RequestQueue Q, String projectUrl, int id,GetUserCallBack callBack) {
        Q.add(new StringRequest(Request.Method.POST, projectUrl+"users/getUserById", response -> {
            try {
                JSONObject obj = new JSONObject(response);
                callBack.onSuccess(new User(obj.getInt("id"),obj.getInt("projectId"),obj.getString("name"),obj.getInt("jobNumber"),obj.getString("department"),obj.getString("mobile"),obj.getString("token"),obj.getString("mytoken"),obj.getString("control"),obj.getInt("logedin")));
            } catch (JSONException e) {
                callBack.onError(e.getMessage());
            }
        }, error -> {
            callBack.onError(error.toString());
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(id));
                return params;
            }
        });
    }

    public static void getUsers(RequestQueue Q, String projectUrl, GetUsersCallback callback) {
        List<User> users = new ArrayList<>();
        Q.add(new StringRequest(Request.Method.GET, projectUrl+"users/getALlUsers", response -> {
            try {
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    users.add(new User(row.getInt("id"),row.getInt("projectId"),row.getString("name"),row.getInt("jobNumber"),row.getString("department"),row.getString("mobile"),row.getString("token"),row.getString("mytoken"),row.getString("control"),row.getInt("logedin")));
                }
                callback.onSuccess(users);
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, error -> callback.onError(error.toString())));
    }
}
