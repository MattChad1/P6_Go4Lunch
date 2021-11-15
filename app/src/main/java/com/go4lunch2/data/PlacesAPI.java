package com.go4lunch2.data;

import com.go4lunch2.data.model.model_gmap.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlacesAPI {

    String latitude = null;
    String longitude = null;

    @GET("maps/api/place/nearbysearch/json?location={latitude},{longitude}&radius=1500&type=restaurant&key=AIzaSyBQ0LvyvKXRyLvQstUVvBQOKvzXs1IFTE4")
    public Call<Place> getResults(@Path("latitude") String latitude, @Path("longitude") String longitude);

}
