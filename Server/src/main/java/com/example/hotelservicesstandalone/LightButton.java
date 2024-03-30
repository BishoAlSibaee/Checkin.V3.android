package com.example.hotelservicesstandalone;

import com.tuya.smart.sdk.bean.DeviceBean;

public class LightButton {

    DeviceBean device;
    String button;

    public LightButton(DeviceBean device, String button) {
        this.device = device;
        this.button = button;
    }
}
