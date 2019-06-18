package edu.uci.ics.khefner.service.movies.models;

import edu.uci.ics.khefner.service.movies.core.Genre;

import java.util.ArrayList;
import java.util.List;

public class SearchGenreByIdResponseModel {

    private int resultCode;
    private String message;
    private List<Genre> genreList;

    public SearchGenreByIdResponseModel(int resultCode, String message, List<Genre> genreList) {
        this.resultCode = resultCode;
        this.message = message;
        this.genreList = new ArrayList<>(genreList);
    }

    public SearchGenreByIdResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.genreList = new ArrayList<>();
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

    public List<Genre> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<Genre> genreList) {
        this.genreList = new ArrayList<>(genreList);
    }
}
