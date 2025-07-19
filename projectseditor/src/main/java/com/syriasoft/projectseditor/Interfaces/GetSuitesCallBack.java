package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.Property.Suite;

import java.util.List;

public interface GetSuitesCallBack {
    void onSuccess(List<Suite> suites);
    void onError(String error);
}
