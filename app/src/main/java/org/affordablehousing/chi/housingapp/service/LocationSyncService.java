package org.affordablehousing.chi.housingapp.service;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.affordablehousing.chi.housingapp.AppExecutors;
import org.affordablehousing.chi.housingapp.data.LocationDatabase;
import org.affordablehousing.chi.housingapp.model.LocationEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class LocationSyncService extends JobService {

    private static final String TAG = LocationSyncService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters job) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    syncLocationData(job);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }) {
        }.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    public void syncLocationData(JobParameters jobParameters) throws InterruptedException {
        try {
            LocationDataService locationDataService = RetrofitClient.getRetrofitInstance().create(LocationDataService.class);
            Call <List <LocationEntity>> call = locationDataService.getAllLocations();
            call.enqueue(new retrofit2.Callback <List <LocationEntity>>() {
                @Override
                public void onResponse(Call <List <LocationEntity>> call, Response <List <LocationEntity>> response) {
                    if (response.isSuccessful()) {

                        List <LocationEntity> locationEntities = response.body();
                        LocationDatabase locationDatabase = LocationDatabase.getInstance(getApplicationContext(), new AppExecutors());

                        new SyncLocationDataTask(locationEntities, locationDatabase).execute();
                    }
                }

                @Override
                public void onFailure(Call <List <LocationEntity>> call, Throwable t) {

                }

            });

        } finally {
            jobFinished(jobParameters, true);

        }

    }

    private class SyncLocationDataTask extends AsyncTask <Void, Void, Void> {

        private List <LocationEntity> mLocationEntityList;
        private LocationDatabase mLocationDatabase;

        public SyncLocationDataTask(List <LocationEntity> locationEntities, LocationDatabase locationDatabase) {
            mLocationEntityList = locationEntities;
            mLocationDatabase = locationDatabase;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mLocationDatabase.locationDAO().updateAll(mLocationEntityList);
            mLocationDatabase.locationDAO().syncInsertAll(mLocationEntityList);

            Log.d(TAG, "REFRESH: " + mLocationEntityList.get(0).getProperty_name());
            return null;
        }


    }
}
