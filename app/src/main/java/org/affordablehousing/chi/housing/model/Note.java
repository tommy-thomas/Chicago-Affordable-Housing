package org.affordablehousing.chi.housing.model;

import java.util.Date;

public interface Note {
    int getNoteId();
    int getLocationId();
    String getText();
    Date getPostedDate();
}
