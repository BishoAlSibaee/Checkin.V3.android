package com.syriasoft.hotelservices;

public class ACVariables {
    long PowerDP ;
    long TempSetDP ;
    long TempCurrentDP ;
    long FanDP ;
    String unit;
    int TempMax;
    int TempMin;
    String[] FanValues;
    int TempChars;
    int step;
    int TempSetPoint;
    int TempClient;

    public ACVariables() {
        PowerDP = 0;
        TempSetDP = 0;
        TempCurrentDP = 0;
        FanDP = 0;
        this.unit = "";
        TempMax = 0;
        TempMin = 0;
        FanValues = new String[]{""};
        TempChars = 0;
        this.step = 0;
        TempSetPoint = 0;
        TempClient = 0;
    }
}
