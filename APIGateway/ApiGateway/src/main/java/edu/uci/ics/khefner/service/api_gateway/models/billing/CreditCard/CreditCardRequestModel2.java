package edu.uci.ics.khefner.service.api_gateway.models.billing.CreditCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class CreditCardRequestModel2 extends RequestModel {

    private String id;


    @JsonCreator
    public CreditCardRequestModel2(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
