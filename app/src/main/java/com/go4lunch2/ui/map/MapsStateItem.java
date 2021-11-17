package com.go4lunch2.ui.map;

public class MapsStateItem {
    String id;
    String name;
    String image;
    Double latitude;
    Double longitude;
    Double starsCount;
    int workmatesCount;

    public MapsStateItem(String id, String name, String image, Double latitude, Double longitude, Double starsCount, int workmatesCount) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.starsCount = starsCount;
        this.workmatesCount = workmatesCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getStarsCount() {
        return starsCount;
    }

    public int getWorkmatesCount() {
        return workmatesCount;
    }
}
