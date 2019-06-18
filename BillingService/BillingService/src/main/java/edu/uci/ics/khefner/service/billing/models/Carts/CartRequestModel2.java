package edu.uci.ics.khefner.service.billing.models.Carts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.billing.core.EmailChecker;

public class CartRequestModel2 {

    private String email;
    private String movieId;



    public CartRequestModel2(){}

    @JsonCreator
    public CartRequestModel2( @JsonProperty(value = "email", required = true) String email,
                             @JsonProperty(value = "movieId", required = true) String movieId
                             ){
        this.email = email;
        this.movieId = movieId;

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




    public boolean isValid(){

        EmailChecker emailChecker = new EmailChecker(email);


        if (!emailChecker.isEmailValidLength()) {
            return false;
        }

        if (!emailChecker.isEmailValidFormat()) {
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

        return -1;

    }
}
