package edu.uci.ics.khefner.service.movies.models.SearchMovie;


import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.khefner.service.movies.core.Movie;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchMovieResponseModel {

    private int resultCode;
    private String message;
    private List<Movie> movies;

    public SearchMovieResponseModel( int resultCode, String message, List<Movie> movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.movies = new ArrayList<Movie>(movies);
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

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = new ArrayList<Movie>(movies);
    }


}
