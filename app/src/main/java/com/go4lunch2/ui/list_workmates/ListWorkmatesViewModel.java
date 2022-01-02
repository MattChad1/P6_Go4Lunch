package com.go4lunch2.ui.list_workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ListWorkmatesViewModel extends ViewModel {

    final RestaurantRepository restaurantRepository;
    final UserRepository userRepository;

    public ListWorkmatesViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public LiveData<List<WorkmateViewStateItem>> getWorkmatesViewStateItemsLiveData() {
        return Transformations.map(userRepository.getWorkmatesWithRestaurantsLiveData(), users -> {
            List<WorkmateViewStateItem> workmateViewStateItems = new ArrayList<>();
            for (CustomUser entry : users) {
                WorkmateViewStateItem w = new WorkmateViewStateItem(entry.getId(), entry.getAvatar(), entry.getName(),
                                                                    entry.getIdRestaurantChosen(),
                                                                    entry.getNameRestaurantChosen());
                workmateViewStateItems.add(w);
            }
            return workmateViewStateItems;
        });
    }
}
