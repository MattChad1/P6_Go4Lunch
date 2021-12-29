package com.go4lunch2.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit getRetrofit() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static DistancesAPI distancesAPI() {
        DistancesAPI distancesAPI = getRetrofit().create(DistancesAPI.class);
        return distancesAPI;
    }

    public static PlaceAutocompleteAPI placeAutocompleteAPI() {
        PlaceAutocompleteAPI placeAutocompleteAPI = getRetrofit().create(PlaceAutocompleteAPI.class);
        return placeAutocompleteAPI;
    }

    public static PlaceDetailsAPI placeDetailsAPI() {
        PlaceDetailsAPI placeDetailsAPI = getRetrofit().create(PlaceDetailsAPI.class);
        return placeDetailsAPI;
    }

    public static PlacesAPI placesAPI() {
        PlacesAPI placesAPI = getRetrofit().create(PlacesAPI.class);
        return placesAPI;
    }
}
