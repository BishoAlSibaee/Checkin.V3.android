package com.syriasoft.cleanup.Interface;

public interface RequestCallback {

    void onSuccess();
    void onFail(String error);
}
