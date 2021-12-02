package com.go4lunch2.ui.detail_restaurant;

import com.go4lunch2.data.model.Workmate;

import java.util.List;

public class DetailRestaurantViewState {

    String id;
    String name;
    String type;
    String adress;
    String openingHours;
    String distance;
    Double starsCount;
    List<Workmate> workmatesInterested;
    String image;
    String phone;
    String website;

    public DetailRestaurantViewState(String id, String name, String type, String adress, String openingHours, String distance,
                                     Double starsCount, List<Workmate> workmatesInterested, String image, String phone, String website) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.adress = adress;
        this.openingHours = openingHours;
        this.distance = distance;
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

    public String getType() {
        return type;
    }

    public String getAdress() {
        return adress;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getDistance() {
        return distance;
    }

    public Double getStarsCount() {
        return starsCount;
    }

    public List<Workmate> getWorkmatesInterested() {
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
