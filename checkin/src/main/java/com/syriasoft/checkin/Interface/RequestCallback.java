package com.syriasoft.checkin.Interface;

public interface RequestCallback {
    void onSuccess();
    void onFail(String error);
}
