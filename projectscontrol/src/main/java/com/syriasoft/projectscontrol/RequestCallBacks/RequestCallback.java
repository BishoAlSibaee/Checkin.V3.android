package com.syriasoft.projectscontrol.RequestCallBacks;

public interface RequestCallback {
    void onSuccess(String result);
    void onFailed(String error);
}
