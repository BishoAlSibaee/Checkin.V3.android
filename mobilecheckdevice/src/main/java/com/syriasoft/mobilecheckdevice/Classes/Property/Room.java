package com.syriasoft.mobilecheckdevice.Classes.Property;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinAC;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinCurtain;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDoorSensor;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinGateway;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinLock;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinMotionSensor;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinPower;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinServiceSwitch;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinSwitch;
import com.syriasoft.mobilecheckdevice.Classes.Enumes.DeviceTypes;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.ACListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.CurtainListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.DoorListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GerRoomsCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetReservationType;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.LockListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.MotionListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.PowerListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.ServiceListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.SwitchListener;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT_VARIABLES;
import com.syriasoft.mobilecheckdevice.Classes.Tuya;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.syriasoft.mobilecheckdevice.MyApp;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Room extends Bed {
    String defaultImage = "https://images.tuyaeu.com/smart/rule/cover/starry.png";
    private static final String getRoomsUrl = "roomsManagement/getRooms";

    public int id ; // from server
    public int RoomNumber ; // from server
    public int Status ; // from server
    public int hotel ; // from server
    public int building_id ; // from server
    public int floor_id ; // from server
    public String RoomType ; // from server
    public int SuiteStatus ; // from server
    public int SuiteNumber ; // from server
    public int SuiteId ; // from server
    public int ReservationNumber ; // from server
    public int roomStatus ; // from server
    public int ClientIn ; // from firebase
    public String message ; // from firebase
    public int selected ; // from firebase
    public int loading ; // from firebase
    public int Tablet ; // from server
    public String dep ; // from firebase
    public long Cleanup ; // from firebase
    public long Laundry ; // from firebase
    public long RoomService ; // from firebase
    public String RoomServiceText ; // from firebase
    public long Checkout ; // from firebase
    public long Restaurant ; // from firebase
    public int Facility ; // from server
    public long SOS ; // from firebase
    public long DND ; // from firebase
    public int online ; // from firebase
    public int powerStatus ; // from firebase
    public int curtainStatus ; // from firebase
    public int doorStatus ; // from firebase
    static int addCleanupCounter = 1 , cancelOrderCounter = 1 , addLaundryCounter = 1 , addCheckoutCounter = 1;

    public HomeBean RoomHome;
    ValueEventListener roomStatusListener,clientInListener;

    public boolean byLink = false; // from firebase
    public String token ; // from server
    Building building;
    Floor floor;
    List<CheckinSwitch> switches;
    List<CheckinServiceSwitch> services;
    CheckinPower power;
    List<CheckinGateway> gateways;
    List<CheckinAC> acs;
    List<CheckinDoorSensor> doorSensors;
    List<CheckinCurtain> curtains;
    List<CheckinMotionSensor> motionSensors;
    List<CheckinLock> locks;
    public DatabaseReference fireRoom,devicesControlReference,devicesDataReference;
    Timer acScenarioTimer,doorWarningTimer;
    boolean acScenarioStarted = false;
    boolean firstDoorOpen = false;
    boolean powerScene = false;
    Timer electricTimer;
    boolean somebody = false;

    public Room(int id, int roomNumber, int status, int hotel, int building_id, int floor_id, String roomType, int suiteStatus, int suiteNumber, int suiteId, int reservationNumber, int roomStatus, int clientIn, String message, int selected, int loading, int tablet, String dep, int cleanup, int laundry, int roomService, String roomServiceText, int checkout, int restaurant, int miniBarCheck, int facility, int SOS, int DND, int online, String lockGateway, String lockName, int powerStatus, int curtainStatus, int doorStatus, String token) {
        this.id = id;
        RoomNumber = roomNumber;
        Status = status;
        this.hotel = hotel;
        this.building_id = building_id;
        this.floor_id = floor_id;
        RoomType = roomType;
        SuiteStatus = suiteStatus;
        SuiteNumber = suiteNumber;
        SuiteId = suiteId;
        ReservationNumber = reservationNumber;
        this.roomStatus = roomStatus;
        ClientIn = clientIn;
        this.message = message;
        this.selected = selected;
        this.loading = loading;
        Tablet = tablet;
        this.dep = dep;
        Cleanup = cleanup;
        Laundry = laundry;
        RoomService = roomService;
        RoomServiceText = roomServiceText;
        Checkout = checkout;
        Restaurant = restaurant;
        Facility = facility;
        this.SOS = SOS;
        this.DND = DND;
        this.online = online;
        this.powerStatus = powerStatus;
        this.curtainStatus = curtainStatus;
        this.doorStatus = doorStatus;
        this.token = token;
    }

    public Room(int id, int roomNumber, int status, int hotel, int building_id, int floor_id, String roomType, int suiteStatus, int suiteNumber, int suiteId, int reservationNumber, int roomStatus, int tablet, int facility) {
        this.id = id;
        RoomNumber = roomNumber;
        Status = status;
        this.hotel = hotel;
        this.building_id = building_id;
        this.floor_id = floor_id;
        RoomType = roomType;
        SuiteStatus = suiteStatus;
        SuiteNumber = suiteNumber;
        SuiteId = suiteId;
        ReservationNumber = reservationNumber;
        this.roomStatus = roomStatus;
        Tablet = tablet;
        Facility = facility;
    }

    public Room(JSONObject jsonRoom) throws JSONException {
        this.id = jsonRoom.getInt("id");
        this.building_id = jsonRoom.getInt("building_id");
        this.floor_id = jsonRoom.getInt("floor_id");
        this.hotel = jsonRoom.getInt("hotel");
        this.ReservationNumber = jsonRoom.getInt("ReservationNumber");
        this.Restaurant = jsonRoom.getInt("Restaurant");
        this.RoomNumber = jsonRoom.getInt("RoomNumber");
        this.RoomType = jsonRoom.getString("RoomType");
        this.Status = jsonRoom.getInt("Status");
        this.SuiteId = jsonRoom.getInt("SuiteId");
        this.SuiteNumber = jsonRoom.getInt("SuiteNumber");
        this.SuiteStatus = jsonRoom.getInt("SuiteStatus");
        this.token = jsonRoom.getString("token");
        this.Tablet = jsonRoom.getInt("Tablet");
        this.Facility = jsonRoom.getInt("Facility");
    }

    public void setRoomBuilding(List<Building> buildings) {
        for (Building b:buildings) {
            if (b.id == building_id) {
                building = b;
                break;
            }
        }
    }

    public void setRoomFloor(List<Floor> floors) {
        for (Floor f:floors) {
            if (f.id == floor_id) {
                floor = f;
                break;
            }
        }
    }

    public void setFireRoom(FirebaseDatabase database) {
        fireRoom = database.getReference(MyApp.My_PROJECT.projectName+"/B"+building.buildingNo+"/F"+floor.floorNumber+"/R"+RoomNumber);
        devicesControlReference = database.getReference(MyApp.My_PROJECT.projectName+"Devices/"+RoomNumber);
        devicesDataReference = database.getReference(MyApp.My_PROJECT.projectName+"DevicesData/"+RoomNumber);
        fireRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("DND").getValue() != null) {
                    DND = Long.parseLong(Objects.requireNonNull(snapshot.child("DND").getValue()).toString());
                }
                else {
                    fireRoom.child("DND").setValue(0);
                }
                if (snapshot.child("Cleanup").getValue() != null) {
                    Cleanup = Long.parseLong(Objects.requireNonNull(snapshot.child("Cleanup").getValue()).toString());
                }
                else {
                    fireRoom.child("Cleanup").setValue(0);
                }
                if (snapshot.child("Laundry").getValue() != null) {
                    Laundry = Long.parseLong(Objects.requireNonNull(snapshot.child("Laundry").getValue()).toString());
                }
                else {
                    fireRoom.child("Laundry").setValue(0);
                }
                if (snapshot.child("RoomService").getValue() != null) {
                    RoomService = Long.parseLong(Objects.requireNonNull(snapshot.child("RoomService").getValue()).toString());
                }
                else {
                    fireRoom.child("RoomService").setValue(0);
                }
                if (snapshot.child("SOS").getValue() != null) {
                    SOS = Long.parseLong(Objects.requireNonNull(snapshot.child("SOS").getValue()).toString());
                }
                else {
                    fireRoom.child("SOS").setValue(0);
                }
                if (snapshot.child("Checkout").getValue() != null) {
                    Checkout = Long.parseLong(Objects.requireNonNull(snapshot.child("Checkout").getValue()).toString());
                }
                else {
                    fireRoom.child("Checkout").setValue(0);
                }
                if (snapshot.child("Restaurant").getValue() != null) {
                    Restaurant = Long.parseLong(Objects.requireNonNull(snapshot.child("Restaurant").getValue()).toString());
                }
                else {
                    fireRoom.child("Restaurant").setValue(0);
                }
                if (snapshot.child("ClientIn").getValue() != null) {
                    ClientIn = Integer.parseInt(Objects.requireNonNull(snapshot.child("ClientIn").getValue()).toString());
                }
                else {
                    fireRoom.child("ClientIn").setValue(0);
                }
                if (snapshot.child("selected").getValue() != null) {
                    selected = Integer.parseInt(Objects.requireNonNull(snapshot.child("selected").getValue()).toString());
                }
                else {
                    fireRoom.child("selected").setValue(0);
                }
                if (snapshot.child("loading").getValue() != null) {
                    loading = Integer.parseInt(Objects.requireNonNull(snapshot.child("loading").getValue()).toString());
                }
                else {
                    fireRoom.child("loading").setValue(0);
                }
                if (snapshot.child("online").getValue() != null) {
                    online = Integer.parseInt(Objects.requireNonNull(snapshot.child("online").getValue()).toString());
                }
                else {
                    fireRoom.child("online").setValue(0);
                }
                if (snapshot.child("powerStatus").getValue() != null) {
                    powerStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("powerStatus").getValue()).toString());
                }
                else {
                    fireRoom.child("powerStatus").setValue(0);
                }
                if (snapshot.child("curtainStatus").getValue() != null) {
                    curtainStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("curtainStatus").getValue()).toString());
                }
                else {
                    fireRoom.child("curtainStatus").setValue(0);
                }
                if (snapshot.child("doorStatus").getValue() != null) {
                    doorStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("doorStatus").getValue()).toString());
                }
                else {
                    fireRoom.child("doorStatus").setValue(0);
                }
                if (snapshot.child("reservationType").getValue() != null) {
                    byLink = Integer.parseInt(Objects.requireNonNull(snapshot.child("reservationType").getValue()).toString()) == 1;
                }
                else {
                    fireRoom.child("reservationType").setValue(0);
                }
                if (snapshot.child("RoomServiceText").getValue() != null) {
                    RoomServiceText = Objects.requireNonNull(snapshot.child("RoomServiceText").getValue()).toString();
                }
                else {
                    fireRoom.child("RoomServiceText").setValue(0);
                }
                if (snapshot.child("dep").getValue() != null) {
                    dep = Objects.requireNonNull(snapshot.child("dep").getValue()).toString();
                }
                else {
                    fireRoom.child("dep").setValue(0);
                }
                if (snapshot.child("message").getValue() != null) {
                    message = Objects.requireNonNull(snapshot.child("message").getValue()).toString();
                }
                else {
                    fireRoom.child("dep").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setFireRoom(PROJECT p,FirebaseDatabase database) {
        fireRoom = database.getReference(p.projectName+"/B"+building.buildingNo+"/F"+floor.floorNumber+"/R"+RoomNumber);
        devicesControlReference = database.getReference(p.projectName+"Devices/"+RoomNumber);
        devicesDataReference = database.getReference(p.projectName+"DevicesData/"+RoomNumber);
        fireRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("DND").getValue() != null) {
                    DND = Long.parseLong(Objects.requireNonNull(snapshot.child("DND").getValue()).toString());
                }
                else {
                    fireRoom.child("DND").setValue(0);
                }
                if (snapshot.child("Cleanup").getValue() != null) {
                    Cleanup = Long.parseLong(Objects.requireNonNull(snapshot.child("Cleanup").getValue()).toString());
                }
                else {
                    fireRoom.child("Cleanup").setValue(0);
                }
                if (snapshot.child("Laundry").getValue() != null) {
                    Laundry = Long.parseLong(Objects.requireNonNull(snapshot.child("Laundry").getValue()).toString());
                }
                else {
                    fireRoom.child("Laundry").setValue(0);
                }
                if (snapshot.child("RoomService").getValue() != null) {
                    RoomService = Long.parseLong(Objects.requireNonNull(snapshot.child("RoomService").getValue()).toString());
                }
                else {
                    fireRoom.child("RoomService").setValue(0);
                }
                if (snapshot.child("SOS").getValue() != null) {
                    SOS = Long.parseLong(Objects.requireNonNull(snapshot.child("SOS").getValue()).toString());
                }
                else {
                    fireRoom.child("SOS").setValue(0);
                }
                if (snapshot.child("Checkout").getValue() != null) {
                    Checkout = Long.parseLong(Objects.requireNonNull(snapshot.child("Checkout").getValue()).toString());
                }
                else {
                    fireRoom.child("Checkout").setValue(0);
                }
                if (snapshot.child("Restaurant").getValue() != null) {
                    Restaurant = Long.parseLong(Objects.requireNonNull(snapshot.child("Restaurant").getValue()).toString());
                }
                else {
                    fireRoom.child("Restaurant").setValue(0);
                }
                if (snapshot.child("ClientIn").getValue() != null) {
                    ClientIn = Integer.parseInt(Objects.requireNonNull(snapshot.child("ClientIn").getValue()).toString());
                }
                else {
                    fireRoom.child("ClientIn").setValue(0);
                }
                if (snapshot.child("selected").getValue() != null) {
                    selected = Integer.parseInt(Objects.requireNonNull(snapshot.child("selected").getValue()).toString());
                }
                else {
                    fireRoom.child("selected").setValue(0);
                }
                if (snapshot.child("loading").getValue() != null) {
                    loading = Integer.parseInt(Objects.requireNonNull(snapshot.child("loading").getValue()).toString());
                }
                else {
                    fireRoom.child("loading").setValue(0);
                }
                if (snapshot.child("online").getValue() != null) {
                    online = Integer.parseInt(Objects.requireNonNull(snapshot.child("online").getValue()).toString());
                }
                else {
                    fireRoom.child("online").setValue(0);
                }
                if (snapshot.child("powerStatus").getValue() != null) {
                    powerStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("powerStatus").getValue()).toString());
                }
                else {
                    fireRoom.child("powerStatus").setValue(0);
                }
                if (snapshot.child("curtainStatus").getValue() != null) {
                    curtainStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("curtainStatus").getValue()).toString());
                }
                else {
                    fireRoom.child("curtainStatus").setValue(0);
                }
                if (snapshot.child("doorStatus").getValue() != null) {
                    doorStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("doorStatus").getValue()).toString());
                }
                else {
                    fireRoom.child("doorStatus").setValue(0);
                }
                if (snapshot.child("reservationType").getValue() != null) {
                    byLink = Integer.parseInt(Objects.requireNonNull(snapshot.child("reservationType").getValue()).toString()) == 1;
                }
                else {
                    fireRoom.child("reservationType").setValue(0);
                }
                if (snapshot.child("RoomServiceText").getValue() != null) {
                    RoomServiceText = Objects.requireNonNull(snapshot.child("RoomServiceText").getValue()).toString();
                }
                else {
                    fireRoom.child("RoomServiceText").setValue(0);
                }
                if (snapshot.child("dep").getValue() != null) {
                    dep = Objects.requireNonNull(snapshot.child("dep").getValue()).toString();
                }
                else {
                    fireRoom.child("dep").setValue(0);
                }
                if (snapshot.child("message").getValue() != null) {
                    message = Objects.requireNonNull(snapshot.child("message").getValue()).toString();
                }
                else {
                    fireRoom.child("dep").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setFireRoomListeners() {
        Room r = this;
        final int[] firstRun = {0};
        Log.d("roomStatus", RoomNumber+"");
        roomStatusListener = fireRoom.child("roomStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null ) {
                    int value = Integer.parseInt(snapshot.getValue().toString());
                    if (value == 3 && roomStatus != 3) {
                        if (firstRun[0] > 0) {
                            PROJECT_VARIABLES.checkoutMood.startCheckoutMood(r);
                        }
                    }
                    else if (value == 2 && roomStatus != 2) {
                        firstDoorOpen = true;
                        if (firstRun[0] > 0) {
                            PROJECT_VARIABLES.checkInMood.startCheckinMood(r);
                        }
                    }
                    roomStatus = value;
                }
                firstRun[0]++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        clientInListener = fireRoom.child("ClientIn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    ClientIn = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeFireRoomListeners() {
        if (roomStatusListener != null) {
            fireRoom.child("roomStatus").removeEventListener(roomStatusListener);
        }
        if (clientInListener != null) {
            fireRoom.child("ClientIn").removeEventListener(clientInListener);
        }
    }

    public List<CheckinDevice> setMyDevices(List<DeviceBean> devices,HomeBean homeBean) {
        List<CheckinDevice> devs = new ArrayList<>();
        RoomHome = homeBean;
        for (int i=0;i<devices.size();i++) {
            DeviceBean d = devices.get(i);
            if (d.getName().equals(DeviceTypes.Gateway.toString()+RoomNumber) || d.getName().equals(RoomNumber+"ZGatway") || d.getName().equals(RoomNumber+"Gatway") || d.getName().equals(RoomNumber+"Gateway")) {
                //Log.d("getDevicesRun","gateway found "+d.name);
                if (gateways == null) {
                    gateways = new ArrayList<>();
                }
                CheckinGateway g = new CheckinGateway(d,this);
                //Log.d("getDevicesRun","gateway casted "+g.device.name);
                gateways.add(g);
                devs.add(g);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.Power.toString())) {
                CheckinPower p = new CheckinPower(d,this);
                power = p;
                devs.add(p);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.Lock.toString())) {
                if (locks == null) {
                    locks = new ArrayList<>();
                }
                CheckinLock l = new CheckinLock(d,this);
                locks.add(l);
                devs.add(l);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.Service.toString()+"Switch") || d.getName().equals(RoomNumber+DeviceTypes.Service.toString())) {
                if (services == null) {
                    services = new ArrayList<>();
                }
                CheckinServiceSwitch ss = new CheckinServiceSwitch(d,this);
                services.add(ss);
                devs.add(ss);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.MotionSensor.toString())) {
                if (motionSensors == null) {
                    motionSensors = new ArrayList<>();
                }
                CheckinMotionSensor mm = new CheckinMotionSensor(d,this);
                motionSensors.add(mm);
                devs.add(mm);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"1") || d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"2") || d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"3") || d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"4") || d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"5")||d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"6") || d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"7") || d.getName().equals(RoomNumber+DeviceTypes.Switch.toString()+"8")) {
                if (switches == null) {
                    switches = new ArrayList<>();
                }
                CheckinSwitch sw = new CheckinSwitch(d,this);
                switches.add(sw);
                devs.add(sw);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.DoorSensor.toString())) {
                if (doorSensors == null) {
                    doorSensors = new ArrayList<>();
                }
                CheckinDoorSensor ds = new CheckinDoorSensor(d,this);
                doorSensors.add(ds);
                devs.add(ds);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.Curtain.toString())) {
                if (curtains == null) {
                    curtains = new ArrayList<>();
                }
                CheckinCurtain cc = new CheckinCurtain(d,this);
                curtains.add(cc);
                devs.add(cc);
            }
            else if (d.getName().equals(RoomNumber+DeviceTypes.AC.toString())) {
                if (acs == null) {
                    acs = new ArrayList<>();
                }
                CheckinAC ca = new CheckinAC(d,this);
                acs.add(ca);
                devs.add(ca);
            }
        }
        Log.d("roomDevices",RoomNumber+devs.size()+"");
        return devs;
    }

    public List<CheckinDevice> getMyDevices() {
        List<CheckinDevice> devices = new ArrayList<>();
        if (isHasPower()) {
            devices.add(getPowerModule());
        }
        if (isHasAC()) {
            devices.addAll(acs);
        }
        if (isHasMotion()) {
            devices.addAll(motionSensors);
        }
        if (isHasCurtain()) {
            devices.addAll(curtains);
        }
        if (isHasLock()) {
            devices.addAll(locks);
        }
        if (isHasServiceSwitch()) {
            devices.addAll(services);
        }
        if (isHasDoorSensor()) {
            devices.addAll(doorSensors);
        }
        if (isHasGateway()) {
            devices.addAll(gateways);
        }
        if (isHasSwitch()) {
            devices.addAll(switches);
        }
        return devices;
    }

    public void setRoomDevicesListener(TextView tv,RequestQueue CLEANUP_QUEUE,RequestQueue LAUNDRY_QUEUE,RequestQueue CHECKOUT_QUEUE) {
        if (isHasPower()) {
            Log.d("deviceListener"+RoomNumber,"power set");
            power.listen(new PowerListener() {
                @Override
                public void powerOn() {
                    Log.d("deviceListener"+RoomNumber,"power on");
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    setRoomPowerStatus(2);
                    //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " power on");
                }

                @Override
                public void powerOff() {
                    Log.d("deviceListener"+RoomNumber,"power off");
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    setRoomPowerStatus(0);
                    //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " power off");
                }

                @Override
                public void powerByCard() {
                    Log.d("deviceListener"+RoomNumber,"power card");
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    setRoomPowerStatus(1);
                    //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " power byCard");
                }

                @Override
                public void online(boolean online) {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                }
            });
        }
        if (isHasServiceSwitch()) {
            getMainServiceSwitch().listen(new ServiceListener() {
                @Override
                public void cleanup() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        addCleanupOrder(CLEANUP_QUEUE);
                    }
                    //ControlDevice.setCurrentAction(tv,"Cleanup on room " + RoomNumber);
                }

                @Override
                public void cancelCleanup() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        cancelServiceOrder(CLEANUP_QUEUE,"Cleanup");
                    }
                    //ControlDevice.setCurrentAction(tv,"Cleanup canceled room " + RoomNumber);
                }

                @Override
                public void laundry() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        addLaundryOrder(LAUNDRY_QUEUE);
                    }
                   // ControlDevice.setCurrentAction(tv,"Laundry on room " + RoomNumber);
                }

                @Override
                public void cancelLaundry() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        cancelServiceOrder(LAUNDRY_QUEUE, "Laundry");
                    }
                    //ControlDevice.setCurrentAction(tv,"Laundry canceled room " + RoomNumber);
                }

                @Override
                public void dnd() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        addDndOrder();
                    }
                    //ControlDevice.setCurrentAction(tv,"DND on room " + RoomNumber);
                }

                @Override
                public void cancelDnd() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        cancelDndOrder();
                    }
                    //ControlDevice.setCurrentAction(tv,"DND canceled room " + RoomNumber);
                }

                @Override
                public void checkout() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        addCheckoutOrder(CHECKOUT_QUEUE);
                    }
                    //ControlDevice.setCurrentAction(tv,"Checkout on room " + RoomNumber);
                }

                @Override
                public void cancelCheckout() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (roomStatus == 2) {
                        cancelServiceOrder(CHECKOUT_QUEUE, "Checkout");
                    }
                    //ControlDevice.setCurrentAction(tv,"Checkout canceled room " + RoomNumber);
                }

                @Override
                public void online(boolean online) {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (!isHasMotion()) {
                        setClientIn(online);
                    }
                }
            });
        }
        if (isHasCurtain()) {
            for (CheckinCurtain cc:curtains) {
                cc.listen(new CurtainListener() {
                    @Override
                    public void open() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        setRoomCurtainStatus(1);
                    }

                    @Override
                    public void close() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        setRoomCurtainStatus(0);
                    }

                    @Override
                    public void stop() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    }

                    @Override
                    public void online(boolean online) {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    }
                });
            }
        }
        if (isHasSwitch()) {
            for (CheckinSwitch cs : switches) {
                cs.listen(new SwitchListener() {
                    @Override
                    public void oneOn() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp1.dpId)).setValue(3);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp1.dpId+" on");
                    }

                    @Override
                    public void oneOff() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp1.dpId)).setValue(0);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp1.dpId+" off");
                    }

                    @Override
                    public void secondOn() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp2.dpId)).setValue(3);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp2.dpId+" on");
                    }

                    @Override
                    public void secondOff() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp2.dpId)).setValue(0);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp2.dpId+" off");
                    }

                    @Override
                    public void thirdOn() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp3.dpId)).setValue(3);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp3.dpId+" on");
                    }

                    @Override
                    public void thirdOff() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp3.dpId)).setValue(0);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp3.dpId+" off");
                    }

                    @Override
                    public void forthOn() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp4.dpId)).setValue(3);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp4.dpId+" on");
                    }

                    @Override
                    public void forthOff() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(cs.device.name).child(String.valueOf(cs.dp4.dpId)).setValue(0);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " "+cs.device.name+ " button "+cs.dp4.dpId+" off");
                    }

                    @Override
                    public void online(boolean online) {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    }
                });
            }
        }
        if (isHasGateway()) {
            if (gateways.get(0) != null) {
                gateways.get(0).listen(this::setRoomOnline);
            }
        }
        if (isHasLock()) {
            getRoomLock().listen(new LockListener() {
                @Override
                public void unlocked() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    devicesControlReference.child(getRoomLock().device.name).child("1").setValue(0);
                    //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " lock unlocked");
                }

                @Override
                public void battery(int battery) {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    fireRoom.child("lockBattery").setValue(battery);
                    //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " lock battery "+battery);
                }

                @Override
                public void online(boolean online) {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                }
            });
        }
        if (isHasAC()) {
            for (CheckinAC ca : acs) {
                ca.listen(new ACListener() {
                    @Override
                    public void onPowerOn() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(ca.name).child("power").setValue(3);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " ac turn on");
                    }

                    @Override
                    public void onPowerOff() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        devicesControlReference.child(ca.name).child("power").setValue(0);
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " ac turn off");
                    }

                    @Override
                    public void onTempSet(String newTemp) {
                        //Log.d("acScenario", RoomNumber+" temp set "+newTemp);
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        int intNewValue = Integer.parseInt(newTemp);
                        if (newTemp.length() > 2) {
                            double temp = intNewValue*0.1;
                            if (temp != PROJECT_VARIABLES.Temp) {
                                ca.clientSetTemp = newTemp;
                                Log.d("acScenario", RoomNumber+" client temp saved "+ca.clientSetTemp);
                            }
                            fireRoom.child("temp").setValue(temp) ;
                        }
                        else {
                            if (intNewValue != PROJECT_VARIABLES.Temp) {
                                ca.clientSetTemp = newTemp;
                                Log.d("acScenario", RoomNumber+" client temp saved "+ca.clientSetTemp);
                            }
                            fireRoom.child("temp").setValue(newTemp) ;
                        }
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " ac set to "+newTemp);
                    }

                    @Override
                    public void onTempCurrent(String currentTemp) {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    }

                    @Override
                    public void onFanSet(String newFan) {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        //ControlDevice.setCurrentAction(tv,"Room " + RoomNumber + " ac fan set to "+newFan);
                    }

                    @Override
                    public void online(boolean online) {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    }
                });
            }
        }
        if (isHasDoorSensor()) {
            getMainRoomDoorSensor().listen(new DoorListener() {
                @Override
                public void open() {
                    if (doorWarningTimer == null) {
                        doorWarningTimer = new Timer();
                        doorWarningTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setRoomDoorStatus(2);
                                if (doorWarningTimer != null) {
                                    doorWarningTimer.cancel();
                                    doorWarningTimer = null;
                                }
                            }
                        },PROJECT_VARIABLES.DoorWarning);
                    }
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    setRoomDoorStatus(1);
                }

                @Override
                public void close() {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (doorWarningTimer != null) {
                        doorWarningTimer.cancel();
                        doorWarningTimer = null;
                    }
                    setRoomDoorStatus(0);
                    //TODO only for samples project remove it for other projects
                    //powerScene = true;

//                    if (RoomNumber == 110 || RoomNumber == 109) {
//                        Timer t = new Timer();
//                        t.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                Log.d("powerScenario", RoomNumber + " time finish " + powerScene);
//                                if (powerScene) {
//                                    powerOffRoom(new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                    setClientIn(false);
//                                }
//                            }
//                        }, 30 * 1000 + PROJECT_VARIABLES.Interval);
//                        if (PROJECT_VARIABLES.isAcScenarioActive()) {
//                            if (doorWarningTimer != null) {
//                                doorWarningTimer.cancel();
//                                doorWarningTimer = null;
//                            }
//                            Log.d("acScenario", "has ac: " + isHasAC() + " has motion: " + isHasMotion() + " timer " + (acScenarioTimer == null));
//                            if (isHasAC() && isHasMotion()) {
//                                if (acScenarioTimer != null) {
//                                    Log.d("acScenario", RoomNumber + " timer not null");
//                                    acScenarioTimer.cancel();
//                                    acScenarioTimer = null;
//                                }
//                                Log.d("acScenario", RoomNumber + " started " + PROJECT_VARIABLES.Interval);
//                                acScenarioStarted = true;
//                                acScenarioTimer = new Timer();
//                                acScenarioTimer.schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        Log.d("acScenario", RoomNumber + " time finish " + acScenarioStarted);
//                                        if (acScenarioStarted) {
//                                            setAcToSetPoint();
//                                            acScenarioStarted = false;
//                                            Log.d("acScenario", RoomNumber + " scenario executed");
//                                        }
//                                        if (acScenarioTimer != null) {
//                                            acScenarioTimer.cancel();
//                                            acScenarioTimer = null;
//                                        }
//                                    }
//                                }, PROJECT_VARIABLES.Interval);
//                            }
//                        }
//                    }
//                    // for electric scenario
//                    if (RoomNumber == 211 || RoomNumber == 104) {
//                        if (electricTimer == null) {
//                            Log.d("powerScenario", RoomNumber + " time start " + powerScene);
//                            electricTimer = new Timer();
//                            electricTimer.schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    Log.d("powerScenario", RoomNumber + " time finish power scenario: " + powerScene + " somebody: " + somebody);
//                                    electricTimer.cancel();
//                                    electricTimer = null;
//                                    if (!somebody && powerScene) {
//                                        powerOffRoom(new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                    powerScene = false;
//                                }
//                            }, PROJECT_VARIABLES.Interval);
//                        }
//                    }
//                    // for ac scenario
//                    if (RoomNumber == 210 || RoomNumber == 1040) {
//                        if (PROJECT_VARIABLES.isAcScenarioActive()) {
//                            if (doorWarningTimer != null) {
//                                doorWarningTimer.cancel();
//                                doorWarningTimer = null;
//                            }
//                            //Log.d("acScenario", "has ac: "+isHasAC() +" has motion: "+isHasMotion() + " timer "+ (acScenarioTimer == null));
//                            if (isHasAC() && isHasMotion()) {
//                                Log.d("acScenario" + RoomNumber, "________________________________________________________");
//                                Log.d("acScenario" + RoomNumber, RoomNumber + " started " + PROJECT_VARIABLES.Interval);
//                                if (acScenarioTimer == null) {
//                                    acScenarioStarted = true;
//                                    acScenarioTimer = new Timer();
//                                    acScenarioTimer.schedule(new TimerTask() {
//                                        @Override
//                                        public void run() {
//                                            Log.d("acScenario" + RoomNumber, RoomNumber + " time finish acScenario: " + acScenarioStarted + " somebody: " + somebody);
//                                            if (acScenarioTimer != null) {
//                                                acScenarioTimer.cancel();
//                                                acScenarioTimer = null;
//                                            }
//                                            if (!somebody && acScenarioStarted) {
//                                                setAcToSetPoint();
//                                                Log.d("acScenario" + RoomNumber, "temp raised to setPoint");
//                                            }
//                                            acScenarioStarted = false;
//                                        }
//                                    }, PROJECT_VARIABLES.Interval);
//                                }
//                            }
//                        }
//                    }
                }

                @Override
                public void battery(int battery) {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                }

                @Override
                public void online(boolean online) {
                    Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                }
            });
        }
        if (isHasMotion()) {
            for (CheckinMotionSensor ms : motionSensors) {
                ms.listen(new MotionListener() {
                    @Override
                    public void motionDetected() {
                        Log.d("powerScenario" , RoomNumber+" motion detected");
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (RoomNumber == 110 || RoomNumber == 109) {
                            setClientIn(true);
                            powerScene = false;
                            powerOnRoom(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });

                        }

                        if (PROJECT_VARIABLES.isAcScenarioActive()) {
                            if (acScenarioStarted) {
                                acScenarioStarted = false;
                                Log.d("acScenario", RoomNumber+" scenario cancelled motion detected");
                            }
                            else {
                                Log.d("acScenario", RoomNumber+" motion detected temp back ");
                                for (CheckinAC ac :acs) {
                                    ac.setTemperature(Integer.parseInt(ac.clientSetTemp), new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            Log.d("acScenario", RoomNumber+" temp back error "+error+" "+ac.clientSetTemp );
                                        }

                                        @Override
                                        public void onSuccess() {
                                            Log.d("acScenario",RoomNumber+" temp done "+ac.clientSetTemp);
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void nobody() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        Log.d("powerScenario"+RoomNumber , RoomNumber+" nobody");
                        Log.d("acScenario"+RoomNumber, RoomNumber+" nobody");
                        setClientIn(false);
                        // for electric scenario
//                        if (RoomNumber == 211 || RoomNumber == 1040) {
//                            if (powerScene) {
//                                powerOffRoom(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                            }
//                        }
                        // for ac scenario
//                        if (RoomNumber == 210 || RoomNumber == 104) {
//                            if (acScenarioStarted) {
//                                setAcToSetPoint();
//                                Log.d("acScenario", RoomNumber+" setpoint done "+PROJECT_VARIABLES.Temp);
//                            }
//                        }
                    }

                    @Override
                    public void somebody() {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        Log.d("powerScenario"+RoomNumber , RoomNumber+" somebody");
                        Log.d("acScenario"+RoomNumber , RoomNumber+" somebody");
                        setClientIn(true);
                        if (RoomNumber == 211 || RoomNumber == 104) {
                            if (!powerScene) {
                                powerOnRoom(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                            else {
                                powerScene = false;
                            }
                        }
                        if (RoomNumber == 210 || RoomNumber == 1040) {
                            if (!acScenarioStarted) {
                                setAcToGuestTemperature();
                            }
                            else {
                                acScenarioStarted = false;
                            }
                        }
                    }

                    @Override
                    public void online(boolean online) {
                        Tuya.LastListenersActionTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    }
                });
            }
        }
    }

    public void stopListeningAllRoomDevices() {
        if (power != null) {
            power.unListen();
        }
        if (switches != null) {
            for (CheckinSwitch sw : switches) {
                sw.unListen();
            }
        }
        if (acs != null) {
            for (CheckinAC sw : acs) {
                sw.unListen();
            }
        }
        if (services != null) {
            for (CheckinServiceSwitch sw : services) {
                sw.unListen();
            }
        }
        if (locks != null) {
            for (CheckinLock sw : locks) {
                sw.unListen();
            }
        }
        if (doorSensors != null) {
            for (CheckinDoorSensor sw : doorSensors) {
                sw.unListen();
            }
        }
        if (motionSensors != null) {
            for (CheckinMotionSensor sw : motionSensors) {
                sw.unListen();
            }
        }
        if (curtains != null) {
            for (CheckinCurtain sw : curtains) {
                sw.unListen();
            }
        }
    }

    public void getRoomReservationType(GetReservationType callback) {
        fireRoom.child("reservationType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callback.onSuccess(Integer.parseInt(snapshot.getValue().toString()));
                }
                else {
                    callback.onError("type is null");
                }
            }

            // getting reservation  type failed
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }

    public CheckinLock getRoomLock() {
        return locks.get(0);
    }

    public boolean isHasAC() {
        return acs != null && !acs.isEmpty();
    }

    public boolean isHasPower() {
        return power != null;
    }
    public CheckinPower getPowerModule() {
        return power;
    }

    public boolean isHasGateway() {
        return gateways != null && !gateways.isEmpty();
    }
    public CheckinGateway getRoomGateway() {
        if (isHasGateway()) {
            return gateways.get(0);
        }
        return null;
    }

    public boolean isHasLock() {
        return locks != null && !locks.isEmpty();
    }

    public boolean isHasMotion() {
        return motionSensors != null && !motionSensors.isEmpty();
    }

    public boolean isHasDoorSensor() {
        return doorSensors != null && !doorSensors.isEmpty();
    }
    public CheckinDoorSensor getMainRoomDoorSensor() {
        return doorSensors.get(0);
    }

    public boolean isHasServiceSwitch() {
        return services != null && !services.isEmpty();
    }
    public CheckinServiceSwitch getMainServiceSwitch() {
        if (isHasServiceSwitch()) {
            return services.get(0);
        }
        return null;
    }

    public boolean isHasSwitch() {
        return switches != null && !switches.isEmpty();
    }

    public boolean isHasCurtain() {
        return curtains != null && !curtains.isEmpty();
    }
    public List<CheckinCurtain> getCurtains() {
        return curtains;
    }

    public void powerOnRoom(IResultCallback result) {
        if (power == null) {
            result.onError("no code","room has no power module");
        }
        else {
            power.powerOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void powerOnRoomOffline(IResultCallback result) {
        if (power == null) {
            result.onError("no code","room has no power module");
        }
        else {
            power.powerOnOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void powerByCardRoom(IResultCallback result) {
        if (power == null) {
            result.onError("no code","room has no power module");
        }
        else {
            power.powerByCard(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void powerByCardRoomOffline(IResultCallback result) {
        if (power == null) {
            result.onError("no code","room has no power module");
        }
        else {
            power.powerByCard(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void powerByCardAfterMinutes(int minutes, RequestCallback callback) {
        if (power != null) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    powerByCardRoom(new IResultCallback() {
                        @Override
                        public void onSuccess() {
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String code,String error) {
                            callback.onFail(error);
                        }
                    });
                }
            }, (long) minutes * 60 * 1000);
        }
    }

    public void powerOffRoom(IResultCallback result) {
        if (power == null) {
         result.onError("no code","no power modules installed in this room");
        }
        else {
            power.powerOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void powerOffRoomOffline(IResultCallback result) {
        if (power == null) {
            result.onError("no code","no power modules installed in this room");
        }
        else {
            power.powerOffOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void powerOffAfterMinutes(int minutes, RequestCallback callback) {
        if (power != null) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    powerOffRoom(new IResultCallback() {
                        @Override
                        public void onSuccess() {
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String code,String error) {
                            callback.onFail(error);
                        }
                    });
                }
            }, (long) minutes * 60 * 1000);
        }
    }

    public void cleanupRoomOn(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).cleanupOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }
    public void addCleanupOrder(RequestQueue Q) {
        if (getMainServiceSwitch().lastCleanup == 0) {
            Cleanup = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
            fireRoom.child("Cleanup").setValue(Cleanup);
        }
        fireRoom.child("dep").setValue("Cleanup");
        String url = MyApp.My_PROJECT.url + "reservations/addCleanupOrderControlDevice"+addCleanupCounter ;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            try {
                JSONObject result = new JSONObject(response);
                if (!result.getString("result").equals("success")) {
                    Log.d("addCleanupOrder", "error "+result.getString("error"));
                }
                else {
                    Log.d("addCleanupOrder", "done ");
                }
            } catch (JSONException e) {
                Log.d("addCleanupOrder", "error "+e.getMessage());
            }
        }, error -> Log.d("addCleanupOrder", "error "+error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(id));
                return params;
            }
        };
        Q.add(addOrder);
        addCleanupCounter++;
        if (addCleanupCounter == 5) {
            addCleanupCounter = 1 ;
        }
    }

    public void cleanupRoomOff(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).cleanupOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void laundryRoomOn(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).laundryOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }
    public void addLaundryOrder(RequestQueue Q) {
        if (getMainServiceSwitch().lastLaundry == 0) {
            Laundry = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
            fireRoom.child("Laundry").setValue(Laundry);
        }
        fireRoom.child("dep").setValue("Laundry");
        Log.d("addLaundryRsp" , "started");
        String url = MyApp.My_PROJECT.url + "reservations/addLaundryOrderControlDevice"+addLaundryCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addLaundryRsp" , response);
            try {
                JSONObject result = new JSONObject(response);
                if (!result.getString("result").equals("success")) {
                    Log.d("addLaundryRsp", result.getString("error"));
                }
            } catch (JSONException e) {
                Log.d("addLaundryRsp", e.toString());
            }
        }, error -> Log.d("addLaundryRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(id));
                return params;
            }
        };
        Q.add(addOrder);
        addLaundryCounter++;
        if (addLaundryCounter == 5) {
            addLaundryCounter = 1 ;
        }
    }

    public void laundryRoomOff(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            getMainServiceSwitch().laundryOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void checkoutRoomOn(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).checkoutOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }
    public void addCheckoutOrder (RequestQueue Q) {
        if (getMainServiceSwitch().lastCheckout == 0) {
            Checkout = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
            fireRoom.child("Checkout").setValue(Checkout);
        }
        fireRoom.child("dep").setValue("Checkout");
        String url = MyApp.My_PROJECT.url + "reservations/addCheckoutOrderControlDevice"+addCheckoutCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("addCheckoutRsp" , response);
            try {
                JSONObject result = new JSONObject(response);
                if (!result.getString("result").equals("success")) {
                    Log.d("addCheckoutRsp", result.getString("error"));
                }
            } catch (JSONException e) {
                Log.d("addCheckoutRsp", e.toString());
            }
        }, error -> Log.d("addCheckoutRsp" , error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(id));
                return params;
            }
        };
        Q.add(addOrder);
        addCheckoutCounter++;
        if (addCheckoutCounter == 5) {
            addCheckoutCounter = 1 ;
        }
    }

    public void checkoutRoomOff(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).checkoutOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void dndRoomOn(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).dndOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }
    public void addDndOrder() {
        if (getMainServiceSwitch().lastDND == 0) {
            DND = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
            fireRoom.child("DND").setValue(DND);
        }
        fireRoom.child("dep").setValue("DND");
    }

    public void dndRoomOff(IResultCallback result) {
        if (services == null || services.isEmpty()) {
            result.onError("no code","no service switch installed in this room");
        }
        else {
            services.get(0).dndOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }
    public void cancelDndOrder() {
        DND = 0;
        fireRoom.child("DND").setValue(DND);
    }

    public void showMessageOnReception(String message,int seconds) {
        fireRoom.child("message").setValue(message);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                fireRoom.child("message").setValue("");
            }
        },seconds* 1000L);
    }

    public void cancelServiceOrder(RequestQueue Q,String type) {
        switch (type) {
            case "Cleanup":
                Cleanup = 0;
                fireRoom.child("Cleanup").setValue(0);
                break;
            case "Laundry":
                Laundry = 0;
                fireRoom.child("Laundry").setValue(0);
                break;
            case "Checkout":
                Checkout = 0;
                fireRoom.child("Checkout").setValue(0);
                break;
            case "RoomService":
                RoomService = 0;
                fireRoom.child("RoomService").setValue(0);
                break;
        }
        Log.d("remove"+type+"Order" , "pressed");
        String url = MyApp.My_PROJECT.url + "reservations/cancelServiceOrderControlDevice"+cancelOrderCounter;
        StringRequest removeOrder = new StringRequest(Request.Method.POST,url, response -> {
            Log.d("remove"+type+"Order" , "response "+response);
            try {
                JSONObject result = new JSONObject(response);
                if (!result.getString("result").equals("success")) {
                    Log.d("remove"+type+"Order" , "error "+result.getString("error"));
                }
            } catch (JSONException e) {
                Log.d("remove"+type+"Order" , "error "+e);
            }
        }, error -> Log.d("remove"+type+"Order" , "error "+error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(id));
                params.put("order_type",type);
                return params;
            }
        };
        Q.add(removeOrder);
        cancelOrderCounter++;
        if (cancelOrderCounter == 5) {
            cancelOrderCounter = 1 ;
        }
    }

