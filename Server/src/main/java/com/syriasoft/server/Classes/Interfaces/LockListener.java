package com.syriasoft.server.Classes.Interfaces;

public interface LockListener extends DeviceAction{
    void unlocked();
    void battery(int battery);
}
