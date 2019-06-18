package edu.uci.ics.khefner.service.idm.models.VerifySession;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.idm.core.EmailChecker;
import edu.uci.ics.khefner.service.idm.core.PasswordChecker;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;

import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

public class VerifySessionRequestModel {
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

    public boolean isValid(){
        EmailChecker checkEmail = new EmailChecker(email);

        if(sessionID == null || sessionID.length() > 128){
            ServiceLogger.LOGGER.info("SessionID is not valid length");
            return false;
        }

        if(!checkEmail.isEmailValidLength()){
            ServiceLogger.LOGGER.info("Email is not valid length");
            return false;
        }

        if(!checkEmail.isEmailValidFormat()){
            ServiceLogger.LOGGER.info("Email is not valid format");
            return false;
        }
        return true;
    }

    public int getResultCode (){

        EmailChecker checkEmail = new EmailChecker(email);

        //Check edu.ics.uci.khefner.service.idm.core.ResponseHandler for info on numbers being returned
        if(sessionID == null || sessionID.length() > 128){return TOKEN_HAS_INVALID_LENGTH;}
        if(!checkEmail.isEmailValidLength()) {return INVALID_LENGTH_EMAIL;}
        if(!checkEmail.isEmailValidFormat()) {return INVALID_FORMAT_EMAIL;}

        return 0;

    }
}
