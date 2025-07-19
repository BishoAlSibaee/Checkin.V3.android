package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

public interface DoorListener extends DeviceAction {
    void open();
    void close();
    void battery(int battery);
}
