package com.syriasoft.mobilecheckdevice.Classes.Enumes;

public enum Curtain {
    zig_clkg,
    zig_cl;

    Curtain getMyEnum(String code) {
        for (Curtain cur:Curtain.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsCurtain(String code) {
        for (Curtain cur:Curtain.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
