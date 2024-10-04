package org.affordablehousing.chi.housing.service;

import org.affordablehousing.chi.housing.model.LocationEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LocationDataService {
    @GET("/resource/uahe-iimk.json")
    Call <List <LocationEntity>> getAllLocations();
}
