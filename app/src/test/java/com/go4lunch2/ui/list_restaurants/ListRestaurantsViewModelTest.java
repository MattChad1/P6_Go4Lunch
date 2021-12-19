package com.go4lunch2.ui.list_restaurants;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.MyApplication;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.SortRepository;
import com.go4lunch2.ui.list_workmates.ListWorkmatesViewModel;
import com.go4lunch2.ui.list_workmates.WorkmateViewStateItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class ListRestaurantsViewModelTest {

    List<Restaurant> allRestaurants = new ArrayList<>();
    private final MutableLiveData<List<Restaurant>> fakeRestaurantsLiveData = new MutableLiveData<>();
    ListRestaurantsViewModel viewModel;
    String testName = "Fake restaurant";
    String testId = "a1";

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    SortRepository sortRepository;

    @Mock
    Context ctx;




    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        allRestaurants.add(new Restaurant(testId, testName, "", "", "", "", 0.0, 0.0, new RestaurantCustomFields()));
        fakeRestaurantsLiveData.setValue(allRestaurants);
        ctx = Mockito.mock(Context.class);
        viewModel = new ListRestaurantsViewModel(restaurantRepository, sortRepository, ctx);

        when(restaurantRepository.getRestaurantsLiveData()).thenReturn(fakeRestaurantsLiveData);

    }

    @Test
    public void getAllRestaurantsViewStateLD() throws InterruptedException {
        List<RestaurantViewState> listTest =  getOrAwaitValue(viewModel.getAllRestaurantsViewStateLD());
        assertTrue(listTest.get(0).getName().equals(testName));
        assertTrue(listTest.get(0).getId().equals(testId));

    }
}