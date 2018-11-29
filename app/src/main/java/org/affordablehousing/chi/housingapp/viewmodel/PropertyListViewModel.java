package org.affordablehousing.chi.housingapp.viewmodel;

import android.app.Application;

import org.affordablehousing.chi.housingapp.App;
import org.affordablehousing.chi.housingapp.data.PropertyRepository;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


public class PropertyListViewModel extends AndroidViewModel {

    private final PropertyRepository mRepository;

    private final String TAG = PropertyListViewModel.class.getSimpleName() + " -- view model -- ";

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<PropertyEntity>> mObservableProperties;

    private final MediatorLiveData<List<String>> mObservableCommunities;

    private final MediatorLiveData<List<String>> mObservablePropertyTypes;

    public PropertyListViewModel(Application application) {
        super(application);

        mObservableProperties = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableProperties.setValue(null);

        mObservableCommunities = new MediatorLiveData <>();
        mObservableCommunities.setValue(null);

        mObservablePropertyTypes = new MediatorLiveData <>();
        mObservablePropertyTypes.setValue(null);

        mRepository = ((App) application).getRepository();
        LiveData<List<PropertyEntity>> properties = mRepository.getProperties();

        // observe the changes of the properties from the database and forward them
        mObservableProperties.addSource(properties, mObservableProperties::setValue);

        LiveData<List<String>> communities = mRepository.getCommunities();
        mObservableCommunities.addSource( communities , mObservableCommunities::setValue);

        LiveData<List<String>> property_types = mRepository.getPropertyTypes();
        mObservablePropertyTypes.addSource( property_types , mObservablePropertyTypes::setValue);

    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<PropertyEntity>> getProperties() {
        return mObservableProperties;
    }

    public LiveData<List<String>> getCommunities() { return mObservableCommunities; }

    public LiveData<List<String>> getPropertyTypes() { return mObservablePropertyTypes; }

//    public LiveData<List<PropertyEntity>> searchProducts(String query) {
//        return mRepository.searchProducts(query);
//    }
}
