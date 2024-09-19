package com.example.hotelservicesstandalone.Classes.Devices;

public class ValueKeyValue {

    public int min;
    public int max;
    public int step;
    public String unit;
    public int scale;

    public ValueKeyValue(int min,int max,int step,String unit,int scale) {
        this.max = max;
        this.min = min;
        this.scale = scale;
        this.unit = unit;
        this.step = step;
    }
}
