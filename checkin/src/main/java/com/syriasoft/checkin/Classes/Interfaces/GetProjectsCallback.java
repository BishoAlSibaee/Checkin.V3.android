package com.example.hotelservicesstandalone.Classes.Interfaces;

import com.example.hotelservicesstandalone.Classes.PROJECT;

import java.util.List;

public interface GetProjectsCallback {
    void onSuccess(List<PROJECT> projects);
    void onError(String error);
}
