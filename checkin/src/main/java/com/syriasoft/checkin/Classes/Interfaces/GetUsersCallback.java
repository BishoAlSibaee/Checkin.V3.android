package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.User;

import java.util.List;

public interface GetUsersCallback {
    void onSuccess(List<User> users);
    void onError(String error);
}
