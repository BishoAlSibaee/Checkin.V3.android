package com.syriasoft.checkin.Classes.Devices;

import com.syriasoft.checkin.Classes.Interfaces.DeviceAction;
import com.syriasoft.checkin.Classes.Interfaces.Listen;
import com.syriasoft.checkin.Classes.Interfaces.SetInitialValues;
import com.syriasoft.checkin.Classes.Property.Room;
import com.tuya.smart.sdk.bean.DeviceBean;

public class CheckinShutter extends CheckinDevice implements SetInitialValues, Listen {

    public CheckinShutter(DeviceBean device, Room room) {
        super(device,room);
    }

    @Override
    public void setInitialCurrentValues() {

    }

    @Override
    public void listen(DeviceAction action) {

    }

    @Override
    public void unListen() {

    }
}
