package com.go4lunch2.ui.detail_restaurant;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.model.RestaurantDetails;
import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;
import com.go4lunch2.data.model.model_gmap.restaurant_details.ResultDetails;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class DetailRestaurantViewModelTest {

    String idFromJson = "ChIJzbddL8Jv5kcRSxvM72EgJ90";
    String nameFromJson = "Warwick Paris";
    String adressFromJson = "5 Rue de Berri, 75008 Paris, France";
    String websiteFromJson = "https://www.warwickhotels.com/warwick-paris/";


    private final MutableLiveData<ResultDetails> restaurantDetailsLiveData = new MutableLiveData<>();

    @Mock
    UserRepository userRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    DetailRestaurantViewModel viewModel;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Mock
    private Application application;

    @Mock
    private Resources mockResources;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        viewModel = new DetailRestaurantViewModel(restaurantRepository, userRepository, application);

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("restaurantDetails.json");
        final Gson gson = new Gson();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        RestaurantDetailsJson details = gson.fromJson(reader, RestaurantDetailsJson.class);
        restaurantDetailsLiveData.setValue(details.getResult());
        when(restaurantRepository.getRestaurantDetailsLiveData(anyString())).thenReturn(restaurantDetailsLiveData);
        when(userRepository.getListWorkmatesByIds(anyList())).thenReturn(null);
        when(restaurantRepository.getRestaurantById(anyString())).thenReturn(null);
        when(application.getResources()).thenReturn(mockResources);
        when(mockResources.getString(anyInt())).thenReturn("");


    }

    @Test
    public void getDetailRestaurantLiveData() throws InterruptedException {
        DetailRestaurantViewState detailTest = getOrAwaitValue(viewModel.getDetailRestaurantLiveData(idFromJson));

        assertEquals(idFromJson, detailTest.getId());
        assertEquals(nameFromJson, detailTest.getName());
        assertEquals(adressFromJson, detailTest.getAdress());
        assertEquals(websiteFromJson, detailTest.getWebsite());



    }

    @Test
    public void addRate() {
    }
}