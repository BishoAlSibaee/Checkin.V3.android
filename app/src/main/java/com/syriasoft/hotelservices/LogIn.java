package com.syriasoft.hotelservices;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.datatransport.BuildConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.reflect.TypeToken;
import com.syriasoft.hotelservices.Interface.RequestCallback;
import com.syriasoft.hotelservices.lock.AccountInfo;
import com.syriasoft.hotelservices.lock.ApiService;
import com.syriasoft.hotelservices.lock.LockObj;
import com.syriasoft.hotelservices.lock.RetrofitAPIManager;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class LogIn extends AppCompatActivity {
    private EditText password ;
    Activity act ;
    private final String projectLoginUrl = "users/loginProject" ;
    private FirebaseDatabase database ;
    Spinner PROJECTS_SPINNER, buildings , floors , types , rooms;
    static List<BUILDING> Buildings ;
    static List<FLOOR> Floors ;
    static List<ROOM_TYPE> Types ;
    List<FLOOR> BuildingFloors ;
    static List<ROOM> Rooms ;
    List<ROOM> FloorRooms ;
    public static ROOM THE_ROOM;
    String[] buildingsNames , floorsNames ,roomNums , typeNames ;
    static public DatabaseReference  Hotel,Floor , Room ;
    LinearLayout Login , LoginImage , projectsLayout ;
    private String pass = "Freesyria579251";
    public static AccountInfo acc ;
    public AccountInfo accountInfo;
    private final int pageNo = 1;
    private final int pageSize = 100;
    ArrayList<LockObj> lockObs;
    public static LockObj myLock ;
    List<HomeBean> Homes;
    public static HomeBean selectedHome ;
    public static List<Activity> ActList ;
    TextView version, CurrentOperation ;
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    String[] Names ;
    PROJECT THE_PROJECT ;
    List<PROJECT> Projects ;
    AVLoadingIndicatorView loading ;
    String ProjectName , ProjectURL ,TuyaUser,TuyaPassword,LockUser,LockPassword ;
    WindowInsetsControllerCompat windowInsetsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        act = this ;
        setActivity();
        ActList.add(act);
        if (ActList.size() >1 ) {
            for (int i=0;i<ActList.size();i++) {
                ActList.get(i).finish();
            }
        }
        ProjectName = pref.getString("projectName", null);
        MyApp.ProjectName = ProjectName ;
        ProjectURL = pref.getString("url", null);
        MyApp.ProjectURL = ProjectURL ;
        TuyaUser = pref.getString("tuyaUser", null);
        MyApp.TuyaUser = TuyaUser ;
        TuyaPassword = pref.getString("tuyaPassword", null);
        MyApp.TuyaPassword = TuyaPassword ;
        LockUser = pref.getString("lockUser", null);
        MyApp.LockUser = LockUser ;
        LockPassword = pref.getString("lockPassword", null);
        MyApp.LockPassword = LockPassword ;
        if (ProjectName == null) {
            loading.setVisibility(View.VISIBLE);
            CurrentOperation.setText(getResources().getString(R.string.gettingProjects));
            getProjects(new loginCallback() {
                @Override
                public void onSuccess() {
                    loading.setVisibility(View.INVISIBLE);
                    CurrentOperation.setText("");
                }

                @Override
                public void onFailed() {
                    loading.setVisibility(View.INVISIBLE);
                    new messageDialog("error getting projects.. check internet connection" , "error",act);
                    reRunActivity();
                }
            });
        }
        else {
            loading.setVisibility(View.VISIBLE);
            projectsLayout.setVisibility(View.GONE);
            CurrentOperation.setText(getResources().getString(R.string.gettingProjectVariables));
            getProjectVariables(new loginCallback() {
                @Override
                public void onSuccess() {
                    Log.d("bootUp","variables done");
                    buildings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("buildingSelect" , "selected "+position + " "+Floors.size()+" " +Rooms.size());
                            BuildingFloors.clear();
                            MyApp.Building = Buildings.get(position) ;
                            for (int i=0;i<Floors.size();i++) {
                                if (Floors.get(i).buildingId == MyApp.Building.id) {
                                    BuildingFloors.add(Floors.get(i));
                                }
                            }
                            floorsNames = new String[BuildingFloors.size()];
                            for (int i = 0 ; i < BuildingFloors.size(); i++) {
                                floorsNames[i] = String.valueOf(BuildingFloors.get(i).floorNumber);
                            }
                            ArrayAdapter<String> floorsadapter = new ArrayAdapter<>(act, R.layout.spinners_item, floorsNames);
                            floors.setAdapter(floorsadapter);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    floors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("floorSelected" , position+" "+BuildingFloors.get(position).floorNumber);
                            FloorRooms.clear();
                            MyApp.Floor = BuildingFloors.get(position);
                            for (int i=0;i<Rooms.size();i++) {
                                if (Rooms.get(i).floor_id == MyApp.Floor.id) {
                                    FloorRooms.add(Rooms.get(i));
                                }
                            }
                            roomNums = new String[FloorRooms.size()];
                            for (int i=0;i<FloorRooms.size();i++) {
                                roomNums[i] = String.valueOf(FloorRooms.get(i).RoomNumber);
                            }
                            Log.d("floorSelected" , FloorRooms.size()+" "+Rooms.size());
                            ArrayAdapter<String> roomsAdapter = new ArrayAdapter<>(act,R.layout.spinners_item,roomNums);
                            rooms.setAdapter(roomsAdapter);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            THE_ROOM = Rooms.get(position);
                            MyApp.Room = Rooms.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    CurrentOperation.setText(getResources().getString(R.string.gettingBuildings));
                    getBuildings(new RequestCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("bootUp","buildings done");
                            CurrentOperation.setText(getResources().getString(R.string.gettingFloors));
                            getFloors(new RequestCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("bootUp","floors done");
                                    CurrentOperation.setText(getResources().getString(R.string.gettingRoomTypes));
                                    getRoomTypes(new RequestCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("bootUp","types done");
                                            CurrentOperation.setText(getResources().getString(R.string.gettingRooms));
                                            getRooms(new RequestCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("bootUp","rooms done");
                                                    CurrentOperation.setText("");
                                                    if (pref.getString("RoomNumber", null) == null) {
                                                        Login.setVisibility(View.VISIBLE);
                                                        loading.setVisibility(View.INVISIBLE);
                                                        ArrayAdapter<String> buildingsadapter = new ArrayAdapter<>(act, R.layout.spinners_item, buildingsNames);
                                                        buildings.setAdapter(buildingsadapter);
                                                    }
                                                    else {
                                                        int RoomNumber = Integer.parseInt(pref.getString("RoomNumber", null));
                                                        Log.d("savedRoom" , RoomNumber+"");
                                                        for (int i=0;i<Rooms.size();i++) {
                                                            if (RoomNumber == Rooms.get(i).RoomNumber) {
                                                                MyApp.Room = Rooms.get(i) ;
                                                                break;
                                                            }
                                                        }
                                                        Log.d("bootUp","room is "+MyApp.Room.RoomNumber);
                                                        CurrentOperation.setText(getResources().getString(R.string.gettingRoomDevices));
                                                        onlyLogInToTuya(new RequestCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Log.d("bootUp","tuya login done");
                                                                getMyDevices(new RequestCallback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("bootUp","devices done");
                                                                        startActivity(new Intent(act,FullscreenActivity.class));
                                                                        act.finish();
                                                                    }

                                                                    @Override
                                                                    public void onFail(String error) {
                                                                        new messageDialog("error getting devices"+ error , "error",act);
                                                                        reRunActivity();
                                                                    }
                                                                });

//                                                                getTheLock(new RequestCallback() {
//                                                                    @Override
//                                                                    public void onSuccess() {
//                                                                        startActivity(new Intent(act,FullscreenActivity.class));
//                                                                        act.finish();
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onFail(String error) {
//                                                                        reRunActivity();
//                                                                    }
//                                                                });
                                                            }
                                                            @Override
                                                            public void onFail(String error) {
                                                                new messageDialog("error getting devices"+ error , "error",act);
                                                                reRunActivity();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFail(String error) {
                                                    loading.setVisibility(View.INVISIBLE);
                                                    new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                                    reRunActivity();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFail(String error) {
                                            loading.setVisibility(View.INVISIBLE);
                                            new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                            reRunActivity();
                                        }
                                    });
                                }
                                @Override
                                public void onFail(String error) {
                                    loading.setVisibility(View.INVISIBLE);
                                    new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                    reRunActivity();
                                }
                            });
                        }
                        @Override
                        public void onFail(String error) {
                            loading.setVisibility(View.INVISIBLE);
                            new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                            reRunActivity();
                        }
                    });
                }
                @Override
                public void onFailed() {
                    loading.setVisibility(View.INVISIBLE);
                    new messageDialog("error getting data.. check internet connection" , "error",act);
                    reRunActivity();
                }
            });
        }
    }

    void reRunActivity() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(act,LogIn.class);
                startActivity(i);
                t.cancel();
                act.finish();
            }
        },1000*10,1000*60*60);
    }

    void getBuildings(RequestCallback callback){
        String buildingsUrl = "roomsManagement/getbuildings";
        StringRequest buildingsReq = new StringRequest(Request.Method.GET, ProjectURL+ buildingsUrl, response -> {
            Log.e("buildings" , response);
            try {
                JSONArray arr = new JSONArray(response);
                buildingsNames = new String[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    buildingsNames[i] = row.getString("buildingName");
                    int id = row.getInt("id");
                    String BuildingName = row.getString("buildingName");
                    int bNo = row.getInt("buildingNo") ;
                    int Floors = row.getInt("floorsNumber");
                    int project = row.getInt("projectId");
                    Buildings.add(new BUILDING(id,project,bNo,BuildingName,Floors));
                }
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
            MyApp.Buildings = Buildings ;
            callback.onSuccess();

        }, error -> callback.onFail(error.toString()));
        Volley.newRequestQueue(act).add(buildingsReq);
    }

    void getFloors(RequestCallback callback) {
        String floorsUrl = "roomsManagement/getfloors";
        StringRequest floorsReq = new StringRequest(Request.Method.GET, ProjectURL+ floorsUrl, response -> {
            Log.e("floors" , response);
            try{
                JSONArray arr = new JSONArray(response);
                for (int i = 0 ; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    int id = row.getInt("id");
                    int bId = row.getInt("building_id");
                    int fNum = row.getInt("floorNumber");
                    int rooms = row.getInt("rooms");
                    Floors.add(new FLOOR(id,bId,fNum ,rooms));
                }
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
            MyApp.Floors = Floors ;
            callback.onSuccess();
        }, error -> callback.onFail(error.toString()));
        Volley.newRequestQueue(act).add(floorsReq);
    }

    void getRoomTypes(RequestCallback callback) {
        String typesUrl = "roomsManagement/getRoomTypes";
        StringRequest typesReq = new StringRequest(Request.Method.GET, ProjectURL+ typesUrl, response -> {
            Log.e("types" , response);
            try{
                JSONArray arr = new JSONArray(response);
                for (int i = 0 ; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    int id = row.getInt("id");
                    String type = row.getString("type");
                    Types.add(new ROOM_TYPE(id,type));
                }
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
            MyApp.Types = Types ;
            typeNames = new String[Types.size()];
            for (int i=0;i<Types.size();i++) {
                typeNames[i] = Types.get(i).type;
            }
            ArrayAdapter<String> ad = new ArrayAdapter<>(act,R.layout.spinners_item,typeNames);
            types.setAdapter(ad);
            callback.onSuccess();
        }, error -> callback.onFail(error.toString()));
        Volley.newRequestQueue(act).add(typesReq);
    }

    void getRooms(RequestCallback callback) {
        String roomsUrl = "roomsManagement/getRooms";
        StringRequest roomsReq = new StringRequest(Request.Method.GET, ProjectURL+ roomsUrl, response -> {
            Log.e("roomRes" , response);
            try {
                JSONArray arr = new JSONArray(response);
                roomNums = new String[arr.length()];
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
                    room.setFireRoom(database.getReference(ProjectName+"/B"+room.Building+"/F"+room.Floor+"/R"+room.RoomNumber));
                    Rooms.add(room);
                }
                MyApp.Rooms = Rooms ;
                callback.onSuccess();
            } catch (JSONException e) {
                callback.onFail(e.toString());
            }
        }, error -> callback.onFail(error.toString()));
        Volley.newRequestQueue(act).add(roomsReq);
    }

    void setActivity() {
        Buildings = new ArrayList<>();
        Floors = new ArrayList<>();
        Rooms = new ArrayList<>();
        BuildingFloors = new ArrayList<>();
        FloorRooms = new ArrayList<>();
        Types = new ArrayList<>();
        loading = findViewById(R.id.loadingIcon);
        projectsLayout = findViewById(R.id.projectsLayout);
        PROJECTS_SPINNER = findViewById(R.id.spinner_projects);
        types = findViewById(R.id.types_spinner);
        password = findViewById(R.id.passwordEntry);
        buildings = findViewById(R.id.spinner_buildings);
        floors  = findViewById(R.id.spinner_floors);
        rooms = findViewById(R.id.spinner_rooms);
        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
        Login = findViewById(R.id.Login_Layout);
        LoginImage = findViewById(R.id.LoginImage);
        version = findViewById(R.id.textView64);
        CurrentOperation = findViewById(R.id.operations);
        ActList = new ArrayList<>();
        Projects = new ArrayList<>();
        version.setText(String.format("Welcome To Checkin Version%s", BuildConfig.VERSION_NAME));
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
        KeepScreenFull();
    }

    public void goRegister(View view) {
        loading.setVisibility(View.VISIBLE);
        CurrentOperation.setText(getResources().getString(R.string.loggingInProject));
        if (THE_PROJECT != null ) {
            getProjectVariables(new loginCallback() {
                @Override
                public void onSuccess() {
                    final String pass = password.getText().toString();
                    StringRequest re = new StringRequest(Request.Method.POST, THE_PROJECT.url + projectLoginUrl, response -> {
                        CurrentOperation.setText("");
                        if (response != null) {
                            try {
                                JSONObject resp = new JSONObject(response);
                                if (resp.getString("result").equals("success")) {
                                    Toast.makeText(act,"Login Success",Toast.LENGTH_LONG).show();
                                    editor.putString("projectName" , THE_PROJECT.projectName);
                                    ProjectName = THE_PROJECT.projectName ; MyApp.ProjectName = THE_PROJECT.projectName;
                                    editor.putString("tuyaUser" , THE_PROJECT.TuyaUser);
                                    TuyaUser = THE_PROJECT.TuyaUser ; MyApp.TuyaUser = THE_PROJECT.TuyaUser ;
                                    editor.putString("tuyaPassword" , THE_PROJECT.TuyaPassword);
                                    TuyaPassword = THE_PROJECT.TuyaPassword ; MyApp.TuyaPassword = THE_PROJECT.TuyaPassword ;
                                    editor.putString("lockUser" , THE_PROJECT.LockUser);
                                    LockUser = THE_PROJECT.LockUser ; MyApp.LockUser = THE_PROJECT.LockUser ;
                                    editor.putString("lockPassword" , THE_PROJECT.LockPassword);
                                    LockPassword = THE_PROJECT.LockPassword ; MyApp.LockPassword = THE_PROJECT.LockPassword ;
                                    editor.putString("url" , THE_PROJECT.url);
                                    ProjectURL = THE_PROJECT.url ; MyApp.ProjectURL = THE_PROJECT.url ;
                                    editor.apply();
                                    projectsLayout.setVisibility(View.GONE);
                                    if (pref.getString("RoomNumber", null) == null) {
                                        Login.setVisibility(View.VISIBLE);
                                        buildings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                                                Log.d("buildingSelect" , "selected "+position + " "+Floors.size()+" " +Rooms.size());
                                                BuildingFloors.clear();
                                                MyApp.Building = Buildings.get(position) ;
                                                for (int i=0;i<Floors.size();i++) {
                                                    if (Floors.get(i).buildingId == MyApp.Building.id) {
                                                        BuildingFloors.add(Floors.get(i));
                                                    }
                                                }
                                                floorsNames = new String[BuildingFloors.size()];
                                                for (int i = 0 ; i < BuildingFloors.size(); i++) {
                                                    floorsNames[i] = String.valueOf(BuildingFloors.get(i).floorNumber);
                                                }
                                                ArrayAdapter<String> floorsAdapter = new ArrayAdapter<>(act, R.layout.spinners_item, floorsNames);
                                                floors.setAdapter(floorsAdapter);
                                            }
                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                        floors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                                                Log.d("floorSelected" , position+" "+BuildingFloors.get(position).floorNumber);
                                                FloorRooms.clear();
                                                MyApp.Floor = BuildingFloors.get(position);
                                                for (int i=0;i<Rooms.size();i++) {
                                                    if (Rooms.get(i).floor_id == MyApp.Floor.id) {
                                                        FloorRooms.add(Rooms.get(i));
                                                        Log.d("floorSelected" , Rooms.get(i).RoomNumber+"");
                                                    }
                                                }
                                                roomNums = new String[FloorRooms.size()];
                                                for (int i=0;i<FloorRooms.size();i++) {
                                                    roomNums[i] = String.valueOf(FloorRooms.get(i).RoomNumber);
                                                }
                                                Log.d("floorSelected" , FloorRooms.size()+" "+Rooms.size());
                                                ArrayAdapter<String> roomsAdapter = new ArrayAdapter<>(act,R.layout.spinners_item,roomNums);
                                                rooms.setAdapter(roomsAdapter);
                                            }
                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                        rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                                                THE_ROOM = FloorRooms.get(position);
                                                MyApp.Room = FloorRooms.get(position);
                                                Log.d("floorSelected" , " "+THE_ROOM.RoomNumber);
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                        CurrentOperation.setText(getResources().getString(R.string.gettingBuildings));
                                        getBuildings(new RequestCallback() {
                                            @Override
                                            public void onSuccess() {
                                                CurrentOperation.setText(getResources().getString(R.string.gettingFloors));
                                                getFloors(new RequestCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        CurrentOperation.setText(getResources().getString(R.string.gettingRoomTypes));
                                                        getRoomTypes(new RequestCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                CurrentOperation.setText(getResources().getString(R.string.gettingRooms));
                                                                getRooms(new RequestCallback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        CurrentOperation.setText("");
                                                                        loading.setVisibility(View.INVISIBLE);
                                                                        ArrayAdapter<String> buildingsadapter = new ArrayAdapter<>(act, R.layout.spinners_item, buildingsNames);
                                                                        buildings.setAdapter(buildingsadapter);
                                                                    }

                                                                    @Override
                                                                    public void onFail(String error) {
                                                                        loading.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                                                    }
                                                                });
                                                            }
                                                            @Override
                                                            public void onFail(String error) {
                                                                loading.setVisibility(View.INVISIBLE);
                                                                new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                                            }
                                                        });
                                                    }
                                                    @Override
                                                    public void onFail(String error) {
                                                        loading.setVisibility(View.INVISIBLE);
                                                        new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                                    }
                                                });
                                            }
                                            @Override
                                            public void onFail(String error) {
                                                loading.setVisibility(View.INVISIBLE);
                                                new messageDialog("error getting data.. check internet connection "+ error , "error",act);
                                            }
                                        });
                                    }
                                    else {
                                        int RoomNumber = Integer.parseInt(pref.getString("RoomNumber", null));
                                        Log.d("continueResp",RoomNumber+"");
                                        for (int i=0;i<Rooms.size();i++) {
                                            if (RoomNumber == Rooms.get(i).RoomNumber) {
                                                MyApp.Room = Rooms.get(i) ;
                                                break;
                                            }
                                        }
                                        CurrentOperation.setText(MessageFormat.format("getting room {0} devices", MyApp.Room.RoomNumber));
                                        onlyLogInToTuya(new RequestCallback() {
                                            @Override
                                            public void onSuccess() {
                                                CurrentOperation.setText("");
                                                getMyDevices(new RequestCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        startActivity(new Intent(act,FullscreenActivity.class));
                                                        act.finish();
                                                    }

                                                    @Override
                                                    public void onFail(String error) {
                                                        loading.setVisibility(View.INVISIBLE);
                                                        new messageDialog("error getting devices"+ error , "error",act);
                                                    }
                                                });

                                            }
                                            @Override
                                            public void onFail(String error) {
                                                loading.setVisibility(View.INVISIBLE);
                                                new messageDialog("error getting devices"+ error , "error",act);
                                            }
                                        });
                                    }
                                }
                                else {
                                    loading.setVisibility(View.INVISIBLE);
                                    Toast.makeText(act,"Login Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                loading.setVisibility(View.INVISIBLE);
                                Toast.makeText(act,"Login Failed " + e,Toast.LENGTH_LONG).show();
                            }
                        }
                    }, error -> {
                        loading.setVisibility(View.INVISIBLE);
                        new messageDialog(error.toString(),"Failed",act);
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String,String> par = new HashMap<>();
                            par.put( "password" , pass ) ;
                            par.put( "project_name" , THE_PROJECT.projectName ) ;
                            return par;
                        }
                    };
                    Volley.newRequestQueue(act).add(re);
                }

                @Override
                public void onFailed() {
                    new messageDialog("failed to get project variables","failed",act);
                }
            });
        }
        else {
            new messageDialog("please select project","project",act);
        }
    }

    private void getTheLock(RequestCallback callback) {
        Log.d("loginLock" , "start");
        if (LockUser.equals("no")) {
            Log.d("loginLock" , "project has no bluetooth locks");
            callback.onSuccess();
        }
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        pass = DigitUtil.getMD5(LockPassword);
        Log.d("loginLock" , LockUser+" "+pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password",LockUser, pass, ApiService.REDIRECT_URI);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                Log.d("loginLock" , response.toString());
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null) {
                    if (accountInfo.errcode == 0) {
                        accountInfo.setMd5Pwd(pass);
                        acc = accountInfo;
                        Call<String> call1 = apiService.getLockList(ApiService.CLIENT_ID, acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                                Log.d("loginLock" , response.toString());
                                String json = response.body();
                                if (json != null) {
                                    if (json.contains("list")) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(json);
                                            JSONArray array = jsonObject.getJSONArray("list");
                                            lockObs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                                            Log.d("loginLock" , lockObs.size()+"" );
                                            for (int i = 0; i< lockObs.size(); i++) {
                                                Log.d("loginLock" , lockObs.get(i).getLockName());
                                                if (lockObs.get(i).getLockName().equals(MyApp.Room.RoomNumber+"Lock")) {
                                                    MyApp.BluetoothLock = lockObs.get(i);
                                                    MyApp.Room.setLock(lockObs.get(i));
                                                }
                                            }
                                            callback.onSuccess();
                                        }
                                        catch (JSONException e) {
                                            Log.d("loginLock" , e.getMessage());
                                            callback.onFail("Lock list Failed "+e.getMessage());
                                        }
                                    }
                                    else {
                                        Log.d("loginLock" , "Lock list Failed ");
                                        callback.onFail("Lock list Failed ");
                                    }
                                }
                                else {
                                    Log.d("loginLock" , "json null ");
                                    callback.onFail("json null ");
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.d("loginLock" , response.toString());
                                callback.onFail("Lock list Failed "+t.getMessage());
                            }
                        });

                    } else {
                        Log.d("loginLock" , response.toString());
                        callback.onFail("Lock Login Failed ");
                    }
                } else {
                    Log.d("loginLock" , response.toString());
                    callback.onFail("Lock Login Failed ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                new messageDialog("Lock Login Failed "+t.getMessage(),"Failed",act);
            }
        });
    }

    private void onlyLogInToTuya(RequestCallback callback) {
        Log.d("tuyaLogin","start");
        TuyaHomeSdk.getUserInstance().loginWithEmail("966",TuyaUser,TuyaPassword, new ILoginCallback() {
            @Override
            public void onSuccess (User user) {
                Log.d("tuyaLogin","success ");
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error) {
                        callback.onFail("Tuya Login Failed " + error+" "+errorCode);
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans) {
                        // do something
                        for (HomeBean h : homeBeans) {
                            Log.d("tuyaLogin",h.getName());
                        }
                        for (int i = 0; i< homeBeans.size(); i++) {
                            if (MyApp.ProjectName.equals("apiTest")) {
                                if (homeBeans.get(i).getName().contains("Test")) {
                                    MyApp.Homes.add(homeBeans.get(i));
                                }
                            }
                            else if (homeBeans.get(i).getName().contains(MyApp.ProjectName)) {
                                MyApp.Homes.add(homeBeans.get(i));
                            }
                        }
                        if (MyApp.Homes.size() == 0) {
                            callback.onFail("Tuya home not found");
                        }
                        else {
                            Log.d("tuyaLogin",MyApp.Homes.size()+"");
                            for (HomeBean h : MyApp.Homes) {
                                Log.d("tuyaLogin",h.getName());
                            }
                            callback.onSuccess();
                        }
                    }
                });
            }
            @Override
            public void onError (String code, String error) {
                Log.d("tuyaLogin","failed "+error+" "+code);
                callback.onFail(error +" "+code);
            }
        });
    }

    private void getProjects(loginCallback callback) {
        String projectsUrl = "https://www.ratco-solutions.com/Checkin/getProjects.php";
        StringRequest re = new StringRequest(Request.Method.POST, projectsUrl, response -> {
            Log.d("getProjectsResp" , response);
            if (response != null ) {
                try {
                    JSONArray arr = new JSONArray(response);
                    Names = new String[arr.length()];
                    for(int i=0;i<arr.length();i++) {
                        JSONObject row = arr.getJSONObject(i);
                        Projects.add(new PROJECT(row.getInt("id"),row.getString("projectName"),row.getString("city"),row.getString("salesman"),row.getString("TuyaUser"),row.getString("TuyaPassword"),row.getString("LockUser"),row.getString("LockPassword"),row.getString("url")));
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
                        THE_PROJECT = Projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
                        MyApp.THE_PROJECT = Projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
                        ProjectURL = THE_PROJECT.url ;
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
        Volley.newRequestQueue(act).add(re);
    }

    private void getProjectVariables(loginCallback callback) {
        String url = ProjectURL + "roomsManagement/getProjectVariables";
        StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("getProjectVariables" , response);
            try {
                JSONObject row = new JSONObject(response);
                JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                MyApp.ProjectVariables = new PROJECT_VARIABLES(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                callback.onSuccess();
            }
            catch (JSONException e) {
                new messageDialog("error getting project variables "+ e,"error",act);
                callback.onFailed();
            }
        }, error -> {
            new messageDialog("error getting project variables "+error.toString(),"error",act);
            callback.onFailed();
        });
        Volley.newRequestQueue(act).add(re);
    }

    public void Continue(View view) {
        loading.setVisibility(View.VISIBLE);
        CurrentOperation.setText(getResources().getString(R.string.gettingRoomDevices));
        onlyLogInToTuya(new RequestCallback() {
            @Override
            public void onSuccess() {
                getTheLock(new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        CurrentOperation.setText(MessageFormat.format("logging in room {0}", MyApp.Room.RoomNumber));
                        String url = ProjectURL + "roomsManagement/loginRoom";
                        StringRequest re = new StringRequest(Request.Method.POST, url, response -> {
                            Log.d("continueResp",response);
                            if (response != null) {
                                try {
                                    JSONObject resp = new JSONObject(response);
                                    if (resp.getString("result").equals("success")) {
                                        Toast.makeText(act,"Room Register Success",Toast.LENGTH_LONG).show();
                                        editor.putString("RoomNumber" , String.valueOf(FloorRooms.get(rooms.getSelectedItemPosition()).RoomNumber));
                                        editor.putString("RoomID" , String.valueOf(FloorRooms.get(rooms.getSelectedItemPosition()).id));
                                        editor.apply();
                                        startActivity(new Intent(act,FullscreenActivity.class));
                                    }
                                    else {
                                        loading.setVisibility(View.INVISIBLE);
                                        new messageDialog("Login Failed " + resp.getString("error"),"Failed",act);
                                    }
                                } catch (JSONException e) {
                                    Log.d("continueResp",e.getMessage());
                                    loading.setVisibility(View.INVISIBLE);
                                    new messageDialog("Login Failed " + e.getMessage(),"Failed",act);
                                }
                            }
                        }, error -> {
                            Log.d("continueResp",error.toString());
                            loading.setVisibility(View.INVISIBLE);
                            new messageDialog(error.toString(),"Failed",act);
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String,String> par = new HashMap<>();
                                par.put( "room_id" , String.valueOf(MyApp.Room.id)) ;
                                return par;
                            }
                        };
                        Volley.newRequestQueue(act).add(re);
                    }
                    @Override
                    public void onFail(String error) {
                        Log.d("continueResp",error);
                        loading.setVisibility(View.INVISIBLE);
                        new messageDialog(error,"Failed",act);
                    }
                });
            }
            @Override
            public void onFail(String error) {
                Log.d("continueResp",error);
                loading.setVisibility(View.INVISIBLE);
                new messageDialog(error,"Failed",act);
            }
        });
    }

    public void getMyDevices(RequestCallback callback) {
//        TuyaHomeSdk.newHomeInstance(MyApp.HOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
//            @Override
//            public void onSuccess(HomeBean homeBean) {
//                List<DeviceBean> TheDevicesList = homeBean.getDeviceList();
//                if (TheDevicesList.size() == 0) {
//                    ToastMaker.MakeToast("no devices" , act );
//
//                }
//                else {
//                    Log.d("tuyaLogin"," devices "+TheDevicesList.size());
//                    for (int i=0;i<TheDevicesList.size();i++) {
//                        if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Power")) {
//                            MyApp.Room.setPOWER_B(TheDevicesList.get(i));
//                            MyApp.Room.setPOWER(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getPOWER_B().devId));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ZGatway")) {
//                            MyApp.Room.setGATEWAY_B(TheDevicesList.get(i));
//                            MyApp.Room.setGATEWAY(TuyaHomeSdk.newGatewayInstance(MyApp.Room.getGATEWAY_B().devId));
//                        }
//                        else if(TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"AC")) {
//                            MyApp.Room.setAC_B(TheDevicesList.get(i));
//                            MyApp.Room.setAC(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getAC_B().devId));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor")) {
//                            MyApp.Room.setDOORSENSOR_B(TheDevicesList.get(i));
//                            MyApp.Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getDOORSENSOR_B().devId));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor")) {
//                            MyApp.Room.setMOTIONSENSOR_B(TheDevicesList.get(i));
//                            MyApp.Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getMOTIONSENSOR_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain")) {
//                            MyApp.Room.setCURTAIN_B(TheDevicesList.get(i));
//                            MyApp.Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getCURTAIN_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch")) {
//                            MyApp.Room.setSERVICE1_B(TheDevicesList.get(i));
//                            MyApp.Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSERVICE1_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1")) {
//                            MyApp.Room.setSWITCH1_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH1_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2")) {
//                            MyApp.Room.setSWITCH2_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH2_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3")) {
//                            MyApp.Room.setSWITCH3_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH3_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4")) {
//                            MyApp.Room.setSWITCH4_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH4_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch5")) {
//                            MyApp.Room.setSWITCH5_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH5_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch6")) {
//                            MyApp.Room.setSWITCH6_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH6_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch7")) {
//                            MyApp.Room.setSWITCH7_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH7_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch8")) {
//                            MyApp.Room.setSWITCH8_B(TheDevicesList.get(i));
//                            MyApp.Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH8_B().getDevId()));
//                        }
//                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Lock")) {
//                            MyApp.Room.setLOCK_B(TheDevicesList.get(i));
//                            MyApp.Room.setLOCK(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getLOCK_B().getDevId()));
//                        }
//                    }
//                    //Log.d("tuyaLogin"," devices "+MyApp.Room.getSERVICE1_B().getName());
//                    callback.onSuccess();
//                }
//            }
//
//            @Override
//            public void onError(String errorCode, String errorMsg) {
//                callback.onFail(errorCode+" "+errorMsg);
//            }
//        });
//        final int[] x = {0};
//        for (int i=0;i<MyApp.Homes.size();i++) {
//            HomeBean h = MyApp.Homes.get(i);
//            Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
//                        @Override
//                        public void onSuccess(HomeBean bean) {
//                            x[0]++;
//                            List<DeviceBean> TheDevicesList = bean.getDeviceList();
//                            for (int i=0;i<TheDevicesList.size();i++) {
//                                if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Power")) {
//                                    MyApp.Room.setPOWER_B(TheDevicesList.get(i));
//                                    MyApp.Room.setPOWER(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getPOWER_B().devId));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ZGatway")) {
//                                    MyApp.Room.setGATEWAY_B(TheDevicesList.get(i));
//                                    MyApp.Room.setGATEWAY(TuyaHomeSdk.newGatewayInstance(MyApp.Room.getGATEWAY_B().devId));
//                                }
//                                else if(TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"AC")) {
//                                    MyApp.Room.setAC_B(TheDevicesList.get(i));
//                                    MyApp.Room.setAC(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getAC_B().devId));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor")) {
//                                    MyApp.Room.setDOORSENSOR_B(TheDevicesList.get(i));
//                                    MyApp.Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getDOORSENSOR_B().devId));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor")) {
//                                    MyApp.Room.setMOTIONSENSOR_B(TheDevicesList.get(i));
//                                    MyApp.Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getMOTIONSENSOR_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain")) {
//                                    MyApp.Room.setCURTAIN_B(TheDevicesList.get(i));
//                                    MyApp.Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getCURTAIN_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch")) {
//                                    MyApp.Room.setSERVICE1_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSERVICE1_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1")) {
//                                    MyApp.Room.setSWITCH1_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH1_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2")) {
//                                    MyApp.Room.setSWITCH2_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH2_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3")) {
//                                    MyApp.Room.setSWITCH3_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH3_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4")) {
//                                    MyApp.Room.setSWITCH4_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH4_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch5")) {
//                                    MyApp.Room.setSWITCH5_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH5_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch6")) {
//                                    MyApp.Room.setSWITCH6_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH6_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch7")) {
//                                    MyApp.Room.setSWITCH7_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH7_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch8")) {
//                                    MyApp.Room.setSWITCH8_B(TheDevicesList.get(i));
//                                    MyApp.Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH8_B().getDevId()));
//                                }
//                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Lock")) {
//                                    MyApp.Room.setLOCK_B(TheDevicesList.get(i));
//                                    MyApp.Room.setLOCK(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getLOCK_B().getDevId()));
//                                }
//                            }
//                            if (THE_ROOM.getGATEWAY_B() != null) {
//                                MyApp.HOME = h;
//                            }
//                            if (x[0] == MyApp.Homes.size()) {
//                                callback.onSuccess();
//                            }
//                        }
//
//                        @Override
//                        public void onError(String errorCode, String errorMsg) {
//                            callback.onFail(errorMsg);
//                        }
//                    });
//                }
//            },i* 1000L);
//        }
        Log.d("tuyaLogin","devices start "+MyApp.Homes.size());
        final int[] x = {0};
        for (int i=0; i< MyApp.Homes.size();i++) {
            HomeBean h = MyApp.Homes.get(i);
            Log.d("tuyaLogin",h.getName()+" "+i);
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean homeBean) {
                            x[0] = x[0]+1;
                            Log.d("tuyaLogin","devices done "+h.getName()+" "+x[0]);
                            List<DeviceBean> TheDevicesList = homeBean.getDeviceList();
                            for (int i=0;i<TheDevicesList.size();i++) {
                                if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Power")) {
                                    MyApp.Room.setPOWER_B(TheDevicesList.get(i));
                                    MyApp.Room.setPOWER(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getPOWER_B().devId));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ZGatway")) {
                                    MyApp.Room.setGATEWAY_B(TheDevicesList.get(i));
                                    MyApp.Room.setGATEWAY(TuyaHomeSdk.newGatewayInstance(MyApp.Room.getGATEWAY_B().devId));
                                }
                                else if(TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"AC")) {
                                    MyApp.Room.setAC_B(TheDevicesList.get(i));
                                    MyApp.Room.setAC(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getAC_B().devId));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor")) {
                                    MyApp.Room.setDOORSENSOR_B(TheDevicesList.get(i));
                                    MyApp.Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getDOORSENSOR_B().devId));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor")) {
                                    MyApp.Room.setMOTIONSENSOR_B(TheDevicesList.get(i));
                                    MyApp.Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getMOTIONSENSOR_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain")) {
                                    MyApp.Room.setCURTAIN_B(TheDevicesList.get(i));
                                    MyApp.Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getCURTAIN_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch")) {
                                    MyApp.Room.setSERVICE1_B(TheDevicesList.get(i));
                                    MyApp.Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSERVICE1_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1")) {
                                    MyApp.Room.setSWITCH1_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH1_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2")) {
                                    MyApp.Room.setSWITCH2_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH2_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3")) {
                                    MyApp.Room.setSWITCH3_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH3_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4")) {
                                    MyApp.Room.setSWITCH4_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH4_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch5")) {
                                    MyApp.Room.setSWITCH5_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH5(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH5_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch6")) {
                                    MyApp.Room.setSWITCH6_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH6(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH6_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch7")) {
                                    MyApp.Room.setSWITCH7_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH7(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH7_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch8")) {
                                    MyApp.Room.setSWITCH8_B(TheDevicesList.get(i));
                                    MyApp.Room.setSWITCH8(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH8_B().getDevId()));
                                }
                                else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Lock")) {
                                    MyApp.Room.setLOCK_B(TheDevicesList.get(i));
                                    MyApp.Room.setLOCK(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getLOCK_B().getDevId()));
                                }
                            }
                            Log.d("tuyaLogin","set devices finish");
                            if (MyApp.Room.getGATEWAY_B() != null) {
                                MyApp.HOME = h;
                            }
                            Log.d("tuyaLogin",x[0] +" "+MyApp.Homes.size());
                            if (x[0] == MyApp.Homes.size()) {
                                callback.onSuccess();
                            }
                        }
                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            Log.d("tuyaLogin","error "+errorMsg);
                            callback.onFail(errorCode+" "+errorMsg);
                        }
                    });
                }
            }, (long) i * 1000);
        }
    }

    private void KeepScreenFull() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,300);
                hideSystemUI();
            }
        }).start();
    }

    private void hideSystemUI() {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}

interface loginCallback {
    void onSuccess();
    void onFailed();
}