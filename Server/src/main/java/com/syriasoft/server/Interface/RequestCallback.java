package com.syriasoft.server.Interface;

public interface RequestCallback {
    void onSuccess();
    void onFail(String error);
}
