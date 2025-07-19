package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.ControlDevice;

public interface ControlDeviceCallback {
    void onSuccess(ControlDevice device);
    void onError(String error);
}
