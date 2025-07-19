package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.PROJECT;

import java.util.List;

public interface GetProjectsCallback {
    void onSuccess(List<PROJECT> projects);
    void onError(String error);
}
