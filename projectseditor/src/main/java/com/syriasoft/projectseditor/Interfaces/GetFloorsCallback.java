package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.Property.Floor;

import java.util.List;

public interface GetFloorsCallback {
    void onSuccess(List<Floor> floors);
    void onError(String error);
}
