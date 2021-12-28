package com.go4lunch2.data.repositories;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.go4lunch2.DI.DI;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.model.RestaurantDetails;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepositoryTest {

    private Double centerLatitude;
    private Double centerLongitude;

    String idTest1 = "ekadnfq12q3a";
    String idTest2 = "ekadnfq12q3a";
    Restaurant fakeRestaurant1 = new Restaurant(idTest1, "Name 1", "", "true", "xx rue xxxxx", 45.0, 2.0, new RestaurantCustomFields(), new RestaurantDetails());
    Restaurant fakeRestaurant2 = new Restaurant(idTest2, "Name 2", "", "true", "xx rue xxxxx", 46.0, 2.5, new RestaurantCustomFields(), new RestaurantDetails());


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    RestaurantRepository restaurantRepository;
    private List<Restaurant> allRestaurants = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
//        FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
//        when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);

        restaurantRepository = new RestaurantRepository();
//        when(restaurantRepository.getCustomFields(anyList())).thenReturn(null);

    }

    @Test
    public void updateRepoPlaces() {
        allRestaurants.clear();
    }

    @Test
    public void getDetailsFromAPI() {
    }

    @Test
    public void getRestaurantById() {
        allRestaurants.add(fakeRestaurant1);
        allRestaurants.add(fakeRestaurant2);

        Restaurant tested = restaurantRepository.getRestaurantById(idTest1);
        assertEquals(fakeRestaurant1, tested);


    }

    @Test
    public void getDistancesAPI() {
    }
}