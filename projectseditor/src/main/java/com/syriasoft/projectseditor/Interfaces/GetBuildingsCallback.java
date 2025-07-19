package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.Property.Building;

import java.util.List;

public interface GetBuildingsCallback {
    void onSuccess(List<Building> buildings);
    void onError(String error);
}
