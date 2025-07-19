package com.syriasoft.server.Classes;

import android.util.Log;

import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.sdk.api.IResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
                Log.d("clientBack", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    public void start(Room room) {
        Log.d("clientBack"+room.RoomNumber,"start");
        room.powerOnRoom(new IResultCallback() {
            @Override
            public void onError(String code, String error) {

            }

            @Override
            public void onSuccess() {
                Log.d("clientBack"+room.RoomNumber,"power done");
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (lights) {
                            room.turnLightsOn();
                            Log.d("clientBack"+room.RoomNumber,"lights done ");
                        }
                        if (curtain) {
                            room.openCurtain();
                        }
                        if (ac) {
                            room.turnAcOn();
                        }
                    }
                },15000);

                room.powerByCardAfterMinutes(2, new RequestCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
            }
        });

    }
}
