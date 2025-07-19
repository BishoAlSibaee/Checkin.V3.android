package com.syriasoft.projectseditor.Classes.Enumes;

public enum DeviceTypes {
    Gateway,
    Power,
    Switch,
    Service,
    DoorSensor,
    MotionSensor,
    AC,
    IR,
    Lock,
    Shutter,
    Curtain,
    Unknown;

//    public static DeviceTypes getDeviceType(String code) {
//        if (com.syriasoft.server.Classes.Enumes.AC.getIsAc(code)) {
//            return AC;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Curtain.getIsCurtain(code)) {
//            return Curtain;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Power.getIsPower(code)) {
//            return Power;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.DoorSensor.getIsDoorSensor(code)) {
//            return DoorSensor;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Gateway.getIsGateway(code)) {
//            return Gateway;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.MotionSensor.getIsMotionSensor(code)) {
//            return MotionSensor;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Switch.getIsSwitch(code)) {
//            return Switch;
//        }
//        else {
//            return Unknown;
//        }
//    }
//
//    public static DeviceTypes getDeviceType(String code,String deviceName) {
//        if (com.syriasoft.server.Classes.Enumes.AC.getIsAc(code)) {
//            return AC;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Curtain.getIsCurtain(code)) {
//            return Curtain;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Power.getIsPower(code) && deviceName.contains("Power")) {
//            return Power;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.DoorSensor.getIsDoorSensor(code)) {
//            return DoorSensor;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Gateway.getIsGateway(code)) {
//            return Gateway;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.MotionSensor.getIsMotionSensor(code)) {
//            return MotionSensor;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Switch.getIsSwitch(code) && deviceName.contains("Service")) {
//            return Service;
//        }
//        else if (com.syriasoft.server.Classes.Enumes.Switch.getIsSwitch(code)) {
//            return Switch;
//        }
//        else {
//            return Unknown;
//        }
//    }
}
