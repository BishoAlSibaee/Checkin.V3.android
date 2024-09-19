package com.example.hotelservicesstandalone.Classes.Devices;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Interfaces.LockListener;
import com.example.hotelservicesstandalone.Classes.Interfaces.RecordUnlock;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetFirebaseDevicesControl;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Property.Suite;
import com.example.hotelservicesstandalone.Classes.Tuya;
import com.example.hotelservicesstandalone.Interface.RequestOrder;
import com.example.hotelservicesstandalone.MyApp;
import com.example.hotelservicesstandalone.Classes.ZigbeeLock;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CheckinLock extends CheckinDevice implements Listen, SetFirebaseDevicesControl {

    long lastUnlockTime = 0;

    ValueEventListener lockControlListener;

    public CheckinLock(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinLock(DeviceBean device, Suite suite) {
        super(device,suite);
    }

    public void unlock(String clientId, String clientSecret,Context context,RequestOrder order) {
        ZigbeeLock.getTokenFromApi(clientId, clientSecret, context, new RequestOrder() {
            @Override
            public void onSuccess(String token) {
                ZigbeeLock.getTicketId(token, clientId, clientSecret, device.devId, context, new RequestOrder() {
                    @Override
                    public void onSuccess(String id) {
                        ZigbeeLock.unlockWithoutPassword(token, id, clientId, clientSecret, device.devId, context, new RequestOrder() {
                            @Override
                            public void onSuccess(String result) {
                                order.onSuccess(result);
                            }

                            @Override
                            public void onFailed(String error) {
                                order.onFailed(error);
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        order.onFailed(error);
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                order.onFailed(error);
            }
        });
    }

    @Override
    public void listen(DeviceAction action) {
        LockListener lock = (LockListener) action;
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("lockAction",dpStr.toString());
                if (dpStr.get("door_opened") != null) {
                    lock.unlocked();
                }
                if (dpStr.get("residual_electricity") != null) {
                    int bat = Integer.parseInt(Objects.requireNonNull(dpStr.get("residual_electricity")).toString());
                    lock.battery(bat);
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                me.online = online;
                lock.online(online);
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

    public void addClientUnlockOperationToDB(String projectUrl, RecordUnlock callback) {
        String url = projectUrl + "roomsManagement/addClientDoorOpen";
        StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("doorOpenResp", "ZB" + response);
            try {
                JSONObject result = new JSONObject(response);
                result.getString("result");
                if (result.getString("result").equals("success")) {
                    callback.onSuccess();
                }
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id", String.valueOf(my_room.id));
                return params;
            }
        };
    }

    public void addUserUnlockOperationToDB(String projectUrl,int user_id, RecordUnlock callback) {
        String url = projectUrl + "roomsManagement/addUserDoorOpen";
        StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    callback.onSuccess();
                }
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id", String.valueOf(my_room.id));
                params.put("user_id",String.valueOf(user_id));
                return params;
            }
        };
    }

    @Override
    public void setFirebaseDevicesControl(DatabaseReference controlReference) {
        lockControlListener = controlReference.child(device.name).child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    int value = Integer.parseInt(snapshot.getValue().toString());
                    if (value == 1) {
                        long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (now > (lastUnlockTime+5000)) {
                            unlock(Tuya.clientId, Tuya.clientSecret, MyApp.app, new RequestOrder() {
                                @Override
                                public void onSuccess(String token) {
                                    lastUnlockTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                                }

                                @Override
                                public void onFailed(String error) {
                                    controlReference.child(device.name).child("1").setValue(0);
                                }
                            });
                            addClientUnlockOperationToDB(MyApp.My_PROJECT.url, new RecordUnlock() {
                                @Override
                                public void onSuccess() {
                                    Log.d("unlockRecording",device.name+" done");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.d("unlockRecording",device.name+" error "+error);
                                }
                            });
                        }
                    }
                    else if (value > 1) {
                        long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (now > (lastUnlockTime+5000)) {
                            unlock(Tuya.clientId, Tuya.clientSecret, MyApp.app, new RequestOrder() {
                                @Override
                                public void onSuccess(String token) {
                                    lastUnlockTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                                }

                                @Override
                                public void onFailed(String error) {
                                    controlReference.child(device.name).child("1").setValue(0);
                                }
                            });
                            addUserUnlockOperationToDB(MyApp.My_PROJECT.url,value, new RecordUnlock() {
                                @Override
                                public void onSuccess() {
                                    Log.d("unlockRecording",device.name+" done");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.d("unlockRecording",device.name+" error "+error);
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

    @Override
    public void removeFirebaseDevicesControl(DatabaseReference controlReference) {
        if (lockControlListener != null) {
            controlReference.child(device.name).child("1").removeEventListener(lockControlListener);
        }
    }
}
