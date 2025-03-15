package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.ServerStop;

public interface GetStops {
    void onSuccess(ServerStop stop);
    void onError(String error);
}
