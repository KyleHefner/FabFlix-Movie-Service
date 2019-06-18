package edu.uci.ics.khefner.service.movies.core;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Movie {



    private String movieId;
    private String title;
    private String director;
    private Integer year;
    private Float rating;
    private Integer numVotes;
    private Boolean hidden;


    @JsonCreator
    public Movie(@JsonProperty("movieId") String movieid,
                 @JsonProperty("title") String title,
                      @JsonProperty("director") String director,
                      @JsonProperty("year") int year,
                      @JsonProperty("rating") float rating,
                      @JsonProperty("numVotes") int numVotes,
                      @JsonProperty("hidden") Boolean hidden) {
        this.movieId = movieid;
        this.title = title;
        this.director = director;
        this.year = year;
        this.numVotes = numVotes;
        this.hidden = hidden;
        this.rating = rating;


    }


    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieid) {
        this.movieId = movieid;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
