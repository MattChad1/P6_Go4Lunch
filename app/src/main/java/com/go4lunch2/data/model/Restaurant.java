package com.go4lunch2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    @NonNull String id;

    // champs Google place
    @NonNull String name;
    @Nullable String image;
    @Nullable String type;
    @Nullable String openingTime;
    @NonNull String adress;
    @NonNull Double latitude;
    @NonNull Double longitude;

    // champs custom
    @NonNull RestaurantCustomFields rcf;

    public Restaurant(@NonNull String id, @NonNull String name, @Nullable String image, @Nullable String type, @Nullable String openingTime,
                      @NonNull String adress, @NonNull Double latitude, @NonNull Double longitude, @NonNull RestaurantCustomFields rcf) {


        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
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

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", openingTime='" + openingTime + '\'' +
                ", adress='" + adress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rcf=" + rcf +
                '}';
    }
}
