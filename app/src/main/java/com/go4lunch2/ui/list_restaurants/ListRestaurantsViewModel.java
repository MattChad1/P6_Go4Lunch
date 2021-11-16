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
    private MutableLiveData<List<RestaurantsViewState>> allRestaurantsLiveData = new MutableLiveData<>();

    ;

    public ListRestaurantsViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<RestaurantsViewState>> getAllRestaurantsViewStateLiveData() {
        return Transformations.map(repository.getRestaurantsLiveData(), restaurantsList -> {
            List<RestaurantsViewState> restaurantsViewStates = new ArrayList<>();
            for (Restaurant r : restaurantsList) {

                restaurantsViewStates.add(new RestaurantsViewState(
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

            allRestaurantsLiveData.setValue(restaurantsViewStates);
            return restaurantsViewStates;
        });
    }
}
