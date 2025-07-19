package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.Property.Building;

import java.util.List;

public interface GetBuildingsCallback {
    void onSuccess(List<Building> buildings);
    void onError(String error);
}
