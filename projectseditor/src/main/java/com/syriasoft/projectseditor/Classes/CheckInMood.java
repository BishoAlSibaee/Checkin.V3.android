package com.syriasoft.projectseditor.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CheckInMood {

    int active;
    boolean power ;
    boolean lights ;
    boolean ac ;
    boolean curtain ;

    public CheckInMood(String actions,int active) {
        this.active = active;
        if (actions != null) {
            try {
                JSONObject res = new JSONObject(actions);
                power = res.getBoolean("power");
                lights = res.getBoolean("lights");
                ac = res.getBoolean("ac");
                curtain = res.getBoolean("curtain");
            } catch (JSONException e) {
                Log.d("checkinMoodError", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    public boolean isActive() {
        return this.active > 0;
    }

    public boolean isPower() {
        return power;
    }

    public boolean isLights() {
        return lights;
    }

    public boolean isCurtain() {
        return curtain;
    }

    public boolean isAc() {
        return ac;
    }

//    public void startCheckinMood(Room room) {
//        Log.d("checkinMood","checkin mood run");
//        room.getRoomReservationType(new GetReservationType() {
//            @Override
//            public void onSuccess(int type) {
//                if (type == 1) {
//                    // by link
//                    Log.d("checkinMood"+room.RoomNumber,"1");
//                    room.powerOnRoom(new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            if (!MyApp.My_PROJECT.projectName.equals("P0003")) {
//                                Timer t = new Timer();
//                                t.schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        if (isActive()) {
//                                            if (isAc()) {
//                                                room.turnAcOn();
//                                            }
//                                            if (isCurtain()) {
//                                                room.openCurtain();
//                                            }
//                                            if (isLights()) {
//                                                room.turnLightsOn();
//                                            }
//                                        }
//                                    }
//                                },5000);
//                            }
//                        }
//                    });
//                }
//                else if (type == 0) {
//                    // by card
//                    Log.d("checkinMood"+room.RoomNumber,"0");
//                    if (isActive()) {
//                        Log.d("checkinMood"+room.RoomNumber,"active");
//                        if (isPower()) {
//                            Log.d("checkinMood"+room.RoomNumber,"power");
//                            room.powerOnRoom(new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    Log.d("checkinMood"+room.RoomNumber,"power success "+PROJECT_VARIABLES.CheckinModeTime);
//                                    if (!MyApp.My_PROJECT.projectName.equals("P0003")) {
//                                        Timer t = new Timer();
//                                        t.schedule(new TimerTask() {
//                                            @Override
//                                            public void run() {
//                                                if (isAc()) {
//                                                    room.turnAcOn();
//                                                }
//                                                if (isCurtain()) {
//                                                    room.openCurtain();
//                                                }
//                                                if (isLights()) {
//                                                    Log.d("checkinMood"+room.RoomNumber,"lights active");
//                                                    room.turnLightsOn();
//                                                }
//                                            }
//                                        },5000);
//                                    }
//                                    room.powerByCardAfterMinutes(PROJECT_VARIABLES.CheckinModeTime, new RequestCallback() {
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//
//                                        @Override
//                                        public void onFail(String error) {
//
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                        else {
//                            room.powerByCardRoom(new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//                    else {
//                        room.powerByCardRoom(new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//
//                            }
//
//                            @Override
//                            public void onSuccess() {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });
//    }

//    public void startCheckinMood(Room room,IResultCallback result) {
//        room.getRoomReservationType(new GetReservationType() {
//            @Override
//            public void onSuccess(int type) {
//                if (type == 1) {
//                    // by link
//                    room.powerOnRoom(new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                        }
//                    });
//                }
//                else if (type == 0) {
//                    // by card
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });
//        if (isActive()) {
//            if (isPower()) {
//                room.powerOnRoom(new IResultCallback() {
//                    @Override
//                    public void onError(String code, String error) {
//                        Log.e("checkinMood","code: "+code+" , error: "+error);
//                        result.onError(code,error);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        if (isLights()) {
//                            room.turnLightsOn();
//                        }
//                        if (isCurtain()) {
//                            room.openCurtain();
//                        }
//                        if (isAc()) {
//                            room.turnAcOn();
//                        }
//                        result.onSuccess();
//                        Log.d("checkinMood","checkin mood success on "+room.RoomNumber+" room");
//                    }
//                });
//            }
//            else {
//                room.powerByCardRoom(new IResultCallback() {
//                    @Override
//                    public void onError(String code, String error) {
//                        Log.e("checkinMood","code: "+code+" , error: "+error);
//                        result.onError(code,error);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        Log.d("checkinMood","checkin mood success on "+room.RoomNumber+" room");
//                        result.onSuccess();
//                    }
//                });
//            }
//        }
//        else {
//            result.onError("no code","checkin mood is inactive");
//        }
//    }

}
