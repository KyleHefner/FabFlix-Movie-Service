package edu.uci.ics.khefner.service.api_gateway.models.billing.Cart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class CartRequestModel3 extends RequestModel {
    private String email;



    public CartRequestModel3(){}

    @JsonCreator
    public CartRequestModel3( @JsonProperty(value = "email", required = true) String email){
        this.email = email;


    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





}
