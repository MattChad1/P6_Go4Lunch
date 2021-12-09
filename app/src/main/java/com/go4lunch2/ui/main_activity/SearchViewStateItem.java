package com.go4lunch2.ui.main_activity;



public class SearchViewStateItem {
    String id;
    String name;
    String adress;

    public SearchViewStateItem(String id, String name, String adress) {
        this.id = id;
        this.name = name;
        this.adress = adress;
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
}
