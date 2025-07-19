package com.syriasoft.projectscontrol;

import java.util.ArrayList;
import java.util.List;

public class BUILDING {

    int id ;
    int projectId ;
    int buildingNu ;
    String buildingName ;
    int floorsNumber ;
    List<FLOOR> Floors;

    public BUILDING(int id, int projectId, int buildingNu, String buildingName, int floorsNumber) {
        this.id = id;
        this.projectId = projectId;
        this.buildingNu = buildingNu;
        this.buildingName = buildingName;
        this.floorsNumber = floorsNumber;
        this.Floors = new ArrayList<>();
    }

    void getBuildingFloors(List<FLOOR> floors) {
        for (FLOOR f:floors) {
            if (f.buildingId == this.id) {
                Floors.add(f);
            }
        }
    }
}
