package com.go4lunch2.data.api;

import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.place_autocomplete.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceAutocompleteAPI {

    String input = null;
    String location = null;
    String radius = null;
    String mapsAPIKey = null;
    String types = null;

    @GET("maps/api/place/autocomplete/json")
    public Call<Root> getResults(@Query("input") String input, @Query("location") String location, @Query("radius") String radius, @Query("types") String types, @Query("key") String mapsAPIKey);

}