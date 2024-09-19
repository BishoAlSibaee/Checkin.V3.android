package com.syriasoft.checkin.Interface;

public interface RequestOrder {
    void onSuccess(String token);
    void onFailed(String error);
}
