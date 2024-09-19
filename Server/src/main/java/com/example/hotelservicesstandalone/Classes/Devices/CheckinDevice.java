package com.example.hotelservicesstandalone.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hotelservicesstandalone.Classes.Enumes.DeviceTypes;
import com.example.hotelservicesstandalone.Classes.Enumes.DpTypes;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetInitialValues;
import com.example.hotelservicesstandalone.Classes.Interfaces.getDeviceDataCallback;
import com.example.hotelservicesstandalone.Classes.Interfaces.getDeviceInfo;
import com.example.hotelservicesstandalone.Classes.LocalDataStore;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Property.Suite;
import com.example.hotelservicesstandalone.Classes.Tuya;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CheckinDevice extends DeviceBean implements getDeviceInfo, SetInitialValues {

    public DeviceBean device;
    ITuyaDevice control;
    String deviceCode; // tuya category code
    DeviceTypes deviceType; // device type eg.(power,service,switch,door sensor)
    public CheckinDevice me;
    public List<DeviceDP> deviceDPS;
    boolean online;

    public Room my_room;
    public Suite my_suite;

    public CheckinDevice(DeviceBean device,Room room) {
        this.device = device;
        this.control = TuyaHomeSdk.newDeviceInstance(device.devId);
        this.deviceCode = this.device.getCategoryCode();
        this.deviceType = DeviceTypes.getDeviceType(this.deviceCode,this.device.name);
        this.my_room = room;
        this.me = this;
        deviceDPS = new ArrayList<>();
    }

    public CheckinDevice(DeviceBean device, Suite suite) {
        this.device = device;
        this.control = TuyaHomeSdk.newDeviceInstance(device.devId);
        this.deviceCode = this.device.getCategoryCode();
        this.deviceType = DeviceTypes.getDeviceType(this.deviceCode,this.device.name);
        this.my_suite = suite;
        this.me = this;
        deviceDPS = new ArrayList<>();
    }

    @Override
    public void getDeviceDPs(getDeviceDataCallback callback) {
            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(this.device.devId, new ITuyaResultCallback<List<TaskListBean>>() {
                @Override
                public void onSuccess(List<TaskListBean> result) {
                    Log.d("deviceInfo", device.name+" has "+result.size()+" "+me.getClass().getName()+" dps "+device.dps);
                    for (TaskListBean tb : result) {
                        Log.d("deviceInfo", device.name+" "+tb.getName()+" "+tb.getDpId()+" "+tb.getType()+" "+tb.getTasks().size());
                        Object[] values = tb.getTasks().values().toArray();
                        Object[] keys = tb.getTasks().keySet().toArray();
                        if (tb.getType().equals("bool")) {
                            DeviceDPBool dp = new DeviceDPBool(tb.getDpId(),tb.getName(), DpTypes.getType(tb.getType()),me);
                            String falseName = null;
                            String trueName = null;
                            for (int i=0;i<tb.getTasks().size();i++) {
                                if (keys[i].toString().equals("true")) {
                                    trueName =  values[i].toString();
                                }
                                else {
                                    falseName = values[i].toString();
                                }
                                Log.d("deviceInfo", device.name+" "+"    - value "+ values[i].toString()+" , key "+keys[i].toString());
                            }
                            if (trueName != null && falseName != null) {
                                dp.boolValues = new BoolKeyValue(trueName,falseName);
                            }
                            dp.nowValue = device.dps.get(String.valueOf(dp.dpId));
                            deviceDPS.add(dp);
                        }
                        else if (tb.getType().equals("value")) {
                            DeviceDPValue dp = new DeviceDPValue(tb.getDpId(),tb.getName(),DpTypes.getType(tb.getType()),me);
                            Log.d("deviceInfo", tb.getValueSchemaBean().toString());
                            dp.valueKeyValue = new ValueKeyValue(tb.getValueSchemaBean().min,tb.getValueSchemaBean().max,tb.getValueSchemaBean().step,tb.getValueSchemaBean().unit,tb.getValueSchemaBean().scale);
                            dp.nowValue = device.dps.get(String.valueOf(dp.dpId));
                            deviceDPS.add(dp);
                        }
                        else if (tb.getType().equals("enum")) {
                            DeviceDPEnum dp = new DeviceDPEnum(tb.getDpId(), tb.getName(), DpTypes.getType(tb.getType()),me);
                            dp.enumKeyValue = new EnumKeyValue(keys, values);
                            dp.nowValue = device.dps.get(String.valueOf(dp.dpId));
                            deviceDPS.add(dp);
                            for (int i=0;i<tb.getTasks().size();i++) {
                                Log.d("deviceInfo", device.name+" "+"    - value "+ values[i].toString()+" , key "+keys[i].toString());
                            }
                        }
                    }
                    for (DeviceDP dp : deviceDPS) {
                        if (my_room != null) {
                            my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("name").setValue(dp.dpName);
                            my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("type").setValue(dp.dpType);
                            if (dp.dpType == DpTypes.value) {
                                if (dp.getDpValue().valueKeyValue != null) {
                                    my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("max").setValue(dp.getDpValue().valueKeyValue.max);
                                    my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("min").setValue(dp.getDpValue().valueKeyValue.min);
                                    my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("step").setValue(dp.getDpValue().valueKeyValue.step);
                                    my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("unit").setValue(dp.getDpValue().valueKeyValue.unit);
                                }
                            }
                            else if (dp.dpType == DpTypes.bool) {
                                my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("true").setValue(dp.getDpBoolean().boolValues.TrueName);
                                my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("false").setValue(dp.getDpBoolean().boolValues.FalseName);
                            }
                            else if (dp.dpType == DpTypes.Enum) {
                                for (int i=0;i<dp.getDpEnum().enumKeyValue.enums.size();i++) {
                                    my_room.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child(String.valueOf(i)).setValue(dp.getDpEnum().enumKeyValue.enums.get(i));
                                }
                            }
                        }
                        else if (my_suite != null) {
                            my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("name").setValue(dp.dpName);
                            my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("type").setValue(dp.dpType);
                            if (dp.dpType == DpTypes.value) {
                                if (dp.getDpValue().valueKeyValue != null) {
                                    my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("max").setValue(dp.getDpValue().valueKeyValue.max);
                                    my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("min").setValue(dp.getDpValue().valueKeyValue.min);
                                    my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("step").setValue(dp.getDpValue().valueKeyValue.step);
                                    my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("unit").setValue(dp.getDpValue().valueKeyValue.unit);
                                }
                            }
                            else if (dp.dpType == DpTypes.bool) {
                                my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("true").setValue(dp.getDpBoolean().boolValues.TrueName);
                                my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child("false").setValue(dp.getDpBoolean().boolValues.FalseName);
                            }
                            else if (dp.dpType == DpTypes.Enum) {
                                for (int i=0;i<dp.getDpEnum().enumKeyValue.enums.size();i++) {
                                    my_suite.devicesDataReference.child(device.name).child(String.valueOf(dp.dpId)).child("data").child(String.valueOf(i)).setValue(dp.getDpEnum().enumKeyValue.enums.get(i));
                                }
                            }
                        }
                    }
                    callback.onSuccess();
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    Log.d("deviceInfo",errorCode+" "+errorMessage);
                    callback.onError(errorMessage);
                }
            });
    }

    public void getDeviceDpsFromFirebase(getDeviceDataCallback callback) {
        if (my_room != null) {
            my_room.devicesDataReference.child(device.name).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (!Objects.requireNonNull(ds.getKey()).equals("id")) {
                            if (ds.child("type").getValue() != null && ds.child("name").getValue() != null) {
                                if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals(DpTypes.bool.toString())) {
                                    DeviceDPBool dp = new DeviceDPBool(Long.parseLong(ds.getKey()), Objects.requireNonNull(ds.child("name").getValue()).toString(),DpTypes.bool,me);
                                    String trueName = Objects.requireNonNull(ds.child("data").child("true").getValue()).toString();
                                    String falseName = Objects.requireNonNull(ds.child("data").child("false").getValue()).toString();
                                    dp.boolValues = new BoolKeyValue(trueName,falseName);
                                    dp.device = me;
                                    deviceDPS.add(dp);
                                }
                                else if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals(DpTypes.value.toString())) {
                                    DeviceDPValue dp = new DeviceDPValue(Long.parseLong(ds.getKey()), Objects.requireNonNull(ds.child("name").getValue()).toString(),DpTypes.value,me);
                                    int min = Integer.parseInt(Objects.requireNonNull(ds.child("data").child("min").getValue()).toString());
                                    int max = Integer.parseInt(Objects.requireNonNull(ds.child("data").child("max").getValue()).toString());
                                    int step = Integer.parseInt(Objects.requireNonNull(ds.child("data").child("step").getValue()).toString());
                                    String unit = Objects.requireNonNull(ds.child("data").child("unit").getValue()).toString();
                                    dp.valueKeyValue = new ValueKeyValue(min,max,step,unit,0);
                                    dp.device = me;
                                    deviceDPS.add(dp);
                                }
                                else if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals(DpTypes.Enum.toString())) {
                                    DeviceDPEnum dp = new DeviceDPEnum(Long.parseLong(ds.getKey()), Objects.requireNonNull(ds.child("name").getValue()).toString(),DpTypes.Enum,me);
                                    Object[] values = new Object[(int) ds.child("data").getChildrenCount()];
                                    Object[] keys = new Object[(int) ds.child("data").getChildrenCount()];
                                    for (int i=0;i<ds.child("data").getChildrenCount();i++) {
                                        keys[i] = ds.child("data").child(String.valueOf(i)).child("key").getValue();
                                        values[i] = ds.child("data").child(String.valueOf(i)).child("value").getValue();
                                    }
                                    dp.enumKeyValue = new EnumKeyValue(keys, values);
                                    dp.device = me;
                                    deviceDPS.add(dp);
                                }
                            }
                        }
                        else {
                            Tuya.devicesIds.add(Objects.requireNonNull(ds.getValue()).toString());
                        }
                    }
                    callback.onSuccess();
                    Log.d("deviceInfo", "finish "+device.name+" "+deviceDPS.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError(error.getMessage());
                    Log.d("deviceInfo", error.getMessage());
                }
            });
        }
        if (my_suite != null) {
            my_suite.devicesDataReference.child(device.name).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (!Objects.requireNonNull(ds.getKey()).equals("id")) {
                            if (ds.child("type").getValue() != null && ds.child("name").getValue() != null) {
                                if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals(DpTypes.bool.toString())) {
                                    DeviceDPBool dp = new DeviceDPBool(Long.parseLong(ds.getKey()), Objects.requireNonNull(ds.child("name").getValue()).toString(),DpTypes.bool,me);
                                    String trueName = Objects.requireNonNull(ds.child("data").child("true").getValue()).toString();
                                    String falseName = Objects.requireNonNull(ds.child("data").child("false").getValue()).toString();
                                    dp.boolValues = new BoolKeyValue(trueName,falseName);
                                    dp.device = me;
                                    deviceDPS.add(dp);
                                }
                                else if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals(DpTypes.value.toString())) {
                                    DeviceDPValue dp = new DeviceDPValue(Long.parseLong(ds.getKey()), Objects.requireNonNull(ds.child("name").getValue()).toString(),DpTypes.value,me);
                                    int min = Integer.parseInt(Objects.requireNonNull(ds.child("data").child("min").getValue()).toString());
                                    int max = Integer.parseInt(Objects.requireNonNull(ds.child("data").child("max").getValue()).toString());
                                    int step = Integer.parseInt(Objects.requireNonNull(ds.child("data").child("step").getValue()).toString());
                                    String unit = Objects.requireNonNull(ds.child("data").child("unit").getValue()).toString();
                                    dp.valueKeyValue = new ValueKeyValue(min,max,step,unit,0);
                                    dp.device = me;
                                    deviceDPS.add(dp);
                                }
                                else if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals(DpTypes.Enum.toString())) {
                                    DeviceDPEnum dp = new DeviceDPEnum(Long.parseLong(ds.getKey()), Objects.requireNonNull(ds.child("name").getValue()).toString(),DpTypes.Enum,me);
                                    Object[] values = new Object[(int) ds.child("data").getChildrenCount()];
                                    Object[] keys = new Object[(int) ds.child("data").getChildrenCount()];
                                    for (int i=0;i<ds.child("data").getChildrenCount();i++) {
                                        keys[i] = ds.child("data").child(String.valueOf(i)).child("key").getValue();
                                        values[i] = ds.child("data").child(String.valueOf(i)).child("value").getValue();
                                    }
                                    dp.enumKeyValue = new EnumKeyValue(keys, values);
                                    dp.device = me;
                                    deviceDPS.add(dp);
                                }
                            }
                        }
                        else {
                            Tuya.devicesIds.add(Objects.requireNonNull(ds.getValue()).toString());
                        }
                    }
                    callback.onSuccess();
                    Log.d("deviceInfo", "finish "+device.name+" "+deviceDPS.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError(error.getMessage());
                    Log.d("deviceInfo", error.getMessage());
                }
            });
        }
    }

    public CheckinPower getCheckinPower() {
        return (CheckinPower) this;
    }

    public CheckinGateway getCheckinGateway() {
        return (CheckinGateway) this;
    }

    public CheckinAC getCheckinAC() {
        return (CheckinAC) this;
    }

    public CheckinSwitch getCheckinSwitch() {
        return (CheckinSwitch) this;
    }

    public CheckinServiceSwitch getCheckinServiceSwitch() {
        return (CheckinServiceSwitch) this;
    }

    public CheckinLock getCheckinLock() {
        return (CheckinLock) this;
    }

    public CheckinDoorSensor getCheckinDoorSensor() {
        return (CheckinDoorSensor) this;
    }

    public CheckinMotionSensor getCheckinMotionSensor() {
        return (CheckinMotionSensor) this;
    }

    public CheckinCurtain getCheckinCurtain() {
        return (CheckinCurtain) this;
    }

    public void setInitialCurrentValues() {
    }

    public static void saveDevicesDataToLocalStorage(LocalDataStore storage,List<CheckinDevice> devices) {
        for (int i = 0;i<devices.size();i++) {
            CheckinDevice cd = devices.get(i);
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.saveDeviceData(cd);
                    Log.d("bootingOp","saving device data "+cd.device.name);
                }
            },1000L*i);


        }
        storage.saveBoolean(true,"devicesDataSaved");
    }

    public static void getDevicesDataFromLocalStorage(LocalDataStore storage,List<CheckinDevice> devices) {
        for (CheckinDevice cd : devices) {
            cd.deviceDPS = storage.getDeviceData(cd);
        }
    }

    public static boolean getIsDevicesDataSaved(LocalDataStore storage) {
        return storage.getBoolean("devicesDataSaved");
    }
}
