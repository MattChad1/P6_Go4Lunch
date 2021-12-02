package com.go4lunch2.data.model;

public class RestaurantDetails {

    String phone;
    String website;

    public RestaurantDetails(String phone, String website) {
        this.phone = phone;
        this.website = website;
    }

    public RestaurantDetails() {
        this.phone = null;
        this.website = null;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }
}
