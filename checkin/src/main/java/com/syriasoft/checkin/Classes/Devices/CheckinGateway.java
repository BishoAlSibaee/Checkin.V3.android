package com.example.hotelservicesstandalone.Classes.Devices;

import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;

public class CheckinGateway extends CheckinDevice implements Listen {

    public CheckinGateway(DeviceBean device, Room room) {
        super(device,room);
    }

    @Override
    public void listen(DeviceAction action) {
        this.control.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {

            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                action.online(online);
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });
    }

    @Override
    public void unListen() {

    }
}
