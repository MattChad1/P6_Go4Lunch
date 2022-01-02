package com.go4lunch2.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Restaurant {
    @NonNull
    String id;

    // champs Google place
    @NonNull
    String name;
    @Nullable
    String image;
    @Nullable
    String openingTime;
    @NonNull
    String adress;
    @NonNull
    Double latitude;
    @NonNull
    Double longitude;

    // champs custom (from database)
    @NonNull
    RestaurantCustomFields rcf;

    public Restaurant(@NonNull String id, @NonNull String name, @Nullable String image, @Nullable String openingTime,
                      @NonNull String adress, @NonNull Double latitude, @NonNull Double longitude, @NonNull RestaurantCustomFields rcf) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.openingTime = openingTime;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rcf = rcf;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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
    public String getImage() {
        return image;
    }

    @Nullable
    public String getOpeningTime() {
        return openingTime;
    }

    @NonNull
    public String getAdress() {
        return adress;
    }

    @NonNull
    public Double getLatitude() {
        return latitude;
    }

    @NonNull
    public Double getLongitude() {
        return longitude;
    }

    @NonNull
    public RestaurantCustomFields getRcf() {
        return rcf;
    }

    public void setRcf(@NonNull RestaurantCustomFields rcf) {
        this.rcf = rcf;
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", openingTime='" + openingTime + '\'' +
                ", adress='" + adress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rcf=" + rcf +
                '}';
    }
}
