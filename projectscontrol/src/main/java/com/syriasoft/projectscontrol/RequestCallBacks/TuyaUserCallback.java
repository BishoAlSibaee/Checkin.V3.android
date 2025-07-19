package com.syriasoft.projectscontrol.RequestCallBacks;

import com.tuya.smart.android.user.bean.User;

public interface TuyaUserCallback {
    void onSuccess(User user);
    void onFail(String error);
}
