package com.syriasoft.projectseditor.Interfaces;

public interface GetDeviceLastWorkingTime {
    void onSuccess(Long time);
    void onError(String error);
}
