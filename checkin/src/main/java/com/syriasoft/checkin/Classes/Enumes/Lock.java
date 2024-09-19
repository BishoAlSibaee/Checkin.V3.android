package com.syriasoft.checkin.Classes.Enumes;

public enum Lock {
    hotelms_4z_1;

    Lock getMyEnum(String code) {
        for (Lock cur:Lock.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsLock(String code) {
        for (Lock cur:Lock.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
