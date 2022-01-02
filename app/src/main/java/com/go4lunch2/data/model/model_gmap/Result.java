package com.go4lunch2.data.model.model_gmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Result {

    @SerializedName("business_status")
    @Expose
    public String businessStatus;
    @SerializedName("geometry")
    @Expose
    public Geometry geometry;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("icon_background_color")
    @Expose
    public String iconBackgroundColor;
    @SerializedName("icon_mask_base_uri")
    @Expose
    public String iconMaskBaseUri;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("opening_hours")
    @Expose
    public OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    public final List<Photo> photos = null;
    @SerializedName("place_id")
    @Expose
    public String placeId;
    @SerializedName("plus_code")
    @Expose
    public PlusCode plusCode;
    @SerializedName("rating")
    @Expose
    public Double rating;
    @SerializedName("reference")
    @Expose
    public String reference;
    @SerializedName("scope")
    @Expose
    public String scope;
    @SerializedName("types")
    @Expose
    public final List<String> types = null;
    @SerializedName("user_ratings_total")
    @Expose
    public Integer userRatingsTotal;
    @SerializedName("vicinity")
    @Expose
    public String vicinity;
    @SerializedName("permanently_closed")
    @Expose
    public Boolean permanentlyClosed;
    @SerializedName("price_level")
    @Expose
    public Integer priceLevel;

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getVicinity() {
        return vicinity;
    }

    @Override
    public String toString() {
        return "Result{" +
                "businessStatus='" + businessStatus + '\'' +
                ", geometry=" + geometry +
                ", icon='" + icon + '\'' +
                ", iconBackgroundColor='" + iconBackgroundColor + '\'' +
                ", iconMaskBaseUri='" + iconMaskBaseUri + '\'' +
                ", name='" + name + '\'' +
                ", openingHours=" + openingHours +
                ", photos=" + photos +
                ", placeId='" + placeId + '\'' +
                ", plusCode=" + plusCode +
                ", rating=" + rating +
                ", reference='" + reference + '\'' +
                ", scope='" + scope + '\'' +
                ", types=" + types +
                ", userRatingsTotal=" + userRatingsTotal +
                ", vicinity='" + vicinity + '\'' +
                ", permanentlyClosed=" + permanentlyClosed +
                ", priceLevel=" + priceLevel +
                '}';
    }
}
