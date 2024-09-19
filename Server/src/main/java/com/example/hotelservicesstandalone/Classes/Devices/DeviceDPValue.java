package com.example.hotelservicesstandalone.Classes.Devices;

import com.example.hotelservicesstandalone.Classes.Enumes.DpTypes;
import com.example.hotelservicesstandalone.Classes.Interfaces.increaseDecrease;
import com.tuya.smart.sdk.api.IResultCallback;

public class DeviceDPValue extends DeviceDP implements increaseDecrease {

    public ValueKeyValue valueKeyValue;

    String current;

    public DeviceDPValue(long dpId, String dpName, DpTypes dpType,CheckinDevice device) {
        super(dpId, dpName, dpType,device);
    }

    @Override
    public void increase(IResultCallback result) {
        int now = (int) nowValue;
        if (valueKeyValue.max > now) {
            int newValue = now + valueKeyValue.step;
            String increaseString = "{\""+dpId+"\": "+newValue+"}";
            device.control.publishDps(increaseString, new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    current = String.valueOf(newValue);
                    result.onSuccess();
                }
            });
        }
    }

    @Override
    public void decrease(IResultCallback result) {
        int now = (int) nowValue;
        if (now < valueKeyValue.min) {
            int newValue = now - valueKeyValue.step;
            String decreaseString = "{\""+dpId+"\": "+newValue+"}";
            device.control.publishDps(decreaseString, new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    result.onError(code,error);
                }

                @Override
                public void onSuccess() {
                    current = String.valueOf(newValue);
                    result.onSuccess();
                }
            });
        }
    }

    @Override
    public void setTemp(int temp,IResultCallback result) {
        String decreaseString = "{\""+dpId+"\": "+temp+"}";
        device.control.publishDps(decreaseString, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                result.onError(code,error);
            }

            @Override
            public void onSuccess() {
                current = String.valueOf(temp);
                result.onSuccess();
            }
        });
    }
}
