package com.go4lunch2.ui.list_workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListWorkmatesViewModel extends ViewModel {

    String TAG = "MyLog ListWorkmatesViewModel";
    RestaurantRepository restaurantRepository;
    UserRepository userRepository;

    public ListWorkmatesViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public LiveData<List<WorkmateViewStateItem>> getWorkmatesViewStateItemsLiveData() {
        return Transformations.map(userRepository.getWorkmatesWithRestaurantsLiveData(), map -> {
            List<WorkmateViewStateItem> workmateViewStateItems = new ArrayList<>();
            for (Map.Entry<CustomUser, String> entry : map.entrySet()) {
                WorkmateViewStateItem w = new WorkmateViewStateItem(entry.getKey().getId(), entry.getKey().getAvatar(), entry.getKey().getName(),
                                                                    entry.getKey().getIdRestaurantChosen(),
                                                                    entry.getValue());
                if (!workmateViewStateItems.contains(w)) workmateViewStateItems.add(w);
            }
            return workmateViewStateItems;
        });
    }
}
