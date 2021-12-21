package com.go4lunch2.ui.list_restaurants;


public class RestaurantViewState {

    String id;
    String name;
    String type;
    String adress;
    String openingHours;
    Integer distance;
    Double starsCount;
    int workmatesCount;
    String image;

    public RestaurantViewState(String id, String name, String type, String adress, String openingHours, Integer distance, Double starsCount,
                               int workmatesCount,
                               String image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.adress = adress;
        this.openingHours = openingHours;
        this.distance = distance;
        this.starsCount = starsCount;
        this.workmatesCount = workmatesCount;
        this.image = image;
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

    public Integer getDistance() {
        return distance;
    }

    public Double getStarsCount() {
        return starsCount;
    }

    public int getWorkmatesCount() {
        return workmatesCount;
    }

    public String getImage() {
        return image;
    }
}
