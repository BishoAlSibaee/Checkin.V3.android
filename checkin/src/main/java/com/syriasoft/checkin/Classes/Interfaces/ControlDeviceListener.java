package com.example.hotelservicesstandalone.Classes.Interfaces;

public interface ControlDeviceListener {
    void onRoomsChanged();
    void onError(String error);
}