//    public void setRoomACScenarioAndDoorOpenWarning(TextView tv) {
//        Room r = this;
//        if (isHasDoorSensor()) {
//            acScenarioTimer = new Timer();
//            doorWarningTimer = new Timer();
//            getMainRoomDoorSensor().listen(new DoorListener() {
//                @Override
//                public void open() {
//                    tv.setText(MessageFormat.format("Room {0} door open", RoomNumber));
//                    fireRoom.child("doorStatus").setValue(1);
//                    if (acScenarioTimer != null) {
//                        acScenarioTimer.cancel();
//                    }
//                    if (doorWarningTimer != null) {
//                        doorWarningTimer.cancel();
//                    }
//                    if (isHasAC() && isHasMotion()) {
//                        acScenarioStarted = true;
//                        if (acScenarioTimer != null) {
//                            tv.setText(MessageFormat.format("Room {0} ac scenario started", RoomNumber));
//                            acScenarioTimer.schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    for (CheckinAC ac : acs) {
//                                        ac.setTemperature(PROJECT_VARIABLES.Temp, new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                    tv.setText(MessageFormat.format("Room {0} ac scenario done", RoomNumber));
//                                }
//                            }, PROJECT_VARIABLES.Interval);
//                        }
//                    }
//                    doorWarningTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            fireRoom.child("doorStatus").setValue(2);
//                        }
//                    },PROJECT_VARIABLES.DoorWarning);
//                    if (firstDoorOpen && roomStatus == 2) {
//                        firstDoorOpen = false;
//                        PROJECT_VARIABLES.checkInMood.startCheckinMood(r);
//                    }
//                    else if (!firstDoorOpen && roomStatus == 2) {
//                        // run client back actions
//                        PROJECT_VARIABLES.clientBackActions.start(r);
//                    }
//                }
//
//                @Override
//                public void close() {
//                    tv.setText(MessageFormat.format("Room {0} door closed", RoomNumber));
//                    fireRoom.child("doorStatus").setValue(0);
//                    if (doorWarningTimer != null) {
//                        doorWarningTimer.cancel();
//                    }
//                }
//
//                @Override
//                public void battery(int battery) {
//                    fireRoom.child("doorSensorBattery").setValue(battery);
//                }
//
//                @Override
//                public void online(boolean online) {
//
//                }
//            });
//            if (isHasAC() && isHasMotion()) {
//                for (CheckinMotionSensor cm : motionSensors) {
//                    cm.listen(new MotionListener() {
//                        @Override
//                        public void motionDetected() {
//                            if (acScenarioStarted) {
//                                tv.setText(MessageFormat.format("Room {0} ac scenario canceled", RoomNumber));
//                                acScenarioTimer.cancel();
//                                acScenarioStarted = false;
//                                for (CheckinAC ac : acs) {
//                                    ac.setTemperature(Integer.parseInt(ac.clientSetTemp), new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void online(boolean online) {
//
//                        }
//                    });
//                }
//            }
//        }
//    }

    // static functions

    public static void sortRoomsByNumber(List<Room> room) {
        for (int i = 0; i < room.size(); i++) {
            for (int j = 1; j < (room.size() - i); j++) {
                if (room.get(j - 1).RoomNumber > room.get(j).RoomNumber) {
                    Collections.swap(room, j, j - 1);
                }
            }
        }
    }

    public static void setRoomsBuildingsAndFloors(List<Room> rooms,List<Building> buildings,List<Floor> floors) {
        Log.d("roomBuildingFloor",floors.size()+" ");
        for (Room r :rooms) {
            r.setRoomBuilding(buildings);
            r.setRoomFloor(floors);
            Log.d("roomBuildingFloor",r.RoomNumber+" "+r.building.buildingNo+" "+r.floor_id+" "+r.floor.floorNumber);
        }
        for (Building b : buildings) {
            b.setFloors(floors);
        }
        for (Floor f : floors) {
            f.setFloorRooms(rooms);
            f.setFloorBuilding(buildings);
        }
    }

    public static void setRoomsFireRooms(List<Room> rooms,FirebaseDatabase database) {
        for (Room r :rooms) {
            r.setFireRoom(database);
        }
    }

    public static void setRoomsFireRooms(PROJECT p,List<Room> rooms,FirebaseDatabase database) {
        for (Room r :rooms) {
            r.setFireRoom(p,database);
        }
    }

    public static void setRoomsFireRoomsListener(List<Room> rooms) {
        for (Room r : rooms) {
            r.setFireRoomListeners();
        }
    }

    public static void removeRoomsFireRoomsListener(List<Room> rooms) {
        for (Room r : rooms) {
            r.removeFireRoomListeners();
        }
    }

    public static void setRoomsFireRoomsDevicesControlListener(List<Room> rooms) {
        for (Room room : rooms) {
            if (room.RoomNumber == 104) {
                Log.d("104Prob","control listeners");
            }
            if (room.isHasPower()) {
                room.power.setFirebaseDevicesControl(room.devicesControlReference);
                if (room.RoomNumber == 104) {
                    Log.d("104Prob","control listeners power done");
                }
            }
            if (room.isHasCurtain()) {
                for (CheckinCurtain cc : room.curtains) {
                    cc.setFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasAC()) {
                for (CheckinAC ca : room.acs) {
                    ca.setFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasLock()) {
                for (CheckinLock cl : room.locks) {
                    cl.setFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasSwitch()) {
                for (CheckinSwitch cs : room.switches) {
                    cs.setFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasServiceSwitch()) {
                if (room.RoomNumber == 104) {
                    Log.d("104Prob","control listeners service done");
                }
                room.getMainServiceSwitch().setFirebaseDevicesControl(room.fireRoom);
            }
        }
    }

    public static void removeRoomFireRoomsDevicesControlListener(List<Room> rooms){
        for (Room room : rooms) {
            if (room.isHasPower()) {
                room.power.removeFirebaseDevicesControl(room.devicesControlReference);
            }
            if (room.isHasCurtain()) {
                for (CheckinCurtain cc : room.curtains) {
                    cc.removeFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasAC()) {
                for (CheckinAC ca : room.acs) {
                    ca.removeFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasLock()) {
                for (CheckinLock cl : room.locks) {
                    cl.removeFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasSwitch()) {
                for (CheckinSwitch cs : room.switches) {
                    cs.removeFirebaseDevicesControl(room.devicesControlReference);
                }
            }
            if (room.isHasServiceSwitch()) {
                room.getMainServiceSwitch().removeFirebaseDevicesControl(room.devicesControlReference);
            }
        }
    }

    public static List<CheckinDevice> setRoomsDevices(List<Room> rooms,List<DeviceBean> devices,HomeBean homeBean) {
        List<CheckinDevice> devs = new ArrayList<>();
        for (Room r:rooms) {
            devs.addAll(r.setMyDevices(devices,homeBean));
        }
        return devs;
    }

    public static void setRoomsDevicesListener(List<Room> rooms,TextView tv,RequestQueue CQ,RequestQueue LQ,RequestQueue CHQ) {
        for (Room room:rooms) {
            Log.d("deviceListener"+room.RoomNumber,"started");
            room.setRoomDevicesListener(tv,CQ,LQ,CHQ);
        }
    }

    public static void stopListeningAllRoomsDevices(List<Room> rooms) {
        for (Room r :rooms) {
            r.stopListeningAllRoomDevices();
        }
    }

    public static void stopAllRoomListeners(List<Room> rooms) {
        Log.d("bootingOp","stop listeners");
        removeRoomsFireRoomsListener(rooms);
        removeRoomFireRoomsDevicesControlListener(rooms);
        stopListeningAllRoomsDevices(rooms);
    }

    public static void createRoomsServiceMoods(List<Room> rooms,List<SceneBean> scenes) {
        for (Room room:rooms) {
            room.createRoomServiceMoods(room.RoomHome,scenes);
        }
    }

    public static void setAllRoomsAcScenarioAndDoorWarning(List<Room> rooms,TextView tv) {
        for (Room room : rooms) {
            //room.setRoomACScenarioAndDoorOpenWarning(tv);
        }
    }

    public static Room searchRoomInList(List<Room> rooms , int roomNumber) {
        if (rooms != null) {
            for (int i = 0; i<rooms.size(); i++) {
                if (rooms.get(i).RoomNumber == roomNumber) {
                    return rooms.get(i);
                }
            }
        }
        return null ;
    }

    public void createRoomServiceMoods(HomeBean h,List<SceneBean> list) {
        if (h != null) {
            if (PROJECT_VARIABLES.cleanupButton != 0 && PROJECT_VARIABLES.dndButton != 0) {
                if (getMainServiceSwitch() != null) {
                    if (getMainServiceSwitch().dps.get(String.valueOf(PROJECT_VARIABLES.cleanupButton)) != null && getMainServiceSwitch().dps.get(String.valueOf(PROJECT_VARIABLES.dndButton)) != null) {
                        if (searchScene(list,RoomNumber+"ServiceSwitchDNDScene2")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            BoolRule rule = BoolRule.newInstance("dp" + PROJECT_VARIABLES.dndButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(getMainServiceSwitch(), String.valueOf(PROJECT_VARIABLES.dndButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(PROJECT_VARIABLES.cleanupButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(getMainServiceSwitch().devId, taskMap);
                            tasks.add(task);
                            TuyaHomeSdk.getSceneManagerInstance().createScene(
                                    h.getHomeId(),
                                    RoomNumber + "ServiceSwitchDNDScene2", // The name of the scene.
                                    false,
                                    defaultImage,  // Indicates whether the scene is displayed on the homepage.
                                    condS, // The effective period. This parameter is optional.
                                    tasks, // The conditions.
                                    null,     // The tasks.
                                    SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                    new ITuyaResultCallback<SceneBean>() {
                                        @Override
                                        public void onSuccess(SceneBean sceneBean) {
                                            Log.d("SCENE_DND1", "createScene Success");
                                            TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                    IResultCallback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Log.d("SCENE_DND1", "enable Scene Success");
                                                        }

                                                        @Override
                                                        public void onError(String errorCode, String errorMessage) {
                                                            Log.d("SCENE_DND1", errorMessage + " " + errorCode);
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            Log.d("SCENE_DND1", errorMessage + " " + errorCode);
                                        }
                                    });
                        }
                        if (searchScene(list,RoomNumber + "ServiceSwitchCleanupScene")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            BoolRule rule = BoolRule.newInstance("dp" + PROJECT_VARIABLES.cleanupButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(getMainServiceSwitch(), String.valueOf(PROJECT_VARIABLES.cleanupButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(PROJECT_VARIABLES.dndButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(getMainServiceSwitch().devId, taskMap);
                            tasks.add(task);
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                                            h.getHomeId(),
                                            RoomNumber + "ServiceSwitchCleanupScene", // The name of the scene.
                                            false,
                                            defaultImage,  // Indicates whether the scene is displayed on the homepage.
                                            condS, // The effective period. This parameter is optional.
                                            tasks, // The conditions.
                                            null,     // The tasks.
                                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                            new ITuyaResultCallback<SceneBean>() {
                                                @Override
                                                public void onSuccess(SceneBean sceneBean) {
                                                    Log.d("SCENE_Cleanup", "createScene Success");
                                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                            IResultCallback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Log.d("SCENE_Cleanup", "enable Scene Success");
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {
                                                                    Log.d("SCENE_Cleanup", errorMessage);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_Cleanup", errorMessage);
                                                }
                                            });
                                }
                            },2000);

                        }
                    }
                }
            }
            if (PROJECT_VARIABLES.laundryButton != 0 && PROJECT_VARIABLES.dndButton != 0) {
                if (getMainServiceSwitch() != null) {
                    if (getMainServiceSwitch().dps.get(String.valueOf(PROJECT_VARIABLES.laundryButton)) != null && getMainServiceSwitch().dps.get(String.valueOf(PROJECT_VARIABLES.dndButton)) != null) {
                        if (searchScene(list,RoomNumber + "ServiceSwitchDNDScene3")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            BoolRule rule = BoolRule.newInstance("dp" + PROJECT_VARIABLES.dndButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(getMainServiceSwitch(), String.valueOf(PROJECT_VARIABLES.dndButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(PROJECT_VARIABLES.laundryButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(getMainServiceSwitch().devId, taskMap);
                            tasks.add(task);
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                                            h.getHomeId(),
                                            RoomNumber + "ServiceSwitchDNDScene3", // The name of the scene.
                                            false,
                                            defaultImage,  // Indicates whether the scene is displayed on the homepage.
                                            condS, // The effective period. This parameter is optional.
                                            tasks, // The conditions.
                                            null,     // The tasks.
                                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                            new ITuyaResultCallback<SceneBean>() {
                                                @Override
                                                public void onSuccess(SceneBean sceneBean) {
                                                    Log.d("SCENE_DND2", "createScene Success");
                                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                            IResultCallback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Log.d("SCENE_DND2", "enable Scene Success");
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {
                                                                    Log.d("SCENE_DND2", errorMessage);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_DND2", errorMessage);
                                                }
                                            });
                                }
                            }, 4000);

                        }
                        if (searchScene(list, RoomNumber + "ServiceSwitchLaundryScene")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            BoolRule rule = BoolRule.newInstance("dp" + PROJECT_VARIABLES.laundryButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(getMainServiceSwitch(), String.valueOf(PROJECT_VARIABLES.laundryButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(PROJECT_VARIABLES.dndButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(getMainServiceSwitch().devId, taskMap);
                            tasks.add(task);
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                                            h.getHomeId(),
                                            RoomNumber + "ServiceSwitchLaundryScene", // The name of the scene.
                                            false,
                                            defaultImage,  // Indicates whether the scene is displayed on the homepage.
                                            condS, // The effective period. This parameter is optional.
                                            tasks, // The conditions.
                                            null,     // The tasks.
                                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                            new ITuyaResultCallback<SceneBean>() {
                                                @Override
                                                public void onSuccess(SceneBean sceneBean) {
                                                    Log.d("SCENE_Laundry", "createScene Success");
                                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                            IResultCallback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Log.d("SCENE_Laundry", "enable Scene Success");
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {
                                                                    Log.d("SCENE_Laundry", errorMessage);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_Laundry", errorMessage);
                                                }
                                            });
                                }
                            },6000);

                        }
                    }
                }
            }
            if (PROJECT_VARIABLES.checkoutButton != 0 && PROJECT_VARIABLES.dndButton != 0) {
                if (getMainServiceSwitch() != null) {
                    if (getMainServiceSwitch().dps.get(String.valueOf(PROJECT_VARIABLES.checkoutButton)) != null && getMainServiceSwitch().dps.get(String.valueOf(PROJECT_VARIABLES.dndButton)) != null) {
                        if (searchScene(list, RoomNumber + "ServiceSwitchDNDScene4")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            BoolRule rule = BoolRule.newInstance("dp" + PROJECT_VARIABLES.dndButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(getMainServiceSwitch(), String.valueOf(PROJECT_VARIABLES.dndButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(PROJECT_VARIABLES.checkoutButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(getMainServiceSwitch().devId, taskMap);
                            tasks.add(task);
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                                            h.getHomeId(),
                                            RoomNumber + "ServiceSwitchDNDScene4", // The name of the scene.
                                            false,
                                            defaultImage,  // Indicates whether the scene is displayed on the homepage.
                                            condS, // The effective period. This parameter is optional.
                                            tasks, // The conditions.
                                            null,     // The tasks.
                                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                            new ITuyaResultCallback<SceneBean>() {
                                                @Override
                                                public void onSuccess(SceneBean sceneBean) {
                                                    Log.d("SCENE_DND2", "createScene Success");
                                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                            IResultCallback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Log.d("SCENE_DND2", "enable Scene Success");
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {
                                                                    Log.d("SCENE_DND2", errorMessage);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_DND2", errorMessage);
                                                }
                                            });
                                }
                            },8000);

                        }
                        if (searchScene(list, RoomNumber + "ServiceSwitchCheckoutScene")) {
                            List<SceneCondition> condS = new ArrayList<>();
                            List<SceneTask> tasks = new ArrayList<>();
                            BoolRule rule = BoolRule.newInstance("dp" + PROJECT_VARIABLES.checkoutButton, true);
                            SceneCondition cond = SceneCondition.createDevCondition(getMainServiceSwitch(), String.valueOf(PROJECT_VARIABLES.checkoutButton), rule);
                            condS.add(cond);
                            HashMap<String, Object> taskMap = new HashMap<>();
                            taskMap.put(String.valueOf(PROJECT_VARIABLES.dndButton), false); // Starts a device.
                            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(getMainServiceSwitch().devId, taskMap);
                            tasks.add(task);
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                                            h.getHomeId(),
                                            RoomNumber + "ServiceSwitchCheckoutScene", // The name of the scene.
                                            false,
                                            defaultImage,  // Indicates whether the scene is displayed on the homepage.
                                            condS, // The effective period. This parameter is optional.
                                            tasks, // The conditions.
                                            null,     // The tasks.
                                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                                            new ITuyaResultCallback<SceneBean>() {
                                                @Override
                                                public void onSuccess(SceneBean sceneBean) {
                                                    Log.d("SCENE_Laundry", "createScene Success");
                                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                                            IResultCallback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Log.d("SCENE_Laundry", "enable Scene Success");
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {
                                                                    Log.d("SCENE_Laundry", errorMessage);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_Laundry", errorMessage);
                                                }
                                            });
                                }
                            },10000);

                        }
                    }
                }
            }
        }
    }

    public void turnLightsOn() {
        // for samples only
        //TODO remove it for projects

//        if (isHasSwitch()) {
//            for (CheckinSwitch cs:switches) {
//                if (cs.dp1 != null) {
//                    cs.dp1.turnOn(new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//                    });
//                }
//                if (cs.dp2 != null) {
//                    cs.dp2.turnOn(new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//                    });
//                }
//            }
//        }
        Log.d("lightsOn","lights on function ");
        if (MyApp.My_PROJECT.projectName.equals("apiTest")) {
            Log.d("lightsOn","project done ");
            if (RoomNumber == 103) {
                Log.d("lightsOn","room done ");
                if (isHasSwitch()) {
                    for (CheckinSwitch cs : switches) {
                        if (cs.device.name.contains("Switch4")) {
                            Log.d("lightsOn","switch found done ");
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    cs.dp2.turnOn(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            Log.d("lightsOn",error);
                                        }

                                        @Override
                                        public void onSuccess() {
                                            Log.d("lightsOn","done ");
                                        }
                                    });
                                }
                            },5000);

                        }
                    }
                }
            }
        }
    }

    public void openCurtain() {
        if (isHasCurtain()) {
            for (CheckinCurtain cc : curtains) {
                cc.open(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {

                    }
                });
            }
        }
    }

    public void turnAcOn() {
        if (isHasAC()) {
            for (CheckinAC ac : acs) {
                ac.turnOn(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {

                    }
                });
            }
        }
    }

    public void setAcToSetPoint() {
        if (isHasAC()) {
            for (CheckinAC ac : acs) {
                ac.setTemperature(PROJECT_VARIABLES.Temp, new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Log.d("acScenario", error);
                    }

                    @Override
                    public void onSuccess() {
                        //Log.d("acScenario", "done");
                    }
                });
            }
        }
    }

    public void setAcToGuestTemperature() {
        if (isHasAC()) {
            for (CheckinAC ac : acs) {
                if (ac.clientSetTemp != null) {
                    if (!ac.clientSetTemp.equals(ac.setTempDp.current)) {
                        int v = Integer.parseInt(ac.clientSetTemp);
                        ac.setTemperature(v, new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("acScenario" , RoomNumber+" set client temp error "+error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("acScenario" , RoomNumber+" set client temp done");
                            }
                        });
                        Log.d("acScenario"+RoomNumber , RoomNumber+" temp return to guest temp ");
                    }
                }
            }
        }
    }

    public void setRoomOnline(boolean online) {
        if (online) {
            fireRoom.child("online").setValue(1);
        }
        else {
            fireRoom.child("online").setValue(0);
        }
    }

    public void setPowerStatus(int status) {
        fireRoom.child("powerStatus").setValue(status);
    }

    public void setRoomDoorStatus(int status) {
            fireRoom.child("doorStatus").setValue(status);
    }

    public void setRoomPowerStatus(int status) {
        fireRoom.child("powerStatus").setValue(status);
    }

    public void setRoomCurtainStatus(int status) {
        fireRoom.child("curtainStatus").setValue(status);
    }

    public void setClientIn(boolean status) {
        somebody = status;
        int val = 0;
        if (status) {
            val = 1;
        }
        fireRoom.child("ClientIn").setValue(val);
    }

    public static boolean searchScene (List<SceneBean> list , String name) {
        for (int i=0 ; i<list.size();i++) {
            if (list.get(i).getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public static void getAllRooms(RequestQueue R, GerRoomsCallback callback) {
        R.add(new StringRequest(Request.Method.GET, MyApp.My_PROJECT.url + getRoomsUrl, response -> {
            List<Room> rooms = new ArrayList<>();
            try {
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    rooms.add(new Room(arr.getJSONObject(i)));
                }
                callback.onSuccess(rooms);
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, error -> callback.onError(error.toString())));
    }

    public static void getAllRooms(PROJECT p,RequestQueue R, GerRoomsCallback callback) {
        R.add(new StringRequest(Request.Method.GET, p.url + getRoomsUrl, response -> {
            List<Room> rooms = new ArrayList<>();
            try {
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    rooms.add(new Room(arr.getJSONObject(i)));
                }
                callback.onSuccess(rooms);
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, error -> callback.onError(error.toString())));
    }
}
