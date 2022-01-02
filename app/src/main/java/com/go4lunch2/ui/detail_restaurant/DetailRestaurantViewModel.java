package com.go4lunch2.ui.detail_restaurant;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class DetailRestaurantViewModel extends AndroidViewModel {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public DetailRestaurantViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository, Application application) {
        super(application);
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public LiveData<CustomUser> getCurrentCustomUser(String id) {
        return Transformations.map(userRepository.getCurrentCustomUserLD(id), currentUser -> {
            if (currentUser == null) return new CustomUser(id);
            else return currentUser;
        });
    }

    // get datas for the restaurant selected from restaurant repository
    public LiveData<DetailRestaurantViewState> getDetailRestaurantLiveData(String idRestaurant) {
        return Transformations.map(restaurantRepository.getRestaurantDetailsLiveData(idRestaurant), restaurant -> {

            if (restaurant != null) {
                String image = (restaurant.getPhotos() != null)
                        ? "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
                        + "&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference()
                        + "&key=" + getApplication().getResources().getString(R.string.google_maps_key22)
                        : null;

                Restaurant restaurantInRepo = restaurantRepository.getRestaurantById(idRestaurant);

                return new DetailRestaurantViewState(
                        idRestaurant,
                        restaurant.getName(),
                        restaurant.getFormattedAddress(),
                        restaurantInRepo != null ? Utils.ratingToStars(restaurantInRepo.getRcf().getAverageRate()) : null,
                        restaurantInRepo != null ? userRepository.getListWorkmatesByIds(restaurantInRepo.getRcf().getWorkmatesInterestedIds()) : null,
                        image,
                        restaurant.getInternationalPhoneNumber(),
                        restaurant.getWebsite()
                );
            }
            // if data problem
            else return new DetailRestaurantViewState(idRestaurant);
        });
    }

    // get the list of the workmates who have selected this restaurant (list is shown at the bottom of the screen if there are any)
    public LiveData<List<CustomUser>> getWorkmatesForThisRestaurantLiveData(String idRestaurant) {
        return Transformations.map(userRepository.getWorkmatesWithRestaurantsLiveData(), users -> {
            List<CustomUser> workmates = new ArrayList<>();
            for (CustomUser c : users) {
                if (c.getIdRestaurantChosen() != null && c.getIdRestaurantChosen().equals(idRestaurant)) {
                    workmates.add(c);
                }
            }
            return workmates;
        });
    }

    public void addRate(String idWorkmate, String idRestaurant, int givenRate) {
        restaurantRepository.addGrade(new Rating(idRestaurant, idWorkmate, givenRate));
    }

    public void updateRestaurantChosen(String idUser, String idRestaurant, String nameRestaurant) {
        userRepository.updateRestaurantChosen(idUser, idRestaurant, nameRestaurant);
    }
}