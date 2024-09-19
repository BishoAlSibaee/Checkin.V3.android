package com.syriasoft.checkin.Classes.Interfaces;

public interface LockListener extends DeviceAction{
    void unlocked();
    void battery(int battery);
}
