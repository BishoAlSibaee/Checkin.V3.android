package com.syriasoft.mobilecheckdevice.Classes.Devices;

import android.util.Log;

import com.syriasoft.mobilecheckdevice.Classes.Enumes.DpTypes;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.DeviceAction;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.Listen;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.MotionListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.SetInitialValues;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
import com.syriasoft.mobilecheckdevice.Classes.Property.Suite;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckinMotionSensor extends CheckinDevice implements SetInitialValues, Listen {

    private final String[] statusNames = {"State","PIR state","Motion State","condition"};
    private final String[] batteryNames = {"Battery","Battery level","battery"};

    DeviceDP statusDp;
    DeviceDPValue batteryDp;

    public CheckinMotionSensor(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinMotionSensor(DeviceBean device, Suite suite) {
        super(device,suite);
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
                                    motion.nobody();
                                }
                                else if (en.equals("presence") || en.equals("Somebody")) {
                                    motion.somebody();
                                }
                            }
                        }
                    }
                }catch (JSONException e) {
                    //Log.d("motionActionError", Objects.requireNonNull(e.getMessage()));
                }
//                try{
//                    JSONObject action = new JSONObject(dpStr);
//                    if (my_room.RoomNumber == 210 || my_room.RoomNumber == 211) {
//                        Log.d("motionAction","210 211");
//                        String val = action.getString("9");
//                        int x =Integer.parseInt(val);
//                        Log.d("motionAction","hi");
//                        if (x < 400) {
//                            motion.motionDetected();
//                            Log.d("motionAction","motion");
//                        }
//                    }
//                }catch (Exception e) {
//                    Log.d("motionAction", "error "+Objects.requireNonNull(e.getMessage()));
//                }
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
