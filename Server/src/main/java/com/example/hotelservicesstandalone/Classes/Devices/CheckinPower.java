package com.example.hotelservicesstandalone.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Interfaces.PowerListener;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetFirebaseDevicesControl;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetInitialValues;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Property.Suite;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.TYDevicePublishModeEnum;

import java.util.Map;
import java.util.Objects;

public class CheckinPower extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    public DeviceDPBool dp1;
    public DeviceDPBool dp2;

    public ValueEventListener powerControlListener;

    public CheckinPower(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinPower(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    public void powerOn(IResultCallback result) {
        if (dp1 != null && dp2 != null) {
            Log.d("deviceListener"+device.name,"not null");
            this.control.publishDps("{\""+dp1.dpId+"\": "+dp1.boolValues.True+",\""+dp2.dpId+"\": "+dp2.boolValues.True+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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

    public void powerByCard(IResultCallback result) {
        if (dp1 != null && dp2 != null) {
            Log.d("deviceListener"+device.name,"not null");
            this.control.publishDps("{\""+dp1.dpId+"\": "+dp1.boolValues.True+",\""+dp2.dpId+"\": "+dp2.boolValues.False+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
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

    public void powerOff(IResultCallback result) {
        if (dp1 != null && dp2 != null) {
            Log.d("deviceListener"+device.name,"not null");
            this.control.publishDps("{\"" + dp1.dpId + "\": " + dp1.boolValues.False + " , \"" + dp2.dpId + "\": " + dp2.boolValues.False + "}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code, error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
    }

    @Override
    public void setInitialCurrentValues() {
        for (DeviceDP dp: deviceDPS) {
            Log.d("powerInfo","setting power dps "+dp.dpName+" "+dp.dpId+" "+dp.dpType);
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
                if (my_room != null) {
                    my_room.setPowerStatus(2);
                }
                else if (my_suite != null) {
                    my_suite.setPowerStatus(2);
                }
            }
            else if (!dp2.current) {
                if (my_room != null) {
                    my_room.setPowerStatus(1);
                }
                else if (my_suite != null) {
                    my_suite.setPowerStatus(1);
                }
            }
            else {
                if (my_room != null) {
                    my_room.setPowerStatus(0);
                }
                else if (my_suite != null) {
                    my_suite.setPowerStatus(0);
                }
            }
        }
    }

    @Override
    public void listen(DeviceAction action) {
        PowerListener power = (PowerListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("powerActionNew"+device.name,dpStr.toString());
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
                    else if (dp1.current) {
                        power.powerByCard();
                    }
                    else if (!dp2.current){
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
    public void setFirebaseDevicesControl(DatabaseReference controlReference) {
        powerControlListener = controlReference.child(device.name).child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Log.d("deviceListener"+device.name,"fire control");
                    int value = Integer.parseInt(snapshot.getValue().toString());

                    if (value == 0) {
                        Log.d("deviceListener"+device.name,"0");
                        powerOff(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("deviceListener"+device.name,"error "+error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("deviceListener"+device.name,"done");
                            }
                        });
                    }
                    else if (value == 1) {
                        Log.d("deviceListener"+device.name,"1");
                        powerByCard(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("deviceListener"+device.name,"error "+error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("deviceListener"+device.name,"done");
                            }
                        });
                    }
                    else if (value == 2) {
                        Log.d("deviceListener"+device.name,"2");
                        powerOn(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("deviceListener"+device.name,"error "+error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("deviceListener"+device.name,"done");
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
