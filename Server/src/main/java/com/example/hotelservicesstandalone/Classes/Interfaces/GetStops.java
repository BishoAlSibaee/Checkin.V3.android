package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.ServerStop;

public interface GetStops {
    void onSuccess(ServerStop stop);
    void onError(String error);
}
