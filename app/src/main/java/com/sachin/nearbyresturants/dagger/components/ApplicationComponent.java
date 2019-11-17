package com.sachin.nearbyresturants.dagger.components;


import com.sachin.nearbyresturants.RestaurantListActivity;
import com.sachin.nearbyresturants.dagger.modules.AppModule;
import com.sachin.nearbyresturants.dagger.modules.NetworkModule;
import com.sachin.nearbyresturants.dagger.modules.SharedPreferenceModule;
import com.sachin.nearbyresturants.ui.main.RestaurantViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 *  Dagger single app component used for injecting SharedPreferenceModule.class, NetworkModule.class
 */
@Singleton
@Component(modules = {SharedPreferenceModule.class, NetworkModule.class, AppModule.class})
public interface ApplicationComponent {

    void inject(RestaurantListActivity activity);

    void inject(RestaurantViewModel viewModel);

}