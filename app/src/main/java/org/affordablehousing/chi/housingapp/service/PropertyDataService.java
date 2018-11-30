package org.affordablehousing.chi.housingapp.service;

import org.affordablehousing.chi.housingapp.model.PropertyEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PropertyDataService {
    @GET("/resource/uahe-iimk.json")
    Call <List <PropertyEntity>> getAllProperties();
}
