package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.PROJECT;

import java.util.List;

public interface GetProjectsCallback {
    void onSuccess(List<PROJECT> projects);
    void onError(String error);
}
