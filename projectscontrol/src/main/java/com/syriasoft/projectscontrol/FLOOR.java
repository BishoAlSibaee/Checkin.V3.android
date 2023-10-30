package com.syriasoft.projectscontrol;

import java.util.ArrayList;
import java.util.List;

public class FLOOR {

    int id ;
    int buildingId ;
    int floorNumber ;
    int rooms ;
    List<ROOM> Rooms;

    public FLOOR(int id, int buildingId, int floorNumber, int rooms) {
        this.id = id;
        this.buildingId = buildingId;
        this.floorNumber = floorNumber;
        this.rooms = rooms;
        this.Rooms = new ArrayList<>();
    }

    void getFloorRooms(List<ROOM> rooms) {
        for (ROOM r :rooms) {
            if (r.floor_id == this.id) {
                Rooms.add(r);
            }
        }
    }
}
