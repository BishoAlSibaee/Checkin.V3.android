package com.syriasoft.checkin.Classes.Interfaces;

import com.tuya.smart.sdk.api.IResultCallback;

public interface increaseDecrease {
    void increase(IResultCallback result);
    void decrease(IResultCallback result);

    void setTemp(int temp,IResultCallback result);
}
