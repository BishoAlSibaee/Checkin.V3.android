package com.syriasoft.mobilecheckdevice;

import com.tuya.smart.sdk.bean.DeviceBean;

public class MoodBtn {

    DeviceBean Switch ;
    int SwitchButton ;
    boolean status ;
    String statusString;
    boolean delay = false;
    int minutes = 0 ;
    int seconds = 0 ;

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

    public MoodBtn(int minutes,int seconds) {
        this.delay = true;
        this.minutes = minutes;
        this.seconds = seconds;
    }
}
