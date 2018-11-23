package org.affordablehousing.chi.housingapp.data;

import org.affordablehousing.chi.housingapp.model.Property;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PropertyDAO {

    @Insert(onConflict = REPLACE)
    void save(Property property);

    @Query("SELECT * FROM property")
    public List<Property> loadAllProperties();
}