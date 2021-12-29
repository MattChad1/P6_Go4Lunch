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

    // champs custom
    @NonNull
    RestaurantCustomFields rcf;

    // champs contacts
    @NonNull
    RestaurantDetails restaurantDetails;

    public Restaurant(@NonNull String id, @NonNull String name, @Nullable String image, @Nullable String openingTime,
                      @NonNull String adress, @NonNull Double latitude, @NonNull Double longitude, @NonNull RestaurantCustomFields rcf,
                      @NonNull RestaurantDetails restaurantDetails) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.openingTime = openingTime;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rcf = rcf;
        this.restaurantDetails = restaurantDetails;
    }

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
        this.restaurantDetails = new RestaurantDetails();
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

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    @NonNull
    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(@NonNull String openingTime) {
        this.openingTime = openingTime;
    }

    @NonNull
    public String getAdress() {
        return adress;
    }

    public void setAdress(@NonNull String adress) {
        this.adress = adress;
    }

    @NonNull
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NonNull Double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NonNull Double longitude) {
        this.longitude = longitude;
    }

    @NonNull
    public RestaurantCustomFields getRcf() {
        return rcf;
    }

    public void setRcf(@NonNull RestaurantCustomFields rcf) {
        this.rcf = rcf;
    }

    @NonNull
    public RestaurantDetails getRestaurantDetails() {
        return restaurantDetails;
    }

    public void setRestaurantDetails(@NonNull RestaurantDetails restaurantDetails) {
        this.restaurantDetails = restaurantDetails;
    }

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
