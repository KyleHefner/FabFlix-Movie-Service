package edu.uci.ics.khefner.service.billing.models.Customers;

import com.fasterxml.jackson.annotation.JsonProperty;

import static edu.uci.ics.khefner.service.billing.core.ResultCodeGlobals.CREDIT_CARD_ID_HAS_INVALID_LENGTH;
import static edu.uci.ics.khefner.service.billing.core.ResultCodeGlobals.CREDIT_CARD_ID_HAS_INVALID_VALUE;

public class CustomerRequestModel1 {

    private String email;


    public CustomerRequestModel1(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;

    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
