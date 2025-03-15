package com.syriasoft.mobilecheckdevice.Classes;

import android.app.Activity;
import android.util.Log;

import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.DevicesListenerWatcherCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetDevicesCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetHomeDevicesCallback;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.getDeviceDataCallback;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
import com.syriasoft.mobilecheckdevice.Classes.Property.Suite;
import com.syriasoft.mobilecheckdevice.Dialogs.ProgressDialog;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.syriasoft.mobilecheckdevice.MyApp;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
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
    public static long ListenersWaitingTimeWorking = 20 * 60 * 1000;
    public static long ListenersWaitingTimeNotWorking = 5 * 60 * 1000;
    public static long LastListenersActionTime = 0;
    public static Timer ListenersTimerWorking;
    static int stopsCounter;

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

    public static void getProjectHomes(PROJECT project,LocalDataStore storage,ITuyaGetHomeListCallback result) {
        if (!Tuya.getHomesFromStorage(storage).isEmpty()) {
            Log.d("bootingOp","homes locally");
            result.onSuccess(getHomesFromStorage(storage));
        }
        else {
            Log.d("bootingOp","homes internet");
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
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
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
            }, (long) i * 2000);

        }
    }

    public static void getDevicesNoTimers(List<HomeBean> homeBeans, List<Room> rooms, GetDevicesCallback callback) {
        Log.d("getDevicesNew","______________");
        List<CheckinDevice> Devices = new ArrayList<>();
        final int[] counter = {0};
        HomeBean h = homeBeans.get(counter[0]);
        Log.d("getDevicesNew", h.getName());
        getHomeDevices(h, new GetHomeDevicesCallback() {
            @Override
            public void devices(List<DeviceBean> devices) {
                counter[0]++;
                Devices.addAll(Room.setRoomsDevices(rooms,devices,h));
                Log.d("getDevicesNew", "devices "+Devices.size());
                if (counter[0] == homeBeans.size()) {
                    Log.d("getDevicesNew", "finish");
                    callback.devices(Devices);
                }
                else {
                    Log.d("getDevicesNew", "next");
                    HomeBean h = homeBeans.get(counter[0]);
                    Log.d("getDevicesNew", h.getName());
                    getHomeDevices(h,this);
                }
            }

            @Override
            public void oError(String error) {
                callback.onError(error);
            }
        });
    }

    static void getHomeDevices(HomeBean h, GetHomeDevicesCallback callback) {
        TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                List<DeviceBean> devices = bean.getDeviceList();
                devices.addAll(bean.getSharedDeviceList());
                callback.devices(devices);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                callback.oError(errorMsg);
            }
        });
    }

    public static void getDevices2(List<HomeBean> homeBeans, List<Room> rooms, List<Suite> suites, GetDevicesCallback callback) {
        Runnable r = () -> {
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
                                Log.d("getDevicesRun","rooms "+Devices.size());
                                Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                                Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                                Log.d("getDevicesRun","suites "+Devices.size());
                                if (counter[0] == MyApp.PROJECT_HOMES.size()) {
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
                }, (long) i * 2000);

            }
        };
        Thread t = new Thread(r);
        t.start();
