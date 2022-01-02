package com.go4lunch2.ui.detail_restaurant;

import com.go4lunch2.data.model.CustomUser;

import java.util.List;

public class DetailRestaurantViewState {

    private final String id;
    private final String name;
    private final String adress;
    private final Double starsCount;
    private final List<CustomUser> workmatesInterested;
    private final String image;
    private final String phone;
    private final String website;

    public DetailRestaurantViewState(String id, String name, String adress, Double starsCount, List<CustomUser> workmatesInterested, String image,
                                     String phone, String website) {
        this.id = id;
        this.name = name;
        this.adress = adress;
        this.starsCount = starsCount;
        this.workmatesInterested = workmatesInterested;
        this.image = image;
        this.phone = phone;
        this.website = website;
    }

    public DetailRestaurantViewState(String id) {
        this.id = id;
        this.name = null;
        this.adress = null;
        this.starsCount = null;
        this.workmatesInterested = null;
        this.image = null;
        this.phone = null;
        this.website = null;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdress() {
        return adress;
    }

    public Double getStarsCount() {
        return starsCount;
    }

    public List<CustomUser> getWorkmatesInterested() {
        return workmatesInterested;
    }

    public String getImage() {
        return image;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }
}
