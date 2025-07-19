package com.syriasoft.server.Classes.Interfaces;

public interface ShutterListener extends DeviceAction {
    void openShutterOn();
    void openShutterOff();
    void closeShutterOn();
    void closeShutterOff();
}
