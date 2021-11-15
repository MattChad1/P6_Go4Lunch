package com.go4lunch2.data.model;


public class Rating {
    String idRestaurant;
    String idWorkmate;
    Double rateGiven;

    public Rating(String idRestaurant, String idWorkmate, Double rateGiven) {
        this.idRestaurant = idRestaurant;
        this.idWorkmate = idWorkmate;
        this.rateGiven = rateGiven;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getIdWorkmate() {
        return idWorkmate;
    }

    public void setIdWorkmate(String idWorkmate) {
        this.idWorkmate = idWorkmate;
    }

    public Double getRateGiven() {
        return rateGiven;
    }

    public void setRateGiven(Double rateGiven) {
        this.rateGiven = rateGiven;
    }
}
