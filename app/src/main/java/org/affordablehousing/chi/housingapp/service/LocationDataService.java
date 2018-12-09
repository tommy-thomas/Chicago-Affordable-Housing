package org.affordablehousing.chi.housingapp.service;

import org.affordablehousing.chi.housingapp.model.LocationEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LocationDataService {
    @GET("/resource/uahe-iimk.json")
    Call <List <LocationEntity>> getAllLocations();
}
