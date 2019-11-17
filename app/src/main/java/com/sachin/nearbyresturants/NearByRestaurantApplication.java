package com.sachin.nearbyresturants;

import android.app.Application;

import com.sachin.nearbyresturants.dagger.components.ApplicationComponent;
import com.sachin.nearbyresturants.dagger.components.DaggerApplicationComponent;
import com.sachin.nearbyresturants.dagger.modules.AppModule;
import com.sachin.nearbyresturants.dagger.modules.NetworkModule;
import com.sachin.nearbyresturants.dagger.modules.SharedPreferenceModule;

public class NearByRestaurantApplication extends Application {
    private ApplicationComponent applicationComponent;
    private static NearByRestaurantApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .networkModule(new NetworkModule())
                .sharedPreferenceModule(new SharedPreferenceModule(getBaseContext()))
                .appModule(new AppModule())
                .build();
        context = this;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static NearByRestaurantApplication getApplication() {
        return context;
    }
}
