package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.Devices.CheckinDevice;

import java.util.List;

public interface GetDevicesCallback {
    void devices(List<CheckinDevice> devices);
    void onError(String error);
}
