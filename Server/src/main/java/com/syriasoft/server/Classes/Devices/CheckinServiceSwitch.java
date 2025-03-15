package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.ServiceListener;
import com.syriasoft.server.Classes.Interfaces.SetFirebaseDevicesControl;
import com.syriasoft.server.Classes.Interfaces.SetInitialValues;
import com.syriasoft.server.Classes.PROJECT_VARIABLES;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Interface.RequestCallback;
import com.syriasoft.server.MyApp;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CheckinServiceSwitch extends CheckinDevice implements SetInitialValues, Listen, SetFirebaseDevicesControl {

    public DeviceDPBool cleanup;
    public DeviceDPBool laundry;
    public DeviceDPBool checkout;
    public DeviceDPBool dnd;
    public DeviceDPBool light;

    boolean serviceRoom;
    int cleanupRoom;
    int laundryRoom;
    int dndRoom;
    int checkoutRoom;
    ValueEventListener cleanupControlListener;
    ValueEventListener laundryControlListener;
    ValueEventListener dndControlListener;
    ValueEventListener checkoutControlListener;

    public long lastCleanup,lastLaundry,lastCheckout,lastDND;

    public CheckinServiceSwitch(DeviceBean device, Room room) {
        super(device,room);
        Log.d("serviceButtons"+my_room.RoomNumber,"construct service switch");
        if (MyApp.My_PROJECT.projectName.equals("apiTest")) {
            my_room.fireRoom.child("ServiceButtons").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("serviceButtons"+my_room.RoomNumber,"reading values done");
                    if (snapshot.getValue() != null) {
                        Log.d("serviceButtons"+my_room.RoomNumber,"not null");
                        serviceRoom = true;
                        for (DataSnapshot ss : snapshot.getChildren()) {
                            if (Objects.requireNonNull(ss.getValue()).toString().equals("cleanup")) {
                                cleanupRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                            else if (Objects.requireNonNull(ss.getValue()).toString().equals("laundry")) {
                                laundryRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                            else if (Objects.requireNonNull(ss.getValue()).toString().equals("dnd")) {
                                dndRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                            else if (Objects.requireNonNull(ss.getValue()).toString().equals("checkout")) {
                                checkoutRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                        }
                        Log.d("serviceButtons"+my_room.RoomNumber,"cleanup "+cleanupRoom+" laundry "+laundryRoom+" dnd "+dndRoom+" checkout "+checkoutRoom);
                    }
                    else {
                        Log.d("serviceButtons"+my_room.RoomNumber,"null");
                        serviceRoom = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public CheckinServiceSwitch(DeviceBean device, Room room, RequestCallback callback) {
        super(device,room);
        Log.d("serviceButtons"+my_room.RoomNumber,"construct service switch");
        if (MyApp.My_PROJECT.projectName.equals("apiTest")) {
            my_room.fireRoom.child("ServiceButtons").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("serviceButtons"+my_room.RoomNumber,"reading values done");
                    if (snapshot.getValue() != null) {
                        Log.d("serviceButtons"+my_room.RoomNumber,"not null");
                        serviceRoom = true;
                        for (DataSnapshot ss : snapshot.getChildren()) {
                            if (Objects.requireNonNull(ss.getValue()).toString().equals("cleanup")) {
                                cleanupRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                            else if (Objects.requireNonNull(ss.getValue()).toString().equals("laundry")) {
                                laundryRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                            else if (Objects.requireNonNull(ss.getValue()).toString().equals("dnd")) {
                                dndRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                            else if (Objects.requireNonNull(ss.getValue()).toString().equals("checkout")) {
                                checkoutRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
                            }
                        }
                        Log.d("serviceButtons"+my_room.RoomNumber,"cleanup "+cleanupRoom+" laundry "+laundryRoom+" dnd "+dndRoom+" checkout "+checkoutRoom);
                    }
                    else {
                        Log.d("serviceButtons"+my_room.RoomNumber,"null");
                        serviceRoom = false;
                    }
                    callback.onSuccess();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFail(error.getMessage());
                }
            });
        }
        else {
            callback.onSuccess();
        }
    }

    public CheckinServiceSwitch(DeviceBean device, Suite suite) {
        super(device,suite);
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

    public void lightOn(IResultCallback callback) {
        if (light != null) {
            light.turnOn(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        }
    }

    public void lightOff(IResultCallback callback) {
        if (light != null) {
            light.turnOff(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    callback.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }
            });
        }
    }

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        Log.d("setInitValues"+device.name , "service");
//        if (MyApp.isInternetConnected) {
//            if (my_room != null) {
//                my_room.fireRoom.child("ServiceButtons").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Log.d("serviceButtons"+my_room.RoomNumber,"reading values done");
//                        if (snapshot.getValue() != null) {
//                            Log.d("serviceButtons"+my_room.RoomNumber,"not null "+snapshot.getChildren());
//                            serviceRoom = true;
//                            for (DataSnapshot ss : snapshot.getChildren()) {
//                                if (Objects.requireNonNull(ss.getValue()).toString().equals("cleanup")) {
//                                    cleanupRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
//                                }
//                                else if (Objects.requireNonNull(ss.getValue()).toString().equals("laundry")) {
//                                    laundryRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
//                                }
//                                else if (Objects.requireNonNull(ss.getValue()).toString().equals("dnd")) {
//                                    dndRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
//                                }
//                                else if (Objects.requireNonNull(ss.getValue()).toString().equals("checkout")) {
//                                    checkoutRoom = Integer.parseInt(Objects.requireNonNull(ss.getKey()));
//                                }
//                            }
//                            Log.d("serviceButtons"+my_room.RoomNumber,"cleanup "+cleanupRoom+" laundry "+laundryRoom+" dnd "+dndRoom+" checkout "+checkoutRoom);
//                        }
//                        else {
//                            Log.d("serviceButtons"+my_room.RoomNumber,"null");
//                            serviceRoom = false;
//                        }
//
//                        if (serviceRoom) {
//                            for (DeviceDP dp : deviceDPS) {
//                                if (dp.dpId == cleanupRoom) {
//                                    cleanup = (DeviceDPBool) dp;
//                                }
//                                else if (dp.dpId == laundryRoom) {
//                                    laundry = (DeviceDPBool) dp;
//                                }
//                                else if (dp.dpId == checkoutRoom) {
//                                    checkout = (DeviceDPBool) dp;
//                                }
//                                else if (dp.dpId == dndRoom) {
//                                    dnd = (DeviceDPBool) dp;
//                                }
//                            }
//                            Log.d("serviceButtons"+my_room.RoomNumber,"set room buttons "+deviceDPS.size());
//                        }
//                        else {
//                            for (DeviceDP dp : deviceDPS) {
//                                if (dp.dpId == PROJECT_VARIABLES.cleanupButton) {
//                                    cleanup = (DeviceDPBool) dp;
//                                }
//                                else if (dp.dpId == PROJECT_VARIABLES.laundryButton) {
//                                    laundry = (DeviceDPBool) dp;
//                                }
//                                else if (dp.dpId == PROJECT_VARIABLES.checkoutButton) {
//                                    checkout = (DeviceDPBool) dp;
//                                }
//                                else if (dp.dpId == PROJECT_VARIABLES.dndButton) {
//                                    dnd = (DeviceDPBool) dp;
//                                }
//                            }
//                        }
//
//                        if (cleanup != null) {
//                            cleanup.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(cleanup.dpId))).toString());
//                            if (my_room != null) {
//                                if (my_room.roomStatus == 2) {
//                                    if (cleanup.current) {
//                                        my_room.fireRoom.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                    }
//                                    else {
//                                        my_room.fireRoom.child("Cleanup").setValue(0);
//                                    }
//                                }
//                            }
//                            else if (my_suite != null) {
//                                if (my_suite.Status == 1) {
//                                    if (cleanup.current) {
//                                        my_suite.fireSuite.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                    }
//                                    else {
//                                        my_suite.fireSuite.child("Cleanup").setValue(0);
//                                    }
//                                }
//                            }
//                            Log.d("serviceInfo",device.name+" cleanup "+cleanup.dpId+" "+cleanup.current);
//                        }
//                        if (laundry != null) {
//                            laundry.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(laundry.dpId))).toString());
//                            if (my_room != null) {
//                                if (my_room.roomStatus == 2) {
//                                    if (laundry.current) {
//                                        my_room.fireRoom.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                    }
//                                    else {
//                                        my_room.fireRoom.child("Laundry").setValue(0);
//                                    }
//                                }
//                            }
//                            else if (my_suite != null) {
//                                if (my_suite.Status == 1) {
//                                    if (laundry.current) {
//                                        my_suite.fireSuite.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                    }
//                                    else {
//                                        my_suite.fireSuite.child("Laundry").setValue(0);
//                                    }
//                                }
//                            }
//                            Log.d("serviceInfo",device.name+" laundry "+laundry.dpId+" "+laundry.current);
//                        }
//                        if (checkout != null) {
//                            checkout.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(checkout.dpId))).toString());
//                            if (my_room != null) {
//                                if (my_room.roomStatus == 2) {
//                                    if (checkout.current) {
//                                        my_room.fireRoom.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                    }
//                                    else {
//                                        my_room.fireRoom.child("Checkout").setValue(0);
//                                    }
//                                }
//                            }
//                            else if (my_suite != null) {
//                                //if (my_suite.Status == 1) {
//                                if (checkout.current) {
//                                    my_suite.fireSuite.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                }
//                                else {
//                                    my_suite.fireSuite.child("Checkout").setValue(0);
//                                }
//                                //}
//                            }
//                            Log.d("serviceInfo",device.name+" checkout "+checkout.dpId+" "+checkout.current);
//                        }
//                        if (dnd != null) {
//                            dnd.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dnd.dpId))).toString());
//                            if (my_room != null) {
//                                if (my_room.roomStatus == 2) {
//                                    if (dnd.current) {
//                                        my_room.fireRoom.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                    }
//                                    else {
//                                        my_room.fireRoom.child("DND").setValue(0);
//                                    }
//                                }
//                            }
//                            else if (my_suite != null) {
//                                //if (my_suite.Status == 1) {
//                                if (dnd.current) {
//                                    my_suite.fireSuite.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                                }
//                                else {
//                                    my_suite.fireSuite.child("DND").setValue(0);
//                                }
//                                //}
//                            }
//                            Log.d("serviceInfo",device.name+" dnd "+dnd.dpId+" "+dnd.current);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        }
//        else {
//            if (serviceRoom) {
//                for (DeviceDP dp : deviceDPS) {
//                    if (dp.dpId == cleanupRoom) {
//                        cleanup = (DeviceDPBool) dp;
//                    }
//                    else if (dp.dpId == laundryRoom) {
//                        laundry = (DeviceDPBool) dp;
//                    }
//                    else if (dp.dpId == checkoutRoom) {
//                        checkout = (DeviceDPBool) dp;
//                    }
//                    else if (dp.dpId == dndRoom) {
//                        dnd = (DeviceDPBool) dp;
//                    }
//                }
//                Log.d("serviceButtons"+my_room.RoomNumber,"set room buttons "+deviceDPS.size());
//            }
//            else {
//                for (DeviceDP dp : deviceDPS) {
//                    if (dp.dpId == PROJECT_VARIABLES.cleanupButton) {
//                        cleanup = (DeviceDPBool) dp;
//                    }
//                    else if (dp.dpId == PROJECT_VARIABLES.laundryButton) {
//                        laundry = (DeviceDPBool) dp;
//                    }
//                    else if (dp.dpId == PROJECT_VARIABLES.checkoutButton) {
//                        checkout = (DeviceDPBool) dp;
//                    }
//                    else if (dp.dpId == PROJECT_VARIABLES.dndButton) {
//                        dnd = (DeviceDPBool) dp;
//                    }
//                }
//            }
//
//            if (cleanup != null) {
//                cleanup.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(cleanup.dpId))).toString());
//                if (my_room != null) {
//                    if (my_room.roomStatus == 2) {
//                        if (cleanup.current) {
//                            my_room.fireRoom.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                        }
//                        else {
//                            my_room.fireRoom.child("Cleanup").setValue(0);
//                        }
//                    }
//                }
//                else if (my_suite != null) {
//                    if (my_suite.Status == 1) {
//                        if (cleanup.current) {
//                            my_suite.fireSuite.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                        }
//                        else {
//                            my_suite.fireSuite.child("Cleanup").setValue(0);
//                        }
//                    }
//                }
//                Log.d("serviceInfo",device.name+" cleanup "+cleanup.dpId+" "+cleanup.current);
//            }
//            if (laundry != null) {
//                laundry.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(laundry.dpId))).toString());
//                if (my_room != null) {
//                    if (my_room.roomStatus == 2) {
//                        if (laundry.current) {
//                            my_room.fireRoom.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                        }
//                        else {
//                            my_room.fireRoom.child("Laundry").setValue(0);
//                        }
//                    }
//                }
//                else if (my_suite != null) {
//                    if (my_suite.Status == 1) {
//                        if (laundry.current) {
//                            my_suite.fireSuite.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                        }
//                        else {
//                            my_suite.fireSuite.child("Laundry").setValue(0);
//                        }
//                    }
//                }
//                Log.d("serviceInfo",device.name+" laundry "+laundry.dpId+" "+laundry.current);
//            }
//            if (checkout != null) {
//                checkout.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(checkout.dpId))).toString());
//                if (my_room != null) {
//                    if (my_room.roomStatus == 2) {
//                        if (checkout.current) {
//                            my_room.fireRoom.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                        }
//                        else {
//                            my_room.fireRoom.child("Checkout").setValue(0);
//                        }
//                    }
//                }
//                else if (my_suite != null) {
//                    //if (my_suite.Status == 1) {
//                    if (checkout.current) {
//                        my_suite.fireSuite.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                    }
//                    else {
//                        my_suite.fireSuite.child("Checkout").setValue(0);
//                    }
//                    //}
//                }
//                Log.d("serviceInfo",device.name+" checkout "+checkout.dpId+" "+checkout.current);
//            }
//            if (dnd != null) {
//                dnd.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dnd.dpId))).toString());
//                if (my_room != null) {
//                    if (my_room.roomStatus == 2) {
//                        if (dnd.current) {
//                            my_room.fireRoom.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                        }
//                        else {
//                            my_room.fireRoom.child("DND").setValue(0);
//                        }
//                    }
//                }
//                else if (my_suite != null) {
//                    //if (my_suite.Status == 1) {
//                    if (dnd.current) {
//                        my_suite.fireSuite.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//                    }
//                    else {
//                        my_suite.fireSuite.child("DND").setValue(0);
//                    }
//                    //}
//                }
//                Log.d("serviceInfo",device.name+" dnd "+dnd.dpId+" "+dnd.current);
//            }
//        }
        if (serviceRoom) {
            for (DeviceDP dp : deviceDPS) {
                if (dp.dpId == cleanupRoom) {
                    cleanup = (DeviceDPBool) dp;
                }
                else if (dp.dpId == laundryRoom) {
                    laundry = (DeviceDPBool) dp;
                }
                else if (dp.dpId == checkoutRoom) {
                    checkout = (DeviceDPBool) dp;
                }
                else if (dp.dpId == dndRoom) {
                    dnd = (DeviceDPBool) dp;
                }
            }
            Log.d("serviceButtons"+my_room.RoomNumber,"set room buttons "+deviceDPS.size());
        }
        else {
            for (DeviceDP dp : deviceDPS) {
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
                else if (dp.dpId == PROJECT_VARIABLES.lightButton) {
                    light = (DeviceDPBool) dp;
                 }
            }
        }

        if (cleanup != null) {
            cleanup.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(cleanup.dpId))).toString());
            if (my_room != null) {
                if (my_room.roomStatus == 2) {
                    if (cleanup.current) {
                        my_room.fireRoom.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                    else {
                        my_room.fireRoom.child("Cleanup").setValue(0);
                    }
                }
            }
            else if (my_suite != null) {
                if (my_suite.Status == 1) {
                    if (cleanup.current) {
                        my_suite.fireSuite.child("Cleanup").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                    else {
                        my_suite.fireSuite.child("Cleanup").setValue(0);
                    }
                }
            }
            Log.d("serviceInfo",device.name+" cleanup "+cleanup.dpId+" "+cleanup.current);
        }
        if (laundry != null) {
            laundry.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(laundry.dpId))).toString());
            if (my_room != null) {
                if (my_room.roomStatus == 2) {
                    if (laundry.current) {
                        my_room.fireRoom.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                    else {
                        my_room.fireRoom.child("Laundry").setValue(0);
                    }
                }
            }
            else if (my_suite != null) {
                if (my_suite.Status == 1) {
                    if (laundry.current) {
                        my_suite.fireSuite.child("Laundry").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                    else {
                        my_suite.fireSuite.child("Laundry").setValue(0);
                    }
                }
            }
            Log.d("serviceInfo",device.name+" laundry "+laundry.dpId+" "+laundry.current);
        }
        if (checkout != null) {
            checkout.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(checkout.dpId))).toString());
            if (my_room != null) {
                if (my_room.roomStatus == 2) {
                    if (checkout.current) {
                        my_room.fireRoom.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                    else {
                        my_room.fireRoom.child("Checkout").setValue(0);
                    }
                }
            }
            else if (my_suite != null) {
                //if (my_suite.Status == 1) {
                if (checkout.current) {
                    my_suite.fireSuite.child("Checkout").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
                else {
                    my_suite.fireSuite.child("Checkout").setValue(0);
                }
                //}
            }
            Log.d("serviceInfo",device.name+" checkout "+checkout.dpId+" "+checkout.current);
        }
        if (dnd != null) {
            dnd.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(dnd.dpId))).toString());
            if (my_room != null) {
                if (my_room.roomStatus == 2) {
                    if (dnd.current) {
                        my_room.fireRoom.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    }
                    else {
                        my_room.fireRoom.child("DND").setValue(0);
                    }
                }
            }
            else if (my_suite != null) {
                //if (my_suite.Status == 1) {
                if (dnd.current) {
                    my_suite.fireSuite.child("DND").setValue(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                }
                else {
                    my_suite.fireSuite.child("DND").setValue(0);
                }
                //}
            }
            Log.d("serviceInfo",device.name+" dnd "+dnd.dpId+" "+dnd.current);
        }
        if (light != null) {
            light.current = Boolean.parseBoolean(Objects.requireNonNull(device.dps.get(String.valueOf(light.dpId))).toString());
            if (light.current) {
                my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).setValue(3);
            }
            else {
                my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).setValue(0);
            }
        }
        callback.onSuccess();
    }

    @Override
    public void listen(DeviceAction action) {
        ServiceListener sl = (ServiceListener) action;
        this.control.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                if (MyApp.isInternetConnected) {
                    if (dpStr.length() < 12) {
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (cleanup != null) {
                                boolean xxx = c.getBoolean(String.valueOf(cleanup.dpId));
                                if (!cleanup.current && xxx) {
                                    sl.cleanup();
                                    Log.d("serviceAction","add");
                                } else if (cleanup.current && !xxx) {
                                    sl.cancelCleanup();
                                }
                                cleanup.current = c.getBoolean(String.valueOf(cleanup.dpId));
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (laundry != null) {
                                boolean xxx = c.getBoolean(String.valueOf(laundry.dpId));
                                if (!laundry.current && xxx) {
                                    sl.laundry();
                                } else if (laundry.current && !xxx){
                                    sl.cancelLaundry();
                                }
                                laundry.current = c.getBoolean(String.valueOf(laundry.dpId));
                                Log.d("serviceAction", "laundry " + laundry.current);
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (dnd != null) {
                                boolean xxx = c.getBoolean(String.valueOf(dnd.dpId));
                                if (!dnd.current && xxx) {
                                    sl.dnd();
                                } else if (dnd.current && !xxx) {
                                    sl.cancelDnd();
                                }
                                dnd.current = xxx;
                                Log.d("serviceAction", "dnd " + dnd.current);
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            if (checkout != null) {
                                JSONObject c = new JSONObject(dpStr);
                                if (checkout != null) {
                                    checkout.current = c.getBoolean(String.valueOf(checkout.dpId));
                                    if (checkout.current) {
                                        sl.checkout();
                                    } else {
                                        sl.cancelCheckout();
                                    }
                                    Log.d("serviceAction", "checkout " + checkout.current);
                                }
                            }

                        } catch (Exception e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (light != null) {
                                boolean xxx = c.getBoolean(String.valueOf(light.dpId));
                                if (!light.current && xxx) {
                                    sl.lightOn();
                                } else if (light.current && !xxx) {
                                    sl.lightOff();
                                }
                                light.current = xxx;
                                Log.d("serviceAction", "light " + light.current);
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                    }
                }
                else {
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (cleanup != null) {
                                cleanup.current = c.getBoolean(String.valueOf(cleanup.dpId));
                                if (cleanup.current) {
                                    sl.cleanup();
                                } else {
                                    sl.cancelCleanup();
                                }
                                Log.d("serviceAction", "cleanup " + cleanup.current);
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (laundry != null) {
                                laundry.current = c.getBoolean(String.valueOf(laundry.dpId));
                                if (laundry.current) {
                                    sl.laundry();
                                } else {
                                    sl.cancelLaundry();
                                }
                                Log.d("serviceAction", "laundry " + laundry.current);
                            }

                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (dnd != null) {
                                dnd.current = c.getBoolean(String.valueOf(dnd.dpId));
                                if (dnd.current) {
                                    sl.dnd();
                                } else {
                                    sl.cancelDnd();
                                }
                                Log.d("serviceAction", "dnd " + dnd.current);
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                            JSONObject c = new JSONObject(dpStr);
                            if (checkout != null) {
                                checkout.current = c.getBoolean(String.valueOf(checkout.dpId));
                                if (checkout.current) {
                                    sl.checkout();
                                } else {
                                    sl.cancelCheckout();
                                }
                                Log.d("serviceAction", "checkout " + checkout.current);
                            }
                        } catch (JSONException e) {
                            Log.d("serviceAction", "-");
                        }
                        try {
                        JSONObject c = new JSONObject(dpStr);
                        if (light != null) {
                            light.current = c.getBoolean(String.valueOf(light.dpId));
                            if (light.current) {
                                sl.lightOn();
                            } else {
                                sl.lightOff();
                            }
                            Log.d("serviceAction", "light " + light.current);
                        }
                    } catch (JSONException e) {
                        Log.d("serviceAction", "-");
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
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
            cleanupControlListener = roomReference.child("Cleanup").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Log.d("serviceAction",my_room.RoomNumber+" cleanup "+snapshot.getValue().toString());
                        lastCleanup = Long.parseLong(snapshot.getValue().toString());
                        if (lastCleanup > 0) {
                            if (!cleanup.current) {
                                cleanupOn(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        else if (lastCleanup == 0) {
                            if (cleanup.current) {
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
                        Log.d("serviceAction",my_room.RoomNumber+" laundry "+snapshot.getValue().toString());
                        lastLaundry = Long.parseLong(snapshot.getValue().toString());
                        if (lastLaundry > 0) {
                            if (!laundry.current) {
                                laundryOn(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        else if (lastLaundry == 0) {
                            if (laundry.current) {
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
                        Log.d("serviceAction",my_room.RoomNumber+" dnd "+snapshot.getValue().toString());
                        lastDND = Long.parseLong(snapshot.getValue().toString());
                        if (lastDND > 0) {
                            if (!dnd.current) {
                                dndOn(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        else if (lastDND == 0) {
                            if (dnd.current) {
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
                        Log.d("serviceAction",my_room.RoomNumber+" checkout "+snapshot.getValue().toString());
                        lastCheckout = Long.parseLong(snapshot.getValue().toString());
                        if (lastCheckout > 0) {
                            if (!checkout.current) {
                                checkoutOn(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }

                        }
                        else if (lastCheckout == 0) {
                            if (checkout.current) {
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        if (light != null) {
            if (my_room != null) {
                boolean[] first = {true};
                if (my_room.devicesControlReference != null) {
                    my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                int value = Integer.parseInt(snapshot.getValue().toString());
                                if (value == 1) {
                                    if (!light.getCurrent() && !first[0]) {
                                        lightOn(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).setValue(0);
                                            }

                                            @Override
                                            public void onSuccess() {
                                                my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).setValue(3);
                                            }
                                        });
                                    }
                                }
                                else if (value == 2) {
                                    if (light.getCurrent() && !first[0]) {
                                        lightOff(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).setValue(3);
                                            }

                                            @Override
                                            public void onSuccess() {
                                                my_room.devicesControlReference.child(device.name).child(String.valueOf(light.dpId)).setValue(0);
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
