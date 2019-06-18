package edu.uci.ics.khefner.service.movies.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FullMovie {

    private String movieid;
    private String title;
    private String director;
    private Integer year;
    private String backdrop_path;
    private Integer budget;
    private String overview;
    private String poster_path;
    private Float rating;
    private Integer revenue;
    private Integer numVotes;
    private Boolean hidden;
    private List<Genre> genres;
    private List<Star> Stars;

    @JsonCreator
    public FullMovie(@JsonProperty("movieId") String movieid, @JsonProperty("title") String title, @JsonProperty("director") String director,
                     @JsonProperty("year") int year, @JsonProperty("backdrop_path") String backdrop_path, @JsonProperty("budget") int budget,
                     @JsonProperty("overview") String overview, @JsonProperty("poster_path") String poster_path,
                     @JsonProperty("rating") float rating, @JsonProperty("revenue") int revenue, @JsonProperty("numVotes") int numVotes, @JsonProperty("hidden") Boolean hidden,
                     @JsonProperty("genres") List<Genre> genres, @JsonProperty("stars") List<Star> stars ){



        this.movieid = movieid;
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.rating = rating;
        this.revenue = revenue;
        this.numVotes = numVotes;
        this.hidden = hidden;
        this.genres = new ArrayList<Genre>(genres);
        this.Stars = new ArrayList<Star>(stars);
    }


    public void addGenre(Genre genre){
        this.genres.add(genre);
    }
    public void addStar(Star star){
        this.Stars.add(star);
    }


    public boolean containsGenre(int genreID){
        for(Genre genre: genres ){
            if(genre.getId() == genreID ){return true;}
        }
        return false;
    }

     public FullMovie(FullMovie m){



        this.movieid = m.movieid;
        this.title = m.title;
        this.director = m.director;
        this.year = m.year;
        this.backdrop_path = m.backdrop_path;
        this.overview = m.overview;
        this.poster_path = m.poster_path;
        this.rating = m.rating;
        this.numVotes = m.numVotes;
        this.hidden = m.hidden;
        this.budget = m.budget;
        this.revenue = m.revenue;
        this.genres = new ArrayList<Genre>(m.genres);
        this.Stars = new ArrayList<Star>(m.Stars);
    }

    public boolean containsStar(String starsID){
        for(Star star: Stars ){
            if(star.getId().equals(starsID) ){return true;}
        }
        return false;
    }

    @JsonProperty(value = "movieId", required = true)
    public String getMovieid() {
        return movieid;
    }
    @JsonProperty(value = "title", required = true)
    public String getTitle() {
        return title;
    }
    @JsonProperty(value = "director")
    public String getDirector() {
        return director;
    }
    @JsonProperty(value = "year")
    public Integer getYear() { return year; }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public Integer getBudget() { return budget; }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public Float getRating() {
        return rating;
    }

    public Integer getRevenue() { return revenue; }

    public Integer getNumVotes() {
        return numVotes;
    }


    public Boolean isHidden() { return hidden; }

    @JsonProperty(value = "genres", required = true)
    public List<Genre> getGenres() {
        return genres;
    }
    @JsonProperty(value = "stars", required = true)
    public List<Star> getStars() {
        return Stars;
    }


    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = new ArrayList<Genre>(genres);
    }

    public void setStars(List<Star> stars) {
        Stars = new ArrayList<Star>(stars);
    }
}
