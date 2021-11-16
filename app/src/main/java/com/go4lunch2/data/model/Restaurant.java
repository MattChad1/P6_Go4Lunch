package com.go4lunch2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class Restaurant implements Parcelable {
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
    @Nullable Double ratingAverage;
    @Nullable List<Workmate> workmatesInterested;

    public Restaurant(@NonNull String id, @NonNull String name, @Nullable String image, @Nullable String type, @Nullable String openingTime,
                      @NonNull String adress, @NonNull Double latitude, @NonNull Double longitude, @Nullable Double ratingAverage,
                      @Nullable List<Workmate> workmatesInterested) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
        this.openingTime = openingTime;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ratingAverage = ratingAverage;
        this.workmatesInterested = workmatesInterested;
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

    @Nullable
    public Double getRatingAverage() {
        return ratingAverage;
    }

    @Nullable
    public List<Workmate> getWorkmatesInterested() {
        return workmatesInterested;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int i) {
        p.writeString (this.id);
        p.writeString(this.name);
        p.writeString(this.image);
        p.writeString(this.type);
        p.writeString(this.openingTime);
        p.writeString(this.adress);
        p.writeDouble(this.latitude);
        p.writeDouble(this.longitude);
    }

    protected Restaurant(Parcel in) {
        this.id = in.readString();
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
                ", ratingAverage=" + ratingAverage +
                ", workmatesInterested=" + workmatesInterested +
                '}';
    }
}
