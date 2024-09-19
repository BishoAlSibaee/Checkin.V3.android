package com.example.hotelservicesstandalone.Classes.Devices;

import com.example.hotelservicesstandalone.Classes.Enumes.DpTypes;
import com.example.hotelservicesstandalone.Classes.Interfaces.enumControl;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.List;

public class DeviceDPEnum extends DeviceDP implements enumControl {

    EnumKeyValue enumKeyValue;

    String current;

    public DeviceDPEnum(long dpId, String dpName, DpTypes dpType, CheckinDevice device) {
        super(dpId, dpName, dpType,device);
    }

    enumUnit getEnum(int index) {
        return enumKeyValue.enums.get(index);
    }

    List<enumUnit> getControlEnum() {
        return enumKeyValue.enums;
    }



    @Override
    public void sendOrder(int index,IResultCallback result) {
        String orderString = "{\""+dpId+"\": \""+enumKeyValue.enums.get(index).key+"\"}";
        device.control.publishDps(orderString, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                result.onError(code,error);
            }

            @Override
            public void onSuccess() {
                result.onSuccess();
            }
        });
    }
}
