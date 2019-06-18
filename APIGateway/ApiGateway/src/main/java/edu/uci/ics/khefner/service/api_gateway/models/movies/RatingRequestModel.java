package edu.uci.ics.khefner.service.api_gateway.models.movies;

import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class RatingRequestModel extends RequestModel {
    private String id;
    private Float rating;

    public RatingRequestModel() {
    }

    public RatingRequestModel(String id, Float rating) {
        this.id = id;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

}
