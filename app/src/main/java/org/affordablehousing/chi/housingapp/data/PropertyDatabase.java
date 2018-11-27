package org.affordablehousing.chi.housingapp.data;

import android.content.Context;

import org.affordablehousing.chi.housingapp.AppExecutors;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.service.GetPropertyDataService;
import org.affordablehousing.chi.housingapp.service.RetrofitClientInstance;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import retrofit2.Call;
import retrofit2.Response;

@Database(entities = {PropertyEntity.class}, version = 1, exportSchema = false)
public abstract class PropertyDatabase extends RoomDatabase {

    private static PropertyDatabase sInstance;

    public abstract PropertyDAO propertyDAO();

    public static final String DATABASE_NAME = "properties";

    public static final String TAG = PropertyDatabase.class.getCanonicalName() + " -- database -- ";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private static List<PropertyEntity> mPropertyEntityList;


    public static PropertyDatabase getInstance(Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (PropertyDatabase.class) {
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
    private static PropertyDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, PropertyDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
                            PropertyDatabase database = PropertyDatabase.getInstance(appContext, executors);
                            database.setPropertyEntityList();
                            addDelay();
                            insertData(database , mPropertyEntityList);
                            database.setDatabaseCreated();

                        });

                    }
                })
                .build();

    }


    private void setPropertyEntityList(){
        GetPropertyDataService getPropertyDataService = RetrofitClientInstance.getRetrofitInstance().create(GetPropertyDataService.class);
        Call<List <PropertyEntity>> call = getPropertyDataService.getAllProperties();
        call.enqueue(new retrofit2.Callback <List <PropertyEntity>>() {
            @Override
            public void onResponse(Call <List <PropertyEntity>> call, Response<List <PropertyEntity>> response) {
               mPropertyEntityList = response.body();
            }

            @Override
            public void onFailure(Call <List <PropertyEntity>> call, Throwable t) {

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

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    private static void insertData(final PropertyDatabase database, final List<PropertyEntity> propertyEntities) {
        database.runInTransaction(() -> {
            database.propertyDAO().insertAll(propertyEntities);
        });
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    private static void addDelay() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }
    }


    public static void destroyInstance() {
        sInstance = null;
    }


}
