package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.PROJECT;

import java.util.List;

public interface GetProjectsCallback {
    void onSuccess(List<PROJECT> projects);
    void onError(String error);
}
