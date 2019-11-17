package com.sachin.nearbyresturants.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferenceModule {

    private Context context;
    private static final String PREFERENCE_NAME = "GoGitApp";

    public SharedPreferenceModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

/*    @Provides
    @Singleton
    GoGitAppPreferenceManager provideAppPreferencemanager(SharedPreferences sharedPreferences) {
        return new GoGitAppPreferenceManager(sharedPreferences);
    }*/

}
