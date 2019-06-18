package edu.uci.ics.khefner.service.billing.models.Orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRequestModel {

    private String email;

    public OrderRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
