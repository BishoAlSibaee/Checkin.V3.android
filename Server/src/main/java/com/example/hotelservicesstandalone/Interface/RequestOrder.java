package com.example.hotelservicesstandalone.Interface;

public interface RequestOrder {
    void onSuccess(String token);
    void onFailed(String error);
}
