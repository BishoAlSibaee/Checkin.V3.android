package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.Devices.CheckinDevice;

import java.util.List;

public interface GetDevicesCallback {
    void devices(List<CheckinDevice> devices);
    void onError(String error);
}
