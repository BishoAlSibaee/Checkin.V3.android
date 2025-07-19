package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.SetFirebaseDevicesControl;
import com.syriasoft.server.Classes.Interfaces.SetInitialValues;
import com.syriasoft.server.Classes.Interfaces.SwitchListener;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Objects;

public class CheckinSwitch extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    public DeviceDPBool dp1;
    public DeviceDPBool dp2;
    public DeviceDPBool dp3;
    public DeviceDPBool dp4;

    ValueEventListener dp1ControlListener;
    ValueEventListener dp2ControlListener;
    ValueEventListener dp3ControlListener;
    ValueEventListener dp4ControlListener;

    public CheckinSwitch(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinSwitch(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    public void turn1On(IResultCallback result) {
        if (dp1 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp1.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn1On(IResultCallback result,String dp) {
        String onString = "{\" "+dp+" \": true }";
        ITuyaDevice control = TuyaHomeSdk.newDeviceInstance(device.devId);
        control.publishDps(onString, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                result.onError(code,error);
            }

            @Override
            public void onSuccess() {
                //nowValue = boolValues.True;
                result.onSuccess();
            }
        });
    }

    public void turn2On(IResultCallback result) {
        if (dp2 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp2.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn3On(IResultCallback result) {
        if (dp3 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp3.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn4On(IResultCallback result) {
        if (dp4 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp4.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn1Off(IResultCallback result) {
        if (dp1 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp1.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn2Off(IResultCallback result) {
        if (dp2 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp2.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn3Off(IResultCallback result) {
        if (dp3 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp3.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    public void turn4Off(IResultCallback result) {
        if (dp4 == null) {
            result.onError("no","dp1 null");
        }
        else {
            dp4.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        for (DeviceDP dp: deviceDPS) {
            if (dp.dpId == 1) {
                try {
                    dp1 = (DeviceDPBool) dp;
                } catch (Exception e) {
                    Log.d("devicesData","set initial error "+device.name+" "+dp.dpId+" "+e.getMessage());
                }
            }
            else if (dp.dpId == 2) {
                try {
                    dp2 = (DeviceDPBool) dp;
                }catch (Exception e) {
                    Log.d("devicesData","set initial error "+device.name+" "+dp.dpId+" "+e.getMessage());
                }
            }
            else if (dp.dpId == 3) {
                try {
                    dp3 = (DeviceDPBool) dp;
                }catch (Exception e) {
                    Log.d("devicesData","set initial error "+device.name+" "+dp.dpId+" "+e.getMessage());
                }
            }
            else if (dp.dpId == 4) {
                try {
                    dp4 = (DeviceDPBool) dp;
                }catch (Exception e) {
                    Log.d("devicesData","set initial error "+device.name+" "+dp.dpId+" "+e.getMessage());
                }
            }
        }
        if (dp1 != null) {
            if (device.dps.get(String.valueOf(dp1.dpId)) != null) {
                dp1.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp1.dpId))).toString());
                if (dp1.current) {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp1.dpId)).setValue(3);
                }
                else {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp1.dpId)).setValue(0);
                }
                Log.d("switchInfo","switch "+dp1.dpId+" "+dp1.current);
            }
        }
        if (dp2 != null) {
            if (device.dps.get(String.valueOf(dp2.dpId)) != null) {
                dp2.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp2.dpId))).toString());
                if (dp2.current) {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp2.dpId)).setValue(3);
                }
                else {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp2.dpId)).setValue(0);
                }
                Log.d("switchInfo","switch "+dp2.dpId+" "+dp2.current);
            }
        }
        if (dp3 != null) {
            if (device.dps.get(String.valueOf(dp3.dpId)) != null) {
                dp3.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp3.dpId))).toString());
                if (dp3.current) {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp3.dpId)).setValue(3);
                }
                else {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp3.dpId)).setValue(0);
                }
                Log.d("switchInfo","switch "+dp3.dpId+" "+dp3.current);
            }
        }
        if (dp4 != null) {
            if (device.dps.get(String.valueOf(dp4.dpId)) != null) {
                dp4.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp4.dpId))).toString());
                if (dp4.current) {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp4.dpId)).setValue(3);
                }
                else {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(dp4.dpId)).setValue(0);
                }
                Log.d("switchInfo","switch "+dp4.dpId+" "+dp4.current);
            }
        }
        callback.onSuccess();
    }

    @Override
    public void listen(DeviceAction action) {
        SwitchListener sw = (SwitchListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("switchAction",dpStr.toString());
                if (dp1 != null) {
                    if (dpStr.get("switch_"+dp1.dpId) != null) {
                        dp1.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp1.dpId)).toString());
                        if (dp1.current) {
                            sw.oneOn();
                        }
                        else {
                            sw.oneOff();
                        }
                    }
                }
                if (dp2 != null) {
                    if (dpStr.get("switch_"+dp2.dpId) != null) {
                        dp2.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp2.dpId)).toString());
                        if (dp2.current) {
                            sw.secondOn();
                        }
                        else {
                            sw.secondOff();
                        }
                    }
                }
                if (dp3 != null) {
                    if (dpStr.get("switch_"+dp3.dpId) != null) {
                        dp3.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp3.dpId)).toString());
                        if (dp3.current) {
                            sw.thirdOn();
                        }
                        else {
                            sw.thirdOff();
                        }
                    }
                }
                if (dp4 != null) {
                    if (dpStr.get("switch_"+dp4.dpId) != null) {
                        dp4.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp4.dpId)).toString());
                        if (dp4.current) {
                            sw.forthOn();
                        }
                        else {
                            sw.forthOff();
                        }
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                me.online = online;
                sw.online(online);
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
    public void setFirebaseDevicesControl(DatabaseReference roomReference) {
        if (dp1 != null) {
            boolean[] first = {true};
            dp1ControlListener = roomReference.child(device.name).child(String.valueOf(dp1.dpId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        int value = Integer.parseInt(snapshot.getValue().toString());
                        if (value == 1) {
                            if (!dp1.getCurrent() && !first[0]) {
                                turn1On(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp1.dpId)).setValue(0);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp1.dpId)).setValue(3);
                                    }
                                });
                            }
                        }
                        else if (value == 2) {
                            if (dp1.getCurrent() && !first[0]) {
                                turn1Off(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp1.dpId)).setValue(3);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp1.dpId)).setValue(0);
                                    }
                                });
                            }
                        }
                        if (first[0]) {
                            first[0] = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (dp2 != null) {
            boolean[] first = {true};
            dp2ControlListener = roomReference.child(device.name).child(String.valueOf(dp2.dpId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        int value = Integer.parseInt(snapshot.getValue().toString());
                        if (value == 1) {
                            if (!dp2.getCurrent() && !first[0]) {
                                turn2On(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp2.dpId)).setValue(0);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp2.dpId)).setValue(3);
                                    }
                                });
                            }
                        }
                        else if (value == 2) {
                            if (dp2.getCurrent() && !first[0]) {
                                turn2Off(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp2.dpId)).setValue(3);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp2.dpId)).setValue(0);
                                    }
                                });
                            }
                        }
                        if (first[0]) {
                            first[0] = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (dp3 != null) {
            boolean[] first = {true};
            dp3ControlListener = roomReference.child(device.name).child(String.valueOf(dp3.dpId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        int value = Integer.parseInt(snapshot.getValue().toString());
                        if (value == 1) {
                            if (!dp3.getCurrent() && !first[0]) {
                                turn3On(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp3.dpId)).setValue(0);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp3.dpId)).setValue(3);
                                    }
                                });
                            }
                        }
                        else if (value == 2) {
                            if (dp3.getCurrent() && !first[0]) {
                                turn3Off(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp3.dpId)).setValue(3);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp3.dpId)).setValue(0);
                                    }
                                });
                            }
                        }
                        if (first[0]) {
                            first[0] = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (dp4 != null) {
            boolean[] first = {true};
            dp4ControlListener = roomReference.child(device.name).child(String.valueOf(dp4.dpId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        int value = Integer.parseInt(snapshot.getValue().toString());
                        if (value == 1) {
                            if (!dp4.getCurrent() && !first[0]) {
                                turn4On(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp4.dpId)).setValue(0);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp4.dpId)).setValue(3);
                                    }
                                });
                            }
                        }
                        else if (value == 2) {
                            if (dp4.getCurrent() && !first[0]) {
                                turn4Off(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        roomReference.child(device.name).child(String.valueOf(dp4.dpId)).setValue(3);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        roomReference.child(device.name).child(String.valueOf(dp4.dpId)).setValue(0);
                                    }
                                });
                            }
                        }
                        if (first[0]) {
                            first[0] = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void removeFirebaseDevicesControl(DatabaseReference roomReference) {
        if (dp1ControlListener != null) {
            roomReference.child(device.name).child(String.valueOf(dp1.dpId)).removeEventListener(dp1ControlListener);
        }
        if (dp2ControlListener != null) {
            roomReference.child(device.name).child(String.valueOf(dp2.dpId)).removeEventListener(dp2ControlListener);
        }
        if (dp3ControlListener != null) {
            roomReference.child(device.name).child(String.valueOf(dp3.dpId)).removeEventListener(dp3ControlListener);
        }
        if (dp4ControlListener != null) {
            roomReference.child(device.name).child(String.valueOf(dp4.dpId)).removeEventListener(dp4ControlListener);
        }
    }
}
