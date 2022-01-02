package com.go4lunch2.data.model.model_gmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Place {

    @SerializedName("html_attributions")
    @Expose
    public final List<Object> htmlAttributions = null;
    @SerializedName("next_page_token")
    @Expose
    public String nextPageToken;
    @SerializedName("results")
    @Expose
    public final List<Result> results = null;
    @SerializedName("status")
    @Expose
    public String status;

    public List<Result> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "Place{" +
                "htmlAttributions=" + htmlAttributions +
                ", nextPageToken='" + nextPageToken + '\'' +
                ", results=" + results.toString() +
                ", status='" + status + '\'' +
                '}';
    }
}
