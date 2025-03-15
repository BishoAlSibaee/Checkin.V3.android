package com.syriasoft.mobilecheckdevice.Classes.Devices;

import com.syriasoft.mobilecheckdevice.Classes.Interfaces.DeviceAction;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.Listen;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.SetInitialValues;
import com.syriasoft.mobilecheckdevice.Classes.Property.Room;
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
