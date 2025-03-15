package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.DoorListener;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.SetInitialValues;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckinDoorSensor extends CheckinDevice implements SetInitialValues, Listen {

    private final String[] statusNames = {"Door and window sensor","Door Sensor","门窗开关"};
    private final String[] batteryNames = {"Battery Level"};

    DeviceDPBool statusDp;
    DeviceDPValue batteryDp;

    int Battery;

    public CheckinDoorSensor(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinDoorSensor(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        if (my_room != null) {
            my_room.fireRoom.child("DoorSensor").setValue(1);
        }
        List<String> statuses = Arrays.asList(statusNames);
        for (DeviceDP dp:deviceDPS) {
            if (statuses.contains(dp.dpName)) {
                statusDp = (DeviceDPBool) dp;
                break;
            }
        }
        List<String> batteries = Arrays.asList(batteryNames);
        for (DeviceDP dp:deviceDPS) {
            if (batteries.contains(dp.dpName)) {
                batteryDp = (DeviceDPValue) dp;
                break;
            }
        }
        if (statusDp != null) {
            statusDp.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(statusDp.dpId))).toString());
            if (statusDp.current) {
                my_room.fireRoom.child("doorStatus").setValue(1);
            }
            else {
                my_room.fireRoom.child("doorStatus").setValue(0);
            }
        }
        if (batteryDp != null) {
            batteryDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(batteryDp.dpId))).toString();
        }
        callback.onSuccess();
    }

    @Override
    public void listen(DeviceAction action) {
        DoorListener door = (DoorListener) action;
        this.control.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                Log.d("doorAction",dpStr);
                if (statusDp != null) {
                    if (dpStr.length() < 12) {
                        try {
                            JSONObject action = new JSONObject(dpStr);
                            boolean status = Boolean.parseBoolean(action.getString(String.valueOf(statusDp.dpId)));
                            statusDp.current = status;
                            if (status) {
                                door.open();
                            } else {
                                door.close();
                            }

                        } catch (JSONException e) {
                            Log.d("doorAction", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                }
                if (batteryDp != null) {
                    try {
                        JSONObject action = new JSONObject(dpStr);
                        int battery = Integer.parseInt(action.getString(String.valueOf(batteryDp.dpId)));
                        Battery = battery;
                        door.battery(battery);
                    } catch (JSONException e) {
                        Log.d("doorAction", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                door.online(online);
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });
    }

    @Override
    public void unListen() {
        this.control.unRegisterDevListener();
    }
}
