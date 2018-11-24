package org.affordablehousing.chi.housingapp.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    //https://data.cityofchicago.org/resource/uahe-iimk.json

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://data.cityofchicago.org";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
