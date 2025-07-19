package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.ControlDevice;

import java.util.List;

public interface GetControlDevicesCallback {
    void onSuccess(List<ControlDevice> devices);
    void onError(String error);
}
