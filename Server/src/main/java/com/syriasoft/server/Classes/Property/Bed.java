package com.syriasoft.server.Classes.Property;

public class Bed {
    BedType bedType;
    public Room room;
    public Suite suite;

    public Bed setBed(Room room) {
        bedType = BedType.Room;
        this.room = room;
        return this;
    }
    public Bed setBed(Suite suite) {
        bedType = BedType.Suite;
        this.suite = suite;
        return this;
    }

    public boolean isRoom() {
        return bedType == BedType.Room;
    }

    public boolean isSuite() {
        return bedType == BedType.Suite;
    }
}
