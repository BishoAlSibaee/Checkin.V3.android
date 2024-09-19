package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.ControlDevice;

public interface ControlDeviceCallback {
    void onSuccess(ControlDevice device);
    void onError(String error);
}
