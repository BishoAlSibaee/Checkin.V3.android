package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.ControlDevice;

import java.util.List;

public interface GetControlDevicesCallback {
    void onSuccess(List<ControlDevice> devices);
    void onError(String error);
}
