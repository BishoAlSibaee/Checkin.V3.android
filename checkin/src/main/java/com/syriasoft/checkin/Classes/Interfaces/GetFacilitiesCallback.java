package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.Property.Facility;

import java.util.List;

public interface GetFacilitiesCallback {
    void onSuccess(List<Facility> facilities);
    void onError(String error);
}
