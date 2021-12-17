package com.go4lunch2.ui.list_restaurants;

import static com.go4lunch2.DI.DI.getReader;
import static com.go4lunch2.data.api.APIClient.distancesAPI;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.annimon.stream.Stream;
import com.go4lunch2.BuildConfig;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.api.DistancesAPI;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.model_gmap.Element;
import com.go4lunch2.data.model.model_gmap.Matrix;
import com.go4lunch2.data.model.model_gmap.Row;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.SortRepository;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListRestaurantsViewModel extends ViewModel {

    String TAG = "MyLog RestaurantsViewModel";

    private final RestaurantRepository restaurantRepository;
    private final SortRepository sortRepository;
    private final MutableLiveData<List<RestaurantViewState>> allRestaurantsViewStateLD = new MutableLiveData<>();
    private final MutableLiveData<SortRepository.OrderBy> orderLiveData = new MutableLiveData<>();
    private final MediatorLiveData<List<RestaurantViewState>> allRestaurantsWithOrderMediatorLD;

    Context ctx;
    BufferedReader reader;

    public MediatorLiveData<List<RestaurantViewState>> getAllRestaurantsWithOrderMediatorLD() {
        return allRestaurantsWithOrderMediatorLD;
    }

    public ListRestaurantsViewModel(RestaurantRepository restaurantRepository, SortRepository sortRepository, Context ctx) {
        this.ctx = ctx;
        this.restaurantRepository = restaurantRepository;
        this.sortRepository = sortRepository;

        allRestaurantsWithOrderMediatorLD = new MediatorLiveData<>();
        allRestaurantsWithOrderMediatorLD.addSource(getAllRestaurantsViewStateLD(), value -> {
            Log.i(TAG, "ListRestaurantsViewModel: source1");
            allRestaurantsWithOrderMediatorLD.setValue(value);
        });
        allRestaurantsWithOrderMediatorLD.addSource(getOrderLiveData(), order -> {
                                                        Log.i(TAG, "ListRestaurantsViewModel: source2");
                                                        List<RestaurantViewState> restaurants = getAllRestaurantsViewStateLD().getValue();
                                                        if (restaurants != null && !restaurants.isEmpty()) {
                                                            List<RestaurantViewState> newList = new ArrayList<>();
                                                            if (order == SortRepository.OrderBy.DISTANCE)
                                                                newList =
                                                                        Stream.of(restaurants).sorted((a, b) -> a.getDistance() - b.getDistance()).toList();
                                                            else if (order == SortRepository.OrderBy.NAME) newList = restaurants;
                                                            else if (order == SortRepository.OrderBy.RATING)
                                                                newList = Stream.of(restaurants).sorted((a, b) -> Double.compare(a.getStarsCount(),
                                                                                                                                 b.getStarsCount())).toList();

                                                            allRestaurantsWithOrderMediatorLD.setValue(newList);
                                                        }
                                                    }
                                                   );
    }



    public LiveData<SortRepository.OrderBy> getOrderLiveData() {
        return sortRepository.getOrderLiveData();
    }

    public LiveData<List<RestaurantViewState>> getAllRestaurantsViewStateLD() {
        return Transformations.map(restaurantRepository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantViewState> restaurantViewStates = new ArrayList<>();
            Log.i(TAG, "Appel getAllRestaurantsViewStateLiveData");
            List<String> ids = new ArrayList<>();
            for (Restaurant r : restaurantsList) ids.add(r.getId());
            Map<String, Integer> mapDistance = getDistancesAPI(48.856614, 2.3522219, ids);
            for (Restaurant r : restaurantsList) {

                restaurantViewStates.add(new RestaurantViewState(
                                                 r.getId(),
                                                 r.getName(),
                                                 r.getType(),
                                                 r.getAdress(),
                                                 (r.getOpeningTime() == "true") ? ctx.getString(R.string.open) : ctx.getString(R.string.closed),
                                                 mapDistance.get(r.getId()),
                                                 Utils.ratingToStars(r.getRcf().getAverageRate()),
                                                 r.getRcf().getWorkmatesInterestedIds() == null ? 0 : r.getRcf().getWorkmatesInterestedIds().size(),
                                                 r.getImage()
                                         )
                                        );
            }
            return restaurantViewStates;
        });
    }

    private Map<String, Integer> getDistancesAPI(Double latitudeOrigin, Double longitudeOrigin, List<String> destinations) {

        List<Element> elements = new ArrayList<>();
        Map<String, Integer> mapResult = new HashMap<>();

        if (BuildConfig.DEBUG) {
            try {
                final BufferedReader reader = getReader(ctx, "distances_matrix.json");
                final Gson gson = new Gson();
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

            DistancesAPI service = distancesAPI();
            Call<Matrix> callAsync = service.getResults(latitudeOrigin.toString() + "," + longitudeOrigin.toString(), destinationsURLParameter,
                                                        "walking", ctx.getString(
                            R.string.google_maps_key22));

            callAsync.enqueue(new Callback<Matrix>() {
                @Override
                public void onResponse(Call<Matrix> call, Response<Matrix> response) {
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
                mapResult.put(destinations.get(i), ((Element) elements.get(i)).getDistance().getValue());
            } catch (NullPointerException e) {
                Log.i(TAG, "Erreur : " + destinations.get(i));
                mapResult.put(destinations.get(i), 0);
            } catch (IndexOutOfBoundsException e) {
                Log.i(TAG, "Erreur getDistancesAPI: " + destinations.get(i));
            }
        }

        return mapResult;
    }
}
