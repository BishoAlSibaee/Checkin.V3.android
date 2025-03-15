package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;

import java.util.List;

public interface GetDevicesCallback {
    void devices(List<CheckinDevice> devices);
    void onError(String error);
}
