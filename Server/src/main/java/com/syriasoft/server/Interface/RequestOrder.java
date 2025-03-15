package com.syriasoft.server.Interface;

public interface RequestOrder {
    void onSuccess(String token);
    void onFailed(String error);
}
