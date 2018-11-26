package org.affordablehousing.chi.housingapp;

import android.app.Application;

import org.affordablehousing.chi.housingapp.data.PropertyDatabase;
import org.affordablehousing.chi.housingapp.data.PropertyRepository;

/**
 * Android Application class. Used for accessing singletons.
 */
public class App extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    public PropertyDatabase getDatabase() {
        return PropertyDatabase.getInstance(this, mAppExecutors);
    }

    public PropertyRepository getRepository() {
        return PropertyRepository.getInstance(getDatabase());
    }
}
