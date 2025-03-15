package com.syriasoft.mobilecheckdevice.Classes.Enumes;

public enum DoorSensor {
    zig_mcs;

    DoorSensor getMyEnum(String code) {
        for (DoorSensor cur:DoorSensor.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsDoorSensor(String code) {
        for (DoorSensor cur:DoorSensor.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
