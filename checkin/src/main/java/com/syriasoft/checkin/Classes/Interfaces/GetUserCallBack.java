package com.syriasoft.checkin.Classes.Interfaces;

import com.syriasoft.checkin.Classes.User;

public interface GetUserCallBack {

    void onSuccess(User u);
    void onError(String error);
}
