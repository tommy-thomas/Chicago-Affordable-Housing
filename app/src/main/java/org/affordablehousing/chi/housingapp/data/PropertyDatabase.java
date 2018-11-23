package org.affordablehousing.chi.housingapp.data;

import android.content.Context;

import org.affordablehousing.chi.housingapp.model.Property;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Property.class}, version = 1, exportSchema = false)
public abstract class PropertyDatabase extends RoomDatabase {

    private static PropertyDatabase INSTANCE;

    public abstract PropertyDAO propertyDAO();


    public static PropertyDatabase getPropertyDatababse(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), PropertyDatabase.class, "property")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            //.allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
