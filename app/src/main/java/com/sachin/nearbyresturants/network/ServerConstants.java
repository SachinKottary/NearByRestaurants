package com.sachin.nearbyresturants.network;

import android.text.TextUtils;

/**
 * Used for holding API related constants
 */

public class ServerConstants {

    public static final String APIKEY = "";//Update API key here
    private static String LOCATION_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&key="+APIKEY+"&rankby=distance&location=";
    private static final String nextPageParam = "&pagetoken=";


    public static String getRestaurantDetailUrl(String latLang, String nextPageToken) {
        StringBuilder urlBuilder = new StringBuilder(LOCATION_URL);
        urlBuilder.append(latLang);
        if (!TextUtils.isEmpty(nextPageToken)) {
            urlBuilder.append(nextPageParam)
                    .append(nextPageToken);
        }
        return urlBuilder.toString();
    }
}
