package com.go4lunch2;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.DI.DI;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsViewModel;
import com.go4lunch2.ui.main_activity.MainActivityViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Any;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


@RunWith(MockitoJUnitRunner.class)
public class ViewModelTests {

    List<Restaurant> allRestaurants = new ArrayList<>();


    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    private Application application;

    @Mock
    AssetManager assetManager;
    @Mock
    InputStream inputStream;

    @Mock
    private Context ctx;


    ListRestaurantsViewModel viewModel;

    MutableLiveData<List<Restaurant>> fakeRestaurantsLiveData = new MutableLiveData<>();


    @Mock
    Map<String, String> mapDistance;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void setUp() throws IOException {
        initMocks(this);

        allRestaurants.add (new Restaurant("aa", "Fake restaurant", "", "", "", "", 0.0, 0.0, new RestaurantCustomFields()));
        fakeRestaurantsLiveData.setValue(allRestaurants);
        ctx = Mockito.mock(Context.class);
        application = mock(MyApplication.class);


//        String resource = "distances_matrix.json";
//
//        assetManager = ctx.getAssets();
//        doReturn(assetManager).when(ctx).getAssets();
//
//        InputStream is = assetManager.open(resource);
//        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//
//        doReturn(inputStream).when(assetManager).open(resource);

//        InputStream isMock = mock(InputStream.class);
//        db = Mockito.mock(FirebaseFirestore.class);
//        Mockito.when(ff.getInstance()).thenReturn(/* null ?? */);
//        restaurantRepository = new RestaurantRepository();
//        userRepository = new UserRepository();
        viewModel = new ListRestaurantsViewModel(restaurantRepository, ctx);

        Mockito.when(restaurantRepository.getRestaurantsLiveData()).thenReturn(fakeRestaurantsLiveData);
//        Mockito.when(DI.getAppContext()).thenReturn(ctx);
//        Mockito.when(MyApplication.getDebug()).thenReturn(true);


    }

    @Test
    public void checkItemsCount() throws InterruptedException {
        int numRepository = allRestaurants.size();
        int numLD = getOrAwaitValue(viewModel.getAllRestaurantsViewStateLiveData()).size();
        assertEquals(numLD, numRepository);
        assertTrue(numRepository ==1);
        assertTrue(numLD ==1);
    }


}