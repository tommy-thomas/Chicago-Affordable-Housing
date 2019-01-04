package org.affordablehousing.chi.housingapp;

import android.app.Application;

import org.affordablehousing.chi.housingapp.data.LocationDatabase;
import org.affordablehousing.chi.housingapp.data.LocationRepository;
import org.affordablehousing.chi.housingapp.service.LocationSyncService;

/**
 * Android Application class. Used for accessing singletons.
 */
public class App extends Application {

    private AppExecutors mAppExecutors;
    private final static String JOB_TAG = LocationSyncService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    public LocationDatabase getDatabase() {
        return LocationDatabase.getInstance(this, mAppExecutors);
    }

    public LocationRepository getRepository() {
        return LocationRepository.getInstance(getDatabase());
    }

}
