package org.affordablehousing.chi.housingapp.model;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes",
        foreignKeys = {
                @ForeignKey(entity = LocationEntity.class,
                        parentColumns = "comendId",
                        childColumns = "locationId",
                        onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "locationId")
        })
public class NoteEntity implements Note {
    @PrimaryKey(autoGenerate = true)
    private int noteId;
    private int locationId;
    private String text;
    private Date postedDate;

    @Ignore
    public NoteEntity(int noteId, int locationId, String text, Date postedDate) {
        this.noteId = noteId;
        this.locationId = locationId;
        this.text = text;
        this.postedDate = postedDate;
    }

    public NoteEntity(){}

    @Override
    public int getCommentId() {
        return noteId;
    }

    @Override
    public int getLocationId() {
        return locationId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getPostedDate() {
        return postedDate;
    }

    public void setCommentId(int noteId) {
        this.noteId = noteId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }
}
