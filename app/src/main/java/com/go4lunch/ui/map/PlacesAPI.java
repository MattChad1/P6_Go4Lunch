package com.go4lunch.ui.map;

import com.firebase.ui.auth.data.model.User;
import com.go4lunch.ui.map.model_gmap.Place;
import com.go4lunch.ui.map.model_gmap.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlacesAPI {

//    @GET("api/place/nearbysearch/json?location=latitude,longitude&radius=1500&type=restaurant&key=AIzaSyBQ0LvyvKXRyLvQstUVvBQOKvzXs1IFTE4")
//    static Call<PredictionResponse> loadPredictions(@Query("input") Double latitude, Double longitude) {
//        return null;
//    }

    @GET("maps/api/place/nearbysearch/json?location=48.8769350933,2.329975529903&radius=1500&type=restaurant&key=AIzaSyBQ0LvyvKXRyLvQstUVvBQOKvzXs1IFTE4")
    public Call<Place> getResults();

}
