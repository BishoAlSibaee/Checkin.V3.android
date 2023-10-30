package com.syriasoft.projectscontrol.RequestCallBacks;

import com.syriasoft.projectscontrol.ServerDevice;

import java.util.List;

public interface ServerDevicesCallBack {
    void onSuccess(List<ServerDevice> devices);
    void onFailed(String error);
}
