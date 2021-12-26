package com.go4lunch2.data.repositories;

import static com.go4lunch2.DI.DI.getReader;
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
import com.go4lunch2.BuildConfig;
import com.go4lunch2.DI.DI;
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
import com.go4lunch2.data.model.model_gmap.restaurant_details.ResultDetails;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

    String TAG = "MyLog Repository";

    private final MutableLiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResultDetails> restaurantDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> restaurantsDistancesLiveData = new MutableLiveData<>();

    FirebaseFirestore db;
    Context ctx = MyApplication.getInstance();
    List<Restaurant> allRestaurants = new ArrayList<>();
    Double centerLatitude;
    Double centerLongitude;
    CollectionReference colRefRestaurants;
    Map<String, Integer> distancesResult = new HashMap<>();

    public RestaurantRepository() {
        db = DI.getDatabase();
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




    private void getPlacesAPI(Double latitude, Double longitude) {
        Log.i(TAG, "Appel getPlacesAPI");
        allRestaurants.clear();
        List<Result> results = new ArrayList<>();
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
                public void onResponse(Call<Place> call, Response<Place> response) {

                    results.addAll(response.body().getResults());
                    updateRepoPlaces(results);
                }

                @Override
                public void onFailure(Call<Place> call, Throwable t) {
                    Log.i(TAG, "Repository - onFailure: " + t);
                }
            });
        }

    }


    public void updateRepoPlaces (List<Result> results) {
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
            else image = "";
            String finalImage = image;
            Restaurant newRestaurant = new Restaurant(
                    result.getPlaceId(),
                    result.getName(),
                    finalImage,
                    "", //TODO : type from Places.API?
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



    private void getCustomFields(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            final DocumentReference docRef = colRefRestaurants.document(restaurant.getId());

            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                RestaurantCustomFields rcf;
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed in getCustomFields().", e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            rcf = snapshot.toObject(RestaurantCustomFields.class);
                            allRestaurants.get(allRestaurants.indexOf(restaurant)).setRcf(rcf);
                        } else {
                            rcf = new RestaurantCustomFields();
                            insertNewRestaurantDB(restaurant.getId(), restaurant.getName());
                        }

                }
            });
        }
        restaurantsLiveData.setValue(allRestaurants);
    }



    public void getDetailsFromAPI(String idRestaurant) {
        PlaceDetailsAPI service = placeDetailsAPI();
        Call<RestaurantDetailsJson> callAsync = service.getResults(idRestaurant, ctx.getString(R.string.google_maps_key22));

        callAsync.enqueue(new Callback<RestaurantDetailsJson>() {
                              @Override
                              public void onResponse(Call<RestaurantDetailsJson> call, Response<RestaurantDetailsJson> response) {
                                  ResultDetails resultDetails = response.body().getResult();
                                  restaurantDetailsLiveData.setValue(resultDetails);
                              }

                              @Override
                              public void onFailure(Call<RestaurantDetailsJson> call, Throwable t) {

                              }
                          });
    }

            // Insert un nouveau restaurant dans la table "custom fields" si son id n'existe pas
    private void insertNewRestaurantDB(String idRestaurant, String name) {
        db.collection("restaurants")
                .document(idRestaurant)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                    }
                });
    }

    public void addGrade(Rating rating) {
        // Check if user has already given a rate
        db.collection("rates")
                .whereEqualTo("idWorkmate", rating.getIdWorkmate())
                .whereEqualTo("idRestaurant", rating.getIdRestaurant())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                updateAverageRate(rating.getIdRestaurant());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        }
                        else Log.w(TAG, "FAIL  addGrade()");
                    }
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

                        double average = 0.0;
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

        if (BuildConfig.DEBUG) {
            try {
                final BufferedReader reader = getReader(ctx, "distances_matrix.json");
                final Gson gson = new Gson();
                Matrix matrix = gson.fromJson(reader, Matrix.class);
                for (Row r : matrix.getRows()) elements.addAll(r.getElements());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            String destinationsURLParameter = "";
            for (Restaurant restaurant : restaurants) {
                if (!destinationsURLParameter.isEmpty()) destinationsURLParameter += "|place_id:";
                else destinationsURLParameter += "place_id:";
                destinationsURLParameter += restaurant.getId();
            }

            DistancesAPI service = distancesAPI();
            Call<Matrix> callAsync = service.getResults(centerLatitude + "," + centerLongitude, destinationsURLParameter,
                                                        "walking", ctx.getString(
                            R.string.google_maps_key22));

            callAsync.enqueue(new Callback<Matrix>() {
                @Override
                public void onResponse(Call<Matrix> call, Response<Matrix> response) {
                    Matrix matrix = response.body();
                    for (Row r : matrix.getRows()) elements.addAll(r.getElements());
                }

                @Override
                public void onFailure(Call<Matrix> call, Throwable t) {
                    Log.i("Test retrofit", "onFailure: " + t);
                    System.out.println(t);
                }
            });
        }
        for (int i = 0; i < restaurants.size(); i++) {
            try {
                distancesResult.put(restaurants.get(i).getId(), ((Element) elements.get(i)).getDistance().getValue());
            } catch (NullPointerException e) {
                Log.i(TAG, "Erreur : " + restaurants.get(i).getId());
                distancesResult.put(restaurants.get(i).getId(), 0);
            } catch (IndexOutOfBoundsException e) {
                Log.i(TAG, "Erreur getDistancesAPI: " + restaurants.get(i).getId());
            }
        }

        restaurantsDistancesLiveData.setValue(distancesResult);
    }

}

