package com.go4lunch2.ui.list_workmates;

public class WorkmateViewStateItem {

    private final String idWorkmate;
    private final String avatar;
    private final String nameWorkmate;
    private final String idRestaurant;
    private String nameRestaurant;

    public WorkmateViewStateItem(String idWorkmate, String avatar, String nameWorkmate, String idRestaurant, String nameRestaurant) {
        this.idWorkmate = idWorkmate;
        this.avatar = avatar;
        this.nameWorkmate = nameWorkmate;
        this.idRestaurant = idRestaurant;
        this.nameRestaurant = nameRestaurant;
    }

    public String getIdWorkmate() {
        return idWorkmate;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNameWorkmate() {
        return nameWorkmate;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }
}
