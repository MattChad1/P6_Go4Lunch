package com.go4lunch2.data.api;

import com.go4lunch2.data.model.model_gmap.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesAPI {

    @GET("maps/api/place/nearbysearch/json")
    public Call<Place> getResults(@Query("location") String location, @Query("radius") String radius, @Query("type") String type,
                                  @Query("key") String mapsAPIKey);
}
