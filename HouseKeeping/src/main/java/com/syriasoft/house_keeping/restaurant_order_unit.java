package com.syriasoft.house_keeping;

import java.util.List;

public class restaurant_order_unit {

    int id;
    int hotel;
    int facility;
    int Reservation;
    int room;
    int RorS;
    int roomId;
    long dateTime;
    double total;
    int status;

    public restaurant_order_unit(int id, int hotel, int facility, int reservation, int room, int rorS, int roomId, long dateTime, double total, int status) {
        this.id = id;
        this.hotel = hotel;
        this.facility = facility;
        Reservation = reservation;
        this.room = room;
        RorS = rorS;
        this.roomId = roomId;
        this.dateTime = dateTime;
        this.total = total;
        this.status = status;
    }

    public static int searchRestaurantOrder(List<restaurant_order_unit> list,int roomNumber , int facilityId) {
        for (int i=0;i<list.size();i++) {
            if (list.get(i).room == roomNumber && list.get(i).facility == facilityId) {
                return i;
            }
        }
        return -1;
    }
}
