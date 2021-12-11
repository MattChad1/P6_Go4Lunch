package com.go4lunch2.ui.detail_restaurant;

import com.go4lunch2.data.model.CustomUser;

import java.util.List;

public class DetailRestaurantViewState {

    String id;
    String name;
    String adress;
    Double starsCount;
    List<CustomUser> workmatesInterested;
    String image;
    String phone;
    String website;

    public DetailRestaurantViewState(String id, String name, String adress, Double starsCount, List<CustomUser> workmatesInterested, String image, String phone, String website) {
        this.id = id;
        this.name = name;
        this.adress = adress;
        this.starsCount = starsCount;
        this.workmatesInterested = workmatesInterested;
        this.image = image;
        this.phone = phone;
        this.website = website;
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

    public void setStarsCount(Double starsCount) {
        this.starsCount = starsCount;
    }

    public void setWorkmatesInterested(List<CustomUser> workmatesInterested) {
        this.workmatesInterested = workmatesInterested;
    }
}
