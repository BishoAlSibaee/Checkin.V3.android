package com.syriasoft.house_keeping.Interface;

import com.syriasoft.house_keeping.restaurant_order_unit;

public interface RestaurantOrderCallback {
    void onSuccess(restaurant_order_unit order);
    void onFail(String error);
}
