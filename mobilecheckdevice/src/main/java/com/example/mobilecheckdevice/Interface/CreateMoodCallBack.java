package com.example.mobilecheckdevice.Interface;

import com.tuya.smart.home.sdk.bean.scene.SceneBean;

public interface CreateMoodCallBack {
    void onSuccess(SceneBean mood);
    void onFail(String error);
}
