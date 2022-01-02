package com.go4lunch2.ui.list_workmates;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class ListWorkmatesViewModelTest {

    private final List<CustomUser> users = new ArrayList<>();
    private final MutableLiveData<List<CustomUser>> workmatesWithRestaurantsLiveData = new MutableLiveData<>();

    @Mock
    UserRepository userRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    ListWorkmatesViewModel viewModel;

    final String test1 = "idWorkmate";
    final String test2 = "name restaurant";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        initMocks(this);
        viewModel = new ListWorkmatesViewModel(restaurantRepository, userRepository);

        users.add(new CustomUser(test1, "Mr test", "", "r1", "name restaurant", null));
        workmatesWithRestaurantsLiveData.setValue(users);

        when(userRepository.getWorkmatesWithRestaurantsLiveData()).thenReturn(workmatesWithRestaurantsLiveData);
    }

    @Test
    public void getWorkmatesViewStateItemsLiveData() throws InterruptedException {
        List<WorkmateViewStateItem> listTest =  getOrAwaitValue(viewModel.getWorkmatesViewStateItemsLiveData());
        assertEquals(listTest.get(0).getIdWorkmate(), test1);
        assertEquals(listTest.get(0).getNameRestaurant(), test2);
    }
}