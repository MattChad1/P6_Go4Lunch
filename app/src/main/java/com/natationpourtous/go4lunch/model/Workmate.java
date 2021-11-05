package com.natationpourtous.go4lunch.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Workmate {
    @NonNull
    long id;
    @NonNull
    String name;
    @Nullable
    String avatar;
    long idRestaurantChosen;

    public Workmate(@NonNull String name, @Nullable String avatar, long idRestaurantChosen) {
        this.id = requestMaxId();
        this.name = name;
        this.avatar = avatar;
        this.idRestaurantChosen = idRestaurantChosen;
    }

    public long requestMaxId() {
        return 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getIdRestaurantChosen() {
        return idRestaurantChosen;
    }

    public void setIdRestaurantChosen(long idRestaurantChosen) {
        this.idRestaurantChosen = idRestaurantChosen;
    }
}
