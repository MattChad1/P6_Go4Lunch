package com.go4lunch2.ui.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.ui.list_restaurants.RestaurantViewState;

import java.util.ArrayList;
import java.util.List;

public class MapsViewModel extends ViewModel {

    String TAG = "MyLog MapsViewModel";
    Repository repository;
    MutableLiveData<List<MapsStateItem>> markersLiveData = new MutableLiveData<>();

    public MapsViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<MapsStateItem>> getMarkersLiveData() {
        return Transformations.map(repository.getRestaurantsLiveData(), restaurantsList -> {
            List<MapsStateItem> mapsStateItems = new ArrayList<>();
            for (Restaurant r : restaurantsList) {
                mapsStateItems.add(new MapsStateItem(
                                                 r.getId(),
                                                 r.getName(),
                                                 r.getImage(),
                                                 r.getLatitude(),
                                                 r.getLongitude(),
                                                 Utils.ratingToStars(r.getRcf().getAverageRate()),
                                                 r.getRcf().getWorkmatesInterestedIds() == null ? 0 : r.getRcf().getWorkmatesInterestedIds().size()
                                   ));
            }

            for (MapsStateItem r : mapsStateItems) {
                Log.i(TAG, r.getName() + "=>" + r.getWorkmatesCount());
            }

            //markersLiveData.setValue(mapsStateItems);
            return mapsStateItems;
        });
    }

}
