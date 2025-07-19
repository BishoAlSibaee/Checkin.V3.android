package com.syriasoft.mobilecheckdevice.Classes.Devices;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.CurtainListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.DeviceAction;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.Listen;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.SetFirebaseDevicesControl;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.SetInitialValues;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
import com.syriasoft.mobilecheckdevice.Classes.Property.Suite;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CheckinCurtain extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    private final String[] controlNames = {"Device control","device control","Device Control","device control 1"};
    DeviceDPEnum controlDp;

    ValueEventListener curtainControlListener;


    public CheckinCurtain(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinCurtain(DeviceBean device, Suite suite) {
        super(device,suite);
    }
    @Override
    public void setInitialCurrentValues() {
        List<String> controls = Arrays.asList(controlNames);
        for (DeviceDP dp:deviceDPS) {
            if (controls.contains(dp.dpName)) {
                controlDp = (DeviceDPEnum) dp;
                break;
            }
        }
        if (controlDp != null) {
            controlDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(controlDp.dpId))).toString();
            Log.d("curtainInfo" ,"control "+ controlDp.dpId+" "+ controlDp.current);
        }
        if (controlDp != null) {
            for (EnumKeyValue.enumUnit eu : controlDp.enumKeyValue.enums) {
                Log.d("curtainEnum" , eu.key+" "+eu.value);
            }
        }
    }

    public void open(IResultCallback result) {
        if (controlDp != null) {
            int index = 0;
            for (int i = 0; i< controlDp.enumKeyValue.enums.size(); i++) {
                if (controlDp.enumKeyValue.enums.get(i).value.equals("open") || controlDp.enumKeyValue.enums.get(i).value.equals("Open") || controlDp.enumKeyValue.enums.get(i).value.equals("OPEN")) {
                    index = i;
                }
            }
            controlDp.sendOrder(index, new IResultCallback() {
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

    public void close(IResultCallback result) {
        if (controlDp != null) {
            int index = 0;
            for (int i = 0; i< controlDp.enumKeyValue.enums.size(); i++) {
                if (controlDp.enumKeyValue.enums.get(i).value.equals("close") || controlDp.enumKeyValue.enums.get(i).value.equals("Close") || controlDp.enumKeyValue.enums.get(i).value.equals("CLOSE")) {
                    index = i;
                }
            }
            controlDp.sendOrder(index, new IResultCallback() {
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

    public void stop(IResultCallback result) {
        if (controlDp != null) {
            int index = 0;
            for (int i = 0; i< controlDp.enumKeyValue.enums.size(); i++) {
                if (controlDp.enumKeyValue.enums.get(i).value.equals("stop") || controlDp.enumKeyValue.enums.get(i).value.equals("Stop") || controlDp.enumKeyValue.enums.get(i).value.equals("STOP")) {
                    index = i;
                }
            }
            controlDp.sendOrder(index, new IResultCallback() {
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
    public void listen(DeviceAction action) {
        CurtainListener curtain = (CurtainListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("curtainAction",dpStr.toString());
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                me.online = online;
                curtain.online(online);
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
    public void setFirebaseDevicesControl(DatabaseReference controlReference) {
        curtainControlListener = controlReference.child(device.name).child("control").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    switch (value) {
                        case "open" : case "Open": case "OPEN":
                            open(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                            break;
                        case "close": case "Close": case "CLOSE":
                            close(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                            break;
                        case "stop": case "Stop": case "STOP":
                            stop(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                            break;
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
        if (curtainControlListener != null) {
            controlReference.child(device.name).child("control").removeEventListener(curtainControlListener);
        }
    }
}
