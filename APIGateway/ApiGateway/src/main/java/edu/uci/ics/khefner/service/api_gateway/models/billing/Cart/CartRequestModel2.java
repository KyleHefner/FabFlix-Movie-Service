package edu.uci.ics.khefner.service.api_gateway.models.billing.Cart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class CartRequestModel2 extends RequestModel {


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






}
