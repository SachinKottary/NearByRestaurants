package com.sachin.nearbyresturants.dagger.modules;
import com.sachin.nearbyresturants.rx.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *  Used for providing dependency for Context and RxBus
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    public RxBus providesRxBus() {
        return new RxBus();
    }
}
