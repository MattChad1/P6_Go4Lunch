package com.go4lunch2.data;

import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.Row;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DistancesAPI {

        String origins = null;
        String destinationsURLParameter = null;
        String key = null;


//    @GET("maps/api/distancematrix/json?origins={latitude},{longitude}&destinations={destinations}&mode=walking&key={key}")
//    public Call<Place> getResults(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("destinations") String destinationsURLParameter,
//                                  @Query("key") String key);

    @GET("maps/api/distancematrix/json")
    public Call<Row> getResults(@Query("origins") String origins, @Query("destinations") String destinationsURLParameter, @Query("mode") String mode,
                                @Query("key") String key);

}
