package com.example.hotelservicesstandalone.Classes.Property;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.hotelservicesstandalone.Classes.Interfaces.GetFloorsCallback;
import com.example.hotelservicesstandalone.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Floor {

    static final String getFloorsUrl = "roomsManagement/getfloors";

    int id;
    int building_id;
    int floorNumber;
    int rooms;

    List<Room> roomsList;

    public Floor(int id, int building_id, int floorNumber, int rooms, List<Room> roomsList) {
        this.id = id;
        this.building_id = building_id;
        this.floorNumber = floorNumber;
        this.rooms = rooms;
        this.roomsList = roomsList;
    }

    public Floor(JSONObject jsonFloor) throws JSONException {
            this.id = jsonFloor.getInt("id");
            this.building_id = jsonFloor.getInt("building_id");
            this.floorNumber = jsonFloor.getInt("floorNumber");
            this.rooms = jsonFloor.getInt("rooms");
    }

    public static void getFloors(RequestQueue queue, GetFloorsCallback result) {
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
