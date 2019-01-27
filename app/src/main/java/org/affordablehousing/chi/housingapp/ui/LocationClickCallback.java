package org.affordablehousing.chi.housingapp.ui;

import org.affordablehousing.chi.housingapp.model.Location;

public interface LocationClickCallback {
    void onClick(Location location);
    void onFavoriteChecked(Location location);
}
