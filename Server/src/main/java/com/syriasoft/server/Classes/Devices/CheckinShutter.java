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
import com.syriasoft.server.Classes.Interfaces.ShutterListener;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Objects;

public class CheckinShutter extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    public DeviceDPBool dp1;
    public DeviceDPBool dp2;

    ValueEventListener dp1ControlListener;
    ValueEventListener dp2ControlListener;

    public CheckinShutter(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinShutter(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    public void openOn(IResultCallback result) {
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

    public void openOff(IResultCallback result) {
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

    public void closeOn(IResultCallback result) {
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

    public void closeOff(IResultCallback result) {
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

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        for (DeviceDP dp: deviceDPS) {
            if (dp.dpId == 1) {
                try {
                    dp1 = (DeviceDPBool) dp;
                }catch (Exception e) {
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
        callback.onSuccess();
    }

    @Override
    public void listen(DeviceAction action) {
        ShutterListener sl = (ShutterListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("shutterAction",dpStr.toString());
                if (dp1 != null) {
                    if (dpStr.get("switch_"+dp1.dpId) != null) {
                        dp1.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp1.dpId)).toString());
                        if (dp1.current) {
                            sl.openShutterOn();
                        }
                        else {
                            sl.openShutterOff();
                        }
                    }
                }
                if (dp2 != null) {
                    if (dpStr.get("switch_"+dp2.dpId) != null) {
                        dp2.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dp2.dpId)).toString());
                        if (dp2.current) {
                            sl.closeShutterOn();
                        }
                        else {
                            sl.closeShutterOff();
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
                sl.online(online);
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
            dp1ControlListener = roomReference.child(device.name).child(String.valueOf(dp1.dpId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Log.d("shutterControl",snapshot.getValue().toString());
                        int value = Integer.parseInt(snapshot.getValue().toString());
                        if (value == 1) {
                            openOn(new IResultCallback() {
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
                        else if (value == 2) {
                            openOff(new IResultCallback() {
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (dp2 != null) {
            dp2ControlListener = roomReference.child(device.name).child(String.valueOf(dp2.dpId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        int value = Integer.parseInt(snapshot.getValue().toString());
                        if (value == 1) {
                            closeOn(new IResultCallback() {
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
                        else if (value == 2) {
                            closeOff(new IResultCallback() {
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void removeFirebaseDevicesControl(DatabaseReference roomReference) {

    }
}
