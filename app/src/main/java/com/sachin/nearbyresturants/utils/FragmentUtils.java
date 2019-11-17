package com.sachin.nearbyresturants.utils;

import androidx.fragment.app.Fragment;

import com.sachin.nearbyresturants.ui.main.RestaurantListFragment;

public class FragmentUtils {

    public static final int FRAGMENT_RESTAURANT_LIST = 0;

    /**
     * Returns the fragment name to be instantiate
     *
     * @param type int identifier to differentiate the fragment
     * @return string fragment name
     */
    public static String getFragmentTag(int type) {
        switch (type) {
            case FRAGMENT_RESTAURANT_LIST:
                return RestaurantListFragment.class.getName();
        }
        return "";
    }

}