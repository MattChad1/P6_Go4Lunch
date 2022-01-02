package com.go4lunch2.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Objects;

public class CustomUser {
    @NonNull
    String id;
    @Nullable
    String name;
    @Nullable
    final
    String avatar;
    @Nullable
    final
    String idRestaurantChosen;

    @Nullable
    final
    String nameRestaurantChosen;

    @ServerTimestamp
    Timestamp lastUpdate;

    public CustomUser(@NonNull String id, @NonNull String name, @Nullable String avatar, @Nullable String idRestaurantChosen, @Nullable String nameRestaurantChosen, Timestamp lastUpdate) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.idRestaurantChosen = idRestaurantChosen;
        this.nameRestaurantChosen = nameRestaurantChosen;
        this.lastUpdate = lastUpdate;
    }

    public CustomUser(@NonNull String id) {
        this.id = id;
        this.name = null;
        this.avatar = null;
        this.idRestaurantChosen = null;
        this.nameRestaurantChosen = null;
    }

    //for Firebase, to avoid Could not deserialize object error
    public CustomUser() {
        this.id = "Error";
        this.name = null;
        this.avatar = null;
        this.idRestaurantChosen = null;
        this.nameRestaurantChosen = null;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Nullable
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

    @Nullable
    public String getIdRestaurantChosen() {
        return idRestaurantChosen;
    }

    @Nullable
    public String getNameRestaurantChosen() {
        return nameRestaurantChosen;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUser that = (CustomUser) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
