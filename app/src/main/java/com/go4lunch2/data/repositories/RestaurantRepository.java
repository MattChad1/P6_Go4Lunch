package com.go4lunch2.data.repositories;

import static com.go4lunch2.data.api.APIClient.distancesAPI;
import static com.go4lunch2.data.api.APIClient.placeDetailsAPI;
import static com.go4lunch2.data.api.APIClient.placesAPI;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.annimon.stream.IntStream;
import com.annimon.stream.OptionalInt;
import com.annimon.stream.Stream;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.data.api.DistancesAPI;
import com.go4lunch2.data.api.PlaceDetailsAPI;
import com.go4lunch2.data.api.PlacesAPI;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.model.model_gmap.Element;
import com.go4lunch2.data.model.model_gmap.Matrix;
import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.Result;
import com.go4lunch2.data.model.model_gmap.Row;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;
import com.go4lunch2.data.model.model_gmap.restaurant_details.ResultDetails;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private final MutableLiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResultDetails> restaurantDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> restaurantsDistancesLiveData = new MutableLiveData<>();
    private final List<Restaurant> allRestaurants = new ArrayList<>();
    private final CollectionReference colRefRestaurants;
    private final Map<String, Integer> distancesResult = new HashMap<>();
    final String TAG = "MyLog Repository";
    final FirebaseFirestore db;
    final Context ctx = MyApplication.getInstance();
    private Double centerLatitude;
    private Double centerLongitude;

    public RestaurantRepository() {
        db = FirebaseFirestore.getInstance();
        colRefRestaurants = db.collection("restaurants");
        centerLatitude = 48.856614;
        centerLongitude = 2.3522219;
        getPlacesAPI(centerLatitude, centerLongitude);
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsLiveData() {
        return restaurantsLiveData;
    }

    public MutableLiveData<ResultDetails> getRestaurantDetailsLiveData(String idRestaurant) {
        getDetailsFromAPI(idRestaurant);
        return restaurantDetailsLiveData;
    }

    public MutableLiveData<Map<String, Integer>> getRestaurantsDistancesLiveData() {
        return restaurantsDistancesLiveData;
    }

    // get JSON results from the Google API
    private void getPlacesAPI(Double latitude, Double longitude) {
        allRestaurants.clear();
        List<Result> results = new ArrayList<>();

        // In case we are in debug mode (set in MyApplication), use a local file
        if (MyApplication.getDebug()) {
            try {
                AssetManager am = ctx.getAssets();
                InputStream is = am.open("restaurants_json.json");
                final Gson gson = new Gson();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Place place = gson.fromJson(reader, Place.class);
                results.addAll(place.getResults());
                updateRepoPlaces(results);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            PlacesAPI service = placesAPI();
            Call<Place> callAsync = service.getResults(latitude.toString() + "," + longitude.toString(), "1500", "restaurant",
                                                       ctx.getString(R.string.google_maps_key22));

            callAsync.enqueue(new Callback<Place>() {
                @Override
                public void onResponse(@NonNull Call<Place> call, @NonNull Response<Place> response) {

                    if (response.body() != null) {
                        results.addAll(response.body().getResults());
                    }
                    updateRepoPlaces(results);
                }

                @Override
                public void onFailure(@NonNull Call<Place> call, @NonNull Throwable t) {
                    Log.i(TAG, "Repository - onFailure: " + t);
                }
            });
        }
    }

    // deals with the Json results to create the places list
    public void updateRepoPlaces(List<Result> results) {
        for (Result result : results) {
            String image = null;
            if (!MyApplication.getDebug()) {
                try {
                    image =
                            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=150"
                                    + "&photo_reference=" + result.getPhotos().get(0).getPhotoReference()
                                    + "&key=" + ctx.getString(R.string.google_maps_key22);
                } catch (Exception e) {
                    Log.w(TAG, "No image found for this restaurant");
                }
            }
            String finalImage = image;
            Restaurant newRestaurant = new Restaurant(
                    result.getPlaceId(),
                    result.getName(),
                    finalImage,
                    result.getOpeningHours() != null ? result.getOpeningHours().getOpenNow() : "",
                    result.getVicinity(),
                    result.getGeometry().location.lat,
                    result.getGeometry().location.getLng(),
                    new RestaurantCustomFields()
            );

            OptionalInt num = IntStream.range(0, allRestaurants.size())
                    .filter(i -> allRestaurants.get(i).getId().equals(result.getPlaceId()))
                    .findFirst();

            if (!num.isEmpty()) {
                allRestaurants.set(num.getAsInt(), newRestaurant);
            }
            else {
                allRestaurants.add(newRestaurant);
            }
        }
        getCustomFields(allRestaurants);
        getDistancesAPI(allRestaurants);
    }

    // Get custom fields form db : rates, list of workmates who have selected this restaurant
    protected void getCustomFields(List<Restaurant> allRestaurants) {
        for (Restaurant r : allRestaurants) {
            final DocumentReference docRef = colRefRestaurants.document(r.getId());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                RestaurantCustomFields rcf;

                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed in getCustomFields().", e);
                    }
                    else if (snapshot != null && snapshot.exists()) {
                        rcf = snapshot.toObject(RestaurantCustomFields.class);
                        OptionalInt num = IntStream.range(0, allRestaurants.size())
                                .filter(i -> allRestaurants.get(i).getId().equals(r.getId()))
                                .findFirst();

                        if (!num.isEmpty()) {
                            allRestaurants.get(num.getAsInt()).setRcf(rcf);
                        }
                        restaurantsLiveData.setValue(allRestaurants);
                    }
                    else {
                        rcf = new RestaurantCustomFields();
                        insertNewRestaurantDB(r.getId(), r.getName());
                    }
                }
            });
        }
    }

    //Get contact infos
    public void getDetailsFromAPI(String idRestaurant) {
        PlaceDetailsAPI service = placeDetailsAPI();
        Call<RestaurantDetailsJson> callAsync = service.getResults(idRestaurant, ctx.getString(R.string.google_maps_key22));

        callAsync.enqueue(new Callback<RestaurantDetailsJson>() {
            @Override
            public void onResponse(@NonNull Call<RestaurantDetailsJson> call, @NonNull Response<RestaurantDetailsJson> response) {
                ResultDetails resultDetails;
                if (response.body() != null) {
                    Log.i(TAG, "onResponse: getDetailsFromAPI update");
                    resultDetails = response.body().getResult();
                    restaurantDetailsLiveData.postValue(resultDetails);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestaurantDetailsJson> call, @NonNull Throwable t) {
            }
        });
    }

    // Insert a new restaurant in the "custom fields" table if its id is not known
    private void insertNewRestaurantDB(String idRestaurant, String name) {
        db.collection("restaurants")
                .document(idRestaurant)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("idRestaurant", idRestaurant);
                            data.put("name", name);
                            data.put("averageRate", null);
                            data.put("workmatesInterestedIds", new ArrayList<String>());
                            data.put("lastUpdate", FieldValue.serverTimestamp());

                            db.collection("restaurants").document(idRestaurant)
                                    .set(data);
                        }
                    }
                    else {
                        Log.d(TAG, "get failed with insertion of new restaurant : ", task.getException());
                    }
                });
    }

    public void addGrade(Rating rating) {
        // Check if user has already given a rate
        db.collection("rates")
                .whereEqualTo("idWorkmate", rating.getIdWorkmate())
                .whereEqualTo("idRestaurant", rating.getIdRestaurant())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // if there is already a rate => update it and call updateAverageRate
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("rates").document(document.getId()).update("rateGiven", rating.getRateGiven());
                                updateAverageRate(rating.getIdRestaurant());
                            }
                        }
                        // else => add new one and call updateAverageRate
                        else {
                            db.collection("rates")
                                    .add(rating)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        updateAverageRate(rating.getIdRestaurant());
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                        }
                    }
                    else Log.w(TAG, "FAIL  addGrade()");
                });
    }

    private void updateAverageRate(String idRestaurant) {
        // use of array for modification inside lambda with java 8
        int[] count = {0};
        double[] total = {0.0};

        db.collection("rates").whereEqualTo("idRestaurant", idRestaurant)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Rating r = document.toObject(Rating.class);
                            count[0] += 1;
                            total[0] += r.getRateGiven();
                        }

                        double average;
                        if (count[0] > 0) {
                            average = total[0] / count[0];
                            db.collection("restaurants").document(idRestaurant)
                                    .update("averageRate", average);
                        }
                    }
                });
    }

    public Restaurant getRestaurantById(String idRestaurant) {
        return Stream.of(allRestaurants)
                .filter(restaurant -> restaurant.getId().equals(idRestaurant))
                .findFirst()
                .orElse(null);
    }

    public void updateCenter(Location location) {
        centerLatitude = location.getLatitude();
        centerLongitude = location.getLongitude();
        getPlacesAPI(centerLatitude, centerLongitude);
    }

    public void getDistancesAPI(List<Restaurant> restaurants) {

        List<Element> elements = new ArrayList<>();
        distancesResult.clear();

        if (MyApplication.getDebug()) {
            try {
                AssetManager am = ctx.getAssets();
                InputStream is = am.open("distances_matrix.json");
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                final Gson gson = new Gson();
                Matrix matrix = gson.fromJson(reader, Matrix.class);
                for (Row r : matrix.getRows()) elements.addAll(r.getElements());
                for (int i = 0; i < restaurants.size(); i++) {
                    try {
                        distancesResult.put(restaurants.get(i).getId(), elements.get(i).getDistance().getValue());
                    } catch (NullPointerException e) {
                        Log.i(TAG, "Erreur : " + restaurants.get(i).getId() + e);
                        distancesResult.put(restaurants.get(i).getId(), 0);
                    } catch (IndexOutOfBoundsException e) {
                        Log.i(TAG, "Erreur getDistancesAPI: " + restaurants.get(i).getId() + e);
                    }
                }
                restaurantsDistancesLiveData.setValue(distancesResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            StringBuilder destinationsURLParameter = new StringBuilder();
            for (Restaurant restaurant : restaurants) {
                if (destinationsURLParameter.length() > 0) destinationsURLParameter.append("|place_id:");
                else destinationsURLParameter.append("place_id:");
                destinationsURLParameter.append(restaurant.getId());
            }

            DistancesAPI service = distancesAPI();
            Call<Matrix> callAsync = service.getResults(centerLatitude + "," + centerLongitude, destinationsURLParameter.toString(),
                                                        "walking", ctx.getString(
                            R.string.google_maps_key22));

            callAsync.enqueue(new Callback<Matrix>() {
                @Override
                public void onResponse(@NonNull Call<Matrix> call, @NonNull Response<Matrix> response) {
                    Matrix matrix = response.body();
                    if (matrix != null) {
                        for (Row r : matrix.getRows()) elements.addAll(r.getElements());
                    }
                    for (int i = 0; i < restaurants.size(); i++) {
                        try {
                            distancesResult.put(restaurants.get(i).getId(), elements.get(i).getDistance().getValue());
                        } catch (NullPointerException e) {
                            Log.i(TAG, "Erreur : " + restaurants.get(i).getId() + e);
                            distancesResult.put(restaurants.get(i).getId(), 0);
                        } catch (IndexOutOfBoundsException e) {
                            Log.i(TAG, "Erreur getDistancesAPI: " + restaurants.get(i).getId() + e);
                        }
                    }
                    restaurantsDistancesLiveData.setValue(distancesResult);
                }

                @Override
                public void onFailure(@NonNull Call<Matrix> call, @NonNull Throwable t) {
                    Log.w(TAG, "Fail distance matrix" + t);
                }
            });
        }
    }
}

