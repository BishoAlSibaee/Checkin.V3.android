package com.syriasoft.housekeeping;

import java.util.List;

public class cleanOrder {
    public String roomNumber;
    public String orderNumber;
    public String dep;
    public String roomServiceText;
    public Long date;
    public ROOM room;

    public cleanOrder(String roomNumber, String orderNumber, String dep, String roomServiceText, Long date) {
        this.roomNumber = roomNumber;
        this.orderNumber = orderNumber;
        this.dep = dep;
        this.roomServiceText = roomServiceText;
        this.date = date;
    }

    public cleanOrder(String roomNumber, String orderNumber, String dep, String roomServiceText, Long date,ROOM r) {
        this.roomNumber = roomNumber;
        this.orderNumber = orderNumber;
        this.dep = dep;
        this.roomServiceText = roomServiceText;
        this.date = date;
        this.room = r ;
    }

    public static int searchOrderInList(List<cleanOrder> list,int roomNumber,String orderType) {
        for (int i=0;i<list.size();i++) {
            cleanOrder o = list.get(i);
            if (Integer.parseInt(o.roomNumber) == roomNumber && o.dep.equals(orderType)) {
                return i;
            }
        }
        return -1;
    }
}
