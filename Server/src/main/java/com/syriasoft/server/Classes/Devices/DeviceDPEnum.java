package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import com.syriasoft.server.Classes.Enumes.DpTypes;
import com.syriasoft.server.Classes.Interfaces.enumControl;
import com.tuya.smart.sdk.api.IResultCallback;

public class DeviceDPEnum extends DeviceDP implements enumControl {

    public EnumKeyValue enumKeyValue;

    String current;

    public DeviceDPEnum(long dpId, String dpName, DpTypes dpType, CheckinDevice device) {
        super(dpId, dpName, dpType,device);
    }

//    enumUnit getEnum(int index) {
//        return enumKeyValue.enums.get(index);
//    }
//
//    List<enumUnit> getControlEnum() {
//        return enumKeyValue.enums;
//    }



    @Override
    public void sendOrder(int index,IResultCallback result) {
        try {
            String orderString = "{\"" + dpId + "\": \"" + enumKeyValue.enums.get(index).key + "\"}";
            device.control.publishDps(orderString, new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code, error);
                }

                @Override
                public void onSuccess() {
                    result.onSuccess();
                }
            });
        }
        catch (Exception e) {
            Log.d("servicesError",e.getMessage());
        }
    }
}
