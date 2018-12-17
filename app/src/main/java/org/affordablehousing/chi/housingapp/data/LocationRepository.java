package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.model.NoteEntity;

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

    public List<LocationEntity> getWidgetLocations(){
        return mDatabase.locationDAO().loadFavoritesforWidget();
    }


    public LiveData<LocationEntity> loadLocation(final int locationId) {
        return mDatabase.locationDAO().loadLocation(locationId);
    }

    public int setFavorite( int locationId , boolean is_favorite){
        return mDatabase.locationDAO().setFavorite( locationId , is_favorite);
    }

    public LiveData<List<LocationEntity>> loadFavorites() {
        return mDatabase.locationDAO().loadFavorites();
    }

    public LiveData<List<LocationEntity>> loadLocationsByCommunity(String community) {
        return mDatabase.locationDAO().loadLocationsByCommunity(community);
    }

    public LiveData<List<LocationEntity>> loadLocationsByCommunityAndPropertyType(String community, List<String> types) {
        return mDatabase.locationDAO().loadLocationsByCommunityAndPropertyType(community , types);
    }

    public LiveData<List<LocationEntity>> loadLocationsByPropertyType(List<String> types) {
        return mDatabase.locationDAO().loadLocationsByPropertyType(types);
    }

    public LiveData<List<NoteEntity>> loadNotes(final int locationId){
        return mDatabase.noteDAO().loadNotes(locationId);
    }

    public void addNote(NoteEntity note){
        mDatabase.noteDAO().save(note);
    }

    public void editNote(int noteId, String noteText){
        mDatabase.noteDAO().editNote(noteId, noteText);
    }

    public void deleteNote(int noteId){
        mDatabase.noteDAO().deleteNote(noteId);
    }


}