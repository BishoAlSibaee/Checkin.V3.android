package com.syriasoft.server.Classes.Devices;

import com.syriasoft.server.Classes.Enumes.DpTypes;
import com.syriasoft.server.Classes.Interfaces.onOff;
import com.tuya.smart.sdk.api.IResultCallback;

public class DeviceDPBool extends DeviceDP implements onOff {

    public BoolKeyValue boolValues;

    boolean current;

    public DeviceDPBool(long dpId, String dpName, DpTypes dpType,CheckinDevice device) {
        super(dpId, dpName, dpType,device);
    }

    @Override
    public void turnOn(IResultCallback result) {
        String onString = "{\""+dpId+"\": "+boolValues.True+"}";
        device.control.publishDps(onString, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                result.onError(code,error);
            }

            @Override
            public void onSuccess() {
                nowValue = boolValues.True;
                result.onSuccess();
            }
        });
    }

    @Override
    public void turnOff(IResultCallback result) {
        String onString = "{\""+dpId+"\": "+boolValues.False+"}";
        device.control.publishDps(onString, new IResultCallback() {
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

    public boolean getCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
