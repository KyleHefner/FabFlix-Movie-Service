package edu.uci.ics.khefner.service.movies.models.AddGenre;

public class AddGenreResponseModel {

    private int resultCode;
    private String message;

    public AddGenreResponseModel(int resultCode, String message) {
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
}
