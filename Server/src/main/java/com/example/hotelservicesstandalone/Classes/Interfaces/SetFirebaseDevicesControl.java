package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.google.firebase.database.DatabaseReference;

public interface SetFirebaseDevicesControl {
    void setFirebaseDevicesControl(DatabaseReference roomReference);
    void removeFirebaseDevicesControl(DatabaseReference roomReference);
}
