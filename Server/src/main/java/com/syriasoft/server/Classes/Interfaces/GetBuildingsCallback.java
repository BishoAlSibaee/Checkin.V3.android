package com.syriasoft.server.Classes.Interfaces;

import com.syriasoft.server.Classes.Property.Building;

import java.util.List;

public interface GetBuildingsCallback {
    void onSuccess(List<Building> buildings);
    void onError(String error);
}
