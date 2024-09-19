package com.example.hotelservicesstandalone.Classes;

import com.example.hotelservicesstandalone.Classes.Property.Room;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientBackActions {

    boolean lights ;
    boolean curtain ;
    boolean ac ;

    public ClientBackActions (String actions) {
        if (actions != null) {
            try {
                JSONObject res = new JSONObject(actions);
                lights = res.getBoolean("lights");
                curtain = res.getBoolean("curtain");
                ac = res.getBoolean("ac");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(Room room) {
        if (lights) {
            room.turnLightsOn();
        }
        if (curtain) {
            room.openCurtain();
        }
        if (ac) {
            room.turnAcOn();
        }
    }
}
