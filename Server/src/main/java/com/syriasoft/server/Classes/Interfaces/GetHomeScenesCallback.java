package com.syriasoft.server.Classes.Interfaces;

import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

public interface GetHomeScenesCallback {
    void onSuccess(List<SceneBean> scenes);
    void inFail(String error);
}
