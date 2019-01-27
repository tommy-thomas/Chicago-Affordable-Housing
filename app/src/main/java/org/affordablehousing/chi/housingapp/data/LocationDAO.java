package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.LocationEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LocationDAO {

    @Insert(onConflict = REPLACE)
    void save(LocationEntity location);

    @Update(onConflict = REPLACE)
    void updateAll(List<LocationEntity> locationEntityList);

    @Insert(onConflict = REPLACE)
    void insertAll(List<LocationEntity> locationEntityList);

    @Insert(onConflict = IGNORE)
    void syncInsertAll(List<LocationEntity> locationEntityList);

    @Query("update location set is_favorite = :is_favorite where locationId = :locationId")
    int setFavorite(int locationId , boolean is_favorite);

    @Query("select * from location where is_favorite = 1 order by locationId asc")
    LiveData<List<LocationEntity>> loadFavorites();

    @Query("select * from location where is_favorite = 1 order by property_name asc limit 10")
    List<LocationEntity> loadFavoritesforWidget();

    @Query("select * FROM location order by locationId asc")
    LiveData<List<LocationEntity>> loadAllLocations();

    @Query("select * from location where community_area = :community")
    LiveData<List<LocationEntity>> loadLocationsByCommunity(String community );

    @Query("select * from location where property_type in (:property_type_array)")
    LiveData<List<LocationEntity>> loadLocationsByPropertyType(List<String> property_type_array);

    @Query("select * from location where community_area = :community and property_type in (:property_type_array)")
    LiveData<List<LocationEntity>> loadLocationsByCommunityAndPropertyType(String community, List<String> property_type_array );

    @Query("select * FROM location where property_type = :property_type")
    LiveData<List<LocationEntity>> loadAllLocationsByType(String property_type);

    @Query("select * from location where locationId= :locationId")
    LiveData<LocationEntity> loadLocation(int locationId);

    @Query("select distinct community_area from location order by community_area asc")
    LiveData<List<String>> loadCommunities();

    @Query("select distinct property_type from location order by property_type asc")
    LiveData<List<String>> loadPropertyTypes();

}