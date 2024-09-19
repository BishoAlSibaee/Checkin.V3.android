package com.syriasoft.checkin.Classes.Devices;

import com.syriasoft.checkin.Classes.Enumes.DpTypes;

public class DeviceDP {
    public long dpId;
    String dpName;
    DpTypes dpType;
    Object nowValue;
    CheckinDevice device;

    public DeviceDP(long dpId,String dpName,DpTypes dpType,CheckinDevice device) {
        this.dpId = dpId;
        this.dpName = dpName;
        this.dpType = dpType;
        this.device = device;
    }

    public DeviceDPBool getDpBoolean() {
        return (DeviceDPBool) this;
    }

    public DeviceDPValue getDpValue() {
        return (DeviceDPValue) this;
    }

    public DeviceDPEnum getDpEnum() {
        return (DeviceDPEnum) this;
    }

    public static boolean isBoolean(Object o) {
        try {
            if(Boolean.parseBoolean(o.toString())) {
                return true;
            }
            else {
                return false;
            }
        }catch (Exception e) {
            return false;
        }
    }

    public static boolean isValue(Object o) {
        try {
            Integer.parseInt(String.valueOf(o));
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isEnum(Object o) {
        for (int i=0;i<o.toString().length();i++) {
            try {
                Integer.parseInt(String.valueOf(o.toString().charAt(i)));
            }
            catch (NumberFormatException e){
                return true;
            }
        }
        return false;
    }
}
