package com.go4lunch2.data;

import android.content.Context;

import com.go4lunch2.MyApplication;
import com.go4lunch2.data.model.model_gmap.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlacesAPI {

    @GET("maps/api/place/nearbysearch/json?location={latitude},{longitude}&radius=1500&type=restaurant&key={key}")
    public Call<Place> getResults(@Path("latitude") String latitude, @Path("longitude") String longitude, @Path("key") String mapsAPIKey);

}
