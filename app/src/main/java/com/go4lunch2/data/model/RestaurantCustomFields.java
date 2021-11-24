package com.go4lunch2.data.model;

import java.util.List;

public class RestaurantCustomFields {

    String idRestaurant;
    Double averageRate;
    List<String> workmatesInterestedIds;

    public RestaurantCustomFields(String idRestaurant, Double averageRate, List<String> workmatesInterestedIds) {
        this.idRestaurant = idRestaurant;
        this.averageRate = averageRate;
        this.workmatesInterestedIds = workmatesInterestedIds;
    }

    public RestaurantCustomFields() {
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public List<String> getWorkmatesInterestedIds() {
        return workmatesInterestedIds;
    }

    @Override
    public String toString() {
        return "RestaurantCustomFields{" +
                "idRestaurant='" + idRestaurant + '\'' +
                ", averageRate=" + averageRate +
                ", workmatesInterestedIds=" + workmatesInterestedIds +
                '}';
    }
}
