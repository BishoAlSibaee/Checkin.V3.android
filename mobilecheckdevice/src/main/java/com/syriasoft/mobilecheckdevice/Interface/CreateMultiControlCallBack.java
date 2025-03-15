package com.syriasoft.mobilecheckdevice.Interface;

import com.tuya.smart.android.device.bean.MultiControlBean;

public interface CreateMultiControlCallBack {
    void onSuccess(MultiControlBean multi);
    void onFail(String error);
}
