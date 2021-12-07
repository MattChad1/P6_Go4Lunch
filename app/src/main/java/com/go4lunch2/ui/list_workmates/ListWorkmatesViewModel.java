package com.go4lunch2.ui.list_workmates;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class ListWorkmatesViewModel extends ViewModel {

    String TAG = "MyLog ListWorkmatesViewModel";
    RestaurantRepository restaurantRepository;
    UserRepository userRepository;
    MutableLiveData<List<WorkmateViewStateItem>> workmatesWithChoisesLiveData = new MutableLiveData<>();

    public ListWorkmatesViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public LiveData<List<WorkmateViewStateItem>> getWorkmatesWithChoisesLiveData() {
        return Transformations.map(userRepository.getWorkmatesWithRestaurantsLiveData(), workmates -> {
            List<WorkmateViewStateItem> workmateViewStateItems = new ArrayList<>();
            for (User w : workmates) {
                if (w.getIdRestaurantChosen()!= null) {
                    Restaurant restaurantChosen = restaurantRepository.getRestaurantById(w.getIdRestaurantChosen());
                    workmateViewStateItems.add(new WorkmateViewStateItem(w.getId(), w.getAvatar(), w.getName(), "", restaurantChosen.getName()));
                }
            }
            return workmateViewStateItems;


        });
    }



}
