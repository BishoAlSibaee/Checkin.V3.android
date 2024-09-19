package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.ControlDevice;

import java.util.List;

public interface GetControlDevicesCallback {
    void onSuccess(List<ControlDevice> devices);
    void onError(String error);
}
