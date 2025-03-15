package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.PROJECT;

import java.util.List;

public interface GetProjectsCallback {
    void onSuccess(List<PROJECT> projects);
    void onError(String error);
}
