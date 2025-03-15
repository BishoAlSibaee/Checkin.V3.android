package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.Property.Room;

import java.util.List;

public interface GerRoomsCallback {
    void onSuccess(List<Room> rooms);
    void onError(String error);
}
