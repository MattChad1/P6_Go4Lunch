package com.go4lunch2.ui.list_restaurants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class ListRestaurantsViewModel extends ViewModel {

    private Repository repository;
    private MutableLiveData<List<RestaurantViewState>> allRestaurantsLiveData = new MutableLiveData<>();

    ;

    public ListRestaurantsViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<RestaurantViewState>> getAllRestaurantsViewStateLiveData() {
        return Transformations.map(repository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantViewState> restaurantViewStates = new ArrayList<>();
            for (Restaurant r : restaurantsList) {

                restaurantViewStates.add(new RestaurantViewState(
                                                  r.getId(),
                                                  r.getName(),
                                                  r.getType(),
                                                  r.getAdress(),
                                                  r.getOpeningTime(),
                                                  "100 m",
                                                  Utils.ratingToStars(r.getRatingAverage()),
                                                  r.getWorkmatesInterested().size(),
                                                  r.getImage()
                                          )
                                        );
            }

            allRestaurantsLiveData.setValue(restaurantViewStates);
            return restaurantViewStates;
        });
    }
}
