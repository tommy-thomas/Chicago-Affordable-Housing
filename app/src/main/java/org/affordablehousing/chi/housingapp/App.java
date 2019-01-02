package org.affordablehousing.chi.housingapp;

import android.app.Application;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.affordablehousing.chi.housingapp.data.LocationDatabase;
import org.affordablehousing.chi.housingapp.data.LocationRepository;
import org.affordablehousing.chi.housingapp.service.LocationSyncService;

/**
 * Android Application class. Used for accessing singletons.
 */
public class App extends Application {

    private AppExecutors mAppExecutors;
    private FirebaseJobDispatcher mJobDispatcher;
    private final static String JOB_TAG = LocationSyncService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
        mJobDispatcher = new FirebaseJobDispatcher( new GooglePlayDriver(this));

        scheduleSyncJob();
    }

    public LocationDatabase getDatabase() {
        return LocationDatabase.getInstance(this, mAppExecutors);
    }

    public LocationRepository getRepository() {
        return LocationRepository.getInstance(getDatabase());
    }

    private void scheduleSyncJob() {

        Job syncJob = mJobDispatcher.newJobBuilder()
                .setService(LocationSyncService.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(0, 10))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mJobDispatcher.mustSchedule(syncJob);
        Toast.makeText(this, "Data sync has been scheduled.", Toast.LENGTH_LONG).show();
    }
}
