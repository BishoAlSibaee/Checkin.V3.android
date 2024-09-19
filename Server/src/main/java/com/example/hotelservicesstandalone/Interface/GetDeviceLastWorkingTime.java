package com.example.hotelservicesstandalone.Interface;

public interface GetDeviceLastWorkingTime {
    void onSuccess(Long time);
    void onError(String error);
}
