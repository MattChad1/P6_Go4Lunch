package com.go4lunch2.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class RestaurantCustomFields {

    String idRestaurant;
    String name;
    Double averageRate;
    List<String> workmatesInterestedIds;

    @ServerTimestamp
    Timestamp lastUpdate;

    public RestaurantCustomFields(String idRestaurant, String name, Double averageRate, List<String> workmatesInterestedIds, Timestamp lastUpdate) {
        this.idRestaurant = idRestaurant;
        this.name = name;
        this.averageRate = averageRate;
        this.workmatesInterestedIds = workmatesInterestedIds;
        this.lastUpdate = lastUpdate;
    }

    public RestaurantCustomFields() {
    }

    public String getName() {
        return name;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public List<String> getWorkmatesInterestedIds() {
        return workmatesInterestedIds;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantCustomFields{" +
                "idRestaurant='" + idRestaurant + '\'' +
                ", name='" + name + '\'' +
                ", averageRate=" + averageRate +
                ", workmatesInterestedIds=" + workmatesInterestedIds +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
