package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.Property.Floor;

import java.util.List;

public interface GetFloorsCallback {
    void onSuccess(List<Floor> floors);
    void onError(String error);
}
