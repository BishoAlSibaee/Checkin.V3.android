package com.syriasoft.checkin.Classes.Property;

public class Restaurant extends Facility{
    int control;

    public Restaurant(int id,int tId,String tName,String name,String photo,int control) {
        this.id = id;
        this.TypeId = tId;
        this.TypeName = tName;
        this.Name = name;
        this.photo = photo;
        this.control = control;
    }

    @Override
    Facility get() {
        return this;
    }
}
