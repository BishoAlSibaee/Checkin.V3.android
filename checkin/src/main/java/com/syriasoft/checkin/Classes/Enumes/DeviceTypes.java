package com.syriasoft.checkin.Classes.Enumes;

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
        if (com.syriasoft.checkin.Classes.Enumes.AC.getIsAc(code)) {
            return AC;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Curtain.getIsCurtain(code)) {
            return Curtain;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Power.getIsPower(code)) {
            return Power;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.DoorSensor.getIsDoorSensor(code)) {
            return DoorSensor;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Gateway.getIsGateway(code)) {
            return Gateway;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.MotionSensor.getIsMotionSensor(code)) {
            return MotionSensor;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Switch.getIsSwitch(code)) {
            return Switch;
        }
        else {
            return Unknown;
        }
    }

    public static DeviceTypes getDeviceType(String code,String deviceName) {
        if (com.syriasoft.checkin.Classes.Enumes.AC.getIsAc(code)) {
            return AC;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Curtain.getIsCurtain(code)) {
            return Curtain;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Power.getIsPower(code) && deviceName.contains("Power")) {
            return Power;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.DoorSensor.getIsDoorSensor(code)) {
            return DoorSensor;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Gateway.getIsGateway(code)) {
            return Gateway;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.MotionSensor.getIsMotionSensor(code)) {
            return MotionSensor;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Switch.getIsSwitch(code) && deviceName.contains("Service")) {
            return ServiceSwitch;
        }
        else if (com.syriasoft.checkin.Classes.Enumes.Switch.getIsSwitch(code)) {
            return Switch;
        }
        else {
            return Unknown;
        }
    }
}
