package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.Property.Floor;

import java.util.List;

public interface GetFloorsCallback {
    void onSuccess(List<Floor> floors);
    void onError(String error);
}
