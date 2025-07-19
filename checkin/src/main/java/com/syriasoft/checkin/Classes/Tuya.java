package com.syriasoft.checkin.Classes;

import android.app.Activity;
import android.util.Log;

import com.syriasoft.checkin.Classes.Devices.CheckinDevice;
import com.syriasoft.checkin.Classes.Interfaces.GetDevicesCallback;
import com.syriasoft.checkin.Classes.Interfaces.getDeviceDataCallback;
import com.syriasoft.checkin.Classes.Property.Room;
import com.syriasoft.checkin.Dialogs.ProgressDialog;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Tuya {

    public static String clientId = "d9hyvtdshnm3uvaun59d";
    public static String clientSecret = "825f9def941f456099798ccdc19112e9";
    static String CountryCode = "966";
    static User User;
    public static List<String> devicesIds = new ArrayList<>();

    public static boolean ListenersWorking = true;
    public static long ListenersWaitingTimeWorking = 10 * 60 * 1000;
    public static long ListenersWaitingTimeNotWorking = 5 * 30 * 1000;
    public static long LastListenersActionTime = 0;
    public static Timer ListenersTimerWorking;

    public Tuya(String countryCode) {
        CountryCode = countryCode;
    }

    public static void loginTuya(PROJECT project, ILoginCallback result) {
        TuyaHomeSdk.getUserInstance().loginWithEmail(CountryCode, project.TuyaUser, project.TuyaPassword, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                User = user;
                result.onSuccess(user);
            }

            @Override
            public void onError(String code, String error) {
                result.onError(code,error);
            }
        });
    }

    public static void getProjectHomes(PROJECT project,ITuyaGetHomeListCallback result) {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                List<HomeBean> homes = new ArrayList<>();
                if (project.projectName.equals("apiTest")) {
                    for (HomeBean hb : homeBeans) {
                        if (hb.getName().contains("Test")) {
                            homes.add(hb);
                        }
                    }
                }
                else {
                    for (HomeBean hb : homeBeans) {
                        if (hb.getName().contains(project.projectName)) {
                            homes.add(hb);
                        }
                    }
                }

                result.onSuccess(homes);
            }

            @Override
            public void onError(String errorCode, String error) {
                result.onError(errorCode,error);
            }
        });
    }

    public static void getDevices(List<HomeBean> homeBeans, List<Room> rooms, GetDevicesCallback callback) {
        List<CheckinDevice> Devices = new ArrayList<>();
        final int[] counter = {0};
        Log.d("getDevicesRun","homes "+homeBeans.size());
        for (int i = 0; i < homeBeans.size();i++) {
            HomeBean h = homeBeans.get(i);
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","_______________________________________");
                            Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getDeviceList().size());
                            Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            if (counter[0] == homeBeans.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            }, (long) i * 1000);

        }
    }

    public static void gettingInitialDevicesData(Activity act, List<CheckinDevice> devices, getDeviceDataCallback callback) {
        devicesIds.clear();
        final int[] index = {0};
        if (PROJECT_VARIABLES.isGettingDevicesData) {
            for (int i=0;i<devices.size();i++) {
                CheckinDevice cd = devices.get(i);
                cd.getDeviceDpsFromFirebase(new getDeviceDataCallback() {
                    @Override
                    public void onSuccess() {
                        index[0]++;
                        if (index[0] == devices.size()) {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }
        }
        else {
            final int[] progress = {0};
            ProgressDialog p = new ProgressDialog(act,"Getting Devices Data",devices.size());
            p.show();
            for (int i=0;i<devices.size();i++) {
                CheckinDevice cd = devices.get(i);
                devicesIds.add(cd.device.devId);
                Timer t = new Timer();
                int finalI = i;
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cd.my_room.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                        cd.getDeviceDPs(new getDeviceDataCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("devicesData",cd.device.name +" finish");
                                progress[0]++;
                                p.setProgress(progress[0]);
                                if (finalI +1 == devices.size()) {
                                    PROJECT_VARIABLES.setGettingDevicesData(true);
                                    callback.onSuccess();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.d("devicesData",cd.device.name +" error "+error);
                                callback.onError(error);
                                //new MessageDialog(error,"error",act);
                                p.close();
                            }
                        });
                    }
                },i*2000L);


            }
        }
    }

    public static void setDevicesListenersWatcher(Activity act,List<Room> rooms) {
        if (ListenersWorking) {
            setDevicesListenersWorking(act,rooms);
        }
        else {
            setDevicesListenersNotWorking(act,rooms);
        }

    }

    static void setDevicesListenersWorking(Activity act,List<Room> rooms) {
        Log.d("devicesListenersListener", "start 10");
        if (ListenersTimerWorking == null) {
            ListenersTimerWorking = new Timer();
        }
        ListenersTimerWorking.schedule(new TimerTask() {
            @Override
            public void run() {
                long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                if (now > (LastListenersActionTime + ListenersWaitingTimeWorking)) {
                    Log.d("devicesListenersListener", "listeners stop 10");
                    implementStopActions(act,rooms);
                }
                else {
                    Log.d("devicesListenersListener", "listeners working 10");
                    implementWorkActions();
                    setDevicesListenersWatcher(act,rooms);
                }
            }
        }, ListenersWaitingTimeWorking);
    }

    static void setDevicesListenersNotWorking(Activity act,List<Room> rooms) {
        Log.d("devicesListenersListener", "start 5");
        if (ListenersTimerWorking == null) {
            ListenersTimerWorking = new Timer();
        }
        ListenersTimerWorking.schedule(new TimerTask() {
            @Override
            public void run() {
                long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                if (now > (LastListenersActionTime + ListenersWaitingTimeNotWorking)) {
                    Log.d("devicesListenersListener", "listeners stop 5");
                    implementStopActions(act,rooms);
                }
                else {
                    Log.d("devicesListenersListener", "listeners working 5");
                    implementWorkActions();
                    setDevicesListenersWatcher(act,rooms);
                }
            }
        }, ListenersWaitingTimeNotWorking);
    }

    static void implementStopActions(Activity act,List<Room> rooms) {
        ListenersWorking = false;
        PROJECT_VARIABLES.setDevicesListenersWorking(0);
        Room.stopAllRoomListeners(rooms);
        act.finish();
    }

    static void implementWorkActions() {
        ListenersWorking = true;
        PROJECT_VARIABLES.setDevicesListenersWorking(1);
    }
}
