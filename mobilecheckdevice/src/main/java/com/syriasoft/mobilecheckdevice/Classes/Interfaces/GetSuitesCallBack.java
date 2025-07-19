package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.Property.Suite;

import java.util.List;

public interface GetSuitesCallBack {
    void onSuccess(List<Suite> suites);
    void onError(String error);
}
