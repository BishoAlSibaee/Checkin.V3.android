package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.ControlDevice;

import java.util.List;

public interface ControlDevicesCallback {
    void onSuccess(List<ControlDevice> controlDevices);
    void onError(String error);
}
