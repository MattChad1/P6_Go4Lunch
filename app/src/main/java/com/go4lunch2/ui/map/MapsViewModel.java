package com.go4lunch2.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.repositories.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class MapsViewModel extends ViewModel {

    final RestaurantRepository restaurantRepository;
    final Double centerLocationLatitude;
    final Double centerLocationLongitude;

    public MapsViewModel(RestaurantRepository restaurantRepository) {

        this.restaurantRepository = restaurantRepository;
        centerLocationLatitude = 48.856614;
        centerLocationLongitude = 2.3522219;
    }

    public LiveData<List<MapsStateItem>> getMarkersLiveData() {
        return Transformations.map(restaurantRepository.getRestaurantsLiveData(), restaurantsList -> {
            List<MapsStateItem> mapsStateItems = new ArrayList<>();
            for (Restaurant r : restaurantsList) {
                int workmatesInterested = 0;
                if (r.getRcf().getWorkmatesInterestedIds() != null) {
                    if (Utils.ValidForToday(r.getRcf().getLastUpdate())) {
                        workmatesInterested = r.getRcf().getWorkmatesInterestedIds().size();
                    }
                }

                mapsStateItems.add(new MapsStateItem(
                        r.getId(),
                        r.getName(),
                        r.getImage(),
                        r.getLatitude(),
                        r.getLongitude(),
                        Utils.ratingToStars(r.getRcf().getAverageRate()),
                        workmatesInterested
                ));
            }

            return mapsStateItems;
        });
    }
}
