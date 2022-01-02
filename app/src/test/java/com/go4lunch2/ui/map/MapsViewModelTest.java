package com.go4lunch2.ui.map;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.repositories.RestaurantRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class MapsViewModelTest {

    @Mock
    RestaurantRepository restaurantRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    MapsViewModel viewModel;
    private final MutableLiveData<List<Restaurant>> mockRestaurantsLiveData = new MutableLiveData<>();
    final List<Restaurant> fakeList = new ArrayList<>();

    final String testId = "abcd1234";
    final String testName = "Le bon restaurant";
    final Double testLatitude = 45.0;
    final Double testLongitude = 2.0;



    @Before
    public void setUp() {
        initMocks(this);
        viewModel = new MapsViewModel(restaurantRepository);
        fakeList.add(new Restaurant(testId, testName, "", "true", "", testLatitude, testLongitude, new RestaurantCustomFields()));
        mockRestaurantsLiveData.setValue(fakeList);
        when (restaurantRepository.getRestaurantsLiveData()).thenReturn(mockRestaurantsLiveData);
    }

    @Test
    public void getMarkersLiveData() throws InterruptedException {
        List<MapsStateItem> markers = getOrAwaitValue(viewModel.getMarkersLiveData());
        assertEquals(testId, markers.get(0).getId());
        assertEquals(testName, markers.get(0).getName());
        assertEquals(testLatitude, markers.get(0).getLatitude());
        assertEquals(testLongitude, markers.get(0).getLongitude());

    }
}