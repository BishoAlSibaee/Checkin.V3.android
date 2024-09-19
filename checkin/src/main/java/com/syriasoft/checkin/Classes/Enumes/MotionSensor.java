package com.syriasoft.checkin.Classes.Enumes;

public enum MotionSensor {
    zig_qt,
    zig_pir;

    MotionSensor getMyEnum(String code) {
        for (MotionSensor cur:MotionSensor.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsMotionSensor(String code) {
        for (MotionSensor cur:MotionSensor.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
