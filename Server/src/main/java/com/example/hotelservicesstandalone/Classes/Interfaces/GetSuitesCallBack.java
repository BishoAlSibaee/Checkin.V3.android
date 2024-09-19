package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.Property.Suite;

import java.util.List;

public interface GetSuitesCallBack {
    void onSuccess(List<Suite> suites);
    void onError(String error);
}
