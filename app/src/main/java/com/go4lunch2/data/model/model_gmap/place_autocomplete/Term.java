package com.go4lunch2.data.model.model_gmap.place_autocomplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Term {

    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("value")
    @Expose
    private String value;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
