package com.go4lunch2;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.go4lunch2.data.Repository;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantViewModel;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsViewModel;
import com.go4lunch2.ui.map.MapsViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(new Repository());
                }
            }
        }
        return factory;
    }

    @NonNull
    private final Repository repository;


    private ViewModelFactory(@NonNull Repository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListRestaurantsViewModel.class)) {
            return (T) new ListRestaurantsViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(MapsViewModel.class)) {
            return (T) new MapsViewModel(repository);
        }
        else throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
