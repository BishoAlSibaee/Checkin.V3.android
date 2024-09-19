package com.example.hotelservicesstandalone.Classes.Devices;

import android.util.Log;

import com.example.hotelservicesstandalone.Classes.Interfaces.DeviceAction;
import com.example.hotelservicesstandalone.Classes.Interfaces.Listen;
import com.example.hotelservicesstandalone.Classes.Interfaces.SetInitialValues;
import com.example.hotelservicesstandalone.Classes.Property.Room;
import com.example.hotelservicesstandalone.Classes.Property.Suite;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;

public class CheckinGateway extends CheckinDevice implements Listen, SetInitialValues {

    public CheckinGateway(DeviceBean device, Room room) {
        super(device,room);
    }

    public CheckinGateway(DeviceBean device, Suite suite) {
        super(device,suite);
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
                Log.d("onlineProblem" , device.name+" listener "+online);
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

    @Override
    public void setInitialCurrentValues() {
        Log.d("onlineProblem" , device.name+" init "+this.device.getIsOnline());
        if (my_room != null) {
            my_room.setRoomOnline(this.device.getIsOnline());
        }
        else if (my_suite != null) {
            my_suite.setSuiteOnline(this.device.getIsOnline());
        }
    }
}
