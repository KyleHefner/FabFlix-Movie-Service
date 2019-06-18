package edu.uci.ics.khefner.service.api_gateway.models.billing.Cart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class CartRequestModel extends RequestModel {

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



}
