package com.syriasoft.checkin.Classes.Interfaces;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

public interface SetFirebaseDevicesControl {
    void setFirebaseDevicesControl(Context c, String projectUrl,DatabaseReference roomReference);
    void removeFirebaseDevicesControl(DatabaseReference roomReference);
}
