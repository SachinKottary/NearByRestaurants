package com.sachin.nearbyresturants.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    /**
     * checks if internet is present or not
     *
     * @param context To get the connectivity info object
     * @return boolean true if internet present, else false
     */
    public static boolean isInternetPresent(Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return false;
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) return networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}