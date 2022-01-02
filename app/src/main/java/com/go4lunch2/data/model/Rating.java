package com.go4lunch2.data.model;

public class Rating {
    String idRestaurant;
    String idWorkmate;
    Integer rateGiven;

    public Rating(String idRestaurant, String idWorkmate, Integer rateGiven) {
        this.idRestaurant = idRestaurant;
        this.idWorkmate = idWorkmate;
        this.rateGiven = rateGiven;
    }

    //for Firebase, to avoid Could not deserialize object error
    public Rating() {}

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public String getIdWorkmate() {
        return idWorkmate;
    }

    public Integer getRateGiven() {
        return rateGiven;
    }
}
