package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.Property.Building;

import java.util.List;

public interface GetBuildingsCallback {
    void onSuccess(List<Building> buildings);
    void onError(String error);
}
