package hotelservices.syriasoft.cleanup.Interface;

import hotelservices.syriasoft.cleanup.restaurant_order_unit;

public interface RestaurantOrderCallback {
    void onSuccess(restaurant_order_unit order);
    void onFail(String error);
}
