package com.syriasoft.projectscontrol;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.projectscontrol.RequestCallBacks.BuildingsCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.FloorsCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.RoomsCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.ServerDevicesCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PROJECT {
    int id ;
    public String projectName ;
    String city ;
    String salesman ;
    String TuyaUser;
    String TuyaPassword;
    String LockUser;
    String LockPassword;
    String url;
    List<ServerDevice> ServerDevices ;
    List<BUILDING> Buildings;
    List<FLOOR> AllFloors;
    List<ROOM> AllRooms;
    private final String getDevicesUrl = "roomsManagement/getServerDevices";
    PROJECT p;


    public PROJECT(int id, String projectName, String city, String salesman, String tuyaUser, String tuyaPassword, String lockUser, String lockPassword, String url) {
        this.id = id;
        this.projectName = projectName;
        this.city = city;
        this.salesman = salesman;
        this.TuyaUser = tuyaUser;
        this.TuyaPassword = tuyaPassword;
        this.LockUser = lockUser;
        this.LockPassword = lockPassword;
        this.url = url;
        this.ServerDevices = new ArrayList<>();
        this.Buildings = new ArrayList<>();
        p = this;
    }

    public List<PROJECT> makeProjectsList (List<Object> list) {
        List<PROJECT> projects = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            PROJECT x = (PROJECT) list.get(i);
            projects.add(x);
        }
        return projects ;
    }

    void getProjectServerDevices(RequestQueue Q) {
        StringRequest request = new StringRequest(Request.Method.GET,url+getDevicesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<ServerDevice> list = new ArrayList<>();
                    JSONArray arr = new JSONArray(response);
                    for (int i=0;i<arr.length();i++) {
                        JSONObject o = arr.getJSONObject(i);
                        ServerDevice sd = new ServerDevice(projectName,o.getInt("id"),o.getString("name"),o.getString("roomsIds"),o.getInt("status"),o.getString("token"),false,p);
                        list.add(sd);
                    }
                    ServerDevices = list ;
                } catch (JSONException e) {
                    ServerDevices = null;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ServerDevices = null;
            }
        });
        Q.add(request);
    }

    void getProjectServerDevices(RequestQueue Q, ServerDevicesCallBack callBack) {
        StringRequest request = new StringRequest(Request.Method.GET,url+getDevicesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<ServerDevice> list = new ArrayList<>();
                    JSONArray arr = new JSONArray(response);
                    for (int i=0;i<arr.length();i++) {
                        JSONObject o = arr.getJSONObject(i);
                        ServerDevice sd = new ServerDevice(projectName,o.getInt("id"),o.getString("name"),o.getString("roomsIds"),o.getInt("status"),o.getString("token"),false,p);
                        list.add(sd);
                    }
                    ServerDevices = list ;
                    callBack.onSuccess(list);
                } catch (JSONException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailed(error.toString());
            }
        });
        Q.add(request);
    }

    void getProjectBuildings(RequestQueue Q) {
        String buildingsUrl = "roomsManagement/getbuildings";
        StringRequest buildingsReq = new StringRequest(Request.Method.GET, url+ buildingsUrl, response -> {
            Log.e("getBuildings" , response);
            try {
                JSONArray arr = new JSONArray(response);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    Buildings.add(new BUILDING(row.getInt("id"),row.getInt("projectId"),row.getInt("buildingNo"),row.getString("buildingName"),row.getInt("floorsNumber")));
                }
            } catch (JSONException e) {
                Log.d("getBuildings",e.getMessage());
            }

        }, error -> Log.d("getBuildings",error.toString()));
        Q.add(buildingsReq);
    }
    void getProjectBuildings(RequestQueue Q, BuildingsCallback callback) {
        String buildingsUrl = "roomsManagement/getbuildings";
        StringRequest buildingsReq = new StringRequest(Request.Method.GET, url+ buildingsUrl, response -> {
            Log.e("buildings" , response);
            try {
                JSONArray arr = new JSONArray(response);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    Buildings.add(new BUILDING(row.getInt("id"),row.getInt("projectId"),row.getInt("buildingNo"),row.getString("buildingName"),row.getInt("floorsNumber")));
                }
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
            callback.onSuccess(Buildings);

        }, error -> callback.onFail(error.toString()));
        Q.add(buildingsReq);
    }

    void getProjectFloors(RequestQueue Q) {
        String floorsUrl = "roomsManagement/getfloors";
        StringRequest floorsReq = new StringRequest(Request.Method.GET, url+ floorsUrl, response -> {
            AllFloors = new ArrayList<>();
            Log.e("floors" , response);
            try{
                JSONArray arr = new JSONArray(response);
                for (int i = 0 ; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    AllFloors.add(new FLOOR(row.getInt("id"),row.getInt("building_id"),row.getInt("floorNumber"),row.getInt("rooms")));
                }
            } catch (JSONException e) {
                Log.e("floors" , e.getMessage());
            }
        }, error -> Log.e("floors" , error.toString()));
        Q.add(floorsReq);
    }
    void getProjectFloors(RequestQueue Q, FloorsCallback callback) {
        String floorsUrl = "roomsManagement/getfloors";
        StringRequest floorsReq = new StringRequest(Request.Method.GET, url+ floorsUrl, response -> {
            AllFloors = new ArrayList<>();
            Log.e("floors" , response);
            try{
                JSONArray arr = new JSONArray(response);
                for (int i = 0 ; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    AllFloors.add(new FLOOR(row.getInt("id"),row.getInt("building_id"),row.getInt("floorNumber"),row.getInt("rooms")));
                }
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
            callback.onSuccess(AllFloors);
        }, error -> callback.onFail(error.toString()));
        Q.add(floorsReq);
    }

    void getProjectRooms(RequestQueue Q) {
        String roomsUrl = "roomsManagement/getRooms";
        StringRequest roomsReq = new StringRequest(Request.Method.GET, url+ roomsUrl, response -> {
            Log.e("roomRes" , response);
            try {
                AllRooms = new ArrayList<>();
                JSONArray arr = new JSONArray(response);
                for (int i = 0 ; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    int id = row.getInt("id");
                    int roomNumber = row.getInt("RoomNumber");
                    int status = row.getInt("Status");
                    int hotel = row.getInt("hotel");
                    int building = row.getInt("Building");
                    int building_id = row.getInt("building_id");
                    int floor = row.getInt("Floor");
                    int floor_id = row.getInt("floor_id");
                    String roomType = row.getString("RoomType");
                    int suiteStatus = row.getInt("SuiteStatus");
                    int suiteNumber = row.getInt("SuiteNumber");
                    int suiteId = row.getInt("SuiteId");
                    int reservationNumber = row.getInt("ReservationNumber");
                    int roomStatus = row.getInt("roomStatus");
                    int clientIn = row.getInt("ClientIn");
                    String message = row.getString("message");
                    int selected = row.getInt("selected");
                    int load = row.getInt("loading");
                    int tablet = row.getInt("Tablet");
                    String dep = row.getString("dep");
                    int cleanup = row.getInt("Cleanup");
                    int laundry = row.getInt("Laundry");
                    int roomService = row.getInt("RoomService");
                    String roomServiceText = row.getString("RoomServiceText");
                    int checkout = row.getInt("Checkout");
                    int restaurant = row.getInt("Restaurant");
                    int miniBarCheck = row.getInt("MiniBarCheck");
                    int facility = row.getInt("Facility");
                    int SOS = row.getInt("SOS");
                    int DND = row.getInt("DND");
                    int powerSwitch = row.getInt("PowerSwitch");
                    int doorSensor = row.getInt("DoorSensor");
                    int motionSensor = row.getInt("MotionSensor");
                    int thermostat = row.getInt("Thermostat");
                    int ZBGateway = row.getInt("ZBGateway");
                    int online = row.getInt("online");
                    int curtainSwitch = row.getInt("CurtainSwitch");
                    int serviceSwitch = row.getInt("ServiceSwitch");
                    int lock = row.getInt("lock");
                    int switch1 = row.getInt("Switch1");
                    int switch2 = row.getInt("Switch2");
                    int switch3 = row.getInt("Switch3");
                    int switch4 = row.getInt("Switch4");
                    int switch5 = row.getInt("Switch5");
                    int switch6 = row.getInt("Switch6");
                    int switch7 = row.getInt("Switch7");
                    int switch8 = row.getInt("Switch8");
                    String lockGateway = row.getString("LockGateway");
                    String lockName = row.getString("LockName");
                    int powerStatus = row.getInt("powerStatus");
                    int curtainStatus = row.getInt("curtainStatus");
                    int doorStatus = row.getInt("doorStatus");
                    int doorWarning = row.getInt("DoorWarning");
                    int temp = row.getInt("temp");
                    int tempSetPoint = row.getInt("TempSetPoint");
                    int setPointInterval = row.getInt("SetPointInterval");
                    int checkInModeTime = row.getInt("CheckInModeTime");
                    int checkOutModeTime = row.getInt("CheckOutModeTime");
                    String welcomeMessage = row.getString("WelcomeMessage");
                    String logo = row.getString("Logo");
                    String token =row.getString("token");
                    ROOM room = new ROOM(id,roomNumber,status,hotel,building,building_id,floor,floor_id,roomType,suiteStatus,suiteNumber,suiteId,reservationNumber,roomStatus,clientIn,message,selected,load,tablet,dep,cleanup,laundry
                            ,roomService,roomServiceText,checkout,restaurant,miniBarCheck,facility,SOS,DND,powerSwitch,doorSensor,motionSensor,thermostat,ZBGateway,online,curtainSwitch,serviceSwitch,lock,switch1,switch2,switch3,switch4,switch5,switch6,switch7,switch8,lockGateway
                            ,lockName,powerStatus,curtainStatus,doorStatus,doorWarning,temp,tempSetPoint,setPointInterval,checkInModeTime,checkOutModeTime,welcomeMessage,logo,token);
                    AllRooms.add(room);
                }
            } catch (JSONException e) {
                Log.e("roomRes" , e.getMessage());
            }
        }, error -> Log.e("roomRes" , error.toString()));
        Q.add(roomsReq);
    }
    void getProjectRooms(RequestQueue Q, RoomsCallback callback) {
        String roomsUrl = "roomsManagement/getRooms";
        StringRequest roomsReq = new StringRequest(Request.Method.GET, url+ roomsUrl, response -> {
            Log.e("roomRes" , response);
            try {
                AllRooms = new ArrayList<>();
                JSONArray arr = new JSONArray(response);
                for (int i = 0 ; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    int id = row.getInt("id");
                    int roomNumber = row.getInt("RoomNumber");
                    int status = row.getInt("Status");
                    int hotel = row.getInt("hotel");
                    int building = row.getInt("Building");
                    int building_id = row.getInt("building_id");
                    int floor = row.getInt("Floor");
                    int floor_id = row.getInt("floor_id");
                    String roomType = row.getString("RoomType");
                    int suiteStatus = row.getInt("SuiteStatus");
                    int suiteNumber = row.getInt("SuiteNumber");
                    int suiteId = row.getInt("SuiteId");
                    int reservationNumber = row.getInt("ReservationNumber");
                    int roomStatus = row.getInt("roomStatus");
                    int clientIn = row.getInt("ClientIn");
                    String message = row.getString("message");
                    int selected = row.getInt("selected");
                    int load = row.getInt("loading");
                    int tablet = row.getInt("Tablet");
                    String dep = row.getString("dep");
                    int cleanup = row.getInt("Cleanup");
                    int laundry = row.getInt("Laundry");
                    int roomService = row.getInt("RoomService");
                    String roomServiceText = row.getString("RoomServiceText");
                    int checkout = row.getInt("Checkout");
                    int restaurant = row.getInt("Restaurant");
                    int miniBarCheck = row.getInt("MiniBarCheck");
                    int facility = row.getInt("Facility");
                    int SOS = row.getInt("SOS");
                    int DND = row.getInt("DND");
                    int powerSwitch = row.getInt("PowerSwitch");
                    int doorSensor = row.getInt("DoorSensor");
                    int motionSensor = row.getInt("MotionSensor");
                    int thermostat = row.getInt("Thermostat");
                    int ZBGateway = row.getInt("ZBGateway");
                    int online = row.getInt("online");
                    int curtainSwitch = row.getInt("CurtainSwitch");
                    int serviceSwitch = row.getInt("ServiceSwitch");
                    int lock = row.getInt("lock");
                    int switch1 = row.getInt("Switch1");
                    int switch2 = row.getInt("Switch2");
                    int switch3 = row.getInt("Switch3");
                    int switch4 = row.getInt("Switch4");
                    int switch5 = row.getInt("Switch5");
                    int switch6 = row.getInt("Switch6");
                    int switch7 = row.getInt("Switch7");
                    int switch8 = row.getInt("Switch8");
                    String lockGateway = row.getString("LockGateway");
                    String lockName = row.getString("LockName");
                    int powerStatus = row.getInt("powerStatus");
                    int curtainStatus = row.getInt("curtainStatus");
                    int doorStatus = row.getInt("doorStatus");
                    int doorWarning = row.getInt("DoorWarning");
                    int temp = row.getInt("temp");
                    int tempSetPoint = row.getInt("TempSetPoint");
                    int setPointInterval = row.getInt("SetPointInterval");
                    int checkInModeTime = row.getInt("CheckInModeTime");
                    int checkOutModeTime = row.getInt("CheckOutModeTime");
                    String welcomeMessage = row.getString("WelcomeMessage");
                    String logo = row.getString("Logo");
                    String token =row.getString("token");
                    ROOM room = new ROOM(id,roomNumber,status,hotel,building,building_id,floor,floor_id,roomType,suiteStatus,suiteNumber,suiteId,reservationNumber,roomStatus,clientIn,message,selected,load,tablet,dep,cleanup,laundry
                            ,roomService,roomServiceText,checkout,restaurant,miniBarCheck,facility,SOS,DND,powerSwitch,doorSensor,motionSensor,thermostat,ZBGateway,online,curtainSwitch,serviceSwitch,lock,switch1,switch2,switch3,switch4,switch5,switch6,switch7,switch8,lockGateway
                            ,lockName,powerStatus,curtainStatus,doorStatus,doorWarning,temp,tempSetPoint,setPointInterval,checkInModeTime,checkOutModeTime,welcomeMessage,logo,token);
                    AllRooms.add(room);
                }
                callback.onSuccess(AllRooms);
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
        }, error -> callback.onFail(error.toString()));
        Q.add(roomsReq);
    }
}

