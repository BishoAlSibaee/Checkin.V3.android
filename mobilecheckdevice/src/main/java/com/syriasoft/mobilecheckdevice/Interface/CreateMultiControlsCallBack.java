package com.syriasoft.mobilecheckdevice.Interface;

import com.tuya.smart.android.device.bean.MultiControlBean;

import java.util.List;

public interface CreateMultiControlsCallBack {
    void onSuccess(List<MultiControlBean> multiControls);
    void onFail(String error);
}
