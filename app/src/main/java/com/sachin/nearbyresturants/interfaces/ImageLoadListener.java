package com.sachin.nearbyresturants.interfaces;

import com.sachin.nearbyresturants.network.dto.RestaurantDetail;

public interface ImageLoadListener {
    void onImageLoadFailed(RestaurantDetail restaurantDetail);

}
