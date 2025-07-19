package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.ControlDevice;

public interface ControlDeviceCallback {
    void onSuccess(ControlDevice device);
    void onError(String error);
}
