package com.syriasoft.mobilecheckdevice;

import com.tuya.smart.sdk.bean.DeviceBean;

public class CheckInTask {

    DeviceBean taskDevice ;
    int taskDp;
    boolean taskStatus;

    public CheckInTask(DeviceBean taskDevice, int taskDp, boolean taskStatus) {
        this.taskDevice = taskDevice;
        this.taskDp = taskDp;
        this.taskStatus = taskStatus;
    }
}
