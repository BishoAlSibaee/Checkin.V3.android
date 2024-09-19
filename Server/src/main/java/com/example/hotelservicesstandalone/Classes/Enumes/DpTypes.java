package com.example.hotelservicesstandalone.Classes.Devices;

public enum DpTypes {
    value,
    Enum,
    bool;

    public static DpTypes getType(String type) {
        switch (type) {
            case "value":
                return value;
            case "enum":
                return Enum;
            default:
                return bool;
        }
    }
}
