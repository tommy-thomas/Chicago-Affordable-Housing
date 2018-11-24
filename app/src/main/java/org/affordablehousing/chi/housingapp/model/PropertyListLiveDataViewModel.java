package org.affordablehousing.chi.housingapp.model;

import org.affordablehousing.chi.housingapp.data.PropertyRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class PropertyListLiveDataViewModel extends ViewModel {


    private LiveData<List<Property>> listLiveData;
    private PropertyRepository propertyRepository;

    // Instructs Dagger 2 to provide the UserRepository parameter.
    @Inject
    public PropertyListLiveDataViewModel(PropertyRepository repository) {
        this.propertyRepository = repository;
    }


    public PropertyListLiveDataViewModel() {
    }

    public void init() {
        if (this.listLiveData!= null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }
       listLiveData = propertyRepository.getProperties();
    }

    public LiveData<List<Property>> getListLiveData() {
        return this.listLiveData;
    }
}
