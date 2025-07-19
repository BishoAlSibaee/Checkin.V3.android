package com.syriasoft.server.Classes.Devices;

public class BoolKeyValue {

    boolean True;
    public String TrueName;
    boolean False;
    public String FalseName;

    public BoolKeyValue(String trueName,String falseName) {
        this.True = true;
        this.False = false;
        this.FalseName = falseName;
        this.TrueName = trueName;
    }
}
