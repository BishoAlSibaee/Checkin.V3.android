package com.syriasoft.projectscontrol.RequestCallBacks;

import com.syriasoft.projectscontrol.FLOOR;

import java.util.List;

public interface FloorsCallback {
    void onSuccess(List<FLOOR> floors);
    void onFail(String error);
}
