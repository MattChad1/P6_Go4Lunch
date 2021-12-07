package com.go4lunch2.data.api;

import com.go4lunch2.data.model.model_gmap.Matrix;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistancesAPI {

    String origins = null;
    String destinationsURLParameter = null;
    String mode = null;
    String key = null;

    @GET("maps/api/distancematrix/json")
    public Call<Matrix> getResults(@Query("origins") String origins, @Query("destinations") String destinationsURLParameter,
                                   @Query("mode") String mode,
                                   @Query("key") String key);
}
