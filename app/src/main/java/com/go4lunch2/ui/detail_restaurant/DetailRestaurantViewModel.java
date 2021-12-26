package com.go4lunch2.ui.detail_restaurant;

import static com.go4lunch2.data.api.APIClient.placeDetailsAPI;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.api.PlaceDetailsAPI;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;
import com.go4lunch2.data.model.model_gmap.restaurant_details.ResultDetails;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRestaurantViewModel extends ViewModel {

    private String TAG = "MyLog DetailRestaurantViewModel";
    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;
    private MutableLiveData<DetailRestaurantViewState> restaurantSelectedLiveData = new MutableLiveData<>();
    private Context ctx = MyApplication.getInstance();

    public DetailRestaurantViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public LiveData<CustomUser> getCurrentCustomUser(String id) {
        return Transformations.map(userRepository.getCurrentCustomUserLD(id), currentUser -> {
            if (currentUser == null) return new CustomUser(id);
            else return currentUser;
        });
    }

    public LiveData<DetailRestaurantViewState> getDetailRestaurantLiveData(String idRestaurant) {
        return Transformations.map(restaurantRepository.getRestaurantDetailsLiveData(idRestaurant), restaurant -> {
            String image =
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
                            + "&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference()
                            + "&key=" + ctx.getString(R.string.google_maps_key22);

            DetailRestaurantViewState detailRestaurantViewState = new DetailRestaurantViewState(
                    idRestaurant,
                    restaurant.getName(),
                    restaurant.getFormattedAddress(),
                    null,
                    null,
                    image,
                    restaurant.getInternationalPhoneNumber(),
                    restaurant.getWebsite()
            );

            return detailRestaurantViewState;
        });
    }

//    public void getDetails(String idRestaurant) {
//        DetailRestaurantViewState detailRestaurantViewState = null;
////        PlaceDetailsAPI service = placeDetailsAPI();
////        Call<RestaurantDetailsJson> callAsync = service.getResults(idRestaurant, ctx.getString(R.string.google_maps_key22));
////
////        callAsync.enqueue(new Callback<RestaurantDetailsJson>() {
////            @Override
////            public void onResponse(Call<RestaurantDetailsJson> call, Response<RestaurantDetailsJson> response) {
////                ResultDetails detailsResponseAPI = response.body().getResult();
////
////                String image =
////                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
////                                + "&photo_reference=" + response.body().getResult().getPhotos().get(0).getPhotoReference()
////                                + "&key=" + ctx.getString(R.string.google_maps_key22);
//
//
//
//
//            Restaurant r = restaurantRepository.getRestaurantById(idRestaurant);
//            if(r!=null)
//
//            {
//                detailRestaurantViewState.setStarsCount(Utils.ratingToStars(r.getRcf().getAverageRate()));
//                detailRestaurantViewState.setWorkmatesInterested(userRepository.getListWorkmatesByIds(r.getRcf().getWorkmatesInterestedIds()));
//            }
//                restaurantSelectedLiveData.setValue(detailRestaurantViewState);
//        }
//
//        @Override
//        public void onFailure (Call < RestaurantDetailsJson > call, Throwable t){
//            Log.e(TAG, "onFailure: "+ t );
//        }
//    });
//}

    public void addRate(String idWorkmate, String idRestaurant, int givenRate) {
        restaurantRepository.addGrade(new Rating(idRestaurant, idWorkmate, givenRate));
    }

    public void updateRestaurantChosen(String idUser, String idRestaurant) {
        userRepository.updateRestaurantChosen(idUser, idRestaurant);
    }
}
