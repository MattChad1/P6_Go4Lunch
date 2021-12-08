package com.go4lunch2.data.model;

import java.util.List;

public class RestaurantCustomFields {

    String idRestaurant;
    String name;
    Double averageRate;
    List<String> workmatesInterestedIds;

    public RestaurantCustomFields(String idRestaurant, String name, Double averageRate, List<String> workmatesInterestedIds) {
        this.idRestaurant = idRestaurant;
        this.name = name;
        this.averageRate = averageRate;
        this.workmatesInterestedIds = workmatesInterestedIds;
    }

    public RestaurantCustomFields() {
    }

    public String getIdRestaurant() {
        return idRestaurant;
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

    @Override
    public String toString() {
        return "RestaurantCustomFields{" +
                "idRestaurant='" + idRestaurant + '\'' +
                ", name='" + name + '\'' +
                ", averageRate=" + averageRate +
                ", workmatesInterestedIds=" + workmatesInterestedIds +
                '}';
    }
}
