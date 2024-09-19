package com.example.hotelservicesstandalone.Classes.Devices;

public class ValueKeyValue {

    int min;
    int max;
    int step;
    String unit;
    int scale;

    public ValueKeyValue(int min,int max,int step,String unit,int scale) {
        this.max = max;
        this.min = min;
        this.scale = scale;
        this.unit = unit;
        this.step = step;
    }
}
