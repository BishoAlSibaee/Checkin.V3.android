package com.syriasoft.housekeeping.Interface;

public interface RequestCallback {

    void onSuccess();
    void onFail(String error);
}
