package com.syriasoft.server.Classes.Interfaces;

public interface ControlDeviceListener {
    void onRoomsChanged();
    void onError(String error);
}
