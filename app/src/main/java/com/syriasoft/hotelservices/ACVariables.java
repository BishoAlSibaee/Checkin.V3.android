package com.syriasoft.hotelservices;

public class ACVariables {
    long PowerDP ;
    long TempSetDP ;
    long TempCurrentDP ;
    long FanDP ;

    public ACVariables(long powerDP, long tempSetDP, long tempCurrentDP, long fanDP) {
        PowerDP = powerDP;
        TempSetDP = tempSetDP;
        TempCurrentDP = tempCurrentDP;
        FanDP = fanDP;
    }
}
