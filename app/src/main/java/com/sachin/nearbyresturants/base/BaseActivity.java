package com.sachin.nearbyresturants.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sachin.nearbyresturants.interfaces.FragmentInteractionListener;
import com.sachin.nearbyresturants.utils.FragmentUtils;


/**
 *  Base class for all the activities, contains methods related to fragment transactions
 */
public class BaseActivity extends AppCompatActivity implements FragmentInteractionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setCurrentFragment(Bundle bundle, int fragmentType, int transType, int frameId) {
        addFragment(bundle, fragmentType, transType, frameId);
    }

    /**
     *  Used for performing fragment transactions
     * @param bundle to be passed to the new fragment
     * @param fragmentType identifier for the fragment to be instantiated
     * @param transType identifier for add, replace with back stack operation
     * @param frameId container id
     */
    private void addFragment(Bundle bundle, int fragmentType, int transType, int frameId) {
        if (isFinishing()) return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        fragment = Fragment.instantiate(this,
                FragmentUtils.getFragmentTag(fragmentType), bundle);
        switch (transType) {
            case FRAG_ADD:
                fragmentTransaction.add(frameId, fragment, FragmentUtils.getFragmentTag(fragmentType));
                break;
            case FRAG_REPLACE:
                fragmentTransaction.replace(frameId, fragment, FragmentUtils.getFragmentTag(fragmentType));
                break;
            case FRAG_REPLACE_WITH_STACK:
                fragmentTransaction.replace(frameId, fragment, FragmentUtils.getFragmentTag(fragmentType));
                fragmentTransaction.addToBackStack(FragmentUtils.getFragmentTag(fragmentType));
                break;
            case FRAG_ADD_WITH_STACK:
                fragmentTransaction.add(frameId, fragment, FragmentUtils.getFragmentTag(fragmentType));
                fragmentTransaction.addToBackStack(FragmentUtils.getFragmentTag(fragmentType));
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }


    @Override
    public void popTopFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }


    @Override
    public void popAllFromStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        for (int iterators = 0; iterators < count; iterators++) {
            popTopFragment();
        }
    }

    @Override
    public String getActiveFragmentTag() {
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}