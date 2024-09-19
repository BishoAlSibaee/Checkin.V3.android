package com.example.hotelservicesstandalone.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Interfaces.ServiceListener;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetFirebaseDevicesControl;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetInitialValues;
import com.example.hotelservicesstandalone.Classes.PROJECT_VARIABLES;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CheckinServiceSwitch extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    DeviceDPBool cleanup;
    DeviceDPBool laundry;
    DeviceDPBool checkout;
    DeviceDPBool dnd;
    ValueEventListener cleanupControlListener;
    ValueEventListener laundryControlListener;
    ValueEventListener dndControlListener;
    ValueEventListener checkoutControlListener;

    public long lastCleanup,lastLaundry,lastCheckout,lastDND;

    public CheckinServiceSwitch(DeviceBean device, Room room) {
        super(device,room);
        if (device.name.equals("104ServiceSwitch")) {
            Log.d("bootingProb","constructing");
        }
    }

    public void cleanupOn(IResultCallback result) {
        if (cleanup == null) {
            result.onError("no code","cleanup null");
        }
        else {
            cleanup.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    Log.d("fireControl",my_room.RoomNumber+" cleanup turn on ");
                    result.onSuccess();
                }
            });
        }
    }

    public void cleanupOff(IResultCallback result) {
        if (cleanup == null) {
            result.onError("no code","cleanup null");
        }
        else {
            cleanup.turnOff(new IResultCallback() {
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

    public void laundryOn(IResultCallback result) {
        if (laundry == null) {
            result.onError("no code","laundry null");
        }
        else {
            laundry.turnOn(new IResultCallback() {
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

    public void laundryOff(IResultCallback result) {
        if (laundry == null) {
            result.onError("no code","laundry null");
        }
        else {
            laundry.turnOff(new IResultCallback() {
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

    public void checkoutOn(IResultCallback result) {
        if (checkout == null) {
            result.onError("no code","checkout null");
        }
        else {
            checkout.turnOn(new IResultCallback() {
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

    public void checkoutOff(IResultCallback result) {
        if (checkout == null) {
            result.onError("no code","checkout null");
        }
        else {
            checkout.turnOff(new IResultCallback() {
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

    public void dndOn(IResultCallback result) {
        if (dnd == null) {
            result.onError("no code","dnd null");
        }
        else {
            dnd.turnOn(new IResultCallback() {
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

    public void dndOff(IResultCallback result) {
        if (dnd == null) {
            result.onError("no code","dnd null");
        }
        else {
            dnd.turnOff(new IResultCallback() {
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
    public void setInitialCurrentValues() {
        for (DeviceDP dp : deviceDPS) {
            Log.d("serviceAction",my_room.RoomNumber+" dps "+dp.dpName);
            if (dp.dpId == PROJECT_VARIABLES.cleanupButton) {
                cleanup = (DeviceDPBool) dp;
            }
            else if (dp.dpId == PROJECT_VARIABLES.laundryButton) {
                laundry = (DeviceDPBool) dp;
            }
            else if (dp.dpId == PROJECT_VARIABLES.checkoutButton) {
                checkout = (DeviceDPBool) dp;
            }
            else if (dp.dpId == PROJECT_VARIABLES.dndButton) {
                dnd = (DeviceDPBool) dp;
            }
        }
        if (cleanup != null) {
            cleanup.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(cleanup.dpId))).toString());
            if (my_room.roomStatus == 2) {
                if (cleanup.current) {
                    my_room.fireRoom.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
                else {
                     my_room.fireRoom.child("Cleanup").setValue(0);
                }
            }
            Log.d("serviceInfo",device.name+" cleanup "+cleanup.dpId+" "+cleanup.current);
        }
        if (laundry != null) {
            laundry.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(laundry.dpId))).toString());
            if (my_room.roomStatus == 2) {
                if (cleanup.current) {
                    my_room.fireRoom.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
                else {
                    my_room.fireRoom.child("Laundry").setValue(0);
                }
            }
            Log.d("serviceInfo",device.name+" laundry "+laundry.dpId+" "+laundry.current);
        }
        if (checkout != null) {
            checkout.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(checkout.dpId))).toString());
            if (my_room.roomStatus == 2) {
                if (cleanup.current) {
                    my_room.fireRoom.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
                else {
                    my_room.fireRoom.child("Checkout").setValue(0);
                }
            }
            Log.d("serviceInfo",device.name+" checkout "+checkout.dpId+" "+checkout.current);
        }
        if (dnd != null) {
            dnd.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dnd.dpId))).toString());
            if (my_room.roomStatus == 2) {
                if (cleanup.current) {
                    my_room.fireRoom.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
                else {
                    my_room.fireRoom.child("DND").setValue(0);
                }
            }
            Log.d("serviceInfo",device.name+" dnd "+dnd.dpId+" "+dnd.current);
        }
    }

    @Override
    public void listen(DeviceAction action) {
        ServiceListener sl = (ServiceListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("serviceAction",dpStr.toString() +" ");
                if (cleanup != null) {
                    if (dpStr.get("switch_"+cleanup.dpId) != null) {
                        Log.d("serviceAction","cleanup");
                        cleanup.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+cleanup.dpId)).toString());
                        if (cleanup.current) {
                            sl.cleanup();
                        }
                        else {
                            sl.cancelCleanup();
                        }
                    }
                }
                if (laundry != null) {
                    Log.d("serviceAction","laundry not null");
                    if (dpStr.get("switch_"+laundry.dpId) != null) {
                        laundry.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+laundry.dpId)).toString());
                        if (laundry.current) {
                            sl.laundry();
                        }
                        else {
                            sl.cancelLaundry();
                        }
                    }
                }
                if (checkout != null) {
                    Log.d("serviceAction","checkout not null");
                    if (dpStr.get("switch_"+checkout.dpId) != null) {
                        checkout.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+checkout.dpId)).toString());
                        if (checkout.current) {
                            sl.checkout();
                        }
                        else {
                            sl.cancelCheckout();
                        }
                    }
                }
                if (dnd != null) {
                    Log.d("serviceAction","dnd not null");
                    if (dpStr.get("switch_"+dnd.dpId) != null) {
                        dnd.current = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_"+dnd.dpId)).toString());
                        if (dnd.current) {
                            sl.dnd();
                        }
                        else {
                            sl.cancelDnd();
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
        if (cleanup != null) {
            Log.d("serviceInfo","cleanup set");
            cleanupControlListener = roomReference.child("Cleanup").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Log.d("fireControl",my_room.RoomNumber+" cleanup "+snapshot.getValue().toString());
                        lastCleanup = Long.parseLong(snapshot.getValue().toString());
                        if (lastCleanup > 0) {
                            cleanupOn(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else if (lastCleanup == 0) {
                            cleanupOff(new IResultCallback() {
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
        if (laundry != null) {
            laundryControlListener = roomReference.child("Laundry").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        lastLaundry = Long.parseLong(snapshot.getValue().toString());
                        if (lastLaundry > 0) {
                            laundryOn(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else if (lastLaundry == 0) {
                            laundryOff(new IResultCallback() {
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
        if (dnd != null) {
            dndControlListener = roomReference.child("DND").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        lastDND = Long.parseLong(snapshot.getValue().toString());
                        if (lastDND > 0) {
                            dndOn(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else if (lastDND == 0) {
                            dndOff(new IResultCallback() {
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
        if (checkout != null) {
            checkoutControlListener = roomReference.child("Checkout").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        lastCheckout = Long.parseLong(snapshot.getValue().toString());
                        if (lastCheckout > 0) {
                            checkoutOn(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else if (lastCheckout == 0) {
                            checkoutOff(new IResultCallback() {
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
    }

    @Override
    public void removeFirebaseDevicesControl(DatabaseReference roomReference) {
        if (cleanupControlListener != null) {
            roomReference.child("Cleanup").removeEventListener(cleanupControlListener);
        }
        if (laundryControlListener != null) {
            roomReference.child("Laundry").removeEventListener(laundryControlListener);
        }
        if (dndControlListener != null) {
            roomReference.child("DND").removeEventListener(dndControlListener);
        }
        if (checkoutControlListener != null) {
            roomReference.child("Checkout").removeEventListener(checkoutControlListener);
        }
    }
}
