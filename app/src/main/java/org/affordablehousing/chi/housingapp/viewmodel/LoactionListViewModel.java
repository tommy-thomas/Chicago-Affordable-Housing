package org.affordablehousing.chi.housingapp.viewmodel;

import android.app.Application;

import org.affordablehousing.chi.housingapp.App;
import org.affordablehousing.chi.housingapp.data.LocationRepository;
import org.affordablehousing.chi.housingapp.model.LocationEntity;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


public class LoactionListViewModel extends AndroidViewModel {

    private final LocationRepository mRepository;

    private final String TAG = LoactionListViewModel.class.getSimpleName() + " -- view model -- ";

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<LocationEntity>> mObservableLocations;

    private final MediatorLiveData<List<String>> mObservableCommunities;

    private final MediatorLiveData<List<String>> mObservablePropertyTypes;

    public LoactionListViewModel(Application application) {
        super(application);

        mObservableLocations = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableLocations.setValue(null);

        mObservableCommunities = new MediatorLiveData <>();
        mObservableCommunities.setValue(null);

        mObservablePropertyTypes = new MediatorLiveData <>();
        mObservablePropertyTypes.setValue(null);

        mRepository = ((App) application).getRepository();
        LiveData<List<LocationEntity>> locations = mRepository.getLocations();

        // observe the changes of the properties from the database and forward them
        mObservableLocations.addSource(locations, mObservableLocations::setValue);

        LiveData<List<String>> communities = mRepository.getCommunities();
        mObservableCommunities.addSource( communities , mObservableCommunities::setValue);

        LiveData<List<String>> property_types = mRepository.getPropertyTypes();
        mObservablePropertyTypes.addSource( property_types , mObservablePropertyTypes::setValue);

    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<LocationEntity>> getProperties() {
        return mObservableLocations;
    }

    public LiveData<List<String>> getCommunities() { return mObservableCommunities; }

    public LiveData<List<String>> getPropertyTypes() { return mObservablePropertyTypes; }

//    public LiveData<List<LocationEntity>> searchProducts(String query) {
//        return mRepository.searchProducts(query);
//    }
}
