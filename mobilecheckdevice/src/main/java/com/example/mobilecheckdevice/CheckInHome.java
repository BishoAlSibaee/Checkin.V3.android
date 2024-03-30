package com.example.mobilecheckdevice;

import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class CheckInHome {
    HomeBean Home;
    List<DeviceBean> Devices;

    public CheckInHome(HomeBean home, List<DeviceBean> devices) {
        Home = home;
        Devices = devices;
    }
}
