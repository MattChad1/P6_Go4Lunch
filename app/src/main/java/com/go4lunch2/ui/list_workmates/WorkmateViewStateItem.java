package com.go4lunch2.ui.list_workmates;

public class WorkmateViewStateItem {

    String idWorkmate;
    String avatar;
    String nameWorkmate;
    String typeFood;
    String nameRestaurant;

    public WorkmateViewStateItem(String idWorkmate, String avatar, String nameWorkmate, String typeFood, String nameRestaurant) {
        this.idWorkmate = idWorkmate;
        this.avatar = avatar;
        this.nameWorkmate = nameWorkmate;
        this.typeFood = typeFood;
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

    public String getTypeFood() {
        return typeFood;
    }

    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }
}
