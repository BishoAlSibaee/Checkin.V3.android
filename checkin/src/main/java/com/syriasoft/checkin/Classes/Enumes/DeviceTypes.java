package com.example.hotelservicesstandalone.Classes.Enumes;

public enum DeviceTypes {
    Gateway,
    Power,
    Switch,
    ServiceSwitch,
    DoorSensor,
    MotionSensor,
    AC,
    IR,
    Lock,
    Shutter,
    Curtain,
    Unknown;

    public static DeviceTypes getDeviceType(String code) {
        if (com.example.hotelservicesstandalone.Classes.Enumes.AC.getIsAc(code)) {
            return AC;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Curtain.getIsCurtain(code)) {
            return Curtain;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Power.getIsPower(code)) {
            return Power;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.DoorSensor.getIsDoorSensor(code)) {
            return DoorSensor;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Gateway.getIsGateway(code)) {
            return Gateway;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.MotionSensor.getIsMotionSensor(code)) {
            return MotionSensor;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Switch.getIsSwitch(code)) {
            return Switch;
        }
        else {
            return Unknown;
        }
    }

    public static DeviceTypes getDeviceType(String code,String deviceName) {
        if (com.example.hotelservicesstandalone.Classes.Enumes.AC.getIsAc(code)) {
            return AC;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Curtain.getIsCurtain(code)) {
            return Curtain;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Power.getIsPower(code) && deviceName.contains("Power")) {
            return Power;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.DoorSensor.getIsDoorSensor(code)) {
            return DoorSensor;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Gateway.getIsGateway(code)) {
            return Gateway;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.MotionSensor.getIsMotionSensor(code)) {
            return MotionSensor;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Switch.getIsSwitch(code) && deviceName.contains("Service")) {
            return ServiceSwitch;
        }
        else if (com.example.hotelservicesstandalone.Classes.Enumes.Switch.getIsSwitch(code)) {
            return Switch;
        }
        else {
            return Unknown;
        }
    }
}
