package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.tuya.smart.sdk.api.IResultCallback;

public interface onOff {
    void turnOn(IResultCallback result);
    void turnOff(IResultCallback result);
}
