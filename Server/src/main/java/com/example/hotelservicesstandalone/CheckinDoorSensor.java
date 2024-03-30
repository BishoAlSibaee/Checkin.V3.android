package com.example.hotelservicesstandalone;

import android.util.Log;

import com.tuya.smart.android.device.bean.DeviceDpInfoBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CheckinDoorSensor extends CheckinRoomDevice {
    boolean my_status;
    boolean isOnline;
    int my_battery;
    DeviceBean me;
    String status_dp;
    String battery_dp;
    ITuyaDevice IT_Device;

    CheckinDoorSensor(ROOM room,DeviceBean device) {
        this.my_room = room;
        this.me = device;
        Name = my_room.RoomNumber+"DoorSensor";
        IT_Device = TuyaHomeSdk.newDeviceInstance(device.devId);
        TuyaHomeSdk.getDeviceMultiControlInstance().getDeviceDpInfoList(me.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
            @Override
            public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                Log.d("doorSensor"+my_room.RoomNumber, "_______________________________________");
                Log.d("doorSensor"+my_room.RoomNumber,me.dps.toString());
                Log.d("doorSensor"+my_room.RoomNumber,"results: "+result.size());
                for (DeviceDpInfoBean tb:result) {
                    Log.d("doorSensor"+my_room.RoomNumber,"name: "+tb.getName()+" dpId: "+tb.getDpId());
                    if (tb.getName().equals("Door and window sensor") || tb.getName().equals("Door Sensor") || tb.getName().equals("") || tb.getName().contains("Sensor") ) {
                        status_dp = tb.getDpId();
                    }
                    else if (tb.getName().equals("Battery level") || tb.getName().equals("Battery Level") || tb.getName().contains("Battery") || tb.getName().contains("battery")) {
                        battery_dp = tb.getDpId();
                    }
                }
                Log.d("doorSensor"+my_room.RoomNumber,"statusDp : "+status_dp+" batteryDp: "+battery_dp);
                Log.d("doorSensor"+my_room.RoomNumber, "_______________________________________");
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }

    void setDoorSensorActions(DoorSensorInterface callback) {
        final long[] lastAction = {Calendar.getInstance(Locale.getDefault()).getTimeInMillis()};
        IT_Device.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                if (dpStr != null) {
                    long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    if (now > (lastAction[0] + 1000)) {
                        lastAction[0] = now ;
                        if (dpStr.length() < 15) {
                            try {
                                JSONObject action = new JSONObject(dpStr);
                                if (Objects.requireNonNull(action.get(status_dp)).toString().contains("Open") || Objects.requireNonNull(action.get(status_dp)).toString().contains("open") || Objects.requireNonNull(action.get(status_dp)).toString().contains("true")) {
                                    my_status = true;
                                    callback.onDoorOpen();
                                }
                                else if (Objects.requireNonNull(action.get(status_dp)).toString().contains("closed") || Objects.requireNonNull(action.get(status_dp)).toString().contains("Closed") || Objects.requireNonNull(action.get(status_dp)).toString().contains("false")) {
                                    my_status = false;
                                    callback.onDoorClosed();
                                }
                                else {
                                    try {
                                        my_battery = Integer.parseInt(Objects.requireNonNull(action.get(status_dp)).toString());
                                        callback.onBatteryChange(my_battery);
                                    }
                                    catch (Exception e) {
                                        Log.d("DoorSensorError","error: "+e.getMessage());
                                    }
                                }

                            }
                            catch (JSONException e) {
                                Log.d("DoorSensorError","error: "+e.getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                isOnline = online;
                callback.onOnlineChange(online);
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });
    }
}

interface DoorSensorInterface {
    void onDoorOpen();
    void onDoorClosed();
    void onOnlineChange(boolean online);
    void onBatteryChange(int battery);
}