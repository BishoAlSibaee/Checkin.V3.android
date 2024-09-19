package com.syriasoft.checkin.Classes.Devices;

public class BoolKeyValue {

    boolean True;
    String TrueName;
    boolean False;
    String FalseName;

    public BoolKeyValue(String trueName,String falseName) {
        this.True = true;
        this.False = false;
        this.FalseName = falseName;
        this.TrueName = trueName;
    }
}
