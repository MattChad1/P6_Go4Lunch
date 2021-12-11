package com.go4lunch2.ui.main_activity;

import android.content.Context;
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
import com.go4lunch2.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivityViewModel extends ViewModel {

    String TAG = "MyLog SearchViewModel";
    private Context ctx = MyApplication.getInstance();
    private MutableLiveData<List<SearchViewStateItem>> searchResultsLiveData;
    UserRepository userRepository;

    public MainActivityViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        searchResultsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<SearchViewStateItem>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }

    public LiveData<CustomUser> getCurrentCustomUser (String id) {
        return Transformations.map(userRepository.getCurrentCustomUserLD(id), currentUser -> {
            if (currentUser == null) return new CustomUser(id);
            else return currentUser;
        });
    }


    public void getSearchResults(String s) {

        List<SearchViewStateItem> searchResults = new ArrayList<>();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        PlaceAutocompleteAPI service = retrofit.create(PlaceAutocompleteAPI.class);
        Call<Root> callAsync = service.getResults("restaurant+" + s, "48.856614, 2.3522219", "1500", "establishment",
                                                  ctx.getString(R.string.google_maps_key22));

        callAsync.enqueue(new Callback<Root>() {

            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                Log.i(TAG, "onResponse: call" );
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








}
