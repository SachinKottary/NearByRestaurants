package com.sachin.nearbyresturants.network;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachin.nearbyresturants.network.dto.RestaurantDetailResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Helper class to handle network related operations
 */

public class NetworkManager {

    public NetworkManager() {}

    public Observable<RestaurantDetailResponse> getRestaurantDetails(String latLang, String nextPageToken) {
        return Observable.create(emitter -> {
                    String apiResponse = getDataFromServer(ServerConstants.getRestaurantDetailUrl(latLang, nextPageToken));
                    if (TextUtils.isEmpty(apiResponse)) {
                        notifyOnError(emitter);
                        return;
                    }
            RestaurantDetailResponse details = getRestaurantDetailsFromResponse(apiResponse);
                    if (details == null) {
                        notifyOnError(emitter);
                        return;
                    }
                    emitter.onNext(details);
                    emitter.onComplete();
                }
        );
    }

    private String getDataFromServer(String apiUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RestaurantDetailResponse getRestaurantDetailsFromResponse(String jsonText) {
        ObjectMapper objectMapper = new ObjectMapper();
        RestaurantDetailResponse restorantDetails = null;
        try {
            restorantDetails = objectMapper.readValue(jsonText, RestaurantDetailResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return restorantDetails;
    }

    private void notifyOnError(Emitter emitter) {
    if (emitter == null) return;
        emitter.onError(new Throwable("Empty Response"));
    }

}