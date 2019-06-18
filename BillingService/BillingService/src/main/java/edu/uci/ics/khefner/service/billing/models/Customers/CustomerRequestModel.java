package edu.uci.ics.khefner.service.billing.models.Customers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import static edu.uci.ics.khefner.service.billing.core.ResultCodeGlobals.*;

public class CustomerRequestModel {

    private String email;
    private String firstName;
    private String lastName;
    private String ccId;
    private String address;

    public CustomerRequestModel(@JsonProperty(value = "email", required = true) String email,
                                @JsonProperty(value = "firstName", required = true) String firstName,
                                @JsonProperty(value = "lastName", required = true) String lastName,
                                @JsonProperty(value = "ccId", required = true) String ccId,
                                @JsonProperty(value = "address", required = true) String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
    }


    public boolean isValid(){
        if(ccId == null || ccId.length() != 19){
            return false;
        }

        if(!ccId.matches("[0-9]+")){
            return false;
        }

        return true;
    }

    public int getResultCode(){
        if(ccId == null || ccId.length() != 19){
            return CREDIT_CARD_ID_HAS_INVALID_LENGTH;
        }

        if(!ccId.matches("[0-9]+")){
            return CREDIT_CARD_ID_HAS_INVALID_VALUE;
        }

        return -1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCcId() {
        return ccId;
    }

    public void setCcId(String ccId) {
        this.ccId = ccId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
