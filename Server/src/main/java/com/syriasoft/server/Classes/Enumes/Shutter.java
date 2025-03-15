package com.syriasoft.server.Classes.Enumes;

public enum Shutter {

    zig_kg;

    public static boolean getIsShutter(String code) {
        for (Shutter cur:Shutter.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
