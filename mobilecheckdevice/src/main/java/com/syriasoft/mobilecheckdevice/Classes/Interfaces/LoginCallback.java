package com.syriasoft.mobilecheckdevice.Classes.Interfaces;

import com.syriasoft.mobilecheckdevice.Classes.USER;

public interface LoginCallback {
    void loggedIn(USER user);
    void onUserOrPasswordMistake();
    void onError(String error);
}
