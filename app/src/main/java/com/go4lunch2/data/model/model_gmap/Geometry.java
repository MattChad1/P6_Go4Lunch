package com.go4lunch2.data.model.model_gmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Geometry {

    @SerializedName("location")
    @Expose
    public Location location;
    @SerializedName("viewport")
    @Expose
    public Viewport viewport;
}
