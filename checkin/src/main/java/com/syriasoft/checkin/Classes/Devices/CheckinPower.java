package com.syriasoft.checkin.Classes.Devices;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.syriasoft.checkin.Classes.Interfaces.DeviceAction;
import com.syriasoft.checkin.Classes.Interfaces.Listen;
import com.syriasoft.checkin.Classes.Interfaces.PowerListener;
import com.syriasoft.checkin.Classes.Interfaces.SetFirebaseDevicesControl;
import com.syriasoft.checkin.Classes.Interfaces.SetInitialValues;
import com.syriasoft.checkin.Classes.Property.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Objects;

public class CheckinPower extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    DeviceDPBool dp1;
    DeviceDPBool dp2;

    public ValueEventListener powerControlListener;

    public CheckinPower(DeviceBean device, Room room) {
        super(device,room);
    }

    public void powerOn(IResultCallback result) {
        final boolean[] res1 = {false};
        final boolean[] res2 = {false};
        if (dp1 != null && dp2 != null) {
            dp1.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    res1[0] = true;
                    if (res2[0]) {
                        result.onError(code,error);
                    }
                }

                @Override
                public void onSuccess() {
                    res1[0] = true;
                    if (res2[0]) {
                        result.onSuccess();
                    }
                }
            });
            dp2.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    res2[0] = true;
                    if (res1[0]) {
                        result.onError(code,error);
                    }
                }

                @Override
                public void onSuccess() {
                    res2[0] = true;
                    if (res1[0]) {
                        result.onSuccess();
                    }
                }
            });
        }
    }

    public void powerByCard(IResultCallback result) {
        final boolean[] res1 = {false};
        final boolean[] res2 = {false};
        if (dp1 != null && dp2 != null) {
            dp1.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    res1[0] = true;
                    if (res2[0]) {
                        result.onError(code,error);
                    }
                }

                @Override
                public void onSuccess() {
                    res1[0] = true;
                    if (res2[0]) {
                        result.onSuccess();
                    }
                }
            });
            dp2.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    res2[0] = true;
                    if (res1[0]) {
                        result.onError(code,error);
                    }
                }

                @Override
                public void onSuccess() {
                    res2[0] = true;
                    if (res1[0]) {
                        result.onSuccess();
                    }
                }
            });
        }
    }

    public void powerOff(IResultCallback result) {
        final boolean[] res1 = {false};
        final boolean[] res2 = {false};
        if (dp1 != null && dp2 != null) {
            dp1.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    res1[0] = true;
                    if (res2[0]) {
                        result.onError(code,error);
                    }
                }

                @Override
                public void onSuccess() {
                    res1[0] = true;
                    if (res2[0]) {
                        result.onSuccess();
                    }
                }
            });
            dp2.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    res2[0] = true;
                    if (res1[0]) {
                        result.onError(code,error);
                    }
                }

                @Override
                public void onSuccess() {
                    res2[0] = true;
                    if (res1[0]) {
                        result.onSuccess();
                    }
                }
            });
        }
    }

    @Override
    public void setInitialCurrentValues() {
        for (DeviceDP dp: deviceDPS) {
            if (dp.dpId == 1) {
                dp1 = (DeviceDPBool) dp;
            }
            else if (dp.dpId == 2) {
                dp2 = (DeviceDPBool) dp;
            }
        }
        if (dp1 != null) {
            dp1.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp1.dpId))).toString());
            Log.d("powerInfo","power "+dp1.dpId+" "+dp1.current);
        }
        if (dp2 != null) {
            dp2.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp2.dpId))).toString());
            Log.d("powerInfo","power "+dp2.dpId+" "+dp2.current);
        }
        if (dp1 != null && dp2 != null) {
            if (dp1.current && dp2.current) {
                my_room.fireRoom.child("powerStatus").setValue(2);
            }
            else if (!dp2.current) {
                my_room.fireRoom.child("powerStatus").setValue(1);
            }
            else {
                my_room.fireRoom.child("powerStatus").setValue(0);
            }
        }
    }

    @Override
    public void listen(DeviceAction action) {
        PowerListener power = (PowerListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                if (dpStr.toString().length() <= 16) {
                    Log.d("powerActionNew"+device.name,dpStr.toString());
                    if (dpStr.get("switch_"+dp1.dpId) != null) {
                        dp1.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp1.dpId)).toString());
                    }
                    if (dpStr.get("switch_"+dp2.dpId) != null) {
                        dp2.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp2.dpId)).toString());
                    }
                    Log.d("powerActionNew"+device.name,dp1.current+" "+dp2.current);
                    if (dp1.current && dp2.current) {
                        power.powerOn();
                    }
                    else if (!dp2.current) {
                        power.powerByCard();
                    }
                    else {
                        power.powerOff();
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                me.online = online;
                power.online(online);
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

    @Override
    public void setFirebaseDevicesControl(Context c, String projectUrl, DatabaseReference controlReference) {
        powerControlListener = controlReference.child(device.name).child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Log.d("deviceListener"+device.name,"fire control "+snapshot.getValue());
                    int value = Integer.parseInt(snapshot.getValue().toString());
                    if (value == 0) {
                        powerOff(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    else if (value == 1) {
                        powerByCard(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    else if (value == 2) {
                        powerOn(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void removeFirebaseDevicesControl(DatabaseReference controlReference) {
        if (powerControlListener != null) {
            controlReference.child(device.name).child("1").removeEventListener(powerControlListener);
        }
    }
}
