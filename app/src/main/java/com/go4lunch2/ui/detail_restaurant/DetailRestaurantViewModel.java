package com.go4lunch2.ui.detail_restaurant;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;

public class DetailRestaurantViewModel extends ViewModel {

    Repository repository;
    MutableLiveData<DetailRestaurantViewState> restaurantSelectedLiveData = new MutableLiveData<>();

    public DetailRestaurantViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<DetailRestaurantViewState> getDetailRestaurantLiveData(String idRestaurant) {

        DetailRestaurantViewState detail = null;
        Restaurant r = repository.getRestaurantById(idRestaurant);
        if (r != null) {
            detail = new DetailRestaurantViewState(
                    r.getId(),
                    r.getName(),
                    r.getType(),
                    r.getAdress(),
                    r.getOpeningTime(),
                    "100 m",
                    Utils.ratingToStars(r.getRcf().getAverageRate()),
                    repository.getListWorkmatesByIds(r.getRcf().getWorkmatesInterestedIds()),
                    r.getImage()
            );
        }

        restaurantSelectedLiveData.setValue(detail);
        return restaurantSelectedLiveData;
    }

    public void addRate (String idWorkmate, String idRestaurant, int givenRate) {
        repository.addGrade(new Rating(idRestaurant, idWorkmate, givenRate));

    }
}
