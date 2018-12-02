package org.affordablehousing.chi.housingapp.viewmodel;

import android.app.Application;

import org.affordablehousing.chi.housingapp.App;
import org.affordablehousing.chi.housingapp.data.PropertyRepository;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PropertyViewModel extends AndroidViewModel {
    
    private final LiveData<PropertyEntity> mObservableProperty;

    public ObservableField<PropertyEntity> property = new ObservableField<>();

    private final int mPropertyId;

    //private final LiveData<List<CommentEntity>> mObservableComments;

    public PropertyViewModel(@NonNull Application application, PropertyRepository repository,
                            final int propertyId) {
        super(application);
        mPropertyId = propertyId;

        //mObservableComments = repository.loadComments(mPropertyId);
        mObservableProperty = repository.loadProperty(mPropertyId);
    }

    /**
     * Expose the LiveData Comments query so the UI can observe it.
     */
//    public LiveData<List<CommentEntity>> getComments() {
//        return mObservableComments;
//    }

    public LiveData<PropertyEntity> getObservableProperty() {
        return mObservableProperty;
    }

    public void setProperty(PropertyEntity property) {
        this.property.set(property);
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

        private final int mPropertyId;

        private final PropertyRepository mRepository;

        public Factory(@NonNull Application application, int propertyId) {
            mApplication = application;
            mPropertyId = propertyId;
            mRepository = ((App) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PropertyViewModel(mApplication, mRepository, mPropertyId);
        }
    }
}
