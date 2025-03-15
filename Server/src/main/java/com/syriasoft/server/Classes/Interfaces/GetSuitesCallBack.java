package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.Property.Suite;

import java.util.List;

public interface GetSuitesCallBack {
    void onSuccess(List<Suite> suites);
    void onError(String error);
}
