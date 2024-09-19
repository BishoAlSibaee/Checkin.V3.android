package com.syriasoft.housekeeping.Interface;

import com.tuya.smart.home.sdk.bean.HomeBean;

public interface HomeBeanCallBack {
    void onSuccess(HomeBean homeBean);
    void onFail(String error);
}
