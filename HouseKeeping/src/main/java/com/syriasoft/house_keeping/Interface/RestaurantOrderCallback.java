package com.syriasoft.housekeeping.Interface;

import com.syriasoft.housekeeping.restaurant_order_unit;

public interface RestaurantOrderCallback {
    void onSuccess(restaurant_order_unit order);
    void onFail(String error);
}
