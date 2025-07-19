package com.syriasoft.mobilecheckdevice.Classes.Property;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetFloorsCallback;
import com.syriasoft.mobilecheckdevice.Classes.LocalDataStore;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT;
import com.syriasoft.mobilecheckdevice.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Floor {

    static final String getFloorsUrl = "roomsManagement/getfloors";

    int id;
    int building_id;
    public int floorNumber;
    int rooms;
    public List<Room> roomsList;
    public Building building;

    public Floor(int id, int building_id, int floorNumber, int rooms) {
        this.id = id;
        this.building_id = building_id;
        this.floorNumber = floorNumber;
        this.rooms = rooms;
    }

    public Floor(JSONObject jsonFloor) throws JSONException {
            this.id = jsonFloor.getInt("id");
            this.building_id = jsonFloor.getInt("building_id");
            this.floorNumber = jsonFloor.getInt("floorNumber");
            this.rooms = jsonFloor.getInt("rooms");
            roomsList = new ArrayList<>();
    }

    public static void getFloors(RequestQueue queue, PropertyDB pDB, GetFloorsCallback result) {
        if (pDB.isFloorsInserted()) {
            Log.d("bootingOp","floors locally");
            result.onSuccess(pDB.getFloors());
        }
        else {
            Log.d("bootingOp","buildings internet");
            queue.add(new StringRequest(Request.Method.GET, MyApp.My_PROJECT.url+getFloorsUrl, response -> {
                try {
                    List<Floor> floors = new ArrayList<>();
                    JSONArray arr = new JSONArray(response);
                    for (int i=0;i<arr.length();i++) {
                        JSONObject row = arr.getJSONObject(i);
                        floors.add(new Floor(row));
                    }
                    result.onSuccess(floors);
                } catch (JSONException e) {
                    result.onError(e.getMessage());
                }
            }, error -> result.onError(error.toString())));
        }
    }

    public void setFloorRooms(List<Room> rooms) {
        roomsList = new ArrayList<>();
        for (Room r: rooms) {
            if (r.floor_id == id ) {
                roomsList.add(r);
            }
        }
    }

    public void setFloorBuilding(List<Building> buildings) {
        for (Building b : buildings) {
            if (b.id == building_id) {
                building = b;
                break;
            }
        }
    }

    public static void getFloors(PROJECT p,RequestQueue queue, GetFloorsCallback result) {
        queue.add(new StringRequest(Request.Method.GET, p.url+getFloorsUrl, response -> {
            try {
                List<Floor> floors = new ArrayList<>();
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    floors.add(new Floor(row));
                }
                result.onSuccess(floors);
            } catch (JSONException e) {
                result.onError(e.getMessage());
            }
        }, error -> result.onError(error.toString())));
    }

    public static void saveFloorsToStorage(LocalDataStore storage, List<Floor> floors) {
        storage.saveInteger(floors.size(),"floorsCount");
        for (int i=0;i<floors.size();i++) {
            storage.saveObject(floors.get(i),"floor"+i);
        }
    }

    public static List<Floor> getFloorsFromStorage(LocalDataStore storage) {
        List<Floor> floors = new ArrayList<>();
        int count = storage.getInteger("floorsCount");
        for (int i=0;i<count;i++) {
            floors.add((Floor) storage.getObject("floor"+i,Floor.class));
        }
        return floors;
    }

    public static void deleteFloorsFromLocalStorage(LocalDataStore storage) {
        int count = storage.getInteger("floorsCount");
        for (int i=0;i<count;i++) {
            storage.deleteObject("floor"+i);
        }
        storage.deleteObject("floorsCount");
    }

    public static void setFloorsRooms(List<Floor> floors, List<Room> rooms) {
        for (Floor f : floors) {
            for (Room r : rooms) {
                if (r.floor_id == f.id) {
                    f.roomsList.add(r);
                }
            }
        }
    }
}
