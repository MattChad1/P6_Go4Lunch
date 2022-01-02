package com.go4lunch2.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.annimon.stream.Stream;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.model.CustomUser;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepository {

    private final MutableLiveData<List<CustomUser>> workmatesWithRestaurantsLiveData = new MutableLiveData<>();
    private final CollectionReference colRefUsers;
    private final String TAG = "MyLog UserRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<CustomUser> usersWithRestaurant = new ArrayList<>();
    private final MutableLiveData<CustomUser> currentCustomUserLD = new MutableLiveData<>();

    public UserRepository() {
        Log.i(TAG, "Appel UserRepository()");
        colRefUsers = db.collection("users");
        getWorkmatesWithRestaurants();
    }

    public LiveData<List<CustomUser>> getWorkmatesWithRestaurantsLiveData() {
        getWorkmatesWithRestaurants();
        return workmatesWithRestaurantsLiveData;
    }

    // Get the list of workmates who have selected a restaurant and listen to changes
    public void getWorkmatesWithRestaurants() {
        Log.i(TAG, "Appel getWorkmatesWithRestaurantsLiveData ");
        colRefUsers
                .whereNotEqualTo("idRestaurantChosen", null)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            CustomUser customUser = doc.toObject(CustomUser.class);
                            assert customUser.getIdRestaurantChosen() != null;
                            if (Utils.ValidForToday(customUser.getLastUpdate())) {
                                usersWithRestaurant.remove(customUser);
                                usersWithRestaurant.add(customUser);
                                workmatesWithRestaurantsLiveData.setValue(usersWithRestaurant);
//                                db.collection("restaurants")
//                                        .document(customUser.getIdRestaurantChosen())
//                                        .get()
//                                        .addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                Log.i(TAG, "getWorkmatesWithRestaurants: task successful");
//                                                DocumentSnapshot document = task.getResult();
//                                                if (document.exists()) {
//                                                    Log.i(TAG, "getWorkmatesWithRestaurants: user add");
//                                                    usersWithRestaurant.put(customUser, document.getString("name"));
//                                                    workmatesWithRestaurantsLiveData.setValue(usersWithRestaurant);
//                                                }
//                                            }
//                                        })
//                                        .addOnFailureListener(err -> Log.w(TAG, "Error searching document", err));

                            }
                        }
                    }
                });
    }

    public void createUser(String id, String name, String avatar) {
        colRefUsers
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            CustomUser newCustomUser = new CustomUser(id, name, avatar, null, null, null);
                            colRefUsers.document(id)
                                    .set(newCustomUser);
                        }
                    }
                    else {
                        Log.d(TAG, "failed to create new user : ", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.i(TAG, "Erreur add new user: " + e));
    }

    public MutableLiveData<CustomUser> getCurrentCustomUserLD(String id) {
        getUserCustomFromFB(id);
        return currentCustomUserLD;
    }

    public void getUserCustomFromFB(String id) {
        colRefUsers
                .document(id)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        currentCustomUserLD.setValue(snapshot.toObject(CustomUser.class));
                    }
                    else {
                        currentCustomUserLD.setValue(new CustomUser(id));
                    }
                });
    }

    public void updateRestaurantChosen(String idUser, String idRestaurant, String nameRestaurant) {
        // If there was a restaurant, we update the restaurants table to remove this user from the list of workmates interested

        DocumentReference docRef = colRefUsers.document(idUser);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String oldRestaurant = document.getString("idRestaurantChosen");
                if (oldRestaurant != null) {
                    db.collection("restaurants").document(oldRestaurant)
                            .update("workmatesInterestedIds", FieldValue.arrayRemove(idUser));
                }
            }
            else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        //  update table users
        docRef.update("idRestaurantChosen", idRestaurant, "nameRestaurantChosen", nameRestaurant)
                .addOnSuccessListener(aVoid -> docRef.update("lastUpdate", FieldValue.serverTimestamp()))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating Restaurant chosen", e));

        // update document of the restaurant
        //  idRestaurant== null when the notification is sent. In this case, there is nothing to do here
        if (idRestaurant != null) {
            DocumentReference docRef2 = db.collection("restaurants").document(idRestaurant);
            docRef2.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (Utils.ValidForToday((Timestamp) documentSnapshot.get("lastUpdate"))) {
                            docRef2.update("workmatesInterestedIds", FieldValue.arrayUnion(idUser), "lastUpdate", FieldValue.serverTimestamp());
                        }
                        else {
                            docRef2.update("workmatesInterestedIds", new ArrayList<>(Collections.singletonList(idUser)), "lastUpdate", FieldValue.serverTimestamp());
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
        }
    }

    public List<CustomUser> getListWorkmatesByIds(List<String> ids) {
        if (ids == null) return null;
        else {
            List<CustomUser> customUsers = new ArrayList<>();
            for (String id : ids) {
                CustomUser cu = Stream.of(usersWithRestaurant)
                        .filter(u -> u.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                if (cu != null) customUsers.add(cu);
            }
            return customUsers;
        }
    }
}
