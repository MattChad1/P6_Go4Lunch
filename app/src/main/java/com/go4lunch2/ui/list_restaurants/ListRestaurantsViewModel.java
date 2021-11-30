package com.go4lunch2.ui.list_restaurants;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.BuildConfig;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.DistancesAPI;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.model_gmap.Element;
import com.go4lunch2.data.model.model_gmap.Matrix;
import com.go4lunch2.data.model.model_gmap.Row;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Log.i(TAG, "getAllRestaurantsViewStateLiveData: ");
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
                Matrix matrix = gson.fromJson(reader, Matrix.class);
                for (Row r : matrix.getRows()) elements.addAll(r.getElements());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            String destinationsURLParameter = "";
            for (String d : destinations) {
                if (!destinationsURLParameter.isEmpty()) destinationsURLParameter += "|place_id:";
                else destinationsURLParameter += "place_id:";
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

            DistancesAPI service = retrofit.create(DistancesAPI.class);
            Call<Matrix> callAsync = service.getResults(latitudeOrigin.toString() + "," + longitudeOrigin.toString(), destinationsURLParameter,
                                                        "walking", ctx.getString(
                            R.string.google_maps_key22));

            callAsync.enqueue(new Callback<Matrix>() {
                @Override
                public void onResponse(Call<Matrix> call, Response<Matrix> response) {
                    Log.i("Test retrofit", "onResponse: " + response.body());
                    Matrix matrix = response.body();
                    for (Row r : matrix.getRows()) elements.addAll(r.getElements());
                }

                @Override
                public void onFailure(Call<Matrix> call, Throwable t) {
                    Log.i("Test retrofit", "onFailure: " + t);
                    System.out.println(t);
                }
            });
        }
        for (int i = 0; i < destinations.size(); i++) {
            try {
                mapResult.put(destinations.get(i), ((Element) elements.get(i)).getDistance().getText());
            } catch (NullPointerException e) {
                Log.i(TAG, "Erreur : " + destinations.get(i));
                mapResult.put(destinations.get(i), "");
            }
        }

        return mapResult;
    }
}
