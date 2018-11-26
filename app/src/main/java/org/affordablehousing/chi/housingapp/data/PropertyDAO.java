package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.PropertyEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PropertyDAO {

    @Insert(onConflict = REPLACE)
    void save(PropertyEntity property);

    @Insert(onConflict = REPLACE)
    void insertAll(List<PropertyEntity> propertyEntityList);

    @Query("SELECT * FROM properties")
    LiveData<List<PropertyEntity>> loadAllProperties();

    @Query("select * from properties where propertyId = :propertyId")
    LiveData<PropertyEntity> loadProperty(int propertyId);

}