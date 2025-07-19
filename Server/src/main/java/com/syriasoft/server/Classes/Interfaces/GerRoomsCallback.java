package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.Property.Room;

import java.util.List;

public interface GerRoomsCallback {
    void onSuccess(List<Room> rooms);
    void onError(String error);
}
