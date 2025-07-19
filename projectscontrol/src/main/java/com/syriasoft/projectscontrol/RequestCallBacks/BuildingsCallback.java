package com.syriasoft.projectscontrol.RequestCallBacks;

import com.syriasoft.projectscontrol.BUILDING;

import java.util.List;

public interface BuildingsCallback {
    void onSuccess(List<BUILDING> buildings);
    void onFail(String error);
}
