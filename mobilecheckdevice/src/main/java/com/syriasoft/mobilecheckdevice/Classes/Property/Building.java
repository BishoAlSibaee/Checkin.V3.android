package com.syriasoft.mobilecheckdevice.Classes.Property;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetBuildingsCallback;
import com.syriasoft.mobilecheckdevice.Classes.LocalDataStore;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT;
import com.syriasoft.mobilecheckdevice.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Building {

    static final String getBuildingsUrl = "roomsManagement/getbuildings";

    int id;
    int projectId;
    int buildingNo;
    public String buildingName;
    int floorsNumber;
    public List<Floor> floorsList;


    public Building(int id, int projectId, int buildingNo, String buildingName, int floorsNumber) {
        this.id = id;
        this.projectId = projectId;
        this.buildingNo = buildingNo;
        this.buildingName = buildingName;
        this.floorsNumber = floorsNumber;
    }

    public Building(JSONObject jsonBuilding) throws JSONException {
            this.id = jsonBuilding.getInt("id");
            this.projectId = jsonBuilding.getInt("projectId");
            this.buildingNo = jsonBuilding.getInt("buildingNo");
            this.buildingName = jsonBuilding.getString("buildingName");
            this.floorsNumber = jsonBuilding.getInt("floorsNumber");
            floorsList = new ArrayList<>();
    }

    void setFloors(List<Floor> floors) {
        floorsList = new ArrayList<>();
        for (Floor f : floors) {
            if (f.building_id == id) {
                floorsList.add(f);
            }
        }
    }

    public static void getBuildings(RequestQueue queue, PropertyDB pDB, GetBuildingsCallback result) {
        if (pDB.isBuildingsInserted()) {
            Log.d("bootingOp","buildings locally");
            result.onSuccess(pDB.getBuildings());
        }
        else {
            Log.d("bootingOp","buildings internet");
            queue.add(new StringRequest(Request.Method.GET, MyApp.My_PROJECT.url+getBuildingsUrl, response -> {
                try {
                    List<Building> buildings = new ArrayList<>();
                    JSONArray arr = new JSONArray(response);
                    for (int i=0;i<arr.length();i++) {
                        JSONObject row = arr.getJSONObject(i);
                        buildings.add(new Building(row));
                    }
                    result.onSuccess(buildings);
                } catch (JSONException e) {
                    result.onError(e.getMessage());
                }
            }, error -> result.onError(error.toString())));
        }
    }

    public static void getBuildings(PROJECT p,RequestQueue queue, GetBuildingsCallback result) {
        queue.add(new StringRequest(Request.Method.GET, p.url+getBuildingsUrl, response -> {
            try {
                List<Building> buildings = new ArrayList<>();
                JSONArray arr = new JSONArray(response);
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    buildings.add(new Building(row));
                }
                result.onSuccess(buildings);
            } catch (JSONException e) {
                result.onError(e.getMessage());
            }
        }, error -> result.onError(error.toString())));
    }

    public static void saveBuildingsToStorage(LocalDataStore storage,List<Building> buildings) {
        storage.saveInteger(buildings.size(),"buildingsCount");
        for (int i=0;i<buildings.size();i++) {
            storage.saveObject(buildings.get(i),"building"+i);
        }
    }

    public static List<Building> getBuildingsFromStorage(LocalDataStore storage) {
        List<Building> buildings = new ArrayList<>();
        int count = storage.getInteger("buildingsCount");
        for (int i=0;i<count;i++) {
            buildings.add((Building) storage.getObject("building"+i,Building.class));
        }
        return buildings;
    }

    public static void deleteBuildingsFromLocalStorage(LocalDataStore storage) {
        int count = storage.getInteger("buildingsCount");
        for (int i=0;i<count;i++) {
            storage.deleteObject("building"+i);
        }
        storage.deleteObject("buildingsCount");
    }

    public static void setBuildingsFloors(List<Building> buildings, List<Floor> floors) {
        for (Building b : buildings) {
            for (Floor f : floors) {
                if (f.building_id == b.id) {
                    b.floorsList.add(f);
                }
            }
        }
    }
}
