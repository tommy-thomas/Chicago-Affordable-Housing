package org.affordablehousing.chi.housingapp.network;

import org.affordablehousing.chi.housingapp.model.Property;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetPropertyDataService {
    @GET("/resource/uahe-iimk.json")
    Call <List <Property>> getAllProperties();
}
