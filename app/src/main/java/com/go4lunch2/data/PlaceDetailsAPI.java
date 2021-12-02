package com.go4lunch2.data;

import com.go4lunch2.data.model.model_gmap.Matrix;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceDetailsAPI {
    String placeId = null;

    @GET("maps/api/place/details/json")
    public Call<RestaurantDetailsJson> getResults(@Query("place_id") String placeId, @Query("key") String key);
}
