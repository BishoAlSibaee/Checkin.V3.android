package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

public interface ServiceListener extends DeviceAction {
    void cleanup();
    void cancelCleanup();
    void laundry();
    void cancelLaundry();
    void dnd();
    void cancelDnd();
    void checkout();
    void cancelCheckout();
}
