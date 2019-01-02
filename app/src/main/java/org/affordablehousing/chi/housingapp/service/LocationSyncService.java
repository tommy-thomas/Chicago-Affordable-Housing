package org.affordablehousing.chi.housingapp.service;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.affordablehousing.chi.housingapp.AppExecutors;
import org.affordablehousing.chi.housingapp.data.LocationDatabase;

public class LocationSyncService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        LocationDatabase.refreshDatabase(getApplicationContext() , new AppExecutors());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
