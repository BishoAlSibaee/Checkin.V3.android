package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.Property.Building;

import java.util.List;

public interface GetBuildingsCallback {
    void onSuccess(List<Building> buildings);
    void onError(String error);
}
