package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.ControlDevice;

import java.util.List;

public interface GetControlDevicesCallback {
    void onSuccess(List<ControlDevice> devices);
    void onError(String error);
}
