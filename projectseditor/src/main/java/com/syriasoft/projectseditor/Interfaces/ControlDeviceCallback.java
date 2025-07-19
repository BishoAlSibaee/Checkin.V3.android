package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.ControlDevice;

public interface ControlDeviceCallback {
    void onSuccess(ControlDevice device);
    void onError(String error);
}
