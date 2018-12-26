package org.affordablehousing.chi.housingapp.workmanager;

import android.content.Context;
import android.util.Log;

import org.affordablehousing.chi.housingapp.AppExecutors;
import org.affordablehousing.chi.housingapp.data.LocationDatabase;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CAHWorker extends Worker {

    private final static String TAG = CAHWorker.class.getSimpleName();
    private Context mContext;

    public CAHWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "working...");
        LocationDatabase.refreshDatabase(mContext , new AppExecutors());
        return Result.success();
    }
}
