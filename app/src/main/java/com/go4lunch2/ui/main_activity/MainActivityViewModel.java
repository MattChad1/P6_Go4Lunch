package com.go4lunch2.ui.main_activity;

import static com.go4lunch2.data.api.APIClient.placeAutocompleteAPI;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.data.api.PlaceAutocompleteAPI;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.model.model_gmap.place_autocomplete.Prediction;
import com.go4lunch2.data.model.model_gmap.place_autocomplete.Root;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.SortRepository;
import com.go4lunch2.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {

    String TAG = "MyLog SearchViewModel";
    private Context ctx = MyApplication.getInstance();
    private MutableLiveData<List<SearchViewStateItem>> searchResultsLiveData;
    RestaurantRepository restaurantRepository;
    UserRepository userRepository;
    SortRepository sortRepository;

    public MainActivityViewModel(UserRepository userRepository, SortRepository sortRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.sortRepository = sortRepository;
        this.restaurantRepository = restaurantRepository;
        searchResultsLiveData = new MutableLiveData<>();
    }

    public void updateOrderLiveData(SortRepository.OrderBy order) {
        sortRepository.updateOrderLiveData(order);
    }

    public MutableLiveData<List<SearchViewStateItem>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }

    public LiveData<CustomUser> getCurrentCustomUser(String id) {
        return Transformations.map(userRepository.getCurrentCustomUserLD(id), currentUser -> {
            if (currentUser == null) return new CustomUser(id);
            else return currentUser;
        });
    }

    public void getSearchResults(String s) {
        List<SearchViewStateItem> searchResults = new ArrayList<>();

        PlaceAutocompleteAPI service = placeAutocompleteAPI();
        Call<Root> callAsync = service.getResults("restaurant+" + s, "48.856614, 2.3522219", "5000", "establishment",
                                                  ctx.getString(R.string.google_maps_key22));

        callAsync.enqueue(new Callback<Root>() {

            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                Log.i(TAG, "onResponse: call");
                for (Prediction p : response.body().getPredictions()) {
                    Log.i(TAG, "onResponse: " + p.getPlaceId());
                    searchResults.add(new SearchViewStateItem(
                            p.getPlaceId(),
                            p.getStructuredFormatting().getMainText(),
                            p.getStructuredFormatting().getSecondaryText()
                    ));
                }
                searchResultsLiveData.setValue(searchResults);
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t);
                System.out.println(t);
            }
        });
    }

    public void updateCenter(Location location) {
        restaurantRepository.updateCenter(location);
    }
}
