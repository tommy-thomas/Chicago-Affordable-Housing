package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.LocationEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LocationDAO {

    @Insert(onConflict = REPLACE)
    void save(LocationEntity location);

    @Insert(onConflict = REPLACE)
    void insertAll(List<LocationEntity> locationEntityList);

    @Query("Update location set is_favorite = :is_favorite where locationId = :locationId")
    int setFavorite(int locationId , boolean is_favorite);

    @Query("select * from location where is_favorite = 1 and locationId = :locationId")
    LiveData<List<LocationEntity>> loadFavorites(int locationId );

    @Query("select * from location where community_area = :community")
    LiveData<List<LocationEntity>> loadLocationsByCommunity(String community );

    @Query("SELECT * FROM location")
    LiveData<List<LocationEntity>> loadAllLocations();

    @Query("SELECT * FROM location where property_type = :property_type")
    LiveData<List<LocationEntity>> loadAllLocationsByType(String property_type);


    @Query("select * from location where locationId= :locationId")
    LiveData<LocationEntity> loadLocation(int locationId);

    @Query("select distinct community_area from location order by community_area asc")
    LiveData<List<String>> loadCommunities();

    @Query("select distinct property_type from location order by property_type asc")
    LiveData<List<String>> loadPropertyTypes();


}