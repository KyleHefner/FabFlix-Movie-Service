package edu.uci.ics.khefner.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.containers.Genre;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

import java.util.ArrayList;
import java.util.List;

public class AddMovieRequestModel extends RequestModel {

    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = true)
    private String director;
    @JsonProperty(value = "year", required = true)
    private Integer year;
    private String backdrop_path;
    private Integer budget;
    private String overview;
    private String poster_path;
    private Integer revenue;
    @JsonProperty(value = "genres", required = true)
    private List<Genre> genres;


    @JsonCreator
    public AddMovieRequestModel(@JsonProperty(value = "title", required = true) String title, @JsonProperty(value = "director", required = true) String director,
                                @JsonProperty(value = "year", required = true) Integer year, @JsonProperty("backdrop_path") String backdrop_path,
                                @JsonProperty("budget") Integer budget, @JsonProperty("overview") String overview,
                                @JsonProperty("poster_path") String poster_path, @JsonProperty("revenue") Integer revenue,
                                @JsonProperty(value = "genres", required = true) List<Genre> genres) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.genres = new ArrayList<Genre>(genres);
    }

    @Override
    public String toString() {
        return "AddMovieRequestModel{" +
                "title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", year=" + year +
                ", backdrop_path='" + backdrop_path + '\'' +
                ", budget=" + budget +
                ", overview='" + overview + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", revenue=" + revenue +
                ", genres=" + genres.toString() +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = new ArrayList<Genre>(genres);
    }

}
