package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.syriasoft.server.Classes.Interfaces.ACListener;
import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.SetFirebaseDevicesControl;
import com.syriasoft.server.Classes.Interfaces.SetInitialValues;
import com.syriasoft.server.Classes.PROJECT_VARIABLES;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckinAC extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    private final String[] powerNames = {"switch","Switch","SWITCH","Power"};
    private final String[] setTempNames = {"Set Temperature","Set temperature","Temp Set"};
    private final String[] currentTempNames = {"Current Temperature","Current Temp","Current temperature"};
    private final String[] fanNames = {"Fan Speed Enum","Gear","FAN Speed","Fan Speed"};

    DeviceDPBool powerDp;
    public DeviceDPValue setTempDp;
    DeviceDPValue currentTempDp;
    DeviceDPEnum fanDp;

    ValueEventListener acControlListenerPower;
    ValueEventListener acControlListenerTemp;
    ValueEventListener acControlListenerFan;

    public String clientSetTemp;

    public CheckinAC(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinAC(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        if (my_room != null) {
            my_room.fireRoom.child("Thermostat").setValue(1);
        }
        clientSetTemp = String.valueOf(PROJECT_VARIABLES.Temp);
        List<String> powers = Arrays.asList(powerNames);
        for (DeviceDP dp:deviceDPS) {
            if (powers.contains(dp.dpName)) {
                powerDp = (DeviceDPBool) dp;
                break;
            }
        }
        List<String> setTemps = Arrays.asList(setTempNames);
        for (DeviceDP dp:deviceDPS) {
            if (setTemps.contains(dp.dpName)) {
                setTempDp = (DeviceDPValue) dp;
                break;
            }
        }
        List<String> currentTemps = Arrays.asList(currentTempNames);
        for (DeviceDP dp:deviceDPS) {
            if (currentTemps.contains(dp.dpName)) {
                currentTempDp = (DeviceDPValue) dp;
                break;
            }
        }
        List<String> fans = Arrays.asList(fanNames);
        for (DeviceDP dp:deviceDPS) {
            if (fans.contains(dp.dpName)) {
                fanDp = (DeviceDPEnum) dp;
                break;
            }
        }
        if (powerDp != null) {
            powerDp.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(powerDp.dpId))).toString());
            if (powerDp.current) {
                my_room.devicesControlReference.child(device.name).child("power").setValue(3);
            }
            else {
                my_room.devicesControlReference.child(device.name).child("power").setValue(0);
            }
            Log.d("acInfo","power "+device.name+" "+powerDp.dpId+" "+powerDp.current);
        }
        if (setTempDp != null) {
            setTempDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(setTempDp.dpId))).toString();
            my_room.devicesControlReference.child(device.name).child("temp").setValue(setTempDp.current);
            Log.d("acInfo","set temp "+device.name+" "+setTempDp.dpId+" "+setTempDp.current);
        }
        if (currentTempDp != null) {
            currentTempDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(currentTempDp.dpId))).toString();
            Log.d("acInfo","current temp "+device.name+" "+currentTempDp.dpId+" "+currentTempDp.current);
        }
        if (fanDp != null) {
            fanDp.current = Objects.requireNonNull(device.dps.get(String.valueOf(fanDp.dpId))).toString();
            my_room.devicesControlReference.child(device.name).child("fan").setValue(fanDp.current);
            Log.d("acInfo","fan "+device.name+" "+fanDp.dpId+" "+fanDp.current);
        }
        callback.onSuccess();
    }

    public void turnOn(final IResultCallback result) {
       if (powerDp != null) {
           powerDp.turnOn(new IResultCallback() {
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
           result.onError("no code","power dp null");
       }
    }

    public void turnOff(final IResultCallback result) {
        if (powerDp != null) {
            powerDp.turnOff(new IResultCallback() {
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
            result.onError("no code","power dp null");
        }
    }

    public void setTemperature(int temp,IResultCallback result) {
        if (setTempDp != null) {
            if (String.valueOf(setTempDp.valueKeyValue.max).length() == 3) {
                if (String.valueOf(temp).length() == 2) {
                    temp = temp * 10;
                }
            }
            setTempDp.setTemp(temp, new IResultCallback() {
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

    public void raiseTemperature(IResultCallback result) {
        if (setTempDp != null) {
            setTempDp.increase(new IResultCallback() {
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
            result.onError("no code","set temp dp null");
        }
    }

    public void downTemperature(IResultCallback result) {
        if (setTempDp != null) {
            setTempDp.decrease(new IResultCallback() {
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
            result.onError("no code","set temp dp null");
        }
    }

    public void setFan(IResultCallback result) {
        if (fanDp != null) {
            fanDp.sendOrder(fanDp.enumKeyValue.getNext(fanDp.current), new IResultCallback() {
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
        ACListener ac = (ACListener) action;
        this.control.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                //Log.d("acAction",dpStr);
                try {
                    JSONObject obj = new JSONObject(dpStr);
                    powerDp.current = obj.getBoolean(String.valueOf(powerDp.dpId));
                    if (powerDp.current) {
                        ac.onPowerOn();
                    }
                    else {
                        ac.onPowerOff();
                    }
                } catch (JSONException | NullPointerException e) {
                    Log.d("error","no power "+e.getMessage());
                }
                try {
                    JSONObject obj = new JSONObject(dpStr);
                    setTempDp.current = obj.getString(String.valueOf(setTempDp.dpId));
                    ac.onTempSet(setTempDp.current);
                } catch (Exception e) {
                    Log.d("error","no set "+e.getMessage());
                }
                try {
                    JSONObject obj = new JSONObject(dpStr);
                    currentTempDp.current = obj.getString(String.valueOf(currentTempDp.dpId));
                    ac.onTempCurrent(currentTempDp.current);
                } catch (Exception e) {
                    Log.d("error","no current "+e.getMessage());
                }
                try {
                    JSONObject obj = new JSONObject(dpStr);
                    fanDp.current = obj.getString(String.valueOf(fanDp.dpId));
                    ac.onFanSet(fanDp.current);
                } catch (Exception e) {
                    Log.d("error","no fan "+e.getMessage());
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                ac.online(online);
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
        acControlListenerPower = controlReference.child(device.name).child("power").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    int value = Integer.parseInt(snapshot.getValue().toString());
                    if (value == 1) {
                        turnOn(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                controlReference.child(device.name).child("power").setValue(0);
                            }

                            @Override
                            public void onSuccess() {
                                controlReference.child(device.name).child("power").setValue(3);
                            }
                        });
                    }
                    else if (value == 2) {
                        turnOff(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                controlReference.child(device.name).child("power").setValue(3);
                            }

                            @Override
                            public void onSuccess() {
                                controlReference.child(device.name).child("power").setValue(0);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        acControlListenerTemp = controlReference.child(device.name).child("temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    int newTemp = Integer.parseInt(snapshot.getValue().toString());
                    if (setTempDp != null) {
                        if (String.valueOf(setTempDp.valueKeyValue.max).length() == 3) {
                            newTemp = newTemp*10;
                        }
                    }
                    setTemperature(newTemp, new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        acControlListenerFan = controlReference.child(device.name).child("fan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (fanDp != null) {
                    fanDp.sendOrder(fanDp.enumKeyValue.getNext(fanDp.current), new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void removeFirebaseDevicesControl(DatabaseReference controlReference) {
        if (acControlListenerPower != null) {
            controlReference.child(device.name).child("power").removeEventListener(acControlListenerPower);
        }
        if (acControlListenerTemp != null) {
            controlReference.child(device.name).child("temp").removeEventListener(acControlListenerTemp);
        }
        if (acControlListenerFan != null) {
            controlReference.child(device.name).child("fan").removeEventListener(acControlListenerFan);
        }
    }
}
