package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.Property.Room;

import java.util.List;

public interface GerRoomsCallback {
    void onSuccess(List<Room> rooms);
    void onError(String error);
}
