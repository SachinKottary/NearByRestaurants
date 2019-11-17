package com.sachin.nearbyresturants.dagger.modules;

import androidx.annotation.NonNull;


import com.sachin.nearbyresturants.network.NetworkManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger network module, used to initialize network component and
 * provide dependency to network manager
 */
@Module
public class NetworkModule {

    public NetworkModule() {
    }

    @Provides
    @Singleton
    public NetworkManager getNetworkManager() {
        return new NetworkManager();
    }

}