package com.go4lunch2;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantViewModel;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsViewModel;
import com.go4lunch2.ui.list_workmates.ListWorkmatesViewModel;
import com.go4lunch2.ui.login.LogInViewModel;
import com.go4lunch2.ui.main_activity.MainActivityViewModel;
import com.go4lunch2.ui.map.MapsViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(new RestaurantRepository(), new UserRepository());
                }
            }
        }
        return factory;
    }

    @NonNull
    private final RestaurantRepository restaurantRepository;

    @NonNull
    private final UserRepository userRepository;

    private ViewModelFactory(@NonNull RestaurantRepository restaurantRepository, @NonNull UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(userRepository);
        }
        else if (modelClass.isAssignableFrom(ListRestaurantsViewModel.class)) {
            return (T) new ListRestaurantsViewModel(restaurantRepository);
        }
        else if (modelClass.isAssignableFrom(LogInViewModel.class)) {
            return (T) new LogInViewModel(userRepository);
        }
        else if (modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(restaurantRepository, userRepository);
        }
        else if (modelClass.isAssignableFrom(MapsViewModel.class)) {
            return (T) new MapsViewModel(restaurantRepository);
        }
        else if (modelClass.isAssignableFrom(ListWorkmatesViewModel.class)) {
            return (T) new ListWorkmatesViewModel(restaurantRepository, userRepository);
        }
        else throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
