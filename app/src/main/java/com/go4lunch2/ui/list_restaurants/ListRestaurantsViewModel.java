package com.go4lunch2.ui.list_restaurants;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.go4lunch2.BuildConfig;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.DistancesAPI;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.model_gmap.Distance;
import com.go4lunch2.data.model.model_gmap.Element;
import com.go4lunch2.data.model.model_gmap.Example;
import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.Result;
import com.go4lunch2.data.model.model_gmap.Row;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListRestaurantsViewModel extends ViewModel {

    String TAG = "MyLog RestaurantsViewModel";

    private Repository repository;
    private MutableLiveData<List<RestaurantViewState>> allRestaurantsLiveData = new MutableLiveData<>();

    Context ctx = MyApplication.getInstance();


    public ListRestaurantsViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<RestaurantViewState>> getAllRestaurantsViewStateLiveData() {
        return Transformations.map(repository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantViewState> restaurantViewStates = new ArrayList<>();

            List<String> ids = new ArrayList<>();
            for (Restaurant r : restaurantsList) ids.add(r.getId());
            Map<String, String> mapDistance = getDistancesAPI(48.856614, 2.3522219, ids);

            for (Restaurant r : restaurantsList) {

                restaurantViewStates.add(new RestaurantViewState(
                                                  r.getId(),
                                                  r.getName(),
                                                  r.getType(),
                                                  r.getAdress(),
                                                  r.getOpeningTime(),
                                                  mapDistance.get(r.getId()),
                                                  Utils.ratingToStars(r.getRcf().getAverageRate()),
                                                  r.getRcf().getWorkmatesInterestedIds() == null ? 0 : r.getRcf().getWorkmatesInterestedIds().size(),
                                                  r.getImage()
                                          )
                                        );



            }

            allRestaurantsLiveData.setValue(restaurantViewStates);
            return restaurantViewStates;
        });
    }


    private Map<String, String> getDistancesAPI(Double latitudeOrigin, Double longitudeOrigin, List<String> destinations) {

        List<Element> elements = new ArrayList<>();
        Map<String, String> mapResult = new HashMap<>();

        if (BuildConfig.DEBUG) {
            try {
                AssetManager am = ctx.getAssets();
                InputStream is = am.open("distances_matrix.json");
                final Gson gson = new Gson();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Example example = gson.fromJson(reader, Example.class);
                for (Row r : example.getRows()) elements.addAll(r.getElements());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {

        String destinationsURLParameter="";
        for (String d : destinations) {
            if (!destinationsURLParameter.isEmpty()) destinationsURLParameter += "|place_id:";
            destinationsURLParameter += d;
        }

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

        Log.i(TAG, "getDistancesAPI: " + "https://maps.googleapis.com/" + "maps/api/distancematrix/json?origins=" + latitudeOrigin + "," + longitudeOrigin + "&destinations="+destinationsURLParameter + "&mode=walking&key=" + ctx.getString(
                R.string.google_maps_key));


        DistancesAPI service = retrofit.create(DistancesAPI.class);
        Call<Row> callAsync = service.getResults(latitudeOrigin.toString() + "," + longitudeOrigin.toString(), destinationsURLParameter , "walking", ctx.getString(
                R.string.google_maps_key));

        callAsync.enqueue(new Callback<Row>() {
            @Override
            public void onResponse(Call<Row> call, Response<Row> response) {
                Log.i("Test retrofit", "onResponse: " + response.body());
                elements.addAll(response.body().getElements());
            }

            @Override
            public void onFailure(Call<Row> call, Throwable t) {
                System.out.println(t);
            }
        });

       }
        for (int i = 0; i< destinations.size(); i++) {
            try {
                mapResult.put(destinations.get(i), ((Element) elements.get(i)).getDistance().getText());
            }
            catch (NullPointerException e) {
                Log.i(TAG, "Erreur : " + destinations.get(i));
                mapResult.put(destinations.get(i), "");

            }
        }

        return mapResult;
    }



}
