package com.go4lunch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Repository {

    static public List<Workmate> FAKE_LIST_WORKMATES = new ArrayList<>(Arrays.asList(
            new Workmate("Bob", "a1.png", 2),
            new Workmate("Léa", "a1.png", 0),
            new Workmate("Joe", "a1.png", 2),
            new Workmate("Yasmine", "a1.png", 2),
            new Workmate("Pierre-Jean", "a1.png", 0))
    );

    static public List<Restaurant> FAKE_LIST_RESTAURANTS = new ArrayList<>(Arrays.asList(
            new Restaurant("Chez Lulu", "r1.png", "Français", "Open until 7 pm",
                           "8 rue du général Bol", 45.12, 2.0),
            new Restaurant("Rajpoot", "r2.png", "Indien", "Open 24h/7", "175 avenue des Perdrix", 45.23, 2.12))
    );
}
