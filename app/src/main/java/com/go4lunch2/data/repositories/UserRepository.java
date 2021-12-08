package com.go4lunch2.data.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch2.MyApplication;
import com.go4lunch2.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    String TAG = "MyLog UserRepository";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context ctx = MyApplication.getInstance();

    private final MutableLiveData<Map<User, String>> workmatesWithRestaurantsLiveData = new MutableLiveData<>();
    List<User> allUsers;
    Map<User, String> usersWithRestaurant = new HashMap<>();

    public UserRepository() {
        Log.i(TAG, "Appel UserRepository()");
        allUsers = new ArrayList<>();
    }

    public LiveData<Map<User, String>> getWorkmatesWithRestaurantsLiveData() {

        Log.i(TAG, "Appel getWorkmatesWithRestaurantsLiveData ");
        usersWithRestaurant.clear();
        db.collection("users")
                .whereNotEqualTo("idRestaurantChosen", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);

                                    db.collection("restaurants")
                                            .document(user.getIdRestaurantChosen())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                    DocumentSnapshot document2 = task2.getResult();
                                                    if (task2.isSuccessful()) {
                                                            usersWithRestaurant.put(user, document2.getString("name"));
                                                            workmatesWithRestaurantsLiveData.setValue(usersWithRestaurant);
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
        db.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                User newUser = new User(id, name, avatar, null);
                                db.collection("users").document(id)
                                        .set(newUser);
                                allUsers.add(newUser);
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

    public void updateRestaurantChosen(String idUser, String idRestaurant) {
        db.collection("users").document(idUser)
                .update("idRestaurantChosen", idRestaurant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        ;

        db.collection("restaurants").document(idRestaurant)
                .update("workmatesInterestedIds", FieldValue.arrayUnion(idUser))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        ;
    }

    public List<User> getListWorkmatesByIds(List<String> ids) {
        if (ids == null) return null;
        else {
            List<User> users = new ArrayList<>();
            for (String id : ids) {
                db.collection("users")
                        .document(id)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                users.add(document.toObject(User.class));
                            }
                        });
            }
            return users;
        }
    }
}
