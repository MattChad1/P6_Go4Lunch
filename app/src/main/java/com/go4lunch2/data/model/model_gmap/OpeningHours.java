package com.go4lunch2.data.model.model_gmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    public Boolean openNow;

    public String getOpenNow() {
        return openNow.toString();
    }
}
