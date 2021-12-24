package com.go4lunch2.ui.list_restaurants;

import static com.go4lunch2.DI.DI.getReader;
import static com.go4lunch2.data.api.APIClient.distancesAPI;

import android.content.Context;
import android.location.Location;
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

    Double centerLocationLatitude;
    Double centerLocationLongitude;
    private final RestaurantRepository restaurantRepository;
    private final SortRepository sortRepository;
    private final MutableLiveData<List<RestaurantViewState>> allRestaurantsViewStateLD = new MutableLiveData<>();
    private final MutableLiveData<SortRepository.OrderBy> orderLiveData = new MutableLiveData<>();
    private final MediatorLiveData<List<RestaurantViewState>> allRestaurantsWithOrderMediatorLD = new MediatorLiveData<>();

    Context ctx;







    public ListRestaurantsViewModel(RestaurantRepository restaurantRepository, SortRepository sortRepository, Context ctx) {
        this.ctx = ctx;
        this.restaurantRepository = restaurantRepository;
        this.sortRepository = sortRepository;
        centerLocationLatitude = 48.856614;
        centerLocationLongitude = 2.3522219;

        allRestaurantsWithOrderMediatorLD.addSource(getAllRestaurantsViewStateLD(), value -> {
            Log.i(TAG, "ListRestaurantsViewModel: source1");
            allRestaurantsWithOrderMediatorLD.postValue(value);
        });
        allRestaurantsWithOrderMediatorLD.addSource(getOrderLiveData(), order -> {
            Log.i(TAG, "ListRestaurantsViewModel: source2" + order.toString());
            List<RestaurantViewState> restaurants = allRestaurantsViewStateLD.getValue();
            if (restaurants != null && !restaurants.isEmpty()) {
                List<RestaurantViewState> newList = new ArrayList<>();
                if (order.equals(SortRepository.OrderBy.DISTANCE)) newList = Stream.of(restaurants).filter(r -> r.getDistance()!= null).sorted((a, b) -> a.getDistance() - b.getDistance()).toList();
                else if (order == SortRepository.OrderBy.NAME) newList = Stream.of(restaurants).sortBy(RestaurantViewState::getName).toList();
                else if (order == SortRepository.OrderBy.RATING) {
                    newList = Stream.of(restaurants).filter(r -> r.getStarsCount()!= null).sortBy(RestaurantViewState::getStarsCount).toList();
                    newList.addAll(Stream.of(restaurants).filter(r -> r.getStarsCount()== null).toList());
                }

                allRestaurantsWithOrderMediatorLD.setValue(newList);
            }
        } );


    }

    public LiveData<List<RestaurantViewState>> getAllRestaurantsWithOrderMediatorLD() {
        return allRestaurantsWithOrderMediatorLD;
    }

    public LiveData<SortRepository.OrderBy> getOrderLiveData() {
        return sortRepository.getOrderLiveData();
    }


    public LiveData<List<RestaurantViewState>> getAllRestaurantsViewStateLD() {
        return Transformations.map(restaurantRepository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantViewState> restaurantViewStates = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for (Restaurant r : restaurantsList) ids.add(r.getId());
            Map<String, Integer> mapDistance = getDistancesAPI(
                    centerLocationLatitude, centerLocationLongitude, ids);
            for (Restaurant r : restaurantsList) {

                int workmatesInterested = 0;
                if (r.getRcf().getWorkmatesInterestedIds()!= null) {
                    if (Utils.ValidForToday(r.getRcf().getLastUpdate())) {
                        workmatesInterested = r.getRcf().getWorkmatesInterestedIds().size();
                    }
                }

                restaurantViewStates.add(new RestaurantViewState(
                                                 r.getId(),
                                                 r.getName(),
                                                 r.getType(),
                                                 r.getAdress(),
                                                 (r.getOpeningTime().equals("true")) ? ctx.getString(R.string.open) : ctx.getString(R.string.closed),
                                                 mapDistance == null ? null : mapDistance.get(r.getId()),
                                                 Utils.ratingToStars(r.getRcf().getAverageRate()),
                                                 workmatesInterested,
                                                 r.getImage()
                                         )
                                        );
            }
            allRestaurantsViewStateLD.setValue(restaurantViewStates);
            return restaurantViewStates;
        });

    }

    protected Map<String, Integer> getDistancesAPI(Double latitudeOrigin, Double longitudeOrigin, List<String> destinations) {

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
