package org.affordablehousing.chi.housing.data;

import org.affordablehousing.chi.housing.model.NoteEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface NoteDAO {

    @Insert(onConflict = REPLACE)
    void save(NoteEntity note);

    @Query("SELECT * FROM notes where locationId = :locationId order by postedDate desc")
    LiveData<List<NoteEntity>> loadNotes(int locationId);

    @Query("SELECT * FROM notes where noteId = :noteId")
    List<NoteEntity> loadNote(int noteId);

    @Query("update notes set text = :text where noteId = :noteId")
    void editNote(int noteId, String text);

    @Query("delete from notes where noteId = :noteId")
    void deleteNote(int noteId);


}
