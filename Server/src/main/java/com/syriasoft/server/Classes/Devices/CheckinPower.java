package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.PowerListener;
import com.syriasoft.server.Classes.Interfaces.SetFirebaseDevicesControl;
import com.syriasoft.server.Classes.Interfaces.SetInitialValues;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.server.Interface.RequestCallback;
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
    static int ind = 0;

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
        else {
            Log.d("deviceListener"+device.name,"null");
        }
    }

    public void powerOnOffline(IResultCallback result) {
        if (dp1 != null && dp2 != null) {
            Log.d("deviceListener"+device.name,"not null");
            this.control.publishDps("{\""+dp1.dpId+"\": "+dp1.boolValues.True+",\""+dp2.dpId+"\": "+dp2.boolValues.True+"}", TYDevicePublishModeEnum.TYDevicePublishModeLocal, new IResultCallback() {
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

    public void powerByCardOffline(IResultCallback result) {
        if (dp1 != null && dp2 != null) {
            Log.d("deviceListener"+device.name,"not null");
            this.control.publishDps("{\""+dp1.dpId+"\": "+dp1.boolValues.True+",\""+dp2.dpId+"\": "+dp2.boolValues.False+"}", TYDevicePublishModeEnum.TYDevicePublishModeLocal, new IResultCallback() {
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

    public void powerOffOffline(IResultCallback result) {
        if (dp1 != null && dp2 != null) {
            Log.d("deviceListener"+device.name,"not null");
            this.control.publishDps("{\"" + dp1.dpId + "\": " + dp1.boolValues.False + " , \"" + dp2.dpId + "\": " + dp2.boolValues.False + "}", TYDevicePublishModeEnum.TYDevicePublishModeLocal, new IResultCallback() {
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
    public void setInitialCurrentValues(RequestCallback callback) {
        if (my_room != null) {
            my_room.fireRoom.child("PowerSwitch").setValue(1);
        }
        ind++;
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
        }
        if (dp2 != null) {
            dp2.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dp2.dpId))).toString());
        }
        if (dp1 != null && dp2 != null) {
            if (dp1.current && dp2.current) {
                if (my_room != null) {
                    my_room.setPowerStatus(2);
                    my_room.devicesControlReference.child(device.name).child("1").setValue(2);
                }
                else if (my_suite != null) {
                    my_suite.setPowerStatus(2);
                }
            }
            else if (!dp2.current) {
                if (my_room != null) {
                    my_room.setPowerStatus(1);
                    my_room.devicesControlReference.child(device.name).child("1").setValue(1);
                }
                else if (my_suite != null) {
                    my_suite.setPowerStatus(1);
                }
            }
            else {
                if (my_room != null) {
                    my_room.setPowerStatus(0);
                    my_room.devicesControlReference.child(device.name).child("1").setValue(0);
                }
                else if (my_suite != null) {
                    my_suite.setPowerStatus(0);
                }
            }
        }
        callback.onSuccess();
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
        CheckinPower me = this;
        boolean[] first = {true};
        powerControlListener = controlReference.child(device.name).child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (first[0]) {
                        first[0] = false;
                        //Log.d("firstRunFBControl",my_room.RoomNumber+" power fb control "+snapshot.getValue().toString()+" || current: 1 = "+me.dp1.getCurrent()+" 2 = "+me.dp2.getCurrent());
                    }
                    int value = Integer.parseInt(snapshot.getValue().toString());
                    if (value == 0) {
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
