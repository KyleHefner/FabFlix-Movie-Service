package edu.uci.ics.khefner.service.movies.models.StarsIn;

public class StarsInRequestModel {

    private String starid;
    private String movieid;

    public StarsInRequestModel() {
    }

    public StarsInRequestModel(String starid, String movieid) {
        this.starid = starid;
        this.movieid = movieid;
    }

    public String getStarid() {
        return starid;
    }

    public void setStarid(String starid) {
        this.starid = starid;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }
}
