package edu.uci.ics.khefner.service.api_gateway.models.billing.CreditCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

import java.util.Date;

public class CreditCardRequestModel extends RequestModel {

    private String id;
    private String firstName;
    private String lastName;
    private Date expiration;

    @JsonCreator
    public CreditCardRequestModel(@JsonProperty(value = "id", required = true) String id,
                                  @JsonProperty(value = "firstName", required = true) String firstName,
                                  @JsonProperty(value = "lastName", required = true) String lastName,
                                  @JsonProperty(value = "expiration", required = true) Date expiration) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = expiration;

    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getExpiration() {
        return expiration;
    }
}
