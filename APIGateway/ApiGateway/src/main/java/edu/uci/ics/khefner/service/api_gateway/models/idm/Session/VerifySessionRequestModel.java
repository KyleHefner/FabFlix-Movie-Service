package edu.uci.ics.khefner.service.api_gateway.models.idm.Session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

public class VerifySessionRequestModel extends RequestModel {

    private String email;
    private String sessionID;


    @JsonCreator
    public VerifySessionRequestModel(@JsonProperty(value = "email", required = true) String email,
                                     @JsonProperty (value = "sessionID", required = true) String sessionID) {
        this.email = email;
        this.sessionID = sessionID;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }



}
