package com.syriasoft.checkin.Classes.Property;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.checkin.Classes.Interfaces.GetFacilitiesCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract  class Facility {
    int id;
    int TypeId;
    String TypeName;
    String Name;
    String photo;

    abstract Facility get();

    public static void getFacilities(String projectUrl, RequestQueue Q,GetFacilitiesCallback callback) {
        List<Facility> facilities = new ArrayList<>();
        String url = projectUrl + "facilitys/getfacilitys";
        StringRequest laundryRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    if (row.getString("TypeName").equals("Restaurant") || row.getString("TypeName").equals("CoffeeShop")) {
                        facilities.add(new Restaurant(row.getInt("id"), row.getInt("TypeId"), row.getString("TypeName"), row.getString("Name"), row.getString("photo"),row.getInt("Control")));
                    }
                }
                callback.onSuccess(facilities);
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, error -> callback.onError(error.toString()));
        Q.add(laundryRequest);
    }
}
