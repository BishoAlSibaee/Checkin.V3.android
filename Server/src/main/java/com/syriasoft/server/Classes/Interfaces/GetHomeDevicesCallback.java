package com.syriasoft.server.Classes.Interfaces;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public interface GetHomeDevicesCallback {
    void devices(List<DeviceBean> devices);
    void oError(String error);
}
