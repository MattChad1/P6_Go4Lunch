package com.go4lunch2.ui.list_restaurants;

import java.util.Comparator;
import java.util.Locale;

public class RestaurantViewState {

    private final String id;
    private final String name;
    private final String adress;
    private final String openingHours;
    private Integer distance;
    private final Double starsCount;
    private final int workmatesCount;
    private final String image;

    public RestaurantViewState(String id, String name, String adress, String openingHours, Integer distance, Double starsCount,
                               int workmatesCount,
                               String image) {
        this.id = id;
        this.name = name;
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

    public String getAdress() {
        return adress;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
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

    public static class NameZAComparator implements Comparator<RestaurantViewState> {
        @Override
        public int compare(RestaurantViewState left, RestaurantViewState right) {
            return right.getName().toLowerCase(Locale.ROOT).compareTo(left.getName().toLowerCase(Locale.ROOT));
        }
    }
}
