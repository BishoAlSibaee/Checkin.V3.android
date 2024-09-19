package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.ControlDevice;

public interface ControlDeviceCallback {
    void onSuccess(ControlDevice device);
    void onError(String error);
}
