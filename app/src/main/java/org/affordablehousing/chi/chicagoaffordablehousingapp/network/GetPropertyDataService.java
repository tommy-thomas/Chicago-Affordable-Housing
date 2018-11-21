package org.affordablehousing.chi.chicagoaffordablehousingapp.network;

import org.affordablehousing.chi.chicagoaffordablehousingapp.model.Property;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetPropertyDataService {
    @GET("/resource/uahe-iimk.json")
    Call <List <Property>> getAllProperties();
}
