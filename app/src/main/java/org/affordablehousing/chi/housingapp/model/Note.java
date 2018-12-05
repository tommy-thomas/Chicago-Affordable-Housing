package org.affordablehousing.chi.housingapp.model;

import java.util.Date;

public interface Note {
    int getCommentId();
    int getLocationId();
    String getText();
    Date getPostedDate();
}
