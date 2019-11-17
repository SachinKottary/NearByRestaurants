package com.sachin.nearbyresturants.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sachin.nearbyresturants.interfaces.FragmentInteractionListener;
import com.sachin.nearbyresturants.utils.NetworkUtils;


/**
 *  Provides base implementation for all the fragments
 */
public abstract class BaseFragment extends Fragment {

    private BroadcastReceiver mNetworkChangeReceiver;
    protected FragmentInteractionListener fragmentInteractionListener;
    private boolean onResume = true;
    private boolean mIsNetDisconnected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNetworkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtils.isInternetPresent(context)) {
                    if (mIsNetDisconnected) {//To avoid multiple callback when network not present and fragment is loaded
                        mIsNetDisconnected = false;
                        onNetworkConnected();
                    }
                    hideNoNetworkDialog();
                } else {
                    mIsNetDisconnected = true;
                    onNetworkDisConnected();
                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fragmentInteractionListener = (FragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentInteractionListener ");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onResume = false;
        if (getActivity() == null) return;
        getActivity().unregisterReceiver(mNetworkChangeReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkUtils.isInternetPresent(getContext())) {
            if (handleNetworkState()) {
                if (mIsNetDisconnected) onNetworkConnected();
                mIsNetDisconnected = false;
                hideNoNetworkDialog();
            }
        } else {//Display network dialog if internet not present
            mIsNetDisconnected = true;
            if (handleNetworkState() && !onResume) {
                showNoNetworkDialog();
            } else {
                onNetworkDisConnected();
            }
        }
        if (getActivity() == null) return;
        getActivity().registerReceiver(mNetworkChangeReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

    }

    /**
     * Should handle network related changes from base fragment or not
     * @return true if base should handle the network connect/disconnect scenario, else false
     */
    public abstract boolean handleNetworkState();

    public abstract void onNetworkDisConnected();

    public abstract void onNetworkConnected();

    public void showNoNetworkDialog()  {}

    public void hideNoNetworkDialog() {}

    @Override
    public void onDetach() {
        super.onDetach();
        if (fragmentInteractionListener != null) fragmentInteractionListener = null;
    }

}
