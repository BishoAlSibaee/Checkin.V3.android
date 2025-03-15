package com.syriasoft.server.Classes.Devices;

import android.util.Log;

import com.syriasoft.server.Classes.Interfaces.DeviceAction;
import com.syriasoft.server.Classes.Interfaces.Listen;
import com.syriasoft.server.Classes.Interfaces.SetInitialValues;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.Interface.RequestCallback;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;

public class CheckinGateway extends CheckinDevice implements Listen, SetInitialValues {

    public boolean currentOnline;

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
                currentOnline = online;
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
        this.control.unRegisterDevListener();
    }

    @Override
    public void setInitialCurrentValues(RequestCallback callback) {
        Log.d("onlineProblem" , device.name+" init "+this.device.getIsOnline());
        currentOnline = this.device.getIsOnline();
        if (my_room != null) {
            my_room.setRoomOnline(this.device.getIsOnline());
        }
        else if (my_suite != null) {
            my_suite.setSuiteOnline(this.device.getIsOnline());
        }
        callback.onSuccess();
    }
}
