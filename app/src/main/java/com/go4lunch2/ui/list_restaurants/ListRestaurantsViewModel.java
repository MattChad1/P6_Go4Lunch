package com.go4lunch2.ui.list_restaurants;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.annimon.stream.Stream;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.SortRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListRestaurantsViewModel extends AndroidViewModel {

    String TAG = "MyLog RestaurantsViewModel";

    Double centerLocationLatitude;
    Double centerLocationLongitude;
    private final RestaurantRepository restaurantRepository;
    private final SortRepository sortRepository;
    private final MutableLiveData<List<RestaurantViewState>> allRestaurantsViewStateLD = new MutableLiveData<>();
    private final MutableLiveData<SortRepository.OrderBy> orderLiveData = new MutableLiveData<>();
    private final MediatorLiveData<List<RestaurantViewState>> allRestaurantsWithOrderMediatorLD = new MediatorLiveData<>();







    public ListRestaurantsViewModel(RestaurantRepository restaurantRepository, SortRepository sortRepository, @NonNull Application application) {
        super(application);
        this.restaurantRepository = restaurantRepository;
        this.sortRepository = sortRepository;
        centerLocationLatitude = 48.856614;
        centerLocationLongitude = 2.3522219;

        // 3 sources for the data
        // Source 1 = all restaurant repository
        allRestaurantsWithOrderMediatorLD.addSource(getAllRestaurantsViewStateLD(), value -> {allRestaurantsWithOrderMediatorLD.postValue(value);});

        // Source 2 = Distances to each restaurant (from API matrix distances)
        allRestaurantsWithOrderMediatorLD.addSource(getDistancesLiveData(), map -> {
            List<RestaurantViewState> restaurants = allRestaurantsViewStateLD.getValue();
            if (restaurants != null && !restaurants.isEmpty()) {
               for (RestaurantViewState r: restaurants) {
                   if (map.containsKey(r.getId())) r.setDistance(map.get(r.getId()));
               }
            }
            allRestaurantsWithOrderMediatorLD.setValue(restaurants);
        });

//         Source 3 : order in which the user wants the list of restaurants
        allRestaurantsWithOrderMediatorLD.addSource(getOrderLiveData(), order -> {
            List<RestaurantViewState> restaurants = allRestaurantsViewStateLD.getValue();
            if (restaurants != null && !restaurants.isEmpty()) {
                List<RestaurantViewState> newList = new ArrayList<>();
                if (order.equals(SortRepository.OrderBy.DISTANCE)) newList = Stream.of(restaurants).filter(r -> r.getDistance()!= null).sorted((a, b) -> a.getDistance() - b.getDistance()).toList();
                else if (order == SortRepository.OrderBy.NAME) newList = Stream.of(restaurants).sortBy(RestaurantViewState::getName).toList();
                else if (order == SortRepository.OrderBy.RATING) {
                    newList = Stream.of(restaurants).filter(r -> r.getStarsCount()!= null).sortBy(RestaurantViewState::getStarsCount).toList();
                    Collections.reverse(newList);
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
    public LiveData<Map<String, Integer>> getDistancesLiveData() {
        return restaurantRepository.getRestaurantsDistancesLiveData();
    }


    public LiveData<List<RestaurantViewState>> getAllRestaurantsViewStateLD() {
        return Transformations.map(restaurantRepository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantViewState> restaurantViewStates = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for (Restaurant r : restaurantsList) ids.add(r.getId());
            Map<String, Integer> mapDistance = getDistancesLiveData().getValue();
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
                                                 r.getAdress(),
                                                 (r.getOpeningTime().equals("true")) ? getApplication().getResources().getString(R.string.open) : getApplication().getResources().getString(R.string.closed),
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



}
