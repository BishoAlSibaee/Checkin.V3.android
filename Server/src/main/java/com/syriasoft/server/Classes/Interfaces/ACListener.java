package com.syriasoft.server.Classes.Interfaces;

public interface ACListener extends DeviceAction {
    void onPowerOn();
    void onPowerOff();
    void onTempSet(String newTemp);
    void onTempCurrent(String currentTemp);
    void onFanSet(String newFan);
}
