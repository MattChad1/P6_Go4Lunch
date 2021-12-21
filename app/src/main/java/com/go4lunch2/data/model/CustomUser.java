package com.go4lunch2.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class CustomUser {
    @NonNull
    String id;
    @Nullable
    String name;
    @Nullable
    String avatar;
    @Nullable
    String idRestaurantChosen;

    @ServerTimestamp
    Timestamp lastUpdate;

    public CustomUser(@NonNull String id, @NonNull String name, @Nullable String avatar, String idRestaurantChosen, Timestamp lastUpdate) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.idRestaurantChosen = idRestaurantChosen;
        this.lastUpdate = lastUpdate;
    }

    public CustomUser(@NonNull String id) {
        this.id = id;
        this.name = null;
        this.avatar = null;
        this.idRestaurantChosen = null;
    }

    public CustomUser() {
        this.id = "Error";
        this.name = null;
        this.avatar = null;
        this.idRestaurantChosen = null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }

    public String getIdRestaurantChosen() {
        return idRestaurantChosen;
    }

    public void setIdRestaurantChosen(String idRestaurantChosen) {
        this.idRestaurantChosen = idRestaurantChosen;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
}
