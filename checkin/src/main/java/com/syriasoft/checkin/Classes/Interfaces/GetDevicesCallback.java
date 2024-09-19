package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;

import java.util.List;

public interface GetDevicesCallback {
    void devices(List<CheckinDevice> devices);
    void onError(String error);
}
