package com.syriasoft.checkin.Classes.Enumes;

public enum AC {
    wf_wk,
    wf_kt,
    zig_wk;

    AC getMyEnum(String code) {
        for (AC ac:AC.values()) {
            if (code.equals(ac.toString())) {
                return ac;
            }
        }
        return null;
    }

    public static boolean getIsAc(String code) {
        for (AC ac:AC.values()) {
            if (code.equals(ac.toString())) {
                return true;
            }
        }
        return false;
    }
}
