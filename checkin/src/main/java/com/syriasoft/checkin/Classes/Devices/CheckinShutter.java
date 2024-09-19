package com.example.hotelservicesstandalone.Classes.Devices;

import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetInitialValues;
import com.example.hotelservicesstandalone.Classes.Property.Room;
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
