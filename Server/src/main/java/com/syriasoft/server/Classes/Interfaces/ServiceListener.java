package com.syriasoft.server.Classes.Interfaces;

public interface ServiceListener extends DeviceAction{
    void cleanup();
    void cancelCleanup();
    void laundry();
    void cancelLaundry();
    void dnd();
    void cancelDnd();
    void checkout();
    void cancelCheckout();
    void lightOn();
    void lightOff();
}
