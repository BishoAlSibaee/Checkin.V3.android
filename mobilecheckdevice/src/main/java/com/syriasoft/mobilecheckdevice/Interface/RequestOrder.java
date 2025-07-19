package com.syriasoft.mobilecheckdevice.Interface;

public interface RequestOrder {
    void onSuccess(String token);
    void onFailed(String error);
}
