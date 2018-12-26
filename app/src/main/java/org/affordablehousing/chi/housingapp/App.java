package org.affordablehousing.chi.housingapp;

import android.app.Application;

import org.affordablehousing.chi.housingapp.data.LocationDatabase;
import org.affordablehousing.chi.housingapp.data.LocationRepository;
import org.affordablehousing.chi.housingapp.workmanager.CAHWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * Android Application class. Used for accessing singletons.
 */
public class App extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();

        createWorker();
    }

    public LocationDatabase getDatabase() {
        return LocationDatabase.getInstance(this, mAppExecutors);
    }

    public LocationRepository getRepository() {
        return LocationRepository.getInstance(getDatabase());
    }

    private void createWorker() {

        PeriodicWorkRequest.Builder dataRefreshBuilder =
                new PeriodicWorkRequest.Builder(CAHWorker.class, 2,
                        TimeUnit.MINUTES);
        // ...if you want, you can apply constraints to the builder here...
        // Create a Constraints object that defines when the task should run
        Constraints workerConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                // Many other constraints are available, see the
                // Constraints.Builder reference
                .build();
        // Create the actual work object:
        PeriodicWorkRequest dataRefreshWork = dataRefreshBuilder
                .build();
        // Then enqueue the recurring task:
        WorkManager.getInstance().enqueue(dataRefreshWork);
    }
}
