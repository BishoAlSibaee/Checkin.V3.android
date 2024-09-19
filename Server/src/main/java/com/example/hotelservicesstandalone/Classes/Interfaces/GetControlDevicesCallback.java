package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.ControlDevice;

import java.util.List;

public interface GetControlDevicesCallback {
    void onSuccess(List<ControlDevice> devices);
    void onError(String error);
}
