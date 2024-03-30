package com.example.mobilecheckdevice;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class CheckInMood {

    String moodName;
    DeviceBean conditionDevice ;
    int conditionDp;
    boolean conditionStatus;
    List<CheckInTask> tasksDevices;

    public CheckInMood(String moodName, DeviceBean conditionDevice, int conditionDp, boolean conditionStatus, List<CheckInTask> tasksDevices) {
        this.moodName = moodName;
        this.conditionDevice = conditionDevice;
        this.conditionDp = conditionDp;
        this.conditionStatus = conditionStatus;
        this.tasksDevices = tasksDevices;
    }
}
