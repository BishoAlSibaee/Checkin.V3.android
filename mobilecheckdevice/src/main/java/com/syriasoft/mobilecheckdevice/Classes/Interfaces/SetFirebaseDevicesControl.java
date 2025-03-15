package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.google.firebase.database.DatabaseReference;

public interface SetFirebaseDevicesControl {
    void setFirebaseDevicesControl(DatabaseReference roomReference);
    void removeFirebaseDevicesControl(DatabaseReference roomReference);
}
