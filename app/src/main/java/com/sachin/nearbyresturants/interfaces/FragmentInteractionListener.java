package com.sachin.nearbyresturants.interfaces;

import android.os.Bundle;

/**
 *  Contains base methods for performing fragment related operations
 */
public interface FragmentInteractionListener {
    int FRAG_ADD = 1;
    int FRAG_REPLACE = 2;
    int FRAG_REPLACE_WITH_STACK = 3;
    int FRAG_ADD_WITH_STACK = 4;

    void setCurrentFragment(Bundle bundle, int fragmentType, int transType, int frameId);

    void popTopFragment();

    void popAllFromStack();

    String getActiveFragmentTag();

}