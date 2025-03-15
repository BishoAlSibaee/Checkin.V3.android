package com.syriasoft.mobilecheckdevice.Interface;

public interface GetDeviceLastWorkingTime {
    void onSuccess(Long time);
    void onError(String error);
}
