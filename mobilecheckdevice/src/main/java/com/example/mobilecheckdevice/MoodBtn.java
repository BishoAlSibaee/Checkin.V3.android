package com.example.mobilecheckdevice;

import com.tuya.smart.sdk.bean.DeviceBean;

public class MoodBtn {

    DeviceBean Switch ;
    int SwitchButton ;
    boolean status ;
    String statusString;

    public MoodBtn(DeviceBean aSwitch, int switchButton,boolean status) {
        Switch = aSwitch;
        SwitchButton = switchButton;
        this.status = status ;
    }

    public MoodBtn(DeviceBean aSwitch, int switchButton,String status) {
        Switch = aSwitch;
        SwitchButton = switchButton;
        this.statusString = status ;
    }
}
