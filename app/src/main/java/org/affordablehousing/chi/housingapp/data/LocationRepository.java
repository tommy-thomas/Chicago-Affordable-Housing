package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.LocationEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

/**
 * Repository handling the work with products and comments.
 */
public class LocationRepository {

    private static LocationRepository sInstance;

    private final LocationDatabase mDatabase;
    private MediatorLiveData<List<LocationEntity>> mObservableLocations;

    private LocationRepository(final LocationDatabase database) {
        mDatabase = database;
        mObservableLocations = new MediatorLiveData <>();

        mObservableLocations.addSource(mDatabase.locationDAO().loadAllLocations(),
                locationEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableLocations.postValue(locationEntities);
                    }
                });
    }

    public static LocationRepository getInstance(final LocationDatabase database) {
        if (sInstance == null) {
            synchronized (LocationRepository.class) {
                if (sInstance == null) {
                    sInstance = new LocationRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<LocationEntity>> getLocations() {
        return mObservableLocations;
    }

    public LiveData<List<String>> getCommunities(){
        return mDatabase.locationDAO().loadCommunities();
    }

    public LiveData<List<String>> getPropertyTypes(){
        return mDatabase.locationDAO().loadPropertyTypes();
    }


    public LiveData<LocationEntity> loadLocation(final int propertyId) {
        return mDatabase.locationDAO().loadLocation(propertyId);
    }


//    public LiveData<List<ProductEntity>> searchProducts(String query) {
//        return mDatabase.productDao().searchAllProducts(query);
//    }
}