package com.syriasoft.checkin.Classes.Property;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.checkin.Classes.Interfaces.GetBuildingsCallback;

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
    String buildingName;
    int floorsNumber;

    List<Floor> floorsList;


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
    }

    void setFloors(List<Floor> floors) {
        this.floorsList = floors;
    }

    public static void getBuildings(RequestQueue queue,GetBuildingsCallback result,String projectUrl) {
        queue.add(new StringRequest(Request.Method.GET, projectUrl+getBuildingsUrl, response -> {
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
