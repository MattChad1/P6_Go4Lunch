package com.go4lunch2.ui.list_workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.Workmate;

import java.util.ArrayList;
import java.util.List;

public class ListWorkmatesViewModel extends ViewModel {

    Repository repository;
    MutableLiveData<List<WorkmateViewStateItem>> workmatesWithChoisesLiveData = new MutableLiveData<>();

    public ListWorkmatesViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<WorkmateViewStateItem>> getWorkmatesWithChoisesLiveData() {
        return Transformations.map(repository.getWorkmatesLiveData(), workmates -> {
            List<WorkmateViewStateItem> workmateViewStateItems = new ArrayList<>();
            for (Workmate w : workmates) {
                if (w.getIdRestaurantChosen()!= null) {
                    Restaurant restaurantChosen = repository.getRestaurantById(w.getIdRestaurantChosen());
                    workmateViewStateItems.add(new WorkmateViewStateItem(w.getId(), w.getAvatar(), w.getName(), "", restaurantChosen.getName()));
                }
            }
            return workmateViewStateItems;


        });
    }



}
