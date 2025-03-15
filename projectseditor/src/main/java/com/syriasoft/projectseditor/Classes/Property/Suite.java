package com.syriasoft.projectseditor.Classes.Property;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.syriasoft.projectseditor.Classes.LocalDataStore;
import com.syriasoft.projectseditor.Classes.PROJECT;
import com.syriasoft.projectseditor.Interfaces.GetSuitesCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Suite extends Bed {

    static String getSuitesUrl = "roomsManagement/getSuites";
    public int id;
    public int SuiteNumber;
    public String Rooms;
    public String RoomsId;
    public int Building;
    public int BuildingId;
    public int Floor;
    public int FloorId;
    public int Status;
    public List<Room> RoomsList;
    Building building;
    Floor floor;

    public DatabaseReference fireSuite,devicesControlReference,devicesDataReference;


    public Suite(int id, int suiteNumber, String rooms, String roomsId, int building, int buildingId, int floor, int floorId, int status) {
        this.id = id;
        SuiteNumber = suiteNumber;
        Rooms = rooms;
        RoomsId = roomsId;
        Building = building;
        BuildingId = buildingId;
        Floor = floor;
        FloorId = floorId;
        Status = status;
        RoomsList = new ArrayList<>();
    }

    public Suite(JSONObject object) {
        try {
            this.id = object.getInt("id");
            SuiteNumber = object.getInt("SuiteNumber");
            Rooms = object.getString("Rooms");
            RoomsId = object.getString("RoomsId");
            Building = object.getInt("Building");
            BuildingId = object.getInt("BuildingId");
            Floor = object.getInt("Floor");
            FloorId = object.getInt("FloorId");
            Status = object.getInt("Status");
            RoomsList = new ArrayList<>();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSuiteBuilding(List<Building> buildings) {
        for (Building b:buildings) {
            if (b.id == BuildingId) {
                building = b;
                break;
            }
        }
    }

    public void setSuiteFloor(List<Floor> floors) {
        for (Floor f:floors) {
            if (f.id == FloorId) {
                floor = f;
                break;
            }
        }
    }

    public void setFireRoom(FirebaseDatabase database, PROJECT project) {
        fireSuite = database.getReference(project.projectName+"/B"+building.buildingNo+"/F"+floor.floorNumber+"/S"+SuiteNumber);
//        fireSuite.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child("DND").getValue() != null) {
//                    DND = Long.parseLong(Objects.requireNonNull(snapshot.child("DND").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("DND").setValue(0);
//                }
//                if (snapshot.child("Cleanup").getValue() != null) {
//                    Cleanup = Long.parseLong(Objects.requireNonNull(snapshot.child("Cleanup").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("Cleanup").setValue(0);
//                }
//                if (snapshot.child("Laundry").getValue() != null) {
//                    Laundry = Long.parseLong(Objects.requireNonNull(snapshot.child("Laundry").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("Laundry").setValue(0);
//                }
//                if (snapshot.child("RoomService").getValue() != null) {
//                    RoomService = Long.parseLong(Objects.requireNonNull(snapshot.child("RoomService").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("RoomService").setValue(0);
//                }
//                if (snapshot.child("SOS").getValue() != null) {
//                    SOS = Long.parseLong(Objects.requireNonNull(snapshot.child("SOS").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("SOS").setValue(0);
//                }
//                if (snapshot.child("Checkout").getValue() != null) {
//                    Checkout = Long.parseLong(Objects.requireNonNull(snapshot.child("Checkout").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("Checkout").setValue(0);
//                }
//                if (snapshot.child("Restaurant").getValue() != null) {
//                    Restaurant = Long.parseLong(Objects.requireNonNull(snapshot.child("Restaurant").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("Restaurant").setValue(0);
//                }
//                if (snapshot.child("ClientIn").getValue() != null) {
//                    ClientIn = Integer.parseInt(Objects.requireNonNull(snapshot.child("ClientIn").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("ClientIn").setValue(0);
//                }
//                if (snapshot.child("selected").getValue() != null) {
//                    selected = Integer.parseInt(Objects.requireNonNull(snapshot.child("selected").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("selected").setValue(0);
//                }
//                if (snapshot.child("loading").getValue() != null) {
//                    loading = Integer.parseInt(Objects.requireNonNull(snapshot.child("loading").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("loading").setValue(0);
//                }
//                if (snapshot.child("online").getValue() != null) {
//                    online = Integer.parseInt(Objects.requireNonNull(snapshot.child("online").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("online").setValue(0);
//                }
//                if (snapshot.child("powerStatus").getValue() != null) {
//                    powerStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("powerStatus").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("powerStatus").setValue(0);
//                }
//                if (snapshot.child("curtainStatus").getValue() != null) {
//                    curtainStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("curtainStatus").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("curtainStatus").setValue(0);
//                }
//                if (snapshot.child("doorStatus").getValue() != null) {
//                    doorStatus = Integer.parseInt(Objects.requireNonNull(snapshot.child("doorStatus").getValue()).toString());
//                }
//                else {
//                    fireRoom.child("doorStatus").setValue(0);
//                }
//                if (snapshot.child("reservationType").getValue() != null) {
//                    byLink = Integer.parseInt(Objects.requireNonNull(snapshot.child("reservationType").getValue()).toString()) == 1;
//                }
//                else {
//                    fireRoom.child("reservationType").setValue(0);
//                }
//                if (snapshot.child("RoomServiceText").getValue() != null) {
//                    RoomServiceText = Objects.requireNonNull(snapshot.child("RoomServiceText").getValue()).toString();
//                }
//                else {
//                    fireRoom.child("RoomServiceText").setValue(0);
//                }
//                if (snapshot.child("dep").getValue() != null) {
//                    dep = Objects.requireNonNull(snapshot.child("dep").getValue()).toString();
//                }
//                else {
//                    fireRoom.child("dep").setValue(0);
//                }
//                if (snapshot.child("message").getValue() != null) {
//                    message = Objects.requireNonNull(snapshot.child("message").getValue()).toString();
//                }
//                else {
//                    fireRoom.child("dep").setValue("");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    void setMyRooms(List<Room> rooms) {
        if (RoomsId != null) {
            String[] roomsIdsArray = RoomsId.split("-");
            for (String s : roomsIdsArray) {
                for (int j = 0; j < rooms.size(); j++) {
                    if (Integer.parseInt(s) == rooms.get(j).id) {
                        RoomsList.add(rooms.get(j));
                    }
                }
            }
        }
    }

    public void setRoomBuilding(List<Building> buildings) {
        for (Building b : buildings) {
            if (b.id == BuildingId) {
                building = b;
                break;
            }
        }
    }

    public void setRoomFloor(List<Floor> floors) {
        for (Floor f:floors) {
            if (f.id == FloorId) {
                floor = f;
                break;
            }
        }
    }

//    public List<CheckinDevice> setMyDevices(List<DeviceBean> devices, HomeBean homeBean) {
//        List<CheckinDevice> devs = new ArrayList<>();
//        home = homeBean;
//        for (int i=0;i<devices.size();i++) {
//            DeviceBean d = devices.get(i);
//            if (d.getName().contains(DeviceTypes.Gateway.toString()+SuiteNumber) || d.getName().equals("Suite"+SuiteNumber+"ZGatway") || d.getName().contains("Suite"+SuiteNumber+"Gatway") || d.getName().contains("Suite"+SuiteNumber+"Gateway")) {
//                if (gateways == null) {
//                    gateways = new ArrayList<>();
//                }
//                CheckinGateway g = new CheckinGateway(d,this);
//                gateways.add(g);
//                devs.add(g);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.Power)) {
//                CheckinPower p = new CheckinPower(d,this);
//                power = p;
//                devs.add(p);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.Lock)) {
//                if (locks == null) {
//                    locks = new ArrayList<>();
//                }
//                CheckinLock l = new CheckinLock(d,this);
//                locks.add(l);
//                devs.add(l);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.Service+"Switch") || d.getName().equals("Suite"+SuiteNumber+DeviceTypes.Service)) {
//                Log.d("serviceProb",SuiteNumber+" found");
//                if (services == null) {
//                    services = new ArrayList<>();
//                }
//                CheckinServiceSwitch ss = new CheckinServiceSwitch(d,this);
//                services.add(ss);
//                devs.add(ss);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.MotionSensor)) {
//                if (motionSensors == null) {
//                    motionSensors = new ArrayList<>();
//                }
//                CheckinMotionSensor mm = new CheckinMotionSensor(d,this);
//                motionSensors.add(mm);
//                devs.add(mm);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.Switch)) {
//                if (switches == null) {
//                    switches = new ArrayList<>();
//                }
//                CheckinSwitch sw = new CheckinSwitch(d,this);
//                switches.add(sw);
//                devs.add(sw);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.DoorSensor)) {
//                if (doorSensors == null) {
//                    doorSensors = new ArrayList<>();
//                }
//                CheckinDoorSensor ds = new CheckinDoorSensor(d,this);
//                doorSensors.add(ds);
//                devs.add(ds);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.Curtain)) {
//                if (curtains == null) {
//                    curtains = new ArrayList<>();
//                }
//                CheckinCurtain cc = new CheckinCurtain(d,this);
//                curtains.add(cc);
//                devs.add(cc);
//            }
//            else if (d.getName().equals("Suite"+SuiteNumber+DeviceTypes.AC)) {
//                if (acs == null) {
//                    acs = new ArrayList<>();
//                }
//                CheckinAC ca = new CheckinAC(d,this);
//                acs.add(ca);
//                devs.add(ca);
//            }
//        }
//        SuitesDevices.addAll(devs);
//        Log.d("serviceProb",SuiteNumber+" "+devs.size());
//        return devs;
//    }
//
//    public List<CheckinDevice> getMyDevices() {
//        List<CheckinDevice> devices = new ArrayList<>();
//        if (isHasPower()) {
//            devices.add(getPowerModule());
//        }
//        if (isHasAC()) {
//            devices.addAll(acs);
//        }
//        if (isHasMotion()) {
//            devices.addAll(motionSensors);
//        }
//        if (isHasCurtain()) {
//            devices.addAll(curtains);
//        }
//        if (isHasLock()) {
//            devices.addAll(locks);
//        }
//        if (isHasServiceSwitch()) {
//            devices.addAll(services);
//        }
//        if (isHasDoorSensor()) {
//            devices.addAll(doorSensors);
//        }
//        if (isHasGateway()) {
//            devices.addAll(gateways);
//        }
//        if (isHasSwitch()) {
//            devices.addAll(switches);
//        }
//        return devices;
//    }
//
//    public CheckinLock getRoomLock() {
//        return locks.get(0);
//    }
//
//    public boolean isHasAC() {
//        return acs != null && !acs.isEmpty();
//    }
//
//    public boolean isHasPower() {
//        return power != null;
//    }
//    public CheckinPower getPowerModule() {
//        return power;
//    }
//
//    public boolean isHasGateway() {
//        return gateways != null && !gateways.isEmpty();
//    }
//    public CheckinGateway getSuiteGateway() {
//        if (isHasGateway()) {
//            return gateways.get(0);
//        }
//        return null;
//    }
//
//    public boolean isHasLock() {
//        return locks != null && !locks.isEmpty();
//    }
//
//    public boolean isHasMotion() {
//        return motionSensors != null && !motionSensors.isEmpty();
//    }
//
//    public boolean isHasDoorSensor() {
//        return doorSensors != null && !doorSensors.isEmpty();
//    }
//    public CheckinDoorSensor getMainRoomDoorSensor() {
//        return doorSensors.get(0);
//    }
//
//    public boolean isHasServiceSwitch() {
//        return services != null && !services.isEmpty();
//    }
//    public CheckinServiceSwitch getMainServiceSwitch() {
//        if (isHasServiceSwitch()) {
//            return services.get(0);
//        }
//        return null;
//    }
//
//    public boolean isHasSwitch() {
//        return switches != null && !switches.isEmpty();
//    }
//
//    public boolean isHasCurtain() {
//        return curtains != null && !curtains.isEmpty();
//    }
//    public List<CheckinCurtain> getCurtains() {
//        return curtains;
//    }
//
//    public void powerOnSuite(IResultCallback result) {
//        if (power == null) {
//            result.onError("no code","room has no power module");
//        }
//        else {
//            power.powerOn(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }
//
//    public void powerByCardSuite(IResultCallback result) {
//        if (power == null) {
//            result.onError("no code","room has no power module");
//        }
//        else {
//            power.powerByCard(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }
//
//    public void powerByCardAfterMinutes(int minutes, RequestCallback callback) {
//        if (power != null) {
//            Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    powerByCardSuite(new IResultCallback() {
//                        @Override
//                        public void onSuccess() {
//                            callback.onSuccess();
//                        }
//
//                        @Override
//                        public void onError(String code,String error) {
//                            callback.onFail(error);
//                        }
//                    });
//                }
//            }, (long) minutes * 60 * 1000);
//        }
//    }
//
//    public void powerOffSuite(IResultCallback result) {
//        if (power == null) {
//            result.onError("no code","no power modules installed in this room");
//        }
//        else {
//            power.powerOff(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }
//
//    public void powerOffAfterMinutes(int minutes, RequestCallback callback) {
//        if (power != null) {
//            Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    powerOffSuite(new IResultCallback() {
//                        @Override
//                        public void onSuccess() {
//                            callback.onSuccess();
//                        }
//
//                        @Override
//                        public void onError(String code,String error) {
//                            callback.onFail(error);
//                        }
//                    });
//                }
//            }, (long) minutes * 60 * 1000);
//        }
//    }
//
//    public void cleanupSuiteOff(IResultCallback result) {
//        if (services == null || services.isEmpty()) {
//            result.onError("no code","no service switch installed in this room");
//        }
//        else {
//            getMainServiceSwitch().cleanupOff(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }
//
//    public void laundrySuiteOff(IResultCallback result) {
//        if (services == null || services.isEmpty()) {
//            result.onError("no code","no service switch installed in this room");
//        }
//        else {
//            getMainServiceSwitch().laundryOff(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }
//
//    public void dndSuiteOff(IResultCallback result) {
//        if (services == null || services.isEmpty()) {
//            result.onError("no code","no service switch installed in this room");
//        }
//        else {
//            getMainServiceSwitch().dndOff(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }
//
//    public void checkoutSuiteOff(IResultCallback result) {
//        if (services == null || services.isEmpty()) {
//            result.onError("no code","no service switch installed in this room");
//        }
//        else {
//            getMainServiceSwitch().checkoutOff(new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    result.onError(code,error);
//                }
//
//                @Override
//                public void onSuccess() {
//                    result.onSuccess();
//                }
//            });
//        }
//    }

    public void setSuiteOnline(boolean online) {
        if (online) {
            fireSuite.child("online").setValue(1);
        }
        else {
            fireSuite.child("online").setValue(0);
        }
    }

    public void setPowerStatus(int status) {
        fireSuite.child("powerStatus").setValue(status);
    }

    public static void getSuites(String projectUrl,RequestQueue Q, GetSuitesCallBack callBack) {
        List<Suite> suites = new ArrayList<>();
        Q.add(new StringRequest(Request.Method.GET,  projectUrl+getSuitesUrl, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    suites.add(new Suite(arr.getJSONObject(i)));
                }
                callBack.onSuccess(suites);
            } catch (JSONException e) {
                callBack.onError(e.getMessage());
            }
        }, error -> callBack.onError(error.toString())));
    }
    public static void setSuitesBuildingsAndFloors(List<Suite> suites,List<Building> buildings,List<Floor> floors) {
        for (Suite s :suites) {
            s.setRoomBuilding(buildings);
            s.setRoomFloor(floors);
        }
    }
    public static void setSuitesFireSuites(List<Suite> suites,PROJECT project,FirebaseDatabase database) {
        for (Suite s :suites) {
            s.setFireRoom(database,project);
        }
    }
    public static void saveSuitesToStorage(LocalDataStore storage, List<Suite> suites) {
        storage.saveInteger(suites.size(),"suitesCount");
        for (int i=0;i<suites.size();i++) {
            storage.saveObject(suites.get(i),"suite"+i);
        }
    }

    public static List<Suite> getSuitesFromStorage(LocalDataStore storage) {
        List<Suite> suites = new ArrayList<>();
        int count = storage.getInteger("suitesCount");
        for (int i=0;i<count;i++) {
            suites.add((Suite) storage.getObject("suite"+i,Suite.class));
        }
        return suites;
    }

    public static void deleteSuitesFromLocalStorage(LocalDataStore storage) {
        int count = storage.getInteger("suitesCount");
        for (int i=0;i<count;i++) {
            storage.deleteObject("suite"+i);
        }
        storage.deleteObject("suitesCount");
    }

//    public static List<CheckinDevice> setSuitesDevices(List<Suite> suites,List<DeviceBean> devices,HomeBean homeBean) {
//        List<CheckinDevice> devs = new ArrayList<>();
//        for (Suite s:suites) {
//            devs.addAll(s.setMyDevices(devices,homeBean));
//        }
//        return devs;
//    }
}