//        List<CheckinDevice> Devices = new ArrayList<>();
//        final int[] counter = {0};
//        Log.d("getDevicesRun","homes "+homeBeans.size());
//        for (int i = 0; i < homeBeans.size();i++) {
//            HomeBean h = homeBeans.get(i);
//            Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
//                        @Override
//                        public void onSuccess(HomeBean bean) {
//                            counter[0]++;
//                            Log.d("getDevicesRun","_______________________________________");
//                            Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getDeviceList().size());
//                            Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
//                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
//                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
//                            Log.d("getDevicesRun","rooms "+Devices.size());
//                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
//                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
//                            Log.d("getDevicesRun","suites "+Devices.size());
//                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
//                                callback.devices(Devices);
//                                Log.d("getDevicesRun","finish "+Devices.size());
//                            }
//                        }
//
//                        @Override
//                        public void onError(String errorCode, String errorMsg) {
//                            callback.onError(errorCode+" "+errorMsg);
//                        }
//                    });
//                }
//            }, (long) i * 2000);
//
//        }
    }

    public static void getLocalDevices(List<HomeBean> homeBeans, List<Room> rooms,List<Suite> suites, GetDevicesCallback callback) {
        List<CheckinDevice> Devices = new ArrayList<>();
        final int[] counter = {0};
        Log.d("getDevicesRun","_______________________________________");
        Log.d("getDevicesRun","homes "+homeBeans.size());
        if (homeBeans.size() > 0 ) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(0);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 1) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(1);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 2) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(2);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 3) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(3);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 4) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(4);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 5) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(5);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 6) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(6);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 7) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(7);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 8) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(8);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 9) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(9);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        if (homeBeans.size() > 10) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HomeBean h = homeBeans.get(10);
                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
                        @Override
                        public void onSuccess(HomeBean bean) {
                            counter[0]++;
                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
                                callback.devices(Devices);
                                Log.d("getDevicesRun","finish "+Devices.size());
                                Log.d("getDevicesRun","_______________________________________");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMsg) {
                            callback.onError(errorCode+" "+errorMsg);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
//        for (int i = 0; i < homeBeans.size();i++) {
//            HomeBean h = homeBeans.get(i);
//            Log.d("getDevicesRun","home "+counter[0]+" name "+h.getName()+" start");
//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    TuyaHomeSdk.newHomeInstance(h.getHomeId()).getHomeLocalCache(new ITuyaHomeResultCallback() {
//                        @Override
//                        public void onSuccess(HomeBean bean) {
//                            counter[0]++;
//                            Log.d("getDevicesRun","home "+counter[0]+" name "+bean.getName()+" devices "+bean.getDeviceList().size()+" shared "+bean.getSharedDeviceList().size());
//                            //Log.d("getDevicesRun","home "+counter[0]+" "+bean.getName()+" "+bean.getSharedDeviceList().size());
//                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getDeviceList(),h));
//                            Devices.addAll(Room.setRoomsDevices(rooms,bean.getSharedDeviceList(),h));
//                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getDeviceList(),h));
//                            Devices.addAll(Suite.setSuitesDevices(suites,bean.getSharedDeviceList(),h));
//                            if (counter[0] == MyApp.PROJECT_HOMES.size()) {
//                                callback.devices(Devices);
//                                Log.d("getDevicesRun","finish "+Devices.size());
//                                Log.d("getDevicesRun","_______________________________________");
//                            }
//                        }
//
//                        @Override
//                        public void onError(String errorCode, String errorMsg) {
//                            callback.onError(errorCode+" "+errorMsg);
//                        }
//                    });
//                }
//            };
//            Thread t = new Thread(r);
//            t.start();
//        }
    }

    public static void gettingInitialDevicesData(Activity act, List<CheckinDevice> devices,DevicesDataDB db, getDeviceDataCallback callback) {
        devicesIds.clear();
//        if (db.isDevicesDataSaved()) {
//            Log.d("bootingOp","getting devices data from db");
//            DevicesDataDB.getDevicesData(devices,db);
//            callback.onSuccess();
//        }
//        else {
            final int[] index = {0};
            if (PROJECT_VARIABLES.isGettingDevicesData) {
                Log.d("bootingOp","getting devices data from firebase");
                for (int i=0;i<devices.size();i++) {
                    CheckinDevice cd = devices.get(i);
                    cd.getDeviceDpsFromFirebase(new getDeviceDataCallback() {
                        @Override
                        public void onSuccess() {
                            index[0]++;
                            if (index[0] == devices.size()) {
                                //DevicesDataDB.saveDevicesData(devices,db);
                                db.getAll();
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
                Log.d("bootingOp","getting devices data from tuya");
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
                            if (cd.my_room != null) {
                                cd.my_room.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                            }
                            else if (cd.my_suite != null) {
                                cd.my_suite.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                            }
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
                                        p.close();
                                    }
                                });
                        }
                    },i*2000L);
                }
            }
        //}
    }

    public static void gettingInitialDevicesData(List<CheckinDevice> devices,DevicesDataDB db, getDeviceDataCallback callback) {
        devicesIds.clear();
        if (db.isDevicesDataSaved()) {
            Log.d("bootingOp","getting devices data from db");
            db.getAllDevicesData(devices, new RequestCallback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onFail(String error) {
                    Log.d("bootingOp","getting devices data from db error "+error);
                    callback.onError(error);
                }
            });

        }
        else {
            final int[] index = {0};
            if (PROJECT_VARIABLES.isGettingDevicesData) {
                Log.d("bootingOp","getting devices data from firebase");
                for (int i=0;i<devices.size();i++) {
                    CheckinDevice cd = devices.get(i);
                    cd.getDeviceDpsFromFirebase(new getDeviceDataCallback() {
                        @Override
                        public void onSuccess() {
                            index[0]++;
                            if (index[0] == devices.size()) {
                                DevicesDataDB.saveDevicesData(devices, db, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("bootingOp","devices data saved ");
                                        callback.onSuccess();
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        callback.onError(error);
                                    }
                                });
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
                Log.d("bootingOp","getting devices data from tuya");
                for (int i=0;i<devices.size();i++) {
                    CheckinDevice cd = devices.get(i);
                    devicesIds.add(cd.device.devId);
                    Timer t = new Timer();
                    int finalI = i;
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (cd.my_room != null) {
                                cd.my_room.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                            }
                            else if (cd.my_suite != null) {
                                cd.my_suite.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                            }
                            cd.getDeviceDPs(new getDeviceDataCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("devicesData",cd.device.name +" finish");
                                    if (finalI +1 == devices.size()) {
                                        PROJECT_VARIABLES.setGettingDevicesData(true);
                                        callback.onSuccess();
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Log.d("devicesData",cd.device.name +" error "+error);
                                    callback.onError(error);
                                }
                            });
                        }
                    },i*2000L);
                }
            }
        }
    }

    public static void gettingInitialDevicesData(List<CheckinDevice> devices, getDeviceDataCallback callback) {
        devicesIds.clear();
        final int[] index = {0};
        if (PROJECT_VARIABLES.isGettingDevicesData) {
            Log.d("bootingUp","getting devices data from firebase");
            for (int i=0;i<devices.size();i++) {
                CheckinDevice cd = devices.get(i);
                cd.getDeviceDpsFromFirebase(new getDeviceDataCallback() {
                    @Override
                    public void onSuccess() {
                        index[0]++;
                        if (devices.size() == index[0]) {
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
            Log.d("bootingUp","getting devices data from tuya");
            for (int i=0;i<devices.size();i++) {
                CheckinDevice cd = devices.get(i);
                devicesIds.add(cd.device.devId);
                Timer t = new Timer();
                int finalI = i;
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (cd.my_room != null) {
                            cd.my_room.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                        }
                        else if (cd.my_suite != null) {
                            cd.my_suite.devicesDataReference.child(cd.device.name).child("id").setValue(cd.device.devId);
                        }
                        cd.getDeviceDPs(new getDeviceDataCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("devicesData",cd.device.name +" finish");
                                if (finalI +1 == devices.size()) {
                                    PROJECT_VARIABLES.setGettingDevicesData(true);
                                    callback.onSuccess();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.d("devicesData",cd.device.name +" error "+error);
                                callback.onError(error);
                            }
                        });
                    }
                },i*2000L);
            }
        }

    }

    public static void setDevicesListenersWatcher(DevicesListenerWatcherCallback callback) {
        if (ListenersWorking) {
            setDevicesListenersWorking(callback);
        }
        else {
            setDevicesListenersNotWorking(callback);
        }
    }

    public static void setDevicesListenersWorking(DevicesListenerWatcherCallback callback) {
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
                    callback.onListenersStop();
                }
                else {
                    Log.d("devicesListenersListener", "listeners working 10");
                    callback.onListenersWork();
                }
            }
        }, ListenersWaitingTimeWorking);
    }

    public static void setDevicesListenersNotWorking(DevicesListenerWatcherCallback callback) {
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
                    callback.onListenersStop();
                }
                else {
                    Log.d("devicesListenersListener", "listeners working 5");
                    callback.onListenersWork();
                }
            }
        }, ListenersWaitingTimeNotWorking);
    }

    static void implementStopActions(Activity act) {
        ListenersWorking = false;
        PROJECT_VARIABLES.setDevicesListenersWorking(0);
        PROJECT_VARIABLES.addServerStop();
        Room.stopAllRoomListeners(MyApp.ROOMS);
        act.finish();
    }

    static void implementWorkActions() {
        ListenersWorking = true;
        PROJECT_VARIABLES.setDevicesListenersWorking(1);
    }

    public static void saveHomesToStorage(LocalDataStore storage , List<HomeBean> homes) {
        storage.saveInteger(homes.size(),"homesCount");
        for (int i=0;i<homes.size();i++) {
            storage.saveObject(homes.get(i),"home"+i);
        }
    }

    public static List<HomeBean> getHomesFromStorage(LocalDataStore storage) {
        List<HomeBean> homes = new ArrayList<>();
        int count = storage.getInteger("homesCount");
        for (int i=0;i<count;i++) {
            homes.add((HomeBean) storage.getObject("home"+i,HomeBean.class));
        }
        return homes;
    }

    public static void deleteHomesFromLocalStorage(LocalDataStore storage) {
        int count = storage.getInteger("homesCount");
        for (int i=0;i<count;i++) {
            storage.deleteObject("home"+i);
        }
        storage.deleteObject("homesCount");
    }

    DevicesListenerWatcherCallback setDevicesListenersCallback() {
        return new DevicesListenerWatcherCallback() {
            @Override
            public void onListenersStop() {

            }

            @Override
            public void onListenersWork() {

            }
        };
    }

}
