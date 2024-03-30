package com.example.mobilecheckdevice;

import com.tuya.smart.sdk.bean.DeviceBean;

public class CheckInMultiControlDevice {

    DeviceBean device;
    int dp;

    public CheckInMultiControlDevice(DeviceBean device, int dp) {
        this.device = device;
        this.dp = dp;
    }
}
