package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.Property.Room;

import java.util.List;

public interface GerRoomsCallback {
    void onSuccess(List<Room> rooms);
    void onError(String error);
}
