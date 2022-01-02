package com.go4lunch2.ui.list_restaurants;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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

    private final RestaurantRepository restaurantRepository;
    private final SortRepository sortRepository;
    private final MutableLiveData<List<RestaurantViewState>> allRestaurantsViewStateLD = new MutableLiveData<>();
    private final MediatorLiveData<List<RestaurantViewState>> allRestaurantsWithOrderMediatorLD = new MediatorLiveData<>();

    public ListRestaurantsViewModel(RestaurantRepository restaurantRepository, SortRepository sortRepository, @NonNull Application application) {
        super(application);
        this.restaurantRepository = restaurantRepository;
        this.sortRepository = sortRepository;

        // 3 sources for the data
        // Source 1 = all restaurant repository
        allRestaurantsWithOrderMediatorLD.addSource(getAllRestaurantsViewStateLD(), value -> allRestaurantsWithOrderMediatorLD.setValue(getListSorted(value, getOrderLiveData().getValue(), getDistancesLiveData().getValue())));

        // Source 2 = Distances to each restaurant (from API matrix distances)
        allRestaurantsWithOrderMediatorLD.addSource(getDistancesLiveData(), map -> allRestaurantsWithOrderMediatorLD.setValue(getListSorted(allRestaurantsViewStateLD.getValue(), getOrderLiveData().getValue(), map)));

//         Source 3 : order in which the user wants the list of restaurants
        allRestaurantsWithOrderMediatorLD.addSource(getOrderLiveData(), order -> allRestaurantsWithOrderMediatorLD.setValue(getListSorted(allRestaurantsViewStateLD.getValue(), order, getDistancesLiveData().getValue())));
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

    protected LiveData<List<RestaurantViewState>> getAllRestaurantsViewStateLD() {
        return Transformations.map(restaurantRepository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantViewState> restaurantViewStates = new ArrayList<>();
            Map<String, Integer> mapDistance = getDistancesLiveData().getValue();
            for (Restaurant r : restaurantsList) {

                // set the number of workmates who have selected this restaurant since last lunchtime
                int workmatesInterested = 0;
                if (r.getRcf().getWorkmatesInterestedIds() != null && !r.getRcf().getWorkmatesInterestedIds().isEmpty()) {
                    if (Utils.ValidForToday(r.getRcf().getLastUpdate())) {
                        workmatesInterested = r.getRcf().getWorkmatesInterestedIds().size();
                    }
                }

                String restaurantOpen = null;
                if (r.getOpeningTime()!=null) {
                    if (r.getOpeningTime().equals("true")) restaurantOpen = getApplication().getResources().getString(R.string.open);
                    else restaurantOpen = getApplication().getResources().getString(R.string.closed);
                }
                restaurantViewStates.add(new RestaurantViewState(
                                                 r.getId(),
                                                 r.getName(),
                                                 r.getAdress(),
                                                 restaurantOpen,
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

    protected List<RestaurantViewState> getListSorted(List<RestaurantViewState> restaurants, SortRepository.OrderBy order,
                                                      Map<String, Integer> distances) {

        List<RestaurantViewState> newList = new ArrayList<>();

        if (restaurants != null && !restaurants.isEmpty()) {
            newList.addAll(restaurants);

            if (order.equals(SortRepository.OrderBy.DISTANCE)) {
                // to avoid bug if distances is recalculating
                if (distances != null && !distances.isEmpty()) {
                    newList = Stream.of(restaurants).filter(r -> r.getDistance() != null).sorted(
                            (a, b) -> a.getDistance() - b.getDistance()).toList();
                }
            }
            else if (order == SortRepository.OrderBy.NAME) newList = Stream.of(restaurants).sortBy(RestaurantViewState::getName).toList();
            else if (order == SortRepository.OrderBy.RATING) {
                newList = Stream.of(restaurants).filter(r -> r.getStarsCount() != null).sortBy(RestaurantViewState::getStarsCount).toList();
                Collections.reverse(newList);
                newList.addAll(Stream.of(restaurants).filter(r -> r.getStarsCount() == null).toList());
            }

            if (distances != null && !distances.isEmpty()) {
                for (RestaurantViewState r : newList) {
                    if (distances.containsKey(r.getId())) r.setDistance(distances.get(r.getId()));
                }
            }
        }
        return newList;
    }
}
