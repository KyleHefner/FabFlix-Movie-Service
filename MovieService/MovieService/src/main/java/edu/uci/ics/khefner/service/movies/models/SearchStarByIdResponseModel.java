package edu.uci.ics.khefner.service.movies.models;

import edu.uci.ics.khefner.service.movies.core.Star;

public class SearchStarByIdResponseModel {

    private int resultCode;
    private String message;
    private Star star;

    public SearchStarByIdResponseModel(int resultCode, String message, Star star) {
        this.resultCode = resultCode;
        this.message = message;
        this.star = star;
    }

    public SearchStarByIdResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }
}
