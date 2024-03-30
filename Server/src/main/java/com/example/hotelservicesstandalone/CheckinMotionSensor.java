package com.example.hotelservicesstandalone;

import android.util.Log;

import com.tuya.smart.android.device.bean.DeviceDpInfoBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CheckinMotionSensor extends CheckinRoomDevice{
    boolean isOnline;
    int my_battery;
    DeviceBean me;
    String motion_dp;
    String battery_dp;
    ITuyaDevice IT_Device;

    CheckinMotionSensor(ROOM room,DeviceBean device) {
        this.my_room = room;
        this.me = device;
        Name = my_room.RoomNumber+"MotionSensor";
        IT_Device = TuyaHomeSdk.newDeviceInstance(device.devId);
        TuyaHomeSdk.getDeviceMultiControlInstance().getDeviceDpInfoList(me.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
            @Override
            public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                Log.d("motionSensor"+my_room.RoomNumber, "_______________________________________");
                Log.d("motionSensor"+my_room.RoomNumber,me.dps.toString());
                Log.d("motionSensor"+my_room.RoomNumber,"results: "+result.size());
                for (DeviceDpInfoBean tb:result) {
                    Log.d("motionSensor"+my_room.RoomNumber,"name: "+tb.getName()+" dpId: "+tb.getDpId());
                    if (tb.getName().equals("State") || tb.getName().equals("PIR state") || tb.getName().contains("State") || tb.getName().contains("state")) {
                        motion_dp = tb.getDpId();
                    }
                    else if (tb.getName().equals("Battery") || tb.getName().equals("battery") || tb.getName().contains("Battery") || tb.getName().contains("battery")) {
                        battery_dp = tb.getDpId();
                    }
                }
                Log.d("motionSensor"+my_room.RoomNumber,"motionDp : "+motion_dp+" batteryDp: "+battery_dp);
                Log.d("motionSensor"+my_room.RoomNumber, "_______________________________________");
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }

    void setMotionSensorActions(MotionSensorInterface callback) {
        IT_Device.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                if (dpStr != null) {
                    if (dpStr.get(motion_dp) != null) {
                        callback.onMotion();
                    }
                    if (dpStr.get(battery_dp) != null) {
                        my_battery = Integer.parseInt(Objects.requireNonNull(dpStr.get(battery_dp)).toString());
                        callback.onBatteryChange(my_battery);
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                isOnline = online ;
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

interface MotionSensorInterface {
    void onMotion();
    void onOnlineChange(boolean online);
    void onBatteryChange(int battery);
}
