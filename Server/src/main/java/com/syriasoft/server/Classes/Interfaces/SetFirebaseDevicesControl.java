package com.syriasoft.server.Classes.Interfaces;

import com.google.firebase.database.DatabaseReference;

public interface SetFirebaseDevicesControl {
    void setFirebaseDevicesControl(DatabaseReference roomReference);
    void removeFirebaseDevicesControl(DatabaseReference roomReference);
}
