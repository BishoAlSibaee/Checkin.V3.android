package com.example.hotelservicesstandalone.Classes.Property;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PropertyDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PropertyData";
    SQLiteDatabase db;
    String createBuildingTable = "CREATE TABLE IF NOT EXISTS Buildings (id INTEGER PRIMARY KEY , projectId INTEGER , buildingNo INTEGER , buildingName VARCHAR (60) , floorsNumber INTEGER)";
    String createFloorTable = "CREATE TABLE IF NOT EXISTS Floors (id INTEGER PRIMARY KEY , building_id INTEGER , floorNumber INTEGER , rooms INTEGER)";
    String createRoomTable = "CREATE TABLE IF NOT EXISTS Rooms (id INTEGER PRIMARY KEY , RoomNumber INTEGER , Status INTEGER , hotel INTEGER, building_id INTEGER,floor_id INTEGER," +
            "RoomType VARCHAR (50) , SuiteStatus INTEGER , SuiteNumber INEGER , SuiteId INTEGER , ReservationNumber INTEGER , roomStatus INTEGER , Tablet INTEGER  , " +
            "Facility INTEGER )";
    String createSuiteTable = "CREATE TABLE IF NOT EXISTS Suites (id INTEGER PRIMARY KEY , SuiteNumber INTEGER , Rooms TEXT , RoomsId TEXT , Building INTEGER , BuildingId INTEGER , Floor INTEGER , FloorId INTEGER , Status INTEGER)";


    public PropertyDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db = sqLiteDatabase;
        db.execSQL(createBuildingTable);
        db.execSQL(createFloorTable);
        db.execSQL(createRoomTable);
        db.execSQL(createSuiteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Buildings");
        db.execSQL("DROP TABLE IF EXISTS Floors");
        db.execSQL("DROP TABLE IF EXISTS Rooms");
        db.execSQL("DROP TABLE IF EXISTS Suites");
        onCreate(db);
    }

    public void insertBuilding(Building building){
        ContentValues values = new ContentValues();
        values.put("id",building.id);
        values.put("projectId",building.projectId);
        values.put("buildingNo",building.buildingNo);
        values.put("buildingName",building.buildingName);
        values.put("floorsNumber",building.floorsNumber);
        db.insert("Buildings",null,values);
    }
    public boolean isBuildingsInserted() {
        Cursor c = db.rawQuery("SELECT * FROM Buildings",null);
        return c.getCount() > 0;
    }
    public List<Building> getBuildings() {
        List<Building> buildings = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Buildings",null);
        while (c.moveToNext()) {
           buildings.add(new Building(c.getInt(0),c.getInt(1),c.getInt(2),c.getString(3),c.getInt(4)));
        }
        return buildings;
    }

    public void insertFloor(Floor floor){
        ContentValues values = new ContentValues();
        values.put("id",floor.id);
        values.put("building_id",floor.building_id);
        values.put("floorNumber",floor.floorNumber);
        values.put("floorNumber",floor.rooms);
        db.insert("Floors",null,values);
    }
    public boolean isFloorsInserted() {
        Cursor c = db.rawQuery("SELECT * FROM Floors",null);
        return c.getCount() > 0;
    }
    public List<Floor> getFloors() {
        List<Floor> floors = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Floors",null);
        while (c.moveToNext()) {
              floors.add(new Floor(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3)));
        }
        return floors;
    }

    public void insertRoom(Room room){
        ContentValues values = new ContentValues();
        values.put("id",room.id);
        values.put("RoomNumber",room.RoomNumber);
        values.put("Status",room.Status);
        values.put("hotel",room.hotel);
        values.put("building_id",room.building_id);
        values.put("floor_id",room.floor_id);
        values.put("RoomType",room.RoomType);
        values.put("SuiteStatus",room.SuiteStatus);
        values.put("SuiteNumber",room.SuiteNumber);
        values.put("SuiteId",room.SuiteId);
        values.put("ReservationNumber",room.ReservationNumber);
        values.put("roomStatus",room.roomStatus);
        values.put("Tablet",room.Tablet);
        values.put("Facility",room.Facility);
        db.insert("Rooms",null,values);
    }
    public boolean isRoomsInserted() {
        Cursor c = db.rawQuery("SELECT * FROM Rooms",null);
        return c.getCount() > 0;
    }
    public List<Room> getRooms() {
         List<Room> rooms = new ArrayList<>();
         Cursor c = db.rawQuery("SELECT * FROM Rooms",null);
         while (c.moveToNext()) {
             rooms.add(new Room(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3),c.getInt(4),c.getInt(5),c.getString(6),c.getInt(7),c.getInt(8),c.getInt(9),c.getInt(10),c.getInt(11),c.getInt(12),c.getInt(13)));
         }
         return rooms;
    }

    public void insertSuite(Suite suite){
        ContentValues values = new ContentValues();
        values.put("id",suite.id);
        values.put("SuiteNumber",suite.SuiteNumber);
        values.put("Rooms",suite.Rooms);
        values.put("RoomsId",suite.RoomsId);
        values.put("Building",suite.Building);
        values.put("BuildingId",suite.BuildingId);
        values.put("Floor",suite.Floor);
        values.put("FloorId",suite.FloorId);
        values.put("Status",suite.Status);
        db.insert("Suites",null,values);
    }
    public boolean isSuitesInserted() {
        Cursor c = db.rawQuery("SELECT * FROM Suites",null);
        return c.getCount() > 0;
    }
    public List<Suite> getSuites() {
        List<Suite> suites = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Suites",null);
        while (c.moveToNext()) {
            suites.add(new Suite(c.getInt(0),c.getInt(1),c.getString(2),c.getString(3),c.getInt(4),c.getInt(5),c.getInt(6),c.getInt(7),c.getInt(8)));
        }
        return suites;
    }

    public void deleteAll() {
        db.execSQL("DROP TABLE IF EXISTS Buildings");
        db.execSQL("DROP TABLE IF EXISTS Floors");
        db.execSQL("DROP TABLE IF EXISTS Rooms");
        db.execSQL("DROP TABLE IF EXISTS Suites");
        onCreate(db);
    }

    public static void insertAllBuildings(List<Building> buildings,PropertyDB db) {
        for (Building b : buildings) {
            db.insertBuilding(b);
        }
    }
    public static void insertAllFloors(List<Floor> floors,PropertyDB db) {
        for (Floor f : floors) {
            db.insertFloor(f);
        }
    }
    public static void insertAllRooms(List<Room> rooms,PropertyDB db) {
        for (Room r : rooms) {
           db.insertRoom(r);
        }
    }
    public static void insertAllSuites(List<Suite> suites,PropertyDB db) {
        for (Suite s : suites) {
            db.insertSuite(s);
        }
    }
}
