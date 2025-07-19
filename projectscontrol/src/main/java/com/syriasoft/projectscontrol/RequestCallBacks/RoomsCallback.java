package com.syriasoft.projectscontrol.RequestCallBacks;

import com.syriasoft.projectscontrol.ROOM;

import java.util.List;

public interface RoomsCallback {
    void onSuccess(List<ROOM> rooms);
    void onFail(String error);
}
