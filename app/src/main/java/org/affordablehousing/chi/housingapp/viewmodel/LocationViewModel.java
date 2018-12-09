package org.affordablehousing.chi.housingapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import org.affordablehousing.chi.housingapp.App;
import org.affordablehousing.chi.housingapp.data.LocationRepository;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.model.NoteEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LocationViewModel extends AndroidViewModel {
    
    private final LiveData<LocationEntity> mObservableLocation;

    private final LocationRepository mRepository;

    public ObservableField<LocationEntity> location = new ObservableField<>();

    private final int mLocationId;

    private final LiveData<List<NoteEntity>> mObservableNotes;

    public LocationViewModel(@NonNull Application application, LocationRepository repository,
                             final int locationId) {
        super(application);
        mLocationId = locationId;

        mRepository = ((App) application).getRepository();

        mObservableNotes = repository.loadNotes(mLocationId);
        mObservableLocation = repository.loadLocation(mLocationId);
    }

    /**
     * Expose the LiveData Notes query so the UI can observe it.
     */
    public LiveData<List<NoteEntity>> getNotes() {
        return mObservableNotes;
    }

    public LiveData<LocationEntity> getObservableLocation() {
        return mObservableLocation;
    }

    public void setLocation(LocationEntity locationEntity) {
        this.location.set(locationEntity);
    }

    public void setFavorite( int locationId, boolean favorite){
        new AddFavoriteTask( mRepository , locationId , favorite).execute();
    }

    public void addNote(NoteEntity noteEntity){
        new AddNoteTask(mRepository,noteEntity).execute();
    }

    public void setLocation(ObservableField <LocationEntity> location) {
        this.location = location;
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
            return (T) new LocationViewModel(mApplication, mRepository, mLocationId);
        }
    }

    private class AddFavoriteTask extends AsyncTask<Void,Void,Void> {

        private int mLocationId;
        private boolean mIsFavorite;
        private LocationRepository mRepository;

        public AddFavoriteTask(LocationRepository repository, int id, boolean favorite ){
            mLocationId = id;
            mIsFavorite = favorite;
            mRepository = repository;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mRepository.setFavorite( mLocationId , mIsFavorite);
            return null;
        }

    }

    private class AddNoteTask extends AsyncTask<Void,Void,Void> {

        private LocationRepository mRepository;
        private NoteEntity mNoteEntity;

        public AddNoteTask(LocationRepository repository, NoteEntity noteEntity){
           mRepository = repository;
           mNoteEntity = noteEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mRepository.addNote(mNoteEntity);
            return null;
        }
    }
}
