package com.go4lunch2.ui.detail_restaurant;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.api.PlaceDetailsAPI;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;
import com.go4lunch2.data.model.model_gmap.restaurant_details.Result;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailRestaurantViewModel extends ViewModel {

    String TAG = "MyLog DetailRestaurantViewModel";
    RestaurantRepository restaurantRepository;
    UserRepository userRepository;
    MutableLiveData<DetailRestaurantViewState> restaurantSelectedLiveData = new MutableLiveData<>();
    Context ctx = MyApplication.getInstance();

    public DetailRestaurantViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public LiveData<DetailRestaurantViewState> getDetailRestaurantLiveData(String idRestaurant) {

        DetailRestaurantViewState detail = null;
        Restaurant r = restaurantRepository.getRestaurantById(idRestaurant);

        if (r != null) {

            detail = new DetailRestaurantViewState(
                    r.getId(),
                    r.getName(),
                    r.getType(),
                    r.getAdress(),
                    r.getOpeningTime(),
                    "100 m",
                    Utils.ratingToStars(r.getRcf().getAverageRate()),
                    userRepository.getListWorkmatesByIds(r.getRcf().getWorkmatesInterestedIds()),
                    r.getImage(),
                    r.getRestaurantDetails().getPhone(),
                    r.getRestaurantDetails().getWebsite()
            );

            if (r.getRestaurantDetails().getPhone()== null) getDetailsFromAPI(detail);
        }
        restaurantSelectedLiveData.setValue(detail);
        return restaurantSelectedLiveData;
    }

    public void getDetailsFromAPI (DetailRestaurantViewState r) {
        Log.i(TAG, "Appel getDetailsFromAPI: id = " + r.getId());
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


        PlaceDetailsAPI service = retrofit.create(PlaceDetailsAPI.class);
        Call<RestaurantDetailsJson> callAsync = service.getResults(r.getId(),
                                                                   ctx.getString(R.string.google_maps_key22));


        callAsync.enqueue(new Callback<RestaurantDetailsJson>() {
            @Override
            public void onResponse(Call<RestaurantDetailsJson> call, Response<RestaurantDetailsJson> response) {
                Log.i(TAG, "onResponse: " + response.body().toString());
                Result res = response.body().getResult();
                Log.i(TAG, "onResponse: " + res.toString());
                String phone = res.getInternationalPhoneNumber();
                String website = res.getWebsite();

                String image =
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
                        + "&photo_reference=" + response.body().getResult().getPhotos().get(0).getPhotoReference()
                        + "&key=" + ctx.getString(R.string.google_maps_key22);

                Log.i(TAG, "onResponse: " + image);

                DetailRestaurantViewState restaurantViewStateUpdated = new DetailRestaurantViewState(
                        r.getId(),
                        r.getName(),
                        r.getType(),
                        r.getAdress(),
                        r.getOpeningHours(),
                        r.getDistance(),
                        r.starsCount,
                        r.getWorkmatesInterested(),
                        image,
                        phone,
                        website
                );
                restaurantSelectedLiveData.setValue(restaurantViewStateUpdated);

            }

            @Override
            public void onFailure(Call<RestaurantDetailsJson> call, Throwable t) {
                System.out.println(t);
            }
        });
    }




    public void addRate (String idWorkmate, String idRestaurant, int givenRate) {
        restaurantRepository.addGrade(new Rating(idRestaurant, idWorkmate, givenRate));
    }

    public void updateRestaurantChosen (String idUser, String idRestaurant) {
        userRepository.updateRestaurantChosen( idUser, idRestaurant);
    }

}
