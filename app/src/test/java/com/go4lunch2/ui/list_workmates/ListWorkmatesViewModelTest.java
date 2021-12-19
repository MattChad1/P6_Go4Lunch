package com.go4lunch2.ui.list_workmates;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWorkmatesViewModelTest {

    private Map<CustomUser, String> map = new HashMap<>();



    private final MutableLiveData<Map<CustomUser, String>> workmatesWithRestaurantsLiveData = new MutableLiveData<>();

    @Mock
    UserRepository userRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    ListWorkmatesViewModel viewModel;

    String test1 = "idWorkmate";
    String test2 = "name restaurant";

    @Mock
    private Context ctx;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        viewModel = new ListWorkmatesViewModel(restaurantRepository, userRepository);

        map.put(new CustomUser(test1, "Mr test", "", "r1"), test2);
        workmatesWithRestaurantsLiveData.setValue(map);

        when(userRepository.getWorkmatesWithRestaurantsLiveData()).thenReturn(workmatesWithRestaurantsLiveData);
    }

    @Test
    public void getWorkmatesViewStateItemsLiveData() throws InterruptedException {
        List<WorkmateViewStateItem> listTest =  getOrAwaitValue(viewModel.getWorkmatesViewStateItemsLiveData());
        assertTrue(listTest.get(0).getIdWorkmate().equals(test1));
        assertTrue(listTest.get(0).getNameRestaurant().equals(test2));


    }
}