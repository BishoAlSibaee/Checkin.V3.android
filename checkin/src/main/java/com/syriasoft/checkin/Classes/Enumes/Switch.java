package com.syriasoft.checkin.Classes.Enumes;

public enum Switch {
    zig_kg,
    zig_wxkg;

    Switch getMyEnum(String code) {
        for (Switch cur:Switch.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsSwitch(String code) {
        for (Switch cur:Switch.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
