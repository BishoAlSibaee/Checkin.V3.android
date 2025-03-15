package com.syriasoft.projectseditor.Classes;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutMood {

    boolean power ;
    boolean lights ;
    boolean ac ;
    boolean curtain ;

    int active;

    public CheckoutMood(String actions,int active) {
        if (actions != null) {
            try {
                JSONObject res = new JSONObject(actions);
                power = res.getBoolean("power");
                lights = res.getBoolean("lights");
                ac = res.getBoolean("ac");
                curtain = res.getBoolean("curtain");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isActive() {
        return active > 0;
    }

    public boolean isLights() {
        return lights;
    }

    public boolean isAc() {
        return ac;
    }

    public boolean isCurtain() {
        return curtain;
    }

    public boolean isPower() {
        return power;
    }

//    public void startCheckoutMood(Room room) {
//        if (PROJECT_VARIABLES.checkoutMood.isActive()) {
//            if (PROJECT_VARIABLES.getIsPowerOffAfterCheckout()) {
//                room.powerOffAfterMinutes(PROJECT_VARIABLES.CheckoutModeTime, new RequestCallback() {
//                    @Override
//                    public void onSuccess() {
//                        if (MyApp.My_PROJECT.projectName.equals("P0003")) {
//                            if (room.isHasSwitch()) {
//                                for (CheckinSwitch cs:room.switches) {
//                                    if (cs.device.name.contains("Switch6")) {
//                                        cs.dp2.turnOn(new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFail(String error) {
//
//                    }
//                });
//            }
//            else {
//                room.powerByCardAfterMinutes(PROJECT_VARIABLES.CheckoutModeTime, new RequestCallback() {
//                    @Override
//                    public void onSuccess() {
//                        if (MyApp.My_PROJECT.projectName.equals("P0003")) {
//                            if (room.isHasSwitch()) {
//                                for (CheckinSwitch cs:room.switches) {
//                                    if (cs.device.name.contains("Switch6")) {
//                                        cs.dp2.turnOn(new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFail(String error) {
//
//                    }
//                });
//            }
//        }
//        else {
//            if (PROJECT_VARIABLES.getIsPowerOffAfterCheckout()) {
//                room.powerOffRoom(new IResultCallback() {
//                    @Override
//                    public void onError(String code, String error) {
//
//                    }
//
//                    @Override
//                    public void onSuccess() {
//
//                    }
//                });
//            }
//            else {
//                room.powerByCardRoom(new IResultCallback() {
//                    @Override
//                    public void onError(String code, String error) {
//
//                    }
//
//                    @Override
//                    public void onSuccess() {
//
//                    }
//                });
//            }
//        }
//    }
}
