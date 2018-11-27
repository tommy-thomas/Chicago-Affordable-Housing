package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.PropertyEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

/**
 * Repository handling the work with products and comments.
 */
public class PropertyRepository {

    private static PropertyRepository sInstance;

    private final PropertyDatabase mDatabase;
    private MediatorLiveData<List<PropertyEntity>> mObservableProperties;

    private PropertyRepository(final PropertyDatabase database) {
        mDatabase = database;
        mObservableProperties = new MediatorLiveData <>();

        mObservableProperties.addSource(mDatabase.propertyDAO().loadAllProperties(),
                propertyEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProperties.postValue(propertyEntities);
                    }
                });
    }

    public static PropertyRepository getInstance(final PropertyDatabase database) {
        if (sInstance == null) {
            synchronized (PropertyRepository.class) {
                if (sInstance == null) {
                    sInstance = new PropertyRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<PropertyEntity>> getProperties() {
        return mObservableProperties;
    }

    public LiveData<List<String>> getCommunites(){
        return mDatabase.propertyDAO().loadCommunities();
    }

    public LiveData<List<String>> getPropertyTypes(){
        return mDatabase.propertyDAO().loadPropertyTypes();
    }


    public LiveData<PropertyEntity> loadPProperty(final int propertyId) {
        return mDatabase.propertyDAO().loadProperty(propertyId);
    }


//    public LiveData<List<ProductEntity>> searchProducts(String query) {
//        return mDatabase.productDao().searchAllProducts(query);
//    }
}