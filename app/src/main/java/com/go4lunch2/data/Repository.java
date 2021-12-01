package com.go4lunch2.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.annimon.stream.IntStream;
import com.annimon.stream.OptionalInt;
import com.annimon.stream.Stream;
import com.go4lunch2.BuildConfig;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.go4lunch2.data.model.Workmate;
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
import java.util.Arrays;
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

public class Repository {

    String TAG = "MyLog Repository";

    private final MutableLiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Workmate>> workmatesLiveData = new MutableLiveData<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context ctx = MyApplication.getInstance();
    List<Restaurant> allRestaurants = new ArrayList<>();
    //TODO : change for Firebase
    List<Workmate> allWorkmates = FAKE_LIST_WORKMATES;

    public Repository() {
        getPlacesAPI(48.856614, 2.3522219);
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsLiveData() {

        List<Restaurant> li = restaurantsLiveData.getValue();
        int num = 0;
        if (li!=null) num = li.size();
        Log.i(TAG, "getRestaurantsLiveData: " + num);
        return restaurantsLiveData;
    }




    private void getPlacesAPI(Double latitude, Double longitude) {
        Log.i(TAG, "Appel getPlacesAPI");
        List<Result> results = new ArrayList<>();
        if (BuildConfig.DEBUG) {
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


            PlacesAPI service = retrofit.create(PlacesAPI.class);
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












    private void getCustomFields (List<Result> results) {
        for (Result result : results) {

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
                            rcf = new RestaurantCustomFields();
                            Map<String, Object> data = new HashMap<>();
                            data.put("idRestaurant", result.getPlaceId());
                            data.put("averageRate", null);
                            data.put("workmatesInterestedIds", new ArrayList<String>());
                            db.collection("restaurants").document(result.getPlaceId())
                                    .set(data);
                        }

                        Restaurant newRestaurant = new Restaurant(
                                result.getPlaceId(),
                                result.getName(),
                                "", //TODO : image from Places.API?
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
                            Log.i(TAG, "getCustomFields: " + newRestaurant.getName() + "/" + num.getAsInt());
                        }
                        else {
                            allRestaurants.add(newRestaurant);
                            Log.i(TAG, "getCustomFields: " + newRestaurant.getName() + "/ new");
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
 private void insertNewRestaurantDB (String idRestaurant) {
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
                                    Log.d(TAG, document.getId() + " => " + document.getData());
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

    public MutableLiveData<List<Workmate>> getWorkmatesLiveData() {
        workmatesLiveData.setValue(allWorkmates);
        return workmatesLiveData;
    }

    public Restaurant getRestaurantById(String idRestaurant) {
        return Stream.of(allRestaurants)
                .filter(restaurant -> restaurant.getId().equals(idRestaurant))
                .findFirst()
                .get();
    }

    public List<Workmate> getListWorkmatesByIds(List<String> ids) {
        if (ids == null) return null;
        else return Stream.of(allWorkmates)
                .filter(workmate -> ids.contains(workmate.getId()))
                .toList();
    }



    static public List<Workmate> FAKE_LIST_WORKMATES = new ArrayList<>(Arrays.asList(
            new Workmate("w1", "Bob", "a1.png", "ChIJ8znTVS5u5kcREq8TmzOICFs"),
            new Workmate("w2", "Léa", "a1.png", "ChIJ_Ze3ZjBu5kcRiCRPWLatnSg"),
            new Workmate("w3", "Joe", "a1.png", "ChIJ8znTVS5u5kcREq8TmzOICFs"),
            new Workmate("w4", "Yasmine", "a1.png", null),
            new Workmate("w5", "Pierre-Jean", "a1.png", "ChIJ8znTVS5u5kcREq8TmzOICFs"))
    );

//    static public List<Rating> FAKE_RATES = new ArrayList<>(Arrays.asList(
//            new Rating("r1", "w1", 1),
//            new Rating("r2", "w1", 2),
//            new Rating("r1", "w2", 3),
//            new Rating("r2", "w3", 2)
//                                                                         )
//    );
//
//    static public List<Restaurant> FAKE_LIST_RESTAURANTS = new ArrayList<>(Arrays.asList(
//            new Restaurant("r1", "Chez Lulu", "r1.png", "Français", "Open until 7 pm",
//                           "8 rue du général Bol", 45.12, 2.0, 2.0, Collections.emptyList()),
//            new Restaurant("r2", "Rajpoot", "r2.png", "Indien", "Open 24h/7", "175 avenue des Perdrix", 45.23, 2.12, 2.0, Collections.emptyList()))
//    );
}
