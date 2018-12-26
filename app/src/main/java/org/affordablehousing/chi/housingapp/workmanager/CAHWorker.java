package org.affordablehousing.chi.housingapp.workmanager;

import android.content.Context;

import org.affordablehousing.chi.housingapp.AppExecutors;
import org.affordablehousing.chi.housingapp.data.LocationDatabase;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CAHWorker extends Worker {
    public CAHWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do periodic data refresh here???
       LocationDatabase.refreshDatabase( getApplicationContext() , new AppExecutors());
        return Result.success();
    }


}


