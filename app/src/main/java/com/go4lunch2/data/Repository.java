package com.go4lunch2.data;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.MyApplication;
import com.annimon.stream.Stream;
import com.go4lunch2.BuildConfig;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.Workmate;
import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.Result;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    private final MutableLiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    Context ctx = MyApplication.getInstance();

    //TODO : change for Firebase
    List<Rating> allRatings = FAKE_RATES;
    List<Workmate> allWorkmates = FAKE_LIST_WORKMATES;

    public MutableLiveData<List<Restaurant>> getRestaurantsLiveData() {
        List<Restaurant> listRestaurants = new ArrayList<>();

        List<Result> results = getPlacesAPI(48.856614, 2.3522219);

        for (Result result : results) {
            List<Workmate> workmatesInterested = getWorkmatesIntersted(result.getPlaceId());
            Double rating = getAverageRating(result.getPlaceId());
            listRestaurants.add(new Restaurant(
                    result.getPlaceId(),
                    result.getName(),
                    "", //TODO : image from Places.API?
                    "", //TODO : type from Places.API?
                    result.getOpeningHours() != null ? result.getOpeningHours().getOpenNow() : "", //TODO : voir comment récupérer la chaîne avec les horaires
                    result.getVicinity(),
                    result.getGeometry().location.lat,
                    result.getGeometry().location.getLng(),
                    rating,
                    workmatesInterested
                    ));

        }
        restaurantsLiveData.setValue(listRestaurants);
        return restaurantsLiveData;
    }

    private List<Result> getPlacesAPI(Double latitude, Double longitude) {

        List<Result> results = new ArrayList<>();

        if (BuildConfig.DEBUG) {
            try {
                AssetManager am = ctx.getAssets();
                InputStream is = am.open("restaurants_json.json");
                final Gson gson = new Gson();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Place place = gson.fromJson(reader, Place.class);
                results.addAll(place.getResults());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://maps.googleapis.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

            PlacesAPI service = retrofit.create(PlacesAPI.class);
            Call<Place> callAsync = service.getResults(latitude.toString(), longitude.toString());

            callAsync.enqueue(new Callback<Place>() {
                @Override
                public void onResponse(Call<Place> call, Response<Place> response) {
                    Log.i("Test retrofit", "onResponse: " + response.body());
                    results.addAll(response.body().getResults());
                }

                @Override
                public void onFailure(Call<Place> call, Throwable t) {
                    System.out.println(t);
                }
            });
        }
        return results;
    }

    private List<Workmate> getWorkmatesIntersted(String idRestaurant) {
        return Stream.of(allWorkmates)
                .filter(workmate -> workmate.getIdRestaurantChosen()==idRestaurant)
                .toList();
    }

    private Double getAverageRating (String idRestaurant) {
        Double total = 0.0;
        int num = 0;


        for (Rating rating: allRatings) {
            if (rating.getIdRestaurant()==idRestaurant) {
                total += rating.getRateGiven();
                num +=1;
            }

        }
        return total/num;

    }

    static public List<Workmate> FAKE_LIST_WORKMATES = new ArrayList<>(Arrays.asList(
            new Workmate("w1", "Bob", "a1.png", "r1"),
            new Workmate("w2", "Léa", "a1.png", "r2"),
            new Workmate("w3", "Joe", "a1.png", "r1"),
            new Workmate("w4", "Yasmine", "a1.png", null),
            new Workmate("w5", "Pierre-Jean", "a1.png", "r1"))
    );

    static public List<Rating> FAKE_RATES = new ArrayList<>(Arrays.asList(
            new Rating("r1", "w1", 1d),
            new Rating("r2", "w1", 2d),
            new Rating("r1", "w2", 3d),
            new Rating("r2", "w3", 2d)
            )
    );

    static public List<Restaurant> FAKE_LIST_RESTAURANTS = new ArrayList<>(Arrays.asList(
            new Restaurant("r1", "Chez Lulu", "r1.png", "Français", "Open until 7 pm",
                           "8 rue du général Bol", 45.12, 2.0, 2.0, Collections.emptyList()),
            new Restaurant("r2", "Rajpoot", "r2.png", "Indien", "Open 24h/7", "175 avenue des Perdrix", 45.23, 2.12, 2.0, Collections.emptyList()))
    );
}
