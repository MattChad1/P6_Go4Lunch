package com.go4lunch2.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Restaurant implements Parcelable {
    @NonNull long id;
    @NonNull String name;
    @Nullable String image;
    @Nullable String type;
    @NonNull String openingTime;
    @NonNull String adress;
    @NonNull Double latitude;
    @NonNull Double longitude;

    public Restaurant(String name, String image, String type, String openingTime, String adress, Double latitude, Double longitude) {
        this.id = requestMaxId() + 1;
        this.name = name;
        this.image = image;
        this.type = type;
        this.openingTime = openingTime;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int i) {
        p.writeLong (this.id);
        p.writeString(this.name);
        p.writeString(this.image);
        p.writeString(this.type);
        p.writeString(this.openingTime);
        p.writeString(this.adress);
        p.writeDouble(this.latitude);
        p.writeDouble(this.longitude);
    }

    protected Restaurant(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.image = in.readString();
        this.type = in.readString();
        this.openingTime = in.readString();
        this.adress = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };









}
