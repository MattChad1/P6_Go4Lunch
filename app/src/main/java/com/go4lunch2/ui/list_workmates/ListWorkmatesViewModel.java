package com.go4lunch2.ui.list_workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.data.model.User;

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
        return Transformations.map(userRepository.getWorkmatesWithRestaurantsLiveData(), users -> {
            List<WorkmateViewStateItem> workmateViewStateItems = new ArrayList<>();
            for (Map.Entry<User, String> entry : users.entrySet()) {
                    WorkmateViewStateItem w = new WorkmateViewStateItem(entry.getKey().getId(), entry.getKey().getAvatar(), entry.getKey().getName(), "",
                                                                                            entry.getValue());
                    if (!workmateViewStateItems.contains(w)) workmateViewStateItems.add(w);
            }
           return workmateViewStateItems;
        });
    }





}
