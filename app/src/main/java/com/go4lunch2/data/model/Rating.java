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
    public Rating (){}

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

    public Integer getRateGiven() {
        return rateGiven;
    }

    public void setRateGiven(Integer rateGiven) {
        this.rateGiven = rateGiven;
    }
}
