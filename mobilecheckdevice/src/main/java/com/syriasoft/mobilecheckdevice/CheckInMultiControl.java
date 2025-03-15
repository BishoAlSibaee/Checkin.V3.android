package com.syriasoft.mobilecheckdevice;

import java.util.List;

public class CheckInMultiControl {
    String name;
    List<CheckInMultiControlDevice> multiControl;

    public CheckInMultiControl(String name, List<CheckInMultiControlDevice> multiControl) {
        this.name = name;
        this.multiControl = multiControl;
    }
}
