package org.affordablehousing.chi.housingapp.data;

import android.content.Context;

import org.affordablehousing.chi.housingapp.AppExecutors;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.model.NoteEntity;
import org.affordablehousing.chi.housingapp.service.LocationDataService;
import org.affordablehousing.chi.housingapp.service.RetrofitClient;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import retrofit2.Call;
import retrofit2.Response;

@SuppressWarnings("deprecation")
@Database(entities = {LocationEntity.class, NoteEntity.class}, version = 4, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class LocationDatabase extends RoomDatabase {

    private static LocationDatabase sInstance;

    public abstract LocationDAO locationDAO();

    public abstract NoteDAO noteDAO();

    public static final String DATABASE_NAME = "location_data";

    public static final String TAG = LocationDatabase.class.getCanonicalName() + " -- database -- ";

    private final MutableLiveData <Boolean> mIsDatabaseCreated = new MutableLiveData <>();

    private static List <LocationEntity> mLocationEntityList;


    public static LocationDatabase getInstance(Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LocationDatabase.class) {
                sInstance = buildDatabase(context.getApplicationContext(), executors);
                sInstance.updateDatabaseCreated(context.getApplicationContext());
            }
        }

        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static LocationDatabase buildDatabase(final Context appContext,
                                                  final AppExecutors executors) {
        return Room.databaseBuilder(appContext, LocationDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
                            LocationDatabase database = LocationDatabase.getInstance(appContext, executors);
                            database.setPropertyEntityList();
                            addDelay();
                            insertData(database, mLocationEntityList);
                            database.setDatabaseCreated();

                        });

                    }
                })
                .fallbackToDestructiveMigration()
                .build();

    }

    private void setPropertyEntityList() {
        LocationDataService locationDataService = RetrofitClient.getRetrofitInstance().create(LocationDataService.class);
        Call <List <LocationEntity>> call = locationDataService.getAllLocations();
        call.enqueue(new retrofit2.Callback <List <LocationEntity>>() {
            @Override
            public void onResponse(Call <List <LocationEntity>> call, Response <List <LocationEntity>> response) {
                if (response.isSuccessful()) {
                    mLocationEntityList = response.body();
                }
            }

            @Override
            public void onFailure(Call <List <LocationEntity>> call, Throwable t) {

            }


        });
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    private static void insertData(final LocationDatabase database, final List <LocationEntity> locationEntities) {
        database.runInTransaction(() -> {
            database.locationDAO().insertAll(locationEntities);
        });
    }

    public LiveData <Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    private static void addDelay() {
        try {
            Thread.sleep(8000);
        } catch (InterruptedException ignored) {
        }
    }


    public static void destroyInstance() {
        sInstance = null;
    }


}
