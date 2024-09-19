package com.example.hotelservicesstandalone.Classes.Devices;

import android.util.Log;

import com.example.hotelservicesstandalone.Classes.Enumes.DpTypes;
import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Interfaces.MotionListener;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetInitialValues;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckinMotionSensor extends CheckinDevice implements SetInitialValues, Listen {

    private final String[] statusNames = {"State","PIR state","Motion State"};
    private final String[] batteryNames = {"Battery","Battery level","battery"};

    DeviceDP statusDp;
    DeviceDPValue batteryDp;

    public CheckinMotionSensor(DeviceBean device, Room room) {
        super(device,room);
    }

    @Override
    public void setInitialCurrentValues() {
        List<String> statuses = Arrays.asList(statusNames);
        for (DeviceDP dp:deviceDPS) {
            if (statuses.contains(dp.dpName)) {
                statusDp = dp;
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
            Log.d("motionSensorInfo" , device.name+" "+statusDp.dpName+" "+statusDp.dpId+" "+statusDp.dpType);
            if (statusDp.dpType == DpTypes.bool) {
                statusDp = statusDp.getDpBoolean();
                statusDp.getDpBoolean().current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(statusDp.dpId))).toString());
                Log.d("motionSensorInfo" ,"status "+statusDp.dpId+" "+statusDp.getDpBoolean().current);
            }
            else if (statusDp.dpType == DpTypes.Enum) {
                statusDp = statusDp.getDpEnum();
                statusDp.getDpEnum().current = Objects.requireNonNull(device.dps.get(String.valueOf(statusDp.dpId))).toString();
                Log.d("motionSensorInfo" ,"status "+statusDp.dpId+" "+statusDp.getDpEnum().current);
            }
        }
        if (batteryDp != null) {
            batteryDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(batteryDp.dpId))).toString();
            Log.d("motionSensorInfo" ,"battery "+batteryDp.dpId+" "+batteryDp.current);
        }
    }


    @Override
    public void listen(DeviceAction action) {
        MotionListener motion = (MotionListener) action;
        this.control.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                Log.d("motionAction",dpStr);
                try{
                    JSONObject action = new JSONObject(dpStr);
                    if (statusDp != null) {
                        if (dpStr.length() < 15) {
                            if (statusDp.dpType == DpTypes.bool) {
                                action.getBoolean(String.valueOf(statusDp.dpId));
                                motion.motionDetected();
                            } else if (statusDp.dpType == DpTypes.Enum) {
                                String en = action.getString(String.valueOf(statusDp.dpId));
                                if (en.equals("true") || en.equals("pir") || en.equals("motion")) {
                                    motion.motionDetected();
                                }
                            }
                        }
                    }

                }catch (JSONException e) {
                    Log.d("motionAction", Objects.requireNonNull(e.getMessage()));
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

    @Override
    public void unListen() {
        this.control.unRegisterDevListener();
    }
}
