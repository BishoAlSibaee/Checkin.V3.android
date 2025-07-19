package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import com.syriasoft.server.Classes.Enumes.DpTypes;
import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.MotionListener;
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

public class CheckinMotionSensor extends CheckinDevice implements SetInitialValues, Listen {

    private final String[] statusNames = {"State","PIR state","Motion State","condition","CONDITION","motion sensor status"};
    private final String[] batteryNames = {"Battery","Battery level","battery"};

    DeviceDP statusDp;
    DeviceDPValue batteryDp;

    public boolean somebody = false;

    public CheckinMotionSensor(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinMotionSensor(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        List<String> statuses = Arrays.asList(statusNames);
        for (DeviceDP dp:deviceDPS) {
            Log.d("bootingOp",dp.dpName+" "+device.name);
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
            if (statusDp.dpType == DpTypes.bool) {
                statusDp = statusDp.getDpBoolean();
                statusDp.getDpBoolean().current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(statusDp.dpId))).toString());
            }
            else if (statusDp.dpType == DpTypes.Enum) {
                statusDp = statusDp.getDpEnum();
                statusDp.getDpEnum().current = Objects.requireNonNull(device.dps.get(String.valueOf(statusDp.dpId))).toString();
            }
        }
        else {
            Log.d("bootingOp","motion sensor status null "+device.name);
        }
        if (batteryDp != null) {
            try {
                batteryDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(batteryDp.dpId))).toString();
            }
            catch (Exception e) {

            }
        }
        callback.onSuccess();
    }


    @Override
    public void listen(DeviceAction action) {
        MotionListener motion = (MotionListener) action;
        this.control.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                Log.d("motionAction",device.name+" "+dpStr);
                try{
                    JSONObject action = new JSONObject(dpStr);
                    if (statusDp != null) {
                        if (dpStr.length() < 17) {
                            if (statusDp.dpType == DpTypes.bool) {
                                action.getBoolean(String.valueOf(statusDp.dpId));
                                motion.motionDetected();
                            } else if (statusDp.dpType == DpTypes.Enum) {
                                String en = action.getString(String.valueOf(statusDp.dpId));
                                if (en.equals("true") || en.equals("pir") || en.equals("motion")) {
                                    motion.motionDetected();
                                }
                                else if (en.equals("none") || en.equals("Nobody")) {
                                    somebody = false;
                                    motion.nobody();
                                }
                                else if (en.equals("presence") || en.equals("Somebody")) {
                                    somebody = true;
                                    motion.somebody();
                                }
                            }
                        }
                    }
                    else {
                        Log.d("motionAction","status dp null "+device.name);
                    }
                }catch (JSONException e) {
                    //Log.d("motionActionError", Objects.requireNonNull(e.getMessage()));
                    Log.d("motionAction",device.name+" "+e.getMessage());
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                motion.online(online);
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
