package com.syriasoft.checkin.Classes.Enumes;

public enum DpTypes {
    value,
    Enum,
    bool,
    other;

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
