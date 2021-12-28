package com.go4lunch2.data.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.annimon.stream.Stream;
import com.go4lunch2.MyApplication;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.model.CustomUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    String TAG = "MyLog UserRepository";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context ctx = MyApplication.getInstance();

    private final MutableLiveData<Map<CustomUser, String>> workmatesWithRestaurantsLiveData = new MutableLiveData<>();
    List<CustomUser> allCustomUsers;
    Map<CustomUser, String> usersWithRestaurant = new HashMap<>();
    MutableLiveData<CustomUser> currentCustomUserLD = new MutableLiveData<>();
    private CollectionReference colRefUsers;

    public UserRepository() {
        Log.i(TAG, "Appel UserRepository()");

        colRefUsers = db.collection("users");
        allCustomUsers = new ArrayList<>();
        getWorkmatesWithRestaurantsLiveData();
    }

    public LiveData<Map<CustomUser, String>> getWorkmatesWithRestaurantsLiveData() {
        Log.i(TAG, "Appel getWorkmatesWithRestaurantsLiveData ");
        usersWithRestaurant.clear();
        colRefUsers
                .whereNotEqualTo("idRestaurantChosen", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CustomUser customUser = document.toObject(CustomUser.class);

                                    db.collection("restaurants")
                                            .document(customUser.getIdRestaurantChosen())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                    DocumentSnapshot document2 = task2.getResult();
                                                    if (task2.isSuccessful()) {
                                                        if (Utils.ValidForToday((Timestamp) document2.get("lastUpdate"))) {
                                                            usersWithRestaurant.put(customUser, document2.getString("name"));
                                                            workmatesWithRestaurantsLiveData.setValue(usersWithRestaurant);
                                                        }
                                                    }
                                                }
                                            });
                            }
                        }
                    }
                });
        return workmatesWithRestaurantsLiveData;
    }

    public void createUser(String id, String name, String avatar) {
        colRefUsers
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                CustomUser newCustomUser = new CustomUser(id, name, avatar, null, null);
                                colRefUsers.document(id)
                                        .set(newCustomUser);
                                allCustomUsers.add(newCustomUser);
                            }
                        }
                        else {
                            Log.d(TAG, "failed to create new user : ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Log.i(TAG, "Erreur add new user: " + e);
                                          }
                                      }
                                     );
    }

    public MutableLiveData<CustomUser> getCurrentCustomUserLD(String id) {
        getUserCustomFromFB(id);
        return currentCustomUserLD;
    }


    public void getUserCustomFromFB(String id) {
        colRefUsers
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            currentCustomUserLD.setValue(snapshot.toObject(CustomUser.class));
                        } else {
                            currentCustomUserLD.setValue(new CustomUser(id));
                        }
                    }
                });
    }

    public void updateRestaurantChosen(String idUser, String idRestaurant) {
        // If there was a restaurant, we update the restaurants table to remove this user from the list of workmates interested
        DocumentReference docRef = colRefUsers.document(idUser);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        String oldRestaurant = document.getString("idRestaurantChosen");
                        if (oldRestaurant!= null) {
                            db.collection("restaurants").document(oldRestaurant)
                                    .update("workmatesInterestedIds", FieldValue.arrayRemove(idUser));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //  update table users

            docRef.update("idRestaurantChosen", idRestaurant)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            docRef.update("lastUpdate", FieldValue.serverTimestamp());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating Restaurant chosen", e);
                        }
                    });

            // update document of the restaurant
            //  idRestaurant== null when the notification is sent. In this case, there is nothing to do here
        if (idRestaurant != null) {
            DocumentReference docRef2 = db.collection("restaurants").document(idRestaurant);
            docRef2.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (Utils.ValidForToday((Timestamp) documentSnapshot.get("lastUpdate"))) {
                                docRef2.update("workmatesInterestedIds", FieldValue.arrayUnion(idUser));
                            }
                            else {
                                docRef2.update("workmatesInterestedIds", new ArrayList<String>(Arrays.asList(idUser)));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

            db.collection("restaurants").document(idRestaurant)
                    .update("lastUpdate", FieldValue.serverTimestamp());
        }
    }

    public List<CustomUser> getListWorkmatesByIds(List<String> ids) {
        if (ids == null) return null;
        else {
            List<CustomUser> customUsers = new ArrayList<>();
            for (String id : ids) {
                Map.Entry entry = Stream.of(usersWithRestaurant)
                        .filter(u -> u.getKey().getId().equals(id))
                        .findFirst()
                        .orElse(null);
                if (entry!=null) customUsers.add((CustomUser) entry.getKey());
            }
            return customUsers;
        }
    }
}
