package org.affordablehousing.chi.housingapp.viewmodel;

import android.app.Application;

import org.affordablehousing.chi.housingapp.App;
import org.affordablehousing.chi.housingapp.data.LocationRepository;
import org.affordablehousing.chi.housingapp.model.LocationEntity;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoactionViewModel extends AndroidViewModel {
    
    private final LiveData<LocationEntity> mObservableLocation;

    public ObservableField<LocationEntity> location = new ObservableField<>();

    private final int mLocationId;

    //private final LiveData<List<CommentEntity>> mObservableComments;

    public LoactionViewModel(@NonNull Application application, LocationRepository repository,
                             final int locationId) {
        super(application);
        mLocationId = locationId;

        //mObservableComments = repository.loadComments(mLocationId);
        mObservableLocation = repository.loadLocation(mLocationId);
    }

    /**
     * Expose the LiveData Comments query so the UI can observe it.
     */
//    public LiveData<List<CommentEntity>> getComments() {
//        return mObservableComments;
//    }

    public LiveData<LocationEntity> getObservableProperty() {
        return mObservableLocation;
    }

    public void setProperty(LocationEntity locationEntity) {
        this.location.set(locationEntity);
    }


    /**
     * A creator is used to inject the product ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final int mLocationId;

        private final LocationRepository mRepository;

        public Factory(@NonNull Application application, int locationId) {
            mApplication = application;
            mLocationId= locationId;
            mRepository = ((App) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new LoactionViewModel(mApplication, mRepository, mLocationId);
        }
    }
}
