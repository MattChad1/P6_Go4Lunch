package com.go4lunch2.data.repositories;

import static com.go4lunch2.data.api.APIClient.placesAPI;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.annimon.stream.IntStream;
import com.annimon.stream.OptionalInt;
import com.annimon.stream.Stream;
import com.go4lunch2.DI.DI;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.data.api.PlacesAPI;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantRepository {

    String TAG = "MyLog Repository";

    private final MutableLiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> restaurantsNamesLiveData = new MutableLiveData<>();

    FirebaseFirestore db;
    Context ctx = MyApplication.getInstance();
    List<Restaurant> allRestaurants = new ArrayList<>();
    Map<String, String> names = new HashMap<>();

    public RestaurantRepository() {
        db = DI.getDatabase();
        getPlacesAPI(48.856614, 2.3522219);
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsLiveData() {
        return restaurantsLiveData;
    }

    private void getPlacesAPI(Double latitude, Double longitude) {
        Log.i(TAG, "Appel getPlacesAPI");
        List<Result> results = new ArrayList<>();
        if (MyApplication.getDebug()) {
            try {
                AssetManager am = ctx.getAssets();
                InputStream is = am.open("restaurants_json.json");
                final Gson gson = new Gson();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Place place = gson.fromJson(reader, Place.class);
                results.addAll(place.getResults());
                getCustomFields(results);
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
                    getCustomFields(results);
                }

                @Override
                public void onFailure(Call<Place> call, Throwable t) {
                    Log.i(TAG, "Repository - onFailure: " + t);
                    System.out.println(t);
                }
            });
        }
    }


    private void getCustomFields(List<Result> results) {
        for (Result result : results) {

            String image;
            if (!MyApplication.getDebug()) {
                image =
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=150"
                                + "&photo_reference=" + result.getPhotos().get(0).getPhotoReference()
                                + "&key=" + ctx.getString(R.string.google_maps_key22);
            }
            else image = "";

            DocumentReference docRef = db.collection("restaurants").document(result.getPlaceId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        RestaurantCustomFields rcf;
                        if (document.exists()) {
                            rcf = document.toObject(RestaurantCustomFields.class);
                        }

                        // Si document n'existe pas dans la base, création
                        else {
                            insertNewRestaurantDB(result.getPlaceId(), result.getName());
                            rcf = new RestaurantCustomFields();
                        }

                        Restaurant newRestaurant = new Restaurant(
                                result.getPlaceId(),
                                result.getName(),
                                image, //TODO : image from Places.API?
                                "", //TODO : type from Places.API?
                                result.getOpeningHours() != null ? result.getOpeningHours().getOpenNow() : "",
                                //TODO : voir comment récupérer la chaîne avec les horaires
                                result.getVicinity(),
                                result.getGeometry().location.lat,
                                result.getGeometry().location.getLng(),
                                rcf
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

                        restaurantsLiveData.setValue(allRestaurants);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error", e);
                }
            });
        }
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

                                db.collection("restaurants").document(idRestaurant)
                                        .set(data);
                            }
                        }
                        else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void addGrade(Rating rating) {// Create a new user with a first, middle, and last name

        db.collection("rates")
                .whereEqualTo("idWorkmate", rating.getIdWorkmate())
                .whereEqualTo("idRestaurant", rating.getIdRestaurant())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("rates").document(document.getId()).update("rateGiven", rating.getRateGiven());
                                    updateAverageRate(rating.getIdRestaurant());
                                }
                            }
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

}
