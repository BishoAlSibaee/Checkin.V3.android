package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.Property.Room;

import java.util.List;

public interface GerRoomsCallback {
    void onSuccess(List<Room> rooms);
    void onError(String error);
}