package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.Property.Floor;

import java.util.List;

public interface GetFloorsCallback {
    void onSuccess(List<Floor> floors);
    void onError(String error);
}
