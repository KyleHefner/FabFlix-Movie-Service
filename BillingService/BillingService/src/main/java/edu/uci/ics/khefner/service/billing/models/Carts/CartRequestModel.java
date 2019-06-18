package edu.uci.ics.khefner.service.billing.models.Carts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.billing.core.EmailChecker;


public class CartRequestModel {
    private String email;
    private String movieId;
    private Integer quantity;


    public CartRequestModel(){}

    @JsonCreator
    public CartRequestModel( @JsonProperty(value = "email", required = true) String email,
                             @JsonProperty(value = "movieId", required = true) String movieId,
                             @JsonProperty(value = "quantity", required = true) int quantity){
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;

    }

    public CartRequestModel( String email, String movieId){
        this.email = email;
        this.movieId = movieId;

        this.quantity = null;
    }



    public CartRequestModel( String email){
        this.email = email;

        this.movieId = null;
        this.quantity = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public boolean isValid(){

        EmailChecker emailChecker = new EmailChecker(email);


        if (!emailChecker.isEmailValidLength()) {
            return false;
        }

        if (!emailChecker.isEmailValidFormat()) {
            return false;
        }

        if (quantity <= 0) {
            return false;

        }
        return true;
    }

    public int getResultCode(){
        EmailChecker emailChecker = new EmailChecker(email);


        if (!emailChecker.isEmailValidLength()) {
            return -10;
        }

        if (!emailChecker.isEmailValidFormat()) {
            return -11;
        }

        if (quantity <= 0) {
            return 33;

        }
        return -1;

    }
}
