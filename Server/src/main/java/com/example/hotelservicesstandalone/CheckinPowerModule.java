package com.example.hotelservicesstandalone;

import android.util.Log;

import com.tuya.smart.android.device.bean.DeviceDpInfoBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.TYDevicePublishModeEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class CheckinPowerModule extends CheckinRoomDevice{
    boolean isOnline;
    DeviceBean me;
    String first_dp;
    String second_dp;
    String first_countDown_dp;
    String second_countDown_dp;
    ITuyaDevice IT_Device;

    CheckinPowerModule(ROOM room,DeviceBean device) {
        this.my_room = room;
        this.me = device;
        Name = my_room.RoomNumber+"Power";
        IT_Device = TuyaHomeSdk.newDeviceInstance(device.devId);
        TuyaHomeSdk.getDeviceMultiControlInstance().getDeviceDpInfoList(me.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
            @Override
            public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                Log.d("powerModule"+my_room.RoomNumber, "_______________________________________");
                Log.d("powerModule"+my_room.RoomNumber,me.dps.toString());
                Log.d("powerModule"+my_room.RoomNumber,"results: "+result.size());
                for (DeviceDpInfoBean tb:result) {
                    Log.d("powerModule"+my_room.RoomNumber,"name: "+tb.getName()+" dpId: "+tb.getDpId());
                    if (tb.getName().equals("Switch") || tb.getName().equals("Switch 1") || tb.getName().equals("Switch1") || tb.getName().equals("switch 1")) {
                        first_dp = tb.getDpId();
                    }
                    else if (tb.getName().equals("Switch 2")) {
                        second_dp = tb.getDpId();
                    }
                    else if (tb.getName().equals("countdown1") || tb.getName().equals("Countdown 1")) {
                        first_countDown_dp = tb.getDpId();
                    }
                    else if (tb.getName().equals("countdown2") || tb.getName().equals("Countdown 2")) {
                        second_countDown_dp = tb.getDpId();
                    }
                }
                Log.d("powerModule"+my_room.RoomNumber,"firstDp : "+first_dp+" secondDp: "+second_dp);
                Log.d("powerModule"+my_room.RoomNumber, "_______________________________________");
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }

    void setPowerModuleActions(PowerModuleInterface callback) {
        final boolean[] v1 = {Boolean.parseBoolean(Objects.requireNonNull(me.dps.get(first_dp)).toString())};
        final boolean[] v2 = {Boolean.parseBoolean(Objects.requireNonNull(me.dps.get(second_dp)).toString())};
        IT_Device.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                if (dpStr != null) {
                    try {
                        JSONObject l1 = new JSONObject(dpStr);
                        v1[0] = l1.getBoolean(first_dp);
                    }
                    catch(JSONException e) {
                        Log.d("powerActions","l1 error "+e.getMessage());
                    }
                    try {
                        JSONObject l2 = new JSONObject(dpStr);
                        v2[0] = l2.getBoolean(second_dp);
                    }
                    catch(JSONException e) {
                        Log.d("powerActions","l2 error "+e.getMessage());
                    }
                    if (v1[0] && v2[0]) {
                        callback.onPowerOn();
                    }
                    else if (v1[0]) {
                        callback.onPowerByCard();
                    }
                    else {
                        callback.onPowerOff();
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {

            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });
    }

    void turnPowerOff(turnPowerOff callback) {
        IT_Device.publishDps("{\""+first_dp+"\": false,\""+second_dp+"\": false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFail(error+" "+code);
            }

            @Override
            public void onSuccess() {
                callback.turnedOff();
            }
        });
    }

    void turnPowerOn(turnPowerOn callback) {
        IT_Device.publishDps("{\""+first_dp+"\": true,\""+second_dp+"\": true}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFail(error+" "+code);
            }

            @Override
            public void onSuccess() {
                callback.turnedOn();
            }
        });
    }

    void turnPowerByCard(turnPowerByCard callback) {
        IT_Device.publishDps("{\""+first_dp+"\": true,\""+second_dp+"\": false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFail(error+" "+code);
            }

            @Override
            public void onSuccess() {
                callback.turnedByCard();
            }
        });
    }
}

interface turnPowerOff {
    void turnedOff();
    void onFail(String error);
}
interface turnPowerOn {
    void turnedOn();
    void onFail(String error);
}
interface turnPowerByCard {
    void turnedByCard();
    void onFail(String error);
}
interface PowerModuleInterface {
    void onOnlineChange(boolean online);
    void onPowerOff();
    void onPowerOn();
    void onPowerByCard();
}