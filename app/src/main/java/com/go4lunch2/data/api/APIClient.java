package com.go4lunch2.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit getRetrofit() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .client(okHttpClient)
                .build();
    }

    public static DistancesAPI distancesAPI() {
        return getRetrofit().create(DistancesAPI.class);
    }

    public static PlaceAutocompleteAPI placeAutocompleteAPI() {
        return getRetrofit().create(PlaceAutocompleteAPI.class);
    }

    public static PlaceDetailsAPI placeDetailsAPI() {
        return getRetrofit().create(PlaceDetailsAPI.class);
    }

    public static PlacesAPI placesAPI() {
        return getRetrofit().create(PlacesAPI.class);
    }
}
