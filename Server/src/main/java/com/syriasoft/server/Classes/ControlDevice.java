package com.syriasoft.server.Classes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.server.Classes.Interfaces.ControlDeviceCallback;
import com.syriasoft.server.Classes.Interfaces.ControlDeviceListener;
import com.syriasoft.server.Classes.Interfaces.GerRoomsCallback;
import com.syriasoft.server.Classes.Interfaces.GetControlDevicesCallback;
import com.syriasoft.server.Classes.Property.PropertyDB;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Interface.GetDeviceLastWorkingTime;
import com.syriasoft.server.Interface.RequestCallback;
import com.syriasoft.server.Login;
import com.syriasoft.server.MyApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ControlDevice {

    private static final String getControlDeviceUrl = "";
    private static final String getControlDevicesUrl = "";
    private static final String addNewControlDevice = "roomsManagement/addControlDevice";
    private static final String deleteControlDevice = "roomsManagement/deleteControlDevice";
    private static final String getRoomsUrl = "roomsManagement/getRoomsForControllDevice";
    private static final String getAllRoomsUrl = "roomsManagement/getRooms";

    public int id ;
    public String name;
    public String roomsIds;
    public int status;
    public String token;
    public static List<Room> myRooms;
    DatabaseReference deviceReference;

    public ControlDevice(JSONObject obj,List<Room> rooms) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.roomsIds = obj.getString("roomsIds");
            this.status = obj.getInt("status");
            this.token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
            setMyRooms(rooms);
    }

    public ControlDevice(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.roomsIds = obj.getString("roomsIds");
            this.status = obj.getInt("status");
            this.token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ControlDevice(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.roomsIds = obj.getString("roomsIds");
            this.status = obj.getInt("status");
            this.token = obj.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setControlDeviceFirebaseListener(DatabaseReference deviceReference, ControlDeviceListener listener) {
        this.deviceReference = deviceReference;
        deviceReference.child("roomsIds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onRoomsChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.toString());
            }
        });
    }

    public static void addNewControlDevice(RequestQueue queue, ControlDeviceCallback result) {
        queue.add(new StringRequest(Request.Method.GET, MyApp.My_PROJECT.url + addNewControlDevice, response -> {
            try {
                JSONObject resp = new JSONObject(response);
                if (resp.getString("result").equals("success")) {
                    JSONObject device = resp.getJSONObject("device");
                    ControlDevice d = new ControlDevice(device);
                    result.onSuccess(d);
                }
                else {
                    result.onError(resp.getString("error"));
                }
            } catch (JSONException e) {
                result.onError(e.getMessage());
            }
        }, error -> {
            result.onError(error.toString());
        }));
    }

    public void deleteControlDevice(RequestQueue Q, RequestCallback callback) {
        String url = MyApp.My_PROJECT.url + deleteControlDevice;
        StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("deleteDeviceStatus" , response);
            try {
                JSONObject res = new JSONObject(response);
                if (res.getString("result").equals("success")) {
                    callback.onSuccess();
                }
                else {
                    callback.onFail(res.getString("error"));
                }
            } catch (JSONException e) {
                callback.onFail(e.getMessage());
            }


        }, error -> {
            callback.onFail(error.toString());
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", String.valueOf(id));
                return params;
            }
        };
        Q.add(req);
    }

    public static void getControlDevices(RequestQueue queue, GetControlDevicesCallback result) {
        queue.add(new StringRequest(Request.Method.GET, getControlDevicesUrl, response -> {
            try {
                List<ControlDevice> devices = new ArrayList<>();
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    devices.add(new ControlDevice(row));
                }
                result.onSuccess(devices);
            } catch (JSONException e) {
                result.onError(e.getMessage());
            }

        }, error -> {
            result.onError(error.toString());
        }));
    }

    public void getMyRooms(DatabaseReference deviceReference, RequestQueue queue, PropertyDB pDB, GerRoomsCallback result) {
        if (pDB.isRoomsInserted()) {
            Log.d("bootingOp","rooms locally");
            result.onSuccess(pDB.getRooms());
        }
        else {
            Log.d("bootingOp","rooms internet");
            setControlDeviceFirebaseListener(deviceReference, new ControlDeviceListener() {
                @Override
                public void onRoomsChanged() {
                    queue.add(new StringRequest(Request.Method.POST,MyApp.My_PROJECT.url+getRoomsUrl, response -> {
                        Log.d("gettingRooms",response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("result").equals("success")) {
                                List<Room> rooms = new ArrayList<>();
                                JSONArray arr = new JSONArray(res.getString("rooms"));
                                for (int i=0;i<arr.length();i++) {
                                    JSONObject row = arr.getJSONObject(i);
                                    rooms.add(new Room(row));
                                }
                                result.onSuccess(rooms);
                            }
                            else {
                                result.onError(res.getString("error"));
                            }
                        } catch (JSONException e) {
                            result.onError(e.getMessage());
                        }
                    }, error -> {
                        result.onError(error.toString());
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("device_id", String.valueOf(id));
                            return params;
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    result.onError(error);
                }
            });
        }
    }

    public void getMyRooms(PROJECT p,DatabaseReference deviceReference,RequestQueue queue, GerRoomsCallback result) {
        setControlDeviceFirebaseListener(deviceReference, new ControlDeviceListener() {
            @Override
            public void onRoomsChanged() {
                queue.add(new StringRequest(Request.Method.POST,p.url+getRoomsUrl, response -> {
                    Log.d("gettingRooms",response);
                    try {
                        JSONObject res = new JSONObject(response);
                        if (res.getString("result").equals("success")) {
                            List<Room> rooms = new ArrayList<>();
                            JSONArray arr = new JSONArray(res.getString("rooms"));
                            for (int i=0;i<arr.length();i++) {
                                JSONObject row = arr.getJSONObject(i);
                                rooms.add(new Room(row));
                            }
                            result.onSuccess(rooms);
                        }
                        else {
                            result.onError(res.getString("error"));
                        }
                    } catch (JSONException e) {
                        result.onError(e.getMessage());
                    }
                }, error -> {
                    result.onError(error.toString());
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("device_id", String.valueOf(id));
                        return params;
                    }
                });
            }

            @Override
            public void onError(String error) {
                result.onError(error);
            }
        });
    }

    public void getAllRooms(RequestQueue queue,String projectUrl,GerRoomsCallback result) {
        queue.add(new StringRequest(Request.Method.GET, projectUrl + getAllRoomsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Room> rooms = new ArrayList<>();
                try {
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    rooms.add(new Room(row));
                }
                result.onSuccess(rooms);
                } catch (JSONException e) {
                    result.onError(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    public static void saveRoomsToStorage(LocalDataStore storage, List<Room> rooms) {
        storage.saveInteger(rooms.size(),"roomsCount");
        for (int i=0;i<rooms.size();i++) {
            storage.saveObject(rooms.get(i),"room"+i);
        }
    }

    public static List<Room> getRoomsFromStorage(LocalDataStore storage) {
        List<Room> rooms = new ArrayList<>();
        int count = storage.getInteger("roomsCount");
        for (int i=0;i<count;i++) {
            rooms.add((Room) storage.getObject("room"+i,Room.class));
        }
        return rooms;
    }

    public static void deleteRoomsFromLocalStorage(LocalDataStore storage) {
        int count = storage.getInteger("roomsCount");
        for (int i=0;i<count;i++) {
            storage.deleteObject("room"+i);
        }
        storage.deleteObject("roomsCount");
    }

    public void setMyRooms(List<Room> rooms) {
        if (roomsIds != null) {
            if (roomsIds.equals("all")) {
                myRooms = rooms;
            }
            else{
                String[] Ids = roomsIds.split("-");
                for (String id:Ids) {
                    for (Room r : rooms) {
                        if (r.id == Integer.parseInt(id)) {
                            myRooms.add(r);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void getMyDevice(RequestQueue queue, String device_id, ControlDeviceCallback callback) {
        queue.add(new StringRequest(Request.Method.POST, getControlDeviceUrl, response -> callback.onSuccess(new ControlDevice(response)), error -> callback.onError(error.toString())) {
            @NonNull
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> params = new HashMap<>();
                params.put("device_id",device_id);
                return params;
            }
        });
    }

    public static void setCurrentAction(TextView tv, String action) {
        Calendar ca = Calendar.getInstance();
        String dateTime = ca.get(Calendar.YEAR)+"-"+ca.get(Calendar.MONTH)+"-"+ca.get(Calendar.DAY_OF_MONTH)+" "+ca.get(Calendar.HOUR_OF_DAY)+":"+ca.get(Calendar.MINUTE);
        String act = action+" "+dateTime;
        tv.setText(act);
    }

    public static void setCurrentError(Activity act,int textView_id, String error) {
        TextView CurrentAction = act.findViewById(textView_id);
        CurrentAction.setText(error);
    }

    public static void restartApplication(int seconds,Activity act) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(act, Login.class);
                act.startActivity(i);
                act.finish();
            }
        }, 1000L *seconds);
    }

    public void addControlDeviceToFirebase(DatabaseReference myReference) {
        myReference.child("id").setValue(id);
        myReference.child("name").setValue(name);
        myReference.child("roomsIds").setValue("all");
        myReference.child("status").setValue(status);
        myReference.child("token").setValue(token);
    }

    public void getLastDeviceWorking(DatabaseReference deviceReference,GetDeviceLastWorkingTime callback) {
        this.deviceReference = deviceReference;
        deviceReference.child("working").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callback.onSuccess(Long.parseLong(snapshot.getValue().toString()));
                }
                else {
                    callback.onError("value is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }

}
