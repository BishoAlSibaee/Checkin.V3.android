package com.syriasoft.hotelservices;

public interface CallbackResult {

    void onSuccess();
    void onFail(String error);
}
