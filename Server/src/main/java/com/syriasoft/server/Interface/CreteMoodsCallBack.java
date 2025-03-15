package com.syriasoft.server.Interface;

import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

public interface CreteMoodsCallBack {
    void onSuccess(List<SceneBean> moods);
    void onFail(String error);
}
