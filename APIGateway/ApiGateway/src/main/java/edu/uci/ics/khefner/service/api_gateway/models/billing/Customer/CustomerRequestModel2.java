package edu.uci.ics.khefner.service.api_gateway.models.billing.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class CustomerRequestModel2 extends RequestModel {
    private String email;


    public CustomerRequestModel2(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;

    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
