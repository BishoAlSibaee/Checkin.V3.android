package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.Property.Floor;

import java.util.List;

public interface GetFloorsCallback {
    void onSuccess(List<Floor> floors);
    void onError(String error);
}
