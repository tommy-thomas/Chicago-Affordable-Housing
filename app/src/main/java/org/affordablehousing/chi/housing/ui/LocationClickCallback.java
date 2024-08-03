package org.affordablehousing.chi.housing.ui;

import org.affordablehousing.chi.housing.model.Location;

public interface LocationClickCallback {
    void onClick(Location location);
    void onFavoriteChecked(Location location);
}
