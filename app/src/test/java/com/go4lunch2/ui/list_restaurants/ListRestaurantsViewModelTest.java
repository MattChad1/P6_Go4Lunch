package com.go4lunch2.ui.list_restaurants;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Application;
import android.content.res.Resources;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.SortRepository;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListRestaurantsViewModelTest {

    List<Restaurant> allRestaurants = new ArrayList<>();
    private final MutableLiveData<List<Restaurant>> fakeRestaurantsLiveData = new MutableLiveData<>();
    private final MutableLiveData<SortRepository.OrderBy> fakeOrderLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> fakeRestaurantsDistancesLiveData = new MutableLiveData<>();
    ListRestaurantsViewModel viewModel;

    String testName1 = "A Fake restaurant";
    String testId1 = "a1";
    Double rating1 = 2.0;

    String testName2 = "B Fake restaurant";
    String testId2 = "a2";
    Double rating2 = 1.0;

    String testName3 = "C Fake restaurant";
    String testId3 = "a3";
    Double rating3 = 2.5;

    @Mock
    RestaurantRepository restaurantRepository;


    @Mock
    SortRepository sortRepository;

    @Mock
    Application application;

    @Mock
    private Resources mockResources;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        allRestaurants.add(new Restaurant(testId1, testName1, "", "", "", 0.0, 0.0, new RestaurantCustomFields(testId1, testName1, rating1, new ArrayList<>(),
                                                                                                               Timestamp.now())));
        allRestaurants.add(new Restaurant(testId2, testName2, "", "", "", 0.0, 0.0, new RestaurantCustomFields(testId2, testName2, rating2, new ArrayList<>(),
                                                                                                               Timestamp.now())));
        allRestaurants.add(new Restaurant(testId3, testName3, "", "", "", 0.0, 0.0, new RestaurantCustomFields(testId3, testName3, rating3, new ArrayList<>(),
                                                                                                               Timestamp.now())));


        fakeRestaurantsLiveData.setValue(allRestaurants);
        when(restaurantRepository.getRestaurantsLiveData()).thenReturn(fakeRestaurantsLiveData);


        fakeOrderLiveData.setValue(SortRepository.OrderBy.NAME);
        when(sortRepository.getOrderLiveData()).thenReturn(fakeOrderLiveData);

        Map<String, Integer> mockMapDistance = new HashMap<>();
        mockMapDistance.put (testId1, 250);
        mockMapDistance.put (testId2, 100);
        mockMapDistance.put (testId3, 1500);

        fakeRestaurantsDistancesLiveData.setValue(mockMapDistance);
        when(restaurantRepository.getRestaurantsDistancesLiveData()).thenReturn(fakeRestaurantsDistancesLiveData);

        viewModel = Mockito.spy(new ListRestaurantsViewModel(restaurantRepository, sortRepository, application));

        when(application.getResources()).thenReturn(mockResources);
        when(mockResources.getString(anyInt())).thenReturn("");

    }

    @Test
    public void getAllRestaurantsViewStateLD() throws InterruptedException {
        List<RestaurantViewState> listTest =  getOrAwaitValue(viewModel.getAllRestaurantsViewStateLD());
        assertEquals(testName1, listTest.get(0).getName());
        assertEquals(testId1, listTest.get(0).getId());
    }

    @Test
    public void testSortingViaMediatorLiveData() throws InterruptedException {
        List<RestaurantViewState>  listTest = getOrAwaitValue(viewModel.getAllRestaurantsWithOrderMediatorLD());
        assertEquals(testName1, listTest.get(0).getName());

        fakeOrderLiveData.setValue(SortRepository.OrderBy.DISTANCE);
        listTest = getOrAwaitValue(viewModel.getAllRestaurantsWithOrderMediatorLD());
        assertEquals(testName2, listTest.get(0).getName());

        fakeOrderLiveData.setValue(SortRepository.OrderBy.RATING);
        listTest = getOrAwaitValue(viewModel.getAllRestaurantsWithOrderMediatorLD());
        assertEquals(testName3, listTest.get(0).getName());

        fakeOrderLiveData.setValue(SortRepository.OrderBy.NAME);
        listTest = getOrAwaitValue(viewModel.getAllRestaurantsWithOrderMediatorLD());
        assertEquals(testName1, listTest.get(0).getName());

    }
}