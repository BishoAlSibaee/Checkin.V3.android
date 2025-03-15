package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.ServerStop;

public interface GetStops {
    void onSuccess(ServerStop stop);
    void onError(String error);
}
