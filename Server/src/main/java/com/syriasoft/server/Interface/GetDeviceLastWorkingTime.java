package com.syriasoft.server.Interface;

public interface GetDeviceLastWorkingTime {
    void onSuccess(Long time);
    void onError(String error);
}
