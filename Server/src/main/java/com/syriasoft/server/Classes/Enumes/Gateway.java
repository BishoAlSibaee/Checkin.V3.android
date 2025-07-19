package com.syriasoft.server.Classes.Enumes;

public enum Gateway {
    wf_zig_wg2,
    wg2_2b10s1w4z_56,
    wf_zig_wfcon,
    ca_zig_wg2;

    Gateway getMyEnum(String code) {
        for (Gateway cur:Gateway.values()) {
            if (code.equals(cur.toString())) {
                return cur;
            }
        }
        return null;
    }

    public static boolean getIsGateway(String code) {
        for (Gateway cur:Gateway.values()) {
            if (code.equals(cur.toString())) {
                return true;
            }
        }
        return false;
    }
}
