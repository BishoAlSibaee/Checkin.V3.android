package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.ServerStop;

public interface GetStops {
    void onSuccess(ServerStop stop);
    void onError(String error);
}
