package com.syriasoft.house_keeping.Interface;

public interface RequestCallback {

    void onSuccess();
    void onFail(String error);
}
