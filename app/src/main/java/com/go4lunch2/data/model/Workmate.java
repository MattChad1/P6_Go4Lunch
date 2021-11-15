package com.go4lunch2.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Workmate {
    @NonNull
    String id;
    @NonNull
    String name;
    @Nullable
    String avatar;
    @Nullable
    String idRestaurantChosen;

    public Workmate(@NonNull String id, @NonNull String name, @Nullable String avatar, String idRestaurantChosen) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.idRestaurantChosen = idRestaurantChosen;
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
}
