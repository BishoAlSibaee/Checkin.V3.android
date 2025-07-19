package com.syriasoft.server.Classes.Enumes;

public enum Power {
    zig_kg,
    zig_pc;

    Power getMyEnum(String code) {
        for (Power cur:Power.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsPower(String code) {
        for (Power cur:Power.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
