package com.go4lunch2.data;

import android.util.Log;

import androidx.annotation.Nullable;

import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.RestaurantCustomFields;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class DatabaseInteractor {
    FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference userData;
    private CollectionReference colRefRestaurants;


    public DatabaseInteractor(FirebaseFirestore firestore) {
        db = firestore;
        colRefRestaurants = db.collection("restaurants");
//        theUser = new User();
    }





    // ... various methods that add/retrieve Users from FireStore ... //
}