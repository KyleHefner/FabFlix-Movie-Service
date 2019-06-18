package edu.uci.ics.khefner.service.movies.models.SearchStar;

import edu.uci.ics.khefner.service.movies.core.Star;

import java.util.ArrayList;
import java.util.List;

public class SearchStarResponseModel {

    private int resultCode;
    private String message;
    private List<Star> stars;

    public SearchStarResponseModel(int resultCode, String message, List<Star> stars) {
        this.resultCode = resultCode;
        this.message = message;
        this.stars = new ArrayList<>(stars);
    }

    public SearchStarResponseModel(int resultCode, String message) {
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

    public List<Star> getStars() {
        return stars;
    }

    public void setStars(List<Star> stars) {
        this.stars = new ArrayList<>(stars);
    }
}
