package edu.uci.ics.khefner.service.movies.models.StarsIn;

public class StarsInResponseModel {

    private int resultCode;
    private String message;

    public StarsInResponseModel(int resultCode, String message) {
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
