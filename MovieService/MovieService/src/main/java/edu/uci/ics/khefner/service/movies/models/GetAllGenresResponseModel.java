package edu.uci.ics.khefner.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.khefner.service.movies.core.Genre;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetAllGenresResponseModel {

    private int resultCode;
    private String message;
    List<Genre> genres;

    public GetAllGenresResponseModel(int resultCode, String message, List<Genre> genres) {
        this.resultCode = resultCode;
        this.message = message;
        this.genres = new ArrayList<Genre>(genres);

    }

    public GetAllGenresResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.genres = new ArrayList<Genre>();
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

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = new ArrayList<Genre>(genres);
    }
}